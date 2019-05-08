package com.zyta.zflikz.messaging;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.zyta.zflikz.GlideApp;
import com.zyta.zflikz.MainActivity;
import com.zyta.zflikz.R;

import java.io.InputStream;
import java.util.concurrent.ExecutionException;

public class ZlikxMessagingService extends FirebaseMessagingService {

    private static final String TAG = ZlikxMessagingService.class.getSimpleName();
    private static final String CHANNEL_ID = "zlikxNotifications";
    String imageUrl, title, message = null;
    Bitmap myBitmap;
    InputStream in;


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if (remoteMessage.getData().size() > 0) {
            imageUrl = remoteMessage.getData().get("imageUrl");
            title = remoteMessage.getData().get("title");
            message = remoteMessage.getData().get("message");
        }

        try {
                myBitmap = GlideApp.with(this).asBitmap().load(imageUrl != null ? imageUrl:R.drawable.zlikx_logo).into(256, 256).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        createNotificationChannel();
        showNotifications(title, message);
    }


    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Zlikx Channel";
            String description = "Zlikx Channel Description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void showNotifications(String title, String body) {

        Intent intent = new Intent(this, MainActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);


        NotificationCompat.Builder noBuilder = new NotificationCompat.Builder(this, "zlikxNotifications")
                .setContentTitle(title)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setLargeIcon(myBitmap)
                .setDefaults(Notification.DEFAULT_ALL)
                .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(myBitmap).bigLargeIcon(null))
                .setContentText(body);


        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(999, noBuilder.build());

    }
}
