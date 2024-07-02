/**
 * 
 */
package net.sci.image.morphology.filter;

import net.sci.array.numeric.ScalarArray;
import net.sci.array.numeric.ScalarArray2D;
import net.sci.array.numeric.ScalarArray3D;
import net.sci.image.morphology.MorphologicalFilter;
import net.sci.image.morphology.Strel;
import net.sci.image.morphology.strel.Strel2D;
import net.sci.image.morphology.strel.Strel3D;

/**
 * Morphological Laplacian, obtained from the difference of the external
 * gradient with the internal gradient, both computed with the same
 * structuring element.
 * </p>
 * 
 * Example of use:
 * <pre>
 * {@code
 * Array inputArray = ...
 * Strel strel = SquareStrel.fromRadius(2);
 * double shift = 128.0; // for representing zero values as gray with UInt8 arrays
 * MorphologicalFilterAlgo filter = new Laplacian(strel, shift);
 * Array result = filter.process(inputArray);
 * }
 * </pre>
 * 
 * @see Dilation
 * @see Erosion
 * @see Gradient
 * @see Laplacian
 *
 * @author dlegland
 *
 */
public class Laplacian extends MorphologicalFilter
{
    double shift;
    
    public Laplacian(Strel strel, double shift)
    {
        super(strel);
        this.shift = shift;
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
        
        // First performs elementary operators
        ScalarArray2D<?> dil = strel2d.dilation(array);
        ScalarArray2D<?> ero = strel2d.erosion(array);
        
        // Compute subtraction of result from original array
        for (int y = 0; y < array.size(1); y++)
        {
            for (int x = 0; x < array.size(0); x++)
            {
                double val = (dil.getValue(x, y) + ero.getValue(x, y)) / 2 - array.getValue(x, y);
                dil.setValue(x, y, val + this.shift);
            }
        }

        return dil;
    }

    private ScalarArray3D<?> processScalar3d(ScalarArray3D<?> array)
    {
        Strel3D strel3d = Strel3D.wrap(this.strel);
        strel3d.addAlgoListener(this);
        
        // First performs elementary operators
        ScalarArray3D<?> dil = strel3d.dilation(array);
        ScalarArray3D<?> ero = strel3d.erosion(array);
        
        // Compute subtraction of result from original array
        for (int z = 0; z < array.size(2); z++)
        {
            for (int y = 0; y < array.size(1); y++)
            {
                for (int x = 0; x < array.size(0); x++)
                {
                    double val = (dil.getValue(x, y, z) + ero.getValue(x, y, z)) / 2 - array.getValue(x, y, z);
                    dil.setValue(x, y, z, val + this.shift);
                }
            }
        }
        
        return dil;
    }
}
