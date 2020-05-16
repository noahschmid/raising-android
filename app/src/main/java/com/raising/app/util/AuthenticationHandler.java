package com.raising.app.util;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.ViewModelProviders;
import com.raising.app.viewModels.SubscriptionViewModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

public class AuthenticationHandler {
    private static long accountId = -1;
    private static String token;
    private static String email;
    private static boolean loggedIn = false;
    private static boolean isStartup = false;

    /**
     * Save the given access token and accountId in internal storage
     * @param token the access token (received from backend on login)
     * @param accountId the accountId
     * @param isStartup indicating whether the account belongs to startup or investor
     * @throws Exception if something went wrong while writing to the internal storage
     */
    public static void login(String email, String token, long accountId,
                             boolean isStartup) throws Exception {
        FileOutputStream outputStream;

        outputStream = InternalStorageHandler.getContext().openFileOutput("token",
                Context.MODE_PRIVATE);
        String saveString = token + "\n" + email + "\n" + isStartup + "\n" + accountId;
        outputStream.write(saveString.getBytes());
        outputStream.flush();
        outputStream.close();

        AuthenticationHandler.token = token;
        AuthenticationHandler.accountId = accountId;
        AuthenticationHandler.isStartup = isStartup;
        AuthenticationHandler.email = email;
        loggedIn = true;

        ContactDataHandler.init();
    }

    public static String getToken() { return token; }

    public static void setToken(String newToken) { token = newToken; }

    public static boolean isStartup() { return isStartup; }

    /**
     * Get accountId
     * @return accountId as long
     */
    public static long getId() {
        return accountId;
    }

    /**
     * Get email of logged in user
     * @return email as string
     */
    public static String getEmail() { return email; }

    /**
     * Delete saved token and accountId
     */
    public static void logout() {
        InternalStorageHandler.getContext().deleteFile("token");

        Log.d("AuthenticationHandler", "logged out");

        AuthenticationHandler.token = null;
        AuthenticationHandler.accountId = -1;
        SubscriptionViewModel subscriptionViewModel = ViewModelProviders.of(InternalStorageHandler.getActivity())
                .get(SubscriptionViewModel.class);
        subscriptionViewModel.removeSubscription();
        loggedIn = false;
    }

    /**
     * Check if there's a token file and if so, read contents
     * @return true if user is currently logged in, false otherwise
     */
    public static void init() {
        File file = InternalStorageHandler.getContext().getFileStreamPath("token");
        if(!file.exists())
            return;

        try {
            FileInputStream fis = InternalStorageHandler.getContext().openFileInput("token");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);

            AuthenticationHandler.token = bufferedReader.readLine();
            AuthenticationHandler.email = bufferedReader.readLine();
            AuthenticationHandler.isStartup = Boolean.parseBoolean(bufferedReader.readLine());
            AuthenticationHandler.accountId = Long.parseLong(bufferedReader.readLine());
            loggedIn = true;

            ContactDataHandler.init();

            Log.i("AuthenticationHandler", "Auto login with accountId " + accountId);
        } catch(Exception e) {
            Log.e("AuthenticationHandler", "Error while initializing: " +  e.getMessage());
        }
    }
    public static boolean isLoggedIn() { return loggedIn; }
}
