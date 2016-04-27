/**
 * 
 */
package com.yiitz.pipeline;

/**
 * @author yiitz
 *
 */
public abstract class NextStep<P, R> extends JobStep<P, R> {

	@Override
	void execRunnable(PriorityRunnable runnable) {
		runnable.run();
	}

}
