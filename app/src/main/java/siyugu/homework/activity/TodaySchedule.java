package siyugu.homework.activity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import org.joda.time.DateTime;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import siyugu.homework.R;
import siyugu.homework.event.Event;
import siyugu.homework.event.EventDB;
import siyugu.homework.util.TimeUtil;

public class TodaySchedule extends AppCompatActivity {
  private EventDB eventDB;
  private ListView mTodayEventsListView;
  private AlarmManager mAlarmManager;

  public final static String NEW_EVENT_EXTRA = "NEW_EVENT_EXTRA";
  public final static String EDIT_EVENT_EXTRA = "EDIT_EVENT_EXTRA";
  public final static String ALARM_EVENT_EXTRA = "ALARM_EVENT_EXTRA";
  public final static String EVENTS_FILE_PATH = "homework_events.ser";

  private final static String TAG = "TodaySchedule";
  private final static int NEW_EVENT_REQUEST = 1;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.today_schedule_view);
    mTodayEventsListView = (ListView) findViewById(R.id.today_events_listview);
    mTodayEventsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
      @Override
      public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Log.i(TAG, "long clicked");
        Item item = (Item) mTodayEventsListView.getAdapter().getItem(position);
        if (item.isSection()) {
          // nothing needs to be done
        } else {
          Event e = ((EntryItem) item).getEvent();
          eventLongClick(e);
        }
        return true;
      }
    });

    try {
      eventDB = new EventDB(getEventsFilePath());
    } catch (Exception e) {
      Log.e(TAG, "FATAL: not able to load " + EVENTS_FILE_PATH);
      throw new RuntimeException(e);
    }

    mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
  }

  @Override
  public void onResume() {
    Log.i(TAG, "onResume");
    super.onResume();
    fillListView();
  }

  public void eventLongClick(final Event e) {
    final CharSequence[] items = getResources().getStringArray(R.array.event_longclick_menus);

    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle(R.string.modify_event_menu_title);
    builder.setItems(items, new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int item) {
        if (items[item].equals(getResources().getString(R.string.mark_as_completed_menuitem))) {
          Log.i(TAG, e.getTitle() + " mark as completed");
          if (!e.getCompleted()) {
            eventDB.addEvent(e.toBuilder().setCompleted(true).build());
            fillListView();
          }
        } else if (items[item].equals(getResources().getString(R.string.modify_event_menuitem))) {
          Log.i(TAG, e.getTitle() + " selected to be modified");
          Intent intent = new Intent(TodaySchedule.this, EditModeActivity.class);
          intent.putExtra(EDIT_EVENT_EXTRA, e);
          startActivityForResult(intent, NEW_EVENT_REQUEST);
        } else if (items[item].equals(getResources().getString(R.string.cancel_menuitem))) {
          dialog.dismiss();
        }
      }
    });
    builder.show();
  }

  private File getEventsFilePath() {
    File eventsFile = new File(this.getFilesDir(), EVENTS_FILE_PATH);
    Log.i(TAG, eventsFile.getAbsolutePath());
    return eventsFile;
  }

  @Override
  public void onStop() {
    Log.i(TAG, "onStop");
    try {
      eventDB.flush();
    } catch (IOException e) {
      Log.e(TAG, "FATAL: not able to save data to " + EVENTS_FILE_PATH);
      throw new RuntimeException(e);
    }
    super.onStop();
  }

  public void onRefreshTodayClick(View view) {
    fillListView();
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
        Event newEvent = (Event) data.getSerializableExtra(NEW_EVENT_EXTRA);
        if (newEvent != null) {
          eventDB.addEvent(newEvent);
          scheduleNotification(newEvent);
          fillListView();
        }
      }
    }
  }

  private void scheduleNotification(Event e) {
    Intent intent = new Intent(this, HomeworkAlarmReceiver.class);
    intent.putExtra(ALARM_EVENT_EXTRA, e);
    PendingIntent pendingIntent = PendingIntent
        .getBroadcast(this, (int) e.getId(), intent, PendingIntent.FLAG_ONE_SHOT);

    DateTime timeToFire = e.getDoDate().toDateTime(e.getStartTime())
        .minusMinutes(e.getWarningTime().getMinute());
    Log.i(TAG,
        String.format("Alarm for event id %d schedule at %s",
            e.getId(),
            timeToFire.toString(TimeUtil.DATETIME_DEBUG_PATTERN)));
    timeToFire.minusMinutes(e.getWarningTime().getMinute());
    mAlarmManager.set(AlarmManager.RTC_WAKEUP,
        timeToFire.toInstant().getMillis(),
        pendingIntent);
  }

  private void fillListView() {
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
        R.layout.listview_item_event,
        R.layout.listview_header_event,
        items);
    // TODO: maybe have a "Past" section as well
    mTodayEventsListView.setAdapter(adaptor);
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
      CheckBox mTitleText = (CheckBox) view.findViewById(R.id.event_title_item);
      TextView mStartTimeText = (TextView) view.findViewById(R.id.start_time_text_item);
      TextView mTimePermittedText = (TextView) view.findViewById(R.id.time_permitted_text_item);

      Event e = item.getEvent();
      mEventTypeText.setText(e.getTypeOfWork().toString());
      mTitleText.setText(e.getTitle());
      mTitleText.setChecked(e.getCompleted());
      mTimePermittedText.setText(
          String.format("%d hr %d min",
              e.getPermittedTime().getHours(),
              e.getPermittedTime().getMinutes()));
      mStartTimeText.setText(TimeUtil.LOCALTIME_FORMATTER.print(e.getStartTime()));
      return view;
    }
  }
}
