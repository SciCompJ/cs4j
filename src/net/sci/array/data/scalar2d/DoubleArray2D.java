/**
 * 
 */
package net.sci.array.data.scalar2d;

import net.sci.array.data.DoubleArray;
import net.sci.array.type.Double;

/**
 * @author dlegland
 *
 */
public abstract class DoubleArray2D extends ScalarArray2D<Double> implements DoubleArray
{
	// =============================================================
	// Static methods

	public static final DoubleArray2D create(int size0, int size1)
	{
		return new BufferedDoubleArray2D(size0, size1);
	}
	
	
	// =============================================================
	// Constructor

	/**
	 * @param size0
	 * @param size1
	 */
	public DoubleArray2D(int size0, int size1)
	{
		super(size0, size1);
	}


	// =============================================================
	// Specialization of Array2D 

	/* (non-Javadoc)
	 * @see net.sci.array.data.Array2D#get(int, int)
	 */
	@Override
	public net.sci.array.type.Double get(int x, int y)
	{
		return new Double(getValue(x, y));
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.Array2D#set(int, int, java.lang.Object)
	 */
	@Override
	public void set(int x, int y, net.sci.array.type.Double value)
	{
		setValue(x, y, value.getValue());
	}
	
	
	// =============================================================
	// Specialization of Array 

	@Override
	public DoubleArray newInstance(int... dims)
	{
		return DoubleArray.create(dims);
	}

	@Override
	public abstract DoubleArray2D duplicate();

}
