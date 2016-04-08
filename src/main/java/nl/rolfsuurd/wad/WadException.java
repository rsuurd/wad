package nl.rolfsuurd.wad;

import java.io.IOException;

/**
 * Signals an operation on a {@link WadFile WAD-File} has failed.
 *
 * @author Rolf
 */
public class WadException extends IOException {
    /**
     * Constructs a {@code WadException} with the specified detail message.
     *
     * @param message The detail message
     */
    public WadException(String message) {
        super(message);
    }

    /**
     * Constructs an {@code WadException} with the specified detail message
     * and cause.
     *
     * <p> Note that the detail message associated with {@code cause} is
     * <i>not</i> automatically incorporated into this exception's detail
     * message.
     *
     * @param message The detail message
     *
     * @param cause The cause
     */
    public WadException(String message, Throwable cause) {
        super(message, cause);
    }
}
