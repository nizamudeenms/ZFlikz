package com.zyta.zflikz.messaging;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.zyta.zflikz.GlideApp;
import com.zyta.zflikz.MainActivity;
import com.zyta.zflikz.R;

import java.io.InputStream;
import java.util.concurrent.ExecutionException;

import io.reactivex.annotations.NonNull;

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
            imageUrl = remoteMessage.getData().get("image_url");
            title = remoteMessage.getData().get("title");
            message = remoteMessage.getData().get("message");
        }

        try {
            myBitmap = GlideApp.with(this).asBitmap().load(imageUrl).into(256, 256).get();
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
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(myBitmap)
                .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(myBitmap).bigLargeIcon(null))
                .setContentText(body);


        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(999, noBuilder.build());


        FirebaseMessaging.getInstance().subscribeToTopic("general")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Successful Messaging";
                        if (!task.isSuccessful()) {
                            msg = "Failed Messaging";
                        }
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                    }
                });

    }
}