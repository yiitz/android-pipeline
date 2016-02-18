/**
 * 
 */
package com.yiitz.pipeline;

/**
 * @author yiitz
 *
 */
public abstract class JobStep<P, R> {
	protected JobStep<?, ?> next = null;

	JobStep() {
	}

	public abstract R execute(P input);

	abstract void exec(final Object input);

	void addNextJob(JobStep<?, ?> jobSetp) {
		next = jobSetp;
	}
}
