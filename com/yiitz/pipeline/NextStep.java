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
	void exec(Object input) {
		R result = execute((P) input);
		if (null != next) {
			next.exec(result);
		}
	}

}
