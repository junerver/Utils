package xyz.junerver.utils.thread;

/**
 * Description: 带优先级的 Runnable
 *
 * @author Junerver
 * date: 2023/12/25-8:44
 * Email: junerver@gmail.com
 * Version: v1.0
 */
public class PriorityRunnable implements Runnable {
    public final Priority priority; //优先级
    private final Runnable runnable;
    long SEQ; //序列

    public PriorityRunnable(Priority priority, Runnable runnable) {
        this.priority = priority == null ? Priority.NORMAL : priority;
        this.runnable = runnable;
    }

    public final void run() {
        this.runnable.run();
    }
}
