/**
 * 
 */
package net.sci.optim;

/**
 * An algorithm for finding minimum of a univariate function.
 * 
 * @author dlegland
 *
 */
public abstract class Optimizer 
{
	/**
	 * The function to minimize.
	 */
	ScalarFunction function;
	
	/**
	 * The current state of the optimizer.
	 */
	double[] params;
	
    /** the current value */
    double value; 

	public abstract ScalarFunction.EvaluationResult startOptimization();
	
	public abstract double[] getFinalResult();
}
