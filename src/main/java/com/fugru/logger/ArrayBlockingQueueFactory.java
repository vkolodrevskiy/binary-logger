package com.fugru.logger;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ArrayBlockingQueueFactory<E> implements BlockingQueueFactory<E> {

    @Override
    public BlockingQueue<E> create(int capacity) {
        return new ArrayBlockingQueue<>(capacity);
    }
}
