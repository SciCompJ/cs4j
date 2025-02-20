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

import java.util.Collection;

import net.sci.algo.AlgoEvent;
import net.sci.algo.AlgoStub;
import net.sci.array.binary.BinaryArray;
import net.sci.array.binary.BinaryArray3D;
import net.sci.array.numeric.IntArray;
import net.sci.array.numeric.IntArray3D;
import net.sci.array.numeric.ScalarArray3D;
import net.sci.array.numeric.UInt16Array;
import net.sci.image.binary.distmap.ChamferMask3D.Offset;


/**
 * <p>
 * Computes 3D Chamfer distance maps using an arbitrary chamfer mask, and
 * storing result in a 3D array of Integer values with arbitrary type.
 * </p>
 * 
 * <p>
 * This algorithm performs two passes to update the distance map, one forward
 * and one backward. The neighborhood of voxels to consider for update it
 * determined by the chamfer mask. Larger masks usually provide more accurate
 * results, but may increase computation time.
 * </p>
 * 
 * <p>
 * The resulting map is stored within a 3D integer array. This implementation
 * allows to choose the type of the result by specifying the factory to use for
 * creating the result array.
 * </p>
 * 
 * @author David Legland
 * 
 * @see ChamferDistanceTransform2DInt
 * @see ChamferDistanceTransform3DFloat32
 */
public class ChamferDistanceTransform3DInt extends AlgoStub implements DistanceTransform, ChamferDistanceTransform3D
{
    // ==============================================================
    // class members
    
    /**
     * The chamfer mask used to propagate distances to neighbor voxels.
     */
    private ChamferMask3D mask;
    
    /**
     * The factory to use for creating the result array.
     */
    private IntArray.Factory<?> factory = UInt16Array.defaultFactory;
    
    /**
     * Flag for dividing final distance map by the value first weight. This
     * results in distance map values closer to euclidean, but with non integer
     * values.
     */
    private boolean normalizeMap = true;
    
    
    // ==============================================================
    // Constructors
    
    /**
     * Default constructor that specifies the chamfer mask.
     * 
     * @param mask
     *            the chamfer mask to use for propagating distances.
     */
    public ChamferDistanceTransform3DInt(ChamferMask3D mask)
    {
        this.mask = mask;
    }
    
    /**
     * Constructor specifying the chamfer mask and the optional normalization
     * option.
     * 
     * @param mask
     *            the chamfer mask to use for propagating distances.
     * @param normalize
     *            flag indicating whether the final distance map should be
     *            normalized by the first weight
     */
    public ChamferDistanceTransform3DInt(ChamferMask3D mask, boolean normalize)
    {
        this.mask = mask;
        this.normalizeMap = normalize;
    }
    
    /**
     * Set the factory for creating the result array.
     * 
     * @param factory
     *            the factory for creating the result array.
     */
    public void setFactory(IntArray.Factory<?> factory)
    {
        this.factory = factory;
    }
    
    
    // ==================================================
    // Implementation of the DistanceTransform3D interface
    
    /**
     * Computes the distance map from a 3D binary image. Distance is computed
     * for each foreground (white) pixel, as the chamfer distance to the nearest
     * background (black) pixel.
     * 
     * @param array
     *            a 3D binary image with white pixels as foreground
     * @return a new 3D image containing:
     *         <ul>
     *         <li>0 for each background pixel</li>
     *         <li>the distance to the nearest background pixel otherwise</li>
     *         </ul>
     */
    public ScalarArray3D<?> process3d(BinaryArray3D array)
    {
        // create new empty image, and fill it with black
        IntArray3D<?> distMap = initializeResult(array);
        
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
    // Implementation of the DistanceTransform interface
    
    @Override
    public Result computeResult(BinaryArray array)
    {
        if (array.dimensionality() != 3) throw new IllegalArgumentException("Requires an array of dimensionity 3");
        BinaryArray3D array3d = BinaryArray3D.wrap(array);
        
        // Allocate result array
        IntArray3D<?> distMap = initializeResult(array3d);
        
        // Two iterations are enough to compute distance map to boundary
        forwardIteration(distMap, array3d);
        int distMax = backwardIteration(distMap, array3d);
        
        // Normalize values by the first weight
        if (this.normalizeMap)
        {
            normalizeResult(distMap, array3d);
            double w0 = mask.getIntegerNormalizationWeight();
            distMax = (int) (distMax / w0);
        }
        
        return new DistanceTransform.Result(distMap, distMax);
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
    private IntArray3D<?> initializeResult(BinaryArray3D array)
    {
        this.fireStatusChanged(new AlgoEvent(this, "Initialization"));
        
        // size of image
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        int sizeZ = array.size(2);
        
        // create new empty image, and fill it max value within mask
        IntArray3D<?> result = IntArray3D.wrap(factory.create(sizeX, sizeY, sizeZ));
        
        // initialize empty image with either 0 (background) or Inf (foreground)
        int maxValue = result.typeMax().intValue();
        for (int z = 0; z < sizeZ; z++)
        {
            fireProgressChanged(this, z, sizeZ);
            for (int y = 0; y < sizeY; y++)
            {
                for (int x = 0; x < sizeX; x++)
                {
                    boolean inside = array.getBoolean(x, y, z);
                    result.setInt(x, y, z, inside ? maxValue : 0);
                }
            }
        }
        fireProgressChanged(this, 1, 1);
        return result;
    }
    
    // private void forwardIteration()
    private void forwardIteration(IntArray3D<?> distMap, BinaryArray3D maskImage)
    {
        this.fireStatusChanged(new AlgoEvent(this, "Forward Scan"));
        
        // size of image
        int sizeX = maskImage.size(0);
        int sizeY = maskImage.size(1);
        int sizeZ = maskImage.size(2);
        
        // create array of forward shifts
        Collection<Offset> offsets = this.mask.getForwardOffsets();
        
        // iterate on image voxels
        for (int z = 0; z < sizeZ; z++)
        {
            fireProgressChanged(this, z, sizeZ);
            for (int y = 0; y < sizeY; y++)
            {
                for (int x = 0; x < sizeX; x++)
                {
                    // process only pixels within the mask
                    if (!maskImage.getBoolean(x, y, z)) continue;
                    
                    // current distance value
                    int currentDist = distMap.getInt(x, y, z);
                    int newDist = currentDist;
                    
                    for (Offset offset : offsets)
                    {
                        int x2 = x + offset.dx;
                        int y2 = y + offset.dy;
                        int z2 = z + offset.dz;
                        
                        // check that current neighbor is within image
                        if (!distMap.containsPosition(x2, y2, z2)) continue;
                        
                        // update min distance, using fact that background distance is zero
                        newDist = min(newDist, distMap.getInt(x2, y2, z2) + offset.intWeight);
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
    
    private int backwardIteration(IntArray3D<?> distMap, BinaryArray3D maskImage)
    {
        this.fireStatusChanged(new AlgoEvent(this, "Backward Scan"));
        
        // size of image
        int sizeX = maskImage.size(0);
        int sizeY = maskImage.size(1);
        int sizeZ = maskImage.size(2);
        
        // create array of forward shifts
        Collection<Offset> offsets = this.mask.getBackwardOffsets();
        
        // initialize largest distance to 0
        int distMax = 0;
        
        // Iterate over pixels
        for (int z = sizeZ - 1; z >= 0; z--)
        {
            fireProgressChanged(this, sizeZ - 1 - z, sizeZ);
            for (int y = sizeY - 1; y >= 0; y--)
            {
                for (int x = sizeX - 1; x >= 0; x--)
                {
                    // process only pixels within the mask
                    if (!maskImage.getBoolean(x, y, z)) continue;
                    
                    // current distance value
                    int currentDist = distMap.getInt(x, y, z);
                    int newDist = currentDist;
                    
                    for (Offset offset : offsets)
                    {
                        int x2 = x + offset.dx;
                        int y2 = y + offset.dy;
                        int z2 = z + offset.dz;
                        
                        // check that current neighbor is within image
                        if (!distMap.containsPosition(x2, y2, z2)) continue;
                        
                        // update min distance, using fact that background distance is zero
                        newDist = min(newDist, distMap.getInt(x2, y2, z2) + offset.intWeight);
                    }
                    
                    if (newDist < currentDist)
                    {
                        distMap.setInt(x, y, z, newDist);
                    }
                    
                    distMax = Math.max(distMax, newDist);
                }
            }
        }
        
        this.fireProgressChanged(this, sizeZ, sizeZ);
        return distMax;
    } // end of backward iteration
    
    private void normalizeResult(IntArray3D<?> distMap, BinaryArray3D array)
    {
        this.fireStatusChanged(new AlgoEvent(this, "Normalization"));
        
        // size of image
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        int sizeZ = array.size(2);
        
        // retrieve the minimum weight
        double w0 = mask.getIntegerNormalizationWeight();
        
        for (int z = 0; z < sizeZ; z++)
        {
            for (int y = 0; y < sizeY; y++)
            {
                for (int x = 0; x < sizeX; x++) 
                {
                    if (array.getBoolean(x, y, z)) 
                    {
                        distMap.setInt(x, y, z, (int) Math.round(distMap.getInt(x, y, z) / w0));
                    }
                }
            }
        }
    }

    
    // ==================================================
    // Implementation of the ChamferDistanceTransform3D interface
    
    @Override
    public ChamferMask3D mask()
    {
        return this.mask;
    }
}
