package siyugu.homework.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.roomorama.caldroid.CaldroidFragment;

import siyugu.homework.R;

public class CalendarFragmentV2 extends Fragment {
  private CaldroidFragment mCaldroidFragment;
  private ListView mDateEventListView;

  @Override
  public View onCreateView(LayoutInflater inflater,
                           ViewGroup container,
                           Bundle savedInstanceState) {
    return inflater.inflate(R.layout.calendar_fragment_v2, container, false);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    mCaldroidFragment = new CaldroidSampleCustomFragment();

    FragmentTransaction t = getFragmentManager().beginTransaction();
    t.replace(R.id.calendar_view, mCaldroidFragment);
    t.commit();

    mDateEventListView = (ListView) view.findViewById(R.id.date_event_listview);

    ArrayAdapter<CharSequence> mAdapter = ArrayAdapter.createFromResource(
        getActivity(),
        R.array.reference_links_name,
        android.R.layout.simple_list_item_1);
    mDateEventListView.setAdapter(mAdapter);
  }
}
