/**
 * 
 */
package net.sci.image.morphology.strel;

import net.sci.algo.AlgoStub;
import net.sci.array.scalar.ScalarArray2D;

/**
 * <pre>{@code
    // Creates a disk structuring element with radius 6
    Strel2D strel = new SlidingDiskStrel(6);
    
    // Creates a simple array with white dot in the middle
    UInt8Array2D array = UInt8Array2D.create(15, 15);
    array.setValue(7, 7, 255);
    
    // applies dilation on array
    ScalarArray2D<?> dilated = strel.dilation(array);
    
    // display result
    dilated.print(System.out);
 * }</pre>
 * @author dlegland
 *
 */
public class SlidingDiskStrel extends AlgoStub implements Strel2D
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
     * The number of pixels around the central pixel. Used to determine the size
     * of the structuring element.
     */
    int intRadius;
    
    /**
     * An array of shifts referring to strel elements, relative to center pixel.
     * Used for lazy evaluation of getShifts() method. 
     */
    int[][] shiftArray;
    
    
    int[] xOffsets;
    int[] yOffsets;
    
    // ==================================================
    // Constructors

    /**
     * Create a new Disk Strel from its radius.
     * 
     * @param radius
     *            the radius of the disk structuring element, in pixels.
     */
    public SlidingDiskStrel(double radius)
    {
        this.radius = radius;
        
        initStrel();
        createShiftArray();
    }
    
    private void initStrel()
    {
        this.intRadius = (int) Math.floor(this.radius + 0.5);
        
        // allocate arrays
        int nOffsets = 2 * this.intRadius + 1;
        this.xOffsets = new int[nOffsets];
        this.yOffsets = new int[nOffsets];
        
        // initialize each row
        double r2 = (this.radius + 0.5) * ((this.radius + 0.5));
        for (int i = 0; i < nOffsets; i++)
        {
            int dy = i - intRadius;
            this.yOffsets[i] = dy; 
            this.xOffsets[i] =  (int) Math.floor(Math.sqrt(r2 - dy * dy));
        }
    }
    

    // ==================================================
    // Specific methods
    
    /**
     * @return the number of non zero elements within this structuring element.
     */
    private int elementCount()
    {
        int count = 0;
        for (int i = 0; i < this.xOffsets.length; i++)
        {
            count += 2 * this.xOffsets[i] + 1;
        }
        return count;
    }
    
    
    // ==================================================
    // Implementation of the Strel2D interface

    /* (non-Javadoc)
     * @see net.sci.image.morphology.Strel2D#dilation(net.sci.array.scalar.ScalarArray2D)
     */
    @Override
    public ScalarArray2D<?> dilation(ScalarArray2D<?> array)
    {
        return slidingDilationScalar(array);
    }

    private ScalarArray2D<?> slidingDilationScalar(ScalarArray2D<?> array)
    {
        // get array size
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        
        // number of non zero elements 
        int count = elementCount();
        int nOffsets = this.xOffsets.length;
        
        // create local histogram instance
        final double OUTSIDE = Double.NEGATIVE_INFINITY;
        LocalHistogramDouble localHisto = new LocalHistogramDouble(count, OUTSIDE);

        // Allocate result
        ScalarArray2D<?> res = array.duplicate();

        // temp variables for updating local histogram
        double vOld, vNew;
        
        // Iterate on image rows
        for (int y = 0; y < sizeY; y++)
        {
            fireProgressChanged(this, y, sizeY);

            // init local histogram with background values
            localHisto.reset(count, OUTSIDE);

            // initialize histogram with values on the right of current position
            for (int x = -intRadius; x < 0; x++)
            {
                for (int i = 0; i < nOffsets; i++)
                {
                    int y2 = y + this.yOffsets[i];
                    if (y2 < 0 || y2 >= sizeY)
                    {
                        continue;
                    }

                    int x2 = x + this.xOffsets[i];
                    if (x2 < 0 || x2 >= sizeX)
                    {
                        continue;
                    }
                    localHisto.replace(OUTSIDE, array.getValue(x2, y2));
                }
            }   

            // iterate along "middle" values
            for (int x = 0; x < sizeX; x++)
            {
                for (int i = 0; i < nOffsets; i++)
                {
                    // current line offset
                    int y2 = y + this.yOffsets[i];
                    
                    // We need to test values only for lines within array bounds
                    if (y2 >= 0 && y2 < sizeY)
                    {
                        // old value
                        int x2 = x - this.xOffsets[i] - 1;
                        vOld = (x2 >= 0 && x2 < sizeX) ? array.getValue(x2, y2) : OUTSIDE;
                        
                        // new value
                        x2 = x + this.xOffsets[i];
                        vNew = (x2 >= 0 && x2 < sizeX) ? array.getValue(x2, y2) : OUTSIDE;
                        
                        localHisto.replace(vOld, vNew);
                    }
                }

                res.setValue(x, y, localHisto.getMaxValue());
            }
        }

        // clear the progress bar
        fireProgressChanged(this, sizeY, sizeY);
        
        return res;
    }
    
    /* (non-Javadoc)
     * @see net.sci.image.morphology.Strel2D#erosion(net.sci.array.scalar.ScalarArray2D)
     */
    @Override
    public ScalarArray2D<?> erosion(ScalarArray2D<?> array)
    {
        return slidingErosionScalar(array);
    }

    private ScalarArray2D<?> slidingErosionScalar(ScalarArray2D<?> array)
    {
        // get array size
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        
        final double OUTSIDE = Double.POSITIVE_INFINITY; 
        
        // number of non zero elements 
        int count = elementCount();
        int nOffsets = this.xOffsets.length;
        
        // create local histogram instance
        LocalHistogramDouble localHisto = new LocalHistogramDouble(count, OUTSIDE);

        // Allocate result
        ScalarArray2D<?> res = array.duplicate();

        // temp variables for updating local histogram
        double vOld, vNew;
        
        // Iterate on image rows
        for (int y = 0; y < sizeY; y++)
        {
            fireProgressChanged(this, y, sizeY);

            // init local histogram with background values
            localHisto.reset(count, OUTSIDE);

            // initialize histogram with values on the right of current position
            for (int x = -intRadius; x < 0; x++)
            {
                for (int i = 0; i < nOffsets; i++)
                {
                    int y2 = y + this.yOffsets[i];
                    if (y2 < 0 || y2 >= sizeY)
                    {
                        continue;
                    }

                    int x2 = x + this.xOffsets[i];
                    if (x2 < 0 || x2 >= sizeX)
                    {
                        continue;
                    }
                    
                    localHisto.replace(OUTSIDE, array.getValue(x2, y2));
                }
            }   

            // iterate along "middle" values
            for (int x = 0; x < sizeX; x++)
            {
                for (int i = 0; i < nOffsets; i++)
                {
                    // current line offset
                    int y2 = y + this.yOffsets[i];
                    
                    // We need to test values only for lines within array bounds
                    if (y2 >= 0 && y2 < sizeY)
                    {
                        // old value
                        int x2 = x - this.xOffsets[i] - 1;
                        vOld = (x2 >= 0 && x2 < sizeX) ? array.getValue(x2, y2) : OUTSIDE;
                        
                        // new value
                        x2 = x + this.xOffsets[i];
                        vNew = (x2 >= 0 && x2 < sizeX) ? array.getValue(x2, y2) : OUTSIDE;
                        
                        localHisto.replace(vOld, vNew);
                    }
                }

                res.setValue(x, y, localHisto.getMinValue());
            }
        }

        // clear the progress bar
        fireProgressChanged(this, sizeY, sizeY);
        
        return res;
    }
    
    /* (non-Javadoc)
     * @see net.sci.image.morphology.Strel2D#closing(net.sci.array.scalar.ScalarArray2D)
     */
    @Override
    public ScalarArray2D<?> closing(ScalarArray2D<?> array)
    {
        return dilation(erosion(array));
    }

    /* (non-Javadoc)
     * @see net.sci.image.morphology.Strel2D#opening(net.sci.array.scalar.ScalarArray2D)
     */
    @Override
    public ScalarArray2D<?> opening(ScalarArray2D<?> array)
    {
        return erosion(dilation(array));
    }

    /* (non-Javadoc)
     * @see net.sci.image.morphology.Strel2D#reverse()
     */
    @Override
    public Strel2D reverse()
    {
        return this;
    }

    /* (non-Javadoc)
     * @see net.sci.image.morphology.Strel2D#getSize()
     */
    @Override
    public int[] size()
    {
        int diam = 2 * this.intRadius + 1;
        return new int[] {diam, diam};
    }
    
    /* (non-Javadoc)
     * @see net.sci.image.morphology.Strel2D#getMask()
     */
    @Override
    public int[][] getMask()
    {
        // convert to "real" radius by taking into account central pixel
        double r2 = this.radius + 0.5;
        
        // size of structuring element
        int diam = 2 * this.intRadius + 1;

        // fill the mask
        int[][] mask = new int[diam][diam];
        for (int y = 0; y < diam; y++)
        {
            for (int x = 0; x < diam; x++)
            {
                if (Math.hypot(x - this.intRadius, y - this.intRadius) <= r2)
                {
                    mask[y][x] = 255;
                }
            }
        }
        return mask;
    }
    
    /* (non-Javadoc)
     * @see net.sci.image.morphology.Strel2D#getOffset()
     */
    @Override
    public int[] getOffset()
    {
        return new int[] {this.intRadius, this.intRadius};
    }
    
    /* (non-Javadoc)
     * @see net.sci.image.morphology.Strel2D#getShifts()
     */
    @Override
    public int[][] getShifts()
    {
        if (this.shiftArray == null)
        {
            createShiftArray();
        }
        return this.shiftArray;
    }

    private void createShiftArray()
    {
        int count = elementCount();
        
        // create the shift array
        this.shiftArray = new int[count][];
        count = 0;

        int nOffsets = this.xOffsets.length;
        for (int i = 0; i < nOffsets; i++)
        {
            int dy = this.yOffsets[i];
            int ri = this.xOffsets[i];
            for (int dx = -ri; dx <= ri; dx++)
            {
                this.shiftArray[count++] = new int[] {dx, dy};
            }
        }
    }
}
