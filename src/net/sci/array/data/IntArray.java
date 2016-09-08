/**
 * 
 */
package net.sci.array.data;

import net.sci.array.type.Int;

/**
 * @author dlegland
 *
 */
public interface IntArray<T extends Int> extends ScalarArray<T>
{
	// =============================================================
	// New methods

	/**
	 * Returns the value at the specified position as an integer.
	 * 
	 * @param pos
	 *            the position
	 * @return the integer value
	 */
	public int getInt(int[] pos);
	
	/**
	 * Sets the value at the specified position as an integer.
	 * 
	 * @param pos
	 *            the position
	 * @param value
	 *            the new integer value
	 */
	public void setInt(int[] pos, int value);
	
	
	// =============================================================
	// Specialization of the Array interface

	@Override
	public IntArray<T> newInstance(int... dims);

	@Override
	public IntArray<T> duplicate();

	public default double getValue(int[] pos)
	{
		return getInt(pos);
	}

	public Iterator<T> iterator();	
	

	// =============================================================
	// Inner interface

	public interface Iterator<T extends Int> extends ScalarArray.Iterator<T>
	{
		public int getInt();
		public void setInt(int value);
		
		@Override
		public default double getValue()
		{
			return get().getValue();
		}
		
		@Override
		public default void setValue(double value)
		{
			setInt((int) value);
		}
	}

}
