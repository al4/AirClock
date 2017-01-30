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
import static org.hamcrest.Matchers.greaterThan;
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
    private TimeCalculator TcLondonAucklandReverse;

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(Log.class);
        // Honolulu to Brisbane, 4h offset, 10h flight
        TcHonoluluBrisbane = new TimeCalculator(
                new DateTime(2017, 1, 1, 14, 0, DateTimeZone.forOffsetHours(-10)),
                new DateTime(2017, 1, 2, 20, 0, DateTimeZone.forOffsetHours(10))
        );

        // London to Auckland, 12h offset, 24h flight
        TcLondonAuckland = new TimeCalculator(
                new DateTime(2017, 1, 1, 0, 0, DateTimeZone.UTC),
                new DateTime(2017, 1, 2, 12, 0, DateTimeZone.forOffsetHours(12))
        );

        // London to Auckland, 12h offset, 24h flight, reverse over date line
        TcLondonAucklandReverse = new TimeCalculator(
                new LocalDateTime(2017, 1, 1, 0, 0).toDateTime(DateTimeZone.UTC),
                new LocalDateTime(2017, 1, 2, 13, 0).toDateTime(DateTimeZone.forOffsetHours(13))
        );

        // Honolulu to Islamabad 10h offset
        TcHonoluluIslamabad = new TimeCalculator(
                new DateTime(2017, 1, 1, 0, 0, DateTimeZone.forOffsetHours(-10)),
                new DateTime(2017, 1, 2, 0, 0, DateTimeZone.forOffsetHours(4))
        );

        // Perth to London 20h flight
        TcPerthLondon = new TimeCalculator(
                new DateTime(2017, 1, 1, 0, 0, DateTimeZone.forOffsetHours(8)),
                new DateTime(2017, 1, 1, 12, 0, DateTimeZone.forOffsetHours(0))
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
    public void getFlightLength_London_to_Auckland()
            throws Exception, AirClockSpaceTimeException {
        assertThat("flight length should be 24 hours",
            this.TcLondonAuckland.getFlightLength(),
            equalTo(new Float(24 * 60 * 60 * 1000))
        );
    }

    @Test
    public void getFlightLength_Perth_to_London()
            throws Exception, AirClockSpaceTimeException {
        assertThat("flight length should be 20 hours",
                this.TcPerthLondon.getFlightLength(),
                equalTo(new Float(20 * 60 * 60 * 1000))
        );
    }

    @Test
    public void getFlightLength_Honlulu_to_Brisbane() throws Exception {
        assertThat("flight length should be 10 hours",
                this.TcHonoluluBrisbane.getFlightLength(),
                equalTo(new Float(10 * 60 * 60 * 1000))
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
    public void getTotalTimeShift_London_to_Auckland() throws Exception, AirClockException {
        assertThat("time shift should be 720 minutes",
                TcLondonAuckland.getTimeShiftMins(),
                equalTo(new Integer(720))
        );
    }

    @Test
    public void getTotalTimeShift_Honolulu_to_Brisbane() throws Exception, AirClockException {
        assertThat("time shift should be -240 minutes",
                TcHonoluluBrisbane.getTimeShiftMins(),
                equalTo(new Integer(-240))
        );
    }

    @Test
    public void getTotalTimeShift_Honolulu_to_Islamabad() throws Exception, AirClockException {
        assertThat("time shift should be -600 minutes",
                TcHonoluluIslamabad.getTimeShiftMins(),
                equalTo(new Integer(-600))
        );
    }


    @Test
    public void getEffectiveOffset_Honolulu_to_Brisbane_at_origin() throws Exception {
        setCurrentMillisFixed(
                new DateTime(2017, 1, 1, 14, 0, DateTimeZone.forOffsetHours(-10)).getMillis()
        );
        assertThat("time zone is same as origin at take off time",
            TcHonoluluBrisbane.getEffectiveOffsetText(),
            equalTo("GMT-1000")
        );
    }

    @Test
    public void getEffectiveOffset_SanFran_to_Washington_at_origin() throws Exception {
        setCurrentMillisFixed(
                new DateTime(2017, 1, 1, 0, 0, DateTimeZone.forOffsetHours(-8)).getMillis()
        );
        assertThat("time zone is same as origin at take off time",
                TcSanFranWashington.getEffectiveOffsetText(),
                equalTo("GMT-0800")
        );
    }

    @Test
    public void getEffectiveOffset_London_to_Auckland_half_way() throws Exception {
        setCurrentMillisFixed(
                new DateTime(2017, 1, 1, 12, 0, DateTimeZone.UTC).getMillis()
        );
        assertThat("time zone is GMT+0600 half way from London UTC to Auckland +12",
                TcLondonAuckland.getEffectiveOffsetText(),
                equalTo("GMT+0600")
        );
    }

    @Test
    public void getEffectiveOffset_London_to_Auckland_half_way_reverse() throws Exception {
        setCurrentMillisFixed(
                new DateTime(2017, 1, 1, 12, 0, DateTimeZone.UTC).getMillis()
        );
        assertThat("time zone is GMT-0530 half way from London UTC to Auckland +13",
                TcLondonAucklandReverse.getEffectiveOffsetText(),
                equalTo("GMT-0530")
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


    @Test
    public void getFlightProgress_in_flight() throws Exception {
        setCurrentMillisFixed(
                new DateTime(2017, 1, 2, 0, 0, DateTimeZone.UTC).getMillis()
        );
        assertThat("status is in flight",
                TcLondonAuckland.getFlightStatusText(),
                equalTo(new String("in flight"))
        );
    }

    @Test
    public void getFlightProgress_at_origin() throws Exception {
        setCurrentMillisFixed(
                new DateTime(2016, 12, 31, 0, 0, DateTimeZone.UTC).getMillis()
        );
        assertThat("status is at origin",
                TcLondonAuckland.getFlightStatusText(),
                equalTo(new String("at origin"))
        );
    }

    @Test
    public void getFlightProgress_at_destination() throws Exception {
        setCurrentMillisFixed(
                new DateTime(2017, 1, 3, 0, 0, DateTimeZone.UTC).getMillis()
        );
        assertThat("status is at destination",
                TcLondonAuckland.getFlightStatusText(),
                equalTo(new String("at destination"))
        );
    }

    @Test
    public void timeForAlarm_returns_DateTime_after_origin() throws Exception {
        // London to Auckland, 12h offset, 24h flight
        TcLondonAuckland = new TimeCalculator(
                new DateTime(2017, 1, 1, 0, 0, DateTimeZone.UTC),
                new DateTime(2017, 1, 2, 12, 0, DateTimeZone.forOffsetHours(12))
        );
        setCurrentMillisFixed(
                new DateTime(2017, 1, 1, 4, 0, DateTimeZone.UTC).getMillis() // 4am UTC
        );
        LocalDateTime alarmTime = new LocalDateTime(2017, 1, 1, 8, 0);   // 8am alarm
        assertThat("returns DateTime after origin",
                TcLondonAuckland.timeForAlarm(alarmTime).getMillis(),
                greaterThan(TcLondonAuckland.mOriginTime.toInstant().getMillis())
        );
    }

//    @Test
//    public void timeForAlarm_() throws Exception {
//        LocalDateTime alarmTime = new LocalDateTime(2017, 1, 1, 8, 0);   // 8am alarm
//        assertThat("returns DateTime after origin",
//                TcLondonAuckland.timeForAlarm(alarmTime).getMillis(),
//                greaterThan(TcLondonAuckland.mOriginTime.getMillis())
//        );
//    }

}