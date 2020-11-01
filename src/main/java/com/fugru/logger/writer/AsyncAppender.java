package com.fugru.logger.writer;

import com.fugru.logger.AbstractBinaryLoggable;
import com.fugru.logger.BinaryLoggable;

import java.util.concurrent.BlockingQueue;

public class AsyncAppender<T extends BinaryLoggable> implements Appender<T> {
    private static final int SHUTDOWN_TIMEOUT = 5000;
    private Appender<T> appender;
    private volatile boolean running;
    private BlockingQueue<T> queue;

    private AppendThread<T> appendThread;

    private AsyncAppender() {
    }

    public AsyncAppender(Appender<T> appender, BlockingQueue<T> queue) {
        this.appender = appender;
        this.queue = queue;
    }

    @Override
    public void append(T event) {
        if (!running) {
            throw new IllegalStateException("AsyncAppender is not running!");
        }
        queue.offer(event);
    }

    public void start() {
        appendThread = new AppendThread<>(queue, appender);
        appendThread.start();

        running = true;
        System.out.println("Started AsyncAppender.");
    }

    public void stop() {
        if (!running) {
            return;
        }

        appendThread.shutdown();
        try {
            appendThread.join(SHUTDOWN_TIMEOUT);
        } catch (InterruptedException e) {
            System.err.println("AsyncAppender stopping problem: " + e.getMessage());
        }
        running = false;
        System.out.println("Stopped AsyncAppender.");
    }

    private static class AppendThread<T extends BinaryLoggable> extends Thread {
        private volatile boolean shutdown;
        private BlockingQueue<T> queue;
        private Appender<T> appender;

        private final BinaryLoggable SHUTDOWN_EVENT = new AbstractBinaryLoggable<T>() {};

        private AppendThread() {
        }

        public AppendThread(BlockingQueue<T> queue, Appender<T> appender) {
            this.queue = queue;
            this.appender = appender;
        }

        @Override
        public void run() {
            while (!shutdown) {
                T event;
                try {
                    event = queue.take();
                    if (event == SHUTDOWN_EVENT) {
                        shutdown = true;
                        continue;
                    }
                } catch (InterruptedException e) {
                    break;
                }

                appender.append(event);
            }

            while (!queue.isEmpty()) {
                T event;
                try {
                    event = queue.take();
                } catch (InterruptedException e) {
                    break;
                }

                appender.append(event);
            }

            System.out.println("AppendThread stopped.");
        }

        public void shutdown() {
            shutdown = true;
            if (queue.isEmpty()) {
                queue.offer((T) SHUTDOWN_EVENT);
            }
        }
    }
}
