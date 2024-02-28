package xyz.junerver.utils.thread;

import java.util.concurrent.ExecutorService;

/**
 * Description:
 *
 * @author Junerver
 * date: 2023/12/25-8:44
 * Email: junerver@gmail.com
 * Version: v1.0
 */
public class ThreadPoolManager {
    private static final ExecutorService executorService = new PriorityExecutor(5, false);
    private static final ThreadPoolManager manager = new ThreadPoolManager();

    private ThreadPoolManager() {
    }

    public static ThreadPoolManager getInstance() {
        return manager;
    }

    public void addTask(Runnable runnable) {
        executorService.execute(runnable);
    }
}
