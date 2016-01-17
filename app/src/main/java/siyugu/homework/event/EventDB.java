package siyugu.homework.event;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by siyugu on 1/17/16.
 */
// TODO: make sure this is thread-safe
public class EventDB {
  private static final String TAG = "EventDB";
  private List<Event> allEvents;
  private static EventDB instance;

  private EventDB() {
    allEvents = new ArrayList<Event>();
  }

  public static synchronized EventDB getInstance() {
    if (instance == null) {
      instance = new EventDB();
    }
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
}
