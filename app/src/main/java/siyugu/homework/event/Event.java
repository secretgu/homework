package siyugu.homework.event;

import android.support.annotation.VisibleForTesting;

import java.util.Date;

/**
 * Created by siyugu on 1/17/16.
 */
public class Event {
  private String value;

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

  public Event(
      TypeOfWork typeOfWork,
      String description,
      Date dueDate,
      Date doDate,
      String picturePath
  ) {
  }

  @VisibleForTesting
  public Event(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return value;
  }
}
