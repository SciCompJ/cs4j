/**
 * 
 */
package net.sci.image.morphology.strel;

import java.util.Collection;

/**
 * Interface for structuring elements that can be decomposed into several
 * "simpler" structuring elements. It is assumed that elementary structuring
 * elements can performs in place dilation or erosion (i.e. the implements the
 * InPlaceStrel interface).
 * 
 * @see InPlaceStrel2D
 * @see SeparableStrel3D
 * 
 * @author David Legland
 *
 */
public interface SeparableStrel2D extends Strel2D
{
    /**
     * Decomposes this separable structuring element into a set of smaller
     * structuring elements that can be used for in place processing of input
     * arrays.
     * 
     * @return a set of elementary structuring elements
     */
    public Collection<InPlaceStrel2D> decompose();
    
    /**
     * @return this instance of separable Strel
     */
    public SeparableStrel2D reverse();
}
