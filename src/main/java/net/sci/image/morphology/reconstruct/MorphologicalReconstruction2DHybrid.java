/**
 * 
 */
package net.sci.image.morphology.reconstruct;

import static java.lang.Math.max;
import static java.lang.Math.min;

import java.util.ArrayDeque;
import java.util.Deque;

import net.sci.algo.AlgoStub;
import net.sci.array.data.scalar2d.ScalarArray2D;
import net.sci.image.data.Connectivity2D;
import net.sci.image.data.Cursor2D;

/**
 * <p>
 * Morphological reconstruction for planar arrays, using hybrid algorithm. The
 * algorithms performs forward scan, backward scan that also initialize a queue
 * of positions that need updates, and finally recursively the positions in the
 * queue.
 * </p>
 * 
 * <p>
 * This class performs the algorithm on the two instances of ScalarArray2D kept
 * in it.
 * </p>
 * 
 * @author David Legland
 *
 */
public class MorphologicalReconstruction2DHybrid extends AlgoStub implements MorphologicalReconstruction2D 
{
	// ==================================================
	// Class variables 
	
	// ==================================================
	// Class variables that could be factorized 
	
	/**
	 * The connectivity of the algorithm, usually either C4 or C8.
	 */
	protected Connectivity2D connectivity = Connectivity2D.C4;

	protected ReconstructionType reconstructionType = ReconstructionType.BY_DILATION;
	
	/**
	 * The sign value associated to reconstruction type.
	 * <ul>
	 * <li>+1 : reconstruction by dilation.</li> 
	 * <li>-1 : reconstruction by erosion.</li>
	 * </ul> 
	 */
	protected int sign = 1;
	

	ScalarArray2D<?> marker;
	ScalarArray2D<?> mask;
	
	ScalarArray2D<?> result;
	
	/** image width */
	int sizeX = 0;
	
	/** image height */
	int sizeY = 0;

	/** the queue containing the positions that need update */
	Deque<Cursor2D> queue;


	// ==================================================
	// Class variables that could be factorized 
	
	/**
	 * Boolean flag for the display of debugging infos.
	 */
	public boolean verbose = false;
	
	/**
	 * Boolean flag for the display of algorithm state in ImageJ status bar
	 */
	public boolean showStatus = true;
	
	/**
	 * Boolean flag for the display of algorithm progress in ImageJ status bar
	 */
	public boolean showProgress = false; 

	
	// ==================================================
	// Constructors 
		
	/**
	 * Creates a new instance of geodesic reconstruction by dilation algorithm,
	 * using the default connectivity 4.
	 */
	public MorphologicalReconstruction2DHybrid()
	{
	}
	
	/**
	 * Creates a new instance of geodesic reconstruction by dilation algorithm,
	 * that specifies the type of reconstruction, and using the connectivity 4.
	 * 
	 * @param type
	 *            the type of reconstruction (erosion or dilation)
	 */
	public MorphologicalReconstruction2DHybrid(ReconstructionType type) 
	{
		setReconstructionType(type);
	}

	/**
	 * Creates a new instance of geodesic reconstruction by dilation algorithm,
	 * that specifies the connectivity to use.
	 * 
	 * @param connectivity
	 *            the 2D connectivity to use (either 4 or 8)
	 */
	public MorphologicalReconstruction2DHybrid(Connectivity2D connectivity)
	{
		setConnectivity(connectivity);
	}

	/**
	 * Creates a new instance of geodesic reconstruction by dilation algorithm,
	 * that specifies the type of reconstruction, and the connectivity to use.
	 * 
	 * @param type
	 *            the type of reconstruction (erosion or dilation)
	 * @param connectivity
	 *            the 2D connectivity to use (either 4 or 8)
	 */
	public MorphologicalReconstruction2DHybrid(ReconstructionType type, Connectivity2D connectivity) 
	{
		setReconstructionType(type);
		setConnectivity(connectivity);
	}

	// ==================================================
	// Accesors and mutators
	
	/**
	 * @return the reconstructionType
	 */
	public ReconstructionType getReconstructionType()
	{
		return reconstructionType;
	}

	/**
	 * @param reconstructionType the reconstructionType to set
	 */
	public void setReconstructionType(ReconstructionType reconstructionType) 
	{
		this.reconstructionType = reconstructionType;
		this.sign = reconstructionType.getSign();
	}

	/**
	 * @return the connectivity
	 */
	public Connectivity2D getConnectivity()
	{
		return connectivity;
	}

	/**
	 * @param connectivity the connectivity to set
	 */
	public void setConnectivity(Connectivity2D connectivity)
	{
		this.connectivity = connectivity;
	}

	

	// ==================================================
	// Methods implementing the MorphologicalReconstruction interface
	
	/**
	 * Run the morphological reconstruction algorithm using the specified arrays
	 * as argument.
	 */
	public ScalarArray2D<?> process(ScalarArray2D<?> marker, ScalarArray2D<?> mask)
	{
		// Keep references to input images
		this.marker = marker;
		this.mask = mask;
		
		// Check sizes are consistent
		this.sizeX = marker.getSize(0);
		this.sizeY = marker.getSize(1);
		if (this.sizeX != mask.getSize(0) || this.sizeY != mask.getSize(1)) 
		{
			throw new IllegalArgumentException("Marker and Mask images must have the same size");
		}
		
		// Check connectivity has a correct value
		if (connectivity != Connectivity2D.C4 && connectivity != Connectivity2D.C8)
		{
			throw new RuntimeException(
					"Connectivity for planar images must be either 4 or 8, not "
							+ connectivity);
		}

		queue = new ArrayDeque<Cursor2D>();
		
		// Initialize the result image with the minimum value of marker and mask
		// images
		initializeResult();
		
		// Display current status
		if (verbose)
		{
			System.out.println("Forward iteration");
		}
		if (showStatus)
		{
			fireStatusChanged(this, "Morpho. Rec. Forward");
		}

		// forward iteration
		if (connectivity == Connectivity2D.C4)
		{
			forwardScanC4();
		} 
		else if (connectivity == Connectivity2D.C8)
		{
			forwardScanC8();
		}

		// Display current status
		if (verbose) 
		{
			System.out.println("Backward iteration");
		}
		if (showStatus)
		{
			fireStatusChanged(this, "Morpho. Rec. Backward");
		}

		// backward iteration
		if (connectivity == Connectivity2D.C4)
		{
			backwardScanC4();
		} 
		else if (connectivity == Connectivity2D.C8)
		{
			backwardScanC8();
		}

		if (verbose) 
		{
			System.out.println("Process queue ");
		}
		if (showStatus)
		{
			fireStatusChanged(this, "Morpho. Rec. Processing queue");
		}

		// Process queue
		if (connectivity == Connectivity2D.C4)
		{
			processQueueC4();
		} 
		else if (connectivity == Connectivity2D.C8)
		{
			processQueueC8();
		}
		
		return this.result;
	}

	
	// ==================================================
	// Inner processing methods
	
	private void initializeResult()
	{
		// Create result image the same size as the mask image
		// TODO: result as ScalarArray or ScalarArray2D ?
		this.result = (ScalarArray2D<?>) this.mask.newInstance(this.sizeX, this.sizeY);
	
		for (int y = 0; y < this.sizeY; y++) 
		{
			for (int x = 0; x < this.sizeX; x++) 
			{
				double v1 = this.marker.getValue(x, y) * this.sign; 
				double v2 = this.mask.getValue(x, y) * this.sign; 
				this.result.setValue(x, y, Math.min(v1, v2) * this.sign);
			}
		}		
	}
	
	/**
	 * Update result image using pixels in the upper left neighborhood,
	 * using the 4-adjacency.
	 */
	private void forwardScanC4() 
	{
		if (showProgress)
		{
			fireProgressChanged(this, 0, this.sizeY);
		}
		
		// Process all other lines
		for (int y = 0; y < this.sizeY; y++) 
		{
			
			if (showProgress)
			{
				fireProgressChanged(this, y, this.sizeY);
			}
	
			// Process pixels in the middle of the line
			for (int x = 0; x < this.sizeX; x++) 
			{
				double currentValue = result.getValue(x, y) * this.sign;
				double maxValue = currentValue;
				
				if (x > 0)
					maxValue = Math.max(maxValue, result.getValue(x-1, y) * this.sign);
				if (y > 0)
					maxValue = Math.max(maxValue, result.getValue(x, y-1) * this.sign);
				
				// update value of current pixel
				maxValue = min(maxValue, mask.getValue(x, y) * this.sign);
				if (maxValue > currentValue) 
				{
					result.setValue(x, y, maxValue * this.sign);
				}
			}
		} // end of forward iteration

		// reset progress display
		if (showProgress)
		{
			fireProgressChanged(this, this.sizeY, this.sizeY);
		}
	}

	/**
	 * Update result image using pixels in the upper left neighborhood,
	 * using the 8-adjacency.
	 */
	private void forwardScanC8()
	{
		if (showProgress)
		{
			fireProgressChanged(this, 0, this.sizeY);
		}
		
		// Process all other lines
		for (int y = 0; y < this.sizeY; y++)
		{
			if (showProgress)
			{
				fireProgressChanged(this, y, this.sizeY);
			}

			// Process pixels in the middle of the line
			for (int x = 0; x < this.sizeX; x++) 
			{
				double currentValue = result.getValue(x, y) * this.sign;
				double maxValue = currentValue;
				
				if (y > 0) 
				{
					// process the 3 values on the line above current pixel
					if (x > 0)
						maxValue = Math.max(maxValue, result.getValue(x-1, y-1) * this.sign);
					maxValue = Math.max(maxValue, result.getValue(x, y-1) * this.sign);
					if (x < this.sizeX - 1)
						maxValue = Math.max(maxValue, result.getValue(x+1, y-1) * this.sign);
				}
				if (x > 0)
					maxValue = Math.max(maxValue, result.getValue(x-1, y) * this.sign);
				
				// update value of current pixel
				maxValue = min(maxValue, mask.getValue(x, y) * this.sign);
				if (maxValue > currentValue) 
				{
					result.setValue(x, y, maxValue * this.sign);
				}
			}
		} // end of forward iteration

		// reset progress display
		if (showProgress)
		{
			fireProgressChanged(this, this.sizeY, this.sizeY);
		}
	}

	/**
	 * Update result image using pixels in the lower-right neighborhood, 
	 * using the 4-adjacency.
	 */
	private void backwardScanC4() 
	{
		if (showProgress)
		{
			fireProgressChanged(this, 0, this.sizeY);
		}
		
		// Process regular lines
		for (int y = this.sizeY-1; y >= 0; y--)
		{
			if (showProgress)
			{
				fireProgressChanged(this, this.sizeY-1-y, this.sizeY);
			}
	
			// Process pixels in the middle of the current line
			// consider pixels on the right and below
			for (int x = this.sizeX - 1; x >= 0; x--) 
			{

				double currentValue = result.getValue(x, y) * this.sign;
				double maxValue = currentValue;
				
				if (x < this.sizeX - 1)
					maxValue = Math.max(maxValue, result.getValue(x+1, y) * this.sign);
				if (y < this.sizeY - 1)
					maxValue = Math.max(maxValue, result.getValue(x, y+1) * this.sign);
				
				// combine with mask
				maxValue = min(maxValue, mask.getValue(x, y) * this.sign);

				// check if update is required
				if (maxValue <= currentValue)
				{
					continue;
				}

				// update value of current pixel
				result.setValue(x, y, maxValue * this.sign);
				
				// eventually add lower-right neighbors to queue
				if (x < this.sizeX - 1) 
					updateQueue(x + 1, y, maxValue);
				if (y < this.sizeY - 1) 
					updateQueue(x, y + 1, maxValue);
			}
		} // end of backward iteration

		// reset progress display
		if (showProgress)
		{
			fireProgressChanged(this, this.sizeY, this.sizeY);
		}
	}

	/**
	 * Update result image using pixels in the lower-right neighborhood, using
	 * the 8-adjacency.
	 */
	private void backwardScanC8() 
	{
		if (showProgress)
		{
			fireProgressChanged(this, 0, this.sizeY);
		}
		
		// Process regular lines
		for (int y = this.sizeY-1; y >= 0; y--)
		{
			if (showProgress)
			{
				fireProgressChanged(this, this.sizeY-1-y, this.sizeY);
			}

			// Process pixels in the middle of the current line
			for (int x = this.sizeX - 1; x >= 0; x--)
			{
				double currentValue = result.getValue(x, y) * this.sign;
				double maxValue = currentValue;
				
				if (y < this.sizeY - 1)
				{
					// process the 3 values on the line below current pixel
					if (x > 0)
						maxValue = Math.max(maxValue, result.getValue(x-1, y+1) * this.sign);
					maxValue = Math.max(maxValue, result.getValue(x, y+1) * this.sign);
					if (x < this.sizeX - 1)
						maxValue = Math.max(maxValue, result.getValue(x+1, y+1) * this.sign);
				}
				if (x < this.sizeX - 1)
					maxValue = Math.max(maxValue, result.getValue(x+1, y) * this.sign);
				
				// combine with mask
				maxValue = min(maxValue, mask.getValue(x, y) * this.sign);

				// check if update is required
				if (maxValue <= currentValue)
				{
					continue;
				}

				// update value of current pixel
				result.setValue(x, y, maxValue * this.sign);
				
				// eventually add lower-right neighbors to queue
				if (x < this.sizeX - 1) 
					updateQueue(x + 1, y, maxValue);
				if (y < this.sizeY - 1) 
				{
					if (x > 0) 
						updateQueue(x - 1, y + 1, maxValue);
					updateQueue(x, y + 1, maxValue);
					if (x < this.sizeX - 1) 
						updateQueue(x + 1, y + 1, maxValue);
				}
			}
		} // end of backward iteration

		// reset progress display
		if (showProgress)
		{
			fireProgressChanged(this, this.sizeY, this.sizeY);
		}
	} 

	/**
	 * Update result image using next pixel in the queue,
	 * using the 4-adjacency.
	 */
	private void processQueueC4() 
	{
		// the maximal value around current pixel
		double value;
		
		while (!queue.isEmpty())
		{
			Cursor2D p = queue.removeFirst();
			int x = p.getX();
			int y = p.getY();
			value = result.getValue(x, y) * sign;
			
			// compare with each one of the four neighbors
			if (x > 0) 
				value = max(value, result.getValue(x - 1, y) * this.sign);
			if (x < this.sizeX - 1) 
				value = max(value, result.getValue(x + 1, y) * this.sign);
			if (y > 0) 
				value = max(value, result.getValue(x, y - 1) * this.sign);
			if (y < this.sizeY - 1) 
				value = max(value, result.getValue(x, y + 1) * this.sign);

			// bound with mask value
			value = min(value, mask.getValue(x, y) * this.sign);
			
			// if no update is needed, continue to next item in queue
			if (value <= result.getValue(x, y) * this.sign) 
				continue;
			
			// update result for current position
			result.setValue(x, y, value * this.sign);

			// Eventually add each neighbor
			if (x > 0)
				updateQueue(x - 1, y, value);
			if (x < sizeX - 1)
				updateQueue(x + 1, y, value);
			if (y > 0)
				updateQueue(x, y - 1, value);
			if (y < sizeY - 1)
				updateQueue(x, y + 1, value);
		}
	}
	
	/**
	 * Update result image using next pixel in the queue,
	 * using the 8-adjacency.
	 */
	private void processQueueC8() 
	{
		// sign for adapting dilation and erosion algorithms
		final int sign = this.reconstructionType.getSign();

		// the maximal value around current pixel
		double value;
		
		while (!queue.isEmpty()) 
		{
			Cursor2D p = queue.removeFirst();
			int x = p.getX();
			int y = p.getY();
			value = result.getValue(x, y) * sign;
			
			// compute bounds of neighborhood
			int xmin = max(x - 1, 0);
			int xmax = min(x + 1, sizeX - 1);
			int ymin = max(y - 1, 0);
			int ymax = min(y + 1, sizeY - 1);

			// compare with each one of the neighbors
			for (int y2 = ymin; y2 <= ymax; y2++) 
			{
				for (int x2 = xmin; x2 <= xmax; x2++)
				{
					value = max(value, result.getValue(x2, y2) * sign);
				}
			}
			
			// bound with mask value
			value = min(value, mask.getValue(x, y) * sign);
			
			// if no update is needed, continue to next item in queue
			if (value <= result.getValue(x, y) * sign) 
				continue;
			
			// update result for current position
			result.setValue(x, y, value * sign);

			// compare with each one of the neighbors
			for (int y2 = ymin; y2 <= ymax; y2++) 
			{
				for (int x2 = xmin; x2 <= xmax; x2++) 
				{
					updateQueue(x2, y2, value);
				}
			}
		}
	}

	/**
	 * Adds the current position to the queue if and only if the value
	 * <code>value<value> is greater than the value of the mask.
	 * 
	 * @param x
	 *            column index
	 * @param y
	 *            row index
	 * @param value
	 *            value at (x, y) position
	 * @param sign
	 *            integer +1 or -1 to manage both erosions and dilations
	 */
	private void updateQueue(int x, int y, double value)
	{
		// update current value only if value is strictly greater
		double maskValue = mask.getValue(x, y) * this.sign;
		value = Math.min(value, maskValue);
		
		double resultValue = result.getValue(x, y) * this.sign; 
		if (value > resultValue) 
		{
			Cursor2D position = new Cursor2D(x, y);
			queue.add(position);
		}
	}

}
