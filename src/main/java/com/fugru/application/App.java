package com.fugru.application;

import com.fugru.logger.AsyncBinaryLoggerImpl;
import com.fugru.logger.BinaryLogger;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class App {
    public static void main(String[] args) throws IOException {
        App app = new App();

        List<Event> events = app.buildEvents(100);
        app.writeAndReadEvents(events);
    }

    void writeAndReadEvents(Iterable<Event> events) throws IOException {
        // Write given events
        try (BinaryLogger<Event> logger = new AsyncBinaryLoggerImpl<>
                ("events.bin", 1000L)) {
            for (Event event : events) {
                logger.write(event);
            }

            // Read them back
            Iterator<Event> iterator = logger.read(new File("events.bin"), Event.class);
            while (iterator.hasNext()) {
                System.out.println(iterator.next());
            }
        }
    }

    private List<Event> buildEvents(int eventsCount) {
        return IntStream.range(0, eventsCount)
                .mapToObj(id -> new Event(id, "Event id: " + id))
                .collect(Collectors.toList());
    }
}
