/**
 * 
 */
package com.yiitz.pipeline;

/**
 * @author yiitz
 *
 */
public abstract class IoJobStep<P, R> extends JobStep<P, R> {

	@Override
	void exec(final Object input) {
		ConcurrentHelper.runOnIoThread(new Runnable() {

			@Override
			public void run() {
				R result = execute((P) input);
				if (null != next) {
					next.exec(result);
				}
			}
		});
	}

}
