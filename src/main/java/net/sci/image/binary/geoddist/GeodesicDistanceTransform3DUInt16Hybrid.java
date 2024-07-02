package net.sci.image.binary.geoddist;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;

import net.sci.algo.AlgoStub;
import net.sci.array.Arrays;
import net.sci.array.binary.BinaryArray3D;
import net.sci.array.numeric.UInt16;
import net.sci.array.numeric.UInt16Array3D;
import net.sci.image.binary.distmap.ChamferMask3D;
import net.sci.image.binary.distmap.ChamferMask3D.Offset;

/**
 * Computation of geodesic distances on 3D arrays based on chamfer masks for
 * propagating distance, and using 16-bits unsigned integer array for storing
 * result.
 * 
 * Implementation based on an "hybrid" algorithm, that uses two raster scans to
 * initialize distance map, then recursively processes the queue of voxels to
 * update.
 * 
 * @see GeodesicDistanceTransform3DFloat32Hybrid
 * @see GeodesicDistanceTransform2DUInt16Hybrid
 * 
 * @author David Legland
 * 
 */
public class GeodesicDistanceTransform3DUInt16Hybrid extends AlgoStub implements GeodesicDistanceTransform3D
{
    // ==================================================
    // Class variables 
    
    /**
     * The chamfer mask used to propagate distances to neighbor pixels.
     */
    ChamferMask3D mask;

    /**
     * Flag for dividing final distance map by the value first weight. This
     * results in distance map values closer to Euclidean distance.
     */
    boolean normalizeMap = true;

    /**
     * The maximum distance that can be computed with the current mask.
     * Corresponds to the largest possible value of an unsigned short minus the
     * largest mask offset value.
     */
    int maxAllowedDistance;
    

	// ==================================================
    // Constructors 
    
    /**
     * Use default (Svensson's) weights, and normalize map.
     */
    public GeodesicDistanceTransform3DUInt16Hybrid()
    {
        this(ChamferMask3D.SVENSSON_3_4_5_7, true);
    }

    public GeodesicDistanceTransform3DUInt16Hybrid(ChamferMask3D mask)
    {
        this(mask, true);
    }

    public GeodesicDistanceTransform3DUInt16Hybrid(ChamferMask3D mask, boolean normalizeMap)
    {
        this.mask = mask;
        this.normalizeMap = normalizeMap;

        // retrieve the maximum weight to identify the largest possible distance value
        int maxWeight = Integer.MIN_VALUE;
        for (Offset offset : mask.getOffsets())
        {
            maxWeight = Math.max(maxWeight, offset.intWeight);
        }
        this.maxAllowedDistance = UInt16.MAX_INT - maxWeight - 1;
    }

    /**
     * Low-level constructor.
     * 
     * @param weights
     *            the array of weights for orthogonal, diagonal, and
     *            cube-diagonal offsets
     * @param normalizeMap
     *            the flag for normalizing result
     */
    public GeodesicDistanceTransform3DUInt16Hybrid(short[] weights, boolean normalizeMap)
    {
        this(ChamferMask3D.fromWeights(weights), normalizeMap);
    }
    

    // ==================================================
    // General Methods 
    
    /**
     * Computes the geodesic distance function for each pixel in mask, using the
     * given mask. Mask and marker should be BinaryArray3D the same size and
     * containing binary values.
     * 
     * The function returns a new Float32Array3D the same size as the input,
     * with values greater or equal to zero.
     */
	public UInt16Array3D process3d(BinaryArray3D marker, BinaryArray3D maskImage)
	{
	    if (!Arrays.isSameSize(marker, maskImage))
	    {
	        throw new IllegalArgumentException("Marker and mask arrays must have same dimensions.");
	    }
	    
		// create new empty image, and fill it with black
		fireStatusChanged(this, "Initialization..."); 
		UInt16Array3D distMap = initializeResult(marker, maskImage);
		
		// forward iteration
		fireStatusChanged(this, "Forward iteration ");
		forwardIteration(distMap, maskImage);
		
        // initialize queue
		Deque<int[]> queue = new ArrayDeque<int[]>();
        
		// backward iteration
		fireStatusChanged(this, "Backward iteration "); 
		backwardIteration(distMap, maskImage, queue);
		
        // Process queue
		fireStatusChanged(this, "Process queue "); 
		processQueue(distMap, maskImage, queue);
		
		// Normalize values by the first weight
		if (this.normalizeMap) 
		{
		    fireStatusChanged(this, "Normalize map");
		    normalizeMap(distMap, maskImage);
		}
		
		return distMap;
	}

	private UInt16Array3D initializeResult(BinaryArray3D marker, BinaryArray3D maskImage)
	{
        int sizeX = marker.size(0);
        int sizeY = marker.size(1);
        int sizeZ = marker.size(2);
        
	    // Allocate memory
        UInt16Array3D distMap = UInt16Array3D.create(sizeX, sizeY, sizeZ);
	    
        // initialize empty image with either 0 (in marker), or max int value (outside marker)
        distMap.fillInts((x,y,z) -> marker.getBoolean(x, y, z) ? 0 : Integer.MAX_VALUE);
        
        return distMap;
	}
	
	private void forwardIteration(UInt16Array3D distMap, BinaryArray3D maskImage) 
	{
       // retrieve image size
       int sizeX = distMap.size(0);
       int sizeY = distMap.size(1);
       int sizeZ = distMap.size(2);
       Collection<Offset> offsets = mask.getForwardOffsets();

       // iterate over pixels
       for (int z = 0; z < sizeZ; z++)
       {
           fireProgressChanged(this, z, sizeZ); 
           
           for (int y = 0; y < sizeY; y++)
           {
               for (int x = 0; x < sizeX; x++)
               {
                   if (!maskImage.getBoolean(x, y, z))
                       continue;
                   
                   // get value of current pixel
                   int dist = distMap.getInt(x, y, z);
                   int newDist = dist;
                   
                   // iterate over neighbors
                   for (Offset offset : offsets)
                   {
                       // compute neighbor coordinates
                       int x2 = x + offset.dx;
                       int y2 = y + offset.dy;
                       int z2 = z + offset.dz;
                       
                       // check bounds
                       if (x2 < 0 || x2 >= sizeX)
                           continue;
                       if (y2 < 0 || y2 >= sizeY)
                           continue;
                       if (z2 < 0 || z2 >= sizeZ)
                           continue;
                       
                       // if pixel is within mask, update the distance
                       if (maskImage.getBoolean(x2, y2, z2))
                       {
                           newDist = Math.min(newDist, distMap.getInt(x2, y2, z2) + offset.intWeight);
                       }
                   }
                   
                   // modify current pixel if needed
                   if (newDist < dist) 
                   {
                       if (newDist <= this.maxAllowedDistance)
                       {
                           distMap.setInt(x, y, z, newDist);
                       }
                       else
                       {
                           String posString = String.format("(%d,%d,%d)", x, y, z);
                           throw new RuntimeException("Maximum allowed distance value reached at " + posString + ". Try using data type with larger dynamic.");
                       }
                   }
               }
           }
        }
       
        fireProgressChanged(this, 1, 1);
	}

	private void backwardIteration(UInt16Array3D distMap, BinaryArray3D maskImage, Deque<int[]> queue)
	{
	    // retrieve image size
        int sizeX = distMap.size(0);
        int sizeY = distMap.size(1);
        int sizeZ = distMap.size(2);
        Collection<Offset> offsets = mask.getBackwardOffsets();

        for (int z = sizeZ - 1; z >= 0; z--)
        {
            fireProgressChanged(this, sizeZ - 1 - z, sizeZ);
            for (int y = sizeY - 1; y >= 0; y--)
            {
                for (int x = sizeX - 1; x >= 0; x--)
                {
                    if (!maskImage.getBoolean(x, y, z))
                        continue;
                    
                    // get value of current pixel
                    int dist = distMap.getInt(x, y, z);
                    int newDist = dist;
                    
                    // iterate over neighbors
                    for (Offset offset : offsets)
                    {
                        // compute neighbor coordinates
                        int x2 = x + offset.dx;
                        int y2 = y + offset.dy;
                        int z2 = z + offset.dz;
                        
                        // check bounds
                        if (x2 < 0 || x2 >= sizeX) continue;
                        if (y2 < 0 || y2 >= sizeY) continue;
                        if (z2 < 0 || z2 >= sizeZ) continue;
                        
                        // process only pixels within mask
                        if (maskImage.getBoolean(x2, y2, z2))
                        {
                            newDist = Math.min(newDist, distMap.getInt(x2, y2, z2) + offset.intWeight);
                        }
                    }                    
                    
                    // check if update is necessary
                    if (dist <= newDist)
                    {
                        continue;
                    }
                    
                    if (newDist > this.maxAllowedDistance)
                    {
                        String posString = String.format("(%d,%d,%d)", x, y, z);
                        throw new RuntimeException("Maximum allowed distance value reached at " + posString + ". Try using data type with larger dynamic.");
                    }
                    
                    // modify current pixel
                    distMap.setInt(x, y, z, newDist);
                    
                    // eventually add lower-right neighbors to queue
                    for (Offset offset : offsets)
                    {
                        // compute neighbor coordinates
                        int x2 = x + offset.dx;
                        int y2 = y + offset.dy;
                        int z2 = z + offset.dz;
                        
                        // check image bounds
                        if (x2 < 0 || x2 > sizeX - 1) continue;
                        if (y2 < 0 || y2 > sizeY - 1) continue;
                        if (z2 < 0 || z2 > sizeZ - 1) continue;
                        
                        // process only pixels within mask
                        if (!maskImage.getBoolean(x2, y2, z2))
                            continue;

                        // update neighbor and add to the queue
                        int neighDist = newDist + offset.intWeight;
                        if (neighDist < distMap.getInt(x2, y2, z2))
                        {
                            if (neighDist > this.maxAllowedDistance)
                            {
                                String posString = String.format("(%d,%d,%d)", x2, y2, z2);
                                throw new RuntimeException("Maximum allowed distance value reached at " + posString + ". Try using data type with larger dynamic.");
                            }

                            distMap.setInt(x2, y2, z2, neighDist);
                            queue.add(new int[] { x2, y2, z2 });
                        }
                    }
                }
            }
        }
		fireProgressChanged(this, 1, 1); 
	}
	
	/**
     * For each element in the queue, get neighbors, try to update them, and
     * eventually add them to the queue.
     */
	private void processQueue(UInt16Array3D distMap, BinaryArray3D maskImage, Deque<int[]> queue)
	{
	    // retrieve image size
        int sizeX = distMap.size(0);
	    int sizeY = distMap.size(1);
	    int sizeZ = distMap.size(2);
	    Collection<Offset> offsets = mask.getOffsets();

	    // Process elements in queue until it is empty
	    while (!queue.isEmpty()) 
	    {
	        int[] p = queue.removeFirst();
            int x = p[0];
            int y = p[1];
            int z = p[2];

	        // get geodesic distance value for current pixel
	        int dist = distMap.getInt(x, y, z);

            // iterate over neighbor pixels
            for (Offset offset : offsets)
            {
                // compute neighbor coordinates
                int x2 = x + offset.dx;
                int y2 = y + offset.dy;
                int z2 = z + offset.dz;
                
                // check image bounds
                if (x2 < 0 || x2 > sizeX - 1) continue;
                if (y2 < 0 || y2 > sizeY - 1) continue;
                if (z2 < 0 || z2 > sizeZ - 1) continue;
                
                // process only pixels within mask
                if (!maskImage.getBoolean(x2, y2, z2))
                    continue;

                // update minimum value
                int newDist = dist + offset.intWeight;
                
                // if no update is needed, continue to next item in queue
                if (newDist < distMap.getInt(x2, y2, z2))
                {
                    if (newDist > this.maxAllowedDistance)
                    {
                        String posString = String.format("(%d,%d,%d)", x2, y2, z2);
                        throw new RuntimeException("Maximum allowed distance value reached at " + posString + ". Try using data type with larger dynamic.");
                    }
                    
                    // update result for current position
                    distMap.setInt(x2, y2, z2, newDist);
                    
                    // add the new modified position to the queue 
                    queue.add(new int[] {x2, y2, z2});
                }
            }
	    }
	}

	private void normalizeMap(UInt16Array3D distMap, BinaryArray3D maskImage)
	{
	    // retrieve image size
        int sizeX = distMap.size(0);
        int sizeY = distMap.size(1);
        int sizeZ = distMap.size(2);
        
        // normalization factor
        double w0 = mask.getIntegerNormalizationWeight();

        // iterate over voxels
	    for (int z = 0; z < sizeZ; z++)
	    {
	        for (int y = 0; y < sizeY; y++)
	        {
	            for (int x = 0; x < sizeX; x++) 
	            {
	                int dist = distMap.getInt(x, y, z);
	                if (maskImage.getBoolean(x, y, z) && dist != UInt16.MAX_INT)
                    {
                        distMap.setInt(x, y, z, (int) Math.round(dist / w0));
                    }
                }
            }
        }
	}
}
