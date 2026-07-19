/**
 * 
 */
package net.sci.array.numeric;

/**
 * @author dlegland
 *
 */
@Deprecated
public abstract class IntArrayND<I extends Int<I>> extends ScalarArrayND<I> implements IntArray<I>
{

	protected IntArrayND(int[] sizes)
	{
		super(sizes);
	}
}
