package com.raising.app.util;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.raising.app.MainActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import javax.net.ssl.HttpsURLConnection;

public class ImageUploader extends AsyncTask<Void, Integer, String> {
    private ProgressDialog progressDialog;
    private String url;
    private ArrayList<File> files = new ArrayList<>();
    private String method;
    private Function<JSONArray, Void> callback;
    private Function<JSONObject, Void> errorCallback;
    private long accountId = -1;
    private String fieldName;

    private final String TAG = "ImageUploader";

    public ImageUploader(String url, String fieldName, Bitmap image, long id, String method,
                          Function<JSONArray, Void> callback,
                         Function<JSONObject, Void> errorCallback) {
        this.url = ApiRequestHandler.getDomain() + url;
        this.files.add(persistImage(image, "tmp"));
        this.method = method;
        this.callback = callback;
        this.errorCallback = errorCallback;
        this.accountId = id;
        this.fieldName = fieldName;
    }

    public ImageUploader(String url, String fieldName, List<Bitmap> images, long id, String method,
                         Function<JSONArray, Void> callback,
                         Function<JSONObject, Void> errorCallback) {
        this.url = ApiRequestHandler.getDomain() + url;
        images.forEach(img -> this.files.add(persistImage(img, "tmp" + files.size())));
        this.method = method;
        this.callback = callback;
        this.errorCallback = errorCallback;
        this.accountId = id;
        this.fieldName = fieldName;
    }

    private File persistImage(Bitmap bitmap, String name) {
        File filesDir = InternalStorageHandler.getContext().getFilesDir();
        File imageFile = new File(filesDir, name + ".jpg");

        OutputStream os;
        try {
            os = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
            return imageFile;
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Error writing bitmap", e);
            return null;
        }
    }

    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(InternalStorageHandler.getActivity());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMessage("Uploading...");
        // progressDialog.setCancelable(false);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                cancel(true);
            }
        });
        //   progressDialog.setMax((int) file.length());
        progressDialog.setMax(100);
        progressDialog.show();
    }

    @Override
    protected String doInBackground(Void... v) {
        String res = "fail";

        HttpsURLConnection.setFollowRedirects(false);
        HttpsURLConnection connection = null;

        try {
            connection = (HttpsURLConnection) new URL(url).openConnection();
            connection.setRequestMethod(method);
            if(AuthenticationHandler.isLoggedIn()) {
                connection.setRequestProperty("Authorization", "Bearer " + AuthenticationHandler.getToken());
            }
            String boundary = "---------------------------boundary";
            String tail = "\r\n--" + boundary + "--\r\n";
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            connection.setDoOutput(true);

            String metadataPart = "--" + boundary + "\r\n"
                    + "Content-Disposition: form-data; name=\"metadata\"\r\n\r\n"
                    + "" + "\r\n";
            StringBuilder fileHeader = new StringBuilder();
            long fileLength = 0;
            long totalFilesLength = 0;

            for(int i = 0; i < files.size(); ++i) {
                String fileName = "";
                String format = "";
                File file = files.get(i);

                if (file.getName().toLowerCase().endsWith(".jpg") ||
                        file.getName().toLowerCase().endsWith(".jpeg")) {
                    fileName = System.currentTimeMillis() + ".jpg";
                    format = "image/jpeg";
                } else if (file.getName().toLowerCase().endsWith(".png")) {
                    fileName = System.currentTimeMillis() + ".png";
                    format = "image/png";
                } else if (file.getName().toLowerCase().endsWith(".bmp")) {
                    fileName = System.currentTimeMillis() + ".bmp";
                    format = "image/bmp";
                }

                String fileHeader1 = "--" + boundary + "\r\n"
                        + "Content-Disposition: form-data; name=\"" + fieldName + "\"; filename=\""
                        + fileName + "\"\r\n"
                        + "Content-Type: " + format + "\r\n"
                        + "Content-Transfer-Encoding: binary\r\n";

                fileLength += file.length() + tail.length();
                totalFilesLength += file.length();
                String fileHeader2 = "Content-length: " + fileLength + "\r\n";
                fileHeader.append(fileHeader1 + fileHeader2 + "\r\n");
            }

            String stringData = metadataPart + fileHeader.toString();

            long requestLength = stringData.length() + fileLength;
            connection.setRequestProperty("Content-length", "" + requestLength);
            connection.setFixedLengthStreamingMode((int) requestLength);
            connection.connect();

            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            out.writeBytes(stringData);
            out.flush();

            for(int i = 0; i < files.size(); ++i) {
                int progress = 0;
                int bytesRead = 0;
                byte buf[] = new byte[1024];
                BufferedInputStream bufInput = new BufferedInputStream(new FileInputStream(files.get(i)));
                while ((bytesRead = bufInput.read(buf)) != -1) {
                    out.write(buf, 0, bytesRead);
                    out.flush();
                    progress += bytesRead;
                    publishProgress((int) ((progress * 100) / (totalFilesLength)));
                }
            }

            Log.d(TAG, "doInBackground: end of while loop");

            out.writeBytes(tail);
            out.flush();
            out.close();
            if (connection.getResponseCode() == 200) {
                Log.d(TAG, "doInBackground: successful upload");
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        connection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line+"\n");
                }
                br.close();
                JSONArray response = new JSONArray();
                if(sb.toString().startsWith("[")) {
                    response = new JSONArray(sb.toString());
                } else if (sb.toString().length() > 0) {
                    response.put(new JSONObject(sb.toString()));
                }
                callback.apply(response);
            } else {
                if(connection.getErrorStream() != null) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(
                            connection.getErrorStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line+"\n");
                    }
                    br.close();
                    JSONObject response = new JSONObject();
                    if(sb.toString().length() > 0) {
                        response = new JSONObject(sb.toString());
                    }
                    errorCallback.apply(response);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "doInBackground: " +  e.getMessage());
            ToastHandler toastHandler = new ToastHandler(InternalStorageHandler.getActivity());
            toastHandler.showToast(
                    "Upload failed", Toast.LENGTH_LONG);
        } finally {
            if (connection != null) connection.disconnect();
        }
        return res;
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        progressDialog.setProgress((int) (progress[0]));
    }

    @Override
    protected void onCancelled() {
        ToastHandler toastHandler = new ToastHandler(InternalStorageHandler.getActivity());
        toastHandler.showToast(
                "Upload canceled", Toast.LENGTH_LONG);
    }

    @Override
    protected void onPostExecute(String v) {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
