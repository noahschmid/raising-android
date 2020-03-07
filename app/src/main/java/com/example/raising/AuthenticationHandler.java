package com.example.raising;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

public class AuthenticationHandler {
    private static long accountId;
    private static String token;

    /**
     * Save the given access token and accountId in internal storage
     * @param token the access token (received from backend on login)
     * @param accountId the accountId
     * @param context the application context
     * @throws Exception if something went wrong while writing to the internal storage
     */
    public static void login(String token, long accountId, Context context) throws Exception {
        FileOutputStream outputStream;

        outputStream = context.openFileOutput("token", Context.MODE_PRIVATE);
        String saveString = token + "\n" + String.valueOf(accountId);
        outputStream.write(saveString.getBytes());
        outputStream.flush();
        outputStream.close();

        AuthenticationHandler.token = token;
        AuthenticationHandler.accountId = accountId;
    }


    public static String getToken() {
        return AuthenticationHandler.token;
    }

    /**
     * Get accountId
     * @return accountId as long
     */
    public static long getId() {
        return AuthenticationHandler.accountId;
    }

    /**
     * Delete saved token and accountId
     * @param context the application context
     */
    public static void logout(Context context) {
        context.deleteFile("token");

        Log.d("debugMessage", "logged out");

        AuthenticationHandler.token = null;
        AuthenticationHandler.accountId = -1;
    }

    /**
     * Check if token is saved in internal storage
     * @param context the application context
     * @return true if user is currently logged in, false otherwise
     */
    public static boolean isLoggedIn(Context context) {
        File file = context.getFileStreamPath("token");
        if(!file.exists())
            return false;

        try {
            FileInputStream fis = context.openFileInput("token");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);

            AuthenticationHandler.token = bufferedReader.readLine();
            AuthenticationHandler.accountId = Long.parseLong(bufferedReader.readLine());
        } catch(Exception e) {
            Log.d("debugMessage", e.getMessage());
            return false;
        }

        return true;
    }
}