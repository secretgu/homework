package siyugu.homework.event;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.google.common.annotations.VisibleForTesting;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.Period;

import java.util.Objects;

import siyugu.homework.util.TimeUtil;

/**
 * Event is immutable. Use Event#toBuilder to get a Builder object.
 */
public class Event implements Parcelable {
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

  /*================Getter and Setter (begin)================*/

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

  /*================Getter and Setter (end)================*/

  /*================Equals (begin)================*/
  @Override
  public int hashCode() {
    return Objects.hash(typeOfWork,
        description,
        dueDate,
        doDate,
        picturePath,
        startTime,
        permittedTime,
        warningTime,
        repeatPattern,
        completed);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (obj instanceof Event == false) {
      return false;
    }
    Event rhs = (Event) obj;
    return Objects.equals(typeOfWork, rhs.typeOfWork)
        && Objects.equals(description, rhs.description)
        && Objects.equals(dueDate, rhs.dueDate)
        && Objects.equals(doDate, rhs.doDate)
        && Objects.equals(picturePath, rhs.picturePath)
        && Objects.equals(startTime, rhs.startTime)
        && Objects.equals(permittedTime, rhs.permittedTime)
        && Objects.equals(warningTime, rhs.warningTime)
        && Objects.equals(repeatPattern, rhs.repeatPattern)
        && Objects.equals(completed, rhs.completed);
  }

  /*================Equals (end)================*/

  /*================Parcelable (begin)================*/
  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeSerializable(typeOfWork);
    dest.writeString(description);
    dest.writeSerializable(dueDate);
    dest.writeSerializable(doDate);
    dest.writeString(picturePath);
    dest.writeSerializable(startTime);
    dest.writeSerializable(permittedTime);
    dest.writeSerializable(warningTime);
    dest.writeSerializable(repeatPattern);
    dest.writeInt(completed ? 1 : 0);
  }

  public static final Parcelable.Creator<Event> CREATOR
      = new Parcelable.Creator<Event>() {
    public Event createFromParcel(Parcel in) {
      return new Event(in);
    }

    public Event[] newArray(int size) {
      return new Event[size];
    }
  };

  // TODO: add an Android Instrumented Tests for this
  private Event(Parcel in) {
    this((TypeOfWork) in.readSerializable(),
        in.readString(),
        (LocalDate) in.readSerializable(),
        (LocalDate) in.readSerializable(),
        in.readString(),
        (LocalTime) in.readSerializable(),
        (Period) in.readSerializable(),
        (WarningTime) in.readSerializable(),
        (RepeatPattern) in.readSerializable(),
        in.readInt() > 0);
  }

  /*================Parcelable (end)================*/

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
    this.permittedTime = new Period(permittedHour,
        permittedMinute,
        0 /* seconds */,
        0 /* millis */);
    this.warningTime = warningTime;
    this.repeatPattern = repeatPattern;
    this.completed = false;
  }

  // used by Builder
  private Event(
      TypeOfWork typeOfWork,
      String description,
      LocalDate dueDate,
      LocalDate doDate,
      String picturePath,
      LocalTime startTime,
      Period permittedTime,
      WarningTime warningTime,
      RepeatPattern repeatPattern,
      boolean completed
  ) {
    this.typeOfWork = typeOfWork;
    this.description = description;
    this.dueDate = dueDate;
    this.doDate = doDate;
    this.picturePath = picturePath;
    this.startTime = startTime;
    this.permittedTime = permittedTime;
    this.warningTime = warningTime;
    this.repeatPattern = repeatPattern;
    this.completed = completed;
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
    this.permittedTime = new Period(permittedHour,
        permittedMinute,
        0 /* seconds */,
        0 /* millis */);
    this.completed = false;
  }

  @Override
  public String toString() {
    return description;
  }

  public Builder toBuilder() {
    return new Builder().setTypeOfWork(typeOfWork).setDescription(description).setDueDate(dueDate)
        .setDoDate(doDate).setPicturePath(picturePath).setPermittedTime(permittedTime)
        .setWarningTime(warningTime).setRepeatPattern(repeatPattern).setCompleted(completed);
  }

  public static final class Builder {
    private TypeOfWork typeOfWork;
    private String description;
    private LocalDate dueDate;
    private LocalDate doDate;
    private String picturePath;
    private LocalTime startTime;
    private Period permittedTime;
    private WarningTime warningTime;
    private RepeatPattern repeatPattern;
    private boolean completed;

    private Builder() {
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

    public Builder setDoDate(LocalDate doDate) {
      this.doDate = doDate;
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

    public Builder setPermittedTime(Period permittedTime) {
      this.permittedTime = permittedTime;
      return this;
    }

    public Builder setWarningTime(WarningTime warningTime) {
      this.warningTime = warningTime;
      return this;
    }

    public Builder setRepeatPattern(RepeatPattern repeatPattern) {
      this.repeatPattern = repeatPattern;
      return this;
    }

    public Builder setCompleted(boolean completed) {
      this.completed = completed;
      return this;
    }

    public Event build() {
      return new Event(typeOfWork,
          description,
          dueDate,
          doDate,
          picturePath,
          startTime,
          permittedTime,
          warningTime,
          repeatPattern,
          completed);
    }
  }
}
