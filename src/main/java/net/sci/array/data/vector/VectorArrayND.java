/**
 * 
 */
package net.sci.array.data.vector;

import net.sci.array.data.ArrayND;
import net.sci.array.data.VectorArray;
import net.sci.array.type.Vector;

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
