#### How to run:


#### Description:



#### Improvements that can/should be made:
- Good tests and polish.
- Instead of ArrayBlockingQueue we can use something faster, for example LMAX Disruptor.
- Need to introduce some exception handler for working threads that are doing reads and writes to log file, so that 
application be aware of those errors. 
