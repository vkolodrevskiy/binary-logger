package com.fugru.logger.writer;

import com.fugru.logger.BinaryLoggable;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

import static com.fugru.logger.Utils.intToByteArray;
import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;

public class BinaryLogFileAppender<T extends BinaryLoggable> implements Appender<T> {
    private OutputStream outputStream;
    private RolloverStrategy rolloverStrategy;

    private BinaryLogFileAppender() {
    }

    public BinaryLogFileAppender(RolloverStrategy rolloverStrategy) throws IOException {
        this.rolloverStrategy = rolloverStrategy;
        this.outputStream = Files.newOutputStream(rolloverStrategy.getCurrentFile().toPath(), CREATE, APPEND);
    }

    @Override
    public void append(T event) {
        try {
            writeBytes(event.toBytes());
        } catch (IOException e) {
            System.err.println("Issue while writing bytes: " + e.getLocalizedMessage());
        }
    }

    private void writeBytes(byte[] outBytes) throws IOException {
        if (outBytes != null && outBytes.length > 0) {
            tryRollover();

            outputStream.write(intToByteArray(outBytes.length));
            outputStream.write(outBytes);
            outputStream.flush();
        }
    }

    private void tryRollover() throws IOException {
        if (rolloverStrategy.rollover()) {
            this.outputStream.close();
            this.outputStream = Files.newOutputStream(rolloverStrategy.getCurrentFile().toPath(), CREATE, APPEND);
        }
    }
}
