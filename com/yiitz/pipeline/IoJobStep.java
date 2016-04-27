/**
 * 
 */
package com.yiitz.pipeline;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author yiitz
 *
 */
@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public abstract class IoJobStep<P, R> extends JobStep<P, R> {

	private static XThreadPoolExecutor mExecutorServiceForPreemptiveJob;

	private static XThreadPoolExecutor mExecutorService;

	static {
		mExecutorServiceForPreemptiveJob = new XThreadPoolExecutor(2, 2, 60, TimeUnit.SECONDS,
				new PriorityBlockingQueue<Runnable>(), new ThreadPoolExecutor.DiscardOldestPolicy());

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			mExecutorServiceForPreemptiveJob.allowCoreThreadTimeOut(true);
		}

		mExecutorService = new XThreadPoolExecutor(8, 8, 3 * 60, TimeUnit.SECONDS, new PriorityBlockingQueue<Runnable>(),
				new ThreadPoolExecutor.DiscardOldestPolicy());

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			mExecutorService.allowCoreThreadTimeOut(true);
		}
	}

	@Override
	void execRunnable(PriorityRunnable runnable) {
		if (!mExecutorService.hasIdleThread() && runnable.getPriority() < 0
				&& runnable.getPriority() < mExecutorService.getLeastPriority()) {
			job.future = mExecutorServiceForPreemptiveJob.submit(runnable);
			Log.d("pipeline","enter level 2 ExecutorService");
		} else {
			job.future = mExecutorService.submit(runnable);
		}
	}

	private static class XThreadPoolExecutor extends ThreadPoolExecutor {

		private Set<Object> runningSet = new HashSet<Object>();

		public XThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
				BlockingQueue<Runnable> workQueue) {
			super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
		}

		public XThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
				BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
			super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
		}

		public XThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
				BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
			super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
		}

		public XThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
				BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
			super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
		}

		@Override
		protected <T> RunnableFuture<T> newTaskFor(Runnable runnable, T value) {
			return new ComparableFutureTask<T>(runnable, value);
		}

		@Override
		protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
			return new ComparableFutureTask<T>(callable);
		}

		@Override
		protected void beforeExecute(Thread t, Runnable r) {
			runningSet.add(r);
			super.beforeExecute(t, r);
		}

		@Override
		protected void afterExecute(Runnable r, Throwable t) {
			runningSet.remove(r);
			super.afterExecute(r, t);
		}

		public int getLeastPriority() {
			int priority = 100;
			for (Object priorityRunnable : runningSet) {
				int tempPriority = ((PriorityRunnable) ((ComparableFutureTask<?>) priorityRunnable).runnable)
						.getPriority();
				if (priority > tempPriority) {
					priority = tempPriority;
				}
			}
			return priority;
		}

		public boolean hasIdleThread() {
			return runningSet.size() < getCorePoolSize();
		}

	}

	private static class ComparableFutureTask<V> extends FutureTask<V> implements Comparable<ComparableFutureTask<V>> {
		Object runnable;

		public ComparableFutureTask(Callable<V> callable) {
			super(callable);
			runnable = callable;
		}

		public ComparableFutureTask(Runnable runnable, V result) {
			super(runnable, result);
			this.runnable = runnable;
		}

		@Override
		@SuppressWarnings("unchecked")
		public int compareTo(ComparableFutureTask<V> o) {
			if (this == o) {
				return 0;
			}
			if (o == null) {
				return -1;
			}
			if (runnable != null && o.runnable != null) {
				if (runnable.getClass().equals(o.runnable.getClass())) {
					if (runnable instanceof Comparable) {
						return ((Comparable) runnable).compareTo(o.runnable);
					}
				}
			}
			return 0;
		}
	}
}
