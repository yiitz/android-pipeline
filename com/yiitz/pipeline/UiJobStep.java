/**
 * 
 */
package com.yiitz.pipeline;

import android.os.Handler;
import android.os.Looper;

/**
 * @author yiitz
 *
 */
public abstract class UiJobStep<P, R> extends JobStep<P, R> {

	private static Handler mHandler = new Handler(Looper.getMainLooper());

	@Override
	void execRunnable(PriorityRunnable runnable) {
		mHandler.post(runnable);
	};
}
