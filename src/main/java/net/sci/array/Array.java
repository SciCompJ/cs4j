/**
 * 
 */
package net.sci.array;

/**
 * N-dimensional array with generic type.
 * 
 * @author dlegland
 *
 */
public interface Array<T> extends Iterable<T>
{
	/**
	 * Returns the dimensionality of this array, i.e. the number of dimensions.
	 * 
	 * @return the dimensionality of the array
	 */
	public int dimensionality();
	
	/**
	 * Returns the size of this image, as an array of dimension.
	 * 
	 * @return an array of integer sizes
	 */
	public int[] getSize();

	/**
	 * Returns the size of the image along the specified dimension, starting
	 * from 0.
	 * 
	 * @return the size along the specified dimension.
	 */
	public int getSize(int dim);

	/**
	 * Creates a new array with same type but with the specified dimensions
	 * 
	 * @param dims
	 *            the size of the new array in each dimension
	 * @return a new instance of Array
	 */
	public Array<T> newInstance(int... dims);

	/**
	 * Creates a new writable array with same size and same content.
	 * @return a new writable copy of this array
	 */
	public Array<T> duplicate();
	
	/**
	 * Returns the array element at the given position
	 * 
	 * @param pos
	 *            the position, as an array of indices
	 * @return the element at the given position
	 */
	public T get(int[]pos);

	/**
	 * Sets the value at the given position.
	 * @param pos
	 * @param value
	 */
	public void set(int[]pos, T value);

	/**
	 * Gets the value at the given position as a numeric double.
	 * @param position
	 * @return
	 */
	public double getValue(int[] position);
	
	/**
	 * Sets the value at the given position as a numeric double.
	 * @param position
	 * @return
	 */
	public void setValue(int[] position, double value);

	public default Cursor getCursor()
	{
		return new Cursor(this.getSize());
	}

	/**
	 * Returns an iterator over the elements of the array, for implementing the
	 * Iterable interface.
	 */
	public Iterator<T> iterator();

	public interface Iterator<T> extends java.util.Iterator<T>
	{
		/**
		 * Moves this iterator to the next element, and returns the new value
		 * pointed by the iterator.
		 */
		public T next();
		
		/**
		 * Moves this iterator to the next element.
		 */
		public void forward();
		
		/**
		 * @return the current value pointed by this iterator
		 */
		public T get();
		
		/**
		 * Updates the array element pointed by this iterator with the specified
		 * value (optional operation).
		 * 
		 * @param value
		 *            the new value to be set in the array.
		 */
		public void set(T value);
		
		/**
		 * Returns the next value as a double.
		 */
		public double nextValue();
	
		/**
		 * @return the value at the current iterator position as a double value
		 */
		public double getValue();
		
		/**
		 * Changes the value of the array at the current iterator position
		 * (optional operation).
		 * 
		 * @param value
		 *            the new value
		 */
		public void setValue(double value);		
	}
}
