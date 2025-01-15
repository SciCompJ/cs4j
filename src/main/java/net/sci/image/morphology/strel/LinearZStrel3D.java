/**
 * 
 */
package net.sci.image.morphology.strel;

import net.sci.array.binary.BinaryArray3D;
import net.sci.array.numeric.ScalarArray3D;

/**
 * @author dlegland
 *
 */
public class LinearZStrel3D extends AbstractStrel3D implements InPlaceStrel3D
{
    // ==================================================
    // Static methods

    public final static LinearZStrel3D fromDiameter(int diam)
    {
        return new LinearZStrel3D(diam);
    }

    public final static LinearZStrel3D fromRadius(int radius)
    {
        return new LinearZStrel3D(2 * radius + 1, radius);
    }
    

    // ==================================================
    // Class variables

    /**
     * Number of element in this structuring element. Corresponds to the length
     * in the Z direction.
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
     * Creates a new linear structuring element in the Z direction with the
     * given size.
     * 
     * @param size
     *            the number of pixels in this structuring element
     */

    public LinearZStrel3D(int size)
    {
        if (size < 1)
        {
            throw new RuntimeException("Requires a positive size");
        }
        this.size = size;

        this.offset = (int) Math.floor((this.size - 1) / 2);
    }

    /**
     * Creates a new linear structuring element in the Z direction with the
     * given size and offset.
     * 
     * @param size
     *            the number of pixels in this structuring element
     * @param offset
     *            the position of the reference pixel (between 0 and size-1)
     */
    public LinearZStrel3D(int size, int offset)
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
    // Methods implementing InPlaceStrel3D

    @Override
    public void inPlaceDilation(ScalarArray3D<?> array)
    {
        // get image dimensions
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        int sizeZ = array.size(2);

        // shifts between reference position and last position
        int shift = this.size - this.offset - 1;

        // create local histogram instance
        LocalExtremumBufferDouble localMax = new LocalExtremumBufferDouble(this.size,
                LocalExtremum.Type.MAXIMUM);

        // Iterate on image z-columns
        for (int y = 0; y < sizeY; y++)
        {
            fireProgressChanged(this, y, sizeY);
            for (int x = 0; x < sizeX; x++)
            {
                // init local histogram with background values
                localMax.fill(Double.NEGATIVE_INFINITY);

                // add neighbor values
                for (int z = 0; z < Math.min(shift, sizeZ); z++)
                {
                    localMax.add(array.getValue(x, y, z));
                }

                // iterate along "middle" values
                for (int z = 0; z < sizeZ - shift; z++)
                {
                    localMax.add(array.getValue(x, y, z + shift));
                    array.setValue(x, y, z, localMax.getMax());
                }

                // process pixels at the end of the line
                for (int z = Math.max(0, sizeZ - shift); z < sizeZ; z++)
                {
                    localMax.add(Double.NEGATIVE_INFINITY);
                    array.setValue(x, y, z, localMax.getMax());
                }
            }
        }

        // clear the progress bar
        fireProgressChanged(this, sizeY, sizeY);
    }

    @Override
    public void inPlaceErosion(ScalarArray3D<?> array)
    {
        // get image dimensions
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        int sizeZ = array.size(2);

        // shifts between reference position and last position
        int shift = this.size - this.offset - 1;

        // create local histogram instance
        LocalExtremumBufferDouble localMin = new LocalExtremumBufferDouble(this.size,
                LocalExtremum.Type.MINIMUM);

        // Iterate on image z-columns
        for (int y = 0; y < sizeY; y++)
        {
            fireProgressChanged(this, y, sizeY);
            for (int x = 0; x < sizeX; x++)
            {
                // init local histogram with background values
                localMin.fill(Double.POSITIVE_INFINITY);

                // add neighbor values
                for (int z = 0; z < Math.min(shift, sizeZ); z++)
                {
                    localMin.add(array.getValue(x, y, z));
                }

                // iterate along "middle" values
                for (int z = 0; z < sizeZ - shift; z++)
                {
                    localMin.add(array.getValue(x, y, z + shift));
                    array.setValue(x, y, z, localMin.getMax());
                }

                // process pixels at the end of the line
                for (int z = Math.max(0, sizeZ - shift); z < sizeZ; z++)
                {
                    localMin.add(Double.POSITIVE_INFINITY);
                    array.setValue(x, y, z, localMin.getMax());
                }
            }
        }

        // clear the progress bar
        fireProgressChanged(this, sizeY, sizeY);
    }
    

    // ==================================================
    // Methods implementing Strel3D

    /*
     * (non-Javadoc)
     * 
     * @see net.sci.image.morphology.Strel3D#getSize()
     */
    @Override
    public int[] size()
    {
        return new int[] { 1, 1, this.size };
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sci.image.morphology.Strel3D#getMask3D()
     */
    @Override
    public BinaryArray3D binaryMask()
    {
        BinaryArray3D mask = BinaryArray3D.create(1, 1, this.size);
        mask.fill(true);
        return mask;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sci.image.morphology.Strel3D#getOffset()
     */
    @Override
    public int[] maskOffset()
    {
        return new int[] { 0, 0, this.offset };
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sci.image.morphology.Strel3D#getShifts3D()
     */
    @Override
    public int[][] shifts()
    {
        int[][] shifts = new int[this.size][3];
        for (int i = 0; i < this.size; i++)
        {
            shifts[i][0] = 0;
            shifts[i][1] = 0;
            shifts[i][2] = i - this.offset;
        }
        return shifts;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sci.image.morphology.Strel3D#reverse()
     */
    @Override
    public LinearZStrel3D reverse()
    {
        return new LinearZStrel3D(this.size, this.size - this.offset - 1);
    }

}
