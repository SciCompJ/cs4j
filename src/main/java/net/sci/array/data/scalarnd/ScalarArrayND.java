/**
 * 
 */
package net.sci.array.data.scalarnd;

import net.sci.array.data.ArrayND;
import net.sci.array.data.ScalarArray;
import net.sci.array.type.Scalar;

/**
 * @author dlegland
 *
 */
public abstract class ScalarArrayND<T extends Scalar> extends ArrayND<T> implements ScalarArray<T>
{

	protected ScalarArrayND(int[] sizes)
	{
		super(sizes);
	}

}
