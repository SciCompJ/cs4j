/**
 * 
 */
package net.sci.array.scalar;

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
