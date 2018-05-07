/**
 * 
 */
package net.sci.optim;

/**
 * Sample function used to test optimization algorithms.
 *
 * The Rosenbrock function (also called "banana function" due to its shape) is
 * a function of 2 variables given by equation:
 * 	<pre><code>
 * f(x,y) = (1 - x)^2 + 100*(y - x^2)^2 
 * </code></pre>
 *   
 * The global minimum is given by x = [1 1], for which f = 0.
 * 
 * @author dlegland
 *
 */
public class RosenbrockFunction implements ScalarFunction 
{
	/* (non-Javadoc)
	 * @see net.sci.optim.UnivariateFunction#evaluate(double[])
	 */
	@Override
	public double evaluate(double[] theta) 
	{
		double x = theta[0];
		double y = theta[1];
		
		double tmp = y - x * x;
		double f = (1 - x) * (1 - x) + 100 * tmp * tmp;

		return f;
	}

}
