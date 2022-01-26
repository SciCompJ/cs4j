/**
 * 
 */
package net.sci.array.process.shape;

import java.util.function.Function;

import net.sci.algo.AlgoStub;
import net.sci.array.Array;
import net.sci.array.ArrayOperator;

/**
 * Computes a crude sub-sampled (decimated) version of the input array, by
 * retaining only one element over k in each dimension.
 * 
 * @author dlegland
 *
 */
public class SubSampler extends AlgoStub implements ArrayOperator
{
	int[] ratios;
	
    /**
     * Creates a sub-sample operator with a given sampling ratio.
     * The same sampling ratio will be applied to each dimension.
     * 
     *  @param ratio the sampling ratio
     */
    public SubSampler(int ratio)
    {
        this.ratios = new int[] {ratio};
    }

    /**
     * Creates a sub-sample operator with different sampling ratio depending on
     * the dimension.
     * 
     * @param ratios
     *            the sampling ratio along each dimension
     */
    public SubSampler(int[] ratios)
    {
        this.ratios = ratios;
    }

	/**
     * Creates a sub-sampled view on the given array.
     * 
     * @param <T>
     *            the type of the input and of the output array.
     * @param array
     *            the array to down-sample.
     * @return a down-sampled view on the input array.
     */
    public <T> Array<T> createView(Array<T> array)
    {
        int[] dims = outputArraySize(array);
        
        Function<int[], int[]> mapping = (int[] pos) ->
        {
            int[] pos2 = new int[pos.length];
            if (this.ratios.length == 1)
            {
                // use the same ratio for each dimension
                for (int d = 0; d < pos.length; d++)
                {
                    pos2[d] = pos[d] * this.ratios[0];
                }
            }
            else
            {
                for (int d = 0; d < pos.length; d++)
                {
                    pos2[d] = pos[d] * this.ratios[d];
                }
            }
            return pos2;
        };
        
        return array.view(dims, mapping);
    }

    /* (non-Javadoc)
	 * @see net.sci.array.ArrayOperator#process(net.sci.array.Array)
	 */
	@Override
	public <T> Array<T> process(Array<T> array)
	{
	    int nd = array.dimensionality();
	    
        checkRatioLength(nd);
	    int[] ratios = ensureRatioLength(this.ratios, nd);
	    
        // allocate memory
        int[] newDims = outputArraySize(array);
        Array<T> result = array.newInstance(newDims);
        int[] pos2 = new int[nd];
        
	    // copy elements of input array to result
	    Array.PositionIterator iter = result.positionIterator();
	    while(iter.hasNext())
	    {
	        int[] pos = iter.next();
	        for (int d = 0; d < nd; d++)
	        {
	            pos2[d] = pos[d] * ratios[d];
	        }

	        result.set(pos, array.get(pos2));
	    }
	    
        return result;
	}
	
	private int[] ensureRatioLength(int[] ratios, int nDims)
	{
	    if (ratios.length == nDims)
	    {
	        return ratios;
	    }
	    int[] res = new int[nDims];
	    for (int d = 0; d < nDims; d++)
	    {
	        res[d] = ratios[0];
	    }
	    return res;
	}

	@Override
	public boolean canProcess(Array<?> array)
	{
	    // if only one sampling factor is specified, it can be applied to any type of array
        if (this.ratios.length == 1)
        {
            return true;
        }
        
        // check dimensionality consistency
        return this.ratios.length == array.dimensionality();
	}
    
    /**
     * Computes the dimensions of the result array based on the array of
     * sub-sampling factors.
     * 
     * @param array
     *            the array to sub sample
     * @return the dimensions of the sub-sampled array
     */
    public int[] outputArraySize(Array<?> array)
    {
        // input array dimensions
        int[] dims = array.size();
        int nd = dims.length;
        
        // check dimensionality consistency
        checkRatioLength(nd);
        
        // compute output array dimensions
        int[] dims2 = new int[nd];
        if (this.ratios.length == 1)
        {
            // if only one ratio is specified, it is used for all dimensions
            int ratio = this.ratios[0];
            for (int d = 0; d < nd; d++)
            {
                dims2[d] = (int) Math.floor(dims[d] / ratio);
            }
        }
        else
        {   
            // use a different ratio for each dimension
            for (int d = 0; d < nd; d++)
            {
                dims2[d] = (int) Math.floor(dims[d] / this.ratios[d]);
            }
        }
        
        return dims2;
    }
    
    private void checkRatioLength(int nd)
    {
        // check dimensionality consistency
        if (this.ratios.length != nd && this.ratios.length != 1)
        {
            throw new IllegalArgumentException(String.format(
                    "input array dimensionality (%d) does not match subsampling ratios number (%d)", 
                    nd, this.ratios.length));
        }

    }
}
