/**
 * 
 */
package net.sci.image.morphology.strel;

import net.sci.algo.AlgoStub;
import net.sci.array.binary.BinaryArray2D;
import net.sci.array.numeric.IntArray2D;
import net.sci.array.numeric.ScalarArray2D;

/**
 * A diagonal linear structuring element of a given length, with direction
 * vector (+1,+1) in image coordinate system. Provides methods for fast in place
 * erosion and dilation.
 * 
 * @see LinearHorizontalStrel
 * @see LinearVerticalStrel
 * @see LinearDiagUpStrel
 * 
 * @author David Legland
 *
 */
public class LinearDiagDownStrel extends AlgoStub implements InPlaceStrel2D
{
    // ==================================================
    // Static methods

    public final static LinearDiagDownStrel fromDiameter(int diam)
    {
        return new LinearDiagDownStrel(diam);
    }

    public final static LinearDiagDownStrel fromRadius(int radius)
    {
        return new LinearDiagDownStrel(2 * radius + 1, radius);
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
    public LinearDiagDownStrel(int size)
    {
        if (size < 1)
        {
            throw new RuntimeException("Requires a positive size");
        }
        this.size = size;

        this.offset = (int) Math.floor((this.size - 1) / 2);
    }

    /**
     * Creates a new diagonal linear structuring element of a given size and
     * with a given offset.
     * 
     * @param size
     *            the number of pixels in this structuring element
     * @param offset
     *            the position of the reference pixel (between 0 and size-1)
     */
    public LinearDiagDownStrel(int size, int offset)
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
    // Implementation of InPlaceStrel interface

    /*
     * (non-Javadoc)
     * 
     * @see
     * net.sci.image.morphology.InPlaceStrel#inPlaceDilation(ij.process.Array2D<
     * ?>)
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
        // get image size
        int sizeX = array.size(0);
        int sizeY = array.size(1);

        // retrieve minimum value allowed within array
        final int defaultValue = array.typeMin().getInt();
        
        // create local histogram instance
        LocalExtremumBufferInt localMax = new LocalExtremumBufferInt(size,
                LocalExtremum.Type.MAXIMUM);

        // Consider all diagonal lines with direction vector (+1,+1) that
        // intersect image.
        // Diagonal lines are identified by their intersection "d" with axis
        // (-1,+1)
        // Need to identify bounds for d
        final int dmin = -(sizeX - 1);
        final int dmax = sizeY - 1;

        // Iterate on diagonal lines
        for (int d = dmin; d < dmax; d++)
        {
            fireProgressChanged(this, d - dmin, dmax - dmin);

            // reset local histogram
            localMax.fill(defaultValue);

            int xmin = Math.max(0, -d);
            int xmax = Math.min(sizeX, sizeY - d);
            int ymin = Math.max(0, d);
            int ymax = Math.min(sizeY, d - sizeX);

            int tmin = Math.max(xmin, d - ymin);
            int tmax = Math.min(xmax, d - ymax);

            // position on the line
            int t = tmin;

            // init local histogram image values after current pos
            while (t < Math.min(tmin + this.offset, tmax))
            {
                localMax.add(array.getInt(t, t + d));
                t++;
            }

            // process position that do not touch lower-right image boundary
            while (t < tmax)
            {
                localMax.add(array.getInt(t, t + d));
                int t2 = t - this.offset;
                array.setInt(t2, t2 + d, localMax.getMax());
                t++;
            }

            // process pixels at the end of the line
            // and that do not touch the upper left image boundary
            while (t < tmax + this.offset)
            {
                localMax.add(defaultValue);
                int x = t - this.offset;
                int y = t + d - this.offset;
                if (x >= 0 && y >= 0 && x < sizeX && y < sizeY)
                    array.setInt(x, y, localMax.getMax());
                t++;
            }
        }

        // clear the progress bar
        fireProgressChanged(this, dmax - dmin, dmax - dmin);
    }

    private void inPlaceDilationFloat(ScalarArray2D<?> array)
    {
        // get image size
        int sizeX = array.size(0);
        int sizeY = array.size(1);

        // Consider all diagonal lines with direction vector (+1,+1) that
        // intersect image.
        // Diagonal lines are identified by their intersection "d" with axis
        // (-1,+1)
        // Need to identify bounds for d
        int dmin = -(sizeX - 1);
        int dmax = sizeY - 1;

        // create local histogram instance
        LocalExtremumBufferDouble localMax = new LocalExtremumBufferDouble(size,
                LocalExtremum.Type.MAXIMUM);

        // Iterate on diagonal lines
        for (int d = dmin; d < dmax; d++)
        {
            fireProgressChanged(this, d - dmin, dmax - dmin);

            // reset local histogram
            localMax.fill(Double.NEGATIVE_INFINITY);

            int xmin = Math.max(0, -d);
            int xmax = Math.min(sizeX, sizeY - d);
            int ymin = Math.max(0, d);
            int ymax = Math.min(sizeY, d - sizeX);

            int tmin = Math.max(xmin, d - ymin);
            int tmax = Math.min(xmax, d - ymax);

            // position on the line
            int t = tmin;

            // init local histogram image values after current pos
            while (t < Math.min(tmin + this.offset, tmax))
            {
                localMax.add(array.getValue(t, t + d));
                t++;
            }

            // process position that do not touch lower-right image boundary
            while (t < tmax)
            {
                localMax.add(array.getValue(t, t + d));
                int t2 = t - this.offset;
                array.setValue(t2, t2 + d, localMax.getMax());
                t++;
            }

            // process pixels at the end of the line
            // and that do not touch the upper left image boundary
            while (t < tmax + this.offset)
            {
                localMax.add(Double.NEGATIVE_INFINITY);
                int x = t - this.offset;
                int y = t + d - this.offset;
                if (x >= 0 && y >= 0 && x < sizeX && y < sizeY)
                    array.setValue(x, y, localMax.getMax());
                t++;
            }
        }

        // clear the progress bar
        fireProgressChanged(this, dmax - dmin, dmax - dmin);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * net.sci.image.morphology.InPlaceStrel#inPlaceErosion(ij.process.Array2D<?
     * >)
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
        final int defaultValue = array.typeMax().getInt(); 

        // create local histogram instance
        LocalExtremumBufferInt localMin = new LocalExtremumBufferInt(size,
                LocalExtremum.Type.MINIMUM);

        // get image size
        final int sizeX = array.size(0);
        final int sizeY = array.size(1);
        
        // Consider all diagonal lines with direction vector (+1,+1) that
        // intersect image.
        // Diagonal lines are identified by their intersection "d" with axis
        // (-1,+1)
        // Need to identify bounds for d
        final int dmin = -(sizeX - 1);
        final int dmax = sizeY - 1;

        // compute shifts
        final int dt0 = this.offset;

        // Iterate on diagonal lines
        for (int d = dmin; d < dmax; d++)
        {
            fireProgressChanged(this, d - dmin, dmax - dmin);

            // reset local histogram
            localMin.fill(defaultValue);

            int xmin = Math.max(0, -d);
            int xmax = Math.min(sizeX, sizeY - d);
            int ymin = Math.max(0, d);
            int ymax = Math.min(sizeY, d - sizeX);

            int tmin = Math.max(xmin, d - ymin);
            int tmax = Math.min(xmax, d - ymax);

            // position on the line
            int t = tmin;

            // init local histogram image values after current pos
            while (t < Math.min(tmin + dt0, tmax))
            {
                localMin.add(array.getInt(t, t + d));
                t++;
            }

            // process position that do not touch lower-right image boundary
            while (t < tmax)
            {
                localMin.add(array.getInt(t, t + d));
                array.setInt(t - dt0, t + d - dt0, localMin.getMax());
                t++;
            }

            // process pixels at the end of the line
            // and that do not touch the upper left image boundary
            while (t < tmax + dt0)
            {
                localMin.add(defaultValue);
                int x = t - dt0;
                int y = t + d - dt0;
                if (x >= 0 && y >= 0 && x < sizeX && y < sizeY)
                {
                    array.setInt(x, y, localMin.getMax());
                }
                t++;
            }
        }

        // clear the progress bar
        fireProgressChanged(this, dmax - dmin, dmax - dmin);
    }

    private void inPlaceErosionFloat(ScalarArray2D<?> array)
    {
        // get image size
        int sizeX = array.size(0);
        int sizeY = array.size(1);

        // Consider all diagonal lines with direction vector (+1,+1) that
        // intersect image.
        // Diagonal lines are identified by their intersection "d" with axis
        // (-1,+1)
        // Need to identify bounds for d
        int dmin = -(sizeX - 1);
        int dmax = sizeY - 1;

        // compute shifts
        int dt0 = this.offset;

        // create local histogram instance
        LocalExtremumBufferDouble localMin = new LocalExtremumBufferDouble(size,
                LocalExtremum.Type.MINIMUM);

        // Iterate on diagonal lines
        for (int d = dmin; d < dmax; d++)
        {
            fireProgressChanged(this, d - dmin, dmax - dmin);

            // reset local histogram
            localMin.fill(Double.POSITIVE_INFINITY);

            int xmin = Math.max(0, -d);
            int xmax = Math.min(sizeX, sizeY - d);
            int ymin = Math.max(0, d);
            int ymax = Math.min(sizeY, d - sizeX);

            int tmin = Math.max(xmin, d - ymin);
            int tmax = Math.min(xmax, d - ymax);

            // position on the line
            int t = tmin;

            // init local histogram image values after current pos
            while (t < Math.min(tmin + dt0, tmax))
            {
                localMin.add(array.getValue(t, t + d));
                t++;
            }

            // process position that do not touch lower-right image boundary
            while (t < tmax)
            {
                localMin.add(array.getValue(t, t + d));
                array.setValue(t - dt0, t + d - dt0, localMin.getMax());
                t++;
            }

            // process pixels at the end of the line
            // and that do not touch the upper left image boundary
            while (t < tmax + dt0)
            {
                localMin.add(Double.POSITIVE_INFINITY);
                int x = t - dt0;
                int y = t + d - dt0;
                if (x >= 0 && y >= 0 && x < sizeX && y < sizeY)
                    array.setValue(x, y, localMin.getMax());
                t++;
            }
        }

        // clear the progress bar
        fireProgressChanged(this, dmax - dmin, dmax - dmin);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sci.image.morphology.Strel#getMask()
     */
    @Override
    public BinaryArray2D binaryMask()
    {
        BinaryArray2D mask = BinaryArray2D.create(this.size, this.size);
        for (int i = 0; i < this.size; i++)
        {
            mask.setBoolean(i, i, true);
        }

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
        return new int[] { this.offset, this.offset };
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
            shifts[i][0] = i - this.offset;
            shifts[i][1] = i - this.offset;
        }
        return shifts;
    }

    /*
     * (non-Javadoc)
     * 
     * @see Strel#getSize()
     */
    @Override
    public int[] size()
    {
        return new int[] { this.size, this.size };
    }

    /**
     * Returns a linear diagonal line with same size and offset equal to
     * size-offset.
     * 
     * @see Strel2D#reverse()
     */
    @Override
    public LinearDiagDownStrel reverse()
    {
        return new LinearDiagDownStrel(this.size, this.size - this.offset - 1);
    }

}
