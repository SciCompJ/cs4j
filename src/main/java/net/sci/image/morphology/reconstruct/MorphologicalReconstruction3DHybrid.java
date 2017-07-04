/**
 * 
 */
package net.sci.image.morphology.reconstruct;

import static java.lang.Math.max;
import static java.lang.Math.min;
import net.sci.algo.AlgoStub;
import net.sci.array.Array;
import net.sci.array.data.scalar3d.ScalarArray3D;
import net.sci.image.data.Connectivity3D;
import net.sci.image.data.Cursor3D;
import net.sci.image.morphology.MorphologicalReconstruction;

import java.util.ArrayDeque;
import java.util.Deque;


/**
 * <p>
 * Morphological reconstruction for 3D arrays of scalar values, using hybrid
 * algorithm. This class manages both reconstructions by dilation and erosion.
 * </p>
 * 
 * <p>
 * This version first performs forward scan, then performs a backward scan that
 * also add lower-right neighbors to the queue, and finally processes voxels in
 * the queue. It is intended to work on 3D images, using 6 or 26 connectivity.
 * </p>
 * 
 * @author David Legland
 * @see MorphologicalReconstruction2DHybrid 
 */
public class MorphologicalReconstruction3DHybrid extends AlgoStub 
{
	// ==================================================
	// Class variables 
	
	protected MorphologicalReconstruction.Type reconstructionType = MorphologicalReconstruction.Type.BY_DILATION;
	
	/**
	 * The sign value associated to reconstruction type.
	 * <ul>
	 * <li>+1 : reconstruction by dilation.</li> 
	 * <li>-1 : reconstruction by erosion.</li>
	 * </ul> 
	 */
	protected int sign = 1;

	/**
	 * The connectivity of the algorithm, usually either C6 or C26.
	 */
	protected Connectivity3D connectivity = Connectivity3D.C6;

	ScalarArray3D<?> marker;
	ScalarArray3D<?> mask;
	ScalarArray3D<?> result;
	
	/** image width */
	int sizeX = 0;
	/** image height */
	int sizeY = 0;
	/** image depth */
	int sizeZ = 0;

	/** the queue containing the positions that need update */
	Deque<Cursor3D> queue;

	/**
	 * Boolean flag for the display of debugging infos.
	 */
	public boolean verbose = false;
	
	/**
	 * Boolean flag for the display of algorithm state
	 */
	public boolean showStatus = true;
	
	/**
	 * Boolean flag for the display of algorithm progress
	 */
	public boolean showProgress = false; 

	

	// ==================================================
	// Constructors 
		
	/**
	 * Creates a new instance of 3D morphological reconstruction by dilation algorithm,
	 * using the default connectivity 6.
	 */
	public MorphologicalReconstruction3DHybrid()
	{
	}
	
	/**
	 * Creates a new instance of 3D morphological reconstruction algorithm,
	 * that specifies the type of reconstruction, and using the connectivity 6.
	 * 
	 * @param type
	 *            the type of reconstruction (erosion or dilation)
	 */
	public MorphologicalReconstruction3DHybrid(MorphologicalReconstruction.Type type) 
	{
		setReconstructionType(type);
	}

	/**
	 * Creates a new instance of 3D morphological reconstruction algorithm,
	 * that specifies the type of reconstruction, and the connectivity to use.
	 * 
	 * @param type
	 *            the type of reconstruction (erosion or dilation)
	 * @param connectivity
	 *            the 3D connectivity to use (either C6 or C26)
	 */
	public MorphologicalReconstruction3DHybrid(MorphologicalReconstruction.Type type, Connectivity3D connectivity)
	{
		setReconstructionType(type);
		setConnectivity(connectivity);
	}

	/**
	 * Creates a new instance of 3D morphological reconstruction by dilation
	 * algorithm, that specifies the connectivity to use.
	 * 
	 * @param connectivity
	 *            the 3D connectivity to use (either C6 or C26)
	 */
	public MorphologicalReconstruction3DHybrid(Connectivity3D connectivity) 
	{
		setConnectivity(connectivity);
	}


	// ==================================================
	// Accesors and mutators
	
	/**
	 * @return the reconstructionType
	 */
	public MorphologicalReconstruction.Type getReconstructionType() 
	{
		return reconstructionType;
	}

	/**
	 * @param reconstructionType the reconstructionType to set
	 */
	public void setReconstructionType(MorphologicalReconstruction.Type reconstructionType)
	{
		this.reconstructionType = reconstructionType;
		this.sign = reconstructionType.getSign();
	}

	/**
	 * @return the connectivity
	 */
	public Connectivity3D getConnectivity()
	{
		return connectivity;
	}

	/**
	 * @param connectivity the connectivity to set
	 */
	public void setConnectivity(Connectivity3D connectivity)
	{
		this.connectivity = connectivity;
	}


	// ==================================================
	// Methods implementing the MorphologicalReconstruction interface
	
	/**
	 * Run the morphological reconstruction algorithm using the specified arrays
	 * as argument.
	 * 
	 * @param marker
	 *            the 3D array of the marker
	 * @param mask
	 *            the 3D array of the mask
	 * @return the morphological reconstruction of the marker array constrained
	 *         to the mask array
	 */
	public ScalarArray3D<?> process(ScalarArray3D<?> marker, ScalarArray3D<?> mask)
	{
		// Keep references to input images
		this.marker = marker;
		this.mask = mask;

		// Check dimensions consistency
		this.sizeX 	= marker.getSize(0);
		this.sizeY 	= marker.getSize(1);
		this.sizeZ 	= marker.getSize(2);
		if (!Array.isSameSize(marker, mask)) 
		{
			throw new IllegalArgumentException("Marker and Mask images must have the same size");
		}
		
		// Check connectivity has a correct value
		if (connectivity != Connectivity3D.C6 && connectivity != Connectivity3D.C26)
		{
			throw new RuntimeException(
					"Connectivity for stacks must be either 6 or 26, not "
							+ connectivity);
		}

		queue = new ArrayDeque<Cursor3D>();
		
		long t0 = System.currentTimeMillis();
		trace("Initialize result ");
		
		initializeResult();
		if (verbose) 
		{
			long t1 = System.currentTimeMillis();
			System.out.println((t1 - t0) + " ms");
			t0 = t1;
		}

		
		// Display current status
		trace("Forward iteration ");
		if (showStatus)
		{
			fireStatusChanged(this, "Morpho. Rec. Forward");
		}

		forwardScan();
		if (verbose) 
		{
			long t1 = System.currentTimeMillis();
			System.out.println((t1 - t0) + " ms");
			t0 = t1;
		}


		// Display current status
		trace("Backward iteration & Init Queue");
		if (showStatus)
		{
			fireStatusChanged(this, "Morpho. Rec. Backward");
		}
		
		backwardScanInitQueue();
		if (verbose) 
		{
			long t1 = System.currentTimeMillis();
			System.out.println((t1 - t0) + " ms");
			t0 = t1;
		}
		
		// Display current status
		trace("Process queue");
		if (showStatus)
		{
			fireStatusChanged(this, "Morpho. Rec. Process Queue");
		}
		
		processQueue();
		if (verbose)
		{
			long t1 = System.currentTimeMillis();
			System.out.println((t1 - t0) + " ms");
			t0 = t1;
		}

		return this.result;
	}

	
	// ==================================================
	// Inner processing methods
	
	/** 
	 * Initialize the result image with the minimum value of marker and mask
	 * images.
	 */
	private void initializeResult() 
	{
		// Create result image the same size as marker image
		this.result = (ScalarArray3D<?>) mask.newInstance(sizeX, sizeY, sizeZ);
		
			// Initialize integer result stack
			for (int z = 0; z < sizeZ; z++)
			{
				for (int y = 0; y < sizeY; y++)
				{
					for (int x = 0; x < sizeX; x++)
					{
						double v1 = marker.getValue(x, y, z) * this.sign;
						double v2 = mask.getValue(x, y, z) * this.sign;
						result.setValue(x, y, z, min(v1, v2)  * this.sign);
						
					}
				}
			}
//		} 
//		else 
//		{
//			// Initialize the result image with the maximum value of marker and mask
//			// images
//			for (int z = 0; z < sizeZ; z++)
//			{
//				// Extract slices
//				markerSlice = this.markerSlices[z];
//				maskSlice = this.maskSlices[z];
//				resultSlice = this.resultSlices[z];
//				
//				// process current slice
//				for (int i = 0; i < sizeX * sizeY; i++)
//				{
//					float v1 = markerSlice[i];
//					float v2 = maskSlice[i];
//					resultSlice[i] = max(v1, v2);
//				}
//			}
//		}
	}
	
	private void forwardScan() 
	{
		if (this.connectivity == Connectivity3D.C6) 
		{
			forwardScanC6();
		}
		else 
		{
			forwardScanC26();
		}
	}

	/**
	 * Update result image using pixels in the upper left neighborhood, using
	 * the 6-adjacency, assuming pixels are stored in bytes.
	 */
	private void forwardScanC6() 
	{
		// the maximal value around current pixel
		double maxValue;

		// Iterate over voxels
		for (int z = 0; z < sizeZ; z++) 
		{
			if (showProgress)
			{
				fireProgressChanged(this, z, this.sizeZ);
			}

			// process current slice
			for (int y = 0; y < sizeY; y++) 
			{
				for (int x = 0; x < sizeX; x++) 
				{
					double currentValue = result.getValue(x, y, z) * this.sign;
					maxValue = currentValue;
					
					// Iterate over the 3 'upper' neighbors of current pixel
					if (x > 0) 
						maxValue = max(maxValue, result.getValue(x - 1, y, z) * this.sign);
					if (y > 0) 
						maxValue = max(maxValue, result.getValue(x, y - 1, z) * this.sign);
					if (z > 0) 
						maxValue = max(maxValue, result.getValue(x, y, z - 1) * this.sign);
					
					// update value of current voxel
					maxValue = min(maxValue, mask.getValue(x, y, z) * this.sign);
					if (maxValue > currentValue) 
					{
						result.setValue(x, y, z, maxValue * this.sign);
					}
				}
			}
		} // end of voxel iteration
		
		if (showProgress)
		{
			fireProgressChanged(this, this.sizeZ, this.sizeZ);
		}
	}

	/**
	 * Update result image using pixels in the upper left neighborhood, using
	 * the 26-adjacency, assuming pixels are stored using integer data types.
	 */
	private void forwardScanC26() 
	{
		// the maximal value around current pixel
		double maxValue;

		// Iterate over voxels
		for (int z = 0; z < sizeZ; z++)
		{
			if (showProgress)
			{
				fireProgressChanged(this, z, this.sizeZ);
			}

			// process current slice
			for (int y = 0; y < sizeY; y++) 
			{
				for (int x = 0; x < sizeX; x++) 
				{
					double currentValue = result.getValue(x, y, z) * this.sign;
					maxValue = currentValue;

					// Iterate over neighbors of current pixel
					int zmax = min(z + 1, sizeZ);
					for (int z2 = max(z - 1, 0); z2 < zmax; z2++)
					{
						int ymax = z2 == z ? y : min(y + 1, sizeY - 1);
						for (int y2 = max(y - 1, 0); y2 <= ymax; y2++)
						{
							int xmax = (z2 == z && y2 == y) ? x - 1 : min(x + 1, sizeX - 1);
							for (int x2 = max(x - 1, 0); x2 <= xmax; x2++)
							{
								maxValue = max(maxValue, result.getValue(x2, y2, z2) * this.sign);
							}
						}
					}

					// update value of current voxel
					maxValue = min(maxValue, mask.getValue(x, y, z) * sign);
					if (maxValue > currentValue)
					{
						result.setValue(x, y, z, maxValue * this.sign);
					}
				}
			}
		}
		
		if (showProgress)
		{
			fireProgressChanged(this, this.sizeZ, this.sizeZ);
		}
	}


	private void backwardScanInitQueue()
	{
		if (this.connectivity == Connectivity3D.C6) 
		{
			backwardScanInitQueueC6();
		} 
		else 
		{
			backwardScanInitQueueC26();
		}
	}
	/**
	 * Update result image using pixels in the lower right neighborhood, using
	 * the 6-adjacency.
	 */
	private void backwardScanInitQueueC6() 
	{
		// the maximal value around current pixel
		double maxValue;

		// Iterate over voxels
		for (int z = sizeZ - 1; z >= 0; z--) 
		{
			if (showProgress)
			{
				fireProgressChanged(this, sizeZ - 1 - z, this.sizeZ);
			}

			// process current slice
			for (int y = sizeY - 1; y >= 0; y--) 
			{
				for (int x = sizeX - 1; x >= 0; x--)
				{
					double currentValue = result.getValue(x, y, z) * this.sign;
					maxValue = currentValue;
					
					// Iterate over the 3 'lower' neighbors of current voxel
					if (x < sizeX - 1) 
						maxValue = max(maxValue, result.getValue(x + 1, y, z) * this.sign);
					if (y < sizeY - 1) 
						maxValue = max(maxValue, result.getValue(x, y + 1, z) * this.sign);
					if (z < sizeZ - 1)
						maxValue = max(maxValue, result.getValue(x, y, z + 1) * this.sign);
				
					// combine with mask
					maxValue = min(maxValue, mask.getValue(x, y, z) * this.sign);
					
					// check if modification is required
					if (maxValue <= currentValue) 
						continue;

					// update value of current voxel
					result.setValue(x, y, z, maxValue * this.sign);
					
					// eventually add lower-right neighbors to queue
					if (x < sizeX - 1) 
						updateQueue(x + 1, y, z, maxValue);
					if (y < sizeY - 1) 
						updateQueue(x, y + 1, z, maxValue);
					if (z < sizeZ - 1) {
						updateQueue(x, y, z + 1, maxValue);
					}
				}
			}
		}	
		
		if (showProgress)
		{
			fireProgressChanged(this, this.sizeZ, this.sizeZ);
		}
	}
	
	/**
	 * Update result image using pixels in the upper left neighborhood, using
	 * the 26-adjacency.
	 */
	private void backwardScanInitQueueC26() 
	{
		// the maximal value around current pixel
		double maxValue;
		
		// Iterate over voxels
		for (int z = sizeZ - 1; z >= 0; z--)
		{
			if (showProgress)
			{
				fireProgressChanged(this, this.sizeZ - 1 - z, this.sizeZ);
			}
			
			// process current slice
			for (int y = sizeY - 1; y >= 0; y--) 
			{
				for (int x = sizeX - 1; x >= 0; x--)
				{
					double currentValue = result.getValue(x, y, z) * this.sign;
					maxValue = currentValue;
	
					// Iterate over neighbors of current voxel
					for (int z2 = min(z + 1, sizeZ - 1); z2 >= z; z2--) 
					{
						int ymin = z2 == z ? y : max(y - 1, 0); 
						for (int y2 = min(y + 1, sizeY - 1); y2 >= ymin; y2--) 
						{
							int xmin = (z2 == z && y2 == y) ? x : max(x - 1, 0); 
							for (int x2 = min(x + 1, sizeX - 1); x2 >= xmin; x2--)
							{
								maxValue = max(maxValue, result.getValue(x2, y2, z2) * this.sign);
							}
						}
					}
	
					// combine with mask
					maxValue = min(maxValue, mask.getValue(x, y, z) * this.sign);
					
					// check if modification is required
					if (maxValue <= currentValue) 
						continue;

					// update value of current voxel
					result.setValue(x, y, z, maxValue * this.sign);
					
					// eventually add lower-right neighbors to queue
					for (int z2 = min(z + 1, sizeZ - 1); z2 >= z; z2--) 
					{
						int ymin = z2 == z ? y : max(y - 1, 0); 
						for (int y2 = min(y + 1, sizeY - 1); y2 >= ymin; y2--) 
						{
							int xmin = (z2 == z && y2 == y) ? x : max(x - 1, 0); 
							for (int x2 = min(x + 1, sizeX - 1); x2 >= xmin; x2--)
							{
								updateQueue(x2, y2, z2, maxValue);
							}
						}
					}
				}
			}
		}	
		
		if (showProgress)
		{
			fireProgressChanged(this, this.sizeZ, this.sizeZ);
		}
	}
	
	private void processQueue()
	{
		if (this.connectivity == Connectivity3D.C6) 
		{
			processQueueC6();
		} 
		else
		{
			processQueueC26();
		}
	}

	/**
	 * Update result image using next pixel in the queue,
	 * using the 6-adjacency.
	 */
	private void processQueueC6()
	{
		// the maximal value around current pixel
		double value;
		
		while (!queue.isEmpty()) 
		{
			Cursor3D p = queue.removeFirst();
			int x = p.getX();
			int y = p.getY();
			int z = p.getZ();
			value = result.getValue(x, y, z) * this.sign;
			
			// compare with each one of the neighbors
			if (x > 0) 
				value = max(value, result.getValue(x - 1, y, z) * this.sign);
			if (x < sizeX - 1) 
				value = max(value, result.getValue(x + 1, y, z)  * this.sign);
			if (y > 0) 
				value = max(value, result.getValue(x, y - 1, z) * this.sign);
			if (y < sizeY - 1) 
				value = max(value, result.getValue(x, y + 1, z) * this.sign);
			if (z > 0) 
				value = max(value, result.getValue(x, y, z - 1) * this.sign);
			if (z < sizeZ - 1) 
				value = max(value, result.getValue(x, y, z + 1) * this.sign);

			// bound with mask value
			value = min(value, mask.getValue(x, y, z)  * this.sign);
			
			// if no update is needed, continue to next item in queue
			if (value <= result.getValue(x, y, z) * this.sign) 
				continue;
			
			// update result for current position
			result.setValue(x, y, z, value * this.sign);

			// Eventually add each neighbor
			if (x > 0)
				updateQueue(x - 1, y, z, value);
			if (x < sizeX - 1)
				updateQueue(x + 1, y, z, value);
			if (y > 0)
				updateQueue(x, y - 1, z, value);
			if (y < sizeY - 1)
				updateQueue(x, y + 1, z, value);
			if (z > 0)
				updateQueue(x, y, z - 1, value);
			if (z < sizeZ - 1)
				updateQueue(x, y, z + 1, value);
		}
		
	}

	/**
	 * Update result image using next pixel in the queue,
	 * using the 26-adjacency.
	 */
	private void processQueueC26() 
	{
		// the maximal value around current pixel
		double value;
		
		while (!queue.isEmpty()) 
		{
			Cursor3D p = queue.removeFirst();
			int x = p.getX();
			int y = p.getY();
			int z = p.getZ();
			value = result.getValue(x, y, z) * this.sign;
			
			// compute bounds of neighborhood
			int xmin = max(x - 1, 0);
			int xmax = min(x + 1, sizeX - 1);
			int ymin = max(y - 1, 0);
			int ymax = min(y + 1, sizeY - 1);
			int zmin = max(z - 1, 0);
			int zmax = min(z + 1, sizeZ - 1);

			// compare with each one of the neighbors
			for (int z2 = zmin; z2 <= zmax; z2++) 
			{
				for (int y2 = ymin; y2 <= ymax; y2++) 
				{
					for (int x2 = xmin; x2 <= xmax; x2++) 
					{
						value = max(value, result.getValue(x2, y2, z2) * this.sign);
					}
				}
			}
			
			// bound with mask value
			value = min(value, mask.getValue(x, y, z) * this.sign);
			
			// if no update is needed, continue to next item in queue
			if (value <= result.getValue(x, y, z) * this.sign) 
				continue;
			
			// update result for current position
			result.setValue(x, y, z, value * this.sign);

			// compare with each one of the neighbors
			for (int z2 = zmin; z2 <= zmax; z2++) 
			{
				for (int y2 = ymin; y2 <= ymax; y2++) 
				{
					for (int x2 = xmin; x2 <= xmax; x2++) 
					{
						updateQueue(x2, y2, z2, value);
					}
				}
			}
		}
	}

	/**
	 * Adds the current 3D position to the queue if and only if the value
	 * <code>value<value> is greater than the value of the mask.
	 * 
	 * @param x
	 *            column index
	 * @param y
	 *            row index
	 * @param z
	 *            slice index
	 * @param value
	 *            value at (x,y,z) position
	 */
	private void updateQueue(int x, int y, int z, double value)
	{
		// combine current value 
		double maskValue = mask.getValue(x, y, z) * this.sign;
		value = Math.min(value, maskValue);
		
		// Update result value only if value is strictly greater
		double resultValue = result.getValue(x, y, z) * this.sign; 
		if (value > resultValue) 
		{
			Cursor3D position = new Cursor3D(x, y, z);
			queue.add(position);
		}
	}
	
	/**
	 * Display a trace message in the console, if the <code>verbose</code> flag
	 * is true.
	 * 
	 * @param traceMessage
	 *            the message to display
	 */
	protected void trace(String traceMessage)
	{
		// Display current status
		if (verbose) 
		{
			System.out.println(traceMessage);
		}
	}
}
