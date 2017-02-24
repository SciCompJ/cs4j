/**
 * 
 */
package net.sci.image.morphology.filter;

import net.sci.array.data.Array2D;
import net.sci.array.data.scalar2d.UInt8Array2D;
import net.sci.image.morphology.Strel2D;

/**
 * A vertical linear structuring element of a given length. Provides methods for
 * fast in place erosion and dilation.
 * 
 * @see LinearHorizontalStrel
 * @see LinearDiagUpStrel
 * @see LinearDiagDownStrel
 * @author David Legland
 *
 */
public class LinearVerticalStrel extends AbstractInPlaceStrel2D
{

	// ==================================================
	// Static methods

	public final static LinearVerticalStrel fromDiameter(int diam)
	{
		return new LinearVerticalStrel(diam);
	}

	public final static LinearVerticalStrel fromRadius(int radius)
	{
		return new LinearVerticalStrel(2 * radius + 1, radius);
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
	 * Creates a new vertical linear structuring element of a given size.
	 * 
	 * @param size
	 *            the number of pixels in this structuring element
	 */
	public LinearVerticalStrel(int size)
	{
		if (size < 1)
		{
			throw new RuntimeException("Requires a positive size");
		}
		this.size = size;

		this.offset = (int) Math.floor((this.size - 1) / 2);
	}

	/**
	 * Creates a new vertical linear structuring element of a given size and
	 * with a given offset.
	 * 
	 * @param size
	 *            the number of pixels in this structuring element
	 * @param offset
	 *            the position of the reference pixel (between 0 and size-1)
	 */
	public LinearVerticalStrel(int size, int offset)
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

	
	// ==================================================
	// Implementation of the InPlaceStrel interface

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sci.image.morphology.filter.InPlaceStrel#inPlaceDilation(ij.process.Array2D<?>)
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
		int width = image.getSize(0);
		int height = image.getSize(1);

		// shifts between reference position and last position
		int shift = this.size - this.offset - 1;

		// create local histogram instance
		LocalExtremumBufferInt localMax = new LocalExtremumBufferInt(size,
				LocalExtremum.Type.MAXIMUM);

		// Iterate on image columns
		for (int x = 0; x < width; x++)
		{
			fireProgressChanged(this, x, width);

			// reset local histogram
			localMax.fill(Strel2D.BACKGROUND);

			// init local histogram with neighbor values
			for (int y = 0; y < Math.min(shift, height); y++)
			{
				localMax.add(image.getInt(x, y));
			}

			// iterate along "middle" values
			for (int y = 0; y < height - shift; y++)
			{
				localMax.add(image.getInt(x, y + shift));
				image.setInt(x, y, (int) localMax.getMax());
			}

			// process pixels at the end of the line
			for (int y = Math.max(0, height - shift); y < height; y++)
			{
				localMax.add(Strel2D.BACKGROUND);
				image.setInt(x, y, localMax.getMax());
			}
		}

		// clear the progress bar
		fireProgressChanged(this, width, width);
	}

	private void inPlaceDilationFloat(Array2D<?> image)
	{
		// get image size
		int width = image.getSize(0);
		int height = image.getSize(1);

		// shifts between reference position and last position
		int shift = this.size - this.offset - 1;

		// create local histogram instance
		LocalExtremumBufferDouble localMax = new LocalExtremumBufferDouble(size,
				LocalExtremum.Type.MAXIMUM);

		// Iterate on image columns
		for (int x = 0; x < width; x++)
		{
			fireProgressChanged(this, x, width);

			// reset local histogram
			localMax.fill(Double.NEGATIVE_INFINITY);

			// init local histogram with neighbor values
			for (int y = 0; y < Math.min(shift, height); y++)
			{
				localMax.add(image.getValue(x, y));
			}

			// iterate along "middle" values
			for (int y = 0; y < height - shift; y++)
			{
				localMax.add(image.getValue(x, y + shift));
				image.setValue(x, y, (float) localMax.getMax());
			}

			// process pixels at the end of the line
			for (int y = Math.max(0, height - shift); y < height; y++)
			{
				localMax.add(Double.NEGATIVE_INFINITY);
				image.setValue(x, y, localMax.getMax());
			}
		}

		// clear the progress bar
		fireProgressChanged(this, width, width);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sci.image.morphology.filter.InPlaceStrel#inPlaceErosion(ij.process.Array2D<?>)
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
		int width = image.getSize(0);
		int height = image.getSize(1);

		// shifts between reference position and last position
		int shift = this.size - this.offset - 1;

		// create local histogram instance
		LocalExtremumBufferInt localMin = new LocalExtremumBufferInt(size,
				LocalExtremum.Type.MINIMUM);

		// Iterate on image columns
		for (int x = 0; x < width; x++)
		{
			fireProgressChanged(this, x, width);

			// reset local histogram
			localMin.fill(Strel2D.FOREGROUND);

			// init local histogram with neighbor values
			for (int y = 0; y < Math.min(shift, height); y++)
			{
				localMin.add(image.getInt(x, y));
			}

			// iterate along "middle" values
			for (int y = 0; y < height - shift; y++)
			{
				localMin.add(image.getInt(x, y + shift));
				image.setInt(x, y, localMin.getMax());
			}

			// process pixels at the end of the line
			for (int y = Math.max(0, height - shift); y < height; y++)
			{
				localMin.add(Strel2D.FOREGROUND);
				image.setInt(x, y, localMin.getMax());
			}
		}

		// clear the progress bar
		fireProgressChanged(this, width, width);
	}

	private void inPlaceErosionFloat(Array2D<?> image)
	{
		// get image size
		int width = image.getSize(0);
		int height = image.getSize(1);

		// shifts between reference position and last position
		int shift = this.size - this.offset - 1;

		// create local histogram instance
		LocalExtremumBufferDouble localMin = new LocalExtremumBufferDouble(size,
				LocalExtremum.Type.MINIMUM);

		// Iterate on image columns
		for (int x = 0; x < width; x++)
		{
			fireProgressChanged(this, x, width);

			// reset local histogram
			localMin.fill(Double.POSITIVE_INFINITY);

			// init local histogram with neighbor values
			for (int y = 0; y < Math.min(shift, height); y++)
			{
				localMin.add(image.getValue(x, y));
			}

			// iterate along "middle" values
			for (int y = 0; y < height - shift; y++)
			{
				localMin.add(image.getValue(x, y + shift));
				image.setValue(x, y, localMin.getMax());
			}

			// process pixels at the end of the line
			for (int y = Math.max(0, height - shift); y < height; y++)
			{
				localMin.add(Double.POSITIVE_INFINITY);
				image.setValue(x, y, localMin.getMax());
			}
		}

		// clear the progress bar
		fireProgressChanged(this, width, width);
	}

	
	// ==================================================
	// Implementation of the Strel interface

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sci.image.morphology.Strel#getMask()
	 */
	@Override
	public int[][] getMask()
	{
		int[][] mask = new int[this.size][1];
		for (int i = 0; i < this.size; i++)
		{
			mask[i][0] = 255;
		}

		return mask;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sci.image.morphology.Strel#getOffset()
	 */
	@Override
	public int[] getOffset()
	{
		return new int[] { this.offset, this.offset };
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sci.image.morphology.Strel#getShifts()
	 */
	@Override
	public int[][] getShifts()
	{
		int[][] shifts = new int[this.size][2];
		for (int i = 0; i < this.size; i++)
		{
			shifts[i][0] = 0;
			shifts[i][1] = i - this.offset;
		}
		return shifts;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sci.image.morphology.Strel#getSize()
	 */
	@Override
	public int[] getSize()
	{
		return new int[] { 1, this.size };
	}

	/**
	 * Returns a linear vertical line with same size and offset equal to
	 * size-offset-1.
	 * 
	 * @see net.sci.image.morphology.Strel2D#reverse()
	 */
	@Override
	public LinearVerticalStrel reverse()
	{
		return new LinearVerticalStrel(this.size, this.size - this.offset - 1);
	}
}
