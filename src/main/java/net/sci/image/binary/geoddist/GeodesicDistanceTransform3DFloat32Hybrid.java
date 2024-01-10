package net.sci.image.binary.geoddist;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;

import net.sci.algo.AlgoStub;
import net.sci.array.Arrays;
import net.sci.array.binary.BinaryArray3D;
import net.sci.array.scalar.Float32Array3D;
import net.sci.image.binary.distmap.ChamferMask3D;
import net.sci.image.binary.distmap.ChamferMask3D.Offset;

/**
 * Computation of Chamfer geodesic distances using floating point integer array
 * for storing result, and 5-by-5 chamfer masks.
 * 
 * @author David Legland
 * 
 */
public class GeodesicDistanceTransform3DFloat32Hybrid extends AlgoStub implements GeodesicDistanceTransform3D
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

    // ==================================================
    // Constructors

    /**
     * Use default weights, and normalize map.
     */
    public GeodesicDistanceTransform3DFloat32Hybrid()
    {
        this(ChamferMask3D.BORGEFORS, true);
    }

    public GeodesicDistanceTransform3DFloat32Hybrid(ChamferMask3D mask)
    {
        this(mask, true);
    }

    public GeodesicDistanceTransform3DFloat32Hybrid(ChamferMask3D mask, boolean normalizeMap)
    {
        this.mask = mask;
        this.normalizeMap = normalizeMap;
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
    public GeodesicDistanceTransform3DFloat32Hybrid(float[] weights, boolean normalizeMap)
    {
        this.mask = ChamferMask3D.fromWeights(weights);
        this.normalizeMap = normalizeMap;
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
    public Float32Array3D process3d(BinaryArray3D marker, BinaryArray3D maskImage)
    {
        if (!Arrays.isSameSize(marker, maskImage))
        {
            throw new IllegalArgumentException("Marker and mask arrays must have same dimensions.");
        }

        // create new empty image, and fill it with black
        fireStatusChanged(this, "Initialization...");
        Float32Array3D distMap = initializeResult(marker, maskImage);

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
            normalizeMap(distMap);
        }

        return distMap;
    }

    private Float32Array3D initializeResult(BinaryArray3D marker, BinaryArray3D maskImage)
    {
        int sizeX = marker.size(0);
        int sizeY = marker.size(1);
        int sizeZ = marker.size(2);

        // Allocate memory
        Float32Array3D distMap = Float32Array3D.create(sizeX, sizeY, sizeZ);

        // initialize empty image with either 0 (in marker), Inf (outside
        // marker), or NaN (not in the mask)
        for (int z = 0; z < sizeZ; z++) 
        {
            for (int y = 0; y < sizeY; y++) 
            {
                for (int x = 0; x < sizeX; x++) 
                {
                    if (maskImage.getBoolean(x, y, z))
                    {
                        double val = marker.getValue(x, y, z);
                        distMap.setFloat(x, y, z, val == 0 ? Float.POSITIVE_INFINITY : 0f);
                    }
                    else
                    {
                        distMap.setFloat(x, y, z, Float.NaN);
                    }
                }
            }
	    }
        
        return distMap;
    }

    private void forwardIteration(Float32Array3D distMap, BinaryArray3D maskImage)
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
                   double dist = distMap.getValue(x, y, z);
                   double newDist = dist;
                   
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
                       
                       // process only pixels within mask
                       if (!maskImage.getBoolean(x2, y2, z2))
                       {
                           continue;
                       }
                       
                       // if pixel is within mask, update the distance
                       newDist = Math.min(newDist, distMap.getValue(x2, y2, z2) + offset.weight);
                   }
                   
                   // modify current pixel if needed
                   if (newDist < dist) 
                   {
                       distMap.setValue(x, y, z, newDist);
                   }
               }
           }
        }
        fireProgressChanged(this, 1, 1);
	}

	private void backwardIteration(Float32Array3D distMap, BinaryArray3D maskImage, Deque<int[]> queue)
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
                    double dist = distMap.getValue(x, y, z);
                    double newDist = dist;
                    
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
                        
                        // process only pixels within mask
                        if (!maskImage.getBoolean(x2, y2, z2))
                        {
                            continue;
                        }
                        
                        // if pixel is within mask, update the distance
                        newDist = Math.min(newDist, distMap.getValue(x2, y2, z2) + offset.weight);
                    }                    
                    
                    // check if update is necessary
                    if (dist < newDist)
                    {
                        continue;
                    }
                    
                    // modify current pixel
                    distMap.setValue(x, y, z, newDist);
                    
                    // eventually add lower-right neighbors to queue
                    for (Offset offset : offsets)
                    {
                        // compute neighbor coordinates
                        int x2 = x + offset.dx;
                        int y2 = y + offset.dy;
                        int z2 = z + offset.dz;
                        
                        // check image bounds
                        if (x2 < 0 || x2 > sizeX - 1)
                            continue;
                        if (y2 < 0 || y2 > sizeY - 1)
                            continue;
                        if (z2 < 0 || z2 > sizeZ - 1)
                            continue;
                        
                        // process only pixels within mask
                        if (!maskImage.getBoolean(x2, y2, z2))
                            continue;

                        // update neighbor and add to the queue
                        if (newDist + offset.weight < distMap.getValue(x2, y2, z2)) 
                        {
                            distMap.setValue(x2, y2, z2, newDist + offset.weight);
                            queue.add(new int[] {x2, y2, z2});
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
    private void processQueue(Float32Array3D distMap, BinaryArray3D maskImage, Deque<int[]> queue)
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
            double dist = distMap.getValue(x, y, z);

            // iterate over neighbor pixels
            for (Offset offset : offsets)
            {
                // compute neighbor coordinates
                int x2 = x + offset.dx;
                int y2 = y + offset.dy;
                int z2 = z + offset.dz;
                
                // check image bounds
                if (x2 < 0 || x2 > sizeX - 1)
                    continue;
                if (y2 < 0 || y2 > sizeY - 1)
                    continue;
                if (z2 < 0 || z2 > sizeZ - 1)
                    continue;
                
                // process only pixels within mask
                if (!maskImage.getBoolean(x2, y2, z2))
                    continue;

                // update minimum value
                double newDist = dist + offset.weight;
                
                // if no update is needed, continue to next item in queue
                if (newDist < distMap.getValue(x2, y2, z2))
                {
                    // update result for current position
                    distMap.setValue(x2, y2, z2, newDist);
                    
                    // add the new modified position to the queue 
                    queue.add(new int[] {x2, y2, z2});
                }
            }
        }
    }

    private void normalizeMap(Float32Array3D distMap)
    {
        // retrieve image size
        int sizeX = distMap.size(0);
        int sizeY = distMap.size(1);
        int sizeZ = distMap.size(2);
        double w0 = mask.getNormalizationWeight();

        for (int z = 0; z < sizeZ; z++)
        {
            for (int y = 0; y < sizeY; y++)
            {
                for (int x = 0; x < sizeX; x++)
                {
                    float dist = distMap.getFloat(x, y, z);
                    if (Float.isFinite(dist))
                    {
                        distMap.setValue(x, y, z, dist / w0);
                    }
                }
            }
        }
	}
}
