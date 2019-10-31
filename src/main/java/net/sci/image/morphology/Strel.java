/**
 * 
 */
package net.sci.image.morphology;

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
public interface Strel
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
     * Returns the size of the structuring element, as an array of size in each
     * direction. As many elements as the number of dimensions. The first index
     * corresponds to the number of pixels in the x direction.
     * 
     * @return the size of the structuring element
     */
    public int[] size();

    public int dimensionality();
}
