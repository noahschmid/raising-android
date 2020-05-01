package com.raising.app.util;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

import javax.net.ssl.HttpsURLConnection;

public class ImageUploader extends AsyncTask<Void, Integer, String> {
    private String boundary;
    private static final String LINE_FEED = "\r\n";
    private HttpsURLConnection httpConn;
    private String charset = "UTF-8";
    private OutputStream outputStream;
    private PrintWriter writer;
    private ProgressDialog progressDialog;
    private File profilePicture;
    private ArrayList<File> gallery = new ArrayList<>();
    private long uploadSize;
    private final String TAG = "ImageUploader";
    private Function<JSONObject, Void> callback;
    private Function<JSONObject, Void> errorCallback;
    private long profilePictureId = -1;

    /**
     * Standard constructor to upload profile picture and/or gallery
     * @param profileImage
     * @param galleryImages
     * @param callback
     * @param errorCallback
     */
    public ImageUploader(Bitmap profileImage, List<Bitmap> galleryImages,
                         Function<JSONObject, Void> callback,
                         Function<JSONObject, Void> errorCallback) {
        this.charset = charset;

        if (profileImage != null) {
            this.profilePicture = persistImage(profileImage, "tmp" + new Date().getTime());
            uploadSize = this.profilePicture.length();
        }
        if(galleryImages != null) {
            galleryImages.forEach(img -> this.gallery.add(persistImage(img, "tmp" + new Date().getTime())));
            this.gallery.forEach(file -> uploadSize += file.length());
        }
        this.callback = callback;
        this.errorCallback = errorCallback;
        this.profilePictureId = -1;
    }

    /**
     * Constructor to change profile picture
     * @param profileImage
     * @param id
     * @param callback
     * @param errorCallback
     */
    public ImageUploader(Bitmap profileImage, long id,
                         Function<JSONObject, Void> callback,
                         Function<JSONObject, Void> errorCallback) {
        this(profileImage, null, callback, errorCallback);
        this.profilePictureId = id;
    }

    private void prepareConnection(String requestUrl) throws Exception {
        // creates a unique boundary based on time stamp
        boundary = "===" + System.currentTimeMillis() + "===";

        URL url = new URL(requestUrl);
        httpConn = (HttpsURLConnection) url.openConnection();

        if(AuthenticationHandler.getToken() != null) {
            httpConn.setRequestProperty("Authorization", "Bearer " + AuthenticationHandler.getToken());
        }

        httpConn.setUseCaches(false);
        if(profilePictureId != -1) {
            httpConn.setRequestMethod("PATCH");
        }
        httpConn.setDoOutput(true);
        httpConn.setDoInput(true);
        httpConn.setRequestProperty("Content-Type",
                "multipart/form-data; boundary=" + boundary);
        httpConn.setRequestProperty("User-Agent", "CodeJava Agent");
        outputStream = httpConn.getOutputStream();
        writer = new PrintWriter(new OutputStreamWriter(outputStream, charset),
                true);
    }

    /**
     * Adds a upload file section to the request
     * @param fieldName name attribute in <input type="file" name="..." />
     * @param uploadFile a File to be uploaded
     * @throws IOException
     */
    public void addFilePart(String fieldName, File uploadFile)
            throws IOException {
        String fileName = uploadFile.getName();
        writer.append("--" + boundary).append(LINE_FEED);
        writer.append(
                "Content-Disposition: form-data; name=\"" + fieldName
                        + "\"; filename=\"" + fileName + "\"")
                .append(LINE_FEED);
        writer.append(
                "Content-Type: "
                        + URLConnection.guessContentTypeFromName(fileName))
                .append(LINE_FEED);
        writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
        writer.append(LINE_FEED);
        writer.flush();

        FileInputStream inputStream = new FileInputStream(uploadFile);
        byte[] buffer = new byte[4096];
        int bytesRead = -1;
        int progress = 0;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
            progress += bytesRead;
            publishProgress((int) ((progress * 100) / (uploadSize)));
        }
        outputStream.flush();
        inputStream.close();

        writer.append(LINE_FEED);
        writer.flush();
    }

    /**
     * Adds a header field to the request.
     * @param name - name of the header field
     * @param value - value of the header field
     */
    public void addHeaderField(String name, String value) {
        writer.append(name).append(": ").append(value).append(LINE_FEED);
        writer.flush();
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
        try {
            JSONObject response = new JSONObject();
            //profile picture
            if(profilePicture != null) {
                String endpoint = ApiRequestHandler.getDomain() + "media/profilepicture";
                if(profilePictureId > 0) {
                    endpoint += "/" + profilePictureId;
                }

                Log.d(TAG, "doInBackground: url " + endpoint);
                prepareConnection(endpoint);
                addFilePart("profilePicture", profilePicture);

                writer.append(LINE_FEED).flush();
                writer.append("--").append(boundary).append("--").append(LINE_FEED);
                writer.close();

                int status = httpConn.getResponseCode();
                if (status == HttpsURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(
                            httpConn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                    br.close();

                    if(!sb.toString().isEmpty()) {
                        response.put("profileResponse", new JSONObject(sb.toString()));
                    }
                    br.close();
                    httpConn.disconnect();
                } else {
                    BufferedReader br = new BufferedReader(new InputStreamReader(
                            httpConn.getErrorStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                    br.close();
                    httpConn.disconnect();
                    errorCallback.apply(new JSONObject(sb.toString()));
                    return "fail";
                }
            }

            // gallery
            if(gallery.size() > 0) {
                prepareConnection(ApiRequestHandler.getDomain() + "media/gallery");

                for (File img : gallery) {
                    addFilePart("gallery", img);
                }

                writer.append(LINE_FEED).flush();
                writer.append("--").append(boundary).append("--").append(LINE_FEED);
                writer.close();

                int status = httpConn.getResponseCode();
                if (status == HttpsURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(
                            httpConn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                    br.close();
                    httpConn.disconnect();

                    if(sb.toString().startsWith("[")) {
                        response.put("galleryResponse", new JSONArray(sb.toString()));
                    } else if (sb.toString().length() > 0) {
                        response.put("galleryResponse", new JSONObject(sb.toString()));
                    }
                } else {
                    BufferedReader br = new BufferedReader(new InputStreamReader(
                            httpConn.getErrorStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                    br.close();
                    httpConn.disconnect();
                    errorCallback.apply(new JSONObject(sb.toString()));
                    return "fail";
                }

            }

            Log.d(TAG, "doInBackground: " + response.toString());

            callback.apply(response);
        } catch (Exception ex) {
            Log.e(TAG, "doInBackground: " + ex.getMessage());
        }

        return "fail";
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
}
