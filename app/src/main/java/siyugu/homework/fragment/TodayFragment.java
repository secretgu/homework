package siyugu.homework.fragment;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import siyugu.homework.BuildConfig;
import siyugu.homework.R;
import siyugu.homework.activity.EditModeActivity;
import siyugu.homework.activity.HomeScreen;
import siyugu.homework.activity.HomeworkAlarmReceiver;
import siyugu.homework.event.Event;
import siyugu.homework.event.EventDB;
import siyugu.homework.event.EventPredicates;
import siyugu.homework.event.ItemAdapter;
import siyugu.homework.event.ItemAdapter.EntryItem;
import siyugu.homework.event.ItemAdapter.EventToggleCompleteListener;
import siyugu.homework.event.ItemAdapter.Item;
import siyugu.homework.event.ItemAdapter.SectionItem;
import siyugu.homework.util.TimeUtil;

public class TodayFragment extends Fragment implements FragmentVisibleListener, EventToggleCompleteListener {
  private final static String TAG = "TodayFragment";
  public final static int NEW_EVENT_REQUEST = 1;

  public final static String NEW_EVENT_EXTRA = "NEW_EVENT_EXTRA";
  public final static String EDIT_EVENT_EXTRA = "EDIT_EVENT_EXTRA";
  public final static String VIEW_EVENT_EXTRA = "VIEW_EVENT_EXTRA";
  public final static String ALARM_EVENT_EXTRA = "ALARM_EVENT_EXTRA";

  private EventDB eventDB;
  private ListView mTodayEventsListView;
  private AlarmManager mAlarmManager;

  @Override
  public void onActivityCreated(Bundle savedInstanceBundle) {
    super.onActivityCreated(savedInstanceBundle);

    if (BuildConfig.DEBUG) {
      if (!(getActivity() instanceof HomeScreen)) {
        throw new AssertionError();
      }
    }
    eventDB = ((HomeScreen) getActivity()).getEventDB();
    mAlarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
  }

  @Override
  public View onCreateView(LayoutInflater inflater,
                           ViewGroup container,
                           Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_today, container, false);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    mTodayEventsListView = (ListView) view.findViewById(R.id.today_events_listview);
    mTodayEventsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
      @Override
      public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Log.i(TAG, "long click");
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
    mTodayEventsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.i(TAG, "normal click");
        Item item = (Item) mTodayEventsListView.getAdapter().getItem(position);
        if (item.isSection()) {
          // nothing needs to be done
        } else {
          Event e = ((EntryItem) item).getEvent();
          eventClick(e);
        }
      }
    });

    ImageButton refreshBtn = (ImageButton) view.findViewById(R.id.refresh_today_btn);
    refreshBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        onRefreshTodayClick();
      }
    });

    ImageButton newEventBtn = (ImageButton) view.findViewById(R.id.new_event_btn);
    newEventBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        onNewEventClick();
      }
    });
  }

  @Override
  public void onResume() {
    Log.i(TAG, "onResume");
    super.onResume();
    fillListView();
  }

  @Override
  public void fragmentBecameVisible() {
    Log.i(TAG, "become visible");
    // not efficient, but for a toy app...
    fillListView();
  }

  public void eventClick(final Event e) {
    Log.i(TAG, e.getTitle() + " selected to be viewed");
    Intent intent = new Intent(getActivity(), EditModeActivity.class);
    intent.putExtra(VIEW_EVENT_EXTRA, e);
    startActivity(intent);
  }

  public void eventLongClick(final Event e) {
    final CharSequence[] items = getResources().getStringArray(R.array.event_longclick_menus);

    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    builder.setTitle(R.string.modify_event_menu_title);
    builder.setItems(items, new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int item) {
        if (items[item].equals(getResources().getString(R.string.modify_event_menuitem))) {
          Log.i(TAG, e.getTitle() + " selected to be modified");
          Intent intent = new Intent(getActivity(), EditModeActivity.class);
          intent.putExtra(EDIT_EVENT_EXTRA, e);
          startActivityForResult(intent, NEW_EVENT_REQUEST);
        } else if (items[item].equals(getResources().getString(R.string.cancel_menuitem))) {
          dialog.dismiss();
        }
      }
    });
    builder.show();
  }

  public void onRefreshTodayClick() {
    fillListView();
  }

  public void onNewEventClick() {
    Intent intent = new Intent(getActivity(), EditModeActivity.class);
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
    Intent intent = new Intent(getActivity(), HomeworkAlarmReceiver.class);
    intent.putExtra(ALARM_EVENT_EXTRA, e);
    PendingIntent pendingIntent = PendingIntent
        .getBroadcast(getActivity(), (int) e.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

    DateTime timeToDo = e.getDoDate().toDateTime(e.getStartTime());
    DateTime timeToFire = timeToDo.minusMinutes(e.getWarningTime().getMinute());
    if (timeToDo.isBefore(DateTime.now())) {
      Log.i(TAG,
          String.format(
              "Time to fire notification is in past %s",
              timeToFire.toString(TimeUtil.DATETIME_DEBUG_PATTERN)));
      Log.i(TAG, "Cancel alarm for event id " + (int) e.getId());
      mAlarmManager.cancel(pendingIntent);
      return;
    }
    Log.i(TAG,
        String.format("Alarm for event id %d schedule at %s",
            e.getId(),
            timeToFire.toString(TimeUtil.DATETIME_DEBUG_PATTERN)));
    timeToFire.minusMinutes(e.getWarningTime().getMinute());
    mAlarmManager.set(AlarmManager.RTC_WAKEUP,
        timeToFire.toInstant().getMillis(),
        pendingIntent);
  }

  //--------------------------Fill in event list--------------------------
  private void fillListView() {
    List<Item> items = new ArrayList<Item>();
    List<Event> todayEvents = eventDB.getTodayEvents();
    LocalTime currentTime = new LocalTime();

    items.add(new SectionItem("Uncompleted"));
    List<Event> nowEvents = getUncompletedEvents(todayEvents, currentTime);
    Collections.sort(nowEvents, new Event.StartTimeComparator());
    for (Event e : nowEvents) {
      items.add(new EntryItem(e));
    }

    items.add(new SectionItem("Upcoming"));
    List<Event> upcomingEvents = getUpcomingIncompleteEvents(todayEvents, currentTime);
    Collections.sort(upcomingEvents, new Event.StartTimeComparator());
    for (Event e : upcomingEvents) {
      items.add(new EntryItem(e));
    }
    ItemAdapter adaptor = new ItemAdapter(getActivity(),
        R.layout.listview_item_event,
        R.layout.listview_header_event,
        items,
        this);
    mTodayEventsListView.setAdapter(adaptor);
  }

  @VisibleForTesting
  static List<Event> getUncompletedEvents(List<Event> todayEvents, LocalTime currentTime) {
    List<Event> nowEvents = new ArrayList<Event>();
    Predicate<Event> uncompletedEventsPredicate = Predicates
        .and(new EventPredicates.NowEventsPredicate(currentTime),
            new EventPredicates.IncompleteEventsPredicate());
    for (Event e : todayEvents) {
      if (uncompletedEventsPredicate.apply(e)) {
        nowEvents.add(e);
      }
    }
    return nowEvents;
  }

  @VisibleForTesting
  static List<Event> getUpcomingIncompleteEvents(List<Event> todayEvents, LocalTime currentTime) {
    List<Event> upcomingIncompleteEvents = new ArrayList<Event>();
    Predicate<Event> upcomingIncompletePredicate = Predicates
        .and(new EventPredicates.UpcomingEventsPredicate(currentTime),
            new EventPredicates.IncompleteEventsPredicate());
    for (Event e : todayEvents) {
      if (upcomingIncompletePredicate.apply(e)) {
        upcomingIncompleteEvents.add(e);
      }
    }
    return upcomingIncompleteEvents;
  }

  public void eventToggleComplete(Event e) {
    Log.i(TAG, e.getTitle() + " change completion status");
    eventDB.addEvent(e.toBuilder().setCompleted(!e.getCompleted()).build());
    fillListView();
  }
}