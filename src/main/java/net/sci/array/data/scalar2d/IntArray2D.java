/**
 * 
 */
package net.sci.array.data.scalar2d;

import net.sci.array.data.IntArray;
import net.sci.array.type.Int;

/**
 * @author dlegland
 *
 */
public abstract class IntArray2D<T extends Int> extends ScalarArray2D<T> implements IntArray<T>
{
	// =============================================================
	// Constructor

	/**
	 * Initialize the protected size variables. 
	 * 
	 * @param size0
	 *            the size of the array along the first dimension
	 * @param size1
	 *            the size of the array along the second dimension
	 */
	protected IntArray2D(int size0, int size1)
	{
		super(size0, size1);
	}

	// =============================================================
	// New methods

	public abstract int getInt(int x, int y);
	public abstract void setInt(int x, int y, int value);
	
	// =============================================================
	// Specialization of Array interface

	@Override
	public abstract IntArray2D<T> duplicate();
}
