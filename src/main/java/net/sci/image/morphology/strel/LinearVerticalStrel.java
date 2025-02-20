/**
 * 
 */
package net.sci.image.morphology.strel;

import net.sci.algo.AlgoStub;
import net.sci.array.binary.BinaryArray2D;
import net.sci.array.numeric.IntArray2D;
import net.sci.array.numeric.ScalarArray2D;

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
public class LinearVerticalStrel extends AlgoStub implements InPlaceStrel2D
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
     * @see
     * net.sci.image.morphology.strel.InPlaceStrel#inPlaceDilation(ij.process.
     * ScalarArray2D<?>)
     */
    @Override
    public void inPlaceDilation2d(ScalarArray2D<?> array)
    {
        // If size is one, there is no need to compute
        if (size <= 1)
        {
            return;
        }

        if (array instanceof IntArray2D<?>)
            inPlaceDilationInt((IntArray2D<?>) array);
        else
            inPlaceDilationFloat(array);
    }

    private void inPlaceDilationInt(IntArray2D<?> array)
    {
        // retrieve minimum value allowed within array
        final int defaultValue = array.typeMin().intValue(); 

        // create local histogram instance
        LocalExtremumBufferInt localMax = new LocalExtremumBufferInt(size,
                LocalExtremum.Type.MAXIMUM);

        // get image size
        final int sizeX = array.size(0);
        final int sizeY = array.size(1);
        
        // shifts between reference position and last position
        final int shift = this.size - this.offset - 1;

        // Iterate on image columns
        for (int x = 0; x < sizeX; x++)
        {
            fireProgressChanged(this, x, sizeX);

            // reset local histogram
            localMax.fill(defaultValue);

            // init local histogram with neighbor values
            for (int y = 0; y < Math.min(shift, sizeY); y++)
            {
                localMax.add(array.getInt(x, y));
            }

            // iterate along "middle" values
            for (int y = 0; y < sizeY - shift; y++)
            {
                localMax.add(array.getInt(x, y + shift));
                array.setInt(x, y, localMax.getMax());
            }

            // process pixels at the end of the line
            for (int y = Math.max(0, sizeY - shift); y < sizeY; y++)
            {
                localMax.add(defaultValue);
                array.setInt(x, y, localMax.getMax());
            }
        }

        // clear the progress bar
        fireProgressChanged(this, sizeX, sizeX);
    }

    private void inPlaceDilationFloat(ScalarArray2D<?> array)
    {
        // get image size
        int width = array.size(0);
        int height = array.size(1);

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
                localMax.add(array.getValue(x, y));
            }

            // iterate along "middle" values
            for (int y = 0; y < height - shift; y++)
            {
                localMax.add(array.getValue(x, y + shift));
                array.setValue(x, y, localMax.getMax());
            }

            // process pixels at the end of the line
            for (int y = Math.max(0, height - shift); y < height; y++)
            {
                localMax.add(Double.NEGATIVE_INFINITY);
                array.setValue(x, y, localMax.getMax());
            }
        }

        // clear the progress bar
        fireProgressChanged(this, width, width);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * net.sci.image.morphology.strel.InPlaceStrel#inPlaceErosion(ij.process.
     * ScalarArray2D<?>)
     */
    @Override
    public void inPlaceErosion2d(ScalarArray2D<?> array)
    {
        // If size is one, there is no need to compute
        if (size <= 1)
        {
            return;
        }

        if (array instanceof IntArray2D<?>)
            inPlaceErosionInt((IntArray2D<?>) array);
        else
            inPlaceErosionFloat(array);
    }

    private void inPlaceErosionInt(IntArray2D<?> array)
    {
        // retrieve maximum value allowed within array
        final int defaultValue = array.typeMax().intValue(); 

        // create local histogram instance
        LocalExtremumBufferInt localMin = new LocalExtremumBufferInt(size,
                LocalExtremum.Type.MINIMUM);

        // get image size
        final int sizeX = array.size(0);
        final int sizeY = array.size(1);
        
        // shifts between reference position and last position
        final int shift = this.size - this.offset - 1;

        // Iterate on image columns
        for (int x = 0; x < sizeX; x++)
        {
            fireProgressChanged(this, x, sizeX);

            // reset local histogram
            localMin.fill(defaultValue);

            // init local histogram with neighbor values
            for (int y = 0; y < Math.min(shift, sizeY); y++)
            {
                localMin.add(array.getInt(x, y));
            }

            // iterate along "middle" values
            for (int y = 0; y < sizeY - shift; y++)
            {
                localMin.add(array.getInt(x, y + shift));
                array.setInt(x, y, localMin.getMax());
            }

            // process pixels at the end of the line
            for (int y = Math.max(0, sizeY - shift); y < sizeY; y++)
            {
                localMin.add(defaultValue);
                array.setInt(x, y, localMin.getMax());
            }
        }

        // clear the progress bar
        fireProgressChanged(this, sizeX, sizeX);
    }

    private void inPlaceErosionFloat(ScalarArray2D<?> array)
    {
        // get image size
        int width = array.size(0);
        int height = array.size(1);

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
                localMin.add(array.getValue(x, y));
            }

            // iterate along "middle" values
            for (int y = 0; y < height - shift; y++)
            {
                localMin.add(array.getValue(x, y + shift));
                array.setValue(x, y, localMin.getMax());
            }

            // process pixels at the end of the line
            for (int y = Math.max(0, height - shift); y < height; y++)
            {
                localMin.add(Double.POSITIVE_INFINITY);
                array.setValue(x, y, localMin.getMax());
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
    public BinaryArray2D binaryMask()
    {
        BinaryArray2D mask = BinaryArray2D.create(1, this.size);
        mask.fill(true);
        return mask;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sci.image.morphology.Strel#getOffset()
     */
    @Override
    public int[] maskOffset()
    {
        return new int[] { 0, this.offset };
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sci.image.morphology.Strel#getShifts()
     */
    @Override
    public int[][] shifts()
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
    public int[] size()
    {
        return new int[] { 1, this.size };
    }

    /**
     * Returns a linear vertical line with same size and offset equal to
     * size-offset-1.
     * 
     * @see net.sci.image.morphology.strel.Strel2D#reverse()
     */
    @Override
    public LinearVerticalStrel reverse()
    {
        return new LinearVerticalStrel(this.size, this.size - this.offset - 1);
    }
}
