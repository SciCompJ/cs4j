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
 * Morphological dilation, that consists in computing the maximum value in the
 * neighborhood defined by the structuring element. </p>
 * 
 * Example of use:
 * <pre>
 * {@code
 * Array inputArray = ...
 * Strel strel = SquareStrel.fromRadius(2);
 * Dilation dil = new Dilation(strel);
 * Array result = dil.process(inputArray);
 * }
 * </pre>
 * 
 * @author dlegland
 *
 */
public class Dilation extends MorphologicalFilterAlgo
{
    public Dilation(Strel strel)
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
            return strel2d.dilation(ScalarArray2D.wrapScalar2d(array));
        }
        else if (nd == 3)
        {
            Strel3D strel3d = Strel3D.wrap(this.strel);
            strel3d.addAlgoListener(this);
            return strel3d.dilation(ScalarArray3D.wrapScalar3d(array));
        }
        else
        {
            throw new IllegalArgumentException("Requires an array of dimensionality 2 or 3, not " + nd);
        }
    }
    
}
