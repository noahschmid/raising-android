package com.raising.app.util;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.function.Function;

import javax.net.ssl.HttpsURLConnection;

public class ImageUploader extends AsyncTask<Void, Integer, String> {
    private ProgressDialog progressDialog;
    private String url;
    private File file;
    private String method;
    private Function<JSONObject, Void> callback;
    private Function<JSONObject, Void> errorCallback;

    private final String TAG = "ImageUploader";

    public ImageUploader(String url, Bitmap image, String method,
                         Function<JSONObject, Void> callback, Function<JSONObject, Void> errorCallback) {
        this.url = url;
        this.file = persistImage(image, "tmp");
        this.method = method;
        this.callback = callback;
        this.errorCallback = errorCallback;
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
        progressDialog = new ProgressDialog(InternalStorageHandler.getContext());
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
        String fileName = "";
        String format = "";
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
        try {
            connection = (HttpsURLConnection) new URL(url).openConnection();
            connection.setRequestMethod(method);
            String boundary = "---------------------------boundary";
            String tail = "\r\n--" + boundary + "--\r\n";
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            connection.setDoOutput(true);

            String metadataPart = "--" + boundary + "\r\n"
                    + "Content-Disposition: form-data; name=\"metadata\"\r\n\r\n"
                    + "" + "\r\n";

            String fileHeader1 = "--" + boundary + "\r\n"
                    + "Content-Disposition: form-data; name=\"profilePicture\"; filename=\""
                    + fileName + "\"\r\n"
                    + "Content-Type: " + format + "\r\n"
                    + "Content-Transfer-Encoding: binary\r\n";

            long fileLength = file.length() + tail.length();
            String fileHeader2 = "Content-length: " + fileLength + "\r\n";
            String fileHeader = fileHeader1 + fileHeader2 + "\r\n";
            String stringData = metadataPart + fileHeader;

            long requestLength = stringData.length() + fileLength;
            connection.setRequestProperty("Content-length", "" + requestLength);
            connection.setFixedLengthStreamingMode((int) requestLength);
            connection.connect();

            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            out.writeBytes(stringData);
            out.flush();

            int progress = 0;
            int bytesRead = 0;
            byte buf[] = new byte[1024];
            BufferedInputStream bufInput = new BufferedInputStream(new FileInputStream(file));
            while ((bytesRead = bufInput.read(buf)) != -1) {
                out.write(buf, 0, bytesRead);
                out.flush();
                progress += bytesRead;
                publishProgress((int) ((progress * 100) / (file.length())));
            }

            // Write closing boundary and close stream
            out.writeBytes(tail);
            out.flush();
            out.close();
            if (connection.getResponseCode() == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        connection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line+"\n");
                }
                br.close();
                callback.apply(new JSONObject(sb.toString()));
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
                    errorCallback.apply(new JSONObject(sb.toString()));
                }
            }
        } catch (Exception e) {
            // Exception
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
        Toast.makeText(InternalStorageHandler.getContext(),
                "Upload canceled", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onPostExecute(String v) {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
