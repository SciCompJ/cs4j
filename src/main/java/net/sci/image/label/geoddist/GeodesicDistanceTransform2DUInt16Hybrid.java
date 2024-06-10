package net.sci.image.label.geoddist;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;

import net.sci.algo.AlgoStub;
import net.sci.array.binary.BinaryArray2D;
import net.sci.array.scalar.IntArray2D;
import net.sci.array.scalar.UInt16;
import net.sci.array.scalar.UInt16Array2D;
import net.sci.image.binary.distmap.ChamferMask2D;
import net.sci.image.binary.distmap.ChamferMask2D.Offset;

/**
 * Computation of Chamfer geodesic distances using 16-bits integer array
 * for storing result, and chamfer masks.
 * 
 * @author David Legland
 * 
 */
public class GeodesicDistanceTransform2DUInt16Hybrid extends AlgoStub implements ChamferGeodesicDistanceTransform2D
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
    public GeodesicDistanceTransform2DUInt16Hybrid()
    {
        this.mask = ChamferMask2D.CHESSKNIGHT;
    }

    public GeodesicDistanceTransform2DUInt16Hybrid(ChamferMask2D mask)
    {
        this.mask = mask;
    }

    public GeodesicDistanceTransform2DUInt16Hybrid(ChamferMask2D mask, boolean normalizeMap)
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
    public GeodesicDistanceTransform2DUInt16Hybrid(short[] weights, boolean normalizeMap)
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
     * with values greater or equal to zero.
     */
    public UInt16Array2D process2d(BinaryArray2D marker, IntArray2D<?> labelMap)
    {
        // TODO: check int overflow?

        // create new empty image, and fill it with black
        fireStatusChanged(this, "Initialization...");
        UInt16Array2D distMap = initializeResult(marker, labelMap);

        // forward iteration
        fireStatusChanged(this, "Forward iteration ");
        forwardIteration(distMap, labelMap);

        // Create the queue containing the positions that need update.
        Deque<int[]> queue = new ArrayDeque<int[]>();
        ;

        // backward iteration
        fireStatusChanged(this, "Backward iteration ");
        backwardIteration(distMap, labelMap, queue);

        // Process queue
        fireStatusChanged(this, "Process queue ");
        processQueue(distMap, labelMap, queue);

        // Normalize values by the first weight
        if (normalizeMap)
        {
            fireStatusChanged(this, "Normalize map");
            normalizeMap(distMap, labelMap);
        }

        return distMap;
    }

    private UInt16Array2D initializeResult(BinaryArray2D marker, IntArray2D<?> labelMap)
    {
        int sizeX = marker.size(0);
        int sizeY = marker.size(1);

        // Allocate memory
        UInt16Array2D distMap = UInt16Array2D.create(sizeX, sizeY);

        // initialize empty image with either 0 (in marker), Inf (outside
        // marker), or NaN (not in the mask)
        for (int y = 0; y < sizeY; y++)
        {
            for (int x = 0; x < sizeX; x++)
            {
                distMap.setInt(x, y, marker.getBoolean(x, y) ? 0 : Integer.MAX_VALUE);
            }
        }

        return distMap;
    }

    private void forwardIteration(UInt16Array2D distMap, IntArray2D<?> labelMap)
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
                int label = labelMap.getInt(x, y);
                if (label == 0) continue;

                // get value of current pixel
                int dist = distMap.getInt(x, y);
                int newDist = dist;

                // iterate over neighbors
                for (Offset offset : offsets)
                {
                    // compute neighbor coordinates
                    int x2 = x + offset.dx;
                    int y2 = y + offset.dy;

                    // check image bounds
                    if (x2 < 0 || x2 > sizeX - 1) continue;
                    if (y2 < 0 || y2 > sizeY - 1) continue;

                    // process only pixels inside structure
                    if (labelMap.getInt(x2, y2) != label) continue;

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

    private void backwardIteration(UInt16Array2D distMap, IntArray2D<?> labelMap, Deque<int[]> queue)
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
                int label = labelMap.getInt(x, y);
                if (label == 0) continue;

                // get value of current pixel
                int dist = distMap.getInt(x, y);
                int newDist = dist;

                // iterate over neighbors
                for (Offset offset : offsets)
                {
                    // compute neighbor coordinates
                    int x2 = x + offset.dx;
                    int y2 = y + offset.dy;

                    // check image bounds
                    if (x2 < 0 || x2 > sizeX - 1) continue;
                    if (y2 < 0 || y2 > sizeY - 1) continue;

                    // process only pixels inside structure
                    if (labelMap.getInt(x2, y2) != label) continue;

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

                // eventually add lower-right neighbors to queue
                for (Offset offset : offsets)
                {
                    // compute neighbor coordinates
                    int x2 = x + offset.dx;
                    int y2 = y + offset.dy;

                    // check image bounds
                    if (x2 < 0 || x2 > sizeX - 1) continue;
                    if (y2 < 0 || y2 > sizeY - 1) continue;

                    // process only pixels within the same label
                    if (labelMap.getInt(x2, y2) != label) continue;

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
    }

    /**
     * For each element in the queue, retrieve their neighbors, try to update them, and
     * eventually add them to the queue.
     */
    private void processQueue(UInt16Array2D distMap, IntArray2D<?> labelMap, Deque<int[]> queue)
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

            int label = labelMap.getInt(x, y);

            // get geodesic distance value for current pixel
            int dist = distMap.getInt(x, y);

            // iterate over neighbor pixels
            for (Offset offset : offsets)
            {
                // compute neighbor coordinates
                int x2 = x + offset.dx;
                int y2 = y + offset.dy;

                // check image bounds
                if (x2 < 0 || x2 > sizeX - 1) continue;
                if (y2 < 0 || y2 > sizeY - 1) continue;

                // process only pixels within the same label
                if (labelMap.getInt(x2, y2) != label) continue;

                // update minimum value
                int newDist = dist + offset.intWeight;

                // if no update is needed, continue to next item in queue
                if (newDist < distMap.getInt(x2, y2))
                {
                    // update result for current position
                    distMap.setInt(x2, y2, newDist);

                    // add the new modified position to the queue
                    queue.add(new int[] { x2, y2 });
                }
            }
        }
    }

    private void normalizeMap(UInt16Array2D distMap, IntArray2D<?> labelMap)
    {
        // retrieve image size
        int sizeX = distMap.size(0);
        int sizeY = distMap.size(1);
        double w0 = mask.getIntegerNormalizationWeight();

        for (int y = 0; y < sizeY; y++)
        {
            for (int x = 0; x < sizeX; x++)
            {
                if (labelMap.getInt(x, y) > 0)
                {
                    int val = distMap.getInt(x, y);
                    if (val < UInt16.MAX_INT)
                    {
                        distMap.setInt(x, y, (int) Math.round(val / w0));
                    }
                }
            }
        }
    }

    @Override
    public ChamferMask2D mask()
    {
        return this.mask;
    }
}
