package com.example.batterychecker;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.BatteryManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import static androidx.core.app.NotificationCompat.PRIORITY_MIN;

public class Music extends Service {
    public static final String NOTIFICATION_CHANNEL_ID = "com.example.batterychecker";
    public static final String CHANNEL_NAME = "My Background Service";
    private IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
    MediaPlayer mediaPlayer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String x = intent.getStringExtra("Extra");
        mediaPlayer = MediaPlayer.create(this, R.raw.music);
        if (x.equals("On")) {
            registerReceiver(broadcastReceiver, intentFilter);
        } else if (x.equals("Start")) {
            mediaPlayer.start();
            onFullBattery();
        }
//        else if(x.equals("Stop")){
//            unregisterReceiver(broadcastReceiver);
//        }
        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(1, new Notification());
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.reset();
    }

    private  void onFullBattery(){
        Intent intentOpenApp = new Intent(this, MainActivity.class);
        intentOpenApp.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Intent intentStopSv = new Intent(this, BatteryReceiver.class);
        intentStopSv.setAction(Intent.ACTION_PICK);
        PendingIntent pendingIntentStopSv = PendingIntent.getBroadcast(this, 0, intentStopSv, 0);
        PendingIntent pendingIntentOpenApp = PendingIntent.getActivity(this, 0, intentOpenApp, 0);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.battery)
                .setContentTitle("Pin sạc đủ rồi rút sạc đi bạn êiiiii")
                .setPriority(NotificationManager.IMPORTANCE_MAX)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setContentIntent(pendingIntentOpenApp)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .addAction(R.drawable.battery, "Tắt thông báo", pendingIntentStopSv)
                .build();
        startForeground(2, notification);
    }
    private void startMyOwnForeground() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_NONE);
            chan.setLightColor(Color.BLUE);
            chan.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert manager != null;
            manager.createNotificationChannel(chan);
            SharedPreferences sharedPreferences = getSharedPreferences("request", Context.MODE_PRIVATE);
            int percent = sharedPreferences.getInt("percent", 0);
            //Intent mo app
            Intent intentOpenApp = new Intent(this, MainActivity.class);
            intentOpenApp.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntentOpenApp = PendingIntent.getActivity(this, 0, intentOpenApp, 0);
            //Intent stop service
            Intent intentStopSv = new Intent(this, BatteryReceiver.class);
            intentStopSv.setAction(Intent.ACTION_PICK);
            PendingIntent pendingIntentStopSv = PendingIntent.getBroadcast(this, 0, intentStopSv, 0);
            //create notif
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
            Notification notification = notificationBuilder.setOngoing(true)
                    .setSmallIcon(R.drawable.battery)
                    .setContentTitle("Pin đến mức " + percent + "% sẽ thông báo")
                    .setPriority(NotificationManager.IMPORTANCE_MIN)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .setContentIntent(pendingIntentOpenApp)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .addAction(R.drawable.battery, "Tắt thông báo", pendingIntentStopSv)
                    .build();
            startForeground(2, notification);
        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            int percentage = level * 100 / scale;
            Log.d("Percent", percentage+"");
            SharedPreferences sharedPreferences;
            sharedPreferences = context.getSharedPreferences("request", MODE_PRIVATE);
            int percent = sharedPreferences.getInt("percent", 0);
            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            boolean check = sharedPreferences.getBoolean("check", false);
//            if(status==BatteryManager.BATTERY_STATUS_DISCHARGING){
//                Intent intentStop = new Intent(context, Music.class);
//                intentStop.putExtra("Extra", "Stop");
//                startService(intentStop);
//            }
//            else
                if (percentage >= percent && check == true&&status==BatteryManager.BATTERY_STATUS_CHARGING) {
                Intent intentStart = new Intent(context, Music.class);
                intentStart.putExtra("Extra", "Start");
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("check", false);
                editor.commit();
                startService(intentStart);
            }


        }
    };
}
