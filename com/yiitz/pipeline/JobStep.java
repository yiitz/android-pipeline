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

	Job job;
	JobStep() {
	}

	public abstract R execute(P input);

	void exec(final Object input){
		execRunnable(new PriorityRunnable() {
			
			@Override
			public void run() {
				if (job.isCanceled) {
					return;
				}
				R result = execute((P) input);
				if (null != next) {
					next.exec(result);
				}
			}
			
			@Override
			public int getPriority() {
				return job.priority;
			}
		});
	}
	
	abstract void execRunnable(PriorityRunnable runnable);

	void addNextJob(JobStep<?, ?> jobSetp) {
		next = jobSetp;
	}
	
	public Job getJob(){
		return job;
	}
}
