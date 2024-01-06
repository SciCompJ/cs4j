/**
 * 
 */
package net.sci.image.morphology;

import net.sci.algo.Algo;
import net.sci.array.binary.BinaryArray;
import net.sci.image.morphology.strel.Strel2D;
import net.sci.image.morphology.strel.Strel3D;

/**
 * General interface for structuring elements.
 * 
 * @see Strel2D
 * @see Strel3D
 * 
 * @author dlegland
 *
 */
public interface Strel extends Algo
{
    /**
     * Returns a reversed (i.e. symmetric wrt the origin) version of this
     * structuring element. Implementations can return more specialized type
     * depending on the implemented interfaces.
     * 
     * @return the reversed structuring element
     */
    public Strel reverse();
    
    /**
     * Returns the structuring element as a binary array the same dimension as
     * this strel. The position of the reference element within the mask can be
     * obtained by the <code>maskOffset()</code> method.
     * 
     * @return the mask of the structuring element
     */
    public BinaryArray binaryMask();

    /**
     * Returns the offset in the mask for each direction. 
     * The first value corresponds to the shift along the first dimension.
     * 
     * @return the offset within the mask
     */
    public int[] maskOffset();

    /**
     * Returns the structuring element as a set of shifts. The size of the
     * result is N-by-Nd, where N is the number of elements of the structuring
     * element. The first value corresponds to the shift along the first dimension.
     * 
     * @return a set of shifts
     */
    public int[][] shifts();
    
    /**
     * Returns the size of the structuring element, as an array of size in each
     * direction. As many elements as the number of dimensions. The first index
     * corresponds to the number of pixels in the x direction.
     * 
     * @return the size of the structuring element
     */
    public int[] size();

    public int dimensionality();
}
