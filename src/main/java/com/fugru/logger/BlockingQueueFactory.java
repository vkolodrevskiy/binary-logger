package com.fugru.logger;

import java.util.concurrent.BlockingQueue;

public interface BlockingQueueFactory<E> {
    BlockingQueue<E> create(int capacity);
}
