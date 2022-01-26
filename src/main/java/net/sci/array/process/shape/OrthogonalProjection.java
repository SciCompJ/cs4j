/**
 * 
 */
package net.sci.array.process.shape;

import net.sci.algo.AlgoStub;
import net.sci.array.Array;
import net.sci.array.ArrayOperator;
import net.sci.array.scalar.ScalarArray;

/**
 * Computes maximum intensity projection along a specified dimension. Returns an
 * array with one dimension less than the input array.
 *
 * @deprecated replaced by net.sci.array.process.numeric.MaxProjection.
 * 
 * @author dlegland
 * 
 */
@Deprecated
public class OrthogonalProjection extends AlgoStub implements ArrayOperator
{
	int dim;
	
	/**
	 * Creates a new instance of OrthogonalProjection operator, that specifies
	 * the dimension to project along.
	 * 
	 * @param dim the dimension for projection
	 */
	public OrthogonalProjection(int dim)
	{
		this.dim = dim;
	}

	public void processScalar(ScalarArray<?> source, ScalarArray<?> target)
	{
		// create position pointer for source image
		int nd = target.dimensionality();
		int[] srcPos = new int[nd + 1];
		
		int indMax = source.size(this.dim);
		
        // iterate over positions in target image
        for (int[] pos : target.positions()) 
        {
			// convert to position in source image
			System.arraycopy(pos, 0, srcPos, 0, dim);
			System.arraycopy(pos, dim, srcPos, dim + 1, nd - dim);
			
			double maxValue = Double.NEGATIVE_INFINITY;
			
			// iterate over current line
			for (int i = 0; i < indMax; i++)
			{
				srcPos[this.dim] = i;
				maxValue = Math.max(maxValue, source.getValue(srcPos));
			}
			
			// copy value of selected position
			target.setValue(pos, maxValue);
		}
	}

    /**
     * Creates a new array that can be used as output for processing the given
     * input array.
     * 
     * @param array
     *            the reference array
     * @return a new instance of Array that can be used for processing input
     *         array.
     */
    public ScalarArray<?> createEmptyOutputArray(ScalarArray<?> array)
    {
    	// number of dimensions of new array
    	int nd = array.dimensionality() - 1;
    	
    	if (dim > nd)
    	{
    		throw new IllegalArgumentException(String.format(
    				"Slicer in dim %d can not process array of size %d", dim,
    				nd + 1));
    	}
    	int[] dims = new int[nd];
    	for (int d = 0; d  < this.dim; d++)
    	{
    		dims[d] = array.size(d);
    	}
    	for (int d = this.dim; d < nd; d++)
    	{
    		dims[d] = array.size(d+1);
    	}
    	return array.newInstance(dims);
    }

    @Override
    public <T> Array<?> process(Array<T> array)
    {
        if (!(array instanceof ScalarArray))
        {
            throw new IllegalArgumentException("Requires a scalar array");
        }
        
        ScalarArray<?> input = (ScalarArray<?>) array;
        ScalarArray<?> output = createEmptyOutputArray(input);
        
        processScalar(input, output);
        
        return output;
    }

    @Override
    public boolean canProcess(Array<?> array)
    {
        return array instanceof ScalarArray;
    }
}
