package com.lkyear.lllauncher.Torch;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.lkyear.lllauncher.R;

/**
 * Created by hhyix on 2017/3/11.
 */

public class NotificationService extends Service {

    private Notification notification;
    private NotificationManager nManager;
    private Notification.Builder nBuilder;
    public static int NOTIFICATION_ID = 11;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        nManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nBuilder = new Notification.Builder(this);
        nBuilder.setDefaults(Notification.DEFAULT_LIGHTS);
        nBuilder.setTicker("LED补光灯已经打开");
        nBuilder.setContentTitle("手电筒正在后台运行");
        nBuilder.setContentText("点击以查看");
        nBuilder.setOngoing(true);
        nBuilder.setSmallIcon(R.drawable.res_user_info_control_light);
        Intent intentMain = new Intent(this, MainActivity.class);
        PendingIntent pd = PendingIntent.getActivity(this, 0, intentMain, 0);
        nBuilder.setContentIntent(pd);
        notification = nBuilder.build();
        nManager.notify(NOTIFICATION_ID, notification);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        nManager.cancelAll();
        super.onDestroy();
    }
}
