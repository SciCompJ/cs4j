/**
 * 
 */
package net.sci.image.morphology;

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

}
