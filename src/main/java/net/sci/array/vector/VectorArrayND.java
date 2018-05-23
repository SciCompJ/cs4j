/**
 * 
 */
package net.sci.array.vector;

import net.sci.array.ArrayND;

/**
 * @author dlegland
 *
 */
public abstract class VectorArrayND<V extends Vector<?>> extends ArrayND<V> implements VectorArray<V>
{
	// =============================================================
	// Constructors

	protected VectorArrayND(int... sizes)
	{
		super(sizes);
	}
}
