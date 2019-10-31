/**
 * 
 */
package net.sci.image.morphology.filter;

import net.sci.array.scalar.ScalarArray;
import net.sci.array.scalar.ScalarArray2D;
import net.sci.array.scalar.ScalarArray3D;
import net.sci.image.morphology.Strel;
import net.sci.image.morphology.strel.Strel2D;
import net.sci.image.morphology.strel.Strel3D;

/**
 * Morphological gradient, that consists in computing the difference of a
 * morphological dilation with a morphological erosion.
 * </p>
 * 
 * Example of use:
 * <pre>
 * {@code
 * Array inputArray = ...
 * Strel strel = SquareStrel.fromRadius(2);
 * MorphologicalFilterAlgo filter = new Opening(strel);
 * Array result = filter.process(inputArray);
 * }
 * </pre>
 * 
 * @author dlegland
 *
 */
public class Gradient extends MorphologicalFilterAlgo
{
    public Gradient(Strel strel)
    {
        super(strel);
    }

    
    public ScalarArray<?> processScalar(ScalarArray<?> array)
    {
        int nd = array.dimensionality();
        if (nd == 2)
        {
            return processScalar2d(ScalarArray2D.wrapScalar2d(array));
        }
        else if (nd == 3)
        {
            return processScalar3d(ScalarArray3D.wrapScalar3d(array));
        }
        else
        {
            return processScalarNd(array);
        }
    }
    
    private ScalarArray2D<?> processScalar2d(ScalarArray2D<?> array)
    {
        Strel2D strel2d = Strel2D.wrap(this.strel);

        // First performs elementary operations
        ScalarArray2D<?> result = strel2d.dilation(array);
        ScalarArray2D<?> eroded = strel2d.erosion(array);
        
        // Compute subtraction of result from original array
        for (int y = 0; y < array.size(1); y++)
        {
            for (int x = 0; x < array.size(0); x++)
            {
                double val = result.getValue(x, y) - eroded.getValue(x, y);
                result.setValue(x, y, val);
            }
        }
        
        return result;
    }

    private ScalarArray3D<?> processScalar3d(ScalarArray3D<?> array)
    {
        Strel3D strel3d = Strel3D.wrap(this.strel);

        // First performs elementary operations
        ScalarArray3D<?> result = strel3d.dilation(array);
        ScalarArray3D<?> eroded = strel3d.erosion(array);
        
        // Compute subtraction of result from original array
        for (int z = 0; z < array.size(2); z++)
        {
            for (int y = 0; y < array.size(1); y++)
            {
                for (int x = 0; x < array.size(0); x++)
                {
                    double val = result.getValue(x, y, z) - eroded.getValue(x, y, z);
                    result.setValue(x, y, z, val);
                }
            }
        }
        return result;
    }
    
    private ScalarArray<?> processScalarNd(ScalarArray<?> array)
    {
        // First performs elementary operations
        ScalarArray<?> result = new Dilation(strel).processScalar(array);
        ScalarArray<?> eroded = new Erosion(strel).processScalar(array);
        
        // Compute subtraction of result from original array
        for (int[] pos : result.positions())
        {
            double value = result.getValue(pos) - eroded.getValue(pos);
            result.setValue(pos, value);
        }
        return result;
    }
}
