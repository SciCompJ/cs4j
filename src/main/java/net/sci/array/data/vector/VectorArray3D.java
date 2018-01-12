/**
 * 
 */
package net.sci.array.data.vector;

import net.sci.array.ArrayFactory;
import net.sci.array.data.Array3D;
import net.sci.array.data.VectorArray;
import net.sci.array.type.Vector;

/**
 * @author dlegland
 *
 */
public abstract class VectorArray3D<V extends Vector<?>> extends Array3D<V> implements VectorArray<V>
{
    // =============================================================
    // Static methods

    public final static <T extends Vector<?>> VectorArray3D<T> wrap(VectorArray<T> array)
    {
        if (array instanceof VectorArray3D)
        {
            return (VectorArray3D<T>) array;
        }
        return new Wrapper<T>(array);
    }

	// =============================================================
	// Constructors

	protected VectorArray3D(int size0, int size1, int size2)
	{
		super(size0, size1, size2);
	}
	
	// =============================================================
	// New methods

	public abstract double[] getValues(int x, int y, int z);
	
    /**
     * Returns the values at a given location in the specified pre-allocated
     * array.
     * 
     * @param x
     *            the x-position of the vector
     * @param y
     *            the y-position of the vector
     * @param z
     *            the z-position of the vector
     * @param values
     *            the pre-allocated array for storing values
     * @return a reference to the pre-allocated array
     */
    public abstract double[] getValues(int x, int y, int z, double[] values);

    public abstract void setValues(int x, int y, int z, double[] values);
	
	/**
	 * Returns the scalar value for the specified position and the specified
	 * component.
	 * 
	 * @param x
	 *            the x-position of the vector
	 * @param y
	 *            the y-position of the vector
	 * @param z
	 *            the z-position of the vector
	 * @param c
	 *            the component to investigate
	 * @return the value of the given component at the given position
	 */
	public abstract double getValue(int x, int y, int z, int c);
	
	public abstract void setValue(int x, int y, int z, int c, double value);


	// =============================================================
	// Specialization of VectorArray interface
	
	public double[] getValues(int[] pos)
	{
		return getValues(pos[0], pos[1], pos[2]);
	}
	
    @Override
    public double[] getValues(int[] pos, double[] values)
    {
        return getValues(pos[0], pos[1], pos[2], values);
    }

	public void setValues(int[] pos, double[] values)
	{
		setValues(pos[0], pos[1], pos[2], values);
	}
	

	// =============================================================
	// Specialization of Array3D interface

	/**
	 * Returns the norm of the vector at the given position.
	 * 
	 * @see net.sci.array.data.Array3D#getValue(int, int, int)
	 */
	@Override
	public double getValue(int x, int y, int z)
	{
		double[] values = getValues(x, y, z);
		double sum = 0;
		for (double v : values)
		{
			sum += v * v;
		}
		return Math.sqrt(sum);
	}

	/**
	 * Changes the value of the vector at given position, by setting the first
	 * component and clearing the others.
	 * 
	 * @see net.sci.array.data.Array3D#setValue(int, int, int, double)
	 */
	@Override
	public void setValue(int x, int y, int z, double value)
	{
		setValue(x, y, z, 0, value);
		for (int c = 1; c < this.getVectorLength(); c++)
		{
			setValue(x, y, c, 0);
		}
	}


	/* (non-Javadoc)
	 * @see net.sci.array.data.VectorArray#duplicate()
	 */
	@Override
	public abstract VectorArray3D<V> duplicate();

	
    // =============================================================
    // Inner Wrapper class

    private static class Wrapper<T extends Vector<?>> extends VectorArray3D<T>
    {
        private VectorArray<T> array;
        
        protected Wrapper(VectorArray<T> array)
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
        public VectorArray<T> newInstance(int... dims)
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
        public VectorArray3D<T> duplicate()
        {
            // TODO: implement in VectorArray3D
            VectorArray<T> tmp = this.array.newInstance(this.size0, this.size1, this.size2);
            if (!(tmp instanceof VectorArray3D))
            {
                throw new RuntimeException("Can not create VectorArray3D instance from " + this.array.getClass().getName() + " class.");
            }
            
            VectorArray3D<T> result = (VectorArray3D <T>) tmp;
            
            VectorArray.Iterator<T> iter1 = array.iterator();
            VectorArray.Iterator<T> iter2 = result.iterator();
            while (iter1.hasNext())
            {
                iter2.setNext(iter1.next());
            }

            return result;
        }
        
        @Override
        public Class<T> getDataType()
        {
            return array.getDataType();
        }

        @Override
        public VectorArray.Iterator<T> iterator()
        {
            return array.iterator();
        }

        @Override
        public int getVectorLength()
        {
            return array.getVectorLength();
        }

        @Override
        public double[] getValues(int x, int y, int z)
        {
            return array.getValues(new int[] {x, y, z});
        }

        @Override
        public double[] getValues(int x, int y, int z, double[] values)
        {
            return getValues(new int[] {x, y, z}, values);
        }

        @Override
        public void setValues(int x, int y, int z, double[] values)
        {
            setValues(new int[] {x, y, z}, values);
        }

        @Override
        public double getValue(int x, int y, int z, int c)
        {
            return getValues(new int[] {x, y, z})[c];
        }

        @Override
        public void setValue(int x, int y, int z, int c, double value)
        {
            int[] pos = new int[] {x, y, z};
            double[] values = array.getValues(pos);
            values[c] = value;
            array.setValues(pos, values);
        }
    }
}
