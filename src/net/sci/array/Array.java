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
public interface Array<T> extends Iterable<T>, Positionable<T>
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
		return new Cursor(getSize());
	}

	/**
	 * Returns an array iterator, for implementing the Iterable interface.
	 */
	// TODO: decide whether one should iterate over values, or over positions...
	public Iterator<T> iterator();

	public interface Iterator<T> extends java.util.Iterator<T>
	{
		public T next();
		
		public void forward();
		public T get();
		
		/**
		 * Returns the next value as a double.
		 */
		public double nextValue();
	
		/**
		 * @return the value at the current iterator position as a double value
		 */
		public double getValue();
		
		/**
		 * Changes the value of the array at the current iterator position.
		 * @param value the new value
		 */
		public void setValue(double value);		
	}
	
	/**
	 * Iterates over the positions within the array.
	 * 
	 * @author dlegland
	 *
	 */
	//TODO: move class out of Array
	public class Cursor implements Positionable.Cursor
	{
		int[] sizes;
		int[] pos;
		int nd;

		protected Cursor(int[] sizes)
		{
			this.sizes = sizes;
			this.nd = sizes.length;
			this.pos = new int[this.nd];
			for (int d = 0; d < this.nd - 1; d++)
			{
				this.pos[d] = sizes[d] - 1;
			}
			this.pos[this.nd - 2] = -1;
		}
		
		public int[] getPosition()
		{
			int[] res = new int[nd];
			System.arraycopy(this.pos, 0, res, 0, nd);
			return res;
		}
		
		public boolean hasNext()
		{
			for (int d = 0; d < nd; d++)
			{
				if (this.pos[d] < sizes[d] - 1)
					return true;
			}
			return false;
		}
		
		public void forward()
		{
			incrementDim(0);
		}
		
		private void incrementDim(int d)
		{
			this.pos[d]++;
			if (this.pos[d] == sizes[d] && d < nd - 1)
			{
				this.pos[d] = 0;
				incrementDim(d + 1);
			}
		}
	}
}
