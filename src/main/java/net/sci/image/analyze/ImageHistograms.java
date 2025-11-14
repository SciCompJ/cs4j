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
import net.sci.array.color.RGB8Array2D;
import net.sci.array.numeric.ScalarArray2D;
import net.sci.array.numeric.UInt8Array;
import net.sci.array.numeric.process.Histogram;
import net.sci.geom.geom2d.Bounds2D;
import net.sci.geom.geom2d.Domain2D;

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
    public static final Histogram.Result[] histogramsRGB8(RGB8Array array)
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
    public static final Histogram.Result[] histogramsRGB16(RGB16Array array)
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
     * Computes histogram in a domain of a 2D array.
     * 
     * @param array
     *            the scalar array to analyze
     * @param domain
     *            the domain for selecting values
     * @param range
     *            gray value range for computing histogram
     * @param nBins
     *            the number of bins of the resulting histogram
     * @return the histogram within the specified domain
     */
    public static final Histogram.Result histogramScalar(ScalarArray2D<?> array, Domain2D domain, double[] range, int nBins)
    {
        // compute the width of an individual bin
        double binWidth = (range[1] - range[0]) / (nBins - 1);
        
        // compute bin centers
        double[] xData = new double[nBins];
        for (int i = 0; i < nBins; i++)
        {
            xData[i] = range[0] + binWidth * i;
        }
        
        // allocate memory for result
        int[] counts = new int[nBins];
        
        // compute bounding box of domain to avoid unnecessary computations
        Bounds2D bbox = domain.bounds();
        int xmin = (int) Math.max(0, Math.floor(bbox.xMin()));
        int xmax = (int) Math.min(array.size(0), Math.ceil(bbox.xMax()));
        int ymin = (int) Math.max(0, Math.floor(bbox.yMin()));
        int ymax = (int) Math.min(array.size(1), Math.ceil(bbox.yMax()));
        
        // iterate over samples to update the histogram
        for (int y = ymin; y < ymax; y++)
        {
            for (int x = xmin; x < xmax; x++)
            {
                if (domain.contains(x, y))
                {
                    double value = array.getValue(x, y);
                    int binIndex = (int) Math.round((value - range[0] - binWidth / 2) / binWidth);
                    binIndex = Math.min(Math.max(binIndex, 0), nBins - 1);
                    counts[binIndex]++;
                }
            }
        }
        
        return new Histogram.Result(xData, counts);
    }
    
    /**
     * Computes histogram of an array of RGB8 elements, and returns the result
     * in a data table.
     * 
     * The data table has four columns. The first column contains the bin center
     * (from 0 to 255). The three other columns contain the count of the
     * corresponding red, green and blue channels respectively.
     * 
     * @param array
     *            the input array of RGB8 elements
     * @return a new instance of DefaultNumericTable containing the resulting histogram.
     */
    public static final Histogram.Result[] histogramsRGB8(RGB8Array2D array, Domain2D domain)
    {
        // allocate memory for result
        int[][] histo = new int[3][256];
        
        // compute bounding box f domain to avoid unnecessary computations
        Bounds2D bbox = domain.bounds();
        int xmin = (int) Math.max(0, Math.floor(bbox.xMin()));
        int xmax = (int) Math.min(array.size(0), Math.ceil(bbox.xMax()));
        int ymin = (int) Math.max(0, Math.floor(bbox.yMin()));
        int ymax = (int) Math.min(array.size(1), Math.ceil(bbox.yMax()));
        
        // iterate over samples to update the histogram
        for (int y = ymin; y < ymax; y++)
        {
            for (int x = xmin; x < xmax; x++)
            {
                if (domain.contains(x, y))
                {
                    RGB8 rgb = array.get(x, y);
                    int r = rgb.getSample(0);
                    int g = rgb.getSample(1);
                    int b = rgb.getSample(2);
                    histo[0][r]++;
                    histo[1][g]++;
                    histo[2][b]++;
                }
            }
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
