/**
 * 
 */
package net.sci.array.numeric.process;

import java.util.Arrays;

import net.sci.array.numeric.ScalarArray;

/**
 * Computes median intensity projection along a specified dimension.
 * 
 * Returns an array with the same number of dimension of the input. The size of
 * the output along the projection dimension is equal to 1.
 * 
 * @author dlegland
 *
 */
public class MedianProjection extends ProjectionOperator
{
    public static final double median(double[] values)
    {
        Arrays.sort(values);
        int n = values.length;
        if (n % 2 == 0)
        {
            // even case
            return (values[n/2] +  values[n/2-1]) / 2.0;
        }
        else
        {
            // odd case
            return values[n/2];
        }
    }
    
    // =============================================================
    // Constructor
    
    /**
     * Creates a new instance of MaxProjection operator, that specifies the
     * dimension to project along.
     * 
     * @param dim
     *            the dimension for projection
     */
    public MedianProjection(int dim)
    {
        super(dim);
    }

    
    // =============================================================
    // New methods
    
    public void processScalar(ScalarArray<?> source, ScalarArray<?> target)
    {
        // create position pointer for source image
        int nd = source.dimensionality();
        int[] srcPos = new int[nd];
        
        int indMax = source.size(this.dim);
        
        // iterate over positions in target image
        double[] values = new double[indMax];
        for (int[] pos : target.positions()) 
        {
            // convert to position in source image
            System.arraycopy(pos, 0, srcPos, 0, nd);
            
            // iterate over current line
            for (int i = 0; i < indMax; i++)
            {
                srcPos[this.dim] = i;
                values[i] = source.getValue(srcPos);
            }
            
            // set to median of current values
            target.setValue(pos, median(values));
        }
    }
}
