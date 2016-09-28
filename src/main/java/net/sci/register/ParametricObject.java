/**
 * 
 */
package net.sci.register;

/**
 * A container for a set of parameters stored as an image of double.
 * 
 * @author dlegland
 *
 */
public class ParametricObject
{

	// =============================================================
	// Class fields

	protected double[] parameters;
	
	// TODO: add management of parmeter names
	
	// =============================================================
	// Constructor

	/**
	 * Creates a new instance of the parametric object, using deep copy of the
	 * image.
	 * 
	 */
	public ParametricObject(double[] params)
	{
		this.parameters = new double[params.length];
		for (int i = 0; i < params.length; i++)
		{
			this.parameters[i] = params[i];
		}
	}
	
	
	// =============================================================
	// accessors

	public double[] getParameters()
	{
		return this.parameters;
	}
	
	public int getParameterNumber()
	{
		return this.parameters.length;
	}
	
	public void setParameters(double[] params)
	{
		int n = params.length;
		if (this.parameters.length != n)
		{
			this.parameters = new double[n];
		}
		
		for (int i = 0; i < n; i++)
		{
			this.parameters[i] = params[i];
		}
	}

	
}
