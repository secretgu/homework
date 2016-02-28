package siyugu.plant.event;

import org.joda.time.Duration;
import org.joda.time.LocalTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import siyugu.plant.util.TimeUtil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class ExampleUnitTest {

  @Test
  public void testNowEventsPredicate() throws Exception {
    LocalTime onePm = TimeUtil.LOCALTIME_FORMATTER.parseLocalTime("01:00 PM");
    LocalTime plus1ms = onePm.plusMillis(1);
    LocalTime minus1ms = onePm.minusMillis(1);
    Duration d = new Duration(onePm.toDateTimeToday(), plus1ms.toDateTimeToday());
    Duration dm = new Duration(onePm.toDateTimeToday(), minus1ms.toDateTimeToday());
    System.out.println(d.getMillis());
    System.out.println(d.getStandardMinutes());
    System.out.println(dm.getMillis());
    System.out.println(dm.getStandardMinutes());
  }
}