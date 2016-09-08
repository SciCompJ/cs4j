/**
 * 
 */
package net.sci.array.data.scalarnd;

import net.sci.array.data.IntArray;
import net.sci.array.type.Int;

/**
 * @author dlegland
 *
 */
public abstract class IntArrayND<T extends Int> extends ScalarArrayND<T> implements IntArray<T>
{

	protected IntArrayND(int[] sizes)
	{
		super(sizes);
	}
}
