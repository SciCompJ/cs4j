/**
 * 
 */
package net.sci.array.data.vector;

import net.sci.array.data.Float64VectorArray;
import net.sci.array.type.Float64Vector;

/**
 * @author dlegland
 *
 */
public abstract class Float64VectorArrayND extends VectorArrayND<Float64Vector> implements Float64VectorArray
{
	// =============================================================
	// Static methods

	public static final Float64VectorArrayND create(int[] sizes, int sizeV)
	{
		return new BufferedFloat64VectorArrayND(sizes, sizeV);
	}
	
	// =============================================================
	// Constructors

	protected Float64VectorArrayND(int[] sizes)
	{
		super(sizes);
	}
}
