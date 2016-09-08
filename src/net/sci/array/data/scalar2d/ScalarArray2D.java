/**
 * 
 */
package net.sci.array.data.scalar2d;

import net.sci.array.data.Array2D;
import net.sci.array.data.ScalarArray;
import net.sci.array.type.Scalar;

/**
 * @author dlegland
 *
 */
public abstract class ScalarArray2D<T extends Scalar> extends Array2D<T> implements ScalarArray<T>
{

	protected ScalarArray2D(int size0, int size1)
	{
		super(size0, size1);
	}

	@Override
	public abstract ScalarArray2D<T> duplicate();
}
