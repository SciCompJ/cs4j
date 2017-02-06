/**
 * 
 */
package net.sci.array.data.vector;

import net.sci.array.data.Float32VectorArray;
import net.sci.array.type.Float32Vector;

/**
 * @author dlegland
 *
 */
public abstract class Float32VectorArray2D extends VectorArray2D<Float32Vector> implements Float32VectorArray
{
	// =============================================================
	// Static methods

	public static final Float32VectorArray2D create(int size0, int size1, int sizeV)
	{
		return new BufferedFloat32VectorArray2D(size0, size1, sizeV);
	}
	
	// =============================================================
	// Constructors

	protected Float32VectorArray2D(int size0, int size1)
	{
		super(size0, size1);
	}


	// =============================================================
	// Specialization of VectorArray interface

	public double[] getValues(int[] pos)
	{
		return getValues(pos[0], pos[1]);
	}
	
    @Override
    public double[] getValues(int[] pos, double[] values)
    {
        return getValues(pos[0], pos[1], values);
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
	public abstract Float32VectorArray2D duplicate();

}
