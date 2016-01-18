package siyugu.homework.event;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class ExampleUnitTest {
  @Test
  public void addition_isCorrect() throws Exception {
    assertEquals(4, 2 + 2);
  }

  @Test
  public void testNowEventsPredicate() throws Exception {
    EventDB.NowEventsPredicate predicate = new EventDB.NowEventsPredicate();
    LocalTime currentTime = new LocalTime();

    assertFalse(predicate.isNowEvent(currentTime, currentTime.minusHours(1)));
    assertFalse(predicate.isNowEvent(currentTime, currentTime.minusHours(2)));
    assertTrue(predicate.isNowEvent(currentTime, currentTime.minusHours(0)));
    assertTrue(predicate.isNowEvent(currentTime, currentTime.plusHours(0)));
    assertTrue(predicate.isNowEvent(currentTime, currentTime.plusHours(2)));
    assertFalse(predicate.isNowEvent(currentTime, currentTime.plusHours(4)));
    assertFalse(predicate.isNowEvent(currentTime, currentTime.plusHours(5)));
  }

  @Test
  public void testTodayEventsPredicate() throws Exception {
    EventDB.TodayEventsPredicate predicate = new EventDB.TodayEventsPredicate();
    LocalDate today = new LocalDate();

    assertTrue(predicate.isTodayEvent(today, today.minusDays(0)));
    assertFalse(predicate.isTodayEvent(today, today.minusDays(1)));
    assertFalse(predicate.isTodayEvent(today, today.plusDays(1)));
  }
}