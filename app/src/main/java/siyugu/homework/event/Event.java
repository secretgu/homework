package siyugu.homework.event;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.Period;

import siyugu.homework.util.TimeUtil;

public class Event {
  private TypeOfWork typeOfWork;
  private String description;
  private LocalDate dueDate;
  private LocalDate doDate;
  private String picturePath;
  private LocalTime startTime;
  private Period permittedTime;
  private WarningTime warningTime;
  private RepeatPattern repeatPattern;

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
    this.dueDate = TimeUtil.LOCALDATE_FORMATTER.parseLocalDate(dueDate);
    this.doDate = TimeUtil.LOCALDATE_FORMATTER.parseLocalDate(doDate);
    this.picturePath = picturePath;
    this.startTime = TimeUtil.LOCALTIME_FORMATTER.parseLocalTime(startTime);
    this.permittedTime = new Period(permittedHour, permittedMinute, 0 /* seconds */, 0 /* millis */);
    this.warningTime = warningTime;
    this.repeatPattern = repeatPattern;
  }

  @Override
  public String toString() {
    return "";
  }
}
