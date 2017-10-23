package net.sci.image.binary.geoddist;

import java.util.ArrayDeque;
import java.util.Deque;

import net.sci.algo.AlgoStub;
import net.sci.array.data.scalar2d.BinaryArray2D;
import net.sci.array.data.scalar2d.Float32Array2D;
import net.sci.image.binary.ChamferWeights2D;
import net.sci.image.data.Cursor2D;

/**
 * Computation of Chamfer geodesic distances using floating point integer array
 * for storing result, and 5-by-5 chamfer masks.
 * 
 * @author David Legland
 * 
 */
public class GeodesicDistanceTransform2DFloatHybrid5x5 extends AlgoStub implements GeodesicDistanceTransform2D
{
    // ==================================================
    // Class variables 
    
	double[] weights = new double[]{5, 7, 11};

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
	double backgroundValue = Double.POSITIVE_INFINITY;
	
	Float32Array2D buffer;
	
    /** 
     * The queue containing the positions that need update.
     */
    Deque<Cursor2D> queue;


	// ==================================================
    // Constructors 
    
	/**
	 * Use default weights, and normalize map.
	 */
	public GeodesicDistanceTransform2DFloatHybrid5x5()
	{
		this(ChamferWeights2D.CHESSKNIGHT.getFloatWeights(), true);
	}

	public GeodesicDistanceTransform2DFloatHybrid5x5(ChamferWeights2D weights)
	{
		this(weights.getFloatWeights(), true);
	}

	public GeodesicDistanceTransform2DFloatHybrid5x5(ChamferWeights2D weights, boolean normalizeMap) 
	{
		this(weights.getFloatWeights(), normalizeMap);
	}


	public GeodesicDistanceTransform2DFloatHybrid5x5(float[] weights)
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
	public GeodesicDistanceTransform2DFloatHybrid5x5(float[] weights, boolean normalizeMap) 
	{
		this.weights = new double[3];
        this.weights[0] = weights[0];
        this.weights[1] = weights[1];
		
		// ensure weight array has minimum size 3
		if (this.weights.length < 3)
		{
			this.weights[2] = weights[0] + weights[1];
		}
		else
		{
		    this.weights[2] = weights[2];
		}
		
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
    public GeodesicDistanceTransform2DFloatHybrid5x5(double[] weights, boolean normalizeMap) 
    {
        this.weights = weights;
        
        // ensure weight array has minimum size 3
        if (this.weights.length < 3)
        {
            this.weights = new double[3];
            this.weights[0] = weights[0];
            this.weights[1] = weights[1];
            this.weights[2] = weights[0] + weights[1];
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
	public Float32Array2D process(BinaryArray2D marker, BinaryArray2D mask)
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
	    buffer = Float32Array2D.create(sizeX, sizeY);
	    buffer.fillValue(0);
	    
	    // initialize empty image with either 0 (in marker), Inf (outside marker), or NaN (not in the mask)
	    for (int y = 0; y < sizeY; y++) 
	    {
	        for (int x = 0; x < sizeX; x++) 
	        {
	            if (mask.getBoolean(x, y))
	            {
    	            double val = marker.getValue(x, y);
    	            buffer.setValue(x, y, val == 0 ? backgroundValue : 0);
	            }
	            else
	            {
	                buffer.setValue(x, y, Double.NaN);
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
		double w0 = weights[0];
		double w1 = weights[1];
		double w2 = weights[2];
		double[] ws = new double[]{w2, w2, w2, w1, w0, w1, w2, w0};

		// iterate over pixels
		for (int y = 0; y < sizeY; y++)
		{
			fireProgressChanged(this, y, sizeY); 

			for (int x = 0; x < sizeX; x++)
			{
				if (!mask.getBoolean(x, y))
					continue;

				// get value of current pixel
                double value = buffer.getValue(x, y);

				// update value with value of neighbors
                double newVal = buffer.getValue(x, y);
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
					newVal = Math.min(newVal, buffer.getValue(x2, y2) + ws[i]);
				}
				
				// modify current pixel if needed
				if (newVal < value) 
		        {
				    buffer.setValue(x, y, newVal);
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
		double w0 = weights[0];
		double w1 = weights[1];
		double w2 = weights[2];
		double[] ws = new double[]{w2, w2, w2, w1, w0, w1, w2, w0};

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
                double value = buffer.getValue(x, y);

                // update value with value of neighbors
                double newVal = buffer.getValue(x, y);
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
					newVal = Math.min(newVal, buffer.getValue(x2, y2) + ws[i]);
				}
				
                // check if update is necessary
                if (value < newVal)
                {
                    continue;
                }
                
                // modify current pixel
                buffer.setValue(x, y, newVal);
                
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
                    if (newVal + ws[i] < buffer.getValue(x2, y2)) 
                    {
                        buffer.setValue(x2, y2, newVal + ws[i]);
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
        double w0 = weights[0];
        double w1 = weights[1];
        double w2 = weights[2];
        double[] ws = new double[]{w2, w2, w2, w1, w0, w1, w2, w0, w2, w2, w2, w1, w0, w1, w2, w0};

        // Process elements in queue until it is empty
        while (!queue.isEmpty()) 
        {
            Cursor2D p = queue.removeFirst();
            int x = p.getX();
            int y = p.getY();
            
            // get geodesic distance value for current pixel
            double value = buffer.getValue(x, y);

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
                double newVal = value + ws[i];
                
                // if no update is needed, continue to next item in queue
                if (newVal < buffer.getValue(x2, y2))
                {
                    // update result for current position
                    buffer.setValue(x2, y2, newVal);
                    
                    // add the new modified position to the queue 
                    queue.add(new Cursor2D(x2, y2));
                }
            }
        }
	}
	
//	/**
//	 * Adds the current position to the queue if and only if the value
//	 * <code>value<value> is greater than the value of the mask.
//	 * 
//	 * @param x
//	 *            column index
//	 * @param y
//	 *            row index
//	 * @param value
//	 *            the new value at (x, y) position
//	 */
//	private void updateQueue(int x, int y, double value)
//	{
//	    double resultValue = buffer.getValue(x, y); 
//	    if (value < resultValue) 
//	    {
//	        Cursor2D position = new Cursor2D(x, y);
//	        queue.add(position);
//	    }
//	}
	private void normalizeMap()
	{
        for (int y = 0; y < sizeY; y++)
        {
            for (int x = 0; x < sizeX; x++) 
            {
                double val = buffer.getValue(x, y);
                if (val != this.backgroundValue)
                {
                    buffer.setValue(x, y, val / this.weights[0]);
                }
            }
        }
	}
}
