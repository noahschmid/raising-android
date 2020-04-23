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
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import javax.net.ssl.HttpsURLConnection;

public class ImageUploader extends AsyncTask<Void, Integer, String> {
    private ProgressDialog progressDialog;
    private String url;
    private ArrayList<File> files = new ArrayList<>();
    private String method;
    private Function<JSONArray, Void> multiCallback;
    private Function<JSONObject, Void> singleCallback;
    private Function<JSONObject, Void> errorCallback;
    private String fieldName;
    private String token;

    private final String TAG = "ImageUploader";

    // Constructor for single file - no auth
    public ImageUploader(String url, String fieldName, Bitmap image, String method,
                          Function<JSONObject, Void> callback,
                         Function<JSONObject, Void> errorCallback) {
        this.url = ApiRequestHandler.getDomain() + url;
        this.files.add(persistImage(image, "tmp"));
        this.method = method;
        this.singleCallback = callback;
        this.errorCallback = errorCallback;
        this.fieldName = fieldName;
    }

    // Constructor for single file
    public ImageUploader(String url, String fieldName, Bitmap image, String method,
                         Function<JSONObject, Void> callback,
                         Function<JSONObject, Void> errorCallback, String token) {
        this.url = ApiRequestHandler.getDomain() + url;
        this.files.add(persistImage(image, "tmp"));
        this.method = method;
        this.singleCallback = callback;
        this.errorCallback = errorCallback;
        this.fieldName = fieldName;
        this.token = token;
    }

    //Constructor for multiple files - no auth
    public ImageUploader(String url, String fieldName, List<Bitmap> images, String method,
                         Function<JSONArray, Void> callback,
                         Function<JSONObject, Void> errorCallback) {
        this.url = ApiRequestHandler.getDomain() + url;
        images.forEach(img -> this.files.add(persistImage(img, "tmp" + files.size())));
        this.method = method;
        this.multiCallback = callback;
        this.errorCallback = errorCallback;
        this.fieldName = fieldName;
    }

    //Constructor for multiple files
    public ImageUploader(String url, String fieldName, List<Bitmap> images, String method,
                         Function<JSONArray, Void> callback,
                         Function<JSONObject, Void> errorCallback, String token) {
        this.url = ApiRequestHandler.getDomain() + url;
        images.forEach(img -> this.files.add(persistImage(img, "tmp" + files.size())));
        this.method = method;
        this.multiCallback = callback;
        this.errorCallback = errorCallback;
        this.fieldName = fieldName;
        this.token = token;
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
        return doMultiFileUpload();
    }

    private String doMultiFileUpload() {
        String res = "fail";

        HttpsURLConnection.setFollowRedirects(false);
        HttpsURLConnection connection = null;

        try {
            connection = (HttpsURLConnection) new URL(url).openConnection();


            String boundary = "---------------------------boundary";
            String tail = "\r\n--" + boundary + "--\r\n";
            if(token != null) {
                connection.setRequestProperty("Authorization", "Bearer " + token);
            }
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            connection.setRequestMethod(method);
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
                fileHeader.append(fileHeader1)
                        .append(fileHeader2)
                        .append("\r\n");
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
                    multiCallback.apply(response);
                } else if (sb.toString().length() > 0) {
                    response.put(new JSONObject(sb.toString()));
                    singleCallback.apply(new JSONObject(sb.toString()));
                }
            } else {
                Log.e(TAG, "doMultiFileUpload: error code " + connection.getResponseCode());
                if(connection.getErrorStream() != null) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(
                            connection.getErrorStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line).append("\n");
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
