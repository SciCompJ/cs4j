/**
 * 
 */
package net.sci.image.morphology.filter;

import net.sci.array.data.Array2D;
import net.sci.array.data.scalar2d.UInt8Array2D;
import net.sci.image.morphology.Strel;

/**
 * Structuring element representing a 3x3 cross, that considers the reference
 * pixel together with four neighbors located either on the left or on the right
 * of the reference pixel.
 * 
 * @see DiamondStrel
 * @author David Legland
 */
public class ShiftedCross3x3Strel
{

	/**
	 * A cross-shaped structuring element located on the left of the reference
	 * pixel.
	 * <p>
	 * 
	 * The structuring has the following shape (x: neighbor, o: reference pixel,
	 * .: irrelevant): s *
	 * 
	 * <pre>
	 * <code>
		 *  . . . . . 
		 *  . . x . . 
		 *  . x x o . 
		 *  . . x . . 
		 *  . . . . . 
		 * </code>
	 * </pre>
	 */
	public final static InPlaceStrel LEFT = new ShiftedCross3x3Strel.Left();

	/**
	 * A cross-shaped structuring element located on the right of the reference
	 * pixel.
	 * <p>
	 * 
	 * The structuring has the following shape (x: neighbor, o: reference pixel,
	 * .: irrelevant):
	 * 
	 * <pre>
	 * <code>
	 *  . . . . . 
	 *  . . x . . 
	 *  . o x x . 
	 *  . . x . . 
	 *  . . . . . 
	 * </code>
	 * </pre>
	 */
	public final static InPlaceStrel RIGHT = new ShiftedCross3x3Strel.Right();

	/**
	 * A cross-shaped structuring element located on the left of the reference
	 * pixel.
	 * </p>
	 * 
	 * The structuring has the following shape (x: neighbor, o: reference pixel,
	 * .: irrelevant): <code><pre>
	 *  . . . . . 
	 *  . . x . . 
	 *  . x x o . 
	 *  . . x . . 
	 *  . . . . . 
	 * </pre></code>
	 */
	private final static class Left extends AbstractInPlaceStrel
	{

		/**
		 * Default constructor.
		 */
		public Left()
		{
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see Strel#getSize()
		 */
		@Override
		public int[] getSize()
		{
			return new int[] { 3, 3 };
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see Strel#getMask()
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
		 * @see Strel#getOffset()
		 */
		@Override
		public int[] getOffset()
		{
			return new int[] { 2, 1 };
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see Strel#getShifts()
		 */
		@Override
		public int[][] getShifts()
		{
			int[][] shifts = new int[][] { { -1, -1 }, { -2, 0 }, { -1, 0 },
					{ 0, 0 }, { -1, +1 } };
			return shifts;
		}

		/**
		 * Returns this structuring element, as is is self-reverse.
		 * 
		 * @see InPlaceStrel#reverse()
		 */
		@Override
		public InPlaceStrel reverse()
		{
			return RIGHT;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see InPlaceStrel#inPlaceDilation(ij.process.Array2D<?>)
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

			int[][] buffer = new int[3][sizeX];

			// init buffer with background and first two lines
			for (int x = 0; x < sizeX; x++)
			{
				buffer[0][x] = Strel.BACKGROUND;
				buffer[1][x] = Strel.BACKGROUND;
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
						tmp[x] = Strel.BACKGROUND;
				}
				buffer[2] = tmp;

				// process first two pixels independently
				valMax = Math.max(buffer[1][0], Strel.BACKGROUND);
				image.setInt(0, y, valMax);
				valMax = max5(buffer[0][0], buffer[1][0], buffer[1][1],
						buffer[2][0], Strel.BACKGROUND);
				image.setInt(1, y, valMax);

				// Iterate over pixel of the line, starting from the third one
				for (int x = 2; x < sizeX; x++)
				{
					valMax = max5(buffer[0][x - 1], buffer[1][x - 2],
							buffer[1][x - 1], buffer[1][x], buffer[2][x - 1]);
					image.setInt(x, y, valMax);
				}
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

				// process first two pixels independently
				valMax = Math.max(buffer[1][0], Double.NEGATIVE_INFINITY);
				image.setValue(0, y, valMax);
				valMax = max5(buffer[0][0], buffer[1][0], buffer[1][1],
						buffer[2][0], Double.NEGATIVE_INFINITY);
				image.setValue(1, y, valMax);

				// Iterate over pixel of the line, starting from the third one
				for (int x = 2; x < sizeX; x++)
				{
					valMax = max5(buffer[0][x - 1], buffer[1][x - 2],
							buffer[1][x - 1], buffer[1][x], buffer[2][x - 1]);
					image.setValue(x, y, valMax);
				}
			}

			// clear the progress bar
			fireProgressChanged(this, sizeY, sizeY);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see InPlaceStrel#inPlaceErosion(ij.process.Array2D<?>)
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
				buffer[0][x] = Strel.FOREGROUND;
				buffer[1][x] = Strel.FOREGROUND;
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
						tmp[x] = Strel.FOREGROUND;
				}
				buffer[2] = tmp;

				// process first pixel independently
				valMin = Math.min(buffer[1][0], Strel.FOREGROUND);
				image.setInt(0, y, valMin);
				valMin = min5(buffer[0][0], buffer[1][0], buffer[1][1],
						buffer[2][0], Strel.FOREGROUND);
				image.setInt(1, y, valMin);

				// Iterate over pixel of the line
				for (int x = 2; x < sizeX; x++)
				{
					valMin = min5(buffer[0][x - 1], buffer[1][x - 2],
							buffer[1][x - 1], buffer[1][x], buffer[2][x - 1]);
					image.setInt(x, y, valMin);
				}
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

			// init buffer with background and first two lines
			for (int x = 0; x < sizeX; x++)
			{
				buffer[0][x] = Double.POSITIVE_INFINITY;
				buffer[1][x] = Double.POSITIVE_INFINITY;
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
						tmp[x] = Double.POSITIVE_INFINITY;
				}
				buffer[2] = tmp;

				// process first pixel independently
				valMin = Math.min(buffer[1][0], Double.POSITIVE_INFINITY);
				image.setValue(0, y, valMin);
				valMin = min5(buffer[0][0], buffer[1][0], buffer[1][1],
						buffer[2][0], Double.POSITIVE_INFINITY);
				image.setValue(1, y, valMin);

				// Iterate over pixel of the line
				for (int x = 2; x < sizeX; x++)
				{
					valMin = min5(buffer[0][x - 1], buffer[1][x - 2],
							buffer[1][x - 1], buffer[1][x], buffer[2][x - 1]);
					image.setValue(x, y, valMin);
				}
			}

			// clear the progress bar
			fireProgressChanged(this, sizeY, sizeY);
		}
	}

	/**
	 * A cross-shaped structuring element located on the right of the reference
	 * pixel.
	 * </p>
	 * 
	 * The structuring has the following shape (x: neighbor, o: reference pixel,
	 * .: irrelevant): <code><pre>
	 *  . . . . . 
	 *  . . x . . 
	 *  . o x x . 
	 *  . . x . . 
	 *  . . . . . 
	 * </pre></code>
	 */
	private final static class Right extends AbstractInPlaceStrel
	{

		/**
		 * Default constructor.
		 */
		public Right()
		{
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see Strel#getSize()
		 */
		@Override
		public int[] getSize()
		{
			return new int[] { 3, 3 };
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see Strel#getMask()
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
		 * @see Strel#getOffset()
		 */
		@Override
		public int[] getOffset()
		{
			return new int[] { 0, 1 };
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see Strel#getShifts()
		 */
		@Override
		public int[][] getShifts()
		{
			int[][] shifts = new int[][] { { +1, -1 }, { 0, 0 }, { +1, 0 },
					{ +2, 0 }, { +1, +1 } };
			return shifts;
		}

		/**
		 * Returns this structuring element, as is is self-reverse.
		 * 
		 * @see InPlaceStrel#reverse()
		 */
		@Override
		public InPlaceStrel reverse()
		{
			return LEFT;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see InPlaceStrel#inPlaceDilation(ij.process.Array2D<?>)
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

			int[][] buffer = new int[3][sizeX];

			// init buffer with background and first two lines
			for (int x = 0; x < sizeX; x++)
			{
				buffer[0][x] = Strel.BACKGROUND;
				buffer[1][x] = Strel.BACKGROUND;
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
						tmp[x] = Strel.BACKGROUND;
				}
				buffer[2] = tmp;

				// Iterate over pixels of the line
				for (int x = 0; x < sizeX - 2; x++)
				{
					valMax = max5(buffer[0][x + 1], buffer[1][x],
							buffer[1][x + 1], buffer[1][x + 2],
							buffer[2][x + 1]);
					image.setInt(x, y, valMax);
				}

				// process last two pixels independently
				valMax = max5(buffer[0][sizeX - 1], buffer[1][sizeX - 2],
						buffer[1][sizeX - 1], buffer[2][sizeX - 1],
						Strel.BACKGROUND);
				image.setInt(sizeX - 2, y, valMax);
				valMax = Math.max(buffer[1][sizeX - 1], Strel.BACKGROUND);
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

				// Iterate over pixels of the line
				for (int x = 0; x < sizeX - 2; x++)
				{
					valMax = max5(buffer[0][x + 1], buffer[1][x],
							buffer[1][x + 1], buffer[1][x + 2],
							buffer[2][x + 1]);
					image.setValue(x, y, valMax);
				}

				// process last two pixels independently
				valMax = max5(buffer[0][sizeX - 1], buffer[1][sizeX - 2],
						buffer[1][sizeX - 1], buffer[2][sizeX - 1],
						Double.NEGATIVE_INFINITY);
				image.setValue(sizeX - 2, y, valMax);
				valMax = Math.max(buffer[1][sizeX - 1],
						Double.NEGATIVE_INFINITY);
				image.setValue(sizeX - 1, y, valMax);
			}

			// clear the progress bar
			fireProgressChanged(this, sizeY, sizeY);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see InPlaceStrel#inPlaceErosion(ij.process.Array2D<?>)
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
				buffer[0][x] = Strel.FOREGROUND;
				buffer[1][x] = Strel.FOREGROUND;
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
						tmp[x] = Strel.FOREGROUND;
				}
				buffer[2] = tmp;

				// Iterate over pixels of the line
				for (int x = 0; x < sizeX - 2; x++)
				{
					valMin = min5(buffer[0][x + 1], buffer[1][x],
							buffer[1][x + 1], buffer[1][x + 2],
							buffer[2][x + 1]);
					image.setInt(x, y, valMin);
				}

				// process last two pixels independently
				valMin = min5(buffer[0][sizeX - 1], buffer[1][sizeX - 2],
						buffer[1][sizeX - 1], buffer[2][sizeX - 1],
						Strel.FOREGROUND);
				image.setInt(sizeX - 2, y, valMin);
				valMin = Math.min(buffer[1][sizeX - 1], Strel.FOREGROUND);
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

			// init buffer with background and first two lines
			for (int x = 0; x < sizeX; x++)
			{
				buffer[0][x] = Double.POSITIVE_INFINITY;
				buffer[1][x] = Double.POSITIVE_INFINITY;
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
						tmp[x] = Double.POSITIVE_INFINITY;
				}
				buffer[2] = tmp;

				// Iterate over pixels of the line
				for (int x = 0; x < sizeX - 2; x++)
				{
					valMin = min5(buffer[0][x + 1], buffer[1][x],
							buffer[1][x + 1], buffer[1][x + 2],
							buffer[2][x + 1]);
					image.setValue(x, y, valMin);
				}

				// process last two pixels independently
				valMin = min5(buffer[0][sizeX - 1], buffer[1][sizeX - 2],
						buffer[1][sizeX - 1], buffer[2][sizeX - 1],
						Double.POSITIVE_INFINITY);
				image.setValue(sizeX - 2, y, valMin);
				valMin = Math.min(buffer[1][sizeX - 1],
						Double.POSITIVE_INFINITY);
				image.setValue(sizeX - 1, y, valMin);
			}

			// clear the progress bar
			fireProgressChanged(this, sizeY, sizeY);
		}

	}

	/**
	 * Makes default constructor private to avoid instantiation.
	 */
	private ShiftedCross3x3Strel()
	{
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
	 * Computes the minimum of the 5 double values.
	 */
	private final static double min5(double v1, double v2, double v3, double v4,
			double v5)
	{
		double min1 = Math.min(v1, v2);
		double min2 = Math.min(v3, v4);
		min1 = Math.min(min1, v5);
		return Math.min(min1, min2);
	}

	/**
	 * Computes the maximum of the 5 values.
	 */
	private final static int max5(int v1, int v2, int v3, int v4, int v5)
	{
		int max1 = Math.max(v1, v2);
		int max2 = Math.max(v3, v4);
		max1 = Math.max(max1, v5);
		return Math.max(max1, max2);
	}

	/**
	 * Computes the maximum of the 5 double values.
	 */
	private final static double max5(double v1, double v2, double v3, double v4,
			double v5)
	{
		double max1 = Math.max(v1, v2);
		double max2 = Math.max(v3, v4);
		max1 = Math.max(max1, v5);
		return Math.max(max1, max2);
	}

}