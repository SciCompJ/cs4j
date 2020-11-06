package net.sci.image.binary.geoddist;

import java.util.ArrayDeque;
import java.util.Deque;

import net.sci.algo.AlgoStub;
import net.sci.array.Arrays;
import net.sci.array.scalar.BinaryArray3D;
import net.sci.array.scalar.Float32Array3D;
import net.sci.image.binary.ChamferWeights3D;
import net.sci.image.data.Cursor3D;

/**
 * Computation of Chamfer geodesic distances using floating point integer array
 * for storing result, and 5-by-5 chamfer masks.
 * 
 * @author David Legland
 * 
 */
public class GeodesicDistanceTransform3DFloat32Hybrid3x3 extends AlgoStub implements GeodesicDistanceTransform3D
{
    // ==================================================
    // Class variables 
    
	double[] weights = new double[]{3, 4, 5};

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
	public GeodesicDistanceTransform3DFloat32Hybrid3x3()
	{
		this(ChamferWeights3D.BORGEFORS.getFloatWeights(), true);
	}

	public GeodesicDistanceTransform3DFloat32Hybrid3x3(ChamferWeights3D weights)
	{
		this(weights.getFloatWeights(), true);
	}

	public GeodesicDistanceTransform3DFloat32Hybrid3x3(ChamferWeights3D weights, boolean normalizeMap) 
	{
		this(weights.getFloatWeights(), normalizeMap);
	}


	public GeodesicDistanceTransform3DFloat32Hybrid3x3(float[] weights)
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
	public GeodesicDistanceTransform3DFloat32Hybrid3x3(float[] weights, boolean normalizeMap) 
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
    public GeodesicDistanceTransform3DFloat32Hybrid3x3(double[] weights, boolean normalizeMap) 
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
	 * given mask. Mask and marker should be BinaryArray3D the same size and
	 * containing binary values.
	 * 
	 * The function returns a new Float32Array3D the same size as the input, with
	 * values greater or equal to zero.
	 */
	public Float32Array3D process3d(BinaryArray3D marker, BinaryArray3D mask)
	{
	    if (!Arrays.isSameSize(marker, mask))
	    {
	        throw new IllegalArgumentException("Marker and mask arrays must have same dimensions.");
	    }
	    
		// create new empty image, and fill it with black
		fireStatusChanged(this, "Initialization..."); 
		Float32Array3D distMap = initializeResult(marker, mask);
		
		// forward iteration
		fireStatusChanged(this, "Forward iteration ");
		forwardIteration(distMap, mask);
		
        // initialize queue
		Deque<Cursor3D> queue = new ArrayDeque<Cursor3D>();
        
		// backward iteration
		fireStatusChanged(this, "Backward iteration "); 
		backwardIteration(distMap, mask, queue);
		
        // Process queue
		fireStatusChanged(this, "Process queue "); 
		processQueue(distMap, mask, queue);
		
		// Normalize values by the first weight
		if (this.normalizeMap) 
		{
		    fireStatusChanged(this, "Normalize map");
		    normalizeMap(distMap);
		}
		
		return distMap;
	}

	private Float32Array3D initializeResult(BinaryArray3D marker, BinaryArray3D mask)
	{
        int sizeX = marker.size(0);
        int sizeY = marker.size(1);
        int sizeZ = marker.size(2);
        
	    // Allocate memory
        Float32Array3D distMap = Float32Array3D.create(sizeX, sizeY, sizeZ);
	    
	    // initialize empty image with either 0 (in marker), Inf (outside marker), or NaN (not in the mask)
        for (int z = 0; z < sizeZ; z++) 
        {
            for (int y = 0; y < sizeY; y++) 
            {
                for (int x = 0; x < sizeX; x++) 
                {
                    if (mask.getBoolean(x, y, z))
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
	
	private void forwardIteration(Float32Array3D distMap, BinaryArray3D mask) 
	{
	    int[][] offsetList = new int[][]{
               {-1, -1, -1},
               { 0, -1, -1},
               {+1, -1, -1},
               {-1,  0, -1},
               { 0,  0, -1},
               {+1,  0, -1},
               {-1, +1, -1},
               { 0, +1, -1},
               {+1, +1, -1},
               {-1, -1,  0},
               { 0, -1,  0},
               {+1, -1,  0},
               {-1,  0,  0},
       };
       
       double[] ws = new double[]{
               weights[2], weights[1], weights[2], 
               weights[1], weights[0], weights[1], 
               weights[2], weights[1], weights[2], 
               weights[1], weights[0], weights[1], 
               weights[0]
       };

       int sizeX = distMap.size(0);
       int sizeY = distMap.size(1);
       int sizeZ = distMap.size(2);
       
       // iterate over pixels
       for (int z = 0; z < sizeZ; z++)
       {
           fireProgressChanged(this, z, sizeZ); 
           
           for (int y = 0; y < sizeY; y++)
           {
               for (int x = 0; x < sizeX; x++)
               {
                   if (!mask.getBoolean(x, y, z))
                       continue;
                   
                   // get value of current pixel
                   double value = distMap.getValue(x, y, z);
                   
                   // update value with value of neighbors
                   double newVal = value;
                   for(int i = 0; i < offsetList.length; i++)
                   {
                       // coordinates of neighbor pixel
                       int[] offset = offsetList[i];
                       int x2 = x + offset[0];
                       int y2 = y + offset[1];
                       int z2 = z + offset[2];
                       
                       // check image bounds
                       if (x2 < 0 || x2 > sizeX - 1)
                           continue;
                       if (y2 < 0 || y2 > sizeY - 1)
                           continue;
                       if (z2 < 0 || z2 > sizeZ - 1)
                           continue;
                       
                       // process only pixels inside structure
                       if (!mask.getBoolean(x2, y2, z2))
                           continue;
                       
                       // update minimum value
                       newVal = Math.min(newVal, distMap.getValue(x2, y2, z2) + ws[i]);
                   }
                   
                   // modify current pixel if needed
                   if (newVal < value) 
                   {
                       distMap.setValue(x, y, z, newVal);
                   }
               }
           }
        }
        fireProgressChanged(this, 1, 1);
	}

	private void backwardIteration(Float32Array3D distMap, BinaryArray3D mask, Deque<Cursor3D> queue)
	{
	    int[][] offsetList = new int[][]{
	        {+1, +1, +1},
	        { 0, +1, +1},
	        {-1, +1, +1},
	        {+1,  0, +1},
	        { 0,  0, +1},
	        {-1,  0, +1},
	        {+1, -1, +1},
	        { 0, -1, +1},
	        {-1, -1, +1},
	        {+1, +1,  0},
	        { 0, +1,  0},
	        {-1, +1,  0},
	        {+1,  0,  0},
	    };
	    
	    double[] ws = new double[]{
	            weights[2], weights[1], weights[2], 
	            weights[1], weights[0], weights[1], 
	            weights[2], weights[1], weights[2], 
	            weights[1], weights[0], weights[1], 
	            weights[0]
	    };
	    
        int sizeX = distMap.size(0);
        int sizeY = distMap.size(1);
        int sizeZ = distMap.size(2);
        
        for (int z = sizeZ - 1; z >= 0; z--)
        {
            fireProgressChanged(this, sizeZ - 1 - z, sizeZ);
            for (int y = sizeY - 1; y >= 0; y--)
            {
                for (int x = sizeX - 1; x >= 0; x--)
                {
                    if (!mask.getBoolean(x, y, z))
                        continue;
                    
                    // get value of current pixel
                    double value = distMap.getValue(x, y, z);
                    
                    // update value with value of neighbors
                    double newVal = value;
                    for (int i = 0; i < offsetList.length; i++)
                    {
                        // coordinates of neighbor pixel
                        int[] offset = offsetList[i];
                        int x2 = x + offset[0];
                        int y2 = y + offset[1];
                        int z2 = z + offset[2];
                        
                        // check image bounds
                        if (x2 < 0 || x2 > sizeX - 1)
                            continue;
                        if (y2 < 0 || y2 > sizeY - 1)
                            continue;
                        if (z2 < 0 || z2 > sizeZ - 1)
                            continue;
                        
                        // process only pixels inside structure
                        if (!mask.getBoolean(x2, y2, z2))
                            continue;
                        
                        // update minimum value
                        newVal = Math.min(newVal, distMap.getValue(x2, y2, z2) + ws[i]);
                    }
                    
                    // check if update is necessary
                    if (value < newVal)
                    {
                        continue;
                    }
                    
                    // modify current pixel
                    distMap.setValue(x, y, z, newVal);
                    
                    // eventually add lower-right neighbors to queue
                    for (int i = 0; i < offsetList.length; i++)
                    {
                        // coordinates of neighbor pixel
                        int[] offset = offsetList[i];
                        int x2 = x + offset[0];
                        int y2 = y + offset[1];
                        int z2 = z + offset[2];
                        
                        // check image bounds
                        if (x2 < 0 || x2 > sizeX - 1)
                            continue;
                        if (y2 < 0 || y2 > sizeY - 1)
                            continue;
                        if (z2 < 0 || z2 > sizeZ - 1)
                            continue;
                        
                        // process only pixels inside structure
                        if (!mask.getBoolean(x2, y2, z2))
                            continue;
                        
                        // update neighbor and add to the queue
                        if (newVal + ws[i] < distMap.getValue(x2, y2, z2))
                        {
                            distMap.setValue(x2, y2, z2, newVal + ws[i]);
                            queue.add(new Cursor3D(x2, y2, z2));
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
	private void processQueue(Float32Array3D distMap, BinaryArray3D mask, Deque<Cursor3D> queue)
	{
	    int[][] offsetList = new int[][]{
               {-1, -1, -1},
               { 0, -1, -1},
               {+1, -1, -1},
               {-1,  0, -1},
               { 0,  0, -1},
               {+1,  0, -1},
               {-1, +1, -1},
               { 0, +1, -1},
               {+1, +1, -1},
               {-1, -1,  0},
               { 0, -1,  0},
               {+1, -1,  0},
               {-1,  0,  0},
               {+1, +1, +1},
               { 0, +1, +1},
               {-1, +1, +1},
               {+1,  0, +1},
               { 0,  0, +1},
               {-1,  0, +1},
               {+1, -1, +1},
               { 0, -1, +1},
               {-1, -1, +1},
               {+1, +1,  0},
               { 0, +1,  0},
               {-1, +1,  0},
               {+1,  0,  0},
	    };

	    double[] ws = new double[]{
	            weights[2], weights[1], weights[2], 
	            weights[1], weights[0], weights[1], 
	            weights[2], weights[1], weights[2], 
	            weights[1], weights[0], weights[1], 
	            weights[0],
	            weights[2], weights[1], weights[2], 
	            weights[1], weights[0], weights[1], 
	            weights[2], weights[1], weights[2], 
	            weights[1], weights[0], weights[1], 
	            weights[0]
	    };

	    int sizeX = distMap.size(0);
	    int sizeY = distMap.size(1);
	    int sizeZ = distMap.size(2);

	    // Process elements in queue until it is empty
	    while (!queue.isEmpty()) 
	    {
	        Cursor3D p = queue.removeFirst();
	        int x = p.getX();
	        int y = p.getY();
	        int z = p.getZ();

	        // get geodesic distance value for current pixel
	        double value = distMap.getValue(x, y, z);

	        // iterate over neighbor pixels
	        for(int i = 0; i < offsetList.length; i++)
	        {
	            // coordinates of neighbor pixel
	            int[] offset = offsetList[i];
	            int x2 = x + offset[0];
	            int y2 = y + offset[1];
	            int z2 = z + offset[2];

	            // check image bounds
	            if (x2 < 0 || x2 > sizeX - 1)
	                continue;
	            if (y2 < 0 || y2 > sizeY - 1)
	                continue;
	            if (z2 < 0 || z2 > sizeZ - 1)
	                continue;

	            // process only pixels inside structure
	            if (!mask.getBoolean(x2, y2, z2))
	                continue;

	            // update minimum value
	            double newVal = value + ws[i];

	            // if no update is needed, continue to next item in queue
	            if (newVal < distMap.getValue(x2, y2, z2))
	            {
	                // update result for current position
	                distMap.setValue(x2, y2, z2, newVal);

	                // add the new modified position to the queue 
	                queue.add(new Cursor3D(x2, y2, z2));
	            }
	        }
	    }
	}

	private void normalizeMap(Float32Array3D distMap)
	{
        int sizeX = distMap.size(0);
        int sizeY = distMap.size(1);
        int sizeZ = distMap.size(2);

	    for (int z = 0; z < sizeZ; z++)
	    {
	        for (int y = 0; y < sizeY; y++)
	        {
	            for (int x = 0; x < sizeX; x++) 
	            {
	                float val = distMap.getFloat(x, y, z);
                    if (Float.isFinite(val))
                    {
                        distMap.setValue(x, y, z, val / this.weights[0]);
                    }
                }
            }
        }
	}
}
