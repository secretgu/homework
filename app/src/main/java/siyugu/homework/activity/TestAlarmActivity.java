package siyugu.homework.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.NumberPicker;

import org.joda.time.DateTime;

import siyugu.homework.R;

public class TestAlarmActivity extends AppCompatActivity {
  private NumberPicker mNumPicker;
  private AlarmManager mAlarmManager;

  public final static String DATA_KEY = "DATA_KEY";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_test_alarm);

    mNumPicker = (NumberPicker) findViewById(R.id.num_picker);
    mNumPicker.setMinValue(1);
    mNumPicker.setMaxValue(6);

    mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
  }

  public void onAddAlarmClick(View view) {
    int second = mNumPicker.getValue();
    Intent intent = new Intent(this, HomeworkAlarmReceiver.class);
    intent.putExtra(DATA_KEY, String.valueOf(second));
    PendingIntent pendingIntent = PendingIntent
        .getBroadcast(this, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_ONE_SHOT);

    DateTime timeToFire = DateTime.now().plusSeconds(second * 10);
    mAlarmManager.set(AlarmManager.RTC_WAKEUP,
        timeToFire.toInstant().getMillis(),
        pendingIntent);
  }
}
