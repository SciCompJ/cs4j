/**
 * 
 */
package net.sci.array.process.shape;

import net.sci.array.Array;
import net.sci.array.ArrayOperator;
import net.sci.array.Cursor;
import net.sci.array.CursorIterator;

/**
 * Compute a crude down-sampled (decimated) version of the input array, by
 * retaining only one element over k in each dimension.
 * 
 * @author dlegland
 *
 */
public class Downsampler implements ArrayOperator
{
	int[] ratios;
	
    /**
     * Create a resampler operator with a given sampling ratio. 
     */
    public Downsampler(int ratio)
    {
        this.ratios = new int[] {ratio};
    }

    /**
     * Create a resampler operator with different sampling ratio depending on the dimension 
     */
    public Downsampler(int[] ratios)
    {
        this.ratios = ratios;
    }

	/* (non-Javadoc)
	 * @see net.sci.array.ArrayOperator#process(net.sci.array.Array)
	 */
	@Override
	public <T> Array<?> process(Array<T> array)
	{
	    int[] dims = array.getSize();
	    int nd = dims.length;
        int[] newDims = new int[nd];
	    
        // ensure we have as many ratios as the number of dimensions
        int[] k = new int[nd];
	    if (this.ratios.length == 1)
	    {
	        // Same ratio for all dimensions
            for (int d = 0; d < nd; d++)
            {
                k[d] = ratios[0]; 
            }
	    }
	    else
	    {
            // Can have different ratios depending on dimensions
	        for (int d = 0; d < nd; d++)
	        {
	            k[d] = ratios[d]; 
	        }
	    }
	    
        // Compute dimension of result array
        for (int d = 0; d < nd; d++)
        {
            newDims[d] = dims[d] / k[d]; 
        }
	    
	    // allocate memory
	    Array<T> result = array.newInstance(newDims);
	    
	    int[] pos2 = new int[nd];
	    
	    // copy elements of input array to result
	    CursorIterator<? extends Cursor> iter = result.cursorIterator();
	    while(iter.hasNext())
	    {
	        Cursor c = iter.next();
	        int[] pos = c.getPosition();
	        for (int d = 0; d < nd; d++)
	        {
	            pos2[d] = pos[d] * k[d];
	        }
	        
	        result.set(pos, array.get(pos2));
	    }
        
        return result;
	}

	@Override
	public boolean canProcess(Array<?> array)
	{
        return true;
	}
	
}