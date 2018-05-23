/**
 * 
 */
package net.sci.array.vector;

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
