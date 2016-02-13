package siyugu.homework.fragment;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
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
import siyugu.homework.activity.HomeScreen;
import siyugu.homework.event.Event;
import siyugu.homework.event.EventDB;
import siyugu.homework.event.ItemAdapter;
import siyugu.homework.event.ItemAdapter.EntryItem;
import siyugu.homework.event.ItemAdapter.Item;
import siyugu.homework.util.TimeUtil;

// List all events for now
public class CalendarFragment extends ListFragment {
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
    fillListView();
  }

  @Override
  public void onResume() {
    Log.i(TAG, "onResume");
    super.onResume();
    fillListView();
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
        items);
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
}