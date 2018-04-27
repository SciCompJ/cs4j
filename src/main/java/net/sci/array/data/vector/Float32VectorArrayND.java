/**
 * 
 */
package net.sci.array.data.vector;

import net.sci.array.data.Float32VectorArray;
import net.sci.array.type.Float32Vector;

/**
 * @author dlegland
 *
 */
public abstract class Float32VectorArrayND extends VectorArrayND<Float32Vector> implements Float32VectorArray
{
	// =============================================================
	// Static methods

	public static final Float32VectorArrayND create(int[] sizes, int sizeV)
	{
		return new BufferedFloat32VectorArrayND(sizes, sizeV);
	}
	
	// =============================================================
	// Constructors

	protected Float32VectorArrayND(int[] sizes)
	{
		super(sizes);
	}
}
