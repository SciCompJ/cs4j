/**
 * 
 */
package net.sci.array.numeric.process;

import static java.lang.Math.max;
import static java.lang.Math.min;

import net.sci.array.Array;
import net.sci.array.numeric.ScalarArray;

/**
 * Computes histogram of values within a numeric array. 
 */
public class Histogram
{
    double[] range;
    int nBins;
    
    public Histogram(double[] range, int nBins)
    {
        this.range = range;
        this.nBins = nBins;
    }
    
    public Result process(Array<?> array)
    {
        if (array instanceof ScalarArray)
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
            for (double v : ((ScalarArray<?>) array).values())
            {
                int binIndex = (int) java.lang.Math.floor((v - v0) / binWidth);
                binIndex = min(max(binIndex, 0), nBins - 1);
                histo[binIndex]++;
            }
            return new Result(xData, histo);
        }
        else
        {
            throw new RuntimeException("Requires a Scalar Array as input");
        }
    }
    
    public static class Result
    {
        /** The position of histogram bins. */
        double[] binCenters;
        
        /**
         * The frequency count for values.
         */
        int[] counts;
            
        public Result(double[] binCenters, int[] counts)
        {
            this.binCenters = binCenters;
            this.counts = counts;
        }
        
        public int length()
        {
            return this.binCenters.length;
        }
        
        public double[] binCenters()
        {
            return this.binCenters;
        }
        
        public int[] counts()
        {
            return this.counts;
        }
    }
}
