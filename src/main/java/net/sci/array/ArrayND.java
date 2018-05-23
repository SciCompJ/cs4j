/**
 * 
 */
package net.sci.array;

/**
 * Base implementation for arrays of any dimensionality.
 * 
 * @author dlegland
 */
public abstract class ArrayND<T> implements Array<T>
{
    // =============================================================
    // Static methods

    protected static int cumProd(int[] dims)
    {
        int prod = 1;
        for (int d : dims)
        {
            prod *= d;
        }
        return prod;
    }
    
	// =============================================================
	// Class fields

    /**
     * The list of dimensions of this array.
     */
	protected int[] sizes;
	
	
	// =============================================================
	// Constructors

	protected ArrayND(int... sizes)
	{
		this.sizes = sizes;
	}


	// =============================================================
	// Utility methods

	/**
	 * Computes the number of array elements.
	 * @return the number of array elements ("pixels")
	 */
	protected int elementNumber()
	{
		int number = 1;
		for (int d = 0; d < this.sizes.length; d++)
		{
			number *= this.sizes[d];
		}
		return number;
	}
	
	/**
	 * Converts a list of indices along each dimension into a linear index.
	 * 
	 * @param inds
	 *            the list of indices for each dimension
	 * @return a linear index
	 */
	protected int subsToInd(int[] inds)
	{
		int index = 0;
		int offset = 1;
		for (int d = 0; d < inds.length; d++)
		{
			index += inds[d] * offset;
			offset *= this.sizes[d];
		}
		return index;
	}


	// =============================================================
	// Specialization of the Array interface
	
	/* (non-Javadoc)
	 * @see net.sci.array.Array#getDimension()
	 */
	@Override
	public int dimensionality()
	{
		return this.sizes.length;
	}

	/* (non-Javadoc)
	 * @see net.sci.array.Array#getSize()
	 */
	@Override
	public int[] getSize()
	{
		int[] res = new int[this.sizes.length];
		for (int d=0; d < this.sizes.length; d++)
		{
			res[d] = this.sizes[d];
		}
		return res;
	}

	/* (non-Javadoc)
	 * @see net.sci.array.Array#getSize(int)
	 */
	@Override
	public int getSize(int dim)
	{
		return this.sizes[dim];
	}
	
	public PositionIterator positionIterator()
	{
	    return new PositionIteratorND();
	}

    /**
     * Iterator over the positions of an array.
     * 
     * @author dlegland
     *
     */
    private class PositionIteratorND implements PositionIterator
    {
//        int[] sizes;
        int[] pos;
        int nd;

        public PositionIteratorND()
        {
            this.nd = sizes.length;
            this.pos = new int[this.nd];
            for (int d = 0; d < this.nd - 1; d++)
            {
                this.pos[d] = sizes[d] - 1;
            }
            this.pos[this.nd - 2] = -1;
        }
        
        @Override
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
        
        @Override
        public int[] get()
        {
            int[] res = new int[nd];
            System.arraycopy(this.pos, 0, res, 0, nd);
            return res;
        }
        
        public int get(int dim)
        {
            return pos[dim];
        }
        
        @Override
        public boolean hasNext()
        {
            for (int d = 0; d < nd; d++)
            {
                if (this.pos[d] < sizes[d] - 1)
                    return true;
            }
            return false;
        }
        
        @Override
        public int[] next()
        {
            forward();
            return get();
        }
    }
}
