/**
 * 
 */
package net.sci.array.data;

import net.sci.array.Array;
import net.sci.array.type.Scalar;

/**
 * @author dlegland
 *
 */
public interface ScalarArray<T extends Scalar> extends Array<T>
{
	// =============================================================
	// Specialization of the Array interface

	@Override
	public ScalarArray<T> newInstance(int... dims);

	@Override
	public ScalarArray<T> duplicate();
	
	public ScalarArray.Iterator<T> iterator();
	

	// =============================================================
	// Inner interface

	public interface Iterator<T extends Scalar> extends Array.Iterator<T>
	{
		public default double nextValue()
		{
			return next().getValue();
		}
	}

}
