/**
 * 
 */
package net.sci.array;

/**
 * Interface for identifying a position within a N-dimensional array.
 * 
 * @author dlegland
 *
 */
//public class Cursor implements Positionable.Cursor
public interface Cursor
{
	public int[] getPosition();
	public int getPosition(int dim);
//	int[] sizes;
//	int[] pos;
//	int nd;

//	protected Cursor(int[] sizes)
//	{
//		this.sizes = sizes;
//		this.nd = sizes.length;
//		this.pos = new int[this.nd];
//		for (int d = 0; d < this.nd - 1; d++)
//		{
//			this.pos[d] = sizes[d] - 1;
//		}
//		this.pos[this.nd - 2] = -1;
//	}
	
//	public int[] getPosition()
//	{
//		int[] res = new int[nd];
//		System.arraycopy(this.pos, 0, res, 0, nd);
//		return res;
//	}
//	
//	public boolean hasNext()
//	{
//		for (int d = 0; d < nd; d++)
//		{
//			if (this.pos[d] < sizes[d] - 1)
//				return true;
//		}
//		return false;
//	}
//	
//	public void forward()
//	{
//		incrementDim(0);
//	}
//	
//	private void incrementDim(int d)
//	{
//		this.pos[d]++;
//		if (this.pos[d] == sizes[d] && d < nd - 1)
//		{
//			this.pos[d] = 0;
//			incrementDim(d + 1);
//		}
//	}
}
