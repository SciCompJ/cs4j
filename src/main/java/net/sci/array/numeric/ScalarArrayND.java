/**
 * 
 */
package net.sci.array.numeric;

import net.sci.array.ArrayND;

/**
 * Specialization of Array for ND arrays of scalar values.
 * 
 * @param <S>
 *            the type of Scalar.
 * @author dlegland
 *
 */
public abstract class ScalarArrayND<S extends Scalar<S>> extends ArrayND<S> implements ScalarArray<S>
{

	protected ScalarArrayND(int[] sizes)
	{
		super(sizes);
	}

}
