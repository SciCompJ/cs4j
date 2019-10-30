/**
 * 
 */
package net.sci.image.morphology.filter;

import net.sci.array.scalar.ScalarArray;
import net.sci.array.scalar.ScalarArray2D;
import net.sci.array.scalar.ScalarArray3D;
import net.sci.image.morphology.MorphologicalFilter;
import net.sci.image.morphology.Strel;
import net.sci.image.morphology.Strel2D;
import net.sci.image.morphology.Strel3D;

/**
 * Morphological closing, that consists in computing a morphological dilation
 * followed by a morphological erosion. </p>
 * 
 * Example of use:
 * <pre>
 * {@code
 * Array inputArray = ...
 * Strel strel = SquareStrel.fromRadius(2);
 * MorphologicalFilter filter = new Closing(strel);
 * Array result = filter.process(inputArray);
 * }
 * </pre>
 * 
 * @author dlegland
 *
 */
public class Closing extends MorphologicalFilter
{
    public Closing(Strel strel)
    {
        super(strel);
    }

    public ScalarArray<?> processScalar(ScalarArray<?> array)
    {
        int nd = array.dimensionality();
        if (nd == 2)
        {
            Strel2D strel2d = Strel2D.wrap(this.strel);
            return strel2d.closing(ScalarArray2D.wrapScalar2d(array));
        }
        else if (nd == 3)
        {
            Strel3D strel3d = Strel3D.wrap(this.strel);
            return strel3d.closing(ScalarArray3D.wrapScalar3d(array));
        }
        else
        {
            throw new IllegalArgumentException("Requires an array of dimensionality 2 or 3, not " + nd);
        }
    }
    
}
