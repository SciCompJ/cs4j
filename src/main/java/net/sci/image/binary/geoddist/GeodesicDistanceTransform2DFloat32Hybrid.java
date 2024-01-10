package net.sci.image.binary.geoddist;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;

import net.sci.algo.AlgoStub;
import net.sci.array.binary.BinaryArray2D;
import net.sci.array.scalar.Float32Array2D;
import net.sci.image.binary.distmap.ChamferMask2D;
import net.sci.image.binary.distmap.ChamferMask2D.Offset;

/**
 * Computation of Chamfer geodesic distances using floating point integer array
 * for storing result, and 5-by-5 chamfer masks.
 * 
 * @author David Legland
 * 
 */
public class GeodesicDistanceTransform2DFloat32Hybrid extends AlgoStub implements GeodesicDistanceTransform2D
{
    // ==================================================
    // Class variables 
    
    /**
     * The chamfer mask used to propagate distances to neighbor pixels.
     */
    ChamferMask2D mask;
    
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
    public GeodesicDistanceTransform2DFloat32Hybrid()
    {
        this(ChamferMask2D.CHESSKNIGHT, true);
    }

    public GeodesicDistanceTransform2DFloat32Hybrid(ChamferMask2D mask)
    {
        this(mask, true);
    }

    public GeodesicDistanceTransform2DFloat32Hybrid(ChamferMask2D mask, boolean normalizeMap)
    {
        this.mask = mask;
        this.normalizeMap = normalizeMap;
    }

    public GeodesicDistanceTransform2DFloat32Hybrid(float[] weights)
    {
        this(weights, true);
    }

    /**
     * Low-level constructor.
     * 
     * @param weights
     *            the array of weights for orthogonal, diagonal, and eventually
     *            2-by-1 moves
     * @param normalizeMap
     *            the flag for normalizing result
     */
    public GeodesicDistanceTransform2DFloat32Hybrid(float[] weights, boolean normalizeMap)
    {
        this.mask = ChamferMask2D.fromWeights(weights);
        this.normalizeMap = normalizeMap;
    }

    // ==================================================
    // General Methods

    /**
     * Computes the geodesic distance function for each pixel in mask, using the
     * given mask. Mask and marker should be BinaryArray2D the same size and
     * containing binary values.
     * 
     * The function returns a new Float32Array2D the same size as the input,
     * with values greater than or equal to zero.
     * 
     * @param marker
     *            the marker image to initialize the reconstruction from
     * @param maskImage
     *            the binary image that will constrain the reconstruction
     * @return the reconstructed image as a new instance of Float32Array2D
     */
    public Float32Array2D process2d(BinaryArray2D marker, BinaryArray2D mask)
    {
        // create new empty image, and fill it with black
        fireStatusChanged(this, "Initialization..."); 
        Float32Array2D distMap = initializeResult(marker, mask);
        
        // forward iteration
        fireStatusChanged(this, "Forward iteration ");
        forwardIteration(distMap, mask);
        
        // Create the queue containing the positions that need update.
        Deque<int[]> queue = new ArrayDeque<int[]>();;

        // backward iteration
        fireStatusChanged(this, "Backward iteration "); 
        backwardIteration(distMap, mask, queue);
        
        // Process queue
        fireStatusChanged(this, "Process queue "); 
        processQueue(distMap, mask, queue);
        
        // Normalize values by the first weight
        if (normalizeMap) 
        {
            fireStatusChanged(this, "Normalize map");
            normalizeMap(distMap);
        }
        
        return distMap;
	}
	
    private Float32Array2D initializeResult(BinaryArray2D marker, BinaryArray2D mask)
    {
        // retrieve image size
        int sizeX = marker.size(0);
        int sizeY = marker.size(1);
        
        // Allocate memory
        Float32Array2D distMap = Float32Array2D.create(sizeX, sizeY);

        // initialize empty image with either 0 (in marker), Inf (outside marker), or NaN (not in the mask)
        for (int y = 0; y < sizeY; y++) 
        {
            for (int x = 0; x < sizeX; x++) 
            {
                if (mask.getBoolean(x, y))
                {
                    boolean val = marker.getBoolean(x, y);
                    distMap.setFloat(x, y, val ? 0 : Float.POSITIVE_INFINITY);
                }
                else
                {
                    distMap.setFloat(x, y, Float.NaN);
                }
            }
        }
        
        return distMap;
    }
    
    private void forwardIteration(Float32Array2D distMap, BinaryArray2D maskImage) 
    {
        // retrieve image size
        int sizeX = distMap.size(0);
        int sizeY = distMap.size(1);
        Collection<Offset> offsets = mask.getForwardOffsets();

        // iterate over pixels
        for (int y = 0; y < sizeY; y++)
        {
            fireProgressChanged(this, y, sizeY); 

            for (int x = 0; x < sizeX; x++)
            {
                if (!maskImage.getBoolean(x, y))
                    continue;

                // get value of current pixel
                double dist = distMap.getValue(x, y);
                double newDist = dist;
                
                // iterate over neighbors
                for (Offset offset : offsets)
                {
                    // compute neighbor coordinates
                    int x2 = x + offset.dx;
                    int y2 = y + offset.dy;
                    
                    // check bounds
                    if (x2 < 0 || x2 >= sizeX)
                        continue;
                    if (y2 < 0 || y2 >= sizeY)
                        continue;
                    
                    // process only pixels within mask
                    if (!maskImage.getBoolean(x2, y2))
                    {
                        continue;
                    }
                    
                    // if pixel is within mask, update the distance
                    newDist = Math.min(newDist, distMap.getValue(x2, y2) + offset.weight);
                }
                
                // modify current pixel if needed
                if (newDist < dist) 
                {
                    distMap.setValue(x, y, newDist);
                }
            }
        }

        fireProgressChanged(this, 1, 1); 
    }

    private void backwardIteration(Float32Array2D distMap, BinaryArray2D maskImage, Deque<int[]> queue)
    {
        // retrieve image size
        int sizeX = distMap.size(0);
        int sizeY = distMap.size(1);
        Collection<Offset> offsets = mask.getBackwardOffsets();


        // iterate over pixels
        for (int y = sizeY - 1; y >= 0; y--)
        {
            fireProgressChanged(this, sizeY - 1 - y, sizeY); 

            for (int x = sizeX - 1; x >= 0; x--)
            {
                if (!maskImage.getBoolean(x, y))
                    continue;

                // get value of current pixel
                double dist = distMap.getValue(x, y);
                double newDist = dist;
                
                // iterate over neighbors
                for (Offset offset : offsets)
                {
                    // compute neighbor coordinates
                    int x2 = x + offset.dx;
                    int y2 = y + offset.dy;
                    
                    // check bounds
                    if (x2 < 0 || x2 >= sizeX)
                        continue;
                    if (y2 < 0 || y2 >= sizeY)
                        continue;
                    
                    // process only pixels within mask
                    if (!maskImage.getBoolean(x2, y2))
                    {
                        continue;
                    }
                    
                    // if pixel is within mask, update the distance
                    newDist = Math.min(newDist, distMap.getValue(x2, y2) + offset.weight);
                }
                
                // modify current pixel if needed
                if (newDist < dist) 
                {
                    distMap.setValue(x, y, newDist);
                }
                
                // check if update is necessary
                if (dist <= newDist)
                {
                    continue;
                }

                // modify current pixel
                distMap.setValue(x, y, newDist);

                // eventually add lower-right neighbors to queue
                for (Offset offset : offsets)
                {
                    // compute neighbor coordinates
                    int x2 = x + offset.dx;
                    int y2 = y + offset.dy;
                    
                    // check image bounds
                    if (x2 < 0 || x2 > sizeX - 1)
                        continue;
                    if (y2 < 0 || y2 > sizeY - 1)
                        continue;
                    
                    // process only pixels within mask
                    if (!maskImage.getBoolean(x2, y2))
                        continue;

                    // update neighbor and add to the queue
                    if (newDist + offset.weight < distMap.getValue(x2, y2)) 
                    {
                        distMap.setValue(x2, y2, newDist + offset.weight);
                        queue.add(new int[] { x2, y2 });
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
    private void processQueue(Float32Array2D distMap, BinaryArray2D maskImage, Deque<int[]> queue)
    {
        // retrieve image size
        int sizeX = distMap.size(0);
        int sizeY = distMap.size(1);
        Collection<Offset> offsets = mask.getOffsets();

        // Process elements in queue until it is empty
        while (!queue.isEmpty()) 
        {
            int[] p = queue.removeFirst();
            int x = p[0];
            int y = p[1];

            // get geodesic distance value for current pixel
            double dist = distMap.getValue(x, y);

            // iterate over neighbor pixels
            for (Offset offset : offsets)
            {
                // compute neighbor coordinates
                int x2 = x + offset.dx;
                int y2 = y + offset.dy;
                
                // check image bounds
                if (x2 < 0 || x2 > sizeX - 1)
                    continue;
                if (y2 < 0 || y2 > sizeY - 1)
                    continue;
                
                // process only pixels within mask
                if (!maskImage.getBoolean(x2, y2))
                    continue;

                // update minimum value
                double newDist = dist + offset.weight;
                
                // if no update is needed, continue to next item in queue
                if (newDist < distMap.getValue(x2, y2))
                {
                    // update result for current position
                    distMap.setValue(x2, y2, newDist);
                    
                    // add the new modified position to the queue 
                    queue.add(new int[] { x2, y2 });
                }
            }
        }
    }

    private void normalizeMap(Float32Array2D distMap)
    {
        // retrieve image size
        int sizeX = distMap.size(0);
        int sizeY = distMap.size(1);
        double w0 = mask.getNormalizationWeight();

        for (int y = 0; y < sizeY; y++)
        {
            for (int x = 0; x < sizeX; x++) 
            {
                double dist = distMap.getValue(x, y);
                if (Double.isFinite(dist))
                {
                    distMap.setValue(x, y, dist / w0);
                }
            }
        }
    }

}
