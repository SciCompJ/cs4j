package net.sci.image.binary.geoddist;

import net.sci.algo.AlgoStub;
import net.sci.array.binary.BinaryArray2D;
import net.sci.array.scalar.Float32Array2D;
import net.sci.image.binary.ChamferWeights2D;

/**
 * Computation of Chamfer geodesic distances using floating point integer array
 * for storing result, and 5-by-5 chamfer masks.
 * 
 * @author David Legland
 * 
 */
@Deprecated
public class GeodesicDistanceTransform2DFloat32Scanning5x5 extends AlgoStub implements GeodesicDistanceTransform2D
{
    // ==================================================
    // Class variables 
    
	double[] weights = new double[]{5, 7, 11};

	/**
	 * Flag for dividing final distance map by the value first weight. 
	 * This results in distance map values closer to Euclidean distance. 
	 */
	boolean normalizeMap = true;


	// ==================================================
    // Constructors 
    
	/**
	 * Use default weights, and normalize map.
	 */
	public GeodesicDistanceTransform2DFloat32Scanning5x5()
	{
		this(ChamferWeights2D.CHESSKNIGHT.getFloatWeights(), true);
	}

	public GeodesicDistanceTransform2DFloat32Scanning5x5(ChamferWeights2D weights)
	{
		this(weights.getFloatWeights(), true);
	}

	public GeodesicDistanceTransform2DFloat32Scanning5x5(ChamferWeights2D weights, boolean normalizeMap) 
	{
		this(weights.getFloatWeights(), normalizeMap);
	}


	public GeodesicDistanceTransform2DFloat32Scanning5x5(float[] weights)
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
	public GeodesicDistanceTransform2DFloat32Scanning5x5(float[] weights, boolean normalizeMap) 
	{
		this.weights = new double[3];
        this.weights[0] = weights[0];
        this.weights[1] = weights[1];
		
		// ensure weight array has minimum size 3
		if (weights.length < 3)
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
    public GeodesicDistanceTransform2DFloat32Scanning5x5(double[] weights, boolean normalizeMap) 
    {
        this.weights = weights;
        
        // ensure weight array has minimum size 3
        if (weights.length < 3)
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
	public Float32Array2D process2d(BinaryArray2D marker, BinaryArray2D mask)
	{
        // create new empty image, and fill it with black
        fireStatusChanged(this, "Initialization..."); 
        Float32Array2D distMap = initializeResult(marker, mask);

		// Performs forward and backward scanning until stabilization
		boolean modif = false;
		int iter = 0;
        do 
		{
			modif = false;

			// forward iteration
			fireStatusChanged(this, "Forward iteration " + iter);
			modif = modif || forwardIteration(distMap, mask);

			// backward iteration
			fireStatusChanged(this, "Backward iteration " + iter); 
			modif = modif || backwardIteration(distMap, mask);

			// Iterate while pixels have been modified
			iter++;
		}
		while (modif);

		// Normalize values by the first weight
		if (this.normalizeMap) 
		{
			fireStatusChanged(this, "Normalize map");
			normalizeMap(distMap);
		}
				
		return distMap;
	}

	private Float32Array2D initializeResult(BinaryArray2D marker, BinaryArray2D mask)
	{
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
	
	private boolean forwardIteration(Float32Array2D distMap, BinaryArray2D mask) 
	{
		// create array of offsets relative to current pixel
		int[] dx = new int[]{-1, +1, -2, -1, +0, +1, +2, -1};
		int[] dy = new int[]{-2, -2, -1, -1, -1, -1, -1, +0};
		
		// also create corresponding array of weights
		double w0 = weights[0];
		double w1 = weights[1];
		double w2 = weights[2];
		double[] ws = new double[]{w2, w2, w2, w1, w0, w1, w2, w0};

        int sizeX = distMap.size(0);
        int sizeY = distMap.size(1);

		// iterate over pixels
		boolean modif = false;
		for (int y = 0; y < sizeY; y++)
		{
			fireProgressChanged(this, y, sizeY); 

			for (int x = 0; x < sizeX; x++)
			{
				if (!mask.getBoolean(x, y))
					continue;
				
                // get value of current pixel
                double value = distMap.getValue(x, y);

                // iterate over neighbor pixels
                double newVal = value;
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
					newVal = Math.min(newVal, distMap.getValue(x2, y2) + ws[i]);
				}
				
                // modify current pixel if needed
                if (newVal < value) 
                {
                    modif = true;
                    distMap.setValue(x, y, newVal);
                }
			}
		}
		

		fireProgressChanged(this, 1, 1);
		return modif;
	}

	private boolean backwardIteration(Float32Array2D distMap, BinaryArray2D mask)
	{
		// create array of offsets relative to current pixel
		int[] dx = new int[]{+1, -1, +2, +1, +0, -1, -2, +1};
		int[] dy = new int[]{+2, +2, +1, +1, +1, +1, +1, +0};
		
		// also create corresponding array of weights
		double w0 = weights[0];
		double w1 = weights[1];
		double w2 = weights[2];
		double[] ws = new double[]{w2, w2, w2, w1, w0, w1, w2, w0};

        int sizeX = distMap.size(0);
        int sizeY = distMap.size(1);

		// iterate over pixels
		boolean modif = false;
		for (int y = sizeY - 1; y >= 0; y--)
		{
			fireProgressChanged(this, sizeY - 1 - y, sizeY); 

			for (int x = sizeX - 1; x >= 0; x--)
			{
				if (!mask.getBoolean(x, y))
					continue;
				
                // get value of current pixel
                double value = distMap.getValue(x, y);

                // iterate over neighbor pixels
				double newVal = value;
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
					newVal = Math.min(newVal, distMap.getValue(x2, y2) + ws[i]);
				}
				
                // modify current pixel if needed
                if (newVal < value) 
                {
                    modif = true;
                    distMap.setValue(x, y, newVal);
                }
			}
		}
		
		fireProgressChanged(this, 1, 1);
		return modif;
	}

	private void normalizeMap(Float32Array2D distMap)
	{
        int sizeX = distMap.size(0);
        int sizeY = distMap.size(1);

        for (int y = 0; y < sizeY; y++)
        {
            for (int x = 0; x < sizeX; x++) 
            {
                double val = distMap.getValue(x, y);
                if (Double.isFinite(val))
                {
                    distMap.setValue(x, y, val / this.weights[0]);
                }
            }
        }
	}
}
