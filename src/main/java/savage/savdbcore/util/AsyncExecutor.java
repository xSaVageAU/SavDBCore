package savage.savdbcore.util;

import java.util.concurrent.*;

/**
 * Async executor for database operations.
 * Provides a thread pool for running database tasks asynchronously.
 */
public class AsyncExecutor {
    private final ExecutorService executor;
    private final String name;

    /**
     * Create an async executor with a fixed thread pool.
     * @param name Name for the thread pool (for debugging)
     * @param threadCount Number of threads in the pool
     */
    public AsyncExecutor(String name, int threadCount) {
        this.name = name;
        this.executor = Executors.newFixedThreadPool(threadCount, r -> {
            Thread thread = new Thread(r);
            thread.setName(name + "-" + thread.getId());
            thread.setDaemon(true);
            return thread;
        });
    }

    /**
     * Create an async executor with a single thread.
     * @param name Name for the thread pool (for debugging)
     */
    public AsyncExecutor(String name) {
        this(name, 1);
    }

    /**
     * Run a task asynchronously.
     * @param task The task to run
     * @return CompletableFuture that completes when the task finishes
     */
    public CompletableFuture<Void> run(Runnable task) {
        return CompletableFuture.runAsync(task, executor);
    }

    /**
     * Run a task asynchronously and return a result.
     * @param task The task to run
     * @param <T> The result type
     * @return CompletableFuture containing the result
     */
    public <T> CompletableFuture<T> supply(Callable<T> task) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return task.call();
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        }, executor);
    }

    /**
     * Shutdown the executor and wait for tasks to complete.
     * @param timeoutSeconds Maximum time to wait for tasks to complete
     */
    public void shutdown(long timeoutSeconds) {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(timeoutSeconds, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Shutdown the executor immediately.
     */
    public void shutdownNow() {
        executor.shutdownNow();
    }

    public String getName() {
        return name;
    }
}
