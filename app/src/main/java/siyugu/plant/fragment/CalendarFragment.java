package siyugu.plant.fragment;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import siyugu.plant.BuildConfig;
import siyugu.plant.R;
import siyugu.plant.activity.EditModeActivity;
import siyugu.plant.activity.HomeScreen;
import siyugu.plant.activity.HomeworkAlarmReceiver;
import siyugu.plant.event.Event;
import siyugu.plant.event.EventDB;
import siyugu.plant.event.ItemAdapter;
import siyugu.plant.event.ItemAdapter.EventToggleCompleteListener;
import siyugu.plant.util.TimeUtil;

public class CalendarFragment extends Fragment implements FragmentVisibleListener, EventToggleCompleteListener {
  private static final String TAG = "CalendarFragment";
  private static final String CALDROID_SAVED_STATE_KEY = "CALDROID_SAVED_STATE";

  private CaldroidFragment mCaldroidFragment;
  private ListView mDateEventListView;
  private EventDB eventDB;
  private AlarmManager mAlarmManager;

  @Override
  public View onCreateView(LayoutInflater inflater,
                           ViewGroup container,
                           Bundle savedInstanceState) {
    return inflater.inflate(R.layout.calendar_fragment, container, false);
  }

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
    updateSelectedDates();
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    mCaldroidFragment = new CaldroidSimpleCustomFragment();

    if (savedInstanceState != null) {
      mCaldroidFragment.restoreStatesFromKey(savedInstanceState, CALDROID_SAVED_STATE_KEY);
    } else {
      Bundle args = new Bundle();
      LocalDate today = LocalDate.now();
      args.putInt(CaldroidFragment.MONTH, today.getMonthOfYear());
      args.putInt(CaldroidFragment.YEAR, today.getYear());
      args.putBoolean(CaldroidFragment.ENABLE_SWIPE, true);
      args.putBoolean(CaldroidFragment.SIX_WEEKS_IN_CALENDAR, true);

      mCaldroidFragment.setArguments(args);
    }

    FragmentTransaction t = getFragmentManager().beginTransaction();
    t.replace(R.id.calendar_view, mCaldroidFragment);
    t.commit();

    final CaldroidListener listener = new CaldroidListener() {
      @Override
      public void onSelectDate(Date date, View view) {
        LocalDate jodaDate = new DateTime(date).toLocalDate();
        CalendarFragment.this.updateEventList(jodaDate);
      }

      @Override
      public void onCaldroidViewCreated() {
        CalendarFragment.this.updateEventList(LocalDate.now());
      }
    };

    mCaldroidFragment.setCaldroidListener(listener);

    mDateEventListView = (ListView) view.findViewById(R.id.date_event_listview);
    mDateEventListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
      @Override
      public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if (BuildConfig.DEBUG) {
          Log.i(TAG, "long click");
        }
        ItemAdapter.Item item = (ItemAdapter.Item) parent.getAdapter().getItem(position);
        if (item.isSection()) {
          // nothing needs to be done
        } else {
          Event e = ((ItemAdapter.EntryItem) item).getEvent();
          eventLongClick(e);
        }
        return true;
      }
    });
    mDateEventListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (BuildConfig.DEBUG) {
          Log.i(TAG, "normal click");
        }
        ItemAdapter.Item item = (ItemAdapter.Item) parent.getAdapter().getItem(position);
        if (item.isSection()) {
          // nothing needs to be done
        } else {
          Event e = ((ItemAdapter.EntryItem) item).getEvent();
          eventClick(e);
        }
      }
    });
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);

    if (mCaldroidFragment != null) {
      mCaldroidFragment.saveStatesToKey(outState, CALDROID_SAVED_STATE_KEY);
    }
  }

  private void updateSelectedDates() {
    if (BuildConfig.DEBUG) {
      Log.i(TAG, "updateSelectedDates");
    }
    List<Event> allEvents = eventDB.getAllEvents();
    Set<LocalDate> datesHaveEvents = new HashSet<>();
    for (Event e : allEvents) {
      datesHaveEvents.add(e.getDoDate());
    }
    mCaldroidFragment.clearSelectedDates();
    for (LocalDate d : datesHaveEvents) {
      mCaldroidFragment.setSelectedDate(d.toDate());
    }
    mCaldroidFragment.refreshView();
  }

  private void updateEventList(LocalDate date) {
    List<Event> events = eventDB.getEventsOfDate(date);

    Collections.sort(events, new Event.StartTimeComparator());
    List<ItemAdapter.Item> items = new ArrayList<ItemAdapter.Item>();
    items.add(new ItemAdapter.SectionItem(TimeUtil.LOCALDATE_FORMATTER.print(date)));
    for (Event e : events) {
      items.add(new ItemAdapter.EntryItem(e));
    }
    ItemAdapter adapter = new ItemAdapter(getActivity(),
        R.layout.listview_item_event,
        R.layout.listview_header_event,
        items,
        this);
    mDateEventListView.setAdapter(adapter);
  }

  @Override
  public void eventToggleComplete(Event e) {
    if (BuildConfig.DEBUG) {
      Log.i(TAG, e.getTitle() + " change completion status");
    }
    eventDB.addEvent(e.toBuilder().setCompleted(!e.getCompleted()).build());
    updateEventList(e.getDoDate());
  }

  @Override
  public void fragmentBecameVisible() {
    updateSelectedDates();
  }

  public void eventClick(final Event e) {
    if (BuildConfig.DEBUG) {
      Log.i(TAG, e.getTitle() + " selected to be viewed");
    }
    Intent intent = new Intent(getActivity(), EditModeActivity.class);
    intent.putExtra(TodayFragment.VIEW_EVENT_EXTRA, e);
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
          if (BuildConfig.DEBUG) {
            Log.i(TAG, e.getTitle() + " selected to be modified");
          }
          Intent intent = new Intent(getActivity(), EditModeActivity.class);
          intent.putExtra(TodayFragment.EDIT_EVENT_EXTRA, e);
          startActivityForResult(intent, TodayFragment.NEW_EVENT_REQUEST);
        } else if (items[item].equals(getResources().getString(R.string.duplicate_event_menuitem))) {
          if (BuildConfig.DEBUG) {
            Log.i(TAG, e.getTitle() + " selected to be duplicated");
          }
          Intent intent = new Intent(getActivity(), EditModeActivity.class);
          intent.putExtra(TodayFragment.DUPLICATE_EVENT_EXTRA, e);
          startActivityForResult(intent, TodayFragment.NEW_EVENT_REQUEST);
        } else if (items[item].equals(getResources().getString(R.string.cancel_menuitem))) {
          dialog.dismiss();
        }
      }
    });
    builder.show();
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == TodayFragment.NEW_EVENT_REQUEST) {
      if (resultCode == Activity.RESULT_OK) {
        Event newEvent = (Event) data.getSerializableExtra(TodayFragment.NEW_EVENT_EXTRA);
        if (newEvent != null) {
          eventDB.addEvent(newEvent);
          scheduleNotification(newEvent);
          updateSelectedDates();
          updateEventList(newEvent.getDoDate());
        }
      }
    }
  }

  private void scheduleNotification(Event e) {
    Intent intent = new Intent(getActivity(), HomeworkAlarmReceiver.class);
    intent.putExtra(TodayFragment.ALARM_EVENT_EXTRA, e);
    PendingIntent pendingIntent = PendingIntent
        .getBroadcast(getActivity(), (int) e.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

    DateTime timeToDo = e.getDoDate().toDateTime(e.getStartTime());
    DateTime timeToFire = timeToDo.minusMinutes(e.getWarningTime().getMinute());
    if (timeToDo.isBefore(DateTime.now())) {
      if (BuildConfig.DEBUG) {
        Log.i(TAG,
            String.format(
                "Time to fire notification is in past %s",
                timeToFire.toString(TimeUtil.DATETIME_DEBUG_PATTERN)));
        Log.i(TAG, "Cancel alarm for event id " + (int) e.getId());
      }
      mAlarmManager.cancel(pendingIntent);
      return;
    }
    if (BuildConfig.DEBUG) {
      Log.i(TAG,
          String.format("Alarm for event id %d schedule at %s",
              e.getId(),
              timeToFire.toString(TimeUtil.DATETIME_DEBUG_PATTERN)));
    }
    timeToFire.minusMinutes(e.getWarningTime().getMinute());
    mAlarmManager.set(AlarmManager.RTC_WAKEUP,
        timeToFire.toInstant().getMillis(),
        pendingIntent);
  }
}
