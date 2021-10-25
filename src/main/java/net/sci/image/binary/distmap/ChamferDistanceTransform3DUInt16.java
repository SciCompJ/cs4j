/*-
 * #%L
 * Mathematical morphology library and plugins for ImageJ/Fiji.
 * %%
 * Copyright (C) 2014 - 2017 INRA.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
package net.sci.image.binary.distmap;

import static java.lang.Math.min;

import java.util.ArrayList;

import net.sci.algo.AlgoEvent;
import net.sci.algo.AlgoStub;
import net.sci.array.binary.BinaryArray3D;
import net.sci.array.scalar.*;
import net.sci.image.binary.ChamferWeights3D;


/**
 * Computes Chamfer distances in a 3x3x3 neighborhood using UInt16.
 * 
 * In practice, computations are done with floats, but result is stored in a
 * 3D array of UInt16, thus requiring less memory than floating point. 
 * 
 * @author David Legland
 * @see ChamferDistanceTransform3DFloat32
 */
public class ChamferDistanceTransform3DUInt16 extends AlgoStub implements DistanceTransform3D
{
	// ==============================================================
	// class members

	private short[] weights;

	/**
	 * Flag for dividing final distance map by the value first weight. 
	 * This results in distance map values closer to euclidean, but with 
	 * non integer values. 
	 */
	private boolean normalizeMap = true;


	// ==============================================================
	// Constructors

	/**
	 * Constructor specifying the chamfer weights and the optional
	 * normalization option.
	 * 
	 * @param weights
	 *            an instance of ChamferWeights3D specifying the weights
	 * @param normalize
	 *            flag indicating whether the final distance map should be
	 *            normalized by the first weight
	 */
	public ChamferDistanceTransform3DUInt16(ChamferWeights3D weights, boolean normalize)
	{
		this.weights = weights.getShortWeights();
		this.normalizeMap = normalize;
	}

	/**
	 * Default constructor that specifies the chamfer weights.
	 * @param weights an array of two weights for orthogonal and diagonal directions
	 */
	public ChamferDistanceTransform3DUInt16(short[] weights)
	{
		this.weights = weights;
	}

	/**
	 * Constructor specifying the chamfer weights and the optional normalization.
	 * @param weights
	 *            an array of two weights for orthogonal and diagonal directions
	 * @param normalize
	 *            flag indicating whether the final distance map should be
	 *            normalized by the first weight
	 */
	public ChamferDistanceTransform3DUInt16(short[] weights, boolean normalize)
	{
		this.weights = weights;
		this.normalizeMap = normalize;
	}

	
    // ==============================================================
    // Implementation of computation methods

	/**
	 * Computes the distance map from a 3D binary image. 
	 * Distance is computed for each foreground (white) pixel, as the 
	 * chamfer distance to the nearest background (black) pixel.
	 * 
	 * @param array a 3D binary image with white pixels as foreground
	 * @return a new 3D image containing: <ul>
	 * <li> 0 for each background pixel </li>
	 * <li> the distance to the nearest background pixel otherwise</li>
	 * </ul>
	 */
	public ScalarArray3D<?> process3d(BinaryArray3D array) 
	{
		// create new empty image, and fill it with black
		UInt16Array3D distMap = initializeResult(array);

		// Two iterations are enough to compute distance map to boundary
		forwardIteration(distMap, array);
		backwardIteration(distMap, array);

		// Normalize values by the first weight
		if (this.normalizeMap) 
		{
		    normalizeResult(distMap, array);
		}
				
		return distMap;
	}
	
	
    // ==================================================
    // Inner computation methods 
    
	/**
     * Initializes empty image with either 0 (background) or Inf (foreground)
     *
     * @param array
     *            the input binary mask.
     * @return an empty distance map, containing either 0 (for background) or
     *         integer max value.
     */
    private UInt16Array3D initializeResult(BinaryArray3D array)
    {
        this.fireStatusChanged(new AlgoEvent(this, "Initialization"));

        // size of image
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        int sizeZ = array.size(2);
        
        // create new empty image, and fill it with black
        UInt16Array3D result = UInt16Array3D.create(sizeX, sizeY, sizeZ);
        
        // initialize empty image with either 0 (background) or Inf (foreground)
        for (int z = 0; z < sizeZ; z++)
        {
            fireProgressChanged(this, z, sizeZ);
            for (int y = 0; y < sizeY; y++)
            {
                for (int x = 0; x < sizeX; x++)
                {
                    boolean inside = array.getBoolean(x, y, z);
                    result.setInt(x, y, z, inside ? Integer.MAX_VALUE : 0);
                }
            }
        }
        fireProgressChanged(this, 1, 1); 
        return result;
    }

    //	private void forwardIteration() 
    private void forwardIteration(UInt16Array3D distMap, BinaryArray3D mask)
    {
        this.fireStatusChanged(new AlgoEvent(this, "Forward Scan"));

        // create array of forward shifts
        ArrayList<WeightedOffset> offsets = new ArrayList<WeightedOffset>(13);

        // offsets in the z-1 plane
        offsets.add(new WeightedOffset(-1, -1, -1, weights[2]));
        offsets.add(new WeightedOffset( 0, -1, -1, weights[1]));
        offsets.add(new WeightedOffset(+1, -1, -1, weights[2]));
        offsets.add(new WeightedOffset(-1,  0, -1, weights[1]));
        offsets.add(new WeightedOffset( 0,  0, -1, weights[0]));
        offsets.add(new WeightedOffset(+1,  0, -1, weights[1]));
        offsets.add(new WeightedOffset(-1, +1, -1, weights[2]));
        offsets.add(new WeightedOffset( 0, +1, -1, weights[1]));
        offsets.add(new WeightedOffset(+1, +1, -1, weights[2]));
        
        // offsets in the current plane
        offsets.add(new WeightedOffset(-1, -1, 0, weights[1]));
        offsets.add(new WeightedOffset( 0, -1, 0, weights[0]));
        offsets.add(new WeightedOffset(+1, -1, 0, weights[1]));
        offsets.add(new WeightedOffset(-1,  0, 0, weights[0]));

        // size of image
        int sizeX = mask.size(0);
        int sizeY = mask.size(1);
        int sizeZ = mask.size(2);

        // iterate on image voxels
		for (int z = 0; z < sizeZ; z++)
		{
			fireProgressChanged(this, z, sizeZ); 
			for (int y = 0; y < sizeY; y++)
			{
				for (int x = 0; x < sizeX; x++)
				{
                    // process only pixels within the mask
                    if (!mask.getBoolean(x, y, z))
                        continue;

                    // current distance value
                    int currentDist = distMap.getInt(x, y, z);
                    int newDist = currentDist;

                    for (WeightedOffset offset : offsets)
                    {
                        int x2 = x + offset.dx;
                        int y2 = y + offset.dy;
                        int z2 = z + offset.dz;
                        
                        // check that current neighbor is within image
                        if (x2 >= 0 && x2 < sizeX && y2 >= 0 && y2 < sizeY && z2 >= 0 && z2 < sizeZ)
                        {
                            newDist = min(newDist, distMap.getInt(x2, y2, z2) + offset.weight);
                        }
                    }

                    if (newDist < currentDist) 
                    {
                        distMap.setInt(x, y, z, newDist);
                    }
				}
			}
		}
		fireProgressChanged(this, 1, 1); 
	}

    private void backwardIteration(UInt16Array3D distMap, BinaryArray3D mask)
    {
        this.fireStatusChanged(new AlgoEvent(this, "Backward Scan"));

        // create array of backward shifts
        ArrayList<WeightedOffset> offsets = new ArrayList<WeightedOffset>(13);
        
        // offsets in the z+1 plane
        offsets.add(new WeightedOffset(-1, -1, +1, weights[2]));
        offsets.add(new WeightedOffset( 0, -1, +1, weights[1]));
        offsets.add(new WeightedOffset(+1, -1, +1, weights[2]));
        offsets.add(new WeightedOffset(-1,  0, +1, weights[1]));
        offsets.add(new WeightedOffset( 0,  0, +1, weights[0]));
        offsets.add(new WeightedOffset(+1,  0, +1, weights[1]));
        offsets.add(new WeightedOffset(-1, +1, +1, weights[2]));
        offsets.add(new WeightedOffset( 0, +1, +1, weights[1]));
        offsets.add(new WeightedOffset(+1, +1, +1, weights[2]));
        
        // offsets in the current plane
        offsets.add(new WeightedOffset(-1, +1, 0, weights[1]));
        offsets.add(new WeightedOffset( 0, +1, 0, weights[0]));
        offsets.add(new WeightedOffset(+1, +1, 0, weights[1]));
        offsets.add(new WeightedOffset(+1,  0, 0, weights[0]));

        // size of image
        int sizeX = mask.size(0);
        int sizeY = mask.size(1);
        int sizeZ = mask.size(2);

        // Iterate over pixels
        for (int z = sizeZ - 1; z >= 0; z--)
        {
            fireProgressChanged(this, sizeZ-1-z, sizeZ);
            for (int y = sizeY - 1; y >= 0; y--)
            {
                for (int x = sizeX - 1; x >= 0; x--)
                {
                    // process only pixels within the mask
                    if (!mask.getBoolean(x, y, z))
                        continue;

                    // current distance value
                    int currentDist = distMap.getInt(x, y, z);
                    int newDist = currentDist;

                    for (WeightedOffset offset : offsets)
                    {
                        int x2 = x + offset.dx;
                        int y2 = y + offset.dy;
                        int z2 = z + offset.dz;
                        
                        // check that current neighbor is within image
                        if (x2 >= 0 && x2 < sizeX && y2 >= 0 && y2 < sizeY && z2 >= 0 && z2 < sizeZ)
                        {
                            newDist = min(newDist, distMap.getInt(x2, y2, z2) + offset.weight);
                        }
                    }

                    if (newDist < currentDist) 
                    {
                        distMap.setInt(x, y, z, newDist);
                    }
                }
            } 
        }
        
        this.fireProgressChanged(this, sizeZ, sizeZ);
    } // end of backward iteration

	private void normalizeResult(UInt16Array3D distMap, BinaryArray3D array)
    {
        this.fireStatusChanged(new AlgoEvent(this, "Normalization"));

        // size of image
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        int sizeZ = array.size(2);
        
        for (int z = 0; z < sizeZ; z++)
        {
            for (int y = 0; y < sizeY; y++)
            {
                for (int x = 0; x < sizeX; x++) 
                {
                    if (array.getBoolean(x, y, z)) 
                    {
                        distMap.setInt(x, y, z, distMap.getInt(x, y, z) / weights[0]);
                    }
                }
            }
        }
    }
    
    private class WeightedOffset
    {
        int dx;
        int dy;
        int dz;
        short weight;
        
        public WeightedOffset(int dx, int dy, int dz, short weight)
        {
            this.dx = dx;
            this.dy = dy;
            this.dz = dz;
            this.weight = weight;
        }
    }
}
