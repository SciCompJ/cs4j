/**
 * 
 */
package net.sci.image.morphology.filter;

import net.sci.array.data.Array2D;
import net.sci.array.data.scalar2d.UInt8Array2D;
import net.sci.image.morphology.Strel2D;

/**
 * An horizontal linear structuring element of a given length. Provides methods
 * for fast in place erosion and dilation.
 * 
 * @see LinearVerticalStrel
 * @see LinearDiagUpStrel
 * @see LinearDiagDownStrel
 * @author David Legland
 *
 */
public class LinearHorizontalStrel extends AbstractInPlaceStrel2D
{
	// ==================================================
	// Static methods

	public final static LinearHorizontalStrel fromDiameter(int diam)
	{
		return new LinearHorizontalStrel(diam);
	}

	public final static LinearHorizontalStrel fromRadius(int radius)
	{
		return new LinearHorizontalStrel(2 * radius + 1, radius);
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
	 * Creates a new horizontal linear structuring element of a given size.
	 * 
	 * @param size
	 *            the number of pixels in this structuring element
	 */
	public LinearHorizontalStrel(int size)
	{
		if (size < 1)
		{
			throw new RuntimeException("Requires a positive size");
		}
		this.size = size;

		this.offset = (int) Math.floor((this.size - 1) / 2);
	}

	/**
	 * Creates a new horizontal linear structuring element of a given size and
	 * with a given offset.
	 * 
	 * @param size
	 *            the number of pixels in this structuring element
	 * @param offset
	 *            the position of the reference pixel (between 0 and size-1)
	 */
	public LinearHorizontalStrel(int size, int offset)
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
	 * @see
	 * net.sci.image.morphology.filter.InPlaceStrel#inPlaceDilation(Array2D)
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

		// shifts between reference position and last position
		int shift = this.size - this.offset - 1;

		// create local histogram instance
		LocalExtremumBufferGray8 localMax = new LocalExtremumBufferGray8(size,
				LocalExtremum.Type.MAXIMUM);

		// Iterate on image rows
		for (int y = 0; y < sizeY; y++)
		{
			fireProgressChanged(this, y, sizeY);

			// init local histogram with background values
			localMax.fill(Strel2D.BACKGROUND);

			// add neighbor values
			for (int x = 0; x < Math.min(shift, sizeX); x++)
			{
				localMax.add(image.getInt(x, y));
			}

			// iterate along "middle" values
			for (int x = 0; x < sizeX - shift; x++)
			{
				localMax.add(image.getInt(x + shift, y));
				image.setInt(x, y, localMax.getMax());
			}

			// process pixels at the end of the line
			for (int x = Math.max(0, sizeX - shift); x < sizeX; x++)
			{
				localMax.add(Strel2D.BACKGROUND);
				image.setInt(x, y, localMax.getMax());
			}
		}

		// clear the progress bar
		fireProgressChanged(this, sizeY, sizeY);
	}

	private void inPlaceDilationFloat(Array2D<?> image)
	{
		// get image size
		int sizeX = image.getSize(0);
		int sizeY = image.getSize(1);

		// shifts between reference position and last position
		int shift = this.size - this.offset - 1;

		// create local histogram instance
		LocalExtremumBufferDouble localMax = new LocalExtremumBufferDouble(size,
				LocalExtremum.Type.MAXIMUM);

		// Iterate on image rows
		for (int y = 0; y < sizeY; y++)
		{
			fireProgressChanged(this, y, sizeY);

			// init local histogram with background values
			localMax.fill(Double.NEGATIVE_INFINITY);

			// add neighbor values
			for (int x = 0; x < Math.min(shift, sizeX); x++)
			{
				localMax.add(image.getValue(x, y));
			}

			// iterate along "middle" values
			for (int x = 0; x < sizeX - shift; x++)
			{
				localMax.add(image.getValue(x + shift, y));
				image.setValue(x, y, localMax.getMax());
			}

			// process pixels at the end of the line
			for (int x = Math.max(0, sizeX - shift); x < sizeX; x++)
			{
				localMax.add(Double.NEGATIVE_INFINITY);
				image.setValue(x, y, localMax.getMax());
			}
		}

		// clear the progress bar
		fireProgressChanged(this, sizeY, sizeY);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * inra.ijpb.morphology.InPlaceStrel#inPlaceErosion(Array2D)
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

		// shifts between reference position and last position
		int shift = this.size - this.offset - 1;

		// create local histogram instance
		LocalExtremumBufferGray8 localMin = new LocalExtremumBufferGray8(size,
				LocalExtremum.Type.MINIMUM);

		// Iterate on image rows
		for (int y = 0; y < sizeY; y++)
		{
			fireProgressChanged(this, y, sizeY);

			// reset local histogram
			localMin.fill(Strel2D.FOREGROUND);

			// init local histogram with neighbor values
			for (int x = 0; x < Math.min(shift, sizeX); x++)
			{
				localMin.add(image.getInt(x, y));
			}

			// iterate along "middle" values
			for (int x = 0; x < sizeX - shift; x++)
			{
				localMin.add(image.getInt(x + shift, y));
				image.setInt(x, y, localMin.getMax());
			}

			// process pixels at the end of the line
			for (int x = Math.max(0, sizeX - shift); x < sizeX; x++)
			{
				localMin.add(Strel2D.FOREGROUND);
				image.setInt(x, y, localMin.getMax());
			}
		}

		// clear the progress bar
		fireProgressChanged(this, sizeY, sizeY);
	}

	private void inPlaceErosionFloat(Array2D<?> image)
	{
		// get image size
		int sizeX = image.getSize(0);
		int sizeY = image.getSize(1);

		// shifts between reference position and last position
		int shift = this.size - this.offset - 1;

		// create local histogram instance
		LocalExtremumBufferDouble localMin = new LocalExtremumBufferDouble(size,
				LocalExtremum.Type.MINIMUM);

		// Iterate on image rows
		for (int y = 0; y < sizeY; y++)
		{
			fireProgressChanged(this, y, sizeY);

			// reset local histogram
			localMin.fill(Double.POSITIVE_INFINITY);

			// init local histogram with neighbor values
			for (int x = 0; x < Math.min(shift, sizeX); x++)
			{
				localMin.add(image.getValue(x, y));
			}

			// iterate along "middle" values
			for (int x = 0; x < sizeX - shift; x++)
			{
				localMin.add(image.getValue(x + shift, y));
				image.setValue(x, y, localMin.getMax());
			}

			// process pixels at the end of the line
			for (int x = Math.max(0, sizeX - shift); x < sizeX; x++)
			{
				localMin.add(Double.POSITIVE_INFINITY);
				image.setValue(x, y, localMin.getMax());
			}
		}

		// clear the progress bar
		fireProgressChanged(this, sizeY, sizeY);
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
		int[][] mask = new int[1][this.size];
		for (int i = 0; i < this.size; i++)
		{
			mask[0][i] = 255;
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
			shifts[i][0] = i - this.offset;
			shifts[i][1] = 0;
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
		return new int[] { this.size, 1 };
	}

	/**
	 * Returns a linear horizontal line with same size and offset equal to
	 * size-offset-1.
	 * 
	 * @see net.sci.image.morphology.Strel2D#reverse()
	 */
	@Override
	public LinearHorizontalStrel reverse()
	{
		return new LinearHorizontalStrel(this.size, this.size - this.offset - 1);
	}
}
