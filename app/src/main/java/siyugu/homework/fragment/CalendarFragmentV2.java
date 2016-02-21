package siyugu.homework.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import siyugu.homework.BuildConfig;
import siyugu.homework.R;
import siyugu.homework.activity.HomeScreen;
import siyugu.homework.event.Event;
import siyugu.homework.event.EventDB;
import siyugu.homework.event.ItemAdapter;

public class CalendarFragmentV2 extends Fragment implements ItemAdapter.EventToggleCompleteListener {
  private static final String TAG = "CalendarFragmentV2";
  private static final String CALDROID_SAVED_STATE_KEY = "CALDROID_SAVED_STATE";

  private CaldroidFragment mCaldroidFragment;
  private ListView mDateEventListView;
  private EventDB eventDB;

  @Override
  public View onCreateView(LayoutInflater inflater,
                           ViewGroup container,
                           Bundle savedInstanceState) {
    return inflater.inflate(R.layout.calendar_fragment_v2, container, false);
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
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    mCaldroidFragment = new CaldroidSampleCustomFragment();

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

    mDateEventListView = (ListView) view.findViewById(R.id.date_event_listview);

    final CaldroidListener listener = new CaldroidListener() {
      @Override
      public void onSelectDate(Date date, View view) {
        LocalDate jodaDate = new DateTime(date).toLocalDate();
        CalendarFragmentV2.this.updateEventList(jodaDate);
      }

      @Override
      public void onCaldroidViewCreated() {
        CalendarFragmentV2.this.updateEventList(LocalDate.now());
      }
    };

    mCaldroidFragment.setCaldroidListener(listener);
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);

    if (mCaldroidFragment != null) {
      mCaldroidFragment.saveStatesToKey(outState, CALDROID_SAVED_STATE_KEY);
    }
  }

  private void updateEventList(LocalDate date) {
    List<Event> events = eventDB.getEventsOfDate(date);

    Collections.sort(events, new Event.StartTimeComparator());
    List<ItemAdapter.Item> items = new ArrayList<ItemAdapter.Item>();
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

  public void eventToggleComplete(Event e) {
    Log.i(TAG, e.getTitle() + " change completion status");
    eventDB.addEvent(e.toBuilder().setCompleted(!e.getCompleted()).build());
    updateEventList(e.getDoDate());
  }
}
