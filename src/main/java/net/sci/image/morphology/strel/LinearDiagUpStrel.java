/**
 * 
 */
package net.sci.image.morphology.strel;

import net.sci.array.binary.BinaryArray2D;
import net.sci.array.numeric.ScalarArray2D;
import net.sci.array.numeric.UInt8;
import net.sci.array.numeric.UInt8Array2D;

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
public class LinearDiagUpStrel extends AbstractStrel2D implements InPlaceStrel2D
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
     * @see net.sci.image.morphology.InPlaceStrel#inPlaceDilation(ij.process.
     * ScalarArray2D<?>)
     */
    @Override
    public void inPlaceDilation(ScalarArray2D<?> array)
    {
        // If size is one, there is no need to compute
        if (size <= 1)
        {
            return;
        }

        if (array instanceof UInt8Array2D)
            inPlaceDilationGray8((UInt8Array2D) array);
        else
            inPlaceDilationFloat(array);
    }

    private void inPlaceDilationGray8(UInt8Array2D array)
    {
        // get image size
        int sizeX = array.size(0);
        int sizeY = array.size(1);

        // Consider all diagonal lines with direction vector (+1,-1) that
        // intersect image.
        // Diagonal lines are identified by their intersection "d" with axis
        // (+1,+1)
        // Need to identify bounds for d
        int dmin = 0;
        int dmax = sizeX + sizeY - 1;

        // create local histogram instance
        LocalExtremumBufferInt localMax = new LocalExtremumBufferInt(size,
                LocalExtremum.Type.MAXIMUM);

        // Iterate on diagonal lines
        for (int d = dmin; d < dmax; d++)
        {
            fireProgressChanged(this, d - dmin, dmax - dmin);

            // reset local histogram
            localMax.fill(UInt8.MIN_INT);

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
                localMax.add(array.getInt(t, d - t));
                t++;
            }

            // process position that do not touch lower-left image boundary
            while (t < tmax)
            {
                localMax.add(array.getInt(t, d - t));
                int t2 = t - this.offset;
                array.setInt(t2, d - t2, localMax.getMax());
                t++;
            }

            // process pixels at the end of the line
            // and that do not touch the upper left image boundary
            while (t < tmax + this.offset)
            {
                localMax.add(UInt8.MIN_INT);
                int t2 = t - this.offset;
                int x = t2;
                int y = d - t2;
                if (x >= 0 && y >= 0 && x < sizeX && y < sizeY)
                    array.setInt(x, y, localMax.getMax());
                t++;
            }
        }
    }

    private void inPlaceDilationFloat(ScalarArray2D<?> array)
    {
        // get image size
        int sizeX = array.size(0);
        int sizeY = array.size(1);

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
                localMax.add(array.getValue(t, d - t));
                t++;
            }

            // process position that do not touch lower-left image boundary
            while (t < tmax)
            {
                localMax.add(array.getValue(t, d - t));
                int t2 = t - this.offset;
                array.setValue(t2, d - t2, localMax.getMax());
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
                    array.setValue(x, y, localMax.getMax());
                t++;
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sci.image.morphology.InPlaceStrel#inPlaceErosion(ij.process.
     * ScalarArray2D<?>)
     */
    @Override
    public void inPlaceErosion(ScalarArray2D<?> array)
    {
        // If size is one, there is no need to compute
        if (size <= 1)
        {
            return;
        }

        if (array instanceof UInt8Array2D)
            inPlaceErosionGray8((UInt8Array2D) array);
        else
            inPlaceErosionFloat(array);
    }

    private void inPlaceErosionGray8(UInt8Array2D array)
    {
        // get image size
        int sizeX = array.size(0);
        int sizeY = array.size(1);

        // Consider all diagonal lines with direction vector (+1,-1) that
        // intersect image.
        // Diagonal lines are identified by their intersection "d" with axis
        // (+1,+1)
        // Need to identify bounds for d
        int dmin = 0;
        int dmax = sizeX + sizeY - 1;

        // create local histogram instance
        LocalExtremumBufferInt localMin = new LocalExtremumBufferInt(size,
                LocalExtremum.Type.MINIMUM);

        // Iterate on diagonal lines
        for (int d = dmin; d < dmax; d++)
        {
            fireProgressChanged(this, d - dmin, dmax - dmin);

            // reset local histogram
            localMin.fill(UInt8.MAX_INT);

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
                localMin.add(array.getInt(t, d - t));
                t++;
            }

            // process position that do not touch lower-left image boundary
            while (t < tmax)
            {
                localMin.add(array.getInt(t, d - t));
                int t2 = t - this.offset;
                array.setInt(t2, d - t2, localMin.getMax());
                t++;
            }

            // process pixels at the end of the line
            // and that do not touch the upper left image boundary
            while (t < tmax + this.offset)
            {
                localMin.add(UInt8.MAX_INT);
                int t2 = t - this.offset;
                int x = t2;
                int y = d - t2;
                if (x >= 0 && y >= 0 && x < sizeX && y < sizeY)
                    array.setInt(x, y, localMin.getMax());
                t++;
            }
        }
    }

    private void inPlaceErosionFloat(ScalarArray2D<?> array)
    {
        // get image size
        int sizeX = array.size(0);
        int sizeY = array.size(1);

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
            localMin.fill(Double.POSITIVE_INFINITY);

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
                localMin.add(array.getValue(t, d - t));
                t++;
            }

            // process position that do not touch lower-left image boundary
            while (t < tmax)
            {
                localMin.add(array.getValue(t, d - t));
                int t2 = t - this.offset;
                array.setValue(t2, d - t2, localMin.getMax());
                t++;
            }

            // process pixels at the end of the line
            // and that do not touch the upper left image boundary
            while (t < tmax + this.offset)
            {
                localMin.add(Double.POSITIVE_INFINITY);
                int t2 = t - this.offset;
                int x = t2;
                int y = d - t2;
                if (x >= 0 && y >= 0 && x < sizeX && y < sizeY)
                    array.setValue(x, y, localMin.getMax());
                t++;
            }
        }
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
            mask.setBoolean(i, this.size - 1 - i, true);
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
            shifts[i][0] = this.size - 1 - i + this.offset;
            shifts[i][1] = i - this.offset;
        }
        return shifts;
    }

    /*
     * (non-Javadoc)
     * 
     * @see Strel2D#getSize()
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
    public LinearDiagUpStrel reverse()
    {
        return new LinearDiagUpStrel(this.size, this.size - this.offset - 1);
    }

}
