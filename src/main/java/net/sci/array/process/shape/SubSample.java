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
public class SubSample extends AlgoStub implements ArrayOperator
{
    /**
     * The sampling step along each dimension. If the array has only one
     * element, it is repeated for each array dimension.
     */
    int[] steps;
    
    /**
     * The starting index along each dimension. If the array has only one
     * element, it is repeated for each array dimension.
     */
    int[] startIndices;
	
    /**
     * Creates a sub-sample operator with a given sampling step.
     * The same sampling step will be applied to each dimension.
     * 
     *  @param step the sampling ratio
     */
    public SubSample(int step)
    {
        this.steps = new int[] {step};
        this.startIndices = new int[] {0};
    }

    /**
     * Creates a sub-sample operator with different sampling ratio depending on
     * the dimension.
     * 
     * @param steps
     *            the sampling step along each dimension
     */
    public SubSample(int[] steps)
    {
        this.steps = steps;
        this.startIndices = repeatValue(0, steps.length);
    }

    /**
     * Creates a sub-sample operator with different sampling ratio depending on
     * the dimension.
     * 
     * @param steps
     *            the sampling step along each dimension
     */
    public SubSample(int[] steps, int[] starts)
    {
        if (steps.length != starts.length)
        {
            throw new RuntimeException("Step array and start array must have same length");
        }
        
        this.steps = steps;
        this.startIndices = starts;
    }

	/**
     * Creates a sub-sampled view of the input array.
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
        
        int nd = array.dimensionality();
        boolean expand = this.steps.length == 1 && nd > 1;
        
        final int[] steps = expand ? repeatValue(this.steps[0], nd) : this.steps;
        final int[] starts = expand ? repeatValue(this.startIndices[0], nd) : this.startIndices;
        
        Function<int[], int[]> mapping = (int[] pos) ->
        {
            int[] pos2 = new int[pos.length];
            for (int d = 0; d < pos.length; d++)
            {
                pos2[d] = pos[d] * steps[d] + starts[d];
            }
            return pos2;
        };
        
        return array.reshapeView(dims, mapping);
    }
    
    /* (non-Javadoc)
	 * @see net.sci.array.ArrayOperator#process(net.sci.array.Array)
	 */
	@Override
	public <T> Array<T> process(Array<T> array)
	{
	    int nd = array.dimensionality();
	    
        checkStepArrayLength(nd);
        int[] steps = ensureArrayLength(this.steps, nd);
        int[] starts = ensureArrayLength(this.startIndices, nd);
	    
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
	            pos2[d] = pos[d] * steps[d] + starts[d];
	        }

	        result.set(pos, array.get(pos2));
	    }
	    
        return result;
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
        checkStepArrayLength(nd);
        
        int[] steps = ensureArrayLength(this.steps, nd);
        int[] starts = ensureArrayLength(this.startIndices, nd);
        
        // compute output array dimensions
        int[] dims2 = new int[nd];
        for (int d = 0; d < nd; d++)
        {
            dims2[d] = (int) Math.floor((dims[d] - starts[d]) / steps[d]);
        }
        
        return dims2;
    }

    private void checkStepArrayLength(int nd)
    {
        // check dimensionality consistency
        if (this.steps.length != nd && this.steps.length != 1)
        {
            throw new IllegalArgumentException(String.format(
                    "input array dimensionality (%d) does not match subsampling ratios number (%d)", 
                    nd, this.steps.length));
        }
    }

    private static final int[] ensureArrayLength(int[] ratios, int nDims)
	{
	    if (ratios.length == nDims)
	    {
	        return ratios;
	    }
	    return repeatValue(ratios[0], nDims);
	}

	private static final int[] repeatValue(int value, int n)
    {
        int[] res = new int[n];
        for (int i = 0; i < n; i++)
        {
            res[i] = value;
        }
        return res;
    }

    @Override
    public boolean canProcess(Array<?> array)
    {
        // if only one sampling factor is specified, it can be applied to any type of array
        if (this.steps.length == 1)
        {
            return true;
        }
        
        // check dimensionality consistency
        return this.steps.length == array.dimensionality();
    }
}
