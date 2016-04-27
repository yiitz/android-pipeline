/**
 * 
 */
package com.yiitz.pipeline;

import java.util.concurrent.Future;

/**
 * @author yiitz
 *
 */
public class Job {
	private Object input;
	private JobStep head;
	private JobStep tail;

	//默认最高优先级
	int priority = Integer.MIN_VALUE;
	
	Future<?> future;
	boolean isCanceled = false;
	
	public void cancelJob(){
		isCanceled = true;
		if (null == future) {
			return;
		}
		future.cancel(true);
		
		future = null;
	}

	/**
	 * 优先级小于0会抢占第二级线程池，导致其他高优先级任务等待，IO时间较长的任务避免小于0
	 * @param priority
	 */
	public void setPriority(int priority) {
		this.priority = priority;
	}

	public Job addFirstSetp(JobStep step, Object input) {
		step.job = this;
		head = step;
		tail = step;
		this.input = input;
		return this;
	}

	public Job addNextStep(JobStep step) {
		step.job = this;
		tail.addNextJob(step);
		tail = step;
		return this;
	}

	public void beginJob() {
		head.exec(input);
		input = null;
	}
}
