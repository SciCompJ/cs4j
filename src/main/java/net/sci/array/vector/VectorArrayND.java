/**
 * 
 */
package net.sci.array.vector;

import net.sci.array.ArrayND;
import net.sci.array.scalar.Scalar;

/**
 * @author dlegland
 *
 */
public abstract class VectorArrayND<V extends Vector<V,S>, S extends Scalar<S>> extends ArrayND<V> implements VectorArray<V,S>
{
	// =============================================================
	// Constructors

	protected VectorArrayND(int... sizes)
	{
		super(sizes);
	}
}
