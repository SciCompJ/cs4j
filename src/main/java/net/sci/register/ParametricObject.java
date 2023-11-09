/**
 * 
 */
package net.sci.register;

/**
 * A container for a set of parameters stored as an array of double values.
 * 
 * @author dlegland
 *
 */
public class ParametricObject
{
	// =============================================================
	// Class fields

    /** The array containing values of parameter vector */
	protected double[] parameters;
	
	
	// =============================================================
	// Constructors

    /**
     * Creates a new instance of the parametric object, using a new parameter
     * array with the specified length.
     * 
     */
    public ParametricObject(int nParams)
    {
        this.parameters = new double[nParams];
    }
    
    /**
     * Creates a new instance of the parametric object, using deep copy of the
     * specified parameter array.
     * 
     */
    public ParametricObject(double[] params)
    {
        this(params.length);
        // copy array values
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
	
	public int parameterCount()
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
	
    public double getParameter(int index)
    {
        return this.parameters[index];
    }
    
    public void setParameter(int index, double value)
    {
        this.parameters[index] = value;
    }
}
