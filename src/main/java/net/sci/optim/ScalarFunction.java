/**
 * 
 */
package net.sci.optim;

/**
 * A function of n variables that returns a scalar value.
 *  
 * @author dlegland
 *
 */
public interface ScalarFunction
{
	/**
	 * Evaluate the function at the specified position.
	 */
	public double evaluate(double[] theta);
	
	/**
	 * Public structure used to store together the result of a function
	 * evaluation and the position of evaluation.
	 */
	public class EvaluationResult
	{
		double[] position;
		double value;
		
		public EvaluationResult(double[] position, double value)
		{
			this.position = position;
			this.value = value;
		}
		
		public double[] getPosition()
		{
			return this.position;
		}
		
		public double getValue()
		{
			return this.value;
		}
	}
}
