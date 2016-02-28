package siyugu.homework.activity;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import siyugu.homework.BuildConfig;
import siyugu.homework.event.Event;
import siyugu.homework.fragment.TodayFragment;

public class HomeworkAlarmReceiver extends BroadcastReceiver {
  private static final String TAG = "HomeworkAlarmReceiver";

  @Override
  public void onReceive(Context context, Intent intent) {
    if (BuildConfig.DEBUG) {
      Log.i(TAG, "Receive broadcast");
    }
    if (!intent.hasExtra(TodayFragment.ALARM_EVENT_EXTRA)) {
      return;
    }

    Event e = (Event) intent.getSerializableExtra(TodayFragment.ALARM_EVENT_EXTRA);
    int minuteToStart = e.getWarningTime().getMinute();
    Duration timeLeft = new Duration(DateTime.now(), e.getDoDate().toDateTime(e.getStartTime()));
    if (BuildConfig.DEBUG) {
      Log.d(TAG, String.format(
          "warning ahead: %d, time left: %d", minuteToStart, (int) timeLeft.getStandardMinutes()));
    }
    minuteToStart = Math.min(minuteToStart, (int) timeLeft.getStandardMinutes());
    String notifyText;
    if (minuteToStart == 0) {
      notifyText = String.format("%s is starting now", e.getTitle());
    } else {
      notifyText = String.format("%s is starting in %d min(s)", e.getTitle(), minuteToStart);
    }
    NotificationCompat.Builder mBuilder =
        new NotificationCompat.Builder(context)
            .setSmallIcon(android.R.drawable.stat_notify_chat)
            .setContentTitle("Homework Notifications")
            .setContentText(notifyText)
            .setContentIntent(PendingIntent
                .getActivity(context,
                    0,
                    new Intent(context, HomeScreen.class),
                    PendingIntent.FLAG_ONE_SHOT));
    mBuilder.setAutoCancel(true);
    Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    mBuilder.setSound(alarmSound);

    NotificationManager mNotificationManager =
        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    mNotificationManager.notify((int) System.currentTimeMillis(), mBuilder.build());

    wakeupScreen(context);
  }

  private void wakeupScreen(Context context) {
    PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
    boolean isScreenOn = pm.isScreenOn();

    if (BuildConfig.DEBUG) {
      Log.i(TAG, "screen on: " + isScreenOn);
    }

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
