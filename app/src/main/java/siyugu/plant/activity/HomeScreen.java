package siyugu.plant.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import siyugu.plant.BuildConfig;
import siyugu.plant.R;
import siyugu.plant.event.EventDB;
import siyugu.plant.fragment.CalendarFragment;
import siyugu.plant.fragment.FragmentVisibleListener;
import siyugu.plant.fragment.ReferenceFragment;
import siyugu.plant.fragment.SimplePagerAdapter;
import siyugu.plant.fragment.TodayFragment;

public class HomeScreen extends AppCompatActivity {
  private static final String TAG = "HomeScreen";
  private static final String EVENTS_FILE_PATH = "homework_events.ser";

  private EventDB eventDB;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_home_screen);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    final ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
    final SimplePagerAdapter adapter = new SimplePagerAdapter(getSupportFragmentManager());
    adapter.addFragment(new TodayFragment(), "Today");
    adapter.addFragment(new CalendarFragment(), "Calendar");
    adapter.addFragment(new ReferenceFragment(), "Reference");
    viewPager.setAdapter(adapter);

    TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
    tabLayout.setupWithViewPager(viewPager);

    try {
      eventDB = new EventDB(getEventsFilePath());
    } catch (Exception e) {
      if (BuildConfig.DEBUG) {
        Log.e(TAG, "FATAL: not able to load " + EVENTS_FILE_PATH);
      }
      throw new RuntimeException(e);
    }

    viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
      }

      @Override
      public void onPageSelected(int position) {
        Fragment fragment = (Fragment) adapter.instantiateItem(viewPager, position);
        if (fragment != null && fragment instanceof FragmentVisibleListener) {
          ((FragmentVisibleListener) fragment).fragmentBecameVisible();
        }
      }

      @Override
      public void onPageScrollStateChanged(int state) {
      }
    });
  }

  @Override
  public void onStop() {
    if (BuildConfig.DEBUG) {
      Log.i(TAG, "onStop");
    }
    try {
      eventDB.flush();
    } catch (IOException e) {
      if (BuildConfig.DEBUG) {
        Log.e(TAG, "FATAL: not able to save data to " + EVENTS_FILE_PATH);
      }
      throw new RuntimeException(e);
    }
    super.onStop();
  }

  private File getEventsFilePath() {
    File eventsFile = new File(getFilesDir(), EVENTS_FILE_PATH);
    if (BuildConfig.DEBUG) {
      Log.i(TAG, eventsFile.getAbsolutePath());
    }
    return eventsFile;
  }

  public EventDB getEventDB() {
    return eventDB;
  }
}