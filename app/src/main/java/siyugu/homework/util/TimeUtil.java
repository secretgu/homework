package siyugu.homework.util;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class TimeUtil {
  public static final DateTimeFormatter LOCALDATE_FORMATTER = DateTimeFormat.forPattern("yyyy/MM/dd");
  public static final DateTimeFormatter LOCALTIME_FORMATTER = DateTimeFormat.forPattern("hh:mm a");
  public static final String DATETIME_DEBUG_PATTERN = "yyyy/MM/dd hh:mm a";

  // java month starts from 0, joda starts from 1
  public static int jodaMonthToJavaMonth(int jodaMonth) {
    return jodaMonth - 1;
  }

  public static int javaMonthToJodaMonth(int javaMonth) {
    return javaMonth + 1;
  }
}
