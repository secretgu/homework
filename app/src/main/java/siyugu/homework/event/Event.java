package siyugu.homework.event;

import android.support.annotation.Nullable;

import com.google.common.base.Strings;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.Period;

import java.io.Serializable;
import java.util.Comparator;

import siyugu.homework.BuildConfig;
import siyugu.homework.util.TimeUtil;

/**
 * Event is immutable. Use Event#toBuilder to get a Builder object.
 */
public class Event implements Serializable {
  private String title;
  private TypeOfWork typeOfWork;
  @Nullable
  private String description;
  @Nullable
  private LocalDate dueDate;
  private LocalDate doDate;
  @Nullable
  private String picturePath;
  private LocalTime startTime;
  private Period permittedTime;
  private WarningTime warningTime;
  private boolean completed;

  private long id;  // unique id in entire EventDB

  private static long lastId = 0;

  static void setLastId(long id) {
    lastId = id;
  }

  static long peekLastId() {
    return lastId;
  }

  private static long getLastId() {
    return lastId++;
  }

  public enum TypeOfWork {
    CLUB("club"),
    MEETING("meeting"),
    HOMEWORK("homework"),
    PROJECT("project"),
    CLASS("class"),
    PERSONAL("personal");

    TypeOfWork(String text) {
      this.text = text;
    }

    @Override
    public String toString() {
      return text;
    }

    private String text;
  }

  public enum WarningTime {
    MINUTE_1("1 minute", 1),
    MINUTE_15("15 minutes", 15),
    MINUTE_30("30 minutes", 30),
    HOUR_1("1 hour", 60),
    HOUR_2("2 hours", 120);

    WarningTime(String text, int minute) {
      this.text = text;
      this.minute = minute;
    }

    @Override
    public String toString() {
      return text;
    }

    public int getMinute() {
      return minute;
    }

    private String text;
    private int minute;
  }

  /*================Getter and Setter (begin)================*/
  public String getTitle() {
    return title;
  }

  public long getId() {
    return id;
  }

  public TypeOfWork getTypeOfWork() {
    return typeOfWork;
  }

  public String getDescription() {
    return description;
  }

  public LocalDate getDueDate() {
    return dueDate;
  }

  public LocalDate getDoDate() {
    return doDate;
  }

  public String getPicturePath() {
    return picturePath;
  }

  public LocalTime getStartTime() {
    return startTime;
  }

  public Period getPermittedTime() {
    return permittedTime;
  }

  public WarningTime getWarningTime() {
    return warningTime;
  }

  public boolean getCompleted() {
    return completed;
  }

  /*================Getter and Setter (end)================*/

  public Event(
      String title,
      TypeOfWork typeOfWork,
      String description,
      String dueDate,
      String doDate,
      String picturePath,
      String startTime,
      int permittedHour,
      int permittedMinute,
      WarningTime warningTime
  ) {
    this.title = title;
    this.typeOfWork = typeOfWork;
    this.description = description;
    if (!Strings.isNullOrEmpty(dueDate)) {
      this.dueDate = TimeUtil.LOCALDATE_FORMATTER.parseLocalDate(dueDate);
    }
    this.doDate = TimeUtil.LOCALDATE_FORMATTER.parseLocalDate(doDate);
    this.picturePath = picturePath;
    this.startTime = TimeUtil.LOCALTIME_FORMATTER.parseLocalTime(startTime);
    this.permittedTime = new Period(permittedHour,
        permittedMinute,
        0 /* seconds */,
        0 /* millis */);
    this.warningTime = warningTime;
    this.completed = false;

    this.id = getLastId();
  }

  public void copyFrom(Event e) {
    if (BuildConfig.DEBUG) {
      if (this.id != e.id) {
        throw new AssertionError("copy from an event that is not itself");
      }
    }
    this.title = e.title;
    this.description = e.description;
    this.dueDate = e.dueDate;
    this.doDate = e.doDate;
    this.picturePath = e.picturePath;
    this.startTime = e.startTime;
    this.permittedTime = e.permittedTime;
    this.warningTime = e.warningTime;
    this.completed = e.completed;
  }

  // used by Builder
  private Event(
      long id,
      String title,
      TypeOfWork typeOfWork,
      String description,
      LocalDate dueDate,
      LocalDate doDate,
      String picturePath,
      LocalTime startTime,
      Period permittedTime,
      WarningTime warningTime,
      boolean completed
  ) {
    this.id = id;
    this.title = title;
    this.typeOfWork = typeOfWork;
    this.description = description;
    this.dueDate = dueDate;
    this.doDate = doDate;
    this.picturePath = picturePath;
    this.startTime = startTime;
    this.permittedTime = permittedTime;
    this.warningTime = warningTime;
    this.completed = completed;
  }

  public Builder toBuilder() {
    return new Builder(id).setTitle(title).setTypeOfWork(typeOfWork).setDescription(description)
        .setDueDate(dueDate)
        .setDoDate(doDate).setStartTime(startTime).setPicturePath(picturePath)
        .setPermittedTime(permittedTime)
        .setWarningTime(warningTime).setCompleted(completed);
  }

  public static class StartTimeComparator implements Comparator<Event> {
    @Override
    public int compare(Event lhs, Event rhs) {
      return lhs.startTime.isBefore(rhs.startTime) ? -1 : 1;
    }
  }

  public static class Builder {
    private long id;
    private String title;
    private TypeOfWork typeOfWork;
    private String description;
    private LocalDate dueDate;
    private LocalDate doDate;
    private String picturePath;
    private LocalTime startTime;
    private Period permittedTime;
    private WarningTime warningTime;
    private boolean completed;

    private Builder(long id) {
      this.id = id;
    }

    public Builder setTitle(String title) {
      this.title = title;
      return this;
    }

    public Builder setTypeOfWork(TypeOfWork typeOfWork) {
      this.typeOfWork = typeOfWork;
      return this;
    }

    public Builder setDescription(String description) {
      this.description = description;
      return this;
    }

    public Builder setDueDate(LocalDate dueDate) {
      this.dueDate = dueDate;
      return this;
    }

    public Builder setDueDate(String dueDateText) {
      if (!Strings.isNullOrEmpty(dueDateText)) {
        this.dueDate = TimeUtil.LOCALDATE_FORMATTER.parseLocalDate(dueDateText);
      }
      return this;
    }

    public Builder setDoDate(LocalDate doDate) {
      this.doDate = doDate;
      return this;
    }

    public Builder setDoDate(String doDateText) {
      this.doDate = TimeUtil.LOCALDATE_FORMATTER.parseLocalDate(doDateText);
      return this;
    }

    public Builder setPicturePath(String picturePath) {
      this.picturePath = picturePath;
      return this;
    }

    public Builder setStartTime(LocalTime startTime) {
      this.startTime = startTime;
      return this;
    }

    public Builder setStartTime(String startTimeText) {
      this.startTime = TimeUtil.LOCALTIME_FORMATTER.parseLocalTime(startTimeText);
      return this;
    }

    public Builder setPermittedTime(Period permittedTime) {
      this.permittedTime = permittedTime;
      return this;
    }

    public Builder setPermittedTime(int hour, int minute) {
      this.permittedTime = new Period(hour, minute, 0, 0);
      return this;
    }

    public Builder setWarningTime(WarningTime warningTime) {
      this.warningTime = warningTime;
      return this;
    }

    public Builder setCompleted(boolean completed) {
      this.completed = completed;
      return this;
    }

    public Event build() {
      return new Event(
          id,
          title,
          typeOfWork,
          description,
          dueDate,
          doDate,
          picturePath,
          startTime,
          permittedTime,
          warningTime,
          completed);
    }
  }
}
