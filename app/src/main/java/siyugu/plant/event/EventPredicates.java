package siyugu.plant.event;

import com.google.common.base.Predicate;

import org.joda.time.Days;
import org.joda.time.Duration;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

public class EventPredicates {
  public final static class TodayEventsPredicate implements Predicate<Event> {
    private LocalDate date;

    public TodayEventsPredicate(LocalDate date) {
      this.date = date;
    }

    public boolean apply(Event event) {
      if (event.getDoDate() == null) {
        return false;
      }
      return Days.daysBetween(date, event.getDoDate()) == Days.ZERO;
    }
  }

  // NowEventsPredicate and UpcomingEventsPredicate are mutual exclusive because
  // to be considered as NowEvent:
  //    eventStartTime <= currentTime [guaranteed by interval.contains()]
  // to be considered as UpcomingEvent:
  //    currentTime < eventStartTime [guaranteed by duration.getMillis() > 0]
  //
  // The goal is to make sure mutual exclusiveness instead of absolute precise of where an event
  // should go at a certain time.
  public final static class NowEventsPredicate implements Predicate<Event> {
    private final LocalTime currentTime;
    private final int EXTRA_MINUTES_TO_STICK_AROUND = 30;

    public NowEventsPredicate(LocalTime currentTime) {
      this.currentTime = currentTime;
    }

    public boolean apply(Event event) {
      LocalTime eventStartTime = event.getStartTime();
      Interval interval = new Interval(eventStartTime.toDateTimeToday(),
          eventStartTime.toDateTimeToday().plusMinutes(event.getPermittedTime().toStandardMinutes()
              .getMinutes() + EXTRA_MINUTES_TO_STICK_AROUND));
      return interval.contains(currentTime.toDateTimeToday());
    }
  }

  public final static class UpcomingEventsPredicate implements Predicate<Event> {
    private final LocalTime currentTime;

    public UpcomingEventsPredicate(LocalTime currentTime) {
      this.currentTime = currentTime;
    }

    public boolean apply(Event event) {
      Duration duration = new Duration(
          currentTime.toDateTimeToday(),
          event.getStartTime().toDateTimeToday());
      return duration.getMillis() > 0 && duration.getStandardMinutes() >= 0;
    }
  }

  public final static class IncompleteEventsPredicate implements Predicate<Event> {
    public boolean apply(Event event) {
      return !event.getCompleted();
    }
  }
}
