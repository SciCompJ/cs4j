/**
 * 
 */
package net.sci.image.process.filter;

import java.util.Iterator;

import net.sci.array.Cursor;

/**
 * A rectangular neighborhood defined by the diameter in each dimension.
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
	/** The position of neighborhood center in original array */
	int[] refPos;
	
	/** The radius in the negative direction */
	int[] offsets1;

	/** The radius in the positive direction */
	int[] offsets2;
	
	public BoxNeighborhood(Cursor cursor, int[] diameters)
	{
		this(cursor.getPosition(), diameters);
	}
	
	public BoxNeighborhood(int[] refPos, int[] diameters)
	{
		this.refPos = new int[refPos.length];
		System.arraycopy(refPos, 0, this.refPos, 0, refPos.length);
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
			this.offsets2[d] = (int) Math.ceil(radius);
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<int[]> iterator()
	{
		return new CursorIterator();
	}

	private class CursorIterator implements Iterator<int[]>
	{
		/** 
		 * Each index in pos iterates between -offset1[d] and +offset1[d], including both.
		 */
		int[] shift;
		
		/**
		 * number of dimension of this iterator
		 */
		int nd;

		public CursorIterator()
		{
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
