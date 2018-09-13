/**
 * 
 */
package net.sci.image.morphology;

import static java.lang.Math.max;
import static java.lang.Math.min;

import java.util.ArrayList;

import net.sci.array.scalar.IntArray3D;
import net.sci.array.scalar.ScalarArray3D;
import net.sci.image.data.Connectivity3D;

/**
* <p>
* Implements various flood-fill algorithms, for 3D images. Rewritten from
* ij.process.FloodFiller. Also supports floating point images. 
* 
* The FloodFill2D class provides support for 2D flood-fill algorithms.
* </p>
* 
* <p>
* Check also "http://en.wikipedia.org/wiki/Flood_fill".
* </p>
* 
* @see FloodFill2D
* @see MinimaAndMaxima
* 
* @author David Legland
* 
* @deprecated replaced by FloodFill class
*/
@Deprecated
public class FloodFill3D
{
	/**
	 * Private constructor to prevent class instantiation.
	 */
	private FloodFill3D()
	{
	}
	
	/**
	 * Assigns to all the neighbor voxels of (x,y,z) that have the same voxel
	 * value in <code>image</code>, the specified new label value (
	 * <code>value</code>) in <code>labelImage</code>, using the specified
	 * connectivity.
	 * 
	 * @param inputArray
	 *            original image to read the voxel values from
	 * @param x
	 *            x- coordinate of the seed voxel
	 * @param y
	 *            y- coordinate of the seed voxel
	 * @param z
	 *            z- coordinate of the seed voxel
	 * @param outputArray
	 *            output label image (to fill)
	 * @param value
	 *            filling value
	 * @param conn
	 *            connectivity to use (6 or 26)
	 */
	public final static void floodFillFloat(ScalarArray3D<?> inputArray, int x,
			int y, int z, ScalarArray3D<?> outputArray, double value, int conn)
	{
		switch (conn)
		{
		case 6:
			floodFillFloatC6(inputArray, x, y, z, outputArray, value);
			return;
		case 26:
			floodFillFloatC26(inputArray, x, y, z, outputArray, value);
			return;
		default:
			throw new IllegalArgumentException(
					"Connectivity must be either 6 or 26, not " + conn);
		}
	}
	
	/**
	 * Assigns to all the neighbor voxels of (x,y,z) that have the same voxel
	 * value in <code>image</code>, the specified new label value (
	 * <code>value</code>) in <code>labelImage</code>, using the specified
	 * connectivity.
	 * 
	 * @param inputArray
	 *            original image to read the voxel values from
	 * @param x
	 *            x- coordinate of the seed voxel
	 * @param y
	 *            y- coordinate of the seed voxel
	 * @param z
	 *            z- coordinate of the seed voxel
	 * @param outputArray
	 *            output label image (to fill)
	 * @param value
	 *            filling value
	 * @param conn
	 *            connectivity to use (6 or 26)
	 */
	public final static void floodFillFloat(ScalarArray3D<?> inputArray, int x,
			int y, int z, ScalarArray3D<?> outputArray, double value, Connectivity3D conn)
	{
		if (conn == Connectivity3D.C6)
		{
			floodFillFloatC6(inputArray, x, y, z, outputArray, value);
		}
		else if (conn == Connectivity3D.C26)
		{
			floodFillFloatC26(inputArray, x, y, z, outputArray, value);
		}
		else
		{
			throw new IllegalArgumentException(
					"Unsupported connectivity option");
		}
	}
	
	/**
	 * Assigns to all the neighbor voxels of (x,y,z) that have the same voxel
	 * value in <code>image</code>, the specified new label value (
	 * <code>value</code>) in <code>labelImage</code>, using the 6-connectivity.
	 * 
	 * @param inputImage
	 *            original image to read the voxel values from
	 * @param x
	 *            x- coordinate of the seed voxel
	 * @param y
	 *            y- coordinate of the seed voxel
	 * @param z
	 *            z- coordinate of the seed voxel
	 * @param outputImage
	 *            output label image (to fill)
	 * @param value
	 *            filling value
	 */
	private final static void floodFillFloatC6(ScalarArray3D<?> inputArray, int x,
			int y, int z, ScalarArray3D<?> outputArray, double value)
	{
		// get image size
		int sizeX = inputArray.getSize(0);
		int sizeY = inputArray.getSize(1);
		int sizeZ = inputArray.getSize(2);

		// get old value
		double oldValue = inputArray.getValue(x, y, z);

		// initialize the stack with original pixel
		ArrayList<Cursor3D> stack = new ArrayList<Cursor3D>();
		stack.add(new Cursor3D(x, y, z));

		boolean inScanLine;

		// process all items in stack
		while (!stack.isEmpty()) 
		{
			// Extract current position
			Cursor3D p = stack.remove(stack.size()-1);
			x = p.x;
			y = p.y;
			z = p.z;

			// process only pixel of the same value
			if (inputArray.getValue(x, y, z) != oldValue)
				continue;

			// x extremities of scan-line
			int x1 = x; 
			int x2 = x;

			// find start of scan-line
			while (x1 > 0 && inputArray.getValue(x1 - 1, y, z) == oldValue)
				x1--;

			// find end of scan-line
			while (x2 < sizeX - 1 && inputArray.getValue(x2 + 1, y, z) == oldValue)
				x2++;

			// fill current scan-line
			fillLineFloat(outputArray, x1, x2, y, z, value);

			// search bounds on x axis for neighbor lines
			int x1l = max(x1, 0);
			int x2l = min(x2, sizeX - 1);

			// find scan-lines above the current one
			if (y > 0) 
			{
				inScanLine = false;
				for (int i = x1l; i <= x2l; i++)
				{
					double val = inputArray.getValue(i, y - 1, z);
					double lab = outputArray.getValue(i, y - 1, z);

					if (!inScanLine && val == oldValue && lab != value) 
					{
						stack.add(new Cursor3D(i, y - 1, z));
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
				for (int i = x1l; i <= x2l; i++) 
				{
					double val = inputArray.getValue(i, y + 1, z);
					double lab = outputArray.getValue(i, y + 1, z);

					if (!inScanLine && val == oldValue && lab != value) 
					{
						stack.add(new Cursor3D(i, y + 1, z));
						inScanLine = true;
					}
					else if (inScanLine && val != oldValue)
					{
						inScanLine = false;
					}
				}
			}

			// find scan-lines in front of the current one
			if (z > 0)
			{
				inScanLine = false;
				for (int i = x1l; i <= x2l; i++) 
				{
					double val = inputArray.getValue(i, y, z - 1);
					double lab = outputArray.getValue(i, y, z - 1);

					if (!inScanLine && val == oldValue && lab != value) 
					{
						stack.add(new Cursor3D(i, y, z - 1));
						inScanLine = true;
					}
					else if (inScanLine && val != oldValue)
					{
						inScanLine = false;
					}
				}
			}

			// find scan-lines behind the current one
			if (z < sizeZ - 1)
			{
				inScanLine = false;
				for (int i = x1l; i <= x2l; i++)
				{
					double val = inputArray.getValue(i, y, z + 1);
					double lab = outputArray.getValue(i, y, z + 1);

					if (!inScanLine && val == oldValue && lab != value)
					{
						stack.add(new Cursor3D(i, y, z + 1));
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
	 * Assigns to all the neighbor voxels of (x,y,z) that have the same voxel
	 * value in <code>image</code>, the specified new label value (
	 * <code>value</code>) in <code>labelImage</code>, using the
	 * 26-connectivity.
	 * 
	 * @param inputImage
	 *            original image to read the voxel values from
	 * @param x
	 *            x- coordinate of the seed voxel
	 * @param y
	 *            y- coordinate of the seed voxel
	 * @param z
	 *            z- coordinate of the seed voxel
	 * @param outputImage
	 *            output label image (to fill)
	 * @param value
	 *            filling value
	 */
	private final static void floodFillFloatC26(ScalarArray3D<?> inputArray, int x,
			int y, int z, ScalarArray3D<?> outputArray, double value)
	{
		// get image size
		int sizeX = inputArray.getSize(0);
		int sizeY = inputArray.getSize(1);
		int sizeZ = inputArray.getSize(2);
		
		// get old value
		double oldValue = inputArray.getValue(x, y, z);
				
		// initialize the stack with original pixel
		ArrayList<Cursor3D> stack = new ArrayList<Cursor3D>();
		stack.add(new Cursor3D(x, y, z));
		
		boolean inScanLine;
		
		// process all items in stack
		while (!stack.isEmpty())
		{
			// Extract current position
			Cursor3D p = stack.remove(stack.size()-1);
			x = p.x;
			y = p.y;
			z = p.z;
			
			// process only pixel of the same value
			if (inputArray.getValue(x, y, z) != oldValue)
				continue;
			
			// x extremities of scan-line
			int x1 = x; 
			int x2 = x;
			
			// find start of scan-line
			while (x1 > 0 && inputArray.getValue(x1-1, y, z) == oldValue)
				x1--;
			
			// find end of scan-line
			while (x2 < sizeX - 1 && inputArray.getValue(x2+1, y, z) == oldValue)
				x2++;
		
			// fill current scan-line
			fillLineFloat(outputArray, x1, x2, y, z, value);
			
			// search bounds on x axis for neighbor lines
			int x1l = max(x1 - 1, 0);
			int x2l = min(x2 + 1, sizeX - 1);

			// check the eight X-lines around the current one
			for (int z2 = max(z - 1, 0); z2 <= min(z + 1, sizeZ - 1); z2++) 
			{
				for (int y2 = max(y - 1, 0); y2 <= min(y + 1, sizeY - 1); y2++) 
				{
					// do not process the middle line
					if (z2 == z && y2 == y)
						continue;
					
					inScanLine = false;
					for (int i = x1l; i <= x2l; i++) 
					{
						double val = inputArray.getValue(i, y2, z2);
						double lab = outputArray.getValue(i, y2, z2);
						
						if (!inScanLine && val == oldValue && lab != value) 
						{
							stack.add(new Cursor3D(i, y2, z2));
							inScanLine = true;
						}
						else if (inScanLine && val != oldValue)
						{
							inScanLine = false;
						}
					}
					
				}
			} // end of iteration on neighbor lines
		}
	}

	/**
	 * Fill in the horizontal line define by y-coordinate and the two x
	 * coordinate extremities (inclusive), with the specified integer value. the
	 * value x1 must be lower than or equal the value x2.
	 */
	private final static void fillLineFloat(ScalarArray3D<?> array, int x1, int x2,
			int y, int z, double value)
	{
		for (int x = x1; x <= x2; x++)
			array.setValue(x, y, z, value);
	}

	/**
	 * Assigns to all the neighbor voxels of (x,y,z) that have the same voxel
	 * value in <code>image</code>, the specified new label value (
	 * <code>value</code>) in <code>labelImage</code>, using the specified
	 * connectivity.
	 * 
	 * @param inputArray
	 *            original image to read the voxel values from
	 * @param x
	 *            x- coordinate of the seed voxel
	 * @param y
	 *            y- coordinate of the seed voxel
	 * @param z
	 *            z- coordinate of the seed voxel
	 * @param outputArray
	 *            output label image (to fill)
	 * @param value
	 *            filling value
	 * @param conn
	 *            connectivity to use (6 or 26)
	 */
	public final static void floodFillInt(IntArray3D<?> inputArray, int x,
			int y, int z, IntArray3D<?> outputArray, int value, int conn)
	{
		switch (conn)
		{
		case 6:
			floodFillIntC6(inputArray, x, y, z, outputArray, value);
			return;
		case 26:
			floodFillIntC26(inputArray, x, y, z, outputArray, value);
			return;
		default:
			throw new IllegalArgumentException(
					"Connectivity must be either 6 or 26, not " + conn);
		}
	}
	

	/**
	 * Assigns to all the neighbor voxels of (x,y,z) that have the same voxel
	 * value in <code>image</code>, the specified new label value (
	 * <code>value</code>) in <code>labelImage</code>, using the specified
	 * connectivity.
	 * 
	 * @param inputArray
	 *            original image to read the voxel values from
	 * @param x
	 *            x- coordinate of the seed voxel
	 * @param y
	 *            y- coordinate of the seed voxel
	 * @param z
	 *            z- coordinate of the seed voxel
	 * @param outputArray
	 *            output label image (to fill)
	 * @param value
	 *            filling value
	 * @param conn
	 *            connectivity to use (6 or 26)
	 */
	public final static void floodFillInt(IntArray3D<?> inputArray, int x,
			int y, int z, IntArray3D<?> outputArray, int value, Connectivity3D conn)
	{
		if (conn == Connectivity3D.C6)
		{
			floodFillIntC6(inputArray, x, y, z, outputArray, value);
		}
		else if (conn == Connectivity3D.C26)
		{
			floodFillIntC26(inputArray, x, y, z, outputArray, value);
		}
		else
		{
			throw new IllegalArgumentException(
					"Unsupported connectivity option");
		}
	}

	/**
	 * Assigns to all the neighbor voxels of (x,y,z) that have the same voxel
	 * value in <code>image</code>, the specified new label value (
	 * <code>value</code>) in <code>labelImage</code>, using the 6-connectivity.
	 * 
	 * @param inputImage
	 *            original image to read the voxel values from
	 * @param x
	 *            x- coordinate of the seed voxel
	 * @param y
	 *            y- coordinate of the seed voxel
	 * @param z
	 *            z- coordinate of the seed voxel
	 * @param outputImage
	 *            output label image (to fill)
	 * @param value
	 *            filling value
	 */
	private final static void floodFillIntC6(IntArray3D<?> inputArray, int x,
			int y, int z, IntArray3D<?> outputArray, int value)
	{
		// get image size
		int sizeX = inputArray.getSize(0);
		int sizeY = inputArray.getSize(1);
		int sizeZ = inputArray.getSize(2);

		// get old value
		int oldValue = inputArray.getInt(x, y, z);

		// initialize the stack with original position
		ArrayList<Cursor3D> stack = new ArrayList<Cursor3D>();
		stack.add(new Cursor3D(x, y, z));

		boolean inScanLine;

		// process all items in stack
		while (!stack.isEmpty()) 
		{
			// Extract current position
			Cursor3D p = stack.remove(stack.size()-1);
			x = p.x;
			y = p.y;
			z = p.z;

			// process only pixel of the same value
			if (inputArray.getInt(x, y, z) != oldValue)
				continue;

			// x extremities of scan-line
			int x1 = x; 
			int x2 = x;

			// find start of scan-line
			while (x1 > 0 && inputArray.getInt(x1 - 1, y, z) == oldValue)
				x1--;

			// find end of scan-line
			while (x2 < sizeX - 1 && inputArray.getInt(x2 + 1, y, z) == oldValue)
				x2++;

			// fill current scan-line
			fillLineInt(outputArray, x1, x2, y, z, value);

			// search bounds on x axis for neighbor lines
			int x1l = max(x1, 0);
			int x2l = min(x2, sizeX - 1);

			// find scan-lines above the current one
			if (y > 0) 
			{
				inScanLine = false;
				for (int i = x1l; i <= x2l; i++)
				{
					int val = inputArray.getInt(i, y - 1, z);
					int lab = outputArray.getInt(i, y - 1, z);

					if (!inScanLine && val == oldValue && lab != value) 
					{
						stack.add(new Cursor3D(i, y - 1, z));
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
				for (int i = x1l; i <= x2l; i++) 
				{
					int val = inputArray.getInt(i, y + 1, z);
					int lab = outputArray.getInt(i, y + 1, z);

					if (!inScanLine && val == oldValue && lab != value) 
					{
						stack.add(new Cursor3D(i, y + 1, z));
						inScanLine = true;
					}
					else if (inScanLine && val != oldValue)
					{
						inScanLine = false;
					}
				}
			}

			// find scan-lines in front of the current one
			if (z > 0)
			{
				inScanLine = false;
				for (int i = x1l; i <= x2l; i++) 
				{
					int val = inputArray.getInt(i, y, z - 1);
					int lab = outputArray.getInt(i, y, z - 1);

					if (!inScanLine && val == oldValue && lab != value) 
					{
						stack.add(new Cursor3D(i, y, z - 1));
						inScanLine = true;
					}
					else if (inScanLine && val != oldValue)
					{
						inScanLine = false;
					}
				}
			}

			// find scan-lines behind the current one
			if (z < sizeZ - 1)
			{
				inScanLine = false;
				for (int i = x1l; i <= x2l; i++)
				{
					int val = inputArray.getInt(i, y, z + 1);
					int lab = outputArray.getInt(i, y, z + 1);

					if (!inScanLine && val == oldValue && lab != value)
					{
						stack.add(new Cursor3D(i, y, z + 1));
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
	 * Assigns to all the neighbor voxels of (x,y,z) that have the same voxel
	 * value in <code>image</code>, the specified new label value (
	 * <code>value</code>) in <code>labelImage</code>, using the
	 * 26-connectivity.
	 * 
	 * @param inputImage
	 *            original image to read the voxel values from
	 * @param x
	 *            x- coordinate of the seed voxel
	 * @param y
	 *            y- coordinate of the seed voxel
	 * @param z
	 *            z- coordinate of the seed voxel
	 * @param outputImage
	 *            output label image (to fill)
	 * @param value
	 *            filling value
	 */
	private final static void floodFillIntC26(IntArray3D<?> inputArray, int x,
			int y, int z, IntArray3D<?> outputArray, int value)
	{
		// get image size
		int sizeX = inputArray.getSize(0);
		int sizeY = inputArray.getSize(1);
		int sizeZ = inputArray.getSize(2);
		
		// get old value
		int oldValue = inputArray.getInt(x, y, z);
				
		// initialize the stack with original pixel
		ArrayList<Cursor3D> stack = new ArrayList<Cursor3D>();
		stack.add(new Cursor3D(x, y, z));
		
		boolean inScanLine;
		
		// process all items in stack
		while (!stack.isEmpty())
		{
			// Extract current position
			Cursor3D p = stack.remove(stack.size()-1);
			x = p.x;
			y = p.y;
			z = p.z;
			
			// process only pixel of the same value
			if (inputArray.getValue(x, y, z) != oldValue)
				continue;
			
			// x extremities of scan-line
			int x1 = x; 
			int x2 = x;
			
			// find start of scan-line
			while (x1 > 0 && inputArray.getValue(x1-1, y, z) == oldValue)
				x1--;
			
			// find end of scan-line
			while (x2 < sizeX - 1 && inputArray.getValue(x2+1, y, z) == oldValue)
				x2++;
		
			// fill current scan-line
			fillLineFloat(outputArray, x1, x2, y, z, value);
			
			// search bounds on x axis for neighbor lines
			int x1l = max(x1 - 1, 0);
			int x2l = min(x2 + 1, sizeX - 1);

			// check the eight X-lines around the current one
			for (int z2 = max(z - 1, 0); z2 <= min(z + 1, sizeZ - 1); z2++) 
			{
				for (int y2 = max(y - 1, 0); y2 <= min(y + 1, sizeY - 1); y2++) 
				{
					// do not process the middle line
					if (z2 == z && y2 == y)
						continue;
					
					inScanLine = false;
					for (int i = x1l; i <= x2l; i++) 
					{
						int val = inputArray.getInt(i, y2, z2);
						int lab = outputArray.getInt(i, y2, z2);
						
						if (!inScanLine && val == oldValue && lab != value) 
						{
							stack.add(new Cursor3D(i, y2, z2));
							inScanLine = true;
						}
						else if (inScanLine && val != oldValue)
						{
							inScanLine = false;
						}
					}
					
				}
			} // end of iteration on neighbor lines
		}
	}

	/**
	 * Fill in the horizontal line define by y-coordinate and the two x
	 * coordinate extremities (inclusive), with the specified integer value. the
	 * value x1 must be lower than or equal the value x2.
	 */
	private final static void fillLineInt(IntArray3D<?> array, int x1, int x2, int y,
			int z, int value)
	{
		for (int x = x1; x <= x2; x++)
			array.setInt(x, y, z, value);
	}

	/**
	 * Defines a position within a 3D stack.
	 * Needs to be a static class to be called by static methods.
	 */
	private static class Cursor3D 
	{
		int x;
		int y;
		int z;
		
		public Cursor3D(int x, int y, int z) 
		{
			this.x = x;
			this.y = y;
			this.z = z;
		}
	}
}
