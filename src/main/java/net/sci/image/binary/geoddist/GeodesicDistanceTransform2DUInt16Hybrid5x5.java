package net.sci.image.binary.geoddist;

import java.util.ArrayDeque;
import java.util.Deque;

import net.sci.algo.AlgoStub;
import net.sci.array.data.scalar2d.BinaryArray2D;
import net.sci.array.data.scalar2d.UInt16Array2D;
import net.sci.image.binary.ChamferWeights2D;
import net.sci.image.data.Cursor2D;

/**
 * Computation of Chamfer geodesic distances using floating point integer array
 * for storing result, and 5-by-5 chamfer masks.
 * 
 * @author David Legland
 * 
 */
public class GeodesicDistanceTransform2DUInt16Hybrid5x5 extends AlgoStub implements GeodesicDistanceTransform2D
{
    // ==================================================
    // Class variables 
    
	short[] weights = new short[]{5, 7, 11};

	/**
	 * Flag for dividing final distance map by the value first weight. 
	 * This results in distance map values closer to Euclidean distance. 
	 */
	boolean normalizeMap = true;

	int sizeX;
	int sizeY;

	BinaryArray2D mask;

	/** 
	 * The value assigned to result pixels that do not belong to the mask. 
	 * Default is Short.MAX_VALUE.
	 */
	short backgroundValue = Short.MAX_VALUE;
	
	UInt16Array2D buffer;
	
    /** 
     * The queue containing the positions that need update.
     */
    Deque<Cursor2D> queue;


	// ==================================================
    // Constructors 
    
	/**
	 * Use default weights, and normalize map.
	 */
	public GeodesicDistanceTransform2DUInt16Hybrid5x5()
	{
		this(ChamferWeights2D.CHESSKNIGHT.getShortWeights(), true);
	}

	public GeodesicDistanceTransform2DUInt16Hybrid5x5(ChamferWeights2D weights)
	{
		this(weights.getShortWeights(), true);
	}

	public GeodesicDistanceTransform2DUInt16Hybrid5x5(ChamferWeights2D weights, boolean normalizeMap) 
	{
		this(weights.getShortWeights(), normalizeMap);
	}


	public GeodesicDistanceTransform2DUInt16Hybrid5x5(short[] weights)
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
	public GeodesicDistanceTransform2DUInt16Hybrid5x5(short[] weights, boolean normalizeMap) 
	{
		this.weights = new short[3];
        this.weights[0] = weights[0];
        this.weights[1] = weights[1];
		
		// ensure weight array has minimum size 3
		if (this.weights.length < 3)
		{
			this.weights[2] = (short) (weights[0] + weights[1]);
		}
		else
		{
		    this.weights[2] = weights[2];
		}
		
		this.normalizeMap = normalizeMap;
	}


    // ==================================================
    // General Methods 
    
	/**
	 * Computes the geodesic distance function for each pixel in mask, using the
	 * given mask. Mask and marker should be BinaryArray2D the same size and
	 * containing binary values.
	 * 
	 * The function returns a new Float32Array2D the same size as the input, with
	 * values greater or equal to zero.
	 */
	public UInt16Array2D process2d(BinaryArray2D marker, BinaryArray2D mask)
	{
		// TODO: check int overflow?
		
		// size of image
		sizeX = mask.getSize(0);
		sizeY = mask.getSize(1);
		
		// update mask
		this.mask = mask;

		// create new empty image, and fill it with black
		fireStatusChanged(this, "Initialization..."); 
		initializeResult(marker, mask);
		
		// forward iteration
		fireStatusChanged(this, "Forward iteration ");
		forwardIteration();
		
		// backward iteration
		fireStatusChanged(this, "Backward iteration "); 
		backwardIteration();
		
        // Process queue
		fireStatusChanged(this, "Process queue "); 
		processQueue();
		
		// Normalize values by the first weight
		if (this.normalizeMap) 
		{
		    fireStatusChanged(this, "Normalize map");
		    normalizeMap();
		}
		
		fireStatusChanged(this, ""); 
		return buffer;
	}

	private void initializeResult(BinaryArray2D marker, BinaryArray2D mask)
	{
	    // Allocate memory
	    buffer = UInt16Array2D.create(sizeX, sizeY);
	    buffer.fillValue(0);
	    
	    // initialize empty image with either 0 (in marker), Inf (outside marker), or NaN (not in the mask)
	    for (int y = 0; y < sizeY; y++) 
	    {
	        for (int x = 0; x < sizeX; x++) 
	        {
	            if (mask.getBoolean(x, y))
	            {
    	            double val = marker.getInt(x, y);
    	            buffer.setInt(x, y, val == 0 ? backgroundValue : 0);
	            }
	        }
	    }
	}
	
	private void forwardIteration() 
	{
		// create array of offsets relative to current pixel
		int[] dx = new int[]{-1, +1, -2, -1, +0, +1, +2, -1};
		int[] dy = new int[]{-2, -2, -1, -1, -1, -1, -1, +0};
		
		// also create corresponding array of weights
		int w0 = weights[0];
		int w1 = weights[1];
		int w2 = weights[2];
		int[] ws = new int[]{w2, w2, w2, w1, w0, w1, w2, w0};

		// iterate over pixels
		for (int y = 0; y < sizeY; y++)
		{
			fireProgressChanged(this, y, sizeY); 

			for (int x = 0; x < sizeX; x++)
			{
				if (!mask.getBoolean(x, y))
					continue;

				// get value of current pixel
                int value = buffer.getInt(x, y);

				// update value with value of neighbors
                int newVal = buffer.getInt(x, y);
				for(int i = 0; i < dx.length; i++)
				{
					// coordinates of neighbor pixel
					int x2 = x + dx[i];
					int y2 = y + dy[i];
					
					// check image bounds
					if (x2 < 0 || x2 > sizeX - 1)
						continue;
					if (y2 < 0 || y2 > sizeY - 1)
						continue;
					
					// process only pixels inside structure
					if (!mask.getBoolean(x2, y2))
						continue;

					// update minimum value
					newVal = Math.min(newVal, buffer.getInt(x2, y2) + ws[i]);
				}
				
				// modify current pixel if needed
				if (newVal < value) 
		        {
				    buffer.setInt(x, y, newVal);
		        }
			}
		}

		fireProgressChanged(this, 1, 1); 
	}

	private void backwardIteration()
	{
		// create array of offsets relative to current pixel
		int[] dx = new int[]{+1, -1, +2, +1, +0, -1, -2, +1};
		int[] dy = new int[]{+2, +2, +1, +1, +1, +1, +1, +0};
		
		// also create corresponding array of weights
        int w0 = weights[0];
        int w1 = weights[1];
        int w2 = weights[2];
        int[] ws = new int[]{w2, w2, w2, w1, w0, w1, w2, w0};

		// initialize queue
		queue = new ArrayDeque<Cursor2D>();
        
		// iterate over pixels
		for (int y = sizeY - 1; y >= 0; y--)
		{
			fireProgressChanged(this, sizeY - 1 - y, sizeY); 

			for (int x = sizeX - 1; x >= 0; x--)
			{
				if (!mask.getBoolean(x, y))
					continue;
				
                // get value of current pixel
                int value = buffer.getInt(x, y);

                // update value with value of neighbors
                int newVal = buffer.getInt(x, y);
				for(int i = 0; i < dx.length; i++)
				{
					// coordinates of neighbor pixel
					int x2 = x + dx[i];
					int y2 = y + dy[i];
					
					// check image bounds
					if (x2 < 0 || x2 > sizeX - 1)
						continue;
					if (y2 < 0 || y2 > sizeY - 1)
						continue;
					
					// process only pixels inside structure
					if (!mask.getBoolean(x2, y2))
						continue;

					// update minimum value
					newVal = Math.min(newVal, buffer.getInt(x2, y2) + ws[i]);
				}
				
                // check if update is necessary
                if (value < newVal)
                {
                    continue;
                }
                
                // modify current pixel
                buffer.setInt(x, y, newVal);
                
                // eventually add lower-right neighbors to queue
                for(int i = 0; i < dx.length; i++)
                {
                    // coordinates of neighbor pixel
                    int x2 = x + dx[i];
                    int y2 = y + dy[i];
                    
                    // check image bounds
                    if (x2 < 0 || x2 > sizeX - 1)
                        continue;
                    if (y2 < 0 || y2 > sizeY - 1)
                        continue;
                    
                    // process only pixels inside structure
                    if (!mask.getBoolean(x2, y2))
                        continue;

                    // update neighbor and add to the queue
                    if (newVal + ws[i] < buffer.getInt(x2, y2)) 
                    {
                        buffer.setInt(x2, y2, newVal + ws[i]);
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
	private void processQueue()
	{
        // create array of offsets relative to current pixel
        int[] dx = new int[]{-1, +1, -2, -1, +0, +1, +2, -1, +1, -1, +2, +1, +0, -1, -2, +1};
        int[] dy = new int[]{-2, -2, -1, -1, -1, -1, -1, +0, +2, +2, +1, +1, +1, +1, +1, +0};

        // also create corresponding array of weights
        int w0 = weights[0];
        int w1 = weights[1];
        int w2 = weights[2];
        int[] ws = new int[]{w2, w2, w2, w1, w0, w1, w2, w0, w2, w2, w2, w1, w0, w1, w2, w0};

        // Process elements in queue until it is empty
        while (!queue.isEmpty()) 
        {
            Cursor2D p = queue.removeFirst();
            int x = p.getX();
            int y = p.getY();
            
            // get geodesic distance value for current pixel
            int value = buffer.getInt(x, y);

            // iterate over neighbor pixels
            for(int i = 0; i < dx.length; i++)
            {
                // coordinates of neighbor pixel
                int x2 = x + dx[i];
                int y2 = y + dy[i];
                
                // check image bounds
                if (x2 < 0 || x2 > sizeX - 1)
                    continue;
                if (y2 < 0 || y2 > sizeY - 1)
                    continue;
                
                // process only pixels inside structure
                if (!mask.getBoolean(x2, y2))
                    continue;

                // update minimum value
                int newVal = value + ws[i];
                
                // if no update is needed, continue to next item in queue
                if (newVal < buffer.getInt(x2, y2))
                {
                    // update result for current position
                    buffer.setInt(x2, y2, newVal);
                    
                    // add the new modified position to the queue 
                    queue.add(new Cursor2D(x2, y2));
                }
            }
        }
	}
	
	private void normalizeMap()
	{
        for (int y = 0; y < sizeY; y++)
        {
            for (int x = 0; x < sizeX; x++) 
            {
                int val = buffer.getInt(x, y);
                if (Double.isFinite(val))
                {
                    buffer.setInt(x, y, val / this.weights[0]);
                }
            }
        }
	}
}