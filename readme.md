#### How to run:
Make sure you have java at least version 11 and maven at least 3.5.0 installed.
Navigate to the root of the source folder and run in terminal:
```
mvn compile exec:java -Dexec.mainClass="com.fugru.application.App"
```

#### Solution description:
Main goal of this solution was to provide lower latency for the client code calling it.
So instead of blocking client code on writing and reading from files directly, events are sent to in-memory queue.
Asynchronous threads are handling working with file system. 

#### Improvements that can/should be made:
- Good tests and polish.
- Instead of `ArrayBlockingQueue` internally we can use something faster, for example LMAX Disruptor.
- Need to introduce some exception handler for working threads that are doing reads and writes to log file, so that 
application be aware of those errors. 
