package siyugu.homework.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import siyugu.homework.R;
import siyugu.homework.fragment.CalendarFragment;
import siyugu.homework.fragment.ReferenceFragment;
import siyugu.homework.fragment.SimplePagerAdapter;
import siyugu.homework.fragment.TodayFragment;

public class HomeScreen extends AppCompatActivity {

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
  }
}