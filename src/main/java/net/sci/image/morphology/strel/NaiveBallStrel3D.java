/**
 * 
 */
package net.sci.image.morphology.strel;

import net.sci.algo.AlgoStub;
import net.sci.array.scalar.ScalarArray3D;

/**
 * @author dlegland
 *
 */
public class NaiveBallStrel3D extends AlgoStub implements Strel3D
{
    // ==================================================
    // Class variables

    /**
     * The radius of the structuring element, in pixels.</p>
     * 
     * A radius of 1 corresponds to a full 3-by-3 square.
     */
    double radius = 1;

    /**
     * The number of pixels around the central pixel.
     */
    int intRadius;
    
    /**
     * An array of shifts referring to strel elements, relative to center pixel.
     */
    int[][] shiftArray;
    
    // ==================================================
    // Constructors

    /**
     * Create a new Disk Strel from its radius.
     * 
     * @param radius
     *            the radius of the disk structuring element, in pixels.
     */
    public NaiveBallStrel3D(double radius)
    {
        this.radius = radius;
        this.intRadius = (int) Math.floor(this.radius + 0.5);
        
        createShiftArray();
    }
    
    private void createShiftArray()
    {
        // convert to "real" radius by taking into account central pixel
        double r2 = this.radius + 0.5;
        
        // size of structuring element
        int diam = 2 * this.intRadius + 1;

        // count the number of strel elements
        int count = 0;
        for (int z = 0; z < diam; z++)
        {
            int z2 = z - this.intRadius; 
            for (int y = 0; y < diam; y++)
            {
                int y2 = y - this.intRadius;
                double tmp = Math.hypot(y2, z2);
                for (int x = 0; x < diam; x++)
                {
                    int x2 = x - this.intRadius; 
                    if (Math.hypot(x2, tmp) <= r2)
                    {
                        count++;
                    }
                }
            }
        }
        
        // create the shift array
        this.shiftArray = new int[count][];
        count = 0;
        for (int z = 0; z < diam; z++)
        {
            int z2 = z - this.intRadius; 
            for (int y = 0; y < diam; y++)
            {
                int y2 = y - this.intRadius;
                double tmp = Math.hypot(y2, z2);
                for (int x = 0; x < diam; x++)
                {
                    int x2 = x - this.intRadius; 
                    if (Math.hypot(x2, tmp) <= r2)
                    {
                        this.shiftArray[count++] = new int[] {x2, y2, z2};
                    }
                }
            }
        }
    } 

    
    // ==================================================
    // Implementation of the Strel3D interface

    /* (non-Javadoc)
     * @see net.sci.image.morphology.Strel3D#dilation(net.sci.array.scalar.ScalarArray3D)
     */
    @Override
    public ScalarArray3D<?> dilation(ScalarArray3D<?> array)
    {
        // size of array
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        int sizeZ = array.size(2);
        ScalarArray3D<?> res = array.duplicate();
        
        // iterate over the pixels of the array
        for (int z = 0; z < sizeZ; z++)
        {
            for (int y = 0; y < sizeY; y++)
            {
                for (int x = 0; x < sizeX; x++)
                {
                    double value = Double.NEGATIVE_INFINITY;
                    
                    // iterate over neighbors
                    for (int[] shift : this.shiftArray)
                    {
                        int x2 = x + shift[0];
                        int y2 = y + shift[1];
                        int z2 = z + shift[2];
                        if (x2 < 0 || x2 >= sizeX) continue;
                        if (y2 < 0 || y2 >= sizeY) continue;
                        if (z2 < 0 || z2 >= sizeZ) continue;
                        
                        value = Math.max(value, array.getValue(x2, y2, z2));
                    }
                    
                    res.setValue(value, x, y, z);
                }
            }
        }
        return res;
    }

    /* (non-Javadoc)
     * @see net.sci.image.morphology.Strel3D#erosion(net.sci.array.scalar.ScalarArray3D)
     */
    @Override
    public ScalarArray3D<?> erosion(ScalarArray3D<?> array)
    {
        // size of array
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        int sizeZ = array.size(2);
        ScalarArray3D<?> res = array.duplicate();
        
        // iterate over the pixels of the array
        for (int z = 0; z < sizeZ; z++)
        {
            for (int y = 0; y < sizeY; y++)
            {
                for (int x = 0; x < sizeX; x++)
                {
                    double value = Double.POSITIVE_INFINITY;
                    
                    // iterate over neighbors
                    for (int[] shift : this.shiftArray)
                    {
                        int x2 = x + shift[0];
                        int y2 = y + shift[1];
                        int z2 = z + shift[2];
                        if (x2 < 0 || x2 >= sizeX) continue;
                        if (y2 < 0 || y2 >= sizeY) continue;
                        if (z2 < 0 || z2 >= sizeZ) continue;
                        
                        value = Math.min(value, array.getValue(x2, y2, z2));
                    }
                    
                    res.setValue(value, x, y, z);
                }
            }
        }
        
        return res;
    }

    /* (non-Javadoc)
     * @see net.sci.image.morphology.Strel3D#closing(net.sci.array.scalar.ScalarArray3D)
     */
    @Override
    public ScalarArray3D<?> closing(ScalarArray3D<?> array)
    {
        return erosion(dilation(array));
    }

    /* (non-Javadoc)
     * @see net.sci.image.morphology.Strel3D#opening(net.sci.array.scalar.ScalarArray3D)
     */
    @Override
    public ScalarArray3D<?> opening(ScalarArray3D<?> array)
    {
        return dilation(erosion(array));
    }

    /* (non-Javadoc)
     * @see net.sci.image.morphology.Strel3D#reverse()
     */
    @Override
    public Strel3D reverse()
    {
        return this;
    }

    /* (non-Javadoc)
     * @see net.sci.image.morphology.Strel3D#getSize()
     */
    @Override
    public int[] size()
    {
        int diam = 2 * this.intRadius + 1;
        return new int[] {diam, diam, diam};
    }
    
    /* (non-Javadoc)
     * @see net.sci.image.morphology.Strel3D#getMask()
     */
    @Override
    public int[][][] getMask()
    {
        // convert to "real" radius by taking into account central pixel
        double r2 = this.radius + 0.5;
        
        // size of structuring element
        int diam = 2 * this.intRadius + 1;

        // fill the mask
        int[][][] mask = new int[diam][diam][diam];
        for (int z = 0; z < diam; z++)
        {
            for (int y = 0; y < diam; y++)
            {
                double tmp = Math.hypot(z - this.intRadius, y - this.intRadius);
                for (int x = 0; x < diam; x++)
                {
                    if (Math.hypot(x - this.intRadius, tmp) <= r2)
                    {
                        mask[z][y][x] = 255;
                    }
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
        return new int[] {this.intRadius, this.intRadius, this.intRadius};
    }
    
    /* (non-Javadoc)
     * @see net.sci.image.morphology.Strel3D#getShifts()
     */
    @Override
    public int[][] getShifts()
    {
        return this.shiftArray;
    }
}
