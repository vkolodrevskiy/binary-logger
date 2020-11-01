package com.fugru.logger;

import com.fugru.logger.reader.AsyncReader;
import com.fugru.logger.reader.BinaryLogFileReader;
import com.fugru.logger.reader.LogReader;
import com.fugru.logger.writer.AsyncAppender;
import com.fugru.logger.writer.BinaryLogFileAppender;
import com.fugru.logger.writer.FileSizeRolloverStrategy;
import com.fugru.logger.writer.RolloverStrategy;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class AsyncBinaryLoggerImpl<T extends BinaryLoggable> implements BinaryLogger<T> {
    private AsyncAppender<T> writer;
    private static final int QUEUE_CAPACITY = 100;
    private final List<AsyncReader<T>> readers = new ArrayList<>();
    private final BlockingQueueFactory<T> arrayQueueFactory = new ArrayBlockingQueueFactory<>();

    private AsyncBinaryLoggerImpl() {
    }

    public AsyncBinaryLoggerImpl(String filePath, long maxFileSize) throws IOException {
        writer = initWriter(filePath, maxFileSize);
        writer.start();
    }

    @Override
    public void write(T loggable) throws IOException {
        writer.append(loggable);
    }

    @Override
    public Iterator<T> read(File file, Class<T> clazz) throws IOException {
        AsyncReader<T> reader = initReader(file, clazz);
        reader.start();
        readers.add(reader);
        return reader.getIterator();
    }

    @Override
    public void close() throws IOException {
        writer.stop();
        readers.forEach(AsyncReader::stop);
    }

    private AsyncAppender<T> initWriter(String filePath, long maxFileSize) throws IOException {
        BlockingQueue<T> queue = arrayQueueFactory.create(QUEUE_CAPACITY);
        RolloverStrategy policy = new FileSizeRolloverStrategy(filePath, maxFileSize);
        BinaryLogFileAppender<T> binaryLogFileAppender = new BinaryLogFileAppender<>(policy);

        return new AsyncAppender<>(binaryLogFileAppender, queue);
    }
    
    private AsyncReader<T> initReader(File file, Class<T> clazz) throws IOException {
        BlockingQueue<T> queue = arrayQueueFactory.create(QUEUE_CAPACITY);
        LogReader<T> logReader = new BinaryLogFileReader<>(file, clazz);

        return new AsyncReader<>(logReader, queue);
    }
}
