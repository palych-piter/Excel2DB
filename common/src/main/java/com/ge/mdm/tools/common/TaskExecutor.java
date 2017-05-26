package com.ge.mdm.tools.common;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Supplier;


public class TaskExecutor<T extends Runnable> {

    private int poolSize;

    private Supplier<T> taskSupplier;

    private Consumer<T> taskConsumer;

    private Map<Future<?>, T> futureTaskMap;

    private int numTasksSupplied;

    private int numTasksConsumed;

    private Date executionStartDate;

    private Date executionEndDate;




    public TaskExecutor(int poolSize) {
        this.poolSize = poolSize;
        futureTaskMap = new HashMap<>();
    }



    public void execute() {
        Objects.requireNonNull(taskSupplier, "taskSupplier");

        ThreadPoolExecutor fixedThreadPool = new ThreadPoolExecutor(poolSize, poolSize, 0L, TimeUnit.MILLISECONDS, new BlockingOnOfferSynchronousQueue<>());

        numTasksSupplied = 0;
        numTasksConsumed = 0;
        executionStartDate = new Date();
        executionEndDate = null;

        T task;

        while((task = taskSupplier.get()) != null) {
            numTasksSupplied++;
            consumeCompletedTasks(false);

            Future<?> future = fixedThreadPool.submit(task);
            futureTaskMap.put(future, task);
        }

        consumeCompletedTasks(true);

        executionEndDate = new Date();

        fixedThreadPool.shutdown(); // must terminate immediately as all tasks completed
    }


    private void consumeCompletedTasks(boolean force) {
        Iterator<Future<?>> it = futureTaskMap.keySet().iterator();
        while(it.hasNext()) {
            Future<?> f = it.next();

            // if task still not completed and force = true, wait until it will complete
            if(! f.isDone() && force) {
                try {
                    f.get(); // block until task completed, always returns null
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                    return; // thread interrupted, stop consuming tasks
                } catch (ExecutionException e) {
                    e.printStackTrace();
                    continue; // do not allow to consume as task has not completed
                }
            }

            // consume task if it was done
            if(f.isDone()) {
                T t = futureTaskMap.get(f);
                taskConsumer.accept(t);
                it.remove();
                numTasksConsumed++;
            }

        }
    }


    public void setTaskSupplier(Supplier<T> taskSupplier) {
        this.taskSupplier = taskSupplier;
    }

    public void setTaskConsumer(Consumer<T> taskConsumer) {
        this.taskConsumer = taskConsumer;
    }


    public int getNumTasksSupplied() {
        return numTasksSupplied;
    }

    public int getNumTasksConsumed() {
        return numTasksConsumed;
    }

    public Date getExecutionStartDate() {
        return executionStartDate;
    }

    public Date getExecutionEndDate() {
        return executionEndDate;
    }


    private static class BlockingOnOfferSynchronousQueue<E> extends SynchronousQueue<E> {

        public BlockingOnOfferSynchronousQueue() {
            super();
        }

        @Override
        public boolean offer(E e) {
            try {
                put(e);
                return true;
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
            return false;
        }

    }

}
