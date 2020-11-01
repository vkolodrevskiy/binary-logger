package com.fugru.logger.reader;

import com.fugru.logger.BinaryLoggable;

import java.io.Closeable;
import java.util.Optional;

public interface LogReader<T extends BinaryLoggable> extends Closeable {
    Optional<T> readNext() throws Exception;
}
