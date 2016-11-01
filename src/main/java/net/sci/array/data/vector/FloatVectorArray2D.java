/**
 * 
 */
package net.sci.array.data.vector;

import net.sci.array.data.FloatVectorArray;
import net.sci.array.type.FloatVector;

/**
 * @author dlegland
 *
 */
public abstract class FloatVectorArray2D extends VectorArray2D<FloatVector> implements FloatVectorArray
{
	// =============================================================
	// Static methods

	public static final FloatVectorArray2D create(int size0, int size1, int sizeV)
	{
		return new BufferedFloatVectorArray2D(size0, size1, sizeV);
	}
	
	// =============================================================
	// Constructors

	protected FloatVectorArray2D(int size0, int size1)
	{
		super(size0, size1);
	}
	
	// =============================================================
	// New methods

	public abstract double[] getValues(int x, int y);
	
	public abstract void setValues(int x, int y, double[] values);
	
	/**
	 * Returns the scalar value for the specified position and the specified
	 * component.
	 * 
	 * @param x
	 *            the x-position of the vector
	 * @param y
	 *            the y-position of the vector
	 * @param c
	 *            the component to investigate
	 * @return the value of the given component at the given position
	 */
	public abstract double getValue(int x, int y, int c);
	
	public abstract void setValue(int x, int y, int c, double value);


	// =============================================================
	// Specialization of VectorArray interface
	
	public double[] getValues(int[] pos)
	{
		return getValues(pos[0], pos[1]);
	}
	
	public void setValues(int[] pos, double[] values)
	{
		setValues(pos[0], pos[1], values);
	}
	

	// =============================================================
	// Specialization of Array interface


	/* (non-Javadoc)
	 * @see net.sci.array.data.VectorArray#duplicate()
	 */
	@Override
	public abstract FloatVectorArray2D duplicate();

}
