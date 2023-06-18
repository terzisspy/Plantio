package com.example.plantio;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * The Notifications class is used so notifications are created and sent to users.
 */
public class Notifications extends FirebaseMessagingService {
  @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage){
    // Retrieve the title and body of the notification
    String title =  remoteMessage.getNotification().getTitle();
    String body = remoteMessage.getNotification().getBody();

    // Define the channel ID for the notification
    final String CHANNEL_ID ="HEAD_UP_NOTIFICATION";

    // Create a notification channel with high importance
    NotificationChannel channel = new NotificationChannel(
            CHANNEL_ID,
            "Heads Up Notification",
            NotificationManager.IMPORTANCE_HIGH
    );

    // Register the notification channel
    getSystemService(NotificationManager.class).createNotificationChannel(channel);
    // Build the notification
    Notification.Builder notification = new Notification.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.flower)
            .setAutoCancel(true);
    // Display the notification
    NotificationManagerCompat.from(this).notify(1,notification.build());
    // Call the superclass method to handle the received message
    super.onMessageReceived(remoteMessage);

  }
}