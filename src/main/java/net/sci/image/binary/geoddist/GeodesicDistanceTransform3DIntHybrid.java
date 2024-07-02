package net.sci.image.binary.geoddist;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;

import net.sci.algo.AlgoStub;
import net.sci.array.binary.BinaryArray;
import net.sci.array.binary.BinaryArray3D;
import net.sci.array.numeric.IntArray;
import net.sci.array.numeric.IntArray3D;
import net.sci.array.numeric.UInt16Array;
import net.sci.image.binary.distmap.ChamferMask3D;
import net.sci.image.binary.distmap.DistanceTransform;
import net.sci.image.binary.distmap.ChamferMask3D.Offset;

/**
 * Computation of Chamfer geodesic distances using integer array
 * for storing result, and chamfer masks.
 * 
 * @author David Legland
 * 
 */
public class GeodesicDistanceTransform3DIntHybrid extends AlgoStub implements GeodesicDistanceTransform, GeodesicDistanceTransform3D
{
    // ==================================================
    // Class variables 
    
    /**
     * The chamfer mask used to propagate distances to neighbor pixels.
     */
    ChamferMask3D mask;

    /** 
     * The factory to use for creating the result array.
     */
    private IntArray.Factory<?> factory = UInt16Array.defaultFactory;
    
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
    public GeodesicDistanceTransform3DIntHybrid()
    {
        this(ChamferMask3D.BORGEFORS, true);
    }

    public GeodesicDistanceTransform3DIntHybrid(ChamferMask3D mask)
    {
        this(mask, true);
    }

    public GeodesicDistanceTransform3DIntHybrid(ChamferMask3D mask, boolean normalizeMap)
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
    public GeodesicDistanceTransform3DIntHybrid(short[] weights, boolean normalizeMap)
    {
        this.mask = ChamferMask3D.fromWeights(weights);
        this.normalizeMap = normalizeMap;
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
    // General Methods 
    
    /**
     * Computes the geodesic distance function for each pixel in mask, using the
     * given mask. Mask and marker should be BinaryArray3D the same size and
     * containing binary values.
     * 
     * The function returns a new Float32Array3D the same size as the input,
     * with values greater or equal to zero.
     */
	public IntArray3D<?> process3d(BinaryArray3D marker, BinaryArray3D maskImage)
	{
        return IntArray3D.wrap((IntArray<?>) computeResult(marker, maskImage).distanceMap);
	}

    // ==================================================
    // Implementation of the GeodesicDistanceTransform interface

    public DistanceTransform.Result computeResult(BinaryArray marker, BinaryArray maskImage)
    {
        // TODO: should check int overflow
        if (marker.dimensionality() != 3)
        {
            throw new RuntimeException("Requires marker array with dimensionality 3");
        }
        BinaryArray3D marker3d = BinaryArray3D.wrap(marker);
        if (maskImage.dimensionality() != 3)
        {
            throw new RuntimeException("Requires mask array with dimensionality 3");
        }
        BinaryArray3D mask3d = BinaryArray3D.wrap(maskImage);
        
        // create new empty image, and fill it with black
        fireStatusChanged(this, "Initialization..."); 
        IntArray3D<?> distMap = initializeResult(marker3d, mask3d);
        
        // forward iteration
        fireStatusChanged(this, "Forward iteration ");
        forwardIteration(distMap, mask3d);
        
        // initialize queue
        Deque<int[]> queue = new ArrayDeque<int[]>();
        
        // backward iteration
        fireStatusChanged(this, "Backward iteration "); 
        int maxDist = backwardIteration(distMap, mask3d, queue);
        
        // Process queue
        fireStatusChanged(this, "Process queue "); 
        processQueue(distMap, mask3d, queue, maxDist);
        
        // Normalize values by the first weight
        if (this.normalizeMap) 
        {
            fireStatusChanged(this, "Normalize map");
            normalizeMap(distMap, mask3d);
            maxDist = (int) (maxDist / this.mask.getIntegerNormalizationWeight());
        }
        
        return new DistanceTransform.Result(distMap, maxDist);
    }

	private IntArray3D<?> initializeResult(BinaryArray3D marker, BinaryArray3D maskImage)
	{
        int sizeX = marker.size(0);
        int sizeY = marker.size(1);
        int sizeZ = marker.size(2);
        
        // create new empty image, and fill it max value within mask
        IntArray3D<?> distMap = IntArray3D.wrap(factory.create(sizeX, sizeY, sizeZ));
	    
        // initialize empty image with either 0 (in marker), or max int value (outside marker)
        int maxValue = distMap.typeMax().getInt();
        distMap.fillInts((x,y,z) -> marker.getBoolean(x, y, z) ? 0 : maxValue);
        
        return distMap;
	}
	
	private void forwardIteration(IntArray3D<?> distMap, BinaryArray3D maskImage) 
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
                       
                       // process only pixels within mask
                       if (!maskImage.getBoolean(x2, y2, z2))
                       {
                           continue;
                       }
                       
                       // if pixel is within mask, update the distance
                       newDist = Math.min(newDist, distMap.getInt(x2, y2, z2) + offset.intWeight);
                   }
                   
                   // modify current pixel if needed
                   if (newDist < dist) 
                   {
                       distMap.setInt(x, y, z, newDist);
                   }
               }
           }
        }
        fireProgressChanged(this, 1, 1);
	}

	private int backwardIteration(IntArray3D<?> distMap, BinaryArray3D maskImage, Deque<int[]> queue)
	{
	    // retrieve image size
        int sizeX = distMap.size(0);
        int sizeY = distMap.size(1);
        int sizeZ = distMap.size(2);
        Collection<Offset> offsets = mask.getBackwardOffsets();

        // initialize largest distance to 0
        int maxDist = 0;
        
        // iterate over voxels of distance map
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
                        newDist = Math.min(newDist, distMap.getInt(x2, y2, z2) + offset.intWeight);
                    }                    
                    
                    // check if update is necessary
                    if (dist < newDist)
                    {
                        continue;
                    }
                    
                    // modify current pixel
                    distMap.setInt(x, y, z, newDist);
                    maxDist = Math.max(maxDist, newDist);
                    
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
                        if (newDist + offset.intWeight < distMap.getInt(x2, y2, z2)) 
                        {
                            distMap.setInt(x2, y2, z2, newDist + offset.intWeight);
                            queue.add(new int[] {x2, y2, z2});
                        }
                    }
                }
            }
        }
        
		fireProgressChanged(this, 1, 1);
        return maxDist;
	}
	
	/**
     * For each element in the queue, get neighbors, try to update them, and
     * eventually add them to the queue.
     */
	private int processQueue(IntArray3D<?> distMap, BinaryArray3D maskImage, Deque<int[]> queue, int maxDist)
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
                int newDist = dist + offset.intWeight;
                
                // if no update is needed, continue to next item in queue
                if (newDist < distMap.getInt(x2, y2, z2))
                {
                    // update result for current position
                    distMap.setInt(x2, y2, z2, newDist);
                    maxDist = Math.max(maxDist, newDist);
                    
                    // add the new modified position to the queue 
                    queue.add(new int[] {x2, y2, z2});
                }
            }
	    }
        
        return maxDist;
	}

	private void normalizeMap(IntArray3D<?> distMap, BinaryArray3D maskImage)
	{
	    // retrieve image size
        int sizeX = distMap.size(0);
        int sizeY = distMap.size(1);
        int sizeZ = distMap.size(2);
        double w0 = mask.getIntegerNormalizationWeight();
        int maxValue = distMap.typeMax().getInt();

	    for (int z = 0; z < sizeZ; z++)
	    {
	        for (int y = 0; y < sizeY; y++)
	        {
	            for (int x = 0; x < sizeX; x++) 
	            {
	                int dist = distMap.getInt(x, y, z);
	                if (maskImage.getBoolean(x, y, z) && dist != maxValue)
                    {
                        distMap.setInt(x, y, z, (int) Math.round(dist / w0));
                    }
                }
            }
        }
	}
}
