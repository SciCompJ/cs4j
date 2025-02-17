/**
 * 
 */
package net.sci.image.morphology.strel;

import net.sci.algo.AlgoStub;
import net.sci.array.binary.BinaryArray2D;
import net.sci.array.numeric.ScalarArray2D;
import net.sci.array.numeric.UInt8;
import net.sci.array.numeric.UInt8Array2D;

/**
 * Structuring element representing a 3x3 cross, that considers the center
 * pixels together with the four orthogonal neighbors.
 * 
 * @author David Legland
 *
 */
public class Cross3x3Strel extends AlgoStub implements InPlaceStrel2D
{
    
    /*
     * (non-Javadoc)
     * 
     * @see net.sci.image.morphology.Strel#getSize()
     */
    @Override
    public int[] size()
    {
        return new int[] { 3, 3 };
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see net.sci.image.morphology.Strel#getMask()
     */
    @Override
    public BinaryArray2D binaryMask()
    {
        BinaryArray2D mask = BinaryArray2D.create(3, 3);
        mask.setBoolean(0, 1, true);
        mask.setBoolean(1, 0, true);
        mask.setBoolean(1, 1, true);
        mask.setBoolean(1, 2, true);
        mask.setBoolean(2, 1, true);
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
        return new int[] { 1, 1 };
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see net.sci.image.morphology.Strel#getShifts()
     */
    @Override
    public int[][] shifts()
    {
        int[][] shifts = new int[][] { { 0, -1 }, { -1, 0 }, { 0, 0 }, { +1, 0 }, { 0, +1 } };
        return shifts;
    }
    
    /**
     * Returns this structuring element, as is is self-reverse.
     * 
     * @see InPlaceStrel2D#reverse()
     */
    @Override
    public InPlaceStrel2D reverse()
    {
        return this;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see net.sci.array.data.Array2D)
     */
    @Override
    public void inPlaceDilation2d(ScalarArray2D<?> array)
    {
        if (array instanceof UInt8Array2D)
            inPlaceDilationGray8((UInt8Array2D) array);
        else
            inPlaceDilationFloat(array);
    }
    
    private void inPlaceDilationGray8(UInt8Array2D array)
    {
        // size of image
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        System.out.println("dilate with cross 3x3 uint8");
        
        int[][] buffer = new int[3][sizeX];
        
        // init buffer with background and first two lines
        for (int x = 0; x < sizeX; x++)
        {
            buffer[0][x] = UInt8.MIN_INT;
            buffer[1][x] = UInt8.MIN_INT;
            buffer[2][x] = array.getInt(x, 0);
        }
        
        // Iterate over image lines
        int valMax;
        for (int y = 0; y < sizeY; y++)
        {
            fireProgressChanged(this, y, sizeY);
            
            // permute lines in buffer
            int[] tmp = buffer[0];
            buffer[0] = buffer[1];
            buffer[1] = buffer[2];
            
            // initialize values of the last line in buffer
            if (y < sizeY - 1)
            {
                for (int x = 0; x < sizeX; x++)
                    tmp[x] = array.getInt(x, y + 1);
            }
            else
            {
                for (int x = 0; x < sizeX; x++)
                    tmp[x] = UInt8.MIN_INT;
            }
            buffer[2] = tmp;
            
            // process first pixel independently
            valMax = max5(buffer[0][0], buffer[1][0], buffer[1][1], buffer[2][0], UInt8.MIN_INT);
            array.setInt(0, y, valMax);
            
            // Iterate over pixel of the line
            for (int x = 1; x < sizeX - 1; x++)
            {
                valMax = max5(buffer[0][x], buffer[1][x - 1], buffer[1][x], buffer[1][x + 1], buffer[2][x]);
                array.setInt(x, y, valMax);
            }
            
            // process last pixel independently
            valMax = max5(buffer[0][sizeX - 1], buffer[1][sizeX - 2], buffer[1][sizeX - 1], buffer[2][sizeX - 1],
                    UInt8.MIN_INT);
            array.setInt(sizeX - 1, y, valMax);
        }
        
        // clear the progress bar
        fireProgressChanged(this, sizeY, sizeY);
    }
    
    private void inPlaceDilationFloat(ScalarArray2D<?> array)
    {
        // size of image
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        
        double[][] buffer = new double[3][sizeX];
        
        // init buffer with background and first two lines
        for (int x = 0; x < sizeX; x++)
        {
            buffer[0][x] = Double.NEGATIVE_INFINITY;
            buffer[1][x] = Double.NEGATIVE_INFINITY;
            buffer[2][x] = array.getValue(x, 0);
        }
        
        // Iterate over image lines
        double valMax;
        for (int y = 0; y < sizeY; y++)
        {
            fireProgressChanged(this, y, sizeY);
            
            // permute lines in buffer
            double[] tmp = buffer[0];
            buffer[0] = buffer[1];
            buffer[1] = buffer[2];
            
            // initialize values of the last line in buffer
            if (y < sizeY - 1)
            {
                for (int x = 0; x < sizeX; x++)
                    tmp[x] = array.getValue(x, y + 1);
            }
            else
            {
                for (int x = 0; x < sizeX; x++)
                    tmp[x] = Double.NEGATIVE_INFINITY;
            }
            buffer[2] = tmp;
            
            // process first pixel independently
            valMax = max5(buffer[0][0], buffer[1][0], buffer[1][1], buffer[2][0], Double.NEGATIVE_INFINITY);
            array.setValue(0, y, valMax);
            
            // Iterate over pixel of the line
            for (int x = 1; x < sizeX - 1; x++)
            {
                valMax = max5(buffer[0][x], buffer[1][x - 1], buffer[1][x], buffer[1][x + 1], buffer[2][x]);
                array.setValue(x, y, valMax);
            }
            
            // process last pixel independently
            valMax = max5(buffer[0][sizeX - 1], buffer[1][sizeX - 2], buffer[1][sizeX - 1], buffer[2][sizeX - 1],
                    Double.NEGATIVE_INFINITY);
            array.setValue(sizeX - 1, y, valMax);
        }
        
        // clear the progress bar
        fireProgressChanged(this, sizeY, sizeY);
    }
    
    /**
     * Computes the maximum of the 5 integer values.
     */
    private final static int max5(int v1, int v2, int v3, int v4, int v5)
    {
        int max1 = Math.max(v1, v2);
        int max2 = Math.max(v3, v4);
        max1 = Math.max(max1, v5);
        return Math.max(max1, max2);
    }
    
    /**
     * Computes the maximum of the 5 float values.
     */
    private final static double max5(double v1, double v2, double v3, double v4, double v5)
    {
        double max1 = Math.max(v1, v2);
        double max2 = Math.max(v3, v4);
        max1 = Math.max(max1, v5);
        return Math.max(max1, max2);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * net.sci.image.morphology.strel.InPlaceStrel#inPlaceErosion(ij.process.
     * Array2D<?>)
     */
    @Override
    public void inPlaceErosion2d(ScalarArray2D<?> array)
    {
        if (array instanceof UInt8Array2D)
            inPlaceErosionGray8((UInt8Array2D) array);
        else
            inPlaceErosionFloat(array);
    }
    
    private void inPlaceErosionGray8(UInt8Array2D array)
    {
        // size of image
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        
        int[][] buffer = new int[3][sizeX];
        
        // init buffer with background and first two lines
        for (int x = 0; x < sizeX; x++)
        {
            buffer[0][x] = UInt8.MAX_INT;
            buffer[1][x] = UInt8.MAX_INT;
            buffer[2][x] = array.getInt(x, 0);
        }
        
        // Iterate over image lines
        int valMin;
        for (int y = 0; y < sizeY; y++)
        {
            fireProgressChanged(this, y, sizeY);
            
            // permute lines in buffer
            int[] tmp = buffer[0];
            buffer[0] = buffer[1];
            buffer[1] = buffer[2];
            
            // initialize values of the last line in buffer
            if (y < sizeY - 1)
            {
                for (int x = 0; x < sizeX; x++)
                    tmp[x] = array.getInt(x, y + 1);
            }
            else
            {
                for (int x = 0; x < sizeX; x++)
                    tmp[x] = UInt8.MAX_INT;
            }
            buffer[2] = tmp;
            
            // process first pixel independently
            valMin = min5(buffer[0][0], buffer[1][0], buffer[1][1], buffer[2][0], UInt8.MAX_INT);
            array.setInt(0, y, valMin);
            
            // Iterate over pixel of the line
            for (int x = 1; x < sizeX - 1; x++)
            {
                valMin = min5(buffer[0][x], buffer[1][x - 1], buffer[1][x], buffer[1][x + 1], buffer[2][x]);
                array.setInt(x, y, valMin);
            }
            
            // process last pixel independently
            valMin = min5(buffer[0][sizeX - 1], buffer[1][sizeX - 2], buffer[1][sizeX - 1], buffer[2][sizeX - 1],
                    UInt8.MAX_INT);
            array.setInt(sizeX - 1, y, valMin);
        }
        
        // clear the progress bar
        fireProgressChanged(this, sizeY, sizeY);
    }
    
    private void inPlaceErosionFloat(ScalarArray2D<?> array)
    {
        // size of image
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        
        double[][] buffer = new double[3][sizeX];
        
        // init buffer with background and first line
        for (int x = 0; x < sizeX; x++)
        {
            buffer[0][x] = Double.MAX_VALUE;
            buffer[1][x] = Double.MAX_VALUE;
            buffer[2][x] = array.getValue(x, 0);
        }
        
        // Iterate over image lines
        double valMin;
        for (int y = 0; y < sizeY; y++)
        {
            fireProgressChanged(this, y, sizeY);
            
            // permute lines in buffer
            double[] tmp = buffer[0];
            buffer[0] = buffer[1];
            buffer[1] = buffer[2];
            
            // initialize values of the last line in buffer
            if (y < sizeY - 1)
            {
                for (int x = 0; x < sizeX; x++)
                    tmp[x] = array.getValue(x, y + 1);
            }
            else
            {
                for (int x = 0; x < sizeX; x++)
                    tmp[x] = Double.MAX_VALUE;
            }
            buffer[2] = tmp;
            
            // process first pixel independently
            valMin = min5(buffer[0][0], buffer[1][0], buffer[1][1], buffer[2][0], Double.MAX_VALUE);
            array.setValue(0, y, valMin);
            
            // Iterate over pixel of the line
            for (int x = 1; x < sizeX - 1; x++)
            {
                valMin = min5(buffer[0][x], buffer[1][x - 1], buffer[1][x], buffer[1][x + 1], buffer[2][x]);
                array.setValue(x, y, valMin);
            }
            
            // process last pixel independently
            valMin = min5(buffer[0][sizeX - 1], buffer[1][sizeX - 2], buffer[1][sizeX - 1], buffer[2][sizeX - 1],
                    Double.MAX_VALUE);
            array.setValue(sizeX - 1, y, valMin);
        }
        
        // clear the progress bar
        fireProgressChanged(this, sizeY, sizeY);
    }
    
    /**
     * Computes the minimum of the 5 values.
     */
    private final static int min5(int v1, int v2, int v3, int v4, int v5)
    {
        int min1 = Math.min(v1, v2);
        int min2 = Math.min(v3, v4);
        min1 = Math.min(min1, v5);
        return Math.min(min1, min2);
    }
    
    /**
     * Computes the minimum of the 5 float values.
     */
    private final static double min5(double v1, double v2, double v3, double v4, double v5)
    {
        double min1 = Math.min(v1, v2);
        double min2 = Math.min(v3, v4);
        min1 = Math.min(min1, v5);
        return Math.min(min1, min2);
    }
}
