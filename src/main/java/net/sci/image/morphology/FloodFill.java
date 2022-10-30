/**
 * 
 */
package net.sci.image.morphology;

import static java.lang.Math.max;
import static java.lang.Math.min;

import java.util.ArrayList;

import net.sci.array.Array2D;
import net.sci.array.scalar.IntArray2D;
import net.sci.array.scalar.IntArray3D;
import net.sci.array.scalar.ScalarArray2D;
import net.sci.array.scalar.ScalarArray3D;
import net.sci.image.data.Connectivity2D;
import net.sci.image.data.Connectivity3D;

/**
 * <p>
 * Implements various flood-fill algorithms, for 2D and 3D arrays.
 * 
 * Rewritten from class ImageJ ij.process.FloodFiller, and updated for double
 * values and 3D arrays.
 * 
 * </p>
 * 
 * <p>
 * Check also "http://en.wikipedia.org/wiki/Flood_fill".
 * </p>
 * 
 * @see MinimaAndMaxima
 * 
 * @author David Legland
 */
public class FloodFill
{
	/**
	 * Private constructor to prevent class instantiation.
	 */
	private FloodFill()
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
	 * @param x0
	 *            x-coordinate of the seed pixel
	 * @param y0
	 *            y-coordinate of the seed pixel
	 * @param target
	 *            the label image to fill in
	 * @param value
	 *            filling value
	 * @param conn
	 *            connectivity to use (4 or 8)
	 */
	public final static <S, T> void floodFill(Array2D<S> source, int x0, int y0,
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
		int sizeX = source.size(0);
		int sizeY = source.size(1);
		
		// get old value
		S oldValue = source.get(x0, y0);
		
		// initialize the stack with original pixel
		ArrayList<Cursor2D> stack = new ArrayList<Cursor2D>();
		stack.add(new Cursor2D(x0, y0));
				
		boolean inScanLine;
		
		// process all items in stack
		while (!stack.isEmpty()) 
		{
			// Extract current position
			Cursor2D p = stack.remove(stack.size()-1);
			x0 = p.x;
			y0 = p.y;
			
			// process only pixel of the same value
			if (!source.get(x0, y0).equals(oldValue))
				continue;
			
			// x extremities of scan-line
			int x1 = x0; 
			int x2 = x0;
			
			// find start of scan-line
			while (x1 > 0 && source.get(x1-1, y0).equals(oldValue))
				x1--;
			
			// find end of scan-line
			while (x2 < sizeX - 1 && source.get(x2+1, y0).equals(oldValue))
				x2++;
			
			// fill current scan-line
			fillLine(target, y0, x1, x2, value);
			
			// find scan-lines above the current one
			if (y0 > 0)
			{
				inScanLine = false;
				for (int i = max(x1 + dx1, 0); i <= min(x2 + dx2, sizeX - 1); i++)
				{
					S val = source.get(i, y0 - 1);
					T lab = target.get(i, y0 - 1);
					if (!inScanLine && val.equals(oldValue) && !lab.equals(value))
					{
						stack.add(new Cursor2D(i, y0 - 1));
						inScanLine = true;
					} 
					else if (inScanLine && !val.equals(oldValue))
					{
						inScanLine = false;
					}
				}
			}

			// find scan-lines below the current one
			if (y0 < sizeY - 1)
			{
				inScanLine = false;
				for (int i = max(x1 + dx1, 0); i <= min(x2 + dx2, sizeX - 1); i++)
				{
					S val = source.get(i, y0 + 1);
					T lab = target.get(i, y0 + 1);
					if (!inScanLine && val.equals(oldValue) && !lab.equals(value))
					{
						stack.add(new Cursor2D(i, y0 + 1));
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
    private final static <T> void fillLine(Array2D<T> array, int y, int x1, int x2, T value)
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
	 * @param x0
	 *            x-coordinate of the seed pixel
	 * @param y0
	 *            y-coordinate of the seed pixel
	 * @param output
	 *            the label image to fill in
	 * @param value
	 *            filling value
	 * @param conn
	 *            connectivity to use (C4 or C8)
	 */
	public final static void floodFill(ScalarArray2D<?> input, int x0, int y0,
			ScalarArray2D<?> output, double value, Connectivity2D conn)
	{
		if (conn == Connectivity2D.C4)
		{
			floodFill(input, x0, y0, output, value, 4);
		}
		else if (conn == Connectivity2D.C8)
		{
			floodFill(input, x0, y0, output, value, 8);
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
	 * @param x0
	 *            x-coordinate of the seed pixel
	 * @param y0
	 *            y-coordinate of the seed pixel
	 * @param output
	 *            the label image to fill in
	 * @param value
	 *            filling value
	 * @param conn
	 *            connectivity to use (4 or 8)
	 */
	public final static void floodFill(ScalarArray2D<?> input, int x0, int y0,
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
		int sizeX = input.size(0);
		int sizeY = input.size(1);
		
		// get old value
		double oldValue = input.getValue(x0, y0);
		
		// initialize the stack with original pixel
		ArrayList<Cursor2D> stack = new ArrayList<Cursor2D>();
		stack.add(new Cursor2D(x0, y0));
		
		
		boolean inScanLine;
		
		// process all items in stack
		while (!stack.isEmpty()) 
		{
			// Extract current position
			Cursor2D p = stack.remove(stack.size()-1);
			x0 = p.x;
			y0 = p.y;
			
			// process only pixel of the same value
			if (input.getValue(x0, y0) != oldValue) 
				continue;
			
			// x extremities of scan-line
			int x1 = x0; 
			int x2 = x0;
			
			// find start of scan-line
			while (x1 > 0 && input.getValue(x1-1, y0) == oldValue)
				x1--;
			
			// find end of scan-line
			while (x2 < sizeX - 1 && input.getValue(x2+1, y0) == oldValue)
				x2++;
			
			// fill current scan-line
	        for (int x = x1; x <= x2; x++)
	        {
	            output.setValue(x, y0, value);
	        }
	        
			// find scan-lines above the current one
			if (y0 > 0)
			{
				inScanLine = false;
				for (int i = max(x1 + dx1, 0); i <= min(x2 + dx2, sizeX - 1); i++)
				{
					double val = input.getValue(i, y0 - 1);
					double lab = output.getValue(i, y0 - 1);
					if (!inScanLine && val == oldValue && lab != value)
					{
						stack.add(new Cursor2D(i, y0 - 1));
						inScanLine = true;
					} 
					else if (inScanLine && val != oldValue)
					{
						inScanLine = false;
					}
				}
			}

			// find scan-lines below the current one
			if (y0 < sizeY - 1)
			{
				inScanLine = false;
				for (int i = max(x1 + dx1, 0); i <= min(x2 + dx2, sizeX - 1); i++)
				{
					double val = input.getValue(i, y0 + 1);
					double lab = output.getValue(i, y0 + 1);
					if (!inScanLine && val == oldValue && lab != value)
					{
						stack.add(new Cursor2D(i, y0 + 1));
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
	 * Assigns in <code>labelImage</code> all the neighbor pixels of (x,y) that
	 * have the same pixel value in <code>image</code>, the specified new label
	 * value (<code>value</code>), using the specified connectivity.
	 * 
	 * @param input
	 *            original image to read the pixel values from
	 * @param x0
	 *            x-coordinate of the seed pixel
	 * @param y0
	 *            y-coordinate of the seed pixel
	 * @param output
	 *            the label image to fill in
	 * @param value
	 *            filling value
	 * @param conn
	 *            connectivity to use (4 or 8)
	 */
	public final static void floodFillInt(IntArray2D<?> input, int x0, int y0,
			IntArray2D<?> output, int value, Connectivity2D conn)
	{
		if (conn == Connectivity2D.C4)
		{
			floodFillInt(input, x0, y0, output, value, 4);
		}
		else if (conn == Connectivity2D.C8)
		{
			floodFillInt(input, x0, y0, output, value, 8);
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
	 * @param x0
	 *            x-coordinate of the seed pixel
	 * @param y0
	 *            y-coordinate of the seed pixel
	 * @param output
	 *            the label image to fill in
	 * @param value
	 *            filling value
	 * @param conn
	 *            connectivity to use (4 or 8)
	 */
    public final static void floodFillInt(IntArray2D<?> input, int x0, int y0, IntArray2D<?> output,
            int value, int conn)
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
		int sizeX = input.size(0);
		int sizeY = input.size(1);
		
		// get old value
		int oldValue = input.getInt(x0, y0);
				
		// initialize the stack with original pixel
		ArrayList<Cursor2D> stack = new ArrayList<Cursor2D>();
		stack.add(new Cursor2D(x0, y0));
		
		boolean inScanLine;
		
		// process all items in stack
		while (!stack.isEmpty()) 
		{
			// Extract current position
			Cursor2D p = stack.remove(stack.size()-1);
			x0 = p.x;
			y0 = p.y;
			
			// process only pixel of the same value
			if (input.getInt(x0, y0) != oldValue) 
				continue;
			
			// x extremities of scan-line
			int x1 = x0; 
			int x2 = x0;
			
			// find start of scan-line
			while (x1 > 0 && input.getInt(x1-1, y0) == oldValue)
				x1--;
			
			// find end of scan-line
			while (x2 < sizeX - 1 && input.getInt(x2+1, y0) == oldValue)
				x2++;
			
			// fill current scan-line
			for (int x = x1; x <= x2; x++)
			{
			    output.setInt(x, y0, value);
			}

			// find scan-lines above the current one
			if (y0 > 0)
			{
				inScanLine = false;
				for (int i = max(x1 + dx1, 0); i <= min(x2 + dx2, sizeX - 1); i++)
				{
					int val = input.getInt(i, y0 - 1);
					int lab = output.getInt(i, y0 - 1);
					if (!inScanLine && val == oldValue && lab != value)
					{
						stack.add(new Cursor2D(i, y0 - 1));
						inScanLine = true;
					} 
					else if (inScanLine && val != oldValue)
					{
						inScanLine = false;
					}
				}
			}
	
			// find scan-lines below the current one
			if (y0 < sizeY - 1)
			{
				inScanLine = false;
				for (int i = max(x1 + dx1, 0); i <= min(x2 + dx2, sizeX - 1); i++)
				{
					int val = input.getInt(i, y0 + 1);
					int lab = output.getInt(i, y0 + 1);
					if (!inScanLine && val == oldValue && lab != value)
					{
						stack.add(new Cursor2D(i, y0 + 1));
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
	
//	/**
//	 * Assigns to all the neighbor voxels of (x,y,z) that have the same voxel
//	 * value in <code>image</code>, the specified new label value (
//	 * <code>value</code>) in <code>labelImage</code>, using the specified
//	 * connectivity.
//	 * 
//	 * @param inputImage
//	 *            original image to read the voxel values from
//	 * @param x
//	 *            x- coordinate of the seed voxel
//	 * @param y
//	 *            y- coordinate of the seed voxel
//	 * @param z
//	 *            z- coordinate of the seed voxel
//	 * @param outputImage
//	 *            output label image (to fill)
//	 * @param value
//	 *            filling value
//	 * @param conn
//	 *            connectivity to use (6 or 26)
//	 */
//	public final static void floodFillFloat(Array3D<?> inputArray, int x,
//			int y, int z, Array3D<?> outputArray, double value, int conn)
//	{
//		switch (conn)
//		{
//		case 6:
//			floodFillFloatC6(inputArray, x, y, z, outputArray, value);
//			return;
//		case 26:
//			floodFillFloatC26(inputArray, x, y, z, outputArray, value);
//			return;
//		default:
//			throw new IllegalArgumentException(
//					"Connectivity must be either 6 or 26, not " + conn);
//		}
//	}
	
	/**
	 * Assigns to all the neighbor voxels of (x,y,z) that have the same voxel
	 * value in <code>image</code>, the specified new label value (
	 * <code>value</code>) in <code>labelImage</code>, using the specified
	 * connectivity.
	 * 
	 * @param inputArray
	 *            original image to read the voxel values from
	 * @param x0
	 *            x-coordinate of the seed voxel
	 * @param y0
	 *            y-coordinate of the seed voxel
	 * @param z0
	 *            z-coordinate of the seed voxel
	 * @param outputArray
	 *            output label image (to fill)
	 * @param value
	 *            filling value
	 * @param conn
	 *            connectivity to use (C6 or C26)
	 */
    public final static void floodFill(ScalarArray3D<?> inputArray, int x0, int y0, int z0,
            ScalarArray3D<?> outputArray, double value, Connectivity3D conn)
	{
		if (conn == Connectivity3D.C6)
		{
			floodFillC6(inputArray, x0, y0, z0, outputArray, value);
		}
		else if (conn == Connectivity3D.C26)
		{
			floodFillC26(inputArray, x0, y0, z0, outputArray, value);
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
	 * @param x0
	 *            x-coordinate of the seed voxel
	 * @param y0
	 *            y-coordinate of the seed voxel
	 * @param z0
	 *            z-coordinate of the seed voxel
	 * @param outputImage
	 *            output label image (to fill)
	 * @param value
	 *            filling value
	 */
    private final static void floodFillC6(ScalarArray3D<?> inputArray, int x0, int y0, int z0,
            ScalarArray3D<?> outputArray, double value)
	{
		// get image size
		int sizeX = inputArray.size(0);
		int sizeY = inputArray.size(1);
		int sizeZ = inputArray.size(2);

		// get old value
		double oldValue = inputArray.getValue(x0, y0, z0);

		// initialize the stack with original pixel
		ArrayList<Cursor3D> stack = new ArrayList<Cursor3D>();
		stack.add(new Cursor3D(x0, y0, z0));

		boolean inScanLine;

		// process all items in stack
		while (!stack.isEmpty()) 
		{
			// Extract current position
			Cursor3D p = stack.remove(stack.size()-1);
			x0 = p.x;
			y0 = p.y;
			z0 = p.z;

			// process only pixel of the same value
			if (inputArray.getValue(x0, y0, z0) != oldValue)
				continue;

			// x extremities of scan-line
			int x1 = x0; 
			int x2 = x0;

			// find start of scan-line
			while (x1 > 0 && inputArray.getValue(x1 - 1, y0, z0) == oldValue)
				x1--;

			// find end of scan-line
			while (x2 < sizeX - 1 && inputArray.getValue(x2 + 1, y0, z0) == oldValue)
				x2++;

			// fill current scan-line
            for (int x = x1; x <= x2; x++)
            {
                outputArray.setValue(x, y0, z0, value);
            }

			// search bounds on x axis for neighbor lines
			int x1l = max(x1, 0);
			int x2l = min(x2, sizeX - 1);

			// find scan-lines above the current one
			if (y0 > 0) 
			{
				inScanLine = false;
				for (int i = x1l; i <= x2l; i++)
				{
					double val = inputArray.getValue(i, y0 - 1, z0);
					double lab = outputArray.getValue(i, y0 - 1, z0);

					if (!inScanLine && val == oldValue && lab != value) 
					{
						stack.add(new Cursor3D(i, y0 - 1, z0));
						inScanLine = true;
					} 
					else if (inScanLine && val != oldValue)
					{
						inScanLine = false;
					}
				}
			}

			// find scan-lines below the current one
			if (y0 < sizeY - 1) 
			{
				inScanLine = false;
				for (int i = x1l; i <= x2l; i++) 
				{
					double val = inputArray.getValue(i, y0 + 1, z0);
					double lab = outputArray.getValue(i, y0 + 1, z0);

					if (!inScanLine && val == oldValue && lab != value) 
					{
						stack.add(new Cursor3D(i, y0 + 1, z0));
						inScanLine = true;
					}
					else if (inScanLine && val != oldValue)
					{
						inScanLine = false;
					}
				}
			}

			// find scan-lines in front of the current one
			if (z0 > 0)
			{
				inScanLine = false;
				for (int i = x1l; i <= x2l; i++) 
				{
					double val = inputArray.getValue(i, y0, z0 - 1);
					double lab = outputArray.getValue(i, y0, z0 - 1);

					if (!inScanLine && val == oldValue && lab != value) 
					{
						stack.add(new Cursor3D(i, y0, z0 - 1));
						inScanLine = true;
					}
					else if (inScanLine && val != oldValue)
					{
						inScanLine = false;
					}
				}
			}

			// find scan-lines behind the current one
			if (z0 < sizeZ - 1)
			{
				inScanLine = false;
				for (int i = x1l; i <= x2l; i++)
				{
					double val = inputArray.getValue(i, y0, z0 + 1);
					double lab = outputArray.getValue(i, y0, z0 + 1);

					if (!inScanLine && val == oldValue && lab != value)
					{
						stack.add(new Cursor3D(i, y0, z0 + 1));
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
	 * @param x0
	 *            x-coordinate of the seed voxel
	 * @param y0
	 *            y-coordinate of the seed voxel
	 * @param z0
	 *            z-coordinate of the seed voxel
	 * @param outputImage
	 *            output label image (to fill)
	 * @param value
	 *            filling value
	 */
    private final static void floodFillC26(ScalarArray3D<?> inputArray, int x0, int y0, int z0,
            ScalarArray3D<?> outputArray, double value)
	{
		// get image size
		int sizeX = inputArray.size(0);
		int sizeY = inputArray.size(1);
		int sizeZ = inputArray.size(2);
		
		// get old value
		double oldValue = inputArray.getValue(x0, y0, z0);
				
		// initialize the stack with original pixel
		ArrayList<Cursor3D> stack = new ArrayList<Cursor3D>();
		stack.add(new Cursor3D(x0, y0, z0));
		
		boolean inScanLine;
		
		// process all items in stack
		while (!stack.isEmpty())
		{
			// Extract current position
			Cursor3D p = stack.remove(stack.size()-1);
			x0 = p.x;
			y0 = p.y;
			z0 = p.z;
			
			// process only pixel of the same value
			if (inputArray.getValue(x0, y0, z0) != oldValue)
				continue;
			
			// x extremities of scan-line
			int x1 = x0; 
			int x2 = x0;
			
			// find start of scan-line
			while (x1 > 0 && inputArray.getValue(x1-1, y0, z0) == oldValue)
				x1--;
			
			// find end of scan-line
			while (x2 < sizeX - 1 && inputArray.getValue(x2+1, y0, z0) == oldValue)
				x2++;
		
			// fill current scan-line
			for (int x = x1; x <= x2; x++)
			{
			    outputArray.setValue(x, y0, z0, value);
			}

			// search bounds on x axis for neighbor lines
			int x1l = max(x1 - 1, 0);
			int x2l = min(x2 + 1, sizeX - 1);

			// check the eight X-lines around the current one
			for (int z2 = max(z0 - 1, 0); z2 <= min(z0 + 1, sizeZ - 1); z2++) 
			{
				for (int y2 = max(y0 - 1, 0); y2 <= min(y0 + 1, sizeY - 1); y2++) 
				{
					// do not process the middle line
					if (z2 == z0 && y2 == y0)
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

//	/**
//	 * Assigns to all the neighbor voxels of (x,y,z) that have the same voxel
//	 * value in <code>image</code>, the specified new label value (
//	 * <code>value</code>) in <code>labelImage</code>, using the specified
//	 * connectivity.
//	 * 
//	 * @param inputImage
//	 *            original image to read the voxel values from
//	 * @param x
//	 *            x- coordinate of the seed voxel
//	 * @param y
//	 *            y- coordinate of the seed voxel
//	 * @param z
//	 *            z- coordinate of the seed voxel
//	 * @param outputImage
//	 *            output label image (to fill)
//	 * @param value
//	 *            filling value
//	 * @param conn
//	 *            connectivity to use (6 or 26)
//	 */
//	public final static void floodFillInt(IntArray3D<?> inputArray, int x,
//			int y, int z, IntArray3D<?> outputArray, int value, int conn)
//	{
//		switch (conn)
//		{
//		case 6:
//			floodFillIntC6(inputArray, x, y, z, outputArray, value);
//			return;
//		case 26:
//			floodFillIntC26(inputArray, x, y, z, outputArray, value);
//			return;
//		default:
//			throw new IllegalArgumentException(
//					"Connectivity must be either 6 or 26, not " + conn);
//		}
//	}
	

	/**
	 * Assigns to all the neighbor voxels of (x,y,z) that have the same voxel
	 * value in <code>image</code>, the specified new label value (
	 * <code>value</code>) in <code>labelImage</code>, using the specified
	 * connectivity.
	 * 
	 * @param inputArray
	 *            original image to read the voxel values from
	 * @param x0
	 *            x-coordinate of the seed voxel
	 * @param y0
	 *            y-coordinate of the seed voxel
	 * @param z0
	 *            z-coordinate of the seed voxel
	 * @param outputArray
	 *            output label image (to fill)
	 * @param value
	 *            filling value
	 * @param conn
	 *            connectivity to use (6 or 26)
	 */
    public final static void floodFillInt(IntArray3D<?> inputArray, int x0, int y0, int z0,
            IntArray3D<?> outputArray, int value, Connectivity3D conn)
	{
		if (conn == Connectivity3D.C6)
		{
			floodFillIntC6(inputArray, x0, y0, z0, outputArray, value);
		}
		else if (conn == Connectivity3D.C26)
		{
			floodFillIntC26(inputArray, x0, y0, z0, outputArray, value);
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
	 * @param x0
	 *            x-coordinate of the seed voxel
	 * @param y0
	 *            y-coordinate of the seed voxel
	 * @param z0
	 *            z-coordinate of the seed voxel
	 * @param outputImage
	 *            output label image (to fill)
	 * @param value
	 *            filling value
	 */
    private final static void floodFillIntC6(IntArray3D<?> inputArray, int x0, int y0, int z0,
            IntArray3D<?> outputArray, int value)
	{
		// get image size
		int sizeX = inputArray.size(0);
		int sizeY = inputArray.size(1);
		int sizeZ = inputArray.size(2);

		// get old value
		int oldValue = inputArray.getInt(x0, y0, z0);

		// initialize the stack with original position
		ArrayList<Cursor3D> stack = new ArrayList<Cursor3D>();
		stack.add(new Cursor3D(x0, y0, z0));

		boolean inScanLine;

		// process all items in stack
		while (!stack.isEmpty()) 
		{
			// Extract current position
			Cursor3D p = stack.remove(stack.size()-1);
			x0 = p.x;
			y0 = p.y;
			z0 = p.z;

			// process only pixel of the same value
			if (inputArray.getInt(x0, y0, z0) != oldValue)
				continue;

			// x extremities of scan-line
			int x1 = x0; 
			int x2 = x0;

			// find start of scan-line
			while (x1 > 0 && inputArray.getInt(x1 - 1, y0, z0) == oldValue)
				x1--;

			// find end of scan-line
			while (x2 < sizeX - 1 && inputArray.getInt(x2 + 1, y0, z0) == oldValue)
				x2++;

			// fill current scan-line
            for (int x = x1; x <= x2; x++)
            {
                outputArray.setInt(x, y0, z0, value);
            }

			// search bounds on x axis for neighbor lines
			int x1l = max(x1, 0);
			int x2l = min(x2, sizeX - 1);

			// find scan-lines above the current one
			if (y0 > 0) 
			{
				inScanLine = false;
				for (int i = x1l; i <= x2l; i++)
				{
					int val = inputArray.getInt(i, y0 - 1, z0);
					int lab = outputArray.getInt(i, y0 - 1, z0);

					if (!inScanLine && val == oldValue && lab != value) 
					{
						stack.add(new Cursor3D(i, y0 - 1, z0));
						inScanLine = true;
					} 
					else if (inScanLine && val != oldValue)
					{
						inScanLine = false;
					}
				}
			}

			// find scan-lines below the current one
			if (y0 < sizeY - 1) 
			{
				inScanLine = false;
				for (int i = x1l; i <= x2l; i++) 
				{
					int val = inputArray.getInt(i, y0 + 1, z0);
					int lab = outputArray.getInt(i, y0 + 1, z0);

					if (!inScanLine && val == oldValue && lab != value) 
					{
						stack.add(new Cursor3D(i, y0 + 1, z0));
						inScanLine = true;
					}
					else if (inScanLine && val != oldValue)
					{
						inScanLine = false;
					}
				}
			}

			// find scan-lines in front of the current one
			if (z0 > 0)
			{
				inScanLine = false;
				for (int i = x1l; i <= x2l; i++) 
				{
					int val = inputArray.getInt(i, y0, z0 - 1);
					int lab = outputArray.getInt(i, y0, z0 - 1);

					if (!inScanLine && val == oldValue && lab != value) 
					{
						stack.add(new Cursor3D(i, y0, z0 - 1));
						inScanLine = true;
					}
					else if (inScanLine && val != oldValue)
					{
						inScanLine = false;
					}
				}
			}

			// find scan-lines behind the current one
			if (z0 < sizeZ - 1)
			{
				inScanLine = false;
				for (int i = x1l; i <= x2l; i++)
				{
					int val = inputArray.getInt(i, y0, z0 + 1);
					int lab = outputArray.getInt(i, y0, z0 + 1);

					if (!inScanLine && val == oldValue && lab != value)
					{
						stack.add(new Cursor3D(i, y0, z0 + 1));
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
     * @param x0
     *            x-coordinate of the seed voxel
     * @param y0
     *            y-coordinate of the seed voxel
     * @param z0
     *            z-coordinate of the seed voxel
     * @param outputImage
     *            output label image (to fill)
     * @param value
     *            filling value
     */
    private final static void floodFillIntC26(IntArray3D<?> inputArray, int x0, int y0, int z0,
            IntArray3D<?> outputArray, int value)
	{
		// get image size
		int sizeX = inputArray.size(0);
		int sizeY = inputArray.size(1);
		int sizeZ = inputArray.size(2);
		
		// get old value
		int oldValue = inputArray.getInt(x0, y0, z0);
				
		// initialize the stack with original pixel
		ArrayList<Cursor3D> stack = new ArrayList<Cursor3D>();
		stack.add(new Cursor3D(x0, y0, z0));
		
		boolean inScanLine;
		
		// process all items in stack
		while (!stack.isEmpty())
		{
			// Extract current position
			Cursor3D p = stack.remove(stack.size()-1);
			x0 = p.x;
			y0 = p.y;
			z0 = p.z;
			
			// process only pixel of the same value
			if (inputArray.getValue(x0, y0, z0) != oldValue)
				continue;
			
			// x extremities of scan-line
			int x1 = x0; 
			int x2 = x0;
			
			// find start of scan-line
			while (x1 > 0 && inputArray.getValue(x1-1, y0, z0) == oldValue)
				x1--;
			
			// find end of scan-line
			while (x2 < sizeX - 1 && inputArray.getValue(x2+1, y0, z0) == oldValue)
				x2++;
		
			// fill current scan-line
			for (int x = x1; x <= x2; x++)
			{
			    outputArray.setInt(x, y0, z0, value);
			}

			// search bounds on x axis for neighbor lines
			int x1l = max(x1 - 1, 0);
			int x2l = min(x2 + 1, sizeX - 1);

			// check the eight X-lines around the current one
			for (int z2 = max(z0 - 1, 0); z2 <= min(z0 + 1, sizeZ - 1); z2++) 
			{
				for (int y2 = max(y0 - 1, 0); y2 <= min(y0 + 1, sizeY - 1); y2++) 
				{
					// do not process the middle line
					if (z2 == z0 && y2 == y0)
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
