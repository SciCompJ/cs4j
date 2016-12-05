/**
 * 
 */
package net.sci.array;

/**
 * A simple interface for n-dimensional entities, or for concept related to
 * n-dimensional entities (like connectivity, neighborhood...)
 * 
 * @author dlegland
 */
public interface Dimensional
{
	/**
	 * Returns the number of dimensions this entity is living in. For arrays,
	 * this corresponds the number of dimensions of the array. For
	 * connectivities or neighborhood, this corresponds to the number of
	 * dimension of the array this entity can process.
	 * 
	 * @return the number of dimensions of this entity.
	 */
	public int dimensionality();
}
