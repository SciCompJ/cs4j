/**
 * 
 */
package net.sci.array.impl;

import net.sci.array.PositionIterator;

/**
 * Iterator over the positions of an array.
 * 
 * @author dlegland
 *
 */
public class DefaultPositionIterator implements PositionIterator
{
    /**
     * The dimensions of the array
     */
    int[] sizes;
    
    /**
     * The dimensionality of the array to iterate on.
     */
    int nd;

    /**
     * The current position
     */
    int[] pos;
    
    /**
     * Main constructor
     * 
     * @param sizes
     *            the dimensions of the array
     */
    public DefaultPositionIterator(int[] sizes)
    {
        this.sizes = sizes;
        this.nd = sizes.length;
        this.pos = new int[this.nd];
        this.pos[0] = -1;
        for (int d = 1; d < this.nd; d++)
        {
            this.pos[d] = 0;
        }
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
    
    @Override
    public int[] get(int[] pos)
    {
        System.arraycopy(this.pos, 0, pos, 0, this.pos.length);
        return pos;
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
