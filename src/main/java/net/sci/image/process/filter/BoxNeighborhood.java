/**
 * 
 */
package net.sci.image.process.filter;

import java.util.Iterator;

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
     * Creates a new rectangular neighborhood around a specific position.
     * 
     * @param refPos
     *            the center position of the neighborhood
     * @param diameters
     *            the side length of the neighborhood along each dimension
     */
	public BoxNeighborhood(int[] diameters)
	{
		computeOffsets(diameters);
	}
	
	private void computeOffsets(int[] diameters)
	{
		int nd = diameters.length;
		this.offsets1 = new int[nd];
		this.offsets2 = new int[nd];
		
		for (int d = 0; d < nd; d++)
		{
			double radius = ((double) diameters[d]) / 2;
			this.offsets1[d] = (int) Math.floor(radius);
			this.offsets2[d] = (int) Math.ceil(radius) - 1;
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
	    int[] refPos;
	    
		/** 
		 * Each index in pos iterates between -offset1[d] and +offset1[d], including both.
		 */
		int[] shift;
		
		/**
		 * number of dimension of this iterator
		 */
		int nd;

		public CursorIterator(int[] refPos)
		{
	        this.refPos = new int[refPos.length];
	        System.arraycopy(refPos, 0, this.refPos, 0, refPos.length);
	        
			// initialize current position before the first position in neighborhood
			this.nd = offsets1.length;
			this.shift = new int[nd];
			this.shift[0] = -offsets1[0] - 1;
			for (int d = 1; d < nd; d++)
			{
				this.shift[d] = -offsets1[d];
			}
		}
		
		@Override
		public boolean hasNext()
		{
			for (int d = 0; d < nd; d++)
			{
				if (this.shift[d] < offsets2[d])
					return true;
			}
			return false;
		}

		@Override
		public int[] next()
		{
			incrementDim(0);
			int[] coords = new int[nd];
			for (int d = 0; d < nd; d++)
			{
				coords[d] = refPos[d] + shift[d];
			}
			return coords;	
		}

		private void incrementDim(int d)
		{
			this.shift[d]++;
			if (this.shift[d] > offsets2[d] && d < nd - 1)
			{
				this.shift[d] = -offsets1[d];
				incrementDim(d + 1);
			}
		}
	}
}
