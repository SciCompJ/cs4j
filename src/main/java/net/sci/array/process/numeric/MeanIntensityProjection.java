/**
 * 
 */
package net.sci.array.process.numeric;

import net.sci.array.numeric.ScalarArray;

/**
 * Computes maximum intensity projection along a specified dimension.
 * 
 * Returns an array with the same number of dimension of the input. The size of
 * the output along the projection dimension is equal to 1.
 * 
 * @author dlegland
 *
 */
public class MeanIntensityProjection extends ProjectionOperator
{
    // =============================================================
    // Constructor
    
    /**
     * Creates a new instance of MaxProjection operator, that specifies the
     * dimension to project along.
     * 
     * @param dim
     *            the dimension for projection
     */
    public MeanIntensityProjection(int dim)
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
        for (int[] pos : target.positions()) 
        {
            // convert to position in source image
            System.arraycopy(pos, 0, srcPos, 0, nd);
            
            double mean = 0.0;
            
            // iterate over current line
            for (int i = 0; i < indMax; i++)
            {
                srcPos[this.dim] = i;
                mean += source.getValue(srcPos);
            }
            
            // compute mean
            mean /= indMax;
            
            // copy value of selected position
            target.setValue(pos, mean);
        }
    }
}
