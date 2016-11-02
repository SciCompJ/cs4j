/**
 * 
 */
package net.sci.array.data;

import net.sci.array.Array;
import net.sci.array.type.Scalar;
import net.sci.array.type.Vector;

/**
 * @author dlegland
 *
 */
public interface VectorArray<V extends Vector<?>> extends Array<V>
{
	// =============================================================
	// Static methods
	
	public static ScalarArray<?> norm(VectorArray<? extends Vector<?>> array)
	{
		FloatArray result = FloatArray.create(array.getSize());
		
		Iterator<? extends Vector<?>> iter1 = array.iterator();
		FloatArray.Iterator iter2 = result.iterator();
		while (iter1.hasNext() && iter2.hasNext())
		{
			double[] values = iter1.next().getValues();
			double norm = 0;
			for (double d : values)
			{
				norm += d * d;
			}
			norm = Math.sqrt(norm);
			
			iter2.forward();
			iter2.setValue(norm);
		}
		
		return result;
	}
	
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
	public VectorArray<V> newInstance(int... dims);
	
	@Override
	public VectorArray<V> duplicate();

	public VectorArray.Iterator<V> iterator();
	
	// =============================================================
	// Inner interface

	public interface Iterator<V extends Vector<? extends Scalar>> extends Array.Iterator<V>
	{
		public default double nextValue()
		{
			forward();
			return getValue();
		}
	}

}
