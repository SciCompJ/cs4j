/**
 * 
 */
package net.sci.image.morphology.filter;

import net.sci.array.scalar.ScalarArray;
import net.sci.array.scalar.ScalarArray2D;
import net.sci.array.scalar.ScalarArray3D;
import net.sci.image.morphology.MorphologicalFilter;
import net.sci.image.morphology.Strel;
import net.sci.image.morphology.strel.Strel2D;
import net.sci.image.morphology.strel.Strel3D;

/**
 * InnerGradient Morphological gradient, that consists in computing the
 * difference of the original image with the result of a morphological erosion.
 * </p>
 * 
 * Example of use:
 * 
 * <pre>
 * {@code
 * Array inputArray = ...
 * Strel strel = SquareStrel.fromRadius(2);
 * MorphologicalFilter filter = new InnerGradient(strel);
 * Array result = filter.process(inputArray);
 * }
 * </pre>
 * 
 * @see Gradient
 * @see OuterGradient
 * @see Erosion
 * 
 * @author dlegland
 */
public class InnerGradient extends MorphologicalFilter
{
    public InnerGradient(Strel strel)
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
        strel2d.addAlgoListener(this);
        
        // First performs erosion
        ScalarArray2D<?> result = strel2d.erosion(array);
        
        // Compute subtraction of result from original array
        // (keep same array for storing result)
        for (int y = 0; y < array.size(1); y++)
        {
            for (int x = 0; x < array.size(0); x++)
            {
                
                double val = array.getValue(x, y) - result.getValue(x, y);
                result.setValue(x, y, val);
            }
        }
        
        strel2d.removeAlgoListener(this);
        return result;
    }

    private ScalarArray3D<?> processScalar3d(ScalarArray3D<?> array)
    {
        Strel3D strel3d = Strel3D.wrap(this.strel);
        strel3d.addAlgoListener(this);
        
        // First performs elementary operations
        ScalarArray3D<?> result = strel3d.erosion(array);
        
        // Compute subtraction of result from original array
        for (int z = 0; z < array.size(2); z++)
        {
            for (int y = 0; y < array.size(1); y++)
            {
                for (int x = 0; x < array.size(0); x++)
                {
                    double val = array.getValue(x, y, z) - result.getValue(x, y, z);
                    result.setValue(x, y, z, val);
                }
            }
        }

        strel3d.removeAlgoListener(this);
        return result;
    }
    
    private ScalarArray<?> processScalarNd(ScalarArray<?> array)
    {
        strel.addAlgoListener(this);
        
        // First performs elementary operations
        ScalarArray<?> result = new Erosion(strel).processScalar(array);
        
        // Compute subtraction of result from original array
        for (int[] pos : result.positions())
        {
            double value = array.getValue(pos) - result.getValue(pos);
            result.setValue(pos, value);
        }

        strel.removeAlgoListener(this);
        return result;
    }
}
