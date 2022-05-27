package net.sci.image.binary.geoddist;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;

import net.sci.algo.AlgoStub;
import net.sci.array.binary.BinaryArray2D;
import net.sci.array.scalar.UInt16;
import net.sci.array.scalar.UInt16Array2D;
import net.sci.image.binary.distmap.ChamferMask2D;
import net.sci.image.binary.distmap.ChamferMask2D.Offset;
import net.sci.image.data.Cursor2D;

/**
 * Computation of Chamfer geodesic distances using 16-bits unsigned integer array
 * for storing result, and chamfer masks.
 * 
 * @author David Legland
 * 
 */
public class GeodesicDistanceTransform2DUInt16Hybrid extends AlgoStub implements GeodesicDistanceTransform2D
{
    // ==================================================
    // Class variables 
    
    /**
     * The chamfer mask used to propagate distances to neighbor pixels.
     */
    ChamferMask2D mask;
	
	/**
	 * Flag for dividing final distance map by the value first weight. 
	 * This results in distance map values closer to Euclidean distance. 
	 */
	boolean normalizeMap = true;


	// ==================================================
    // Constructors 
    
	/**
	 * Use default mask, and normalize map.
	 */
	public GeodesicDistanceTransform2DUInt16Hybrid()
	{
		this(ChamferMask2D.CHESSKNIGHT, true);
	}

	public GeodesicDistanceTransform2DUInt16Hybrid(ChamferMask2D mask)
	{
		this(mask, true);
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
     * 
     * @param marker
     *            the marker image to initialize the reconstruction from
     * @param maskImage
     *            the binary image that will constrain the reconstruction
     * @return the reconstructed image as a new instance of UInt16Array2D
     */
	public UInt16Array2D process2d(BinaryArray2D marker, BinaryArray2D maskImage)
	{
		// TODO: should check int overflow

		// create new empty image, and fill it with black
		fireStatusChanged(this, "Initialization..."); 
		UInt16Array2D distMap = initializeResult(marker, maskImage);
		
        // forward iteration
        fireStatusChanged(this, "Forward iteration ");
        forwardIteration(distMap, maskImage);
        
        // Create the queue containing the positions that need update.
        Deque<Cursor2D> queue = new ArrayDeque<Cursor2D>();

        // backward iteration
        fireStatusChanged(this, "Backward iteration "); 
        backwardIteration(distMap, maskImage, queue);
        
        // Process queue
        fireStatusChanged(this, "Process queue "); 
        processQueue(distMap, maskImage, queue);
        
        // Normalize values by the first weight
        if (normalizeMap) 
        {
            fireStatusChanged(this, "Normalize map");
            normalizeMap(distMap, maskImage);
        }
        
        return distMap;
	}

	private UInt16Array2D initializeResult(BinaryArray2D marker, BinaryArray2D maskImage)
	{
        int sizeX = marker.size(0);
        int sizeY = marker.size(1);
        
	    // Allocate memory
	    UInt16Array2D distMap = UInt16Array2D.create(sizeX, sizeY);
	    
	    // initialize empty image with either 0 (in marker), or max int value (outside marker
        distMap.fillInts((x,y) -> marker.getBoolean(x, y) ? 0 : Integer.MAX_VALUE);
	    
	    return distMap;
	}
	
	private void forwardIteration(UInt16Array2D distMap, BinaryArray2D maskImage) 
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
                int dist = distMap.getInt(x, y);
                int newDist = dist;
                
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

	private void backwardIteration(UInt16Array2D distMap, BinaryArray2D maskImage, Deque<Cursor2D> queue)
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
                int dist = distMap.getInt(x, y);
                int newDist = dist;
                
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
                    if (x2 < 0 || x2 > sizeX - 1)
                        continue;
                    if (y2 < 0 || y2 > sizeY - 1)
                        continue;
                    
                    // process only pixels within mask
                    if (!maskImage.getBoolean(x2, y2))
                        continue;

                    // update neighbor and add to the queue
                    if (newDist + offset.weight < distMap.getInt(x2, y2)) 
                    {
                        distMap.setInt(x2, y2, newDist + offset.intWeight);
                        queue.add(new Cursor2D(x2, y2));
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
	private void processQueue(UInt16Array2D distMap, BinaryArray2D maskImage, Deque<Cursor2D> queue)
	{
        // retrieve image size
        int sizeX = distMap.size(0);
        int sizeY = distMap.size(1);
        Collection<Offset> offsets = mask.getOffsets();
        
        // Process elements in queue until it is empty
        while (!queue.isEmpty()) 
        {
            Cursor2D p = queue.removeFirst();
            int x = p.getX();
            int y = p.getY();
            
            // get geodesic distance value for current pixel
            int dist = distMap.getInt(x, y);

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
                int newDist = dist + offset.intWeight;
                
                // if no update is needed, continue to next item in queue
                if (newDist < distMap.getInt(x2, y2))
                {
                    // update result for current position
                    distMap.setInt(x2, y2, newDist);
                    
                    // add the new modified position to the queue 
                    queue.add(new Cursor2D(x2, y2));
                }
            }
        }
	}
	
	private void normalizeMap(UInt16Array2D distMap, BinaryArray2D maskImage)
	{
	    // retrieve image size
        int sizeX = distMap.size(0);
        int sizeY = distMap.size(1);
        double w0 = mask.getIntegerNormalizationWeight();

        // iterate over pixels
        for (int y = 0; y < sizeY; y++)
        {
            for (int x = 0; x < sizeX; x++) 
            {
                int val = distMap.getInt(x, y);
                if (maskImage.getBoolean(x, y) && val != UInt16.MAX_VALUE)
                {
                    distMap.setInt(x, y, (int) Math.round(val / w0));
                }
            }
        }
	}
}
