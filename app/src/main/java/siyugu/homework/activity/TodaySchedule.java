package siyugu.homework.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;

import siyugu.homework.R;
import siyugu.homework.event.EventDB;

public class TodaySchedule extends AppCompatActivity {
  private EventDB eventDB;
  private ListView mEventsListView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.today_schedule_view);

    eventDB = EventDB.getInstance();
  }

  public void onRefreshTodayClick(View view) {

  }

  public void onNewEventClick(View view) {
    Intent intent = new Intent(this, EditModeActivity.class);
    startActivity(intent);
  }
}
