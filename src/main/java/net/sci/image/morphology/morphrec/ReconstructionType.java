/**
 * 
 */
package net.sci.image.morphology.morphrec;

/**
 * Enumeration of the two different types of morphological reconstruction.
 *  
 * @author David Legland
 *
 */
public enum ReconstructionType 
{
	BY_DILATION,
	BY_EROSION;
	
	/**
	 * Private constructor for avoiding direct instanciation.
	 */
	private ReconstructionType()
	{
	}
	
	/**
	 * Returns the sign that can be used in algorithms generic for dilation 
	 * and erosion.
	 * @return +1 for dilation, and -1 for erosion
	 */
	public int getSign() 
	{
		switch (this)
		{
		case BY_DILATION:
			return +1;
		case BY_EROSION:
			return -1;
		default:
			throw new RuntimeException("Unknown case: " + this.toString());
		}
	}
}
