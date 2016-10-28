/**
 * 
 */
package net.sci.array.data.vector;

import net.sci.array.data.Array2D;
import net.sci.array.data.VectorArray;
import net.sci.array.type.Vector;

/**
 * @author dlegland
 *
 */
public abstract class VectorArray2D<V extends Vector<?>> extends Array2D<V> implements VectorArray<V>
{
	// =============================================================
	// Constructors

	protected VectorArray2D(int size0, int size1)
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

	/**
	 * Returns the norm of the vector at the given position.
	 * 
	 * @see net.sci.array.data.Array2D#getValue(int, int)
	 */
	@Override
	public double getValue(int x, int y)
	{
		double[] values = getValues(x, y);
		double sum = 0;
		for (double v : values)
		{
			sum += v * v;
		}
		return Math.sqrt(sum);
	}

	/**
	 * Changes the value of the vector at given position, by setting the first
	 * component and clearing the others.
	 * 
	 * @see net.sci.array.data.Array2D#setValue(int, int, double)
	 */
	@Override
	public void setValue(int x, int y, double value)
	{
		setValue(x, y, 0, value);
		for (int c = 1; c < this.getVectorLength(); c++)
		{
			setValue(x, y, c, 0);
		}
	}


	/* (non-Javadoc)
	 * @see net.sci.array.data.VectorArray#duplicate()
	 */
	@Override
	public abstract VectorArray2D<V> duplicate();

}
