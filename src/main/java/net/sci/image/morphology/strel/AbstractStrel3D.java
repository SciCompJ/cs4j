/**
 * 
 */
package net.sci.image.morphology.strel;

import net.sci.algo.AlgoStub;
import net.sci.array.numeric.ScalarArray3D;

/**
 * Implementation basis for 3D structuring elements
 * 
 * @author David Legland
 */
public abstract class AbstractStrel3D extends AlgoStub implements Strel3D
{
	// ===================================================================
	// Default implementation of some methods
	
	public ScalarArray3D<?> closing(ScalarArray3D<?> stack)
	{
		return this.reverse().erosion(this.dilation(stack));
	}

	public ScalarArray3D<?> opening(ScalarArray3D<?> stack)
	{
		return this.reverse().dilation(this.erosion(stack));
	}
}
