package com.example.raising;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

public class AuthenticationHandler {
    /**
     * Save token to internal storage
     * @param token the access token which needs to be saved
     * @param context the application context
     */
    public static void saveToken(String token, Context context) {
        FileOutputStream outputStream;

        try {
            outputStream = context.openFileOutput("token", Context.MODE_PRIVATE);
            outputStream.write(token.getBytes());
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            Log.d("debugMessage", e.getMessage());
        }
    }

    /**
     * Get token from internal storage
     * @param context the application context
     * @return access token as string
     */
    public static String getToken(Context context) {
        try {
            FileInputStream fis = context.openFileInput("token");
            Scanner scanner = new Scanner(fis);
            scanner.useDelimiter("\\Z");
            String token = scanner.next();
            scanner.close();

            return token;
        } catch(FileNotFoundException e){
            Log.d("debugMessage", e.getMessage());
            return null;
        }
    }

    /**
     * Delete token from internal storage
     * @param context the application context
     */
    public static void deleteToken(Context context) {
        File dir = context.getFilesDir();
        boolean deleted = context.deleteFile("token");
        if(!deleted)
            Log.d("debugMessage", "Error deleting token!");
    }

    /**
     * Check whether user is logged in
     * @param context the application context
     * @return true if user is logged in, false otherwise
     */
    public boolean isLoggedIn(Context context) {
        String path = context.getFilesDir().getAbsolutePath() + "/token";
        File file = new File(path);
        return file.exists();
    }
}
