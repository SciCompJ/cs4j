/**
 * 
 */
package net.sci.array.data.scalarnd;

import net.sci.array.data.Float32Array;
import net.sci.array.type.Float32;

/**
 * @author dlegland
 *
 */
public abstract class Float32ArrayND extends ScalarArrayND<Float32> implements Float32Array
{
	// =============================================================
	// Static factory
	
	public static Float32ArrayND create(int... dims)
	{
		return new BufferedFloat32ArrayND(dims);
	}
	
	
	// =============================================================
	// Constructors
	
	/**
	 * @param sizes
	 */
	protected Float32ArrayND(int[] sizes)
	{
		super(sizes);
	}

	
	// =============================================================
	// Specialization of Array interface
	
	@Override
	public Float32Array newInstance(int... dims)
	{
		return Float32Array.create(dims);
	}
	
}
