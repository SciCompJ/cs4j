/**
 * 
 */
package net.sci.image.morphology.filter;

import net.sci.array.data.Array2D;
import net.sci.array.data.scalar2d.UInt8Array2D;
import net.sci.image.morphology.Strel;

/**
 * A diagonal linear structuring element of a given length, with direction
 * vector (+1,-1) in image coordinate system. Provides methods for fast in place
 * erosion and dilation.
 * 
 * @see LinearHorizontalStrel
 * @see LinearVerticalStrel
 * @see LinearDiagDownStrel
 * 
 * @author David Legland
 *
 */
public class LinearDiagUpStrel extends AbstractInPlaceStrel
{

	// ==================================================
	// Static methods

	public final static LinearDiagUpStrel fromDiameter(int diam)
	{
		return new LinearDiagUpStrel(diam);
	}

	public final static LinearDiagUpStrel fromRadius(int radius)
	{
		return new LinearDiagUpStrel(2 * radius + 1, radius);
	}

	// ==================================================
	// Class variables

	/**
	 * Number of element in this structuring element. Corresponds to the
	 * horizontal size.
	 */
	int size;

	/**
	 * Position of the origin within the segment. Corresponds to the number of
	 * elements before the reference element.
	 */
	int offset;

	// ==================================================
	// Constructors

	/**
	 * Creates a new diagonal linear structuring element of a given size.
	 * 
	 * @param size
	 *            the number of pixels in this structuring element
	 */
	public LinearDiagUpStrel(int size)
	{
		if (size < 1)
		{
			throw new RuntimeException("Requires a positive size");
		}
		this.size = size;

		this.offset = (int) Math.floor((this.size - 1) / 2);
	}

	// ==================================================
	// General methods

	/**
	 * Creates a new diagonal linear structuring element of a given size and
	 * with a given offset.
	 * 
	 * @param size
	 *            the number of pixels in this structuring element
	 * @param offset
	 *            the position of the reference pixel (between 0 and size-1)
	 */
	public LinearDiagUpStrel(int size, int offset)
	{
		if (size < 1)
		{
			throw new RuntimeException("Requires a positive size");
		}
		this.size = size;

		if (offset < 0)
		{
			throw new RuntimeException("Requires a non-negative offset");
		}
		if (offset >= size)
		{
			throw new RuntimeException("Offset can not be greater than size");
		}
		this.offset = offset;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ijt.morphology.InPlaceStrel#inPlaceDilation(ij.process.Array2D<?>)
	 */
	@Override
	public void inPlaceDilation(Array2D<?> image)
	{
		// If size is one, there is no need to compute
		if (size <= 1)
		{
			return;
		}

		if (image instanceof UInt8Array2D)
			inPlaceDilationGray8((UInt8Array2D) image);
		else
			inPlaceDilationFloat(image);
	}

	private void inPlaceDilationGray8(UInt8Array2D image)
	{
		// get image size
		int sizeX = image.getSize(0);
		int sizeY = image.getSize(1);

		// Consider all diagonal lines with direction vector (+1,-1) that
		// intersect image.
		// Diagonal lines are identified by their intersection "d" with axis
		// (+1,+1)
		// Need to identify bounds for d
		int dmin = 0;
		int dmax = sizeX + sizeY - 1;

		// create local histogram instance
		LocalExtremumBufferGray8 localMax = new LocalExtremumBufferGray8(size,
				LocalExtremum.Type.MAXIMUM);

		// Iterate on diagonal lines
		for (int d = dmin; d < dmax; d++)
		{
			fireProgressChanged(this, d - dmin, dmax - dmin);

			// reset local histogram
			localMax.fill(Strel.BACKGROUND);

			int xmin = Math.max(0, d + 1 - sizeY);
			int xmax = Math.min(sizeX, d + 1);
			int ymin = Math.max(0, d + 1 - sizeX);
			int ymax = Math.min(sizeY, d + 1);

			int tmin = Math.max(xmin, d + 1 - ymax);
			int tmax = Math.min(xmax, d + 1 - ymin);

			// position on the line
			int t = tmin;

			// init local histogram image values after current pos
			while (t < Math.min(tmin + this.offset, tmax))
			{
				localMax.add(image.getInt(t, d - t));
				t++;
			}

			// process position that do not touch lower-left image boundary
			while (t < tmax)
			{
				localMax.add(image.getInt(t, d - t));
				int t2 = t - this.offset;
				image.setInt(t2, d - t2, localMax.getMax());
				t++;
			}

			// process pixels at the end of the line
			// and that do not touch the upper left image boundary
			while (t < tmax + this.offset)
			{
				localMax.add(Strel.BACKGROUND);
				int t2 = t - this.offset;
				int x = t2;
				int y = d - t2;
				if (x >= 0 && y >= 0 && x < sizeX && y < sizeY)
					image.setInt(x, y, localMax.getMax());
				t++;
			}
		}
	}

	private void inPlaceDilationFloat(Array2D<?> image)
	{
		// get image size
		int sizeX = image.getSize(0);
		int sizeY = image.getSize(1);

		// Consider all diagonal lines with direction vector (+1,-1) that
		// intersect image.
		// Diagonal lines are identified by their intersection "d" with axis
		// (+1,+1)
		// Need to identify bounds for d
		int dmin = 0;
		int dmax = sizeX + sizeY - 1;

		// create local histogram instance
		LocalExtremumBufferDouble localMax = new LocalExtremumBufferDouble(size,
				LocalExtremum.Type.MAXIMUM);

		// Iterate on diagonal lines
		for (int d = dmin; d < dmax; d++)
		{
			fireProgressChanged(this, d - dmin, dmax - dmin);

			// reset local histogram
			localMax.fill(Double.NEGATIVE_INFINITY);

			int xmin = Math.max(0, d + 1 - sizeY);
			int xmax = Math.min(sizeX, d + 1);
			int ymin = Math.max(0, d + 1 - sizeX);
			int ymax = Math.min(sizeY, d + 1);

			int tmin = Math.max(xmin, d + 1 - ymax);
			int tmax = Math.min(xmax, d + 1 - ymin);

			// position on the line
			int t = tmin;

			// init local histogram image values after current pos
			while (t < Math.min(tmin + this.offset, tmax))
			{
				localMax.add(image.getValue(t, d - t));
				t++;
			}

			// process position that do not touch lower-left image boundary
			while (t < tmax)
			{
				localMax.add(image.getValue(t, d - t));
				int t2 = t - this.offset;
				image.setValue(t2, d - t2, localMax.getMax());
				t++;
			}

			// process pixels at the end of the line
			// and that do not touch the upper left image boundary
			while (t < tmax + this.offset)
			{
				localMax.add(Double.NEGATIVE_INFINITY);
				int t2 = t - this.offset;
				int x = t2;
				int y = d - t2;
				if (x >= 0 && y >= 0 && x < sizeX && y < sizeY)
					image.setValue(x, y, localMax.getMax());
				t++;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ijt.morphology.InPlaceStrel#inPlaceErosion(ij.process.Array2D<?>)
	 */
	@Override
	public void inPlaceErosion(Array2D<?> image)
	{
		// If size is one, there is no need to compute
		if (size <= 1)
		{
			return;
		}

		if (image instanceof UInt8Array2D)
			inPlaceErosionGray8((UInt8Array2D) image);
		else
			inPlaceErosionFloat(image);
	}

	private void inPlaceErosionGray8(UInt8Array2D image)
	{
		// get image size
		int sizeX = image.getSize(0);
		int sizeY = image.getSize(1);

		// Consider all diagonal lines with direction vector (+1,-1) that
		// intersect image.
		// Diagonal lines are identified by their intersection "d" with axis
		// (+1,+1)
		// Need to identify bounds for d
		int dmin = 0;
		int dmax = sizeX + sizeY - 1;

		// create local histogram instance
		LocalExtremumBufferGray8 localMin = new LocalExtremumBufferGray8(size,
				LocalExtremum.Type.MINIMUM);

		// Iterate on diagonal lines
		for (int d = dmin; d < dmax; d++)
		{
			fireProgressChanged(this, d - dmin, dmax - dmin);

			// reset local histogram
			localMin.fill(Strel.FOREGROUND);

			int xmin = Math.max(0, d - sizeY - 1);
			int xmax = Math.min(sizeX, d + 1);
			int ymin = Math.max(0, d - sizeX - 1);
			int ymax = Math.min(sizeY, d + 1);

			int tmin = Math.max(xmin, d - ymax + 1);
			int tmax = Math.min(xmax, d - ymin + 1);

			// position on the line
			int t = tmin;

			// init local histogram image values after current pos
			while (t < Math.min(tmin + this.offset, tmax))
			{
				localMin.add(image.getInt(t, d - t));
				t++;
			}

			// process position that do not touch lower-left image boundary
			while (t < tmax)
			{
				localMin.add(image.getInt(t, d - t));
				int t2 = t - this.offset;
				image.setInt(t2, d - t2, localMin.getMax());
				t++;
			}

			// process pixels at the end of the line
			// and that do not touch the upper left image boundary
			while (t < tmax + this.offset)
			{
				localMin.add(Strel.FOREGROUND);
				int t2 = t - this.offset;
				int x = t2;
				int y = d - t2;
				if (x >= 0 && y >= 0 && x < sizeX && y < sizeY)
					image.setInt(x, y, localMin.getMax());
				t++;
			}
		}
	}

	private void inPlaceErosionFloat(Array2D<?> image)
	{
		// get image size
		int sizeX = image.getSize(0);
		int sizeY = image.getSize(1);

		// Consider all diagonal lines with direction vector (+1,-1) that
		// intersect image.
		// Diagonal lines are identified by their intersection "d" with axis
		// (+1,+1)
		// Need to identify bounds for d
		int dmin = 0;
		int dmax = sizeX + sizeY - 1;

		// create local histogram instance
		LocalExtremumBufferDouble localMin = new LocalExtremumBufferDouble(size,
				LocalExtremum.Type.MINIMUM);

		// Iterate on diagonal lines
		for (int d = dmin; d < dmax; d++)
		{
			fireProgressChanged(this, d - dmin, dmax - dmin);

			// reset local histogram
			localMin.fill(Double.MAX_VALUE);

			int xmin = Math.max(0, d - sizeY - 1);
			int xmax = Math.min(sizeX, d + 1);
			int ymin = Math.max(0, d - sizeX - 1);
			int ymax = Math.min(sizeY, d + 1);

			int tmin = Math.max(xmin, d - ymax + 1);
			int tmax = Math.min(xmax, d - ymin + 1);

			// position on the line
			int t = tmin;

			// init local histogram image values after current pos
			while (t < Math.min(tmin + this.offset, tmax))
			{
				localMin.add(image.getValue(t, d - t));
				t++;
			}

			// process position that do not touch lower-left image boundary
			while (t < tmax)
			{
				localMin.add(image.getValue(t, d - t));
				int t2 = t - this.offset;
				image.setValue(t2, d - t2, localMin.getMax());
				t++;
			}

			// process pixels at the end of the line
			// and that do not touch the upper left image boundary
			while (t < tmax + this.offset)
			{
				localMin.add(Double.MAX_VALUE);
				int t2 = t - this.offset;
				int x = t2;
				int y = d - t2;
				if (x >= 0 && y >= 0 && x < sizeX && y < sizeY)
					image.setValue(x, y, localMin.getMax());
				t++;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ijt.morphology.Strel#getMask()
	 */
	@Override
	public int[][] getMask()
	{
		int[][] mask = new int[this.size][this.size];
		for (int i = 0; i < this.size; i++)
		{
			mask[i][i] = 255;
		}

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
		return new int[] { this.offset, this.offset };
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ijt.morphology.Strel#getShifts()
	 */
	@Override
	public int[][] getShifts()
	{
		int[][] shifts = new int[this.size][2];
		for (int i = 0; i < this.size; i++)
		{
			shifts[i][0] = i - this.offset;
			shifts[i][1] = i - this.offset;
		}
		return shifts;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ijt.morphology.Strel#getSize()
	 */
	@Override
	public int[] getSize()
	{
		return new int[] { this.size, this.size };
	}

	/**
	 * Returns a linear diagonal line with same size and offset equal to
	 * size-offset.
	 * 
	 * @see inra.ijpb.morphology.Strel#reverse()
	 */
	@Override
	public LinearDiagUpStrel reverse()
	{
		return new LinearDiagUpStrel(this.size, this.size - this.offset - 1);
	}

}