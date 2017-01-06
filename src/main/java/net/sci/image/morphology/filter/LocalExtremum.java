/**
 * 
 */
package net.sci.image.morphology.filter;

/**
 * Computes the extremum in a local sliding neighborhood of the current pixel.
 * 
 * @author David Legland
 *
 */
public interface LocalExtremum
{
	public enum Type
	{
		MINIMUM,
		MAXIMUM
	};

}
