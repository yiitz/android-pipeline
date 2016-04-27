/**
 * 
 */
package com.yiitz.pipeline;

/**
 * @author yiitz
 *
 */
public abstract class PriorityRunnable implements Runnable, Comparable<PriorityRunnable> {

	public PriorityRunnable() {
		super();
	}

	@Override
	public int compareTo(PriorityRunnable another) {
		return getPriority() - another.getPriority();
	}

	public abstract void run();
	
	public abstract int getPriority();

}
