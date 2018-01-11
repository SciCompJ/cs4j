/**
 * 
 */
package net.sci.array.data.scalar3d;

import net.sci.array.ArrayFactory;
import net.sci.array.data.Array3D;
import net.sci.array.data.ScalarArray;
import net.sci.array.type.Scalar;

/**
 * @author dlegland
 *
 */
public abstract class ScalarArray3D<T extends Scalar> extends Array3D<T> implements ScalarArray<T>
{
    // =============================================================
    // Static methods

    public final static <T extends Scalar> ScalarArray3D<T> wrap(ScalarArray<T> array)
    {
        if (array instanceof ScalarArray3D)
        {
            return (ScalarArray3D<T>) array;
        }
        return new Wrapper<T>(array);

    }

    // =============================================================
    // Constructor


	protected ScalarArray3D(int size0, int size1, int size2)
	{
		super(size0, size1, size2);
	}

	// =============================================================
    // Specialization of the Array interface

	@Override
	public abstract ScalarArray3D<T> duplicate();

	
    // =============================================================
    // Inner Wrapper class

    private static class Wrapper<T extends Scalar> extends ScalarArray3D<T>
    {
        private ScalarArray<T> array;
        
        protected Wrapper(ScalarArray<T> array)
        {
            super(0, 0, 0);
            if (array.dimensionality() < 3)
            {
                throw new IllegalArgumentException("Requires an array with at least three dimensions");
            }
            this.array = array;
            this.size0 = array.getSize(0);
            this.size1 = array.getSize(1);
            this.size2 = array.getSize(2);
        }

        @Override
        public ScalarArray<T> newInstance(int... dims)
        {
            return this.array.newInstance(dims);
        }

        @Override
        public ArrayFactory<T> getFactory()
        {
            return this.array.getFactory();
        }

        @Override
        public T get(int x, int y, int z)
        {
            // return value from specified position
            return this.array.get(new int[]{x, y, z});
        }

        @Override
        public void set(int x, int y, int z, T value)
        {
            // set value at specified position
            this.array.set(new int[]{x, y, z}, value);
        }

        @Override
        public double getValue(int x, int y, int z)
        {
            // return value from specified position
            return this.array.getValue(new int[]{x, y, z});
        }

        @Override
        public void setValue(int x, int y, int z, double value)
        {
            // set value at specified position
            this.array.setValue(new int[]{x, y, z}, value);
        }

        @Override
        public ScalarArray3D<T> duplicate()
        {
            ScalarArray<T> tmp = this.array.newInstance(this.size0, this.size1, this.size2);
            if (!(tmp instanceof ScalarArray3D))
            {
                // ensure result is instance of ScalarArray3D
                tmp = new Wrapper<T>(tmp);
            }
            
            ScalarArray3D<T> result = (ScalarArray3D <T>) tmp;
            
            ScalarArray.Iterator<T> iter1 = this.array.iterator();
            ScalarArray.Iterator<T> iter2 = result.iterator();
            
            // Fill new array with input array
            while(iter1.hasNext() && iter2.hasNext())
            {
                iter2.setNextValue(iter1.nextValue());
            }
            
            return result;
        }
        
        @Override
        public Class<T> getDataType()
        {
            return array.getDataType();
        }

        @Override
        public ScalarArray.Iterator<T> iterator()
        {
            return new Iterator3D();
        }
        
        private class Iterator3D implements ScalarArray.Iterator<T>
        {
            int x = -1;
            int y = 0;
            int z = 0;
            
            public Iterator3D() 
            {
            }
            
            @Override
            public boolean hasNext()
            {
                return this.x < size0 - 1 || this.y < size1 - 1|| this.z < size2 - 1;
            }

            @Override
            public T next()
            {
                forward();
                return Wrapper.this.get(x, y, z);
            }

            @Override
            public void forward()
            {
                this.x++;
                if (this.x == size0)
                {
                    this.x = 0;
                    this.y++;
                    if (this.y == size1)
                    {
                        this.y = 0;
                        this.z++;
                    }
                }
            }

            @Override
            public T get()
            {
                return Wrapper.this.get(x, y, z);
            }

            @Override
            public void set(T value)
            {
                Wrapper.this.set(x, y, z, value);
            }
            
            @Override
            public double nextValue()
            {
                forward();
                return getValue();
            }

            @Override
            public double getValue()
            {
                return Wrapper.this.getValue(x, y, z);
            }

            @Override
            public void setValue(double value)
            {
                Wrapper.this.setValue(x, y, z, value);             
            }
        }
    }

}
