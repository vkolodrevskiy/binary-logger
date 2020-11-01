package com.fugru.logger;

import java.io.IOException;

public abstract class AbstractBinaryLoggable<T extends BinaryLoggable> implements BinaryLoggable {

    @Override
    public byte[] toBytes() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void fromBytes(byte[] rawBytes) throws IOException {
        throw new UnsupportedOperationException();
    }
}
