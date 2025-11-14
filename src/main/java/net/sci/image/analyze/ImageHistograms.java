/**
 * 
 */
package net.sci.image.analyze;

import static java.lang.Math.max;
import static java.lang.Math.min;

import net.sci.array.color.RGB16;
import net.sci.array.color.RGB16Array;
import net.sci.array.color.RGB8;
import net.sci.array.color.RGB8Array;
import net.sci.array.numeric.UInt8Array;
import net.sci.array.numeric.process.Histogram;

/**
 * A collection of static methods for computing histograms within images.
 */
public class ImageHistograms
{
    /**
     * Computes the histogram of an array containing UInt8 values.
     * 
     * @param array
     *            the input array.
     * @return the histogram of the UInt8 values, as an array with 256 entries.
     */
    public static final Histogram.Result histogramUInt8(UInt8Array array)
    {
        // allocate memory for result
        int[] histo = new int[256];
        
        // iterate over samples to update the histogram
        for(int[] pos : array.positions())
        {
            int value = array.getInt(pos);
            histo[value]++;
        }
        
        return new Histogram.Result(linearRange(256), histo);
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
    public static final Histogram.Result[] histogramRGB8(RGB8Array array)
    {
        // allocate memory for counts of values within each channel
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
        
        // create Histogram objects
        Histogram.Result[] res = new Histogram.Result[3];
        double[] xdata = linearRange(256);
        for (int i = 0; i < 3; i++)
        {
            res[i] = new Histogram.Result(xdata, histo[i]);
        }
        return res;
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
    public static final Histogram.Result[] histogramRGB16(RGB16Array array)
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
        
        // allocate memory for counts of values within each channel
        int[][] counts = new int[4][256];
        
        double[] binCenters = new double[255];
        // initialize bin centers
        for (int i = 0; i < 255; i++)
        {
            binCenters[i] = (int) (i / k);
        }
        
        // iterate over samples to update the histogram
        for (RGB16 rgb : array)
        {
            int r = min((int) (rgb.getSample(0) * k), 255);
            int g = min((int) (rgb.getSample(1) * k), 255);
            int b = min((int) (rgb.getSample(2) * k), 255);
            counts[0][r]++;
            counts[1][g]++;
            counts[2][b]++;
        }
        
        // create Histogram objects
        Histogram.Result[] res = new Histogram.Result[3];
        double[] xdata = linearRange(256);
        for (int i = 0; i < 3; i++)
        {
            res[i] = new Histogram.Result(xdata, counts[i]);
        }
        return res;
    }
    
    /**
     * Computes a linear range of values between 0 and {@code nValues-1}.
     * @param nValues
     * @return
     */
    private static final double[] linearRange(int nValues)
    {
        double[] res = new double[nValues];
        for (int i = 0; i < nValues; i++)
        {
            res[i] = i;
        }
        return res;
    }

    /**
     * Private constructor to prevent instantiation.
     */
    private ImageHistograms()
    {
    }
}
