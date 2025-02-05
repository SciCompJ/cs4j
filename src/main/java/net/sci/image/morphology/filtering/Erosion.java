/**
 * 
 */
package net.sci.image.morphology.filtering;

import net.sci.array.Array;
import net.sci.array.numeric.ScalarArray;
import net.sci.array.numeric.ScalarArray2D;
import net.sci.array.numeric.ScalarArray3D;
import net.sci.array.numeric.VectorArray;
import net.sci.image.morphology.MorphologicalFilter;
import net.sci.image.morphology.Strel;
import net.sci.image.morphology.strel.Strel2D;
import net.sci.image.morphology.strel.Strel3D;

/**
 * Morphological erosion, that consists in computing the minimum value in the
 * neighborhood defined by the structuring element.
 * </p>
 * 
 * Example of use:
 * 
 * <pre>
 * {@code
 * Array inputArray = ...
 * Strel strel = SquareStrel.fromRadius(2);
 * Erosion filter = new Erosion(strel);
 * Array result = filter.process(inputArray);
 * }
 * </pre>
 * 
 * @see net.sci.image.morphology.filtering.BinaryErosion
 * @see net.sci.image.morphology.filtering.Dilation
 * @see net.sci.image.morphology.filtering.Closing
 * @see net.sci.image.morphology.filtering.Opening
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
    
    public <C extends Comparable<C>> Array<C> processComparable(Array<C> array)
    {
        int[] dims = array.size();
        int nd = array.dimensionality();
        if (strel.dimensionality() != nd)
        {
            throw new IllegalArgumentException("Requires an array with same dimensionality as structuring element (" + strel.dimensionality() + ")");
        }
        
        Array<C> res = array.newInstance(array.size());
        
        int[] pos2 = new int[nd];
        for (int[] pos : res.positions())
        {
            C value = array.get(pos);
            
            neighbors:
            for (int[] shift : strel.shifts())
            {
                for (int d = 0; d < nd; d++)
                {
                    pos2[d] = pos[d] + shift[d];
                    if (pos2[d] < 0) continue neighbors;
                    if (pos2[d] >= dims[d]) continue neighbors;
                }
                
                C v2 = array.get(pos2);
                if (value.compareTo(v2) > 0) value = v2;
            }
            
            res.set(pos, value);
        }
        
        return res;
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
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public <T> Array<?> process(Array<T> array)
    {
        if (array instanceof ScalarArray)
        {
            return processScalar((ScalarArray<?>) array);
        }
        else if (array instanceof VectorArray)
        {
            return processVector((VectorArray<?,?>) array);
        }
        else if (array.sampleElement() instanceof Comparable)
        {
            return processComparable((Array<? extends Comparable>) array);
        }
        else
        {
            throw new RuntimeException(
                    "Requires an array containing either Scalar or Comparable elements");
        }
    }
}
