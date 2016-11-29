/**
 * 
 */
package net.sci.image.data;

/**
 * Identifies a position within a n-dimensional array.
 */
public interface Cursor
{
	/**
	 * @return the dimensionality of this cursor.
	 */
	public int dimensionality();
	
	/**
	 * @return the position as an array of int
	 */
	public int[] getPosition();
	
	/**
	 * @param dim one of the dimension of the underlying array 
	 * @return the position in the specified dimension
	 */
	public int get(int dim);
}
