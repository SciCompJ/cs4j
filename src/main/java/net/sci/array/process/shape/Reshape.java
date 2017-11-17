/**
 * 
 */
package net.sci.array.process.shape;

import net.sci.array.Array;
import net.sci.array.ArrayOperator;

/**
 * Reshape the dimension of an array, initialized with values of input array.
 * 
 * The product of dimensions of both arrays should be the same.
 * 
 * @author dlegland
 *
 */
public class Reshape implements ArrayOperator
{
	int[] newDims;
	
	/**
	 * Create a reshape operator. 
	 */
	public Reshape(int[] newDims)
	{
	    this.newDims = newDims;
	}

	/* (non-Javadoc)
	 * @see net.sci.array.ArrayOperator#process(net.sci.array.Array)
	 */
	@Override
	public <T> Array<?> process(Array<T> array)
	{
	    // compute element number of input array
	    int prodDims = cumProd(array.getSize());
	    
        // compute element number of output array
	    int prodDims2 = cumProd(newDims);
	    
	    // check element numbers are the same
	    if (prodDims != prodDims2)
	    {
	        throw new IllegalArgumentException("Input array should have same number of elements as product of dimensions");
	    }
	    
	    // allocate memory
	    Array<T> result = array.newInstance(newDims);
	    
	    // copy elements of input array to result
        Array.Iterator<T> iter1 = array.iterator();
        Array.Iterator<T> iter2 = result.iterator();
        while(iter1.hasNext())
        {
            iter2.setNext(iter1.next());
        }
        
        return result;
	}

	@Override
	public boolean canProcess(Array<?> array)
	{
        // compute element number of input array
        int prodDims = cumProd(array.getSize());
        
        // compute element number of output array
        int prodDims2 = cumProd(newDims);
        
        // check element numbers are the same
        return prodDims == prodDims2;
	}
	
	private static final int cumProd(int[] dims)
	{
	    int prod = 1;
	    for (int d : dims)
        {
            prod *= d;
        }
	    return prod;
	}
}
