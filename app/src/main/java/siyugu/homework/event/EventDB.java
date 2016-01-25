package siyugu.homework.event;

import android.util.Log;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;

import org.joda.time.Days;
import org.joda.time.Duration;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class EventDB {
  private static final String TAG = "EventDB";
  private ArrayList<Event> allEvents;
  private transient File backupFile;  // not to be serialized

  public EventDB(File backupFile) throws Exception {
    this.backupFile = backupFile;
    load();
  }

  private void load() throws IOException, ClassNotFoundException {
    if (backupFile.exists()) {
      ObjectInputStream in = new ObjectInputStream(new FileInputStream(backupFile));
      allEvents = (ArrayList<Event>) in.readObject();
      in.close();
      Log.i(TAG, "Deserialized data from " + backupFile.getAbsolutePath());
    } else {
      allEvents = new ArrayList<Event>();
    }
  }

  public void addEvent(Event e) {
    Log.i(TAG, "add: " + e.toString());
    allEvents.add(e);
  }

  public void flush() throws IOException {
    ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(backupFile));
    oos.writeObject(allEvents);
    oos.flush();
    oos.close();
    Log.i(TAG, "Serialized data is saved in " + backupFile.getAbsolutePath());
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
      Duration duration = new Duration(currentTime.toDateTimeToday(),
          eventStartTime.toDateTimeToday());
      if (duration.getStandardDays() != 0) {
        throw new java.lang.AssertionError("Not same day?");
      }
      return duration.getStandardHours() >= 0 && duration.getStandardHours() < NOW_WITHIN_HOURS;
    }
  }

  public final static class UpcomingEventsPredicate implements Predicate<Event> {
    public boolean apply(Event event) {
      return isUpcomingEvent(new LocalTime(), event.getStartTime());
    }

    @VisibleForTesting
    boolean isUpcomingEvent(LocalTime currentTime, LocalTime eventStartTime) {
      Duration duration = new Duration(currentTime.toDateTimeToday(),
          eventStartTime.toDateTimeToday());
      if (duration.getStandardDays() != 0) {
        throw new java.lang.AssertionError("Not same day?");
      }
      return duration.getStandardHours() >= NOW_WITHIN_HOURS;
    }
  }

  public final static class IncompleteEventsPredicate implements Predicate<Event> {
    public boolean apply(Event event) {
      return !event.getCompleted();
    }
  }

  // TODO: make the API follow flow pattern. Consider using
  // com.google.common.collect.Lists#transform.
  // Example: java/com/google/geo/sidekick/frontend/controller/EntryQueryController.java
  private List<Event> getTodayEvents() {
    List<Event> todayEvents = new ArrayList<Event>();
    Predicate<Event> predicate = new TodayEventsPredicate();
    for (Event e : allEvents) {
      if (predicate.apply(e)) {
        todayEvents.add(e);
      }
    }
    return ImmutableList.copyOf(todayEvents);
  }

  public List<Event> getNowEvents() {
    List<Event> todayEvents = getTodayEvents();
    List<Event> nowEvents = new ArrayList<Event>();
    Predicate<Event> nowPredicate = new NowEventsPredicate();
    for (Event e : todayEvents) {
      if (nowPredicate.apply(e)) {
        nowEvents.add(e);
      }
    }
    return nowEvents;
  }

  public List<Event> getUpcomingEvents() {
    List<Event> todayEvents = getTodayEvents();
    List<Event> upcomingEvents = new ArrayList<Event>();
    Predicate<Event> upComingPredicate = new UpcomingEventsPredicate();
    for (Event e : todayEvents) {
      if (upComingPredicate.apply(e)) {
        upcomingEvents.add(e);
      }
    }
    return upcomingEvents;
  }
}
