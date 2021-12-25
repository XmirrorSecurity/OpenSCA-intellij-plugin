package cn.xmirror.sca.common;

import java.util.concurrent.*;

/**
 * SCA线程池
 *
 * @author Yuan Shengjun
 */
public class SCAThreadPool {
    private final ExecutorService executor;

    private SCAThreadPool() {
        executor = new ThreadPoolExecutor(0, 10, 60L, TimeUnit.SECONDS, new SynchronousQueue<>());
    }

    public static SCAThreadPool getInstance() {
        return InstanceHolder.instance;
    }

    public Future<?> submit(Runnable task) {
        return executor.submit(task);
    }

    public <T> Future<T> submit(Callable<T> task) {
        return executor.submit(task);
    }

    private static class InstanceHolder {
        private static final SCAThreadPool instance = new SCAThreadPool();
    }
}
