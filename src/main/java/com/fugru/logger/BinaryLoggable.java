package com.fugru.logger;

import java.io.IOException;

/**
 * BinaryLoggable represents an entity that can be logged by {@link BinaryLogger}.
 */
public interface BinaryLoggable {
    /**
     * Serialize the fields of this object into a byte array.
     */
    byte[] toBytes() throws IOException;

    /**
     * Deserialize the fields of this object from given byte array.
     */
    void fromBytes(byte[] rawBytes) throws IOException;
}
