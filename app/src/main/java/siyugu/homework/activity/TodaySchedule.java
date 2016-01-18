package siyugu.homework.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import siyugu.homework.R;
import siyugu.homework.event.Event;
import siyugu.homework.event.EventDB;
import siyugu.homework.util.TimeUtil;

public class TodaySchedule extends AppCompatActivity {
  private EventDB eventDB;
  private ListView mNowEventsListView;
  private EventAdaptor mNowEventAdaptor;
  private ListView mUpcomingEventsListView;
  private EventAdaptor mUpcomingEventAdaptor;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.today_schedule_view);

    initializeEventDB();

    mNowEventsListView = (ListView) findViewById(R.id.now_events_listview);
    mNowEventAdaptor = new EventAdaptor(this, R.layout.list_item_view, eventDB.getNowEvents());
    View header = (View) getLayoutInflater().inflate(R.layout.listview_header_row, null);
    TextView headerText = (TextView) header.findViewById(R.id.txtHeader);
    headerText.setText(R.string.now_header_text);
    mNowEventsListView.addHeaderView(header);
    mNowEventsListView.setAdapter(mNowEventAdaptor);

    mUpcomingEventsListView = (ListView) findViewById(R.id.upcoming_events_listview);
    mUpcomingEventAdaptor = new EventAdaptor(this, R.layout.list_item_view, eventDB.getUpcomingEvents());
    header = (View) getLayoutInflater().inflate(R.layout.listview_header_row, null);
    headerText = (TextView) header.findViewById(R.id.txtHeader);
    headerText.setText(R.string.upcoming_header_text);
    mUpcomingEventsListView.addHeaderView(header);
    mUpcomingEventsListView.setAdapter(mUpcomingEventAdaptor);
  }

  private void initializeEventDB() {
    eventDB = EventDB.getInstance();

    // TODO: later probably the initialization will happen by deserializing data in the Bundle
    initializeEventDBForTesting();
  }

  private void initializeEventDBForTesting() {
    LocalDate today = new LocalDate();
    LocalTime now = new LocalTime();

    eventDB.addEvent(new Event(
        Event.TypeOfWork.HOMEWORK,
        "CS425 MP1",
        TimeUtil.LOCALDATE_FORMATTER.print(today),
        TimeUtil.LOCALTIME_FORMATTER.print(now.plusHours(1)),
        1, 0));
    eventDB.addEvent(new Event(
        Event.TypeOfWork.HOMEWORK,
        "CS423 MP1",
        TimeUtil.LOCALDATE_FORMATTER.print(today),
        TimeUtil.LOCALTIME_FORMATTER.print(now.plusHours(2)),
        0, 30));
    eventDB.addEvent(new Event(
        Event.TypeOfWork.CLUB,
        "Beer",
        TimeUtil.LOCALDATE_FORMATTER.print(today),
        TimeUtil.LOCALTIME_FORMATTER.print(now.plusHours(4).plusMinutes(30)),
        1, 15));
    eventDB.addEvent(new Event(
        Event.TypeOfWork.PERSONAL,
        "GYM",
        TimeUtil.LOCALDATE_FORMATTER.print(today),
        TimeUtil.LOCALTIME_FORMATTER.print(now.plusHours(5)),
        2, 15));
  }

  public void onRefreshTodayClick(View view) {
    mNowEventAdaptor.clear();
    mNowEventAdaptor.addAll(eventDB.getNowEvents());
    mUpcomingEventAdaptor.clear();
    mUpcomingEventAdaptor.addAll(eventDB.getUpcomingEvents());
  }

  public void onNewEventClick(View view) {
    Intent intent = new Intent(this, EditModeActivity.class);
    // TODO: change to startActivityForResult and refresh on return
    startActivity(intent);
  }
}
