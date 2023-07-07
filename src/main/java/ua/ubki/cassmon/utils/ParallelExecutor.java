package ua.ubki.cassmon.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.ubki.cassmon.exception.UbkiException;

import java.util.List;
import java.util.concurrent.*;

public final class ParallelExecutor {

    public static final int DEFAULT_DESIRE_THREADS = 3;
    private static final Logger LOGGER = LoggerFactory.getLogger(ParallelExecutor.class);
    private static final int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();

    private ParallelExecutor() {
        // utils class
    }

    public static ExecutorService buildLimitedExecutor(int desireThreads, boolean forceNumberOfThread) {
        int threadCount = forceNumberOfThread ? desireThreads : Math.min(desireThreads, AVAILABLE_PROCESSORS - 1);
        if (desireThreads > threadCount) {
            LOGGER.warn("Limited thread count to `{}` instead of desired `{}`.", threadCount, desireThreads);
        }
        final ForkJoinPool.ForkJoinWorkerThreadFactory factory = pool -> {
            final ForkJoinWorkerThread worker = ForkJoinPool.defaultForkJoinWorkerThreadFactory.newThread(pool);
            String threadName = String.format("parallel_%d_%d", worker.getPoolIndex(), worker.getId());
            worker.setName(threadName);
            return worker;
        };
        return new ForkJoinPool(threadCount, factory, null, false);
    }

    public static <R> List<R> callOnThreads(Callable<List<R>> task) {
        return callOnThreads(task, DEFAULT_DESIRE_THREADS, false);
    }

    public static void runOnThreads(Runnable task) {
        runOnThreads(task, DEFAULT_DESIRE_THREADS, false);
    }

    public static <R> List<R> callOnThreads(Callable<List<R>> task, int desireThreads, boolean forceNumberOfThread) {
        ExecutorService executorService = buildLimitedExecutor(desireThreads, forceNumberOfThread);
        try {
            return executorService.submit(task).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new UbkiException(500, "Parallel processing just interrupted!", e);
        } catch (ExecutionException e) {
            throw new UbkiException(500, "Exception during a parallel processing", e);
        } finally {
            executorService.shutdown();
        }
    }

    public static void runOnThreads(Runnable task, int desireThreads, boolean forceNumberOfThread) {
        ExecutorService executorService = buildLimitedExecutor(desireThreads, forceNumberOfThread);
        try {
            executorService.submit(task).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new UbkiException(500, "Parallel processing just interrupted!", e);
        } catch (ExecutionException e) {
            throw new UbkiException(500, "Exception during a parallel processing", e);
        } finally {
            executorService.shutdown();
        }
    }

}
