/**
 * 
 */
package net.sci.image.morphology.filtering;

import net.sci.array.Array;
import net.sci.array.numeric.ScalarArray;
import net.sci.array.numeric.ScalarArray2D;
import net.sci.array.numeric.ScalarArray3D;
import net.sci.image.morphology.MorphologicalFilter;
import net.sci.image.morphology.Strel;
import net.sci.image.morphology.strel.Strel2D;
import net.sci.image.morphology.strel.Strel3D;

/**
 * Morphological opening, that consists in computing a morphological erosion
 * followed by a morphological dilation.
 * </p>
 * 
 * Example of use:
 * 
 * <pre>
 * {@code
 * Array inputArray = ...
 * Strel strel = SquareStrel.fromRadius(2);
 * MorphologicalFilterAlgo filter = new Opening(strel);
 * Array result = filter.process(inputArray);
 * }
 * </pre>
 *
 * @see net.sci.image.morphology.filtering.Erosion
 * @see net.sci.image.morphology.filtering.Dilation
 * @see net.sci.image.morphology.filtering.Closing
 * @see net.sci.image.morphology.filtering.BinaryOpening
 * 
 * @author dlegland
 *
 */
public class Opening extends MorphologicalFilter
{
    public Opening(Strel strel)
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
            return strel2d.opening(ScalarArray2D.wrapScalar2d(array));
        }
        else if (nd == 3)
        {
            Strel3D strel3d = Strel3D.wrap(this.strel);
            strel3d.addAlgoListener(this);
            return strel3d.opening(ScalarArray3D.wrapScalar3d(array));
        }
        else
        {
            throw new IllegalArgumentException("Requires an array of dimensionality 2 or 3, not " + nd);
        }
    }
    
    @Override
    public <T> Array<?> process(Array<T> array)
    {
        if (array instanceof ScalarArray)
        {
            return processScalar((ScalarArray<?>) array);
        }
        
        Dilation dilation = new Dilation(strel);
        Erosion erosion = new Erosion(strel.reverse());
        return dilation.process(erosion.process(array));
    }
}
