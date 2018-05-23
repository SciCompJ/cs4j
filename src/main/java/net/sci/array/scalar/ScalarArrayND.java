/**
 * 
 */
package net.sci.array.scalar;

import net.sci.array.ArrayND;

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
