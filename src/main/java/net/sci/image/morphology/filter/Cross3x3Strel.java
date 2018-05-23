/**
 * 
 */
package net.sci.image.morphology.filter;

import net.sci.array.Array2D;
import net.sci.array.scalar.UInt8Array2D;
import net.sci.image.morphology.Strel2D;

/**
 * Structuring element representing a 3x3 cross, that considers the center
 * pixels together with the four orthogonal neighbors.
 * 
 * @author David Legland
 *
 */
public class Cross3x3Strel extends AbstractInPlaceStrel2D
{

	/*
	 * (non-Javadoc)
	 * 
	 * @see ijt.morphology.Strel#getSize()
	 */
	@Override
	public int[] getSize()
	{
		return new int[] { 3, 3 };
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ijt.morphology.Strel#getMask()
	 */
	@Override
	public int[][] getMask()
	{
		int[][] mask = new int[3][];
		mask[0] = new int[] { 0, 255, 0 };
		mask[1] = new int[] { 255, 255, 255 };
		mask[2] = new int[] { 0, 255, 0 };
		return mask;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ijt.morphology.Strel#getOffset()
	 */
	@Override
	public int[] getOffset()
	{
		return new int[] { 1, 1 };
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ijt.morphology.Strel#getShifts()
	 */
	@Override
	public int[][] getShifts()
	{
		int[][] shifts = new int[][] { { 0, -1 }, { -1, 0 }, { 0, 0 },	{ +1, 0 }, { 0, +1 } };
		return shifts;
	}

	/**
	 * Returns this structuring element, as is is self-reverse.
	 * 
	 * @see InPlaceStrel2D#reverse()
	 */
	@Override
	public InPlaceStrel2D reverse()
	{
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sci.array.data.Array2D)
	 */
	@Override
	public void inPlaceDilation(Array2D<?> image)
	{
		if (image instanceof UInt8Array2D)
			inPlaceDilationGray8((UInt8Array2D) image);
		else
			inPlaceDilationFloat(image);
	}

	private void inPlaceDilationGray8(UInt8Array2D image)
	{
		// size of image
		int sizeX = image.getSize(0);
		int sizeY = image.getSize(1);
		System.out.println("dialte with cross 3x3 uint8");

		int[][] buffer = new int[3][sizeX];

		// init buffer with background and first two lines
		for (int x = 0; x < sizeX; x++)
		{
			buffer[0][x] = Strel2D.BACKGROUND;
			buffer[1][x] = Strel2D.BACKGROUND;
			buffer[2][x] = image.getInt(x, 0);
		}

		// Iterate over image lines
		int valMax;
		for (int y = 0; y < sizeY; y++)
		{
			fireProgressChanged(this, y, sizeY);

			// permute lines in buffer
			int[] tmp = buffer[0];
			buffer[0] = buffer[1];
			buffer[1] = buffer[2];

			// initialize values of the last line in buffer
			if (y < sizeY - 1)
			{
				for (int x = 0; x < sizeX; x++)
					tmp[x] = image.getInt(x, y + 1);
			} else
			{
				for (int x = 0; x < sizeX; x++)
					tmp[x] = Strel2D.BACKGROUND;
			}
			buffer[2] = tmp;

			// process first pixel independently
			valMax = max5(buffer[0][0], buffer[1][0], buffer[1][1],
					buffer[2][0], Strel2D.BACKGROUND);
			image.setInt(0, y, valMax);

			// Iterate over pixel of the line
			for (int x = 1; x < sizeX - 1; x++)
			{
				valMax = max5(buffer[0][x], buffer[1][x - 1], buffer[1][x],
						buffer[1][x + 1], buffer[2][x]);
				image.setInt(x, y, valMax);
			}

			// process last pixel independently
			valMax = max5(buffer[0][sizeX - 1], buffer[1][sizeX - 2],
					buffer[1][sizeX - 1], buffer[2][sizeX - 1],
					Strel2D.BACKGROUND);
			image.setInt(sizeX - 1, y, valMax);
		}

		// clear the progress bar
		fireProgressChanged(this, sizeY, sizeY);
	}

	private void inPlaceDilationFloat(Array2D<?> image)
	{
		// size of image
		int sizeX = image.getSize(0);
		int sizeY = image.getSize(1);

		double[][] buffer = new double[3][sizeX];

		// init buffer with background and first two lines
		for (int x = 0; x < sizeX; x++)
		{
			buffer[0][x] = Double.NEGATIVE_INFINITY;
			buffer[1][x] = Double.NEGATIVE_INFINITY;
			buffer[2][x] = image.getValue(x, 0);
		}

		// Iterate over image lines
		double valMax;
		for (int y = 0; y < sizeY; y++)
		{
			fireProgressChanged(this, y, sizeY);

			// permute lines in buffer
			double[] tmp = buffer[0];
			buffer[0] = buffer[1];
			buffer[1] = buffer[2];

			// initialize values of the last line in buffer
			if (y < sizeY - 1)
			{
				for (int x = 0; x < sizeX; x++)
					tmp[x] = image.getValue(x, y + 1);
			} else
			{
				for (int x = 0; x < sizeX; x++)
					tmp[x] = Double.NEGATIVE_INFINITY;
			}
			buffer[2] = tmp;

			// process first pixel independently
			valMax = max5(buffer[0][0], buffer[1][0], buffer[1][1],
					buffer[2][0], Double.NEGATIVE_INFINITY);
			image.setValue(0, y, valMax);

			// Iterate over pixel of the line
			for (int x = 1; x < sizeX - 1; x++)
			{
				valMax = max5(buffer[0][x], buffer[1][x - 1], buffer[1][x],
						buffer[1][x + 1], buffer[2][x]);
				image.setValue(x, y, valMax);
			}

			// process last pixel independently
			valMax = max5(buffer[0][sizeX - 1], buffer[1][sizeX - 2],
					buffer[1][sizeX - 1], buffer[2][sizeX - 1],
					Double.NEGATIVE_INFINITY);
			image.setValue(sizeX - 1, y, valMax);
		}

		// clear the progress bar
		fireProgressChanged(this, sizeY, sizeY);
	}

	/**
	 * Computes the maximum of the 5 integer values.
	 */
	private final static int max5(int v1, int v2, int v3, int v4, int v5)
	{
		int max1 = Math.max(v1, v2);
		int max2 = Math.max(v3, v4);
		max1 = Math.max(max1, v5);
		return Math.max(max1, max2);
	}

	/**
	 * Computes the maximum of the 5 float values.
	 */
	private final static double max5(double v1, double v2, double v3, double v4,
			double v5)
	{
		double max1 = Math.max(v1, v2);
		double max2 = Math.max(v3, v4);
		max1 = Math.max(max1, v5);
		return Math.max(max1, max2);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ijt.morphology.InPlaceStrel#inPlaceErosion(ij.process.Array2D<?>)
	 */
	@Override
	public void inPlaceErosion(Array2D<?> image)
	{
		if (image instanceof UInt8Array2D)
			inPlaceErosionGray8((UInt8Array2D) image);
		else
			inPlaceErosionFloat(image);
	}

	private void inPlaceErosionGray8(UInt8Array2D image)
	{
		// size of image
		int sizeX = image.getSize(0);
		int sizeY = image.getSize(1);

		int[][] buffer = new int[3][sizeX];

		// init buffer with background and first two lines
		for (int x = 0; x < sizeX; x++)
		{
			buffer[0][x] = Strel2D.FOREGROUND;
			buffer[1][x] = Strel2D.FOREGROUND;
			buffer[2][x] = image.getInt(x, 0);
		}

		// Iterate over image lines
		int valMin;
		for (int y = 0; y < sizeY; y++)
		{
			fireProgressChanged(this, y, sizeY);

			// permute lines in buffer
			int[] tmp = buffer[0];
			buffer[0] = buffer[1];
			buffer[1] = buffer[2];

			// initialize values of the last line in buffer
			if (y < sizeY - 1)
			{
				for (int x = 0; x < sizeX; x++)
					tmp[x] = image.getInt(x, y + 1);
			} else
			{
				for (int x = 0; x < sizeX; x++)
					tmp[x] = Strel2D.FOREGROUND;
			}
			buffer[2] = tmp;

			// process first pixel independently
			valMin = min5(buffer[0][0], buffer[1][0], buffer[1][1],
					buffer[2][0], Strel2D.FOREGROUND);
			image.setInt(0, y, valMin);

			// Iterate over pixel of the line
			for (int x = 1; x < sizeX - 1; x++)
			{
				valMin = min5(buffer[0][x], buffer[1][x - 1], buffer[1][x],
						buffer[1][x + 1], buffer[2][x]);
				image.setInt(x, y, valMin);
			}

			// process last pixel independently
			valMin = min5(buffer[0][sizeX - 1], buffer[1][sizeX - 2],
					buffer[1][sizeX - 1], buffer[2][sizeX - 1],
					Strel2D.FOREGROUND);
			image.setInt(sizeX - 1, y, valMin);
		}

		// clear the progress bar
		fireProgressChanged(this, sizeY, sizeY);
	}

	private void inPlaceErosionFloat(Array2D<?> image)
	{
		// size of image
		int sizeX = image.getSize(0);
		int sizeY = image.getSize(1);

		double[][] buffer = new double[3][sizeX];

		// init buffer with background and first line
		for (int x = 0; x < sizeX; x++)
		{
			buffer[0][x] = Double.MAX_VALUE;
			buffer[1][x] = Double.MAX_VALUE;
			buffer[2][x] = image.getValue(x, 0);
		}

		// Iterate over image lines
		double valMin;
		for (int y = 0; y < sizeY; y++)
		{
			fireProgressChanged(this, y, sizeY);

			// permute lines in buffer
			double[] tmp = buffer[0];
			buffer[0] = buffer[1];
			buffer[1] = buffer[2];

			// initialize values of the last line in buffer
			if (y < sizeY - 1)
			{
				for (int x = 0; x < sizeX; x++)
					tmp[x] = image.getValue(x, y + 1);
			} else
			{
				for (int x = 0; x < sizeX; x++)
					tmp[x] = Double.MAX_VALUE;
			}
			buffer[2] = tmp;

			// process first pixel independently
			valMin = min5(buffer[0][0], buffer[1][0], buffer[1][1],
					buffer[2][0], Double.MAX_VALUE);
			image.setValue(0, y, valMin);

			// Iterate over pixel of the line
			for (int x = 1; x < sizeX - 1; x++)
			{
				valMin = min5(buffer[0][x], buffer[1][x - 1], buffer[1][x],
						buffer[1][x + 1], buffer[2][x]);
				image.setValue(x, y, valMin);
			}

			// process last pixel independently
			valMin = min5(buffer[0][sizeX - 1], buffer[1][sizeX - 2],
					buffer[1][sizeX - 1], buffer[2][sizeX - 1],
					Double.MAX_VALUE);
			image.setValue(sizeX - 1, y, valMin);
		}

		// clear the progress bar
		fireProgressChanged(this, sizeY, sizeY);
	}

	/**
	 * Computes the minimum of the 5 values.
	 */
	private final static int min5(int v1, int v2, int v3, int v4, int v5)
	{
		int min1 = Math.min(v1, v2);
		int min2 = Math.min(v3, v4);
		min1 = Math.min(min1, v5);
		return Math.min(min1, min2);
	}

	/**
	 * Computes the minimum of the 5 float values.
	 */
	private final static double min5(double v1, double v2, double v3, double v4, double v5)
	{
		double min1 = Math.min(v1, v2);
		double min2 = Math.min(v3, v4);
		min1 = Math.min(min1, v5);
		return Math.min(min1, min2);
	}
}
