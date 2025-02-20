package net.sci.image.binary.geoddist;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;

import net.sci.algo.AlgoStub;
import net.sci.array.binary.BinaryArray;
import net.sci.array.binary.BinaryArray2D;
import net.sci.array.numeric.IntArray;
import net.sci.array.numeric.IntArray2D;
import net.sci.array.numeric.UInt16Array;
import net.sci.image.binary.distmap.ChamferMask2D;
import net.sci.image.binary.distmap.DistanceTransform;

/**
 * Computation of Chamfer geodesic distances using 16-bits unsigned integer
 * array for storing result, and chamfer masks.
 * 
 * @author David Legland
 * 
 */
public class GeodesicDistanceTransform2DIntHybrid extends AlgoStub implements GeodesicDistanceTransform, GeodesicDistanceTransform2D
{
    // ==================================================
    // Class variables

    /**
     * The chamfer mask used to propagate distances to neighbor pixels.
     */
    ChamferMask2D mask;

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
     * Use default mask, and normalize map.
     */
    public GeodesicDistanceTransform2DIntHybrid()
    {
        this(ChamferMask2D.CHESSKNIGHT, true);
    }

    public GeodesicDistanceTransform2DIntHybrid(ChamferMask2D mask)
    {
        this(mask, true);
    }

    public GeodesicDistanceTransform2DIntHybrid(ChamferMask2D mask, boolean normalizeMap)
    {
        this.mask = mask;
        this.normalizeMap = normalizeMap;
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
    public GeodesicDistanceTransform2DIntHybrid(short[] weights, boolean normalizeMap)
    {
        this.mask = ChamferMask2D.fromWeights(weights);
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
     * given mask. Mask and marker should be BinaryArray2D the same size and
     * containing binary values.
     * 
     * The function returns a new Float32Array2D the same size as the input,
     * with values greater or equal to zero.
     * 
     * @param marker
     *            the marker image to initialize the reconstruction from
     * @param maskImage
     *            the binary image that will constrain the reconstruction
     * @return the reconstructed image as a new instance of UInt16Array2D
     */
    public IntArray2D<?> process2d(BinaryArray2D marker, BinaryArray2D maskImage)
    {
        return IntArray2D.wrap((IntArray<?>) computeResult(marker, maskImage).distanceMap);
    }

    
    // ==================================================
    // Implementation of the GeodesicDistanceTransform interface

    public DistanceTransform.Result computeResult(BinaryArray marker, BinaryArray maskImage)
    {
        // TODO: should check int overflow
        if (marker.dimensionality() != 2)
        {
            throw new RuntimeException("Requires marker array with dimensionality 2");
        }
        BinaryArray2D marker2d = BinaryArray2D.wrap(marker);
        if (maskImage.dimensionality() != 2)
        {
            throw new RuntimeException("Requires mask array with dimensionality 2");
        }
        BinaryArray2D mask2d = BinaryArray2D.wrap(maskImage);

        // create new empty image, and fill it with black
        fireStatusChanged(this, "Initialization...");
        IntArray2D<?> distMap = initializeResult(marker2d, mask2d);

        // forward iteration
        fireStatusChanged(this, "Forward iteration ");
        forwardIteration(distMap, mask2d);

        // Create the queue containing the positions that need update.
        Deque<int[]> queue = new ArrayDeque<int[]>();

        // backward iteration
        fireStatusChanged(this, "Backward iteration ");
        int maxDist = backwardIteration(distMap, mask2d, queue);

        // Process queue
        fireStatusChanged(this, "Process queue ");
        processQueue(distMap, mask2d, queue, maxDist);

        // Normalize values by the first weight
        if (normalizeMap)
        {
            fireStatusChanged(this, "Normalize map");
            normalizeMap(distMap, mask2d);
            maxDist = (int) (maxDist / this.mask.getIntegerNormalizationWeight());
        }

        return new DistanceTransform.Result(distMap, maxDist);
    }
    
    private IntArray2D<?> initializeResult(BinaryArray2D marker, BinaryArray2D maskImage)
    {
        int sizeX = marker.size(0);
        int sizeY = marker.size(1);

        // Allocate memory
        IntArray2D<?> distMap = IntArray2D.wrap(factory.create(sizeX, sizeY));
        int maxValue = distMap.typeMax().intValue();

        // initialize empty image with either 0 (in marker), or max int value
        // (outside marker
        distMap.fillInts((x, y) -> marker.getBoolean(x, y) ? 0 : maxValue);

        return distMap;
    }

    private void forwardIteration(IntArray2D<?> distMap, BinaryArray2D maskImage)
    {
        // retrieve image size
        int sizeX = distMap.size(0);
        int sizeY = distMap.size(1);
        Collection<ChamferMask2D.Offset> offsets = mask.getForwardOffsets();

        // iterate over pixels
        for (int y = 0; y < sizeY; y++)
        {
            fireProgressChanged(this, y, sizeY);

            for (int x = 0; x < sizeX; x++)
            {
                if (!maskImage.getBoolean(x, y)) continue;

                // get value of current pixel
                int dist = distMap.getInt(x, y);
                int newDist = dist;

                // iterate over neighbors
                for (ChamferMask2D.Offset offset : offsets)
                {
                    // compute neighbor coordinates
                    int x2 = x + offset.dx;
                    int y2 = y + offset.dy;

                    // check bounds
                    if (x2 < 0 || x2 >= sizeX) continue;
                    if (y2 < 0 || y2 >= sizeY) continue;

                    if (!maskImage.getBoolean(x2, y2))
                    {
                        continue;
                    }

                    // if pixel is within mask, update the distance
                    newDist = Math.min(newDist, distMap.getInt(x2, y2) + offset.intWeight);
                }

                // modify current pixel if needed
                if (newDist < dist)
                {
                    distMap.setInt(x, y, newDist);
                }
            }
        }

        fireProgressChanged(this, 1, 1);
    }

    private int backwardIteration(IntArray2D<?> distMap, BinaryArray2D maskImage, Deque<int[]> queue)
    {
        // retrieve image size
        int sizeX = distMap.size(0);
        int sizeY = distMap.size(1);
        Collection<ChamferMask2D.Offset> offsets = mask.getBackwardOffsets();

        // initialize largest distance to 0
        int maxDist = 0;
        
        // iterate over pixels
        for (int y = sizeY - 1; y >= 0; y--)
        {
            fireProgressChanged(this, sizeY - 1 - y, sizeY);

            for (int x = sizeX - 1; x >= 0; x--)
            {
                if (!maskImage.getBoolean(x, y)) continue;

                // get value of current pixel
                int dist = distMap.getInt(x, y);
                int newDist = dist;

                // iterate over neighbors
                for (ChamferMask2D.Offset offset : offsets)
                {
                    // compute neighbor coordinates
                    int x2 = x + offset.dx;
                    int y2 = y + offset.dy;

                    // check bounds
                    if (x2 < 0 || x2 >= sizeX) continue;
                    if (y2 < 0 || y2 >= sizeY) continue;

                    // process only pixels within mask
                    if (!maskImage.getBoolean(x2, y2))
                    {
                        continue;
                    }

                    // if pixel is within mask, update the distance
                    newDist = Math.min(newDist, distMap.getInt(x2, y2) + offset.intWeight);
                }

                // check if update is necessary
                if (dist <= newDist)
                {
                    continue;
                }

                // modify current pixel
                distMap.setInt(x, y, newDist);
                maxDist = Math.max(maxDist, newDist);

                // eventually add lower-right neighbors to queue
                for (ChamferMask2D.Offset offset : offsets)
                {
                    // compute neighbor coordinates
                    int x2 = x + offset.dx;
                    int y2 = y + offset.dy;

                    // check image bounds
                    if (x2 < 0 || x2 > sizeX - 1) continue;
                    if (y2 < 0 || y2 > sizeY - 1) continue;

                    // process only pixels within mask
                    if (!maskImage.getBoolean(x2, y2)) continue;

                    // update neighbor and add to the queue
                    if (newDist + offset.weight < distMap.getInt(x2, y2))
                    {
                        distMap.setInt(x2, y2, newDist + offset.intWeight);
                        queue.add(new int[] { x2, y2 });
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
    private int processQueue(IntArray2D<?> distMap, BinaryArray2D maskImage, Deque<int[]> queue, int maxDist)
    {
        // retrieve image size
        int sizeX = distMap.size(0);
        int sizeY = distMap.size(1);
        Collection<ChamferMask2D.Offset> offsets = mask.getOffsets();

        // Process elements in queue until it is empty
        while (!queue.isEmpty())
        {
            int[] p = queue.removeFirst();
            int x = p[0];
            int y = p[1];

            // get geodesic distance value for current pixel
            int dist = distMap.getInt(x, y);

            // iterate over neighbor pixels
            for (ChamferMask2D.Offset offset : offsets)
            {
                // compute neighbor coordinates
                int x2 = x + offset.dx;
                int y2 = y + offset.dy;

                // check image bounds
                if (x2 < 0 || x2 > sizeX - 1) continue;
                if (y2 < 0 || y2 > sizeY - 1) continue;

                // process only pixels within mask
                if (!maskImage.getBoolean(x2, y2)) continue;

                // update minimum value
                int newDist = dist + offset.intWeight;

                // if no update is needed, continue to next item in queue
                if (newDist < distMap.getInt(x2, y2))
                {
                    // update result for current position
                    distMap.setInt(x2, y2, newDist);
                    maxDist = Math.max(maxDist, newDist);

                    // add the new modified position to the queue
                    queue.add(new int[] { x2, y2 });
                }
            }
        }
        
        return maxDist;
    }

    private void normalizeMap(IntArray2D<?> distMap, BinaryArray2D maskImage)
    {
        // retrieve image size
        int sizeX = distMap.size(0);
        int sizeY = distMap.size(1);
        double w0 = mask.getIntegerNormalizationWeight();
        int maxValue = distMap.typeMax().intValue();

        // iterate over pixels
        for (int y = 0; y < sizeY; y++)
        {
            for (int x = 0; x < sizeX; x++)
            {
                int val = distMap.getInt(x, y);
                if (maskImage.getBoolean(x, y) && val != maxValue)
                {
                    distMap.setInt(x, y, (int) Math.round(val / w0));
                }
            }
        }
    }
}
