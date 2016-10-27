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

	/**
	 * Returns the set of values corresponding to the array element for the
	 * given position.
	 * 
	 * @param pos
	 *            list of indices in each dimension
	 * @return the set of values corresponding to the array element for the
	 *         given position
	 */
	public double[] getValues(int[] pos);
	
	/**
	 * Sets of values corresponding to the array element for the given position.
	 * 
	 * @param pos
	 *            list of indices in each dimension
	 * @param values
	 *            the new set of values to assign to the array
	 */
	public void setValues(int[] pos, double[] values);
	
	
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
