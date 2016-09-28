package net.sci.optim;

import static org.junit.Assert.*;

import org.junit.Test;


public class RosenbrockFunctionTest {

	/**
	 * Function value at point (0,0) should equals 1.
	 */
	@Test
	public final void testEvaluate_0_0() {
		ScalarFunction fun = new RosenbrockFunction();
		
		double res = fun.evaluate(new double[]{1, 1});
		assertEquals(0, res, .001);
	}
	
	/**
	 * Function value at point (1,1) should equals 0.
	 */
	@Test
	public final void testEvaluate_1_1() {
		ScalarFunction fun = new RosenbrockFunction();
		
		double res = fun.evaluate(new double[]{1, 1});
		assertEquals(0, res, .001);
	}

}
