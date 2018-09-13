/**
 * 
 */
package net.sci.array.generic;

import net.sci.array.Array;
import net.sci.array.Array2D;

/**
 * @author dlegland
 *
 */
public class BufferedGenericArray2D<T> extends GenericArray2D<T>
{
    // =============================================================
    // Class fields

    /** The buffer containing Array elements */
    Object[] buffer;

    /** initialization value, required for creating new arrays */
    T initValue;
    
    
    // =============================================================
    // Constructors

    public BufferedGenericArray2D(int size0, int size1, T initValue)
    {
        super(size0, size1);
        this.buffer = new Object[size0 * size1];
        this.initValue = initValue;
        fill(initValue);
    }
    
    public BufferedGenericArray2D(int size0, int size1, T[] buffer)
    {
        super(size0, size1);
        this.buffer = buffer;
        this.initValue = buffer[0];
    }

    
    // =============================================================
    // Methods implementing the Array interface

    @SuppressWarnings("unchecked")
    @Override
    public Array2D<T> duplicate()
    {
        int n = this.buffer.length;
        Object[] newBuffer = new Object[n];
        System.arraycopy(this.buffer, 0, newBuffer, 0, n);
        return new BufferedGenericArray2D<T>(this.size0, this.size1, (T[]) newBuffer);
    }

	@SuppressWarnings("unchecked")
	@Override
	public Class<T> getDataType()
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

    @SuppressWarnings("unchecked")
    /* (non-Javadoc)
     * @see net.sci.array.data.Array2D#get(int, int)
     */
    @Override
    public T get(int x, int y)
    {
        int index = x + y * this.size0;
        return (T) this.buffer[index];
    }

    /* (non-Javadoc)
     * @see net.sci.array.data.Array2D#set(int, int, T)
     */
    @Override
    public void set(int x, int y, T value)
    {
        int index = x + y * this.size0;
        this.buffer[index] = value;
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
            return this.index < (size0 * size1 - 1);
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
