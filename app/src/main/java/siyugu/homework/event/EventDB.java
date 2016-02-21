package siyugu.homework.event;

import android.util.Log;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;

import org.joda.time.LocalDate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import siyugu.homework.BuildConfig;

public class EventDB {
  private static final String TAG = "EventDB";
  private ArrayList<Event> allEvents;
  private transient File backupFile;  // not to be serialized
  private boolean flushed = false;

  public EventDB(File backupFile) throws Exception {
    this.backupFile = backupFile;
    try {
      load();
    } catch (Exception e) {
      if (BuildConfig.DEBUG) {
        e.printStackTrace();
        // In debug build, just delete the backupFile
        Log.e(TAG, "delete file: " + backupFile.getAbsolutePath());
        backupFile.delete();
      } else {
        throw e;
      }
    }
  }

  private void load() throws IOException, ClassNotFoundException {
    if (backupFile.exists()) {
      ObjectInputStream in = new ObjectInputStream(new FileInputStream(backupFile));
      allEvents = (ArrayList<Event>) in.readObject();
      long lastId = in.readLong();
      // Invariant: lastId == max(Event#id in allEvents) or 0 if allEvents is empty
      Event.setLastId(lastId);
      in.close();
      Log.i(TAG, "Deserialized data from " + backupFile.getAbsolutePath());
    } else {
      allEvents = new ArrayList<Event>();
    }
    flushed = true;  // nothing new
  }

  public void addEvent(Event e) {
    flushed = false;
    boolean found = false;
    for (Event event : allEvents) {
      if (event.getId() == e.getId()) {
        Log.i(TAG, "replace existing event " + e.getId());
        event.copyFrom(e);
        found = true;
        break;
      }
    }
    if (!found) {
      Log.i(TAG, "add new event " + e.getId() + ": " + e.getDescription());
      allEvents.add(e);
    }
  }

  public void flush() throws IOException {
    if (!flushed) {
      ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(backupFile));
      oos.writeObject(allEvents);
      oos.writeLong(Event.peekLastId());
      oos.flush();
      oos.close();
      Log.i(TAG, "Serialized data is saved in " + backupFile.getAbsolutePath());
      flushed = true;
    } else {
      Log.i(TAG, "Nothing new to persist");
    }
  }

  public List<Event> getEventsOfDate(LocalDate date) {
    List<Event> todayEvents = new ArrayList<Event>();
    Predicate<Event> predicate = new EventPredicates.TodayEventsPredicate(date);
    for (Event e : allEvents) {
      if (predicate.apply(e)) {
        todayEvents.add(e);
      }
    }
    return todayEvents;
  }

  public List<Event> getAllEvents() {
    return ImmutableList.copyOf(allEvents);
  }
}
