/**
 * 
 */
package net.sci.image.morphology.strel;

import java.util.ArrayList;
import java.util.Collection;

import net.sci.array.binary.Binary;
import net.sci.array.binary.BinaryArray2D;

/**
 * Structuring element representing a diamond of a given diameter.
 * 
 * The diamond is decomposed into linear diagonal and 3x3 cross structuring
 * elements.
 *
 * @see LinearDiagDownStrel
 * @see LinearDiagUpStrel
 * @see ShiftedCross3x3Strel
 * @author David Legland
 */
public class DiamondStrel extends AbstractSeparableStrel2D
{
    // ==================================================
    // Static methods
    
    public final static DiamondStrel fromDiameter(int diam)
    {
        return new DiamondStrel(diam);
    }
    
    public final static DiamondStrel fromRadius(int radius)
    {
        return new DiamondStrel(2 * radius + 1, radius);
    }
    
    
    // ==================================================
    // Class variables
    
    /**
     * The size of the diamond, given as orthogonal diameter.
     */
    int size;
    
    /**
     * The offset of the diamond, which is the same in all directions.
     */
    int offset;
    
    
    // ==================================================
    // Constructors
    
    /**
     * Creates a new diamond structuring element of given diameter. Diameter
     * must be odd.
     * 
     * @param size
     *            the diameter of the diamond, that must be odd
     * @throws IllegalArgumentException
     *             if size is negative or zero
     * @throws IllegalArgumentException
     *             if size is even
     */
    public DiamondStrel(int size)
    {
        this(size, (size - 1) / 2);
    }
    
    /**
     * Creates a new diamond structuring element with a given diameter and
     * offset. Diameter must be odd and positive. Offset must be comprised
     * between 0 and size-1.
     * 
     * @param size
     *            the diameter of the diamond, that must be odd
     * @param offset
     *            the position of the reference point
     * @throws IllegalArgumentException
     *             if size is negative or zero
     * @throws IllegalArgumentException
     *             if size is even
     * @throws IllegalArgumentException
     *             if offset is negative or greater than size
     */
    public DiamondStrel(int size, int offset)
    {
        if (size < 1)
        {
            throw new IllegalArgumentException("Requires a positive size");
        }
        if (size % 2 != 1)
        {
            throw new IllegalArgumentException("Diamond size must be odd");
        }
        this.size = size;
        
        if (offset < 0)
        {
            throw new IllegalArgumentException("Requires a non-negative offset");
        }
        if (offset >= size)
        {
            throw new IllegalArgumentException("Offset can not be greater than size");
        }
        this.offset = offset;
    }
    
    
    // ==================================================
    // General methods
    
    /*
     * (non-Javadoc)
     * 
     * @see net.sci.image.morphology.Strel#getSize()
     */
    @Override
    public int[] size()
    {
        return new int[] { this.size, this.size };
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
        
        // Fill everything with 255
        mask.fill(Binary.TRUE);
        
        // Put zeros at the corners
        int radius = (this.size - 1) / 2;
        for (int i = 0; i < radius; i++)
        {
            for (int j = 0; j < radius - i; j++)
            {
                mask.setBoolean(i, j, false);
                mask.setBoolean(i, this.size - 1 - j, false);
                mask.setBoolean(this.size - 1 - i, j, false);
                mask.setBoolean(this.size - 1 - i, this.size - 1 - j, false);
            }
        }
        
        // return the mask
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
        // Put zeros at the corners
        int radius = (this.size - 1) / 2;
        
        // allocate memory for shifts
        int nShifts = radius * (radius - 1) / 2 + 2 * this.size - 1;
        int[][] shifts = new int[nShifts][2];
        
        // Compute the shifts in each row of the mask
        int iShift = 0;
        for (int i = 0; i < this.size; i++)
        {
            int i2 = Math.min(i, this.size - 1 - i);
            int j1 = radius - i2;
            int j2 = radius + i2;
            
            for (int j = j1; j <= j2; j++)
            {
                shifts[iShift][0] = j - this.offset;
                shifts[iShift][1] = i - this.offset;
            }
            iShift++;
        }
        
        // return vector array
        return shifts;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see net.sci.image.morphology.SeparableStrel#reverse()
     */
    @Override
    public SeparableStrel2D reverse()
    {
        return new DiamondStrel(this.size, this.size - 1 - this.offset);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see net.sci.image.morphology.SeparableStrel#decompose()
     */
    @Override
    public Collection<InPlaceStrel2D> decompose()
    {
        // allocate memory
        ArrayList<InPlaceStrel2D> strels = new ArrayList<InPlaceStrel2D>(3);
        
        // add each elementary strel
        int linSize = (this.size - 1) / 2;
        strels.add(ShiftedCross3x3Strel.RIGHT);
        strels.add(new LinearDiagUpStrel(linSize));
        strels.add(new LinearDiagDownStrel(linSize));
        
        return strels;
    }
    
}
