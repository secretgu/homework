package siyugu.homework.activity;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import siyugu.homework.event.Event;

public class HomeworkAlarmReceiver extends BroadcastReceiver {
  @Override
  public void onReceive(Context context, Intent intent) {
    Log.i("HomeworkAlarmReceiver", "Receive broadcast");
    if (!intent.hasExtra(TodaySchedule.ALARM_EVENT_EXTRA)) {
      return;
    }
    Event e = (Event) intent.getSerializableExtra(TodaySchedule.ALARM_EVENT_EXTRA);
    NotificationCompat.Builder mBuilder =
        new NotificationCompat.Builder(context)
            .setSmallIcon(android.R.drawable.stat_notify_chat)
            .setContentTitle("Homework Notifications")
            .setContentText(String
                .format("%s starts in %d min(s)", e.getTitle(), e.getWarningTime().getMinute()));
    mBuilder.setAutoCancel(true);

    NotificationManager mNotificationManager =
        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    mNotificationManager.notify((int) System.currentTimeMillis(), mBuilder.build());

    wakeupScreen(context);
  }

  private void wakeupScreen(Context context) {
    PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
    boolean isScreenOn = pm.isScreenOn();

    Log.i("screen on", "" + isScreenOn);

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
