package nz.al4.airclock;

/**
 * An exception for impossible situations
 *
 * Created by alex on 14/01/2017.
 */
public class AirClockSpaceTimeException extends AirClockException {
    public AirClockSpaceTimeException(String msg) {
        super(msg);
    }
}
