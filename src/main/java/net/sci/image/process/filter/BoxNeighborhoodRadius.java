/**
 * 
 */
package net.sci.image.process.filter;

import java.util.Iterator;

import net.sci.array.Cursor;

/**
 * A rectangular neighborhood defined by the radius in each dimension.
 * 
 * Corresponds to a preliminary version of the {@link BoxNeighborhood} implementation.
 * 
 * @author dlegland
 * 
 * @see BoxNeighborhood
 */
@Deprecated
public class BoxNeighborhoodRadius implements Neighborhood
{
	int[] refPos;
	int[] radiusList;
	
	public BoxNeighborhoodRadius(Cursor cursor, int[] radiusList)
	{
		this.refPos = cursor.getPosition();
		this.radiusList = radiusList;
	}
	
	public BoxNeighborhoodRadius(int[] refPos, int[] radiusList)
	{
		this.refPos = new int[refPos.length];
		System.arraycopy(refPos, 0, this.refPos, 0, refPos.length);
		this.radiusList = new int[radiusList.length];
		System.arraycopy(radiusList, 0, this.radiusList, 0, radiusList.length);
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
		 * each index in pos iterates between -radius[d] and +radius[d], including both.
		 */
		int[] shift;
		
		/**
		 * number of dimension of this iterator
		 */
		int nd;

		public CursorIterator()
		{
			this.nd = radiusList.length;
			this.shift = new int[nd];
			this.shift[0] = -radiusList[0] - 1;
			for (int d = 1; d < nd; d++)
			{
				this.shift[d] = -radiusList[d];
			}
		}
		
		@Override
		public boolean hasNext()
		{
			for (int d = 0; d < nd; d++)
			{
				if (this.shift[d] < radiusList[d])
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
//		public int[] getPosition()
//		{
//			int[] res = new int[nd];
//			System.arraycopy(this.pos, 0, res, 0, nd);
//			return res;
//		}
//		
//		public boolean hasNext()
//		{
//			for (int d = 0; d < nd; d++)
//			{
//				if (this.pos[d] < sizes[d] - 1)
//					return true;
//			}
//			return false;
//		}
//		
//		public void forward()
//		{
//			incrementDim(0);
//		}
//		
		private void incrementDim(int d)
		{
			this.shift[d]++;
			if (this.shift[d] > radiusList[d] && d < nd - 1)
			{
				this.shift[d] = -radiusList[d];
				incrementDim(d + 1);
			}
		}
		
	}
}
