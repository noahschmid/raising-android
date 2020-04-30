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

    /**
     * Completes the request and receives response from the server.
     * @return a json object
     * status OK, otherwise an exception is thrown.
     * @throws IOException
     */
    public JSONObject finish() throws Exception {
        JSONObject response = new JSONObject();

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

            if(sb.toString().startsWith("[")) {
                response.put("array", new JSONArray(sb.toString()));
            } else if (sb.toString().length() > 0) {
                response = new JSONObject(sb.toString());
            }
            br.close();
            httpConn.disconnect();
        } else {
            throw new IOException("Server returned non-OK status: " + status);
        }

        return response;
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
                    Log.d(TAG, "doInBackground: updating picture with id " + profilePictureId);
                }
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
            if(gallery != null) {
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

/*
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
    private File profilePicture;
    private boolean registrationUpload = false;

    private final String TAG = "ImageUploader";
    private final String BOUNDARY = "---------------------------boundary";
    private final String TAIL = "\r\n--" + BOUNDARY + "--\r\n";

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
        images.forEach(img -> this.files.add(persistImage(img, "tmp" + new Date().getTime())));
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
        images.forEach(img -> this.files.add(persistImage(img, "tmp" + new Date().getTime())));
        this.method = method;
        this.multiCallback = callback;
        this.errorCallback = errorCallback;
        this.fieldName = fieldName;
        this.token = token;
    }

    //Constructor for profile and gallery images
    public ImageUploader(Bitmap profilePicture,
                         List<Bitmap> gallery,
                         Function<JSONObject, Void> callback,
                         Function<JSONObject, Void> errorCallback) {
        this.url = ApiRequestHandler.getDomain() + url;
        gallery.forEach(img -> this.files.add(persistImage(img, "tmp" + new Date().getTime())));
        this.profilePicture = persistImage(profilePicture, "tmp" + new Date().getTime());
        this.method = method;
        this.singleCallback = callback;
        this.errorCallback = errorCallback;
        this.fieldName = fieldName;

        this.registrationUpload = true;
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
        if(registrationUpload)
            return doProfileAndGalleryUpload();
        return doFileUpload();
    }

    /**
     * Upload either a gallery or profile picture
     * @return
     */
  /*  private String doFileUpload() {
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

                fileLength += file.length();
                totalFilesLength += file.length();
                String fileHeader2 = "Content-length: " + fileLength + "\r\n";
                fileHeader.append(fileHeader1)
                        .append(fileHeader2)
                        .append("\r\n");
            }

            totalFilesLength += tail.length();

            String stringData = metadataPart + fileHeader.toString() + tail;

            long requestLength = stringData.length() + fileLength + tail.length();
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
                Log.d(TAG, "successful upload");
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        connection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line+"\n");
                }
                br.close();
                Log.d(TAG, "doMultiFileUpload: successful upload");
                Log.d(TAG, "doMultiFileUpload: " + sb.toString());
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
                    Log.d(TAG, "doMultiFileUpload: error while uploading");
                    Log.d(TAG, "doMultiFileUpload: " + sb.toString());
                    errorCallback.apply(response);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "doFileUpload: " +  e.getMessage());
        } finally {
            if (connection != null) connection.disconnect();
        }
        return res;
    }

    /**
     * Upload both gallery and profile picture at the same time
     * @return
     */
   /* private String doProfileAndGalleryUpload() {
        String res = "fail";

        HttpsURLConnection.setFollowRedirects(false);
        HttpsURLConnection connection = null;

        try {
            StringBuilder galleryFileHeader = new StringBuilder();
            JSONObject response = new JSONObject();

            long profileFileLength = profilePicture.length() + TAIL.length();
            long galleryFilesLength = 0;

            for(int i = 0; i < files.size(); ++i) {
                File file = files.get(i);
                galleryFileHeader.append(getFileHeader(file, "gallery"));
                galleryFilesLength += file.length() + TAIL.length();
            }

            long totalFilesLength = profileFileLength + galleryFilesLength;

            // upload profile picture
            String profilePictureStringData = getStringData(getFileHeader(profilePicture, "profilePicture"));
            connection = createConnection(ApiRequestHandler.getDomain() + "media/profilepicture",
                    profilePictureStringData, profileFileLength, "POST");

            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            out.writeBytes(profilePictureStringData);
            out.flush();

            uploadSingleFile(profilePicture, out, totalFilesLength);

            out.writeBytes(TAIL);
            out.flush();
            out.close();

            if (connection.getResponseCode() == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        connection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                br.close();
                if(sb.toString().length() > 0) {
                    response.put("profilePictureResponse", new JSONObject(sb.toString()));
                }

                connection.disconnect();

                // upload gallery
                String galleryStringData = getStringData(galleryFileHeader.toString());
                connection = createConnection(ApiRequestHandler.getDomain() + "media/gallery",
                        galleryStringData, galleryFilesLength, "POST");
                String galleryResponse = processFilesUpload(files, connection, galleryStringData, totalFilesLength);

                if(galleryResponse.startsWith("[")) {
                    JSONArray gallery = new JSONArray(galleryResponse);
                    response.put("galleryResponse", gallery);
                }

                connection.disconnect();
                singleCallback.apply(response);
            } else {
                Log.e(TAG, "profileAndGalleryUpload: error code " + connection.getResponseCode());
                if(connection.getErrorStream() != null) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(
                            connection.getErrorStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                    br.close();
                    response = new JSONObject();
                    if(sb.toString().length() > 0) {
                        response = new JSONObject(sb.toString());
                    }
                    Log.d(TAG, "doProfileAndGalleryUpload: error while uploading");
                    Log.d(TAG, "doProfileAndGalleryUpload: " + sb.toString());
                    errorCallback.apply(response);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "doProfileAndGalleryUpload: " +  e.getMessage());
        } finally {
            if (connection != null) connection.disconnect();
        }
        return res;
    }

    /**
     * Get file header for given file
     * @param file
     * @param name
     * @return
     */
  /*  private String getFileHeader(File file, String name) {
        String fileName = "";
        String format = "";
        long fileLength = 0;

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

        String fileHeader1 = "--" + BOUNDARY + "\r\n"
                + "Content-Disposition: form-data; name=\"" + name + "\"; filename=\""
                + fileName + "\"\r\n"
                + "Content-Type: " + format + "\r\n"
                + "Content-Transfer-Encoding: binary\r\n";

        fileLength += file.length() + TAIL.length();
        String fileHeader2 = "Content-length: " + fileLength + "\r\n";
        return fileHeader1 + fileHeader2 + "\r\n";
    }

    private String getStringData(String fileHeader) {
        String boundary = "---------------------------boundary";

        String metadataPart = "--" + boundary + "\r\n"
                + "Content-Disposition: form-data; name=\"metadata\"\r\n\r\n"
                + "" + "\r\n";
        String stringData = metadataPart + fileHeader.toString();
        return stringData;
    }

    private void uploadSingleFile(File file, DataOutputStream out,
                                   long totalFilesLength) throws Exception {
        int progress = 0;
        int bytesRead = 0;
        byte buf[] = new byte[1024];
        BufferedInputStream bufInput = new BufferedInputStream(new FileInputStream(file));
        while ((bytesRead = bufInput.read(buf)) != -1) {
            out.write(buf, 0, bytesRead);
            out.flush();
            progress += bytesRead;
            publishProgress((int) ((progress * 100) / (totalFilesLength)));
        }
    }

    private String processFilesUpload(List<File> files, HttpsURLConnection connection,
                                          String stringData, long totalFilesLength) throws Exception{
        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        out.writeBytes(stringData);
        out.flush();

        for(int i = 0; i < files.size(); ++i) {
            uploadSingleFile(files.get(i), out, totalFilesLength);
        }

        out.writeBytes(TAIL);
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
            Log.d(TAG, "doMultiFileUpload: successful upload");
            Log.d(TAG, "doMultiFileUpload: " + sb.toString());
            return sb.toString();
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
                JSONObject  response = new JSONObject();
                if(sb.toString().length() > 0) {
                    response = new JSONObject(sb.toString());
                }
                Log.d(TAG, "processFileUpload: error while uploading");
                Log.d(TAG, "processFileUpload: " + sb.toString());
                errorCallback.apply(response);
                return null;
            }
        }

        return null;
    }

    private HttpsURLConnection createConnection(String url, String stringData, long fileLength,
                                                String requestMethod) throws Exception{
        HttpsURLConnection.setFollowRedirects(false);
        HttpsURLConnection connection = null;

        connection = (HttpsURLConnection) new URL(url).openConnection();


        if(token != null) {
            connection.setRequestProperty("Authorization", "Bearer " + token);
        }
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
        connection.setRequestMethod(requestMethod);
        connection.setDoOutput(true);

        long requestLength = stringData.length() + fileLength;
        connection.setRequestProperty("Content-length", "" + requestLength);
        connection.setFixedLengthStreamingMode((int) requestLength);
        connection.connect();

        return connection;
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
*/