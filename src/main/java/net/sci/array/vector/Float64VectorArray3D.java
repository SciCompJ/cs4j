/**
 * 
 */
package net.sci.array.vector;

/**
 * @author dlegland
 *
 */
public abstract class Float64VectorArray3D extends VectorArray3D<Float64Vector> implements Float64VectorArray
{
	// =============================================================
	// Static methods

	public static final Float64VectorArray3D create(int size0, int size1, int size2, int sizeV)
	{
		return new BufferedFloat64VectorArray3D(size0, size1, size2, sizeV);
	}
	
	// =============================================================
	// Constructors

	protected Float64VectorArray3D(int size0, int size1, int size2)
	{
		super(size0, size1, size2);
	}
	
	// =============================================================
	// New methods

	public abstract double[] getValues(int x, int y, int z);
	
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
	
    @Override
    public double[] getValues(int[] pos, double[] values)
    {
        return getValues(pos[0], pos[1], pos[2], values);
    }

	public void setValues(int[] pos, double[] values)
	{
		setValues(pos[0], pos[1], pos[2], values);
	}
	

	// =============================================================
	// Specialization of Array interface


	/* (non-Javadoc)
	 * @see net.sci.array.data.VectorArray#duplicate()
	 */
	@Override
	public abstract Float64VectorArray3D duplicate();

}
