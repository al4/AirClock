package nz.al4.airclock;

import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.joda.time.DateTimeUtils.setCurrentMillisFixed;

/**
 * Created by alex on 07/01/2017.
 */



@RunWith(PowerMockRunner.class)
@PrepareForTest({Log.class})
public class TimeCalculatorTest {

    public TimeCalculator TimeCalculator4h;
    public TimeCalculator TimeCalculator10h;
    public TimeCalculator TimeCalculator12h;


    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(Log.class);
        // 4h offset
        TimeCalculator4h = new TimeCalculator(
                new DateTime(2017, 1, 1, 0, 0, DateTimeZone.forOffsetHours(-10)),
                new DateTime(2017, 1, 2, 0, 0, DateTimeZone.forOffsetHours(10))
        );

        // 12h offset, 24h flight
        TimeCalculator12h = new TimeCalculator(
                new LocalDateTime(2017, 1, 1, 0, 0).toDateTime(DateTimeZone.UTC),
                new LocalDateTime(2017, 1, 2, 12, 0).toDateTime(DateTimeZone.forOffsetHours(12))
        );

        // 10h offset
        TimeCalculator10h = new TimeCalculator(
                new DateTime(2017, 1, 1, 0, 0, DateTimeZone.forOffsetHours(-10)),
                new DateTime(2017, 1, 2, 0, 0, DateTimeZone.forOffsetHours(4))
        );
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getFlightLength_returns_24_for_24h_flight()
            throws Exception, AirClockSpaceTimeException {
        assertThat("flight length should be 24 hours",
            this.TimeCalculator12h.getFlightLength(),
            equalTo(new Float(24 * 60 * 60 * 1000))
        );
    }

    @Test(expected = AirClockSpaceTimeException.class)
    public void getFlightLength_throws_exception_when_land_before_depart()
            throws Exception, AirClockSpaceTimeException {
        TimeCalculator lTimeCalculator = new TimeCalculator(
                new DateTime(2017, 1, 2, 0, 0, DateTimeZone.UTC),
                new DateTime(2016, 1, 1, 0, 0, DateTimeZone.UTC)
        );
        lTimeCalculator.getFlightLength();
    }

//    @Test
//    public void getElapsed() throws Exception {
//        System.out.println(mTimeCalculator.getElapsed(true));
//    }

    @Test
    public void getTotalTimeShift_12h_diff() throws Exception, AirClockException {
        assertThat("time shift should be 720 minutes",
                TimeCalculator12h.getTotalTimeShift(),
                equalTo(new Float(720))
        );
    }

    @Test
    public void getTotalTimeShift_with_4h_reverse_diff() throws Exception, AirClockException {
        assertThat("time shift should be -240 minutes",
                TimeCalculator4h.getTotalTimeShift(),
                equalTo(new Float(-240))
        );
    }

    @Test
    public void getTotalTimeShift_with_10h_diff() throws Exception, AirClockException {
        assertThat("time shift should be 600 minutes",
                TimeCalculator10h.getTotalTimeShift(),
                equalTo(new Float(-600))
        );
    }

    @Test
    public void getEffectiveOffset_with_4h_at_start() throws Exception, AirClockException {
        System.out.println(String.valueOf(TimeCalculator4h.getEffectiveOffset()));
        setCurrentMillisFixed(
                new DateTime(2017, 1, 1, 0, 0, DateTimeZone.forOffsetHours(-10)).getMillis()
        );
        assertThat("time zone is same as origin at take off time",
            TimeCalculator4h.getEffectiveOffset(),
            equalTo("GMT-1000")
        );
    }

    @Test
    public void getEffectiveOffset_at_origin() throws Exception, AirClockException {
        setCurrentMillisFixed(
                new DateTime(2017, 1, 1, 0, 0, DateTimeZone.forOffsetHours(-10)).getMillis()
        );

        TimeCalculator tc = new TimeCalculator(
                new DateTime(2017, 1, 1, 0, 0, DateTimeZone.forOffsetHours(-10)),
                new DateTime(2017, 1, 2, 0, 0, DateTimeZone.forOffsetHours(10))
        );

        assertThat("time zone is same as origin at take off time",
                tc.getEffectiveOffset(),
                equalTo("GMT-1000")
        );
    }

    @Test
    public void getEffectiveOffset_at_destination() throws Exception, AirClockException {
        setCurrentMillisFixed(
                new DateTime(2017, 1, 2, 0, 0, DateTimeZone.forOffsetHours(10)).getMillis()
        );

        TimeCalculator tc = new TimeCalculator(
                new DateTime(2017, 1, 1, 0, 0, DateTimeZone.forOffsetHours(-10)),
                new DateTime(2017, 1, 2, 0, 0, DateTimeZone.forOffsetHours(10))
        );

        assertThat("time zone is same as destination at landing time",
                tc.getEffectiveOffset(),
                equalTo("GMT+1000")
        );
    }

}