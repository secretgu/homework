package siyugu.homework.event;

import android.support.annotation.VisibleForTesting;
import android.util.Log;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;

import org.joda.time.Days;
import org.joda.time.Duration;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.util.ArrayList;
import java.util.List;


// TODO: make sure this is thread-safe
public class EventDB {
  private static final String TAG = "EventDB";
  private List<Event> allEvents;
  private static EventDB instance = new EventDB();

  private EventDB() {
    allEvents = new ArrayList<Event>();
  }

  public static EventDB getInstance() {
    return instance;
  }

  public synchronized void addEvent(Event e) {
    Log.e(TAG, "add: " + e.toString());
    allEvents.add(e);
  }

  public synchronized void printAllEvents() {
    for (Event e : allEvents) {
      Log.e(TAG, "event:" + e.toString());
    }
  }

  public final static class TodayEventsPredicate implements Predicate<Event> {
    public boolean apply(Event event) {
      return isTodayEvent(new LocalDate(), event.getDoDate());
    }

    @VisibleForTesting
    boolean isTodayEvent(LocalDate today, LocalDate eventDoDate) {
      if (eventDoDate == null) {
        return false;
      }
      return Days.daysBetween(today, eventDoDate) == Days.ZERO;
    }
  }

  public final static int NOW_WITHIN_HOURS = 4;

  public final static class NowEventsPredicate implements Predicate<Event> {
    public boolean apply(Event event) {
      return isNowEvent(new LocalTime(), event.getStartTime());
    }

    @VisibleForTesting
    boolean isNowEvent(LocalTime currentTime, LocalTime eventStartTime) {
      Duration duration = new Duration(currentTime.toDateTimeToday(), eventStartTime.toDateTimeToday());
      if (duration.getStandardDays() != 0) {
        throw new java.lang.AssertionError("Not same day?");
      }
      return duration.getStandardHours() >= 0 && duration.getStandardHours() <= NOW_WITHIN_HOURS;
    }
  }

  public final static class UpcomingEventsPredicate implements Predicate<Event> {
    public boolean apply(Event event) {
      NowEventsPredicate nowEventsPredicate = new NowEventsPredicate();
      return !nowEventsPredicate.apply(event);
    }
  }

  public synchronized List<Event> getTodayEvents() {
    List<Event> todayEvents = new ArrayList<Event>();
    Predicate<Event> predicate = new TodayEventsPredicate();
    for (Event e : allEvents) {
      if (predicate.apply(e)) {
        todayEvents.add(e);
      }
    }
    return ImmutableList.copyOf(todayEvents);
  }

  public static List<Event> getNowEvents(List<Event> todayEvents) {
    List<Event> nowEvents = new ArrayList<Event>();
    Predicate<Event> predicate = new NowEventsPredicate();
    for (Event e : todayEvents) {
      if (predicate.apply(e)) {
        nowEvents.add(e);
      }
    }
    return ImmutableList.copyOf(nowEvents);
  }

  public static List<Event> getUpcomingEvents(List<Event> todayEvents) {
    List<Event> upcomingEvents = new ArrayList<Event>();
    Predicate<Event> predicate = new UpcomingEventsPredicate();
    for (Event e : todayEvents) {
      if (predicate.apply(e)) {
        upcomingEvents.add(e);
      }
    }
    return ImmutableList.copyOf(upcomingEvents);
  }
}
