/**
 * 
 */
package net.sci.array;

import java.util.function.Function;

import net.sci.array.scalar.BinaryArray;

/**
 * N-dimensional array with generic type.
 *
 * @see Arrays
 * 
 * @author dlegland
 */
public interface Array<T> extends Iterable<T>, Dimensional
{
    static long countElements(int... dims)
    {
        long n = 1;
        for (int dim : dims) 
            n *= dim;
        return n;
    }
    
    
    // ==================================================
    // Convenience implementations
    
    /**
     * Creates a new array with new dimensions and containing the same elements.
     * 
     * @param newDims
     *            the dimensions of the new array
     * @return a new array with same type and containing the same elements
     */
    public default Array<T> reshape(int... newDims)
    {
        // check dimension consistency
        int n2 = 1;
        for (int dim : newDims)
        {
            n2 *= dim;
        }
        if (n2 != this.elementNumber())
        {
            throw new IllegalArgumentException("The element number should not change after reshape.");
        }
        
        // allocate memory
        Array<T> res = this.newInstance(newDims);
        
        // iterate using a pair of Iterator instances
        Iterator<T> iter1 = this.iterator();
        Iterator<T> iter2 = res.iterator();
        while(iter1.hasNext())
        {
            iter2.setNext(iter1.next());
        }
        
        return res;
    }
   
    
    // ==================================================
    // Interface declaration
	
	/**
	 * Returns the dimensionality of this array, i.e. the number of dimensions.
	 * 
	 * @return the dimensionality of the array
	 */
	public int dimensionality();
	
	/**
	 * Returns the size of this image, as an array of dimensions.
	 * 
	 * @return an array of integer sizes
	 */
	public int[] size();

	/**
	 * Returns the size of the image along the specified dimension, starting
	 * from 0.
	 * 
	 * @param dim
	 *            the dimension, between 0 and dimensionality()-1
	 * @return the size along the specified dimension.
	 */
	public int size(int dim);

	/**
	 * @return the number of elements within this array.
	 */
	public default int elementNumber()
	{
	    int n = 1;
	    for (int dim : size())
	        n *= dim;
	    return n;
	}
	
	/**
	 * Returns the class of the data type stored in this array.
	 * 
	 * @return the class of the data type stored in this array.
	 */
	public Class<T> dataType();
	
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
	public default Array<T> duplicate()
	{
	    Array<T> dup = newInstance(size());
	    for (int[] pos : positions())
	    {
	        dup.set(get(pos), pos);
	    }
	    return dup;
	}
	
    public default Array<T> view(int[] newDims, Function<int[], int[]> coordsMapping)
    {
        return new View<T>(this, newDims, coordsMapping);
    }

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
	public T get(int... pos);

	/**
	 * Sets the value at the given position.
	 * 
     * @param value
     *            the new value for the given position
	 * @param pos
	 *            the position, as an array of indices
	 */
	public void set(T value, int... pos);

	public default Iterable<int[]> positions()
	{
		return new Iterable<int[]>()
		{
			public java.util.Iterator<int[]> iterator()
			{
				return positionIterator();
			}
		};
	}

	/**
	 * Iterates over elements of the array that correspond to a true value in
	 * the mask array.
	 * 
	 * @param mask
	 *            a binary array the same size as this array that specifies the
	 *            elements to iterate
	 * @return the selected elements
	 */
	public default Iterable<T> select(BinaryArray mask)
	{
		// check array dimensions
		if (!Arrays.isSameSize(this, mask))
		{
			throw new IllegalArgumentException("Mask array must have same size as input array");
		}
		
		// create new iterable
		return new Iterable<T>()
		{
			public java.util.Iterator<T> iterator()
			{
				return new java.util.Iterator<T>()
				{
					PositionIterator iter = mask.trueElementPositionIterator();
					
					@Override
					public boolean hasNext()
					{
						return iter.hasNext();
					}

					@Override
					public T next()
					{
						int[] pos = iter.next();
						return Array.this.get(pos);
					}
				};
			}
		};
	}
	
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
    
	/**
     * An array factory, used to create new array instances without knowing a
     * priori the type of the array.
     * 
     * @author dlegland
     *
     * @param <T>
     *            the type of the arrays created by this factory.
     */
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
	
	/**
	 * Iterator over the elements of this array.
	 *  
	 * @author dlegland
	 *
	 * @param <T> the type of the elements stored within this array.
	 */
	public interface Iterator<T> extends java.util.Iterator<T>
	{
		/**
		 * Moves this iterator to the next element, and returns the new value
		 * pointed by the iterator.
		 */
		public default T next()
		{
			forward();
			return get();
		}
		
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
	}
	
    /**
     * Iterator over the element positions in this array. Can be used to design
     * operators based on the neighborhood of each element.
     * 
     * @author dlegland
     *
     */
	public interface PositionIterator extends java.util.Iterator<int[]>
	{
	    public void forward();
	    public int[] get();
	    public int get(int dim);
	    
        /**
         * @param pos
         *            the pre-allocated array for storing current position
         * @return the current position
         */
	    public int[] get(int[] pos);
	    
	}
	
    /**
     * Utility class to create a generic view on an array using arbitrary
     * coordinate mapping.
     *
     * @param <T>
     *            The type of the array, that is kept after computing the view.
     */
    static class View<T> implements Array<T>
    {
        Array<T> array;
        
        int[] newDims;
        
        Function<int[], int[]> coordsMapping;

        /**
         * Creates a generic view on the specified array.
         * 
         * @param array
         *            the array to create a view on.
         * @param newDims
         *            the dimensions of the view.
         * @param coordsMapping
         *            the mapping from coordinate in view to the coordinates in
         *            the original array.
         */
        public View(Array<T> array, int[] newDims, Function<int[], int[]> coordsMapping)
        {
            this.array = array;
            this.newDims = newDims;
            this.coordsMapping = coordsMapping;
        }

        /* (non-Javadoc)
         * @see net.sci.array.Array#dimensionality()
         */
        @Override
        public int dimensionality()
        {
            return newDims.length;
        }

        /* (non-Javadoc)
         * @see net.sci.array.Array#getSize()
         */
        @Override
        public int[] size()
        {
            return newDims;
        }

        /* (non-Javadoc)
         * @see net.sci.array.Array#getSize(int)
         */
        @Override
        public int size(int dim)
        {
            return newDims[dim];
        }

        /* (non-Javadoc)
         * @see net.sci.array.Array#positionIterator()
         */
        @Override
        public PositionIterator positionIterator()
        {
            return new DefaultPositionIterator(newDims);
        }

        @Override
        public Class<T> dataType()
        {
            return array.dataType();
        }

        @Override
        public Array<T> newInstance(int... dims)
        {
            return array.newInstance(dims);
        }

        @Override
        public net.sci.array.Array.Factory<T> getFactory()
        {
            return array.getFactory();
        }

        @Override
        public T get(int... pos)
        {
            return array.get(coordsMapping.apply(pos));
        }

        @Override
        public void set(T value, int... pos)
        {
            array.set(value, coordsMapping.apply(pos));
        }

        @Override
        public net.sci.array.Array.Iterator<T> iterator()
        {
            return new Iterator<T>()
            {
                PositionIterator iter = positionIterator();

                @Override
                public boolean hasNext()
                {
                    return iter.hasNext();
                }

                @Override
                public void forward()
                {
                    iter.forward();
                }

                @Override
                public T next()
                {
                    iter.forward();
                    return View.this.get(iter.get());
                }

                @Override
                public T get()
                {
                    return View.this.get(iter.get());
                }

                @Override
                public void set(T value)
                {
                    View.this.set(value, iter.get());
                }
            };
        }
    }
}
