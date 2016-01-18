package siyugu.homework.event;

import android.support.annotation.Nullable;

import com.google.common.annotations.VisibleForTesting;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.Period;

import siyugu.homework.util.TimeUtil;

public class Event {
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
  @Nullable
  private WarningTime warningTime;
  @Nullable
  private RepeatPattern repeatPattern;

  private boolean completed;

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

  public enum RepeatPattern {
    DAILY("daily"),
    WEEKDAY("weekday"),
    WEEKEND("weekend");

    RepeatPattern(String text) {
      this.text = text;
    }

    @Override
    public String toString() {
      return text;
    }

    private String text;
  }

  public enum WarningTime {
    MINUTE_15("15 minutes"),
    MINUTE_30("30 minutes"),
    HOUR_1("1 hour"),
    HOUR_2("2 hours");

    WarningTime(String text) {
      this.text = text;
    }

    @Override
    public String toString() {
      return text;
    }

    private String text;
  }

  /*================Getter and Setter (start)================*/

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

  public RepeatPattern getRepeatPattern() {
    return repeatPattern;
  }

  public boolean getCompleted() {
    return completed;
  }

  public void setCompleted(boolean completed) {
    this.completed = completed;
  }

  /*================Getter and Setter (end)================*/

  // TODO: make sure fields have appropriate default values
  public Event(
      TypeOfWork typeOfWork,
      String description,
      String dueDate,
      String doDate,
      String picturePath,
      String startTime,
      int permittedHour,
      int permittedMinute,
      WarningTime warningTime,
      RepeatPattern repeatPattern
  ) {
    this.typeOfWork = typeOfWork;
    this.description = description;
    // TODO: exception handling
    this.dueDate = TimeUtil.LOCALDATE_FORMATTER.parseLocalDate(dueDate);
    this.doDate = TimeUtil.LOCALDATE_FORMATTER.parseLocalDate(doDate);
    this.picturePath = picturePath;
    this.startTime = TimeUtil.LOCALTIME_FORMATTER.parseLocalTime(startTime);
    this.permittedTime = new Period(permittedHour, permittedMinute, 0 /* seconds */, 0 /* millis */);
    this.warningTime = warningTime;
    this.repeatPattern = repeatPattern;
    this.completed = false;
  }

  @VisibleForTesting
  public Event(
      TypeOfWork typeOfWork,
      String description,
      String doDate,
      String startTime,
      int permittedHour,
      int permittedMinute) {
    this.typeOfWork = typeOfWork;
    this.description = description;
    this.doDate = TimeUtil.LOCALDATE_FORMATTER.parseLocalDate(doDate);
    this.startTime = TimeUtil.LOCALTIME_FORMATTER.parseLocalTime(startTime);
    this.permittedTime = new Period(permittedHour, permittedMinute, 0 /* seconds */, 0 /* millis */);
    this.completed = false;
  }

  @Override
  public String toString() {
    return description;
  }
}
