package com.fugru.logger.reader;

import com.fugru.logger.BinaryLoggable;

import java.io.IOException;
import java.util.Iterator;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;

import static java.util.concurrent.TimeUnit.SECONDS;

public class AsyncReader<T extends BinaryLoggable> {
    private static final int SHUTDOWN_TIMEOUT = 5000;
    private LogReader<T> reader;
    private volatile boolean running;
    private BlockingQueue<T> queue;

    private ReadThread<T> readThread;

    private AsyncReader() {
    }

    public AsyncReader(LogReader<T> reader, BlockingQueue<T> queue) {
        this.reader = reader;
        this.queue = queue;
    }

    public Iterator<T> getIterator() {
        return new Iterator<>() {
            private final int POLL_TIMEOUT = 3;
            T nextElement;
            boolean hasNextElement;

            @Override
            public boolean hasNext() {
                try {
                    // making hasNext blocking
                    if (!hasNextElement) {
                        nextElement = queue.poll(POLL_TIMEOUT, SECONDS);
                        hasNextElement = nextElement != null;
                    }
                    return hasNextElement;
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public T next() {
                try {
                    if (hasNextElement) {
                        hasNextElement = false;
                        return nextElement;
                    } else {
                        return queue.poll(POLL_TIMEOUT, SECONDS);
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    public void start() {
        readThread = new ReadThread<>(queue, reader);
        readThread.start();

        running = true;
        System.out.println("Started AsyncReader.");
    }

    public void stop() {
        if (!running) {
            return;
        }

        readThread.shutdown();
        try {
            readThread.join(SHUTDOWN_TIMEOUT);
        } catch (InterruptedException e) {
            System.err.println("AsyncReader stopping problem: " + e.getMessage());
        }
        running = false;
        System.out.println("Stopped AsyncReader.");
    }

    private static class ReadThread<T extends BinaryLoggable> extends Thread {
        private volatile boolean shutdown;
        private BlockingQueue<T> queue;
        private LogReader<T> reader;

        private ReadThread() {
        }

        public ReadThread(BlockingQueue<T> queue, LogReader<T> reader) {
            this.queue = queue;
            this.reader = reader;
        }

        @Override
        public void run() {
            while (!shutdown) {
                try {
                    Optional<T> eventOp = reader.readNext();
                    if (eventOp.isPresent()) {
                        queue.put(eventOp.get());
                    } else {
                        shutdown = true;
                    }
                } catch (InterruptedException e) {
                    break;
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                    break;
                }
            }

            try {
                reader.close();
            } catch (IOException e) {
                System.err.println("Error while closing reader: " + e.getMessage());
            }

            System.out.println("ReadThread stopped.");
        }

        public void shutdown() {
            shutdown = true;
            if (queue.remainingCapacity() == 0) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
