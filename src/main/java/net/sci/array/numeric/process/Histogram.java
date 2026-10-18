/**
 * 
 */
package net.sci.array.numeric.process;

import static java.lang.Math.max;
import static java.lang.Math.min;

import net.sci.array.Array;
import net.sci.array.numeric.Scalar;
import net.sci.array.numeric.ScalarArray;

/**
 * Computes histogram of values within a numeric array. 
 */
public class Histogram
{
    double[] range;
    int nBins;
    
    /**
     * Creates a new operator for computing histograms of scalar arrays, using
     * the specified range of values and number of bins.
     * 
     * @param range
     *            the range of values
     * @param nBins
     *            the number of bins of the histogram
     */
    public Histogram(double[] range, int nBins)
    {
        this.range = range;
        this.nBins = nBins;
    }
    
    /**
     * Computes histogram of values within the specified array.
     * 
     * @param array
     *            the array to process, that must contain scalar elements
     * @return the histogram of the values within the array
     */
    public Result process(Array<?> array)
    {
        if (array.elementInstanceOf(Scalar.class))
        {
            @SuppressWarnings({ "unchecked", "rawtypes" })
            ScalarArray scalarArray = ScalarArray.wrap((Array<Scalar>) array);
            return processScalar(scalarArray);
        }
        else
        {
            throw new RuntimeException("Requires a Scalar Array as input");
        }
    }
    
    private Result processScalar(ScalarArray<?> scalarArray)
    {
        // compute the width of an individual bin
        double binWidth = (range[1] - range[0]) / (nBins - 1);
        // and the beginning of the first bin
        double v0 = range[0] - binWidth / 2;
        
        // compute bin centers
        double[] xData = new double[nBins];
        for (int i = 0; i < nBins; i++)
        {
            xData[i] = range[0] + binWidth * i;
        }
        
        // allocate memory for counts
        int[] histo = new int[nBins];
        
        // iterate over samples to update the histogram
        for (double v : scalarArray.values())
        {
            int binIndex = (int) java.lang.Math.floor((v - v0) / binWidth);
            binIndex = min(max(binIndex, 0), nBins - 1);
            histo[binIndex]++;
        }
        return new Result(xData, histo);
    }
    
    /**
     * Utility class that stores results of histogram computation.
     */
    public static class Result
    {
        /** The position of histogram bins. */
        double[] binCenters;
        
        /**
         * The frequency count for values.
         */
        int[] counts;
            
        /**
         * Creates a new result, from bin centers and associated counts
         * 
         * @param binCenters
         *            the bin centers
         * @param counts
         *            the value count for each bin
         */
        public Result(double[] binCenters, int[] counts)
        {
            this.binCenters = binCenters;
            this.counts = counts;
        }
        
        /**
         * Returns the length of the histogram, as the number of bins.
         * 
         * @return the number of bins in the histogram
         */
        public int length()
        {
            return this.binCenters.length;
        }
        
        /**
         * Returns the centers of the histogram bins.
         * 
         * @return the centers of the histogram bins.
         */
        public double[] binCenters()
        {
            return this.binCenters;
        }
        
        /**
         * Returns the number of element values associated to each bin.
         * 
         * @return the number of element values associated to each bin.
         */
        public int[] counts()
        {
            return this.counts;
        }
    }
}
