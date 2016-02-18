/**
 * 
 */
package com.yiitz.pipeline;

/**
 * @author yiitz
 *
 */
public class Job {
	private Object input;
	private JobStep head;
	private JobStep tail;

	public Job addFirstSetp(JobStep step, Object input) {
		head = step;
		tail = step;
		this.input = input;
		return this;
	}

	public Job addNextStep(JobStep step) {
		tail.addNextJob(step);
		tail = step;
		return this;
	}

	public void beginJob() {
		head.exec(input);
	}
}
