/**
 * 
 */
package net.sci.image.morphology.strel;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A cubic structuring element, obtained by decomposition into linear
 * structuring elements with the same size along each dimension.
 * 
 * @author dlegland
 *
 */
public class CubeStrel3D extends AbstractSeparableStrel3D
{
    // ==================================================
    // Static methods 
    
    public final static CubeStrel3D fromDiameter(int diam) 
    {
        return new CubeStrel3D(diam);
    }
    
    public final static CubeStrel3D fromRadius(int radius)
    {
        return new CubeStrel3D(2 * radius + 1, radius);
    }
    
    // ==================================================
    // Class variables
    
    /**
     * The size of each side of the cube. 
     */
    int size;
    
    /**
     * The offset of the cube, which is the same in all directions. 
     */
    int offset;

    
    // ==================================================
    // Constructors 
    
    /**
     * Creates a new cubic structuring element of a given size.
     * @param size the length of each side of the cube
     */
    public CubeStrel3D(int size)
    {
        if (size < 1)
        {
            throw new RuntimeException("Requires a positive size");
        }
        this.size = size;

        this.offset = (int) Math.floor((this.size - 1) / 2);
    }
    
    /**
     * Creates a new cubic structuring element of a given size and with a given
     * offset.
     * 
     * @param size
     *            the length of each side of the cube
     * @param offset
     *            the position of the reference pixel in each direction
     */
    public CubeStrel3D(int size, int offset)
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
    
    
    // ===================================================================
    // Implementation of SeparableStrel3D interface
    
    /**
     * Returns a collection of three linear-shape structuring element, along
     * each principal direction.
     * 
     * @return a collection of three in place structuring elements
     * 
     * @see net.sci.image.morphology.strel.SeparableStrel3D#decompose()
     */
    @Override
    public Collection<InPlaceStrel3D> decompose()
    {
        ArrayList<InPlaceStrel3D> strels = new ArrayList<InPlaceStrel3D>(3);
        strels.add(InPlaceStrel3D.wrap(new LinearHorizontalStrel(this.size, this.offset)));
        strels.add(InPlaceStrel3D.wrap(new LinearVerticalStrel(this.size, this.offset)));
        strels.add(new LinearZStrel3D(this.size, this.offset));
        return strels;
    }
    
    
    // ===================================================================
    // Implementation of Strel3D interface
    
    /* (non-Javadoc)
     * @see net.sci.image.morphology.strel.SeparableStrel3D#reverse()
     */
    @Override
    public SeparableStrel3D reverse()
    {
        return new CubeStrel3D(this.size, this.size - this.offset - 1);
    }
    
    /* (non-Javadoc)
     * @see net.sci.image.morphology.Strel3D#getSize()
     */
    @Override
    public int[] getSize()
    {
        return new int[]{this.size, this.size, this.size};
    }
    
    /* (non-Javadoc)
     * @see net.sci.image.morphology.Strel3D#getMask3D()
     */
    @Override
    public int[][][] getMask3D()
    {
        int[][][] mask = new int[this.size][this.size][this.size];
        for (int z = 0; z < this.size; z++)
        {
            for (int y = 0; y < this.size; y++)
            {
                for (int x = 0; x < this.size; x++)
                {
                    mask[z][y][x] = 255;
                }
            }
        }
        return mask;
    }
    
    /* (non-Javadoc)
     * @see net.sci.image.morphology.Strel3D#getOffset()
     */
    @Override
    public int[] getOffset()
    {
        return new int[]{this.offset, this.offset, this.offset};
    }
    
    /* (non-Javadoc)
     * @see net.sci.image.morphology.Strel3D#getShifts3D()
     */
    @Override
    public int[][] getShifts3D()
    {
        int n = this.size * this.size * this.size;
        int[][] shifts = new int[n][3];
        int i = 0;

        for (int z = 0; z < this.size; z++)
        {
            for (int y = 0; y < this.size; y++)
            {
                for (int x = 0; x < this.size; x++)
                {
                    shifts[i][0] = x - this.offset;
                    shifts[i][1] = y - this.offset;
                    shifts[i][2] = z - this.offset;
                    i++;
                }
            }
        }
        return shifts;
    } 
}
