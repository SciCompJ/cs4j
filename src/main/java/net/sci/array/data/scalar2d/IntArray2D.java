/**
 * 
 */
package net.sci.array.data.scalar2d;

import java.io.PrintStream;
import java.util.Locale;

import net.sci.array.data.IntArray;
import net.sci.array.type.Int;

/**
 * @author dlegland
 *
 */
public abstract class IntArray2D<T extends Int> extends ScalarArray2D<T> implements IntArray<T>
{
	// =============================================================
	// Constructor

	/**
	 * Initialize the protected size variables. 
	 * 
	 * @param size0
	 *            the size of the array along the first dimension
	 * @param size1
	 *            the size of the array along the second dimension
	 */
	protected IntArray2D(int size0, int size1)
	{
		super(size0, size1);
	}

	// =============================================================
	// Methods specific to IntArray2D

	/**
	 * Prints the content of this array on the specified stream.
	 * 
	 * @param stream
	 *            the stream to print on.
	 */
	public void print(PrintStream stream)
	{
		for (int y = 0; y < this.size1; y++)
		{
			for (int x = 0; x < this.size0; x++)
			{
				System.out.print(String.format(Locale.ENGLISH, " %3d", getInt(x, y)));
			}
			System.out.println();
		}
	}

	// =============================================================
	// New methods

	public abstract int getInt(int x, int y);
	public abstract void setInt(int x, int y, int value);
	
	// =============================================================
	// Specialization of Array interface

	@Override
	public abstract IntArray2D<T> duplicate();
}
