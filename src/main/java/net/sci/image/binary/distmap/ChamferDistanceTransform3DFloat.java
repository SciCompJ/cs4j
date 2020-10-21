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
import net.sci.array.scalar.Float32Array3D;
import net.sci.array.scalar.ScalarArray3D;
import net.sci.image.binary.ChamferWeights3D;

/**
 * Computes Chamfer distances in a 3x3x3 neighborhood using floating point 
 * computation. 
 * 
 * @author David Legland
 * @see ChamferDistanceTransform3DUInt16
 */
public class ChamferDistanceTransform3DFloat extends AlgoStub implements DistanceTransform3D
{
	private float[] weights;

	/**
	 * Flag for dividing final distance map by the value first weight. 
	 * This results in distance map values closer to Euclidean, but with 
	 * non integer values. 
	 */
	private boolean normalizeMap = true;

	
	BinaryArray3D maskArray;
	
	/**
	 * The result image that will store the distance map. The content
	 * of the buffer is updated during forward and backward iterations.
	 */
	Float32Array3D distMap;
	
	private int sizeX;
	private int sizeY;
	private int sizeZ;	
	
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
	public ChamferDistanceTransform3DFloat(ChamferWeights3D weights, boolean normalize)
	{
		this.weights = weights.getFloatWeights();
		this.normalizeMap = normalize;
	}

	/**
	 * Default constructor that specifies the chamfer weights.
	 * @param weights an array of two weights for orthogonal and diagonal directions
	 */
	public ChamferDistanceTransform3DFloat(float[] weights)
	{
		this.weights = weights;
	}

	/**
	 * Constructor specifying the chamfer weights and the optional
	 * normalization option.
	 * 
	 * @param weights
	 *            an array of two weights for orthogonal and diagonal directions
	 * @param normalize
	 *            flag indicating whether the final distance map should be
	 *            normalized by the first weight
	 */
	public ChamferDistanceTransform3DFloat(float[] weights, boolean normalize)
	{
		this.weights = weights;
		this.normalizeMap = normalize;
	}

	/**
	 * Computes the distance map from a 3D binary image. Distance is computed
	 * for each foreground (white) pixel, as the chamfer distance to the nearest
	 * background (black) pixel.
	 * 
	 * @param array
	 *            a 3D binary image with white pixels (255) as foreground
	 * @return a new 3D image containing:
	 *         <ul>
	 *         <li>0 for each background pixel</li>
	 *         <li>the distance to the nearest background pixel otherwise</li>
	 *         </ul>
	 */
	public ScalarArray3D<?> process3d(BinaryArray3D array) 
	{
		this.maskArray = array;
		
		// size of image
		sizeX = array.size(0);
		sizeY = array.size(1);
		sizeZ = array.size(2);
		
		// create new empty image, and fill it with black
		this.distMap = Float32Array3D.create(sizeX, sizeY, sizeZ);

		// initialize empty image with either 0 (background) or max value (foreground)
		fireStatusChanged(this, "Initialization..."); 
		for (int z = 0; z < sizeZ; z++) 
		{
			fireProgressChanged(this, z, sizeZ);
			
			for (int y = 0; y < sizeY; y++) 
			{
				for (int x = 0; x < sizeX; x++) 
				{
					boolean b = array.getBoolean(x, y, z);
					distMap.setValue(x, y, z, b ? Float.MAX_VALUE : 0);
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
							double value = distMap.getValue(x, y, z) / weights[0];
							distMap.setValue(x, y, z, value);
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
					double ortho = Float.MAX_VALUE;
					double diago = Float.MAX_VALUE;
					double diag3 = Float.MAX_VALUE;
					
					// process (z-1) slice
					if (z > 0) 
					{
						if (y > 0)
						{
							// voxels in the (y-1) line of  the (z-1) plane
							if (x > 0) 
							{
								diag3 = Math.min(diag3, distMap.getValue(x-1, y-1, z-1));
							}
							diago = Math.min(diago, distMap.getValue(x, y-1, z-1));
							if (x < sizeX - 1) 
							{
								diag3 = Math.min(diag3, distMap.getValue(x+1, y-1, z-1));
							}
						}
						
						// voxels in the y line of the (z-1) plane
						if (x > 0) 
						{
							diago = Math.min(diago, distMap.getValue(x-1, y, z-1));
						}
						ortho = Math.min(ortho, distMap.getValue(x, y, z-1));
						if (x < sizeX - 1) 
						{
							diago = Math.min(diago, distMap.getValue(x+1, y, z-1));
						}

						if (y < sizeY - 1)
						{
							// voxels in the (y+1) line of  the (z-1) plane
							if (x > 0) 
							{
								diag3 = Math.min(diag3, distMap.getValue(x-1, y+1, z-1));
							}
							diago = Math.min(diago, distMap.getValue(x, y+1, z-1));
							if (x < sizeX - 1) 
							{
								diag3 = Math.min(diag3, distMap.getValue(x+1, y+1, z-1));
							}
						}
					}
					
					// voxels in the (y-1) line of the z-plane
					if (y > 0)
					{
						if (x > 0) 
						{
							diago = Math.min(diago, distMap.getValue(x-1, y-1, z));
						}
						ortho = Math.min(ortho, distMap.getValue(x, y-1, z));
						if (x < sizeX - 1) 
						{
							diago = Math.min(diago, distMap.getValue(x+1, y-1, z));
						}
					}
					
					// pixel to the left of the current voxel
					if (x > 0) 
					{
						ortho = Math.min(ortho, distMap.getValue(x-1, y, z));
					}

					double newVal = min3w(ortho, diago, diag3);
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
					double ortho = Double.MAX_VALUE;
					double diago = Double.MAX_VALUE;
					double diag3 = Double.MAX_VALUE;
					
					// process (z+1) slice
					if (z < sizeZ - 1) 
					{
						if (y < sizeY - 1)
						{
							// voxels in the (y+1) line of  the (z+1) plane
							if (x < sizeX - 1) 
							{
								diag3 = Math.min(diag3, distMap.getValue(x+1, y+1, z+1));
							}
							diago = Math.min(diago, distMap.getValue(x, y+1, z+1));
							if (x > 0) 
							{
								diag3 = Math.min(diag3, distMap.getValue(x-1, y+1, z+1));
							}
						}
						
						// voxels in the y line of the (z+1) plane
						if (x < sizeX - 1) 
						{
							diago = Math.min(diago, distMap.getValue(x+1, y, z+1));
						}
						ortho = Math.min(ortho, distMap.getValue(x, y, z+1));
						if (x > 0) 
						{
							diago = Math.min(diago, distMap.getValue(x, y, z+1));
						}

						if (y > 0)
						{
							// voxels in the (y-1) line of  the (z+1) plane
							if (x < sizeX - 1) 
							{
								diag3 = Math.min(diag3, distMap.getValue(x+1, y-1, z+1));
							}
							diago = Math.min(diago, distMap.getValue(x, y-1, z+1));
							if (x > 0) 
							{
								diag3 = Math.min(diag3, distMap.getValue(x-1, y-1, z+1));
							}
						}
					}
					
					// voxels in the (y+1) line of the z-plane
					if (y < sizeY - 1)
					{
						if (x < sizeX - 1) 
						{
							diago = Math.min(diago, distMap.getValue(x+1, y+1, z));
						}
						ortho = Math.min(ortho, distMap.getValue(x, y+1, z));
						if (x > 0) 
						{
							diago = Math.min(diago, distMap.getValue(x-1, y+1, z));
						}
					}
					
					// voxel to the right of the current voxel
					if (x < sizeX - 1) 
					{
						ortho = Math.min(ortho, distMap.getValue(x+1, y, z));
					}
					
					double newVal = min3w(ortho, diago, diag3);
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
	private double min3w(double ortho, double diago, double diag2)
	{
		return min(min(ortho + weights[0], diago + weights[1]), diag2 + weights[2]);
	}
	
	/**
	 * Update the pixel at position (x,y,z) with the value newVal. If newVal is
	 * greater or equal to current value at position (x,y,z), do nothing.
	 */
	private void updateIfNeeded(int x, int y, int z, double newVal)
	{
		double value = distMap.getValue(x, y, z);
		if (newVal < value) 
		{
			distMap.setValue(x, y, z, newVal);
		}
	}
}
