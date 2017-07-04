/**
 * 
 */
package net.sci.image.morphology;

import static java.lang.Math.max;
import static java.lang.Math.min;

import java.util.ArrayList;

import net.sci.array.data.Array2D;
import net.sci.array.data.scalar2d.IntArray2D;
import net.sci.array.data.scalar2d.ScalarArray2D;
import net.sci.image.data.Connectivity2D;

/**
* <p>
* Implements various flood-fill algorithms, for planar images. Rewritten from
* ij.process.FloodFiller. Also supports floating point images. 
* 
* The FloodFill3D class provides support for 3D flood-fill algorithms.
* </p>
* 
* <p>
* Check also "http://en.wikipedia.org/wiki/Flood_fill".
* </p>
* 
* @see FloodFill3D
* @see MinimaAndMaxima
* 
* @author David Legland
*/
public class FloodFill2D
{
	/**
	 * Private constructor to prevent class instantiation.
	 */
	private FloodFill2D()
	{
	}

	/**
	 * Assigns in <code>labelImage</code> all the neighbor pixels of (x,y) that
	 * have the same pixel value in <code>image</code>, the specified new label
	 * value (<code>value</code>), using the specified connectivity.
	 * 
	 * This method uses generics, and should be applicable to any type. It is
	 * based on the equals methods, and may be slower than similar operation on
	 * float or int arrays
	 * 
	 * @param <S>
	 *            the type of the source array
	 * @param <T>
	 *            the type of the target array
	 * @param source
	 *            original image to read the pixel values from
	 * @param x
	 *            x- coordinate of the seed pixel
	 * @param y
	 *            y- coordinate of the seed pixel
	 * @param target
	 *            the label image to fill in
	 * @param value
	 *            filling value
	 * @param conn
	 *            connectivity to use (4 or 8)
	 */
	public final static <S, T> void floodFill(Array2D<S> source, int x, int y,
			Array2D<T> target, T value, Connectivity2D conn)
	{
		// initialize the shifts to look for new markers to start lines
		// default values for C4
		int dx1 = 0;
		int dx2 = 0;
		if (conn == Connectivity2D.C8) 
		{
			dx1 = -1;
			dx2 = +1;
		}
		
		// get image size
		int sizeX = source.getSize(0);
		int sizeY = source.getSize(1);
		
		// get old value
		S oldValue = source.get(x, y);
		
		// initialize the stack with original pixel
		ArrayList<Cursor2D> stack = new ArrayList<Cursor2D>();
		stack.add(new Cursor2D(x, y));
				
		boolean inScanLine;
		
		// process all items in stack
		while (!stack.isEmpty()) 
		{
			// Extract current position
			Cursor2D p = stack.remove(stack.size()-1);
			x = p.x;
			y = p.y;
			
			// process only pixel of the same value
			if (!source.get(x, y).equals(oldValue))
				continue;
			
			// x extremities of scan-line
			int x1 = x; 
			int x2 = x;
			
			// find start of scan-line
			while (x1 > 0 && source.get(x1-1, y).equals(oldValue))
				x1--;
			
			// find end of scan-line
			while (x2 < sizeX - 1 && source.get(x2+1, y).equals(oldValue))
				x2++;
			
			// fill current scan-line
			fillLine(target, y, x1, x2, value);
			
			// find scan-lines above the current one
			if (y > 0)
			{
				inScanLine = false;
				for (int i = max(x1 + dx1, 0); i <= min(x2 + dx2, sizeX - 1); i++)
				{
					S val = source.get(i, y - 1);
					T lab = target.get(i, y - 1);
					if (!inScanLine && val.equals(oldValue) && !lab.equals(value))
					{
						stack.add(new Cursor2D(i, y - 1));
						inScanLine = true;
					} 
					else if (inScanLine && !val.equals(oldValue))
					{
						inScanLine = false;
					}
				}
			}

			// find scan-lines below the current one
			if (y < sizeY - 1)
			{
				inScanLine = false;
				for (int i = max(x1 + dx1, 0); i <= min(x2 + dx2, sizeX - 1); i++)
				{
					S val = source.get(i, y + 1);
					T lab = target.get(i, y + 1);
					if (!inScanLine && val.equals(oldValue) && !lab.equals(value))
					{
						stack.add(new Cursor2D(i, y + 1));
						inScanLine = true;
					} 
					else if (inScanLine && !val.equals(oldValue))
					{
						inScanLine = false;
					}
				}
			}
		}
	}

	/**
	 * Fill in the horizontal line define by y-coordinate and the two x 
	 * coordinate extremities (inclusive), with the specified integer value.
	 * the value x1 must be lower than or equal the value x2. 
	 */
	private final static <T> void fillLine(Array2D<T> array, int y, int x1,
			int x2, T value)
	{
		if (x1 > x2)
		{
			int t = x1;
			x1 = x2;
			x2 = t;
		}
	
		for (int x = x1; x <= x2; x++)
			array.set(x, y, value);
	}



	/**
	 * Assigns in <code>labelImage</code> all the neighbor pixels of (x,y) that
	 * have the same pixel value in <code>image</code>, the specified new label
	 * value (<code>value</code>), using the specified connectivity.
	 * 
	 * @param input
	 *            original image to read the pixel values from
	 * @param x
	 *            x- coordinate of the seed pixel
	 * @param y
	 *            y- coordinate of the seed pixel
	 * @param output
	 *            the label image to fill in
	 * @param value
	 *            filling value
	 * @param conn
	 *            connectivity to use (4 or 8)
	 */
	public final static void floodFillFloat(ScalarArray2D<?> input, int x, int y,
			ScalarArray2D<?> output, double value, Connectivity2D conn)
	{
		if (conn == Connectivity2D.C4)
		{
			floodFillFloat(input, x, y, output, value, 4);
		}
		else if (conn == Connectivity2D.C8)
		{
			floodFillFloat(input, x, y, output, value, 8);
		}
		else
		{
			throw new IllegalArgumentException("Unkown connectivity option");
		}
	}

	/**
	 * Assigns in <code>labelImage</code> all the neighbor pixels of (x,y) that
	 * have the same pixel value in <code>image</code>, the specified new label
	 * value (<code>value</code>), using the specified connectivity.
	 * 
	 * @param input
	 *            original image to read the pixel values from
	 * @param x
	 *            x- coordinate of the seed pixel
	 * @param y
	 *            y- coordinate of the seed pixel
	 * @param output
	 *            the label image to fill in
	 * @param value
	 *            filling value
	 * @param conn
	 *            connectivity to use (4 or 8)
	 */
	public final static void floodFillFloat(ScalarArray2D<?> input, int x, int y,
			ScalarArray2D<?> output, double value, int conn)
	{
		// the shifts to look for new markers to start lines
		int dx1 = 0;
		int dx2 = 0;
		if (conn == 8) 
		{
			dx1 = -1;
			dx2 = +1;
		}
		
		// get image size
		int sizeX = input.getSize(0);
		int sizeY = input.getSize(1);
		
		// get old value
		double oldValue = input.getValue(x, y);
		
		// initialize the stack with original pixel
		ArrayList<Cursor2D> stack = new ArrayList<Cursor2D>();
		stack.add(new Cursor2D(x, y));
		
		
		boolean inScanLine;
		
		// process all items in stack
		while (!stack.isEmpty()) 
		{
			// Extract current position
			Cursor2D p = stack.remove(stack.size()-1);
			x = p.x;
			y = p.y;
			
			// process only pixel of the same value
			if (input.getValue(x, y) != oldValue) 
				continue;
			
			// x extremities of scan-line
			int x1 = x; 
			int x2 = x;
			
			// find start of scan-line
			while (x1 > 0 && input.getValue(x1-1, y) == oldValue)
				x1--;
			
			// find end of scan-line
			while (x2 < sizeX - 1 && input.getValue(x2+1, y) == oldValue)
				x2++;
			
			// fill current scan-line
			fillLineFloat(output, y, x1, x2, value);
			
			// find scan-lines above the current one
			if (y > 0)
			{
				inScanLine = false;
				for (int i = max(x1 + dx1, 0); i <= min(x2 + dx2, sizeX - 1); i++)
				{
					double val = input.getValue(i, y - 1);
					double lab = output.getValue(i, y - 1);
					if (!inScanLine && val == oldValue && lab != value)
					{
						stack.add(new Cursor2D(i, y - 1));
						inScanLine = true;
					} 
					else if (inScanLine && val != oldValue)
					{
						inScanLine = false;
					}
				}
			}

			// find scan-lines below the current one
			if (y < sizeY - 1)
			{
				inScanLine = false;
				for (int i = max(x1 + dx1, 0); i <= min(x2 + dx2, sizeX - 1); i++)
				{
					double val = input.getValue(i, y + 1);
					double lab = output.getValue(i, y + 1);
					if (!inScanLine && val == oldValue && lab != value)
					{
						stack.add(new Cursor2D(i, y + 1));
						inScanLine = true;
					} 
					else if (inScanLine && val != oldValue)
					{
						inScanLine = false;
					}
				}
			}
		}
	}

	/**
	 * Fill in the horizontal line define by y-coordinate and the two x 
	 * coordinate extremities (inclusive), with the specified integer value.
	 * the value x1 must be lower than or equal the value x2. 
	 */
	private final static void fillLineFloat(Array2D<?> array, int y, int x1,
			int x2, double value)
	{
		if (x1 > x2)
		{
			int t = x1;
			x1 = x2;
			x2 = t;
		}
	
		for (int x = x1; x <= x2; x++)
			array.setValue(x, y, value);
	}

	/**
	 * Assigns in <code>labelImage</code> all the neighbor pixels of (x,y) that
	 * have the same pixel value in <code>image</code>, the specified new label
	 * value (<code>value</code>), using the specified connectivity.
	 * 
	 * @param input
	 *            original image to read the pixel values from
	 * @param x
	 *            x- coordinate of the seed pixel
	 * @param y
	 *            y- coordinate of the seed pixel
	 * @param output
	 *            the label image to fill in
	 * @param value
	 *            filling value
	 * @param conn
	 *            connectivity to use (4 or 8)
	 */
	public final static void floodFillInt(IntArray2D<?> input, int x, int y,
			IntArray2D<?> output, int value, Connectivity2D conn)
	{
		if (conn == Connectivity2D.C4)
		{
			floodFillInt(input, x, y, output, value, 4);
		}
		else if (conn == Connectivity2D.C8)
		{
			floodFillInt(input, x, y, output, value, 8);
		}
		else
		{
			throw new IllegalArgumentException("Unkown connectivity option");
		}
		
	}

	/**
	 * Assigns in <code>labelImage</code> all the neighbor pixels of (x,y) that
	 * have the same pixel value in <code>image</code>, the specified new label
	 * value (<code>value</code>), using the specified connectivity.
	 * 
	 * @param input
	 *            original image to read the pixel values from
	 * @param x
	 *            x- coordinate of the seed pixel
	 * @param y
	 *            y- coordinate of the seed pixel
	 * @param output
	 *            the label image to fill in
	 * @param value
	 *            filling value
	 * @param conn
	 *            connectivity to use (4 or 8)
	 */
	public final static void floodFillInt(IntArray2D<?> input, int x, int y,
			IntArray2D<?> output, int value, int conn)
	{
	
		// the shifts to look for new markers to start lines
		int dx1 = 0;
		int dx2 = 0;
		if (conn == 8)
		{
			dx1 = -1;
			dx2 = +1;
		}
		
		// get image size
		int sizeX = input.getSize(0);
		int sizeY = input.getSize(1);
		
		// get old value
		int oldValue = input.getInt(x, y);
				
		// initialize the stack with original pixel
		ArrayList<Cursor2D> stack = new ArrayList<Cursor2D>();
		stack.add(new Cursor2D(x, y));
		
		boolean inScanLine;
		
		// process all items in stack
		while (!stack.isEmpty()) 
		{
			// Extract current position
			Cursor2D p = stack.remove(stack.size()-1);
			x = p.x;
			y = p.y;
			
			// process only pixel of the same value
			if (input.getInt(x, y) != oldValue) 
				continue;
			
			// x extremities of scan-line
			int x1 = x; 
			int x2 = x;
			
			// find start of scan-line
			while (x1 > 0 && input.getInt(x1-1, y) == oldValue)
				x1--;
			
			// find end of scan-line
			while (x2 < sizeX - 1 && input.getInt(x2+1, y) == oldValue)
				x2++;
			
			// fill current scan-line
			fillLineInt(output, y, x1, x2, value);
			
			
			// find scan-lines above the current one
			if (y > 0)
			{
				inScanLine = false;
				for (int i = max(x1 + dx1, 0); i <= min(x2 + dx2, sizeX - 1); i++)
				{
					int val = input.getInt(i, y - 1);
					int lab = output.getInt(i, y - 1);
					if (!inScanLine && val == oldValue && lab != value)
					{
						stack.add(new Cursor2D(i, y - 1));
						inScanLine = true;
					} 
					else if (inScanLine && val != oldValue)
					{
						inScanLine = false;
					}
				}
			}
	
			// find scan-lines below the current one
			if (y < sizeY - 1)
			{
				inScanLine = false;
				for (int i = max(x1 + dx1, 0); i <= min(x2 + dx2, sizeX - 1); i++)
				{
					int val = input.getInt(i, y + 1);
					int lab = output.getInt(i, y + 1);
					if (!inScanLine && val == oldValue && lab != value)
					{
						stack.add(new Cursor2D(i, y + 1));
						inScanLine = true;
					} 
					else if (inScanLine && val != oldValue)
					{
						inScanLine = false;
					}
				}
			}
		}
	}

	/**
	 * In the input array, replaces all pixels in row <code>y</code> located
	 * between <code>x1</code> and <code>x2</code> (inclusive) by the given
	 * value.
	 * 
	 * @param ip
	 *            the input image to modify
	 * @param y
	 *            the index of the row to modify
	 * @param x1
	 *            the column index of the first pixel to modify
	 * @param x2
	 *            the column index of the first pixel to modify
	 * @param value
	 *            the new value of the pixels
	 */
	private final static void fillLineInt(IntArray2D<?> array, int y, int x1,
			int x2, int value)
	{
		if (x1 > x2)
		{
			int t = x1;
			x1 = x2;
			x2 = t;
		}
		
		for (int x = x1; x <= x2; x++)
			array.setInt(x, y, value);
	}

	/**
	 * Defines a position within a 2D array.
	 * Needs to be a static class to be called by static methods.
	 */
	private static class Cursor2D 
	{
		int x;
		int y;
		
		public Cursor2D(int x, int y) 
		{
			this.x = x;
			this.y = y;
		}
	}
	
}
