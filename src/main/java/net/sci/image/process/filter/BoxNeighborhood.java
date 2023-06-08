/**
 * 
 */
package net.sci.image.process.filter;

import java.util.Iterator;

import net.sci.array.Array;

/**
 * A rectangular neighborhood defined by the diameter in each dimension.
 * 
 * <pre>
 * {@code
 *  // size and dimensionality of input array
 *  int[] sizes = source.getSize();
 *  int nd = sizes.length;
 *  
 *  // creates 3-by-3 neighborhood around a specific position 
 *  int[] pos = new int[]{10, 10};
 *  Neighborhood nbg = new BoxNeighborhood(pos, new int[]{3, 3});
 *  
 *  // prepare iteration
 *  double localSum = 0;
 *  int count = 0;
 *  
 *  // iterate over neighbors
 *  for (int[] neighPos : nbg)
 *  {
 *    // clamp neighbor position to array bounds
 *    for (int d = 0; d < nd; d++)
 *    {
 *      neighPos[d] = Math.min(Math.max(neighPos[d], 0), sizes[d]-1);
 *    }
 *    
 *    // update local sum
 *    localSum += source.getValue(neighPos); 
 *  }
 * }
 * </pre>
 * 
 * @see BoxFilter
 * @see BoxMedianFilter
 * @see BoxVarianceFilter
 * 
 * @author dlegland
 *
 */
public class BoxNeighborhood implements Neighborhood
{
	/** The radius in the negative direction */
	int[] offsets1;

	/** The radius in the positive direction */
	int[] offsets2;
	
	/**
     * The list of shifts around the reference position. Each element of the
     * array has <code>nd</code> elements.
     */
	int[][] shifts;
	
    /**
     * Creates a new rectangular neighborhood around a specific position.
     * 
     * @param refPos
     *            the center position of the neighborhood
     * @param diameters
     *            the side length of the neighborhood along each dimension
     */
	public BoxNeighborhood(int[] diameters)
	{
	    initShifts(diameters);
	}
	
	private void initShifts(int[] diameters)
	{
	    // first compute offsets
        int nd = diameters.length;
        this.offsets1 = new int[nd];
        this.offsets2 = new int[nd];
        
        for (int d = 0; d < nd; d++)
        {
            double radius = ((double) diameters[d]) / 2;
            this.offsets1[d] = (int) Math.floor(radius);
            this.offsets2[d] = (int) Math.ceil(radius) - 1;
        }

        // allocate array of shifts
        int nShifts = (int) Array.prod(diameters);
        this.shifts = new int[nShifts][nd];
        
        // initialize first shift
        int[] shift = new int[nd];
        for (int d = 0; d < nd; d++)
        {
            shift[d] = -this.offsets1[d];
        }
        
        // iterate over elements of shift array
        for (int i = 0; i < nShifts; i++)
        {
            for (int d = 0; d < nd; d++)
            {
                this.shifts[i][d] = shift[d];
            }
            
            incrementShift(shift, 0);
        }
	}

    private void incrementShift(int[] shift, int d)
    {
        shift[d]++;
        if (shift[d] > offsets2[d] && d < shift.length - 1)
        {
            shift[d] = -offsets1[d];
            incrementShift(shift, d + 1);
        }
    }

	
	@Override
    public Iterable<int[]> neighbors(int[] pos)
    {
        return new Iterable<int[]>() {

            @Override
            public Iterator<int[]> iterator()
            {
                return new CursorIterator(pos);
            }
        };
    }

    private class CursorIterator implements Iterator<int[]>
	{
	    /** The position of neighborhood center in original array */
	    final int[] refPos;
	    
		int shiftIndex = -1;
		
		/**
		 * The current position.
		 */
		int[] pos;
		
		public CursorIterator(int[] refPos)
		{
            int nd = refPos.length;
            
	        this.refPos = new int[nd];
	        System.arraycopy(refPos, 0, this.refPos, 0, nd);
			
			// initialize position
			this.pos = new int[nd];
		}
		
		@Override
		public boolean hasNext()
		{
		    return shiftIndex < shifts.length - 1;
		}

		@Override
		public int[] next()
		{
		    shiftIndex++;
		    
			for (int d = 0; d < refPos.length; d++)
			{
				pos[d] = refPos[d] + shifts[shiftIndex][d];
			}
			return pos;	
		}
	}
}
