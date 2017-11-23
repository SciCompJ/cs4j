/**
 * 
 */
package net.sci.array.data.generic;

import net.sci.array.Array;
import net.sci.array.data.Array3D;
import net.sci.array.data.GenericArray;

/**
 * @author dlegland
 *
 */
public class BufferedGenericArray3D<T> extends Array3D<T> implements GenericArray<T>
{
    // =============================================================
    // Class fields

    /** The buffer containing Array elements */
    Object[] buffer;

    /** initialization value, required for creating new arrays */
    T initValue;
    
    
    // =============================================================
    // Constructors

    public BufferedGenericArray3D(int size0, int size1, int size2, T initValue)
    {
        super(size0, size1, size2);
        this.buffer = new Object[size0 * size1 * size2];
        fill(initValue);
    }
    
    public BufferedGenericArray3D(int size0, int size1, int size2, T[] buffer)
    {
        super(size0, size1, size2);
        this.buffer = buffer;
        this.initValue = buffer[0];
    }

    
    // =============================================================
    // Methods implementing the Array interface

    @SuppressWarnings("unchecked")
    @Override
    public Array3D<T> duplicate()
    {
        int n = this.buffer.length;
        Object[] newBuffer = new Object[n];
        System.arraycopy(this.buffer, 0, newBuffer, 0, n);
        return new BufferedGenericArray3D<T>(this.size0, this.size1, this.size2, (T[]) newBuffer);
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
     * @see net.sci.array.data.Array3D#get(int, int, int)
     */
    @Override
    public T get(int x, int y, int z)
    {
        int index = x + this.size0 * (y + this.size1 * z);
        return (T) this.buffer[index];
    }

    /* (non-Javadoc)
     * @see net.sci.array.data.Array3D#set(int, int, int, T)
     */
    @Override
    public void set(int x, int y, int z, T value)
    {
        int index = x + this.size0 * (y + this.size1 * z);
        this.buffer[index] = value;
    }

    /* (non-Javadoc)
     * @see net.sci.array.data.Array3D#getValue(int, int, int)
     */
    @Override
    public double getValue(int x, int y, int z)
    {
        throw new RuntimeException("Unimplemented operation");
    }

    /* (non-Javadoc)
     * @see net.sci.array.data.Array3D#setValue(int, int, int, double)
     */
    @Override
    public void setValue(int x, int y, int z, double value)
    {
        throw new RuntimeException("Unimplemented operation");
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
            return this.index < (size0 * size1 * size2 - 1);
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

        @Override
        public double nextValue()
        {
            throw new RuntimeException("Unimplemented operation");
        }

        @Override
        public double getValue()
        {
            throw new RuntimeException("Unimplemented operation");
        }

        @Override
        public void setValue(double value)
        {
            throw new RuntimeException("Unimplemented operation");
        }
    }
}
