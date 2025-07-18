/**
 * 
 */
package net.sci.array;

import java.util.function.Function;

import net.sci.algo.Algo;
import net.sci.algo.AlgoListener;
import net.sci.array.impl.DefaultPositionIterator;
import net.sci.array.impl.GenericArray;
import net.sci.array.shape.Flip;
import net.sci.array.shape.PermuteDimensions;
import net.sci.array.shape.Reshape;
import net.sci.array.shape.Squeeze;
import net.sci.util.MathUtils;

/**
 * N-dimensional array with generic type.
 *
 * @see Arrays
 *
 * @param <T> the type of elements stored within the array.
 * 
 * @author dlegland
 */
public interface Array<T> extends Iterable<T>, Dimensional
{
    // ==================================================
    // Static factory
    
    /**
     * Creates a new array based on its dimensions and an initialization value
     * that will be used to determine the type of the array.
     * 
     * @param <T>
     *            the type of data contained within the array
     * @param dims
     *            the dimension of the array along each dimension
     * @param initValue
     *            the initialization value
     * @return a new instance of Array
     */
    public static <T> Array<T> create(int[] dims, T initValue)
    {
        return GenericArray.create(dims, initValue);
    }
    
    
    // ==================================================
    // Convenience implementations
    
    /**
     * Creates a new array with new dimensions and containing the same elements.
     * 
     * @see net.sci.array.shape.Reshape
     * 
     * @param newDims
     *            the dimensions of the new array
     * @return a new array with same type and containing the same elements
     */
    public default Array<T> reshape(int... newDims)
    {
        return new Reshape(newDims).process(this);
    }
    
    /**
     * Flips the content of an array along the specified dimension.
     *
     * @see net.sci.array.shape.Flip
     * 
     * @param dim
     *            the dimension to flip along
     * @return the array after flip
     */
    public default Array<T> flip(int dim)
    {
        return new Flip(dim).process(this);
    }
    
    /**
     * Permutes the dimensions of the array.
     * 
     * @see net.sci.array.shape.PermuteDimensions
     * 
     * @param dimOrder
     *            the indices of the dimensions in the new array. Should be a
     *            permutation of the integers between 0 and nd.
     * @return a new array of same type with permuted dimensions.
     */
    public default Array<T> permuteDimensions(int[] dimOrder)
    {
        return new PermuteDimensions(dimOrder).process(this);
    }
    
    /**
     * Removes array dimensions whose size is 1.
     * 
     * @see net.sci.array.shape.Squeeze
     * 
     * @return an array with same type and same number of elements, without any
     *         dimension with size equal to 1.
     */
    public default Array<T> squeeze()
    {
        return new Squeeze().process(this);
    }
    
    
    // ==================================================
    // Interface declaration
    
    /**
     * Creates a new array with same type but with the specified dimensions
     * 
     * @param dims
     *            the size of the new array in each dimension
     * @return a new instance of Array
     */
    public Array<T> newInstance(int... dims);
    
    /**
     * Returns the factory of this array. The factory can be used to create new
     * arrays with the same type, without having to know the type of the array.
     * 
     * @return the factory of this array
     */
    public Factory<T> factory();
    
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
            dup.set(pos, get(pos));
        }
        return dup;
    }
    
    /**
     * Returns a view on this array, by mapping the coordinates of the view to
     * the coordinates of the input array.
     * 
     * @param newDims
     *            the dimensions of the view
     * @param coordsMapping
     *            the mapping between the coordinates of the view and the
     *            coordinates of the input array.
     * @return the view on this array
     */
    public default Array<T> reshapeView(int[] newDims, Function<int[], int[]> coordsMapping)
    {
        return new ReshapeView<T>(this, newDims, coordsMapping);
    }
    
    /**
     * Fills the array with the specified (typed) value.
     * 
     * @param value
     *            an instance of T for filling the array.
     */
    public default void fill(T value)
    {
        Iterator<T> iter = iterator();
        while (iter.hasNext())
        {
            iter.forward();
            iter.set(value);
        }
    }
    
    /**
     * Fills the array by using an utility function that computes the value for
     * each position.
     * 
     * {@snippet lang="java" :
     * // create an empty array of String
     * Array<String> array = GenericArray.create(new int[] { 10, 6 }, " ");
     * // populate the array of strings
     * String[] digits = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J" };
     * array.fill(pos -> digits[pos[0]] + digits[pos[1]]);
     * }
     * 
     * @param fun
     *            the function that computes the value at a position from the
     *            array of position indices.
     */
    public default void fill(Function<int[], T> fun)
    {
        for (int[] pos : this.positions())
        {
            this.set(pos, fun.apply(pos));
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
     * Returns a sample element with the same type as the elements in the array.
     * The returned element is not necessarily stored in the array.
     * 
     * @return a sample element of type T
     */
    public default T sampleElement()
    {
        for (T element : this)
        {
            if (element != null) return element;
        }
        throw new RuntimeException("Input array contains only null values");
    }
    
    /**
     * Returns the class of the elements stored in this array.
     * 
     * @return the class of the elements stored in this array.
     */
    public Class<T> elementClass();
    
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
     * Counts the elements within the array.
     * 
     * @see #size()
     * @see net.sci.util.MathUtils#prod(int...)
     * 
     * @return the number of elements within this array.
     */
    public default long elementCount()
    {
        return MathUtils.prod(size());
    }
    
    /**
     * Returns the dimensionality of this array, i.e. the number of dimensions.
     * 
     * @return the dimensionality of the array
     */
    public int dimensionality();
    
    /**
     * Allows to iterate over positions in array.
     * 
     * <pre>{@code
     * for (int[] pos : array.positions())
     * {
     *     doProcessing(array.get(pos));
     * }
     * }</pre>
     * 
     * @see #iterator()
     * 
     * @return an Iterable over array positions.
     */
    public default Iterable<int[]> positions()
    {
        return new Iterable<int[]>()
        {
            public java.util.Iterator<int[]> iterator()
            {
                return new DefaultPositionIterator(Array.this.size());
            }
        };
    }
    
    /**
     * Checks whether the specified position is inside the bounds of this array.
     * The number of elements of the position must match that of the array, and
     * each index must be comprised between 0 and the size of the array along
     * the corresponding dimension.
     * 
     * @param pos
     *            the position to check
     * @return <code>true</code> if the position corresponds to an element
     *         within the array, or <code>false</code> if the position is
     *         outside array bounds
     */
    public default boolean containsPosition(int[] pos)
    {
        // check dimensionality
        int nd = this.dimensionality();
        if (pos.length != this.dimensionality()) return false;
        
        // check bounds
        for (int d = 0; d < nd; d++)
        {
            if (pos[d] < 0 || pos[d] >= this.size(d)) return false;
        }
        return true;
    }
    
    /**
     * Returns an iterator over the elements of the array, for implementing the
     * Iterable interface.
     * 
     * The Array interface provides a default implementation based on the
     * position iterator.
     */
    public default Iterator<T> iterator()
    {
        return new Iterator<T>()
        {
            int[] dims = Array.this.size();
            PositionIterator iter = new DefaultPositionIterator(dims);
            // keep an array of coordinates to avoid repetitive allocation of array
            int[] pos = new int[dims.length];

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
                return Array.this.get(iter.get(pos));
            }

            @Override
            public T get()
            {
                return Array.this.get(iter.get(pos));
            }

            @Override
            public void set(T value)
            {
                Array.this.set(iter.get(pos), value);
            }
        };
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
        /**
         * Moves this iterator to the next position.
         */
        public void forward();
        
        /**
         * Returns the current position.
         * 
         * @return the current position.
         */
        public int[] get();
        
        /**
         * Returns a specific coordinate from the current position.
         * 
         * @param dim
         *            the dimension, between 0 and dimensionality - 1
         * @return the specified coordinate
         */
        public int get(int dim);
        
        /**
         * Returns the current position in a pre-allocated array.
         * 
         * @param pos
         *            the pre-allocated array for storing current position
         * @return the current position
         */
        public int[] get(int[] pos);
        
    }
    
    /**
     * Utility class for creating a reshape view on an array using arbitrary
     * coordinate mapping.
     *
     * @see Array#reshapeView(int[], Function)
     *
     * @param <T>
     *            The type of the array, that is kept after computing the view.
     */
    static class ReshapeView<T> implements Array<T>
    {
        /** 
         * The array to synchronize with. /*
         */
        protected Array<T> array;
        
        /** 
         * The size of the view. 
         */
        protected int[] newDims;
        
        /**
         * The mapping between view coordinates and inner array coordinates.
         */
        protected Function<int[], int[]> coordsMapping;

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
        public ReshapeView(Array<T> array, int[] newDims, Function<int[], int[]> coordsMapping)
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

        @Override
        public Class<T> elementClass()
        {
            return array.elementClass();
        }

        @Override
        public Array<T> newInstance(int... dims)
        {
            return array.newInstance(dims);
        }

        @Override
        public net.sci.array.Array.Factory<T> factory()
        {
            return array.factory();
        }

        @Override
        public T get(int[] pos)
        {
            return array.get(coordsMapping.apply(pos));
        }

        @Override
        public void set(int[] pos, T value)
        {
            array.set(coordsMapping.apply(pos), value);
        }
    }
    
    
    // ==================================================
    // Implementation of an iterator interface
    
    /**
     * Iterator over the elements of this array.
     * 
     * @author dlegland
     *
     * @param <T>
     *            the type of the elements stored within this array.
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
         * Returns the current value pointed by this iterator.
         * 
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
    public interface Factory<T> extends Algo
    {
        @Override
        public default void addAlgoListener(AlgoListener listener)
        {
        }

        @Override
        public default void removeAlgoListener(AlgoListener listener)
        {
        }

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
}
