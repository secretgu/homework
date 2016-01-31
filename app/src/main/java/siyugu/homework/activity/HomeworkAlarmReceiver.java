package siyugu.homework.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

import siyugu.homework.event.Event;
import siyugu.homework.fragment.TodayFragment;

public class HomeworkAlarmReceiver extends BroadcastReceiver {
  private static final String TAG = "HomeworkAlarmReceiver";

  @Override
  public void onReceive(Context context, Intent intent) {
    Log.i(TAG, "Receive broadcast");
    if (!intent.hasExtra(TodayFragment.ALARM_EVENT_EXTRA)) {
      return;
    }
    Event e = (Event) intent.getSerializableExtra(TodayFragment.ALARM_EVENT_EXTRA);
    Notification.Builder mBuilder =
        new Notification.Builder(context)
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

  private void repeat(Context context, Event e) {
    // TODO: repeat pattern seems to be conflicting with do date a little bit. Low priority.
    switch (e.getRepeatPattern()) {
      case NO_REPEAT:
        break;
      case DAILY:
        break;
      case WEEKDAY:
        break;
      case WEEKEND:
        break;
      default:
        Log.e(TAG, "not defined repeat pattern");
    }
  }

  private void wakeupScreen(Context context) {
    PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
    boolean isScreenOn = pm.isScreenOn();

    Log.i(TAG, "screen on: " + isScreenOn);

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
