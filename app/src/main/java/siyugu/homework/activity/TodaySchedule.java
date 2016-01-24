package siyugu.homework.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

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
  private ListView mTodayEventsListView;

  public final static String NEW_EVENT_EXTRA = "NEW_EVENT_EXTRA";

  private final static String TAG = "TodaySchedule";
  private final static int NEW_EVENT_REQUEST = 1;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.today_schedule_view);
    mTodayEventsListView = (ListView) findViewById(R.id.today_events_listview);

    initializeEventDB();
    fillListView(mTodayEventsListView);
  }

  private void initializeEventDB() {
    eventDB = new EventDB();

    // TODO: later probably the initialization will happen by deserializing data in the Bundle
    initializeEventDBForTesting();
  }

  private void initializeEventDBForTesting() {
    LocalDate today = new LocalDate();
    LocalTime now = new LocalTime();

    // now
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
        Event.TypeOfWork.HOMEWORK,
        "CS425 MP2",
        TimeUtil.LOCALDATE_FORMATTER.print(today),
        TimeUtil.LOCALTIME_FORMATTER.print(now.plusHours(1)),
        1, 0));
    eventDB.addEvent(new Event(
        Event.TypeOfWork.HOMEWORK,
        "CS423 MP2",
        TimeUtil.LOCALDATE_FORMATTER.print(today),
        TimeUtil.LOCALTIME_FORMATTER.print(now.plusHours(2)),
        0, 30));

    // upcoming
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
    eventDB.addEvent(new Event(
        Event.TypeOfWork.CLUB,
        "Beer2",
        TimeUtil.LOCALDATE_FORMATTER.print(today),
        TimeUtil.LOCALTIME_FORMATTER.print(now.plusHours(4).plusMinutes(30)),
        1, 15));
    eventDB.addEvent(new Event(
        Event.TypeOfWork.PERSONAL,
        "GYM2",
        TimeUtil.LOCALDATE_FORMATTER.print(today),
        TimeUtil.LOCALTIME_FORMATTER.print(now.plusHours(5)),
        2, 15));
  }

  public void onRefreshTodayClick(View view) {
    fillListView(mTodayEventsListView);
  }

  public void onNewEventClick(View view) {
    Intent intent = new Intent(this, EditModeActivity.class);
    startActivityForResult(intent, NEW_EVENT_REQUEST);
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == NEW_EVENT_REQUEST) {
      if (resultCode == Activity.RESULT_OK) {
        Event newEvent = data.getParcelableExtra(NEW_EVENT_EXTRA);
        if (newEvent != null) {
          eventDB.addEvent(newEvent);
          fillListView(mTodayEventsListView);
        }
      }
    }
  }

  private void fillListView(ListView view) {
    List<Item> items = new ArrayList<Item>();
    items.add(new SectionItem("Now"));
    for (Event e : eventDB.getNowEvents()) {
      items.add(new EntryItem(e));
    }
    items.add(new SectionItem("Upcoming"));
    for (Event e : eventDB.getUpcomingEvents()) {
      items.add(new EntryItem(e));
    }
    ItemAdaptor adaptor = new ItemAdaptor(this,
        R.layout.list_item_view,
        R.layout.listview_header_row,
        items);
    view.setAdapter(adaptor);
  }

  private interface Item {
    public boolean isSection();
  }

  private static class SectionItem implements Item {
    private String headerText;

    public SectionItem(String header) {
      this.headerText = header;
    }

    public String getHeaderText() {
      return headerText;
    }

    @Override
    public boolean isSection() {
      return true;
    }
  }

  private static class EntryItem implements Item {
    private Event event;

    public EntryItem(Event event) {
      this.event = event;
    }

    public Event getEvent() {
      return event;
    }

    @Override
    public boolean isSection() {
      return false;
    }
  }

  private static class ItemAdaptor extends ArrayAdapter<Item> {
    private final Context context;
    private final int entryLayoutResourceId;
    private final int sectionLayoutResourceId;
    private final List<Item> data;


    public ItemAdaptor(Context context,
                       int entryLayoutResourceId,
                       int sectionLayoutResourceId,
                       List<Item> data) {
      super(context, 0, data);

      this.context = context;
      this.entryLayoutResourceId = entryLayoutResourceId;
      this.sectionLayoutResourceId = sectionLayoutResourceId;
      this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      View view = convertView;
      Item item = data.get(position);

      if (item.isSection()) {
        return getSectionView((SectionItem) item, parent, view);
      } else {
        return getEntryView((EntryItem) item, parent, view);
      }
    }

    private View getSectionView(SectionItem item, ViewGroup parent, View view) {
      LayoutInflater inflater = ((Activity) context).getLayoutInflater();
      view = inflater.inflate(sectionLayoutResourceId, parent, false);
      TextView header = (TextView) view.findViewById(R.id.txtHeader);
      header.setText(item.getHeaderText());
      return view;
    }

    private View getEntryView(EntryItem item, ViewGroup parent, View view) {
      LayoutInflater inflater = ((Activity) context).getLayoutInflater();
      view = inflater.inflate(entryLayoutResourceId, parent, false);

      TextView mEventTypeText = (TextView) view.findViewById(R.id.event_type_item);
      CheckBox mDescriptionText = (CheckBox) view.findViewById(R.id.event_description_item);
      TextView mStartTimeText = (TextView) view.findViewById(R.id.start_time_text_item);
      TextView mTimePermittedText = (TextView) view.findViewById(R.id.time_permitted_text_item);

      Event e = item.getEvent();
      mEventTypeText.setText(e.getTypeOfWork().toString());
      mDescriptionText.setText(e.getDescription());
      mDescriptionText.setChecked(e.getCompleted());
      mTimePermittedText.setText(
          String.format("%d hr %d min",
              e.getPermittedTime().getHours(),
              e.getPermittedTime().getMinutes()));
      mStartTimeText.setText(TimeUtil.LOCALTIME_FORMATTER.print(e.getStartTime()));
      return view;
    }
  }
}
