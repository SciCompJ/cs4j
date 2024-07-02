/**
 * 
 */
package net.sci.array;

import net.sci.array.color.RGB16;
import net.sci.array.color.RGB16Array;
import net.sci.array.color.RGB8;
import net.sci.array.color.RGB8Array;
import net.sci.array.numeric.ScalarArray;
import net.sci.array.numeric.UInt8Array;

import static java.lang.Math.min;
import static java.lang.Math.max;

/**
 * A collection of static methods for computing histograms from arrays.
 * 
 * @author dlegland
 *
 */
public class Histograms
{
    /**
     * Computes the histogram of an array containing UInt8 values.
     * 
     * @param array
     *            the input array.
     * @return the histogram of the UInt8 values, as an array with 256 entries.
     */
    public static final int[] histogramUInt8(UInt8Array array)
    {
        // allocate memory for result
        int[] histo = new int[256];
        
        // iterate over samples to update the histogram
        for(int[] pos : array.positions())
        {
            int value = array.getInt(pos);
            histo[value]++;
        }
        
        return histo;
    }
    
    /**
     * Computes the histogram of an array containing scalar values.
     * 
     * @param array
     *            the input array.
     * @param range
     *            the range of values for computing histogram.
     * @param nBins
     *            the number of bins of the output histogram.
     * @return the histogram of the scalar values, as an array with
     *         <code>nBins</code> entries.
     */
	public static final int[] histogramScalar(ScalarArray<?> array, double[] range, int nBins)
	{
		// compute the width of an individual bin
		double binWidth = (range[1] - range[0]) / (nBins - 1);
		// and the beginning of the first bin
		double v0 = range[0] - binWidth / 2;
        
		// allocate memory for result
		int[] histo = new int[nBins];
		
		// iterate over samples to update the histogram
		for(double v : array.values())
		{
			int binIndex = (int) java.lang.Math.round((v - v0) / binWidth);
			binIndex = min(max(binIndex, 0), nBins - 1);
			histo[binIndex]++;
		}
		
		return histo;
	}
	
    /**
     * Computes the histogram of an array containing RGB8 values. One histogram
     * is computed for each channel. The histogram of each channel contains 256
     * entries.
     * 
     * @param array
     *            the input array.
     * @return the histogram of the RGB8 values, as a 3-by-256 integer array.
     */
    public static final int[][] histogramRGB8(RGB8Array array)
    {
        // allocate memory for result
        int[][] histo = new int[3][256];
        
        // iterate over samples to update the histogram
        for (RGB8 rgb : array)
        {
            int r = rgb.getSample(0);
            int g = rgb.getSample(1);
            int b = rgb.getSample(2);
            histo[0][r]++;
            histo[1][g]++;
            histo[2][b]++;
        }
        
        return histo;
    }
    
    /**
     * Computes the histogram of an array containing RGB16 values. One histogram
     * is computed for each channel. The histogram of each channel contains 256
     * entries. The bins are distributed between 0 and the maximum value within
     * the channels (the same max value is used for all channels).
     * 
     * @param array
     *            the input array.
     * @return the histogram of the RGB16 values, as a 4-by-256 integer array.
     *         The first array contains value of bin centers.
     */
    public static final int[][] histogramRGB16(RGB16Array array)
    {
        // determines max red, green and blue values
        int rMax = 0, gMax = 0, bMax = 0;
        for (RGB16 rgb : array)
        {
            rMax = max(rMax, rgb.getSample(0));
            gMax = max(gMax, rgb.getSample(1));
            bMax = max(bMax, rgb.getSample(2));
        }
        double k = 255.0 / max(max(rMax,  gMax),  bMax);
        
        // allocate memory for result
        // first column contains bin center
        int[][] histo = new int[4][256];
        
        // initialize bin center
        for (int i = 0; i < 255; i++)
        {
            histo[0][i] = (int) (i / k);
        }
        
        // iterate over samples to update the histogram
        for (RGB16 rgb : array)
        {
            int r = min((int) (rgb.getSample(0) * k), 255);
            int g = min((int) (rgb.getSample(1) * k), 255);
            int b = min((int) (rgb.getSample(2) * k), 255);
            histo[1][r]++;
            histo[2][g]++;
            histo[3][b]++;
        }
        
        return histo;
    }
    
	public static final double[] computeBinPositions(double[] range, int nBins)
	{
		// compute the array of possible thresholds
		double[] levels = new double[nBins];
		double levelStep = (range[1] - range[0]) / (nBins - 1);
		for (int i = 0; i < nBins; i++)
		{
			levels[i] = range[0] + i * levelStep; 
		}
		return levels;
	}
	
	/**
	 * Private constructor to avoid instantiation.
	 */
	private Histograms()
	{
	}
}
