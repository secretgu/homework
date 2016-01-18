package siyugu.homework.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.common.annotations.VisibleForTesting;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.util.ArrayList;
import java.util.List;

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

    eventDB = EventDB.getInstance();

    mNowEventsListView = (ListView) findViewById(R.id.now_events_listview);
    mNowEventAdaptor = new EventAdaptor(this, R.layout.list_item_view, nowEventsForTesting());
    View header = (View) getLayoutInflater().inflate(R.layout.listview_header_row, null);
    TextView headerText = (TextView) header.findViewById(R.id.txtHeader);
    headerText.setText(R.string.now_header_text);
    mNowEventsListView.addHeaderView(header);
    mNowEventsListView.setAdapter(mNowEventAdaptor);

    mUpcomingEventsListView = (ListView) findViewById(R.id.upcoming_events_listview);
    mUpcomingEventAdaptor = new EventAdaptor(this, R.layout.list_item_view, upComingEventsForTesting());
    header = (View) getLayoutInflater().inflate(R.layout.listview_header_row, null);
    headerText = (TextView) header.findViewById(R.id.txtHeader);
    headerText.setText(R.string.upcoming_header_text);
    mUpcomingEventsListView.addHeaderView(header);
    mUpcomingEventsListView.setAdapter(mUpcomingEventAdaptor);
  }

  public void onRefreshTodayClick(View view) {
  }

  public void onNewEventClick(View view) {
    Intent intent = new Intent(this, EditModeActivity.class);
    // TODO: change to startActivityForResult and refresh on return
    startActivity(intent);
  }

  @VisibleForTesting
  private static Event[] nowEventsForTesting() {
    List<Event> events = new ArrayList<Event>();

    LocalDate today = new LocalDate();
    LocalTime now = new LocalTime();

    events.add(new Event(
        Event.TypeOfWork.HOMEWORK,
        "CS425 MP1",
        TimeUtil.LOCALDATE_FORMATTER.print(today),
        TimeUtil.LOCALTIME_FORMATTER.print(now.plusHours(1)),
        1, 0));
    events.add(new Event(
        Event.TypeOfWork.HOMEWORK,
        "CS423 MP1",
        TimeUtil.LOCALDATE_FORMATTER.print(today),
        TimeUtil.LOCALTIME_FORMATTER.print(now.plusHours(2)),
        0, 30));

    return events.toArray(new Event[events.size()]);
  }

  @VisibleForTesting
  private static Event[] upComingEventsForTesting() {
    List<Event> events = new ArrayList<Event>();

    LocalDate today = new LocalDate();
    LocalTime now = new LocalTime();

    events.add(new Event(
        Event.TypeOfWork.CLUB,
        "Beer",
        TimeUtil.LOCALDATE_FORMATTER.print(today),
        TimeUtil.LOCALTIME_FORMATTER.print(now.plusHours(3)),
        1, 15));
    events.add(new Event(
        Event.TypeOfWork.PERSONAL,
        "GYM",
        TimeUtil.LOCALDATE_FORMATTER.print(today),
        TimeUtil.LOCALTIME_FORMATTER.print(now.plusHours(4)),
        2, 15));

    return events.toArray(new Event[events.size()]);
  }
}
