/**
 * 
 */
package net.sci.array.data.scalar3d;

import net.sci.array.data.Array3D;
import net.sci.array.data.ScalarArray;
import net.sci.array.type.Scalar;

/**
 * @author dlegland
 *
 */
public abstract class ScalarArray3D<T extends Scalar> extends Array3D<T> implements ScalarArray<T>
{

	protected ScalarArray3D(int size0, int size1, int size2)
	{
		super(size0, size1, size2);
	}

	public abstract ScalarArray3D<T> duplicate();

}
