/**
 * 
 */
package net.sci.image.binary.distmap;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Implementation of Chamfer Weights for 3D images that manages three types of
 * offsets, corresponding to orthogonal, square-diagonal and cube-diagonal
 * neighbors.
 * 
 * This implementation manages two series of weights, one for integer
 * computation, the other one for floating-point computation.
 * 
 * @see ChamferMask3DW3
 * @see ChamferMask3DW4
 * 
 * @author dlegland
 */
public class ChamferMask3DW3Float extends ChamferMask3D
{
	/**
	 * The offset weights used for integer computations.
	 */
	short[] shortWeights;
	
	/**
	 * The offset weights used for floating-point computations.
	 */
	float[] floatWeights;

	/**
	 * Creates a new chamfer mask
	 * 
	 * @param shortWeights
	 *            the offset weights used for integer computations
	 * @param floatWeights
	 *            the offset weights used for floating-point computations
	 */
	public ChamferMask3DW3Float(short[] shortWeights, float[] floatWeights)
	{
		if (shortWeights.length != 3)
		{
			throw new RuntimeException("Number of short weights must be 3, not " + shortWeights.length);
		}
		if (floatWeights.length != 3)
		{
			throw new RuntimeException("Number of float weights must be 3, not " + floatWeights.length);
		}

		this.shortWeights = shortWeights;
		this.floatWeights = floatWeights;
	}

	@Override
	public Collection<Offset> getForwardOffsets()
	{
		// create array of forward shifts
		ArrayList<Offset> offsets = new ArrayList<Offset>();
	
		// offsets in the z-1 plane
		offsets.add(new Offset(-1, -1, -1, floatWeights[2], shortWeights[2]));
		offsets.add(new Offset( 0, -1, -1, floatWeights[1], shortWeights[1]));
		offsets.add(new Offset(+1, -1, -1, floatWeights[2], shortWeights[2]));
		offsets.add(new Offset(-1,  0, -1, floatWeights[1], shortWeights[1]));
		offsets.add(new Offset( 0,  0, -1, floatWeights[0], shortWeights[0]));
		offsets.add(new Offset(+1,  0, -1, floatWeights[1], shortWeights[1]));
		offsets.add(new Offset(-1, +1, -1, floatWeights[2], shortWeights[2]));
		offsets.add(new Offset( 0, +1, -1, floatWeights[1], shortWeights[1]));
		offsets.add(new Offset(+1, +1, -1, floatWeights[2], shortWeights[2]));
	
		// offsets in the current plane
		offsets.add(new Offset(-1, -1, 0, floatWeights[1], shortWeights[1]));
		offsets.add(new Offset( 0, -1, 0, floatWeights[0], shortWeights[0]));
		offsets.add(new Offset(+1, -1, 0, floatWeights[1], shortWeights[1]));
		offsets.add(new Offset(-1,  0, 0, floatWeights[0], shortWeights[0]));
	
		return offsets;
	}

	@Override
	public Collection<Offset> getBackwardOffsets()
	{
		// create array of backward shifts
		ArrayList<Offset> offsets = new ArrayList<Offset>();

		// offsets in the z+1 plane
		offsets.add(new Offset(-1, -1, +1, floatWeights[2], shortWeights[2]));
		offsets.add(new Offset( 0, -1, +1, floatWeights[1], shortWeights[1]));
		offsets.add(new Offset(+1, -1, +1, floatWeights[2], shortWeights[2]));
		offsets.add(new Offset(-1,  0, +1, floatWeights[1], shortWeights[1]));
		offsets.add(new Offset( 0,  0, +1, floatWeights[0], shortWeights[0]));
		offsets.add(new Offset(+1,  0, +1, floatWeights[1], shortWeights[1]));
		offsets.add(new Offset(-1, +1, +1, floatWeights[2], shortWeights[2]));
		offsets.add(new Offset( 0, +1, +1, floatWeights[1], shortWeights[1]));
		offsets.add(new Offset(+1, +1, +1, floatWeights[2], shortWeights[2]));

		// offsets in the current plane
		offsets.add(new Offset(-1, +1, 0, floatWeights[1], shortWeights[1]));
		offsets.add(new Offset( 0, +1, 0, floatWeights[0], shortWeights[0]));
		offsets.add(new Offset(+1, +1, 0, floatWeights[1], shortWeights[1]));
		offsets.add(new Offset(+1,  0, 0, floatWeights[0], shortWeights[0]));

		return offsets;
	}

	@Override
	public double getNormalizationWeight()
	{
		return floatWeights[0];
	}

	@Override
	public int getIntegerNormalizationWeight()
	{
		return shortWeights[0];
	}
}
