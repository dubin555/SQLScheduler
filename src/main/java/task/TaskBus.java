package task;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Simple thread-safe implement of a bus with necessary methods.
 */
public class TaskBus<T> {

    /**
     * Simple proxy thread-safe implement.
     */
    private BlockingQueue<T> taskBus = new LinkedBlockingQueue<>();

    /**
     * Non-Blocking method to put a element into bus.
     */
    public void add(T t) {
        taskBus.add(t);
    }

    /**
     * Non-Blocking method to take a element from bus.
     */
    public T poll() {
        return taskBus.poll();
    }

    /**
     * Blocking method to take a element from bus.
     */
    public T take() {
        try {
            return taskBus.take();
        } catch (InterruptedException e) {
            return null;
        }
    }

    /**
     * Current size of taskBus, will be used for monitor!
     */
    public int size() {
        return taskBus.size();
    }
}
