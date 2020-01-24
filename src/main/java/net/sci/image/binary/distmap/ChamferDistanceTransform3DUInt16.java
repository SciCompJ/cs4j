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
import net.sci.algo.AlgoStub;
import net.sci.array.scalar.BinaryArray3D;
import net.sci.array.scalar.ScalarArray3D;
import net.sci.array.scalar.UInt16Array3D;
import net.sci.image.binary.ChamferWeights3D;


/**
 * Computes Chamfer distances in a 3x3x3 neighborhood using UInt16.
 * 
 * In practice, computations are done with floats, but result is stored in a
 * 3D array of UInt16, thus requiring less memory than floating point. 
 * 
 * @author David Legland
 * @see ChamferDistanceTransform3DFloat
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

	BinaryArray3D maskArray;

	/**
	 * The result image that will store the distance map. The content
	 * of the buffer is updated during forward and backward iterations.
	 */
	UInt16Array3D distMap;
	
	private int sizeX;
	private int sizeY;
	private int sizeZ;


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

	/**
	 * Computes the distance map from a 3D binary image. 
	 * Distance is computed for each foreground (white) pixel, as the 
	 * chamfer distance to the nearest background (black) pixel.
	 * 
	 * @param array a 3D binary image with white pixels (255) as foreground
	 * @return a new 3D image containing: <ul>
	 * <li> 0 for each background pixel </li>
	 * <li> the distance to the nearest background pixel otherwise</li>
	 * </ul>
	 */
	public ScalarArray3D<?> process3d(BinaryArray3D array) 
	{
		this.maskArray = array;
		
		// size of image
		sizeX = array.size(0);
		sizeY = array.size(1);
		sizeZ = array.size(2);
		
		// create new empty image, and fill it with black
		this.distMap = UInt16Array3D.create(sizeX, sizeY, sizeZ);

		// initialize empty image with either 0 (background) or Inf (foreground)
		fireStatusChanged(this, "Initialization..."); 
		for (int z = 0; z < sizeZ; z++) 
		{
			fireProgressChanged(this, z, sizeZ);
			
			for (int y = 0; y < sizeY; y++) 
			{
				for (int x = 0; x < sizeX; x++) 
				{
					boolean b = array.getBoolean(x, y, z);
					distMap.setInt(b ? Short.MAX_VALUE : 0, x, y, z);
				}
			}
		}
		fireProgressChanged(this, 1, 1); 
		
		// Two iterations are enough to compute distance map to boundary
		forwardIteration();
		backwardIteration();

		// Normalize values by the first weight
		if (this.normalizeMap) 
		{
			fireStatusChanged(this, "Normalize map..."); 
			for (int z = 0; z < sizeZ; z++) 
			{
				fireProgressChanged(this, z, sizeZ);
				
				for (int y = 0; y < sizeY; y++) 
				{
					for (int x = 0; x < sizeX; x++) 
					{
						if (array.getBoolean(x, y, z))
						{
							int value = distMap.getInt(x, y, z) / weights[0];
							distMap.setInt(value, x, y, z);
						}
					}
				}
			}
			fireProgressChanged(this, 1, 1); 
		}
				
		return distMap;
	}
	
	private void forwardIteration() 
	{
		fireStatusChanged(this, "Forward scan..."); 
		// iterate on image voxels
		for (int z = 0; z < sizeZ; z++)
		{
			fireProgressChanged(this, z, sizeZ); 
			for (int y = 0; y < sizeY; y++)
			{
				for (int x = 0; x < sizeX; x++)
				{
					boolean maskState = maskArray.getBoolean(x, y, z); 

					// check if we need to update current voxel
					if (!maskState)
						continue;
					
					// init new values for current voxel
					int ortho = Short.MAX_VALUE;
					int diago = Short.MAX_VALUE;
					int diag3 = Short.MAX_VALUE;
					
					// process (z-1) slice
					if (z > 0) 
					{
						if (y > 0)
						{
							// voxels in the (y-1) line of  the (z-1) plane
							if (x > 0) 
							{
								diag3 = Math.min(diag3, distMap.getInt(x-1, y-1, z-1));
							}
							diago = Math.min(diago, distMap.getInt(x, y-1, z-1));
							if (x < sizeX - 1) 
							{
								diag3 = Math.min(diag3, distMap.getInt(x+1, y-1, z-1));
							}
						}
						
						// voxels in the y line of the (z-1) plane
						if (x > 0) 
						{
							diago = Math.min(diago, distMap.getInt(x-1, y, z-1));
						}
						ortho = Math.min(ortho, distMap.getInt(x, y, z-1));
						if (x < sizeX - 1) 
						{
							diago = Math.min(diago, distMap.getInt(x+1, y, z-1));
						}

						if (y < sizeY - 1)
						{
							// voxels in the (y+1) line of  the (z-1) plane
							if (x > 0) 
							{
								diag3 = Math.min(diag3, distMap.getInt(x-1, y+1, z-1));
							}
							diago = Math.min(diago, distMap.getInt(x, y+1, z-1));
							if (x < sizeX - 1) 
							{
								diag3 = Math.min(diag3, distMap.getInt(x+1, y+1, z-1));
							}
						}
					}
					
					// voxels in the (y-1) line of the z-plane
					if (y > 0)
					{
						if (x > 0) 
						{
							diago = Math.min(diago, distMap.getInt(x-1, y-1, z));
						}
						ortho = Math.min(ortho, distMap.getInt(x, y-1, z));
						if (x < sizeX - 1) 
						{
							diago = Math.min(diago, distMap.getInt(x+1, y-1, z));
						}
					}
					
					// pixel to the left of the current voxel
					if (x > 0) 
					{
						ortho = Math.min(ortho, distMap.getInt(x-1, y, z));
					}
					
					int newVal = min3w(ortho, diago, diag3);
					updateIfNeeded(x, y, z, newVal);
				}
			}
		}
		fireProgressChanged(this, 1, 1); 
	}

	private void backwardIteration() 
	{
		fireStatusChanged(this, "Backward scan..."); 
		// iterate on image voxels in backward order
		for (int z = sizeZ - 1; z >= 0; z--)
		{
			fireProgressChanged(this, sizeZ-1-z, sizeZ); 
			
			for (int y = sizeY - 1; y >= 0; y--)
			{
				for (int x = sizeX - 1; x >= 0; x--)
				{
					boolean maskState = maskArray.getBoolean(x, y, z); 

					// check if we need to update current voxel
					if (!maskState)
						continue;
					
					// init new values for current voxel
					int ortho = Short.MAX_VALUE;
					int diago = Short.MAX_VALUE;
					int diag3 = Short.MAX_VALUE;
					
					// process (z+1) slice
					if (z < sizeZ - 1) 
					{
						if (y < sizeY - 1)
						{
							// voxels in the (y+1) line of  the (z+1) plane
							if (x < sizeX - 1) 
							{
								diag3 = Math.min(diag3, distMap.getInt(x+1, y+1, z+1));
							}
							diago = Math.min(diago, distMap.getInt(x, y+1, z+1));
							if (x > 0) 
							{
								diag3 = Math.min(diag3, distMap.getInt(x-1, y+1, z+1));
							}
						}
						
						// voxels in the y line of the (z+1) plane
						if (x < sizeX - 1) 
						{
							diago = Math.min(diago, distMap.getInt(x+1, y, z+1));
						}
						ortho = Math.min(ortho, distMap.getInt(x, y, z+1));
						if (x > 0) 
						{
							diago = Math.min(diago, distMap.getInt(x, y, z+1));
						}

						if (y > 0)
						{
							// voxels in the (y-1) line of  the (z+1) plane
							if (x < sizeX - 1) 
							{
								diag3 = Math.min(diag3, distMap.getInt(x+1, y-1, z+1));
							}
							diago = Math.min(diago, distMap.getInt(x, y-1, z+1));
							if (x > 0) 
							{
								diag3 = Math.min(diag3, distMap.getInt(x-1, y-1, z+1));
							}
						}
					}
					
					// voxels in the (y+1) line of the z-plane
					if (y < sizeY - 1)
					{
						if (x < sizeX - 1) 
						{
							diago = Math.min(diago, distMap.getInt(x+1, y+1, z));
						}
						ortho = Math.min(ortho, distMap.getInt(x, y+1, z));
						if (x > 0) 
						{
							diago = Math.min(diago, distMap.getInt(x-1, y+1, z));
						}
					}
					
					// voxel to the right of the current voxel
					if (x < sizeX - 1) 
					{
						ortho = Math.min(ortho, distMap.getInt(x+1, y, z));
					}
					
					int newVal = min3w(ortho, diago, diag3);
					updateIfNeeded(x, y, z, newVal);
				}
			}
		}
		fireProgressChanged(this, 1, 1); 
	}
	
	/**
	 * Computes the weighted minima of orthogonal, diagonal, and 3D diagonal
	 * values.
	 */
	private int min3w(int ortho, int diago, int diag2)
	{
		return min(min(ortho + weights[0], diago + weights[1]), diag2 + weights[2]);
	}
	
	/**
	 * Update the pixel at position (i,j,k) with the value newVal. If newVal is
	 * greater or equal to current value at position (i,j,k), do nothing.
	 */
	private void updateIfNeeded(int x, int y, int z, int newVal)
	{
		int value = distMap.getInt(x, y, z);
		if (newVal < value) 
		{
			distMap.setInt(newVal, x, y, z);
		}
	}
}
