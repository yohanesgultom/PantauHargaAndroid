package id.pantauharga.android.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.os.IBinder;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import id.pantauharga.android.R;
import id.pantauharga.android.aktivitas.Notification;


/**
 * Created by widodo on 3/31/16.
 */
public class NotificationService extends Service {

    /**
     * indicates how to behave if the service is killed
     */
    int mStartMode;

    /**
     * interface for clients that bind
     */
    IBinder mBinder;

    /**
     * indicates whether onRebind should be used
     */
    boolean mAllowRebind;

    int i = 1;
    /**
     * Called when the service is being created.
     */
    @Override
    public void onCreate() {
        Log.w("NotificationService", "oncreate service");
        Notify("Message", "You've received new message on start");
        new Thread(new Runnable() {
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(60000);
                        Log.w("NotificationService", "oncreate service : " + i);
                        Notify("Message " + i, "You've received new message ");
                        i++;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();
    }

    /**
     * The service is starting, due to a call to startService()
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        return mStartMode;
    }

    /**
     * A client is binding to the service with bindService()
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /**
     * Called when all clients have unbound with unbindService()
     */
    @Override
    public boolean onUnbind(Intent intent) {
        return mAllowRebind;
    }

    /**
     * Called when a client is binding to the service with bindService()
     */
    @Override
    public void onRebind(Intent intent) {

    }

    /**
     * Called when The service is no longer used and is being destroyed
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }


    private void Notify(String notificationTitle, String notificationMessage) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.logo)
                        .setContentTitle(notificationTitle)
                        .setContentText(notificationMessage);

        Intent resultIntent = new Intent(this, Notification.class);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        mBuilder.setContentIntent(resultPendingIntent);
        int mNotificationId = i;
        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }
}
