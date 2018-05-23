/**
 * 
 */
package net.sci.array.scalar;

/**
 * @author dlegland
 *
 */
public abstract class IntArray3D<T extends Int> extends ScalarArray3D<T> implements IntArray<T>
{

	protected IntArray3D(int size0, int size1, int size2)
	{
		super(size0, size1, size2);
	}

	@Override
	public abstract IntArray3D<T> duplicate();

	public abstract int getInt(int x, int y, int z);
	public abstract void setInt(int x, int y, int z, int value);
}
