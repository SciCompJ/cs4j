/**
 * 
 */
package net.sci.image.binary.distmap;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Implementation of Chamfer Weights that manages two types of offsets,
 * corresponding to orthogonal and diagonal neighbors.
 * 
 * Weights are stored internally as short values.
 * 
 * @author dlegland
 */
public class ChamferMask2DW2 extends ChamferMask2D
{
	/** The weight for orthogonal neighbors.*/
	short a;
	/** The weight for diagonal neighbors.*/
	short b;
	
	/**
	 * Creates a new ChamferWeights3D object by specifying the weights
	 * associated to orthogonal and diagonal neighbors.
	 * 
	 * @param a
	 *            the weight associated to orthogonal neighbors
	 * @param b
	 *            the weight associated to diagonal neighbors
	 */
	public ChamferMask2DW2(int a, int b)
	{
		this.a = (short) a;
		this.b = (short) b;
	}

	/**
	 * Creates a new ChamferMask2D object by specifying the weights associated
	 * to orthogonal and diagonal neighbors.
	 * 
	 * @param weights
	 *            the weights associated to the different types of offset
	 */	
	public ChamferMask2DW2(short[] weights)
	{
		if (weights.length != 2)
		{
			throw new RuntimeException("Number of weights must be 2, not " + weights.length);
		}
		this.a = weights[0];
		this.b = weights[1];
	}

	@Override
	public Collection<Offset> getForwardOffsets()
	{
		// create array of forward shifts
		ArrayList<Offset> offsets = new ArrayList<Offset>();
	
		// offsets in the current plane
		offsets.add(new Offset(-1, -1, b));
		offsets.add(new Offset( 0, -1, a));
		offsets.add(new Offset(+1, -1, b));
		offsets.add(new Offset(-1,  0, a));
	
		return offsets;
	}

	@Override
	public Collection<Offset> getBackwardOffsets()
	{
		// create array of backward shifts
		ArrayList<Offset> offsets = new ArrayList<Offset>();

		// offsets in the current plane
		offsets.add(new Offset(-1, +1, b));
		offsets.add(new Offset( 0, +1, a));
		offsets.add(new Offset(+1, +1, b));
		offsets.add(new Offset(+1,  0, a));

		return offsets;
	}

	@Override
	public int getIntegerNormalizationWeight()
	{
		return a;
	}
}
