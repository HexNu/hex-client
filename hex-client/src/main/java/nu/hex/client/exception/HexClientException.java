package nu.hex.client.exception;

/**
 * Created 2016-nov-14
 *
 * @author hl
 */
public class HexClientException extends RuntimeException {

    public HexClientException(String message) {
        super(message);
    }

    public HexClientException(String message, Throwable e) {
        super(message, e);
    }
}
