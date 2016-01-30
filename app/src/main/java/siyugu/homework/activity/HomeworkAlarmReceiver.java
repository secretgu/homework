package siyugu.homework.activity;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

public class HomeworkAlarmReceiver extends WakefulBroadcastReceiver {
  public HomeworkAlarmReceiver() {
  }

  @Override
  public void onReceive(Context context, Intent intent) {
    String text = "Hello world!";
    if (intent.hasExtra(TestAlarmActivity.DATA_KEY)) {
      text = intent.getStringExtra(TestAlarmActivity.DATA_KEY);
    }
    NotificationCompat.Builder mBuilder =
        new NotificationCompat.Builder(context)
            .setSmallIcon(android.R.drawable.stat_notify_chat)
            .setContentTitle("Homework")
            .setContentText(text);
    mBuilder.setAutoCancel(true);

    NotificationManager mNotificationManager =
        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    mNotificationManager.notify((int) System.currentTimeMillis(), mBuilder.build());

    wakeupScreen(context);
  }

  private void wakeupScreen(Context context) {
    PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
    boolean isScreenOn = pm.isScreenOn();

    Log.e("screen on", "" + isScreenOn);

    if (isScreenOn == false) {

      PowerManager.WakeLock wl = pm
          .newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE,
              "MyLock");

      wl.acquire(10000);
      PowerManager.WakeLock wl_cpu = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyCpuLock");

      wl_cpu.acquire(10000);
    }
  }
}
