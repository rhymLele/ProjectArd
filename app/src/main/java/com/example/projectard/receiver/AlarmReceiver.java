package com.example.projectard.receiver;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.projectard.R;

import java.util.Date;

public class AlarmReceiver extends BroadcastReceiver {
    final String CHANEL_ID="201";


    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("MyAction")){
            String time =intent.getStringExtra("time");
            String title=intent.getStringExtra("title");
            Uri soundUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.tryu);
            NotificationManager notificationManager=( NotificationManager)context.getSystemService(context.NOTIFICATION_SERVICE);
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
            {
                NotificationChannel chanel=new NotificationChannel(CHANEL_ID,"Channel 1",NotificationManager.IMPORTANCE_DEFAULT);
                chanel.setDescription("Note");
                notificationManager.createNotificationChannel(chanel);

            }
            NotificationCompat.Builder builder=new NotificationCompat.Builder(context,CHANEL_ID)
                    .setContentTitle("Reminder "+ time)
                    .setContentText("NOTE " +title)
                    .setSmallIcon(R.drawable.note)
                    .setColor(Color.RED)
                    .setSound(soundUri)
                    .setCategory(NotificationCompat.CATEGORY_ALARM);
            notificationManager.notify(getNotificationid(),builder.build());

        }
    }
    private int getNotificationid()
    {
        int time=(int)new Date().getTime();
        return time;
    }
}
