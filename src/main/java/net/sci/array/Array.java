/**
 * 
 */
package net.sci.array;

/**
 * N-dimensional array with generic type.
 *
 * @see Arrays
 * 
 * @author dlegland
 */
public interface Array<T> extends Iterable<T>, Dimensional
{
    // ==================================================
    // Interface declaration
	
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
	 * @param dim
	 *            the dimension, between 0 and dimensionality()-1
	 * @return the size along the specified dimension.
	 */
	public int getSize(int dim);

	/**
	 * @return the class of the data type stored in this array.
	 */
	public Class<T> getDataType();
	
	/**
	 * Creates a new array with same type but with the specified dimensions
	 * 
	 * @param dims
	 *            the size of the new array in each dimension
	 * @return a new instance of Array
	 */
	public Array<T> newInstance(int... dims);

	/**
	 * Returns the factory of this array.
	 * @return the factory of this array
	 */
	public Factory<T> getFactory();
	
	/**
	 * Creates a new writable array with same size as this array and containing
	 * the same values.
	 *
	 * @return a new writable copy of this array
	 */
	public Array<T> duplicate();
	
	/**
	 * Fills the array with the specified (typed) value.
	 * 
	 * @param value an instance of T for filling the array.
	 */
	public default void fill(T value)
	{
		Iterator<T> iter = iterator();
		while(iter.hasNext())
		{
			iter.forward();
			iter.set(value);
		}
	}
	
	/**
	 * Returns the array element at the given position
	 * 
	 * @param pos
	 *            the position, as an array of indices
	 * @return the element at the given position
	 */
	public T get(int[] pos);

	/**
	 * Sets the value at the given position.
	 * 
	 * @param pos
	 *            the position, as an array of indices
	 * @param value
	 *            the new value for the given position
	 */
	public void set(int[] pos, T value);

	/**
	 * Gets the value at the given position as a numeric double.
	 * @param pos
	 *            the position, as an array of indices
	 * @return the double value at the given position
	 */
	public double getValue(int[] pos);
	
	/**
	 * Sets the value at the given position as a numeric double.
	 * @param pos
	 *            the position, as an array of indices
	 * @param value
	 *            the new value for the given position
	 */
	public void setValue(int[] pos, double value);

	/**
     * Return an instance if PositionIterator that allows to iterate over the
     * positions of a multi-dimensional array.
     * 
     * @return an instance of PositionIterator
     */
	public PositionIterator positionIterator();
	
	/**
	 * Returns an iterator over the elements of the array, for implementing the
	 * Iterable interface.
	 */
	public Iterator<T> iterator();

    
    // ==================================================
    // Declaration of a factory interface
    
	public interface Factory<T>
	{
	    /**
	     * Creates a new array with the specified dimensions, filled with the
	     * specified initial value.
	     * 
	     * @param dims
	     *            the dimensions of the array to be created
	     * @param value
	     *            an instance of the initial value
	     * @return a new instance of Array
	     */
	    public Array<T> create(int[] dims, T value);
	}
	
    // ==================================================
    // Implementation of an iterator interface
	
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
		 * Moves this iterator to the next element and updates the value with
		 * the specified value (optional operation).
		 * 
		 * @param value
		 *            the new value at the next position
		 */
		public default void setNext(T value)
		{
			forward();
			set(value);
		}
		
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
		 * 
		 * @return the next value as a double
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
	
	public interface PositionIterator extends java.util.Iterator<int[]>
	{
	    public void forward();
	    public int[] get();
	    public int get(int dim);
	}
}
