/**
 * 
 */
package net.sci.array.generic;

import net.sci.array.Array;
import net.sci.array.Array3D;
import net.sci.util.MathUtils;

/**
 * @author dlegland
 *
 */
public class BufferedGenericArray3D<T> extends GenericArray3D<T>
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
        
        // check validity of input size array
        long elCount = MathUtils.prod(size0, size1, size2);
        if (elCount > Integer.MAX_VALUE - 8)
        {
            throw new IllegalArgumentException("Total element count is larger than maximal size for java arays");
        }
        
        // allocate buffer
        this.buffer = new Object[size0 * size1 * size2];
        this.initValue = initValue;
        
        // ensure buffer is initialized
        fill(initValue);
    }
    
    public BufferedGenericArray3D(int size0, int size1, int size2, T[] buffer)
    {
        super(size0, size1, size2);
        if (buffer.length < MathUtils.prod(size0, size1, size2))
        {
            throw new IllegalArgumentException("Buffer size does not match image dimensions");
        }
        this.buffer = buffer;
        this.initValue = buffer[0];
    }
    

    // =============================================================
    // Specialization of the Array3D

    @Override
    public GenericArray2D<T> slice(int sliceIndex)
    {
        return new SliceView(sliceIndex);
    }

    @Override
    public Iterable<? extends GenericArray2D<T>> slices()
    {
        return new Iterable<GenericArray2D<T>>()
        {
            @Override
            public java.util.Iterator<GenericArray2D<T>> iterator()
            {
                return new SliceIterator();
            }
        };
    }

    /* (non-Javadoc)
     * @see net.sci.array.data.Array3D#get(int, int, int)
     */
    @SuppressWarnings("unchecked")
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

    @Override
    public java.util.Iterator<? extends GenericArray2D<T>> sliceIterator()
    {
        return new SliceIterator();
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
	public Class<T> dataType()
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
    public T get(int[] pos)
    {
        int index = pos[0] + this.size0 * (pos[1] + this.size1 * pos[2]);
        return (T) this.buffer[index];
    }

    /* (non-Javadoc)
     * @see net.sci.array.data.Array3D#set(int, int, int, T)
     */
    @Override
    public void set(int[] pos, T value)
    {
        int index = pos[0] + this.size0 * (pos[1] + this.size1 * pos[2]);
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
    }
    
    // =============================================================
    // Inner classes for Array3D
    
    private class SliceView extends GenericArray2D<T>
    {
        int sliceIndex;
        
        protected SliceView(int slice)
        {
            super(BufferedGenericArray3D.this.size0, BufferedGenericArray3D.this.size1);
            if (slice < 0 || slice >= BufferedGenericArray3D.this.size2)
            {
                throw new IllegalArgumentException(String.format(
                        "Slice index %d must be comprised between 0 and %d", slice, BufferedGenericArray3D.this.size2));
            }
            this.sliceIndex = slice;
        }
    
        
        @Override
        public T get(int x, int y)
        {
            return BufferedGenericArray3D.this.get(x, y, sliceIndex);
        }


        @Override
        public void set(int x, int y, T value)
        {
            BufferedGenericArray3D.this.set(x, y, sliceIndex, value);
        }

        @Override
        public T get(int[] pos)
        {
            return BufferedGenericArray3D.this.get(pos[0], pos[1], sliceIndex);
        }

        @Override
        public void set(int[] pos, T value)
        {
            BufferedGenericArray3D.this.set(pos[0], pos[1], sliceIndex, value);
        }

        @Override
        public GenericArray2D<T> duplicate()
        {
            // allocate
            GenericArray2D<T> res = (GenericArray2D<T>) GenericArray2D.create(size0, size1, BufferedGenericArray3D.this.initValue);
            
            // fill values
            for (int y = 0; y < size1; y++)
            {
                for (int x = 0; x < size0; x++)
                {
                    res.set(x, y, BufferedGenericArray3D.this.get(x, y, sliceIndex));
                }
            }
            
            // return
            return res;
        }

        @Override
        public Class<T> dataType()
        {
            return BufferedGenericArray3D.this.dataType();
        }
    }

    private class SliceIterator implements java.util.Iterator<GenericArray2D<T>> 
    {
        int sliceIndex = 0;
    
        @Override
        public boolean hasNext()
        {
            return sliceIndex < BufferedGenericArray3D.this.size2;
        }
    
        @Override
        public GenericArray2D<T> next()
        {
            return new SliceView(sliceIndex++);
        }
    }
}
