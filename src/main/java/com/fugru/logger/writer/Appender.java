package com.fugru.logger.writer;

import com.fugru.logger.BinaryLoggable;

public interface Appender<T extends BinaryLoggable> {
    void append(T event);
}
