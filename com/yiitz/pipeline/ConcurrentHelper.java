/**
 * 
 */
package com.yiitz.pipeline;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author yiitz
 *
 */
public class ConcurrentHelper {
	private static Handler mHandler;
	
	private static ExecutorService mExecutorService;
	
	static{
		mHandler = new Handler(Looper.getMainLooper());
		
		mExecutorService = Executors.newCachedThreadPool();
	}
	
	public static void runOnUiThread(Runnable runnable) {
		mHandler.post(runnable);
	}
	
	public static void runOnIoThread(Runnable runnable) {
		mExecutorService.execute(runnable);
	}
}
