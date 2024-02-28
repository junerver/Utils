package xyz.junerver.utils.thread;

import java.util.Comparator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Description: 带优先级的执行器
 *
 * @author Junerver
 * date: 2023/12/25-8:43
 * Email: junerver@gmail.com
 * Version: v1.0
 */
public class PriorityExecutor extends ThreadPoolExecutor {
    private static final int CORE_POOL_SIZE = 5;
    private static final int MAXIMUM_POOL_SIZE = 256;
    private static final int KEEP_ALIVE = 1;
    private static final AtomicLong SEQ_SEED = new AtomicLong(0L);
    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable runnable) {
            return new Thread(runnable, "download#" + this.mCount.getAndIncrement());
        }
    };
    private static final Comparator<Runnable> FIFO = new Comparator<Runnable>() {
        public int compare(Runnable lhs, Runnable rhs) {
            if (lhs instanceof PriorityRunnable && rhs instanceof PriorityRunnable) {
                PriorityRunnable lpr = (PriorityRunnable)lhs;
                PriorityRunnable rpr = (PriorityRunnable)rhs;
                int result = lpr.priority.ordinal() - rpr.priority.ordinal();
                return result == 0 ? (int)(lpr.SEQ - rpr.SEQ) : result;
            } else {
                return 0;
            }
        }
    };
    private static final Comparator<Runnable> LIFO = new Comparator<Runnable>() {
        public int compare(Runnable lhs, Runnable rhs) {
            if (lhs instanceof PriorityRunnable && rhs instanceof PriorityRunnable) {
                PriorityRunnable lpr = (PriorityRunnable)lhs;
                PriorityRunnable rpr = (PriorityRunnable)rhs;
                int result = lpr.priority.ordinal() - rpr.priority.ordinal();
                return result == 0 ? (int)(rpr.SEQ - lpr.SEQ) : result;
            } else {
                return 0;
            }
        }
    };

    public PriorityExecutor(boolean fifo) {
        this(5, fifo);
    }

    public PriorityExecutor(int poolSize, boolean fifo) {
        this(poolSize, 256, 1L, TimeUnit.SECONDS, new PriorityBlockingQueue(256, fifo ? FIFO : LIFO), sThreadFactory);
    }

    public PriorityExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
    }

    public boolean isBusy() {
        return this.getActiveCount() >= this.getCorePoolSize();
    }

    public void execute(Runnable runnable) {
        if (runnable instanceof PriorityRunnable) {
            ((PriorityRunnable)runnable).SEQ = SEQ_SEED.getAndIncrement();
        }

        super.execute(runnable);
    }
}