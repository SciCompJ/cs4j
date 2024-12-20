/**
 * 
 */
package net.sci.image.morphology.filtering;

import net.sci.array.numeric.ScalarArray;
import net.sci.array.numeric.ScalarArray2D;
import net.sci.array.numeric.ScalarArray3D;
import net.sci.image.morphology.MorphologicalFilter;
import net.sci.image.morphology.Strel;
import net.sci.image.morphology.strel.Strel2D;
import net.sci.image.morphology.strel.Strel3D;

/**
 * Black top-hat, that consists in subtracting the original array from
 * the result of a morphological closing.
 * </p>
 * 
 * Example of use:
 * <pre>
 * {@code
 * Array inputArray = ...
 * Strel strel = SquareStrel.fromRadius(2);
 * MorphologicalFilterAlgo filter = new WhiteTopHat(strel);
 * Array result = filter.process(inputArray);
 * }
 * </pre>
 * 
 * @author dlegland
 *
 */
public class BlackTopHat extends MorphologicalFilter 
{    
    public BlackTopHat(Strel strel)
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
            throw new IllegalArgumentException("Requires an array of dimensionality 2 or 3, not " + nd);
        }
    }
    
    private ScalarArray2D<?> processScalar2d(ScalarArray2D<?> array)
    {
        Strel2D strel2d = Strel2D.wrap(this.strel);
        strel2d.addAlgoListener(this);
        
        // First performs opening
        ScalarArray2D<?> result = strel2d.closing(array);
        
        // Compute subtraction of result from original array
        for (int y = 0; y < array.size(1); y++)
        {
            for (int x = 0; x < array.size(0); x++)
            {
                double val = result.getValue(x, y) - array.getValue(x, y);
                result.setValue(x, y, val);
            }
        }

        return result;
    }

    private ScalarArray3D<?> processScalar3d(ScalarArray3D<?> array)
    {
        Strel3D strel3d = Strel3D.wrap(this.strel);
        strel3d.addAlgoListener(this);
        
        // First performs opening
        ScalarArray3D<?> result = strel3d.closing(array);
        
        // Compute subtraction of result from original array
        for (int z = 0; z < array.size(2); z++)
        {
            for (int y = 0; y < array.size(1); y++)
            {
                for (int x = 0; x < array.size(0); x++)
                {
                    double val = result.getValue(x, y, z) - array.getValue(x, y, z);
                    result.setValue(x, y, z, val);
                }
            }
        }
        
        return result;
    }
}
