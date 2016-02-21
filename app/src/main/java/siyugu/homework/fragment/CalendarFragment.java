package siyugu.homework.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import siyugu.homework.BuildConfig;
import siyugu.homework.R;
import siyugu.homework.activity.EditModeActivity;
import siyugu.homework.activity.HomeScreen;
import siyugu.homework.event.Event;
import siyugu.homework.event.EventDB;
import siyugu.homework.event.ItemAdapter;
import siyugu.homework.event.ItemAdapter.EntryItem;
import siyugu.homework.event.ItemAdapter.EventToggleCompleteListener;
import siyugu.homework.event.ItemAdapter.Item;
import siyugu.homework.util.TimeUtil;

// List all events for now
public class CalendarFragment extends ListFragment implements FragmentVisibleListener, EventToggleCompleteListener {
  private static final String TAG = "CalendarFragment";
  private EventDB eventDB;

  @Override
  public void onActivityCreated(Bundle savedInstanceBundle) {
    super.onActivityCreated(savedInstanceBundle);

    if (BuildConfig.DEBUG) {
      if (!(getActivity() instanceof HomeScreen)) {
        throw new AssertionError();
      }
    }
    eventDB = ((HomeScreen) getActivity()).getEventDB();
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    ListView listview = getListView();
    listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
      @Override
      public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Log.i(TAG, "long click");
        Item item = (Item) parent.getAdapter().getItem(position);
        if (item.isSection()) {
          // nothing needs to be done
        } else {
          Event e = ((EntryItem) item).getEvent();
          eventLongClick(e);
        }
        return true;
      }
    });
    listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.i(TAG, "normal click");
        Item item = (Item) parent.getAdapter().getItem(position);
        if (item.isSection()) {
          // nothing needs to be done
        } else {
          Event e = ((EntryItem) item).getEvent();
          eventClick(e);
        }
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
        if (items[item].equals(getResources().getString(R.string.mark_as_completed_menuitem))) {
          Log.i(TAG, e.getTitle() + " change completion status");
          eventDB.addEvent(e.toBuilder().setCompleted(!e.getCompleted()).build());
          fillListView();
        } else if (items[item].equals(getResources().getString(R.string.modify_event_menuitem))) {
          Log.i(TAG, e.getTitle() + " selected to be modified");
          Intent intent = new Intent(getActivity(), EditModeActivity.class);
          intent.putExtra(TodayFragment.EDIT_EVENT_EXTRA, e);
          startActivityForResult(intent, TodayFragment.NEW_EVENT_REQUEST);
        } else if (items[item].equals(getResources().getString(R.string.cancel_menuitem))) {
          dialog.dismiss();
        }
      }
    });
    builder.show();
  }

  private void fillListView() {
    List<Item> items = new ArrayList<Item>();
    Map<LocalDate, List<Event>> eventsByDate = getSortedEventLists(eventDB.getAllEvents());
    for (Map.Entry<LocalDate, List<Event>> entry : eventsByDate.entrySet()) {
      LocalDate date = entry.getKey();
      List<Event> events = entry.getValue();
      items.add(new ItemAdapter.SectionItem(TimeUtil.LOCALDATE_FORMATTER.print(date)));
      for (Event e : events) {
        items.add(new EntryItem(e));
      }
    }
    ItemAdapter adapter = new ItemAdapter(getActivity(),
        R.layout.listview_item_event,
        R.layout.listview_header_event,
        items,
        this);
    setListAdapter(adapter);
  }

  private static Map<LocalDate, List<Event>> getSortedEventLists(List<Event> allEvents) {
    Map<LocalDate, List<Event>> eventsByDate = new TreeMap<>(new Comparator<LocalDate>() {
      @Override
      public int compare(LocalDate lhs, LocalDate rhs) {
        if (lhs.isEqual(rhs)) {
          return 0;
        }
        return lhs.isBefore(rhs) ? -1 : 1;
      }
    });

    for (Event e : allEvents) {
      List<Event> eventsOfDate = eventsByDate.get(e.getDoDate());
      if (eventsOfDate == null) {
        eventsOfDate = new ArrayList<>();
        eventsByDate.put(e.getDoDate(), eventsOfDate);
      }
      eventsOfDate.add(e);
    }

    for (List<Event> eventsOfDate : eventsByDate.values()) {
      Collections.sort(eventsOfDate, new Event.StartTimeComparator());
    }

    return eventsByDate;
  }

  public void eventToggleComplete(Event e) {
    Log.i(TAG, e.getTitle() + " change completion status");
    eventDB.addEvent(e.toBuilder().setCompleted(!e.getCompleted()).build());
    fillListView();
  }
}