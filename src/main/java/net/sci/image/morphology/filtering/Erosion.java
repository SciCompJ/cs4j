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
 * Morphological erosion, that consists in computing the minimum value in the
 * neighborhood defined by the structuring element. </p>
 * 
 * Example of use:
 * <pre>
 * {@code
 * Array inputArray = ...
 * Strel strel = SquareStrel.fromRadius(2);
 * MorphologicalFilterAlgo filter = new Erosion(strel);
 * Array result = filter.process(inputArray);
 * }
 * </pre>
 * 
 * @author dlegland
 *
 */
public class Erosion extends MorphologicalFilter
{
    public Erosion(Strel strel)
    {
        super(strel);
    }
    
    public ScalarArray<?> processScalar(ScalarArray<?> array)
    {
        int nd = array.dimensionality();
        if (nd == 2)
        {
            Strel2D strel2d = Strel2D.wrap(this.strel);
            strel2d.addAlgoListener(this);
            return strel2d.erosion(ScalarArray2D.wrapScalar2d(array));
        }
        else if (nd == 3)
        {
            Strel3D strel3d = Strel3D.wrap(this.strel);
            strel3d.addAlgoListener(this);
            return strel3d.erosion(ScalarArray3D.wrapScalar3d(array));
        }
        else
        {
            throw new IllegalArgumentException("Requires an array of dimensionality 2 or 3, not " + nd);
        }
    }
    
}
