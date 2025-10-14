/**
 * 
 */
package net.sci.array.impl;

import net.sci.array.PositionIterator;

/**
 * Iterator over the positions of an array that starts from the last element,
 * decrease coordinates in lexicographic order, and terminates by first element
 * in array.
 * 
 * @author dlegland
 *
 */
public class ReverseOrderPositionIterator implements PositionIterator
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
     * The position of the next element that will be returned.
     * Initialized at position <code>[max0-1][max1-1]...[maxN-1]<code>.
     * When value is [0][0]...[0], there is no more next element.
     */
    int[] pos;
    
    /**
     * Main constructor
     * 
     * @param sizes
     *            the dimensions of the array
     */
    public ReverseOrderPositionIterator(int[] sizes)
    {
        this.sizes = sizes;
        this.nd = sizes.length;
        
        // initializes position
        this.pos = new int[this.nd];
        for (int d = 0; d < this.nd; d++)
        {
            this.pos[d] = this.sizes[d] - 1;
        }
    }
    
    @Override
    public void forward()
    {
        processDim(0);
    }
    
    private void processDim(int d)
    {
        this.pos[d]--;
        if (this.pos[d] == -1)
        {
            this.pos[d] = this.sizes[d] - 1;
            if (d < nd - 1)
                processDim(d + 1);
            else
            {
                // setup the 'finish' state
                this.pos[0] = -1;
            }
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
        return this.pos[0] > -1;
//        for (int d = 0; d < nd; d++)
//        {
//            if (this.pos[d] != 0)
//                return true;
//        }
//        return false;
    }
    
    @Override
    public int[] next()
    {
        int[] pos = get();
        forward();
        return pos;
    }
}
