/**
 * 
 */
package net.sci.array.impl;

import net.sci.array.Array;
import net.sci.array.Array1D;

/**
 * Concrete implementation of a 1D array containing generic data stored in a
 * linear buffer of Object instances. Elements are casted to and from generic
 * type T when necessary.
 * 
 * @param <T>
 *            the type of elements stored within the array.
 *            
 * @author dlegland
 */
public class BufferedGenericArray1D<T> extends GenericArray1D<T>
{
    // =============================================================
    // Class fields

    /** The buffer containing Array elements */
    Object[] buffer;

    /** initialization value, required for creating new arrays */
    T initValue;
    
    
    // =============================================================
    // Constructors

    /**
     * Creates a new 1D array with the specified size, using the sample element to
     * initialize array.
     * 
     * @param size
     *            the size of the array to create in the first direction
     * @param initValue
     *            the value used to initialize the array
     */
    public BufferedGenericArray1D(int size, T initValue)
    {
        super(size);
        this.buffer = new Object[size];
        this.initValue = initValue;
        fill(initValue);
    }
    
    /**
     * Creates a new 1D array with the specified size, using the specified array
     * to initialize content.
     *
     * The size of the resulting array is defined as the size of the buffer array.
     * 
     * @param buffer
     *            the array containing elements of the new array
     */
    public BufferedGenericArray1D(T[] buffer)
    {
        super(buffer.length);
        this.buffer = buffer;
        this.initValue = buffer[0];
    }

    
    // =============================================================
    // Methods implementing the Array1D interface

    /* (non-Javadoc)
     * @see net.sci.array.data.Array1D#get(int)
     */
    @SuppressWarnings("unchecked")
    @Override
    public T get(int x)
    {
        return (T) this.buffer[x];
    }

    /* (non-Javadoc)
     * @see net.sci.array.data.Array1D#set(int, T)
     */
    @Override
    public void set(int x, T value)
    {
        this.buffer[x] = value;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Array1D<T> duplicate()
    {
        int n = this.buffer.length;
        Object[] newBuffer = new Object[n];
        System.arraycopy(this.buffer, 0, newBuffer, 0, n);
        return new BufferedGenericArray1D<T>((T[]) newBuffer);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public Class<T> elementClass()
    {
        return (Class<T>) initValue.getClass();
    }
    
    @Override
    public Iterator iterator()
    {
        return new Iterator();
    }

    
    // =============================================================
    // Accessors and mutators

    /**
     * Override default behavior to return the initialization value that as used
     * to construct this array.
     * 
     * @return the initialization value that as used to construct this array
     */
    @Override
    public T sampleElement()
    {
        return initValue;
    }


    // =============================================================
    // Iterator class

    class Iterator implements Array.Iterator<T>
    {
        int index = -1;
        
        public Iterator() 
        {
        }
        

        @Override
        public boolean hasNext()
        {
            return this.index < (size0 - 1);
        }

        @SuppressWarnings("unchecked")
        @Override
        public T next()
        {
            this.index++;
            return (T) buffer[index];
        }

        @Override
        public void forward()
        {
            this.index++;
        }

        @SuppressWarnings("unchecked")
        @Override
        public T get()
        {
            return (T) buffer[index];
        }

        @Override
        public void set(T value)
        {
            buffer[index] = value;
        }
    }
}
