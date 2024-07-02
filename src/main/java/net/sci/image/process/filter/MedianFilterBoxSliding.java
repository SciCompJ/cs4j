/**
 * 
 */
package net.sci.image.process.filter;

import net.sci.algo.AlgoStub;
import net.sci.array.numeric.ScalarArray;
import net.sci.array.numeric.ScalarArray2D;
import net.sci.array.numeric.ScalarArray3D;
import net.sci.array.numeric.UInt8Array;
import net.sci.array.numeric.UInt8Array2D;
import net.sci.array.numeric.UInt8Array3D;
import net.sci.array.process.ScalarArrayOperator;
import net.sci.util.MathUtils;

/**
 * Computes the median value in a box neighborhood around each array element,
 * using local histogram to reduce complexity for large boxes. 
 * 
 * @author dlegland
 *
 */
public class MedianFilterBoxSliding extends AlgoStub implements ScalarArrayOperator
{
    /** The size of the box in each dimension */
    int[] diameters;
    
    
    /**
     * Creates a new instance of box filter by specifying the list of diameters in
     * each dimension.
     * 
     * @param diameters
     *            the box diameter in each dimension
     */
    public MedianFilterBoxSliding(int[] diameters)
    {
        this.diameters = new int[diameters.length];
        System.arraycopy(diameters, 0, this.diameters, 0, diameters.length);
    }
    
    public void processScalar(ScalarArray<?> source, ScalarArray<?> target)
    {
        int nd1 = source.dimensionality();
        int nd2 = target.dimensionality();
        if (nd1 != nd2)
        {
            throw new IllegalArgumentException("Both arrays must have the same dimensionality");
        }
        
        if (!net.sci.array.Arrays.isSameSize(source, target))
        {
            throw new IllegalArgumentException("Both arrays must have the same size");
        }
        
        // Choose the best possible implementation, depending on array dimensions
        if (nd1 == 2 && nd2 == 2)
        {
            processScalar2d(ScalarArray2D.wrap(source), ScalarArray2D.wrap(target));
        }
        else if (nd1 == 3 && nd2 == 3)
        {
            processScalar3d(ScalarArray3D.wrap(source), ScalarArray3D.wrap(target));
        }
//        else 
//        {
//            // use the most generic implementation, also slower
//            processScalarNd(source, target);
//        }
        else
            throw new RuntimeException("Implemented only for dimension 2");
    }

    /**
     * Process the specific case of 2D scalar arrays.
     * 
     * @param source
     *            the source array
     * @param target
     *            the target array
     */
    public void processScalar2d(ScalarArray2D<?> source, ScalarArray2D<?> target)
    {
        if (!(source instanceof UInt8Array2D))
        {
            throw new RuntimeException("Require UInt8Array");
        }
        UInt8Array2D source2 = UInt8Array2D.wrap(UInt8Array.wrapScalar(source));
        
        // get size of input array
        int sizeX = source.size(0);
        int sizeY = source.size(1);
        
        // check dimensions
        if (this.diameters.length < 2)
        {
            throw new RuntimeException("Can not process 2D array with less than two diameters.");
        }
        
        // compute the shift coordinates in each direction
        int rx1 = (this.diameters[0] - 1) / 2 + 1; // shift for pixel just before box
        int rx2 = this.diameters[0] / 2; // shift for last pixel within box
        int ry1 = (this.diameters[1] - 1) / 2;
        int ry2 = this.diameters[1] / 2 + 1;
        
        // compute the normalization constant
        int totalCount = (int) MathUtils.prod(diameters);
        
        // create Local Histogram
        MedianLocalHistogramUInt8 histo = new MedianLocalHistogramUInt8(totalCount, 0);
        
        // iterate over pixel positions
        for(int y = 0; y < sizeY; y++)
        {
            this.fireProgressChanged(this, y, sizeY);
            
            // clear histogram
            histo.reset(0);
            
            // fill histogram with values around the pixel before the first one
            for (int y2 = y - ry1; y2 < y + ry2; y2++)
            {
                int y2r = clamp(y2, 0, sizeY - 1);
                for (int x2 = - rx1; x2 < rx2; x2++)
                {
                    int x2r = clamp(x2, 0, sizeX - 1);
                    histo.replace(0, source2.getInt(x2r, y2r));
                }
            }
            
            for(int x = 0; x < sizeX; x++)
            {
                // iterate over front and back pixels around current pixel
                // iterate over neighbors of current pixel
                for (int y2 = y - ry1; y2 < y + ry2; y2++)
                {
                    int y2r = clamp(y2, 0, sizeY - 1);
                    int x2back = clamp(x - rx1, 0, sizeX - 1);
                    int x2front = clamp(x + rx2, 0, sizeX - 1);
                    histo.replace(source2.getInt(x2back, y2r), source2.getInt(x2front, y2r));
                }
                
                // retrieve the median value
                target.setValue(x, y, histo.getMedianInt());
            }
        }
    }
    
    /**
     * Process the specific case of 3D scalar arrays.
     * 
     * @param source
     *            the source array
     * @param target
     *            the target array
     */
    public void processScalar3d(ScalarArray3D<?> source, ScalarArray3D<?> target)
    {
        if (!(source instanceof UInt8Array3D))
        {
            throw new RuntimeException("Require UInt8Array");
        }
        UInt8Array3D source2 = UInt8Array3D.wrap(UInt8Array.wrapScalar(source));
        
        // get size of input array
        int sizeX = source.size(0);
        int sizeY = source.size(1);
        int sizeZ = source.size(2);
        
        // check dimensions
        if (this.diameters.length < 3)
        {
            throw new RuntimeException("Can not process 3D array with less than three diameters.");
        }
        
        // compute the shift coordinates in each direction
        int rx1 = (this.diameters[0] - 1) / 2 + 1; // shift for pixel just before box
        int rx2 = this.diameters[0] / 2; // shift for last pixel within box
        int ry1 = (this.diameters[1] - 1) / 2;
        int ry2 = this.diameters[1] / 2 + 1;
        int rz1 = (this.diameters[2] - 1) / 2;
        int rz2 = this.diameters[2] / 2 + 1;
        
        // compute the normalization constant
        int totalCount = (int) MathUtils.prod(diameters);
        
        // create Local Histogram
        MedianLocalHistogramUInt8 histo = new MedianLocalHistogramUInt8(totalCount, 0);
        
        // iterate over voxel positions
        for(int z = 0; z < sizeZ; z++)
        {
            this.fireProgressChanged(this, z, sizeZ);
            
            for(int y = 0; y < sizeY; y++)
            {
                // clear histogram
                histo.reset(0);

                // fill histogram with values around the pixel before the first one
                for (int z2 = z - rz1; z2 < z + rz2; z2++)
                {
                    int z2r = clamp(z2, 0, sizeZ - 1);
                    for (int y2 = y - ry1; y2 < y + ry2; y2++)
                    {
                        int y2r = clamp(y2, 0, sizeY - 1);
                        for (int x2 = - rx1; x2 < rx2; x2++)
                        {
                            int x2r = clamp(x2, 0, sizeX - 1);
                            histo.replace(0, source2.getInt(x2r, y2r, z2r));
                        }
                    }
                }
                    
                for(int x = 0; x < sizeX; x++)
                {
                    // iterate over front and back pixels around current pixel
                    // iterate over neighbors of current pixel
                    for (int z2 = z - rz1; z2 < z + rz2; z2++)
                    {
                        int z2r = clamp(z2, 0, sizeZ - 1);
                        for (int y2 = y - ry1; y2 < y + ry2; y2++)
                        {
                            int y2r = clamp(y2, 0, sizeY - 1);
                            int x2back = clamp(x - rx1, 0, sizeX - 1);
                            int x2front = clamp(x + rx2, 0, sizeX - 1);
                            histo.replace(source2.getInt(x2back, y2r, z2r), source2.getInt(x2front, y2r, z2r));
                        }
                    }
                    
                    // retrieve the median value
                    target.setValue(x, y, z, histo.getMedianInt());
                }
            }
        }
    }
    
    private static final int clamp(int value, int minValue, int maxValue)
    {
        if (value < minValue) return minValue;
        if (value > maxValue) return maxValue;
        return value;
    }

    @Override
    public ScalarArray<?> processScalar(ScalarArray<?> array)
    {
        ScalarArray<?> result = array.newInstance(array.size());
        processScalar(array, result);
        return result;
    }
}
