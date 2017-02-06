/**
 * 
 */
package net.sci.array.data.vector;

import net.sci.array.data.Array3D;
import net.sci.array.data.VectorArray;
import net.sci.array.type.Vector;

/**
 * @author dlegland
 *
 */
public abstract class VectorArray3D<V extends Vector<?>> extends Array3D<V> implements VectorArray<V>
{
	// =============================================================
	// Constructors

	protected VectorArray3D(int size0, int size1, int size2)
	{
		super(size0, size1, size2);
	}
	
	// =============================================================
	// New methods

	public abstract double[] getValues(int x, int y, int z);
	
    /**
     * Returns the values at a given location in the specified pre-allocated
     * array.
     * 
     * @param x
     *            the x-position of the vector
     * @param y
     *            the y-position of the vector
     * @param z
     *            the z-position of the vector
     * @param values
     *            the pre-allocated array for storing values
     * @return a reference to the pre-allocated array
     */
    public abstract double[] getValues(int x, int y, int z, double[] values);

    public abstract void setValues(int x, int y, int z, double[] values);
	
	/**
	 * Returns the scalar value for the specified position and the specified
	 * component.
	 * 
	 * @param x
	 *            the x-position of the vector
	 * @param y
	 *            the y-position of the vector
	 * @param z
	 *            the z-position of the vector
	 * @param c
	 *            the component to investigate
	 * @return the value of the given component at the given position
	 */
	public abstract double getValue(int x, int y, int z, int c);
	
	public abstract void setValue(int x, int y, int z, int c, double value);


	// =============================================================
	// Specialization of VectorArray interface
	
	public double[] getValues(int[] pos)
	{
		return getValues(pos[0], pos[1], pos[2]);
	}
	
	public void setValues(int[] pos, double[] values)
	{
		setValues(pos[0], pos[1], pos[2], values);
	}
	

	// =============================================================
	// Specialization of Array3D interface

	/**
	 * Returns the norm of the vector at the given position.
	 * 
	 * @see net.sci.array.data.Array3D#getValue(int, int, int)
	 */
	@Override
	public double getValue(int x, int y, int z)
	{
		double[] values = getValues(x, y, z);
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
	 * @see net.sci.array.data.Array3D#setValue(int, int, int, double)
	 */
	@Override
	public void setValue(int x, int y, int z, double value)
	{
		setValue(x, y, z, 0, value);
		for (int c = 1; c < this.getVectorLength(); c++)
		{
			setValue(x, y, c, 0);
		}
	}


	/* (non-Javadoc)
	 * @see net.sci.array.data.VectorArray#duplicate()
	 */
	@Override
	public abstract VectorArray3D<V> duplicate();

}
