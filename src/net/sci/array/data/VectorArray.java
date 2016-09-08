/**
 * 
 */
package net.sci.array.data;

import net.sci.array.Array;
import net.sci.array.type.Vector;

/**
 * @author dlegland
 *
 */
public interface VectorArray extends Array<Vector>
{
	// =============================================================
	// New methods
	
	public int getVectorLength();

	// =============================================================
	// Specialization of Array interface

	@Override
	public VectorArray newInstance(int... dims);
	
	@Override
	public VectorArray duplicate();

	public VectorArray.Iterator iterator();
	
	// =============================================================
	// Inner interface

	public interface Iterator extends Array.Iterator<Vector>
	{
		public default double nextValue()
		{
			forward();
			return getValue();
		}
	}

}
