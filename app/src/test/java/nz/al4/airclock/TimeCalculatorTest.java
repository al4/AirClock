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

    private TimeCalculator TcHonoluluBrisbane;
    private TimeCalculator TcHonoluluIslamabad;
    private TimeCalculator TcLondonAuckland;
    private TimeCalculator TcPerthLondon;
    private TimeCalculator TcAmsterdamNewYork;
    private TimeCalculator TcImpossible;
    private TimeCalculator TcSanFranWashington;

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(Log.class);
        // Honolulu to Brisbane, 4h offset
        TcHonoluluBrisbane = new TimeCalculator(
                new DateTime(2017, 1, 1, 0, 0, DateTimeZone.forOffsetHours(-10)),
                new DateTime(2017, 1, 2, 0, 0, DateTimeZone.forOffsetHours(10))
        );

        // London to Auckland, 12h offset, 24h flight
        TcLondonAuckland = new TimeCalculator(
                new LocalDateTime(2017, 1, 1, 0, 0).toDateTime(DateTimeZone.UTC),
                new LocalDateTime(2017, 1, 2, 12, 0).toDateTime(DateTimeZone.forOffsetHours(12))
        );

        // Honolulu to Islamabad 10h offset
        TcHonoluluIslamabad = new TimeCalculator(
                new DateTime(2017, 1, 1, 0, 0, DateTimeZone.forOffsetHours(-10)),
                new DateTime(2017, 1, 2, 0, 0, DateTimeZone.forOffsetHours(4))
        );

        // Perth to London 20h flight
        TcPerthLondon = new TimeCalculator(
                new DateTime(2017, 1, 1, 0, 0, DateTimeZone.forOffsetHours(8)),
                new DateTime(2017, 1, 1, 20, 0, DateTimeZone.forOffsetHours(0))
        );

        // Amsterdam to New York 8h flight
        TcAmsterdamNewYork = new TimeCalculator(
                new DateTime(2017, 1, 1, 1, 0, DateTimeZone.forOffsetHours(1)),
                new DateTime(2017, 1, 1, 3, 0, DateTimeZone.forOffsetHours(-5))

        );

        // San Franciso to Washington, 6h flight
        TcSanFranWashington = new TimeCalculator(
                new DateTime(2017, 1, 1, 0, 0, DateTimeZone.forOffsetHours(-8)),
                new DateTime(2017, 1, 1, 9, 0, DateTimeZone.forOffsetHours(-5))
        );

        // Impossible flight, lands before takeoff
        TcImpossible = new TimeCalculator(
                new DateTime(2017, 1, 2, 0, 0, DateTimeZone.UTC),
                new DateTime(2016, 1, 1, 0, 0, DateTimeZone.UTC)
        );
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getFlightLength_returns_24_for_24h_flight()
            throws Exception, AirClockSpaceTimeException {
        assertThat("flight length should be 24 hours",
            this.TcLondonAuckland.getFlightLength(),
            equalTo(new Float(24 * 60 * 60 * 1000))
        );
    }

    @Test
    public void getFlightLength_returns_zero_when_land_before_depart()
            throws Exception, AirClockSpaceTimeException {
        assertThat("flight length should be zero for an impossible flight",
                TcImpossible.getFlightLength(),
                equalTo(new Float(0))
        );
    }

    @Test
    public void getFlightLength_returns_8h_Amsterdam_NY() throws Exception {
        assertThat("flight length should be 8h",
                TcAmsterdamNewYork.getFlightLength(),
                equalTo(new Float(8 * 60 * 60 * 1000))
        );
    }

    @Test
    public void getFlightLength_returns_6h_SanFran_to_Washington() throws Exception {
        assertThat("flight length should be 6h",
                TcSanFranWashington.getFlightLength(),
                equalTo(new Float(6 * 60 * 60 * 1000))
        );
    }

//    @Test
//    public void getElapsed() throws Exception {
//        System.out.println(mTimeCalculator.getElapsed(true));
//    }

    @Test
    public void getTotalTimeShift_12h_diff() throws Exception, AirClockException {
        assertThat("time shift should be 720 minutes",
                TcLondonAuckland.getTotalTimeShift(),
                equalTo(new Float(720))
        );
    }

    @Test
    public void getTotalTimeShift_with_4h_reverse_diff() throws Exception, AirClockException {
        assertThat("time shift should be -240 minutes",
                TcHonoluluBrisbane.getTotalTimeShift(),
                equalTo(new Float(-240))
        );
    }

    @Test
    public void getTotalTimeShift_with_10h_diff() throws Exception, AirClockException {
        assertThat("time shift should be -600 minutes",
                TcHonoluluIslamabad.getTotalTimeShift(),
                equalTo(new Float(-600))
        );
    }

    @Test
    public void getEffectiveOffset_with_4h_at_start() throws Exception, AirClockException {
        System.out.println(String.valueOf(TcHonoluluBrisbane.getEffectiveOffsetText()));
        setCurrentMillisFixed(
                new DateTime(2017, 1, 1, 0, 0, DateTimeZone.forOffsetHours(-10)).getMillis()
        );
        assertThat("time zone is same as origin at take off time",
            TcHonoluluBrisbane.getEffectiveOffsetText(),
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
                tc.getEffectiveOffsetText(),
                equalTo("GMT+1000")
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
                tc.getEffectiveOffsetText(),
                equalTo("GMT-1000")
        );
    }


}