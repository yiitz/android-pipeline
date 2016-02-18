/**
 * 
 */
package com.yiitz.pipeline;

/**
 * @author yiitz
 *
 */
public abstract class UiJobStep<P, R> extends JobStep<P, R> {

	@Override
	void exec(final Object input) {
		ConcurrentHelper.runOnUiThread(new Runnable() {

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
