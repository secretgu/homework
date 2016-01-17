package siyugu.homework;

public class MonthUtil {
  // java month starts from 0, joda starts from 1

  public static int jodaMonthToJavaMonth(int jodaMonth) {
    return jodaMonth - 1;
  }

  public static int javaMonthToJodaMonth(int javaMonth) {
    return javaMonth + 1;
  }
}
