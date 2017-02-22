/**
 * 
 */
package net.sci.image.morphology.filter;

import java.util.Collection;

import net.sci.image.morphology.Strel2D;

/**
 * Interface for structuring elements that can be decomposed into several
 * "simpler" structuring elements. It is assumed that elementary structuring
 * elements can performs in place dilation or erosion (i.e. the implements the
 * InPlaceStrel interface).
 * 
 * @see InPlaceStrel2D
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
