package me.longluo.raytracing.manager;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import timber.log.Timber;

/**
 * 线程池管理类
 */
public final class ThreadPoolManager {

    private static volatile ThreadPoolManager sInstance;

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();// CPU个数

    private static final int CORE_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT - 1, 4));// 线程池中核心线程的数量

    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;// 线程池中最大线程数量

    private static final long KEEP_ALIVE_TIME = 30L;// 非核心线程的超时时长，当系统中非核心线程闲置时间超过keepAliveTime之后，则会被回收。
    // 如果ThreadPoolExecutor的allowCoreThreadTimeOut属性设置为true，则该参数也表示核心线程的超时时长

    private static final int WAIT_COUNT = 128; // 最多排队个数，这里控制线程创建的频率

    private static ThreadPoolExecutor pool = createThreadPoolExecutor();

    public ThreadPoolManager() {
        createThreadPoolExecutor();
    }

    private static ThreadPoolExecutor createThreadPoolExecutor() {
        if (pool == null) {
            pool = new ThreadPoolExecutor(
                    CORE_POOL_SIZE,
                    MAXIMUM_POOL_SIZE,
                    KEEP_ALIVE_TIME,
                    TimeUnit.SECONDS,
                    new LinkedBlockingQueue<Runnable>(WAIT_COUNT),
                    new CThreadFactory("ThreadPoolManager", Thread.NORM_PRIORITY - 2),
                    new CHandlerException());
        }

        return pool;
    }

    public static ThreadPoolManager getInstance() {
        if (sInstance == null) {
            synchronized (ThreadPoolManager.class) {
                if (sInstance == null) {
                    sInstance = new ThreadPoolManager();
                }
            }
        }

        return sInstance;
    }

    public static class CThreadFactory implements ThreadFactory {
        private AtomicInteger counter = new AtomicInteger(1);
        private String prefix = "";
        private int priority = Thread.NORM_PRIORITY;

        public CThreadFactory(String prefix, int priority) {
            this.prefix = prefix;
            this.priority = priority;
        }

        public CThreadFactory(String prefix) {
            this.prefix = prefix;
        }

        public Thread newThread(Runnable r) {
            Thread executor = new Thread(r, prefix + " #" + counter.getAndIncrement());
            executor.setDaemon(true);
            executor.setPriority(priority);
            return executor;
        }
    }

    /**
     * 抛弃当前的任务
     */
    private static class CHandlerException extends ThreadPoolExecutor.AbortPolicy {

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
            Timber.d("rejectedExecution: runnable = %s, stack = %s", r.toString(), logAllThreadStackTrace().toString());
            //            Tips.showForce("任务被拒绝", 5000);
            if (!pool.isShutdown()) {
                pool.shutdown();
                pool = null;
            }

            pool = createThreadPoolExecutor();
        }
    }

    private static ExecutorService jobsForUI = Executors.newFixedThreadPool(
            CORE_POOL_SIZE, new CThreadFactory("CJobsForUI", Thread.NORM_PRIORITY - 1));

    /**
     * 启动一个消耗线程，常驻后台
     *
     * @param r
     */
    public static void startConsumer(final Runnable r, final String name) {
        runInBackground(new Runnable() {
            public void run() {
                new CThreadFactory(name, Thread.NORM_PRIORITY - 3).newThread(r).start();
            }
        });
    }

    /**
     * 提交到其他线程去跑，需要取数据的时候会等待任务完成再继续
     *
     * @param task
     * @return
     */
    public static <T> Future<T> submitTask(Callable<T> task) {
        return jobsForUI.submit(task);
    }

    /**
     * 强制清理任务
     *
     * @param task
     * @return
     */
    public static <T> void cancelTask(Future<T> task) {
        if (task != null) {
            task.cancel(true);
        }
    }

    /**
     * 从 Future 中获取值，如果发生异常，打日志
     *
     * @param future
     * @param tag
     * @param name
     * @return
     */
    public static <T> T getFromTask(Future<T> future, String tag, String name) {
        try {
            return future.get();
        } catch (Exception e) {
            Log.e(tag, (name != null ? name + ": " : "") + e.toString());
        }

        return null;
    }

    public static void runInBackground(Runnable runnable) {
        if (pool == null) {
            createThreadPoolExecutor();
        }

        pool.execute(runnable);

        //        Future future = pool.submit(runnable);
        //        try {
        //            future.get();
        //        } catch (InterruptedException e) {
        //            e.printStackTrace();
        //        } catch (ExecutionException e) {
        //            e.printStackTrace();
        //        }
    }

    private static Thread mainThread;
    private static Handler mainHandler;

    static {
        Looper mainLooper = Looper.getMainLooper();
        mainThread = mainLooper.getThread();
        mainHandler = new Handler(mainLooper);
    }

    public static boolean isOnMainThread() {
        return mainThread == Thread.currentThread();
    }

    public static void runOnMainThread(Runnable r) {
        if (isOnMainThread()) {
            r.run();
        } else {
            mainHandler.post(r);
        }
    }

    public static void runOnMainThread(Runnable r, long delayMillis) {
        if (delayMillis <= 0) {
            runOnMainThread(r);
        } else {
            mainHandler.postDelayed(r, delayMillis);
        }
    }

    // 用于记录后台等待的Runnable，第一个参数外面的Runnable，第二个参数是等待中的Runnable
    private static HashMap<Runnable, Runnable> mapToMainHandler = new HashMap<Runnable, Runnable>();

    public static void runInBackground(final Runnable runnable, long delayMillis) {
        if (delayMillis <= 0) {
            runInBackground(runnable);
        } else {
            Runnable mainRunnable = new Runnable() {

                @Override
                public void run() {
                    mapToMainHandler.remove(runnable);
                    pool.execute(runnable);
                }
            };

            mapToMainHandler.put(runnable, mainRunnable);
            mainHandler.postDelayed(mainRunnable, delayMillis);
        }
    }

    /**
     * 对runOnMainThread的，移除Runnable
     *
     * @param r
     */
    public static void removeCallbackOnMainThread(Runnable r) {
        mainHandler.removeCallbacks(r);
    }

    public static void removeCallbackInBackground(Runnable runnable) {
        Runnable mainRunnable = mapToMainHandler.get(runnable);
        if (mainRunnable != null) {
            mainHandler.removeCallbacks(mainRunnable);
        }
    }

    public static void logStatus() {
        StringBuilder sb = new StringBuilder();
        sb.append("getActiveCount");
        sb.append(pool.getActiveCount());
        sb.append("\ngetTaskCount");
        sb.append(pool.getTaskCount());
        sb.append("\ngetCompletedTaskCount");
        sb.append(pool.getCompletedTaskCount());

        Timber.d(sb.toString());
    }

    public static StringBuilder logAllThreadStackTrace() {
        StringBuilder builder = new StringBuilder();
        Map<Thread, StackTraceElement[]> liveThreads = Thread.getAllStackTraces();

        for (Iterator<Thread> i = liveThreads.keySet().iterator(); i.hasNext(); ) {
            Thread key = i.next();
            builder.append("Thread ").append(key.getName())
                    .append("\n");
            StackTraceElement[] trace = liveThreads.get(key);
            for (int j = 0; j < trace.length; j++) {
                builder.append("\tat ").append(trace[j]).append("\n");
            }
        }

        return builder;
    }
}