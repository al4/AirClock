package nz.al4.airclock;

import android.util.Log;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.joda.time.DateTimeUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * Created by alex on 07/01/2017.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({Log.class})
public class TimeCalculatorTest {
    // 24 hour flight
    DateTime mOriginTime = new LocalDateTime(2017, 1, 1, 0, 0).toDateTime(
        DateTimeZone.UTC
    );
    DateTime mDestTime = new LocalDateTime(2017, 1, 2, 12, 0).toDateTime(
        DateTimeZone.forOffsetHours(12)
    );

    TimeCalculator mTimeCalculator;

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(Log.class);
        mTimeCalculator = new TimeCalculator(mOriginTime, mDestTime);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getFlightLength_returns_24_for_24h_flight() throws Exception {
        assertThat("flight length should be 24 hours",
            mTimeCalculator.getFlightLength(true),
            equalTo(new Float(24))
        );
    }

    @Test(expected = SpaceTimeException.class)
    public void TimeCalculator_throws_exception_when_land_before_depart() throws Exception {
        TimeCalculator lTimeCalculator = new TimeCalculator(
                new DateTime(2017, 1, 2, 0, 0, DateTimeZone.UTC),
                new DateTime(2017, 1, 1, 0, 0, DateTimeZone.UTC)
        );
    }

//    @Test
//    public void getElapsed() throws Exception {
//        System.out.println(mTimeCalculator.getElapsed(true));
//    }

//    @Test
//    public void getTotalTimeShift() throws Exception {
//
//    }
//
//    @Test
//    public void getEffectiveOffset() throws Exception {
//
//    }

}