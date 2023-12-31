package com.raising.app;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.raising.app.util.AuthenticationHandler;

/**
 * This class catches notifications which are received while using the app but does nothing with
 * it for now. It's more of a blueprint class for later.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private final String TAG = "MyFirebaseMessagingService";
    public MyFirebaseMessagingService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.d(TAG, "onNewToken: Refreshed Token: " + s);
    }
}
