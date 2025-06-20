/**
 * 
 */
package net.sci.image.segmentation;

import net.sci.array.Histograms;
import net.sci.array.numeric.ScalarArray;

/**
 * Automatic threshold procedure based on the isodata algorithm, that
 * iteratively computes the average value of background ad foreground, and
 * updates the threshold value. Returns a binary array.
 * 
 * Isodata algorithm is a K-means algorithm with two classes, using image scalar
 * values as input.
 * 
 * Implementation relies solely on image histogram. This implementation computes
 * an histogram using 256 bins.
 * 
 * @see OtsuThreshold
 * @see KMeansSegmentation
 * 
 * @author dlegland
 */
public class IsodataThreshold extends AutoThreshold
{
    /**
     * Default empty  constructor.
     */
    public IsodataThreshold()
    {
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see net.sci.image.process.segment.AutoThreshold#computeThresholdValue(net.sci.array.data.ScalarArray)
     */
    @Override
    public double computeThresholdValue(ScalarArray<?> array)
    {
        // get bounds for computing histogram
        double[] range = array.finiteValueRange();

        // choose 256 levels by default (convenient for 8-bits, should be enough for other types)
        int nLevels = 256;

        return computeThresholdValue(array, range, nLevels);
    }

    public double computeThresholdValue(ScalarArray<?> array, double[] range, int nLevels)
    {
        // Compute count histogram
        int[] histo = Histograms.histogramScalar(array, range, nLevels);
        
        // use the global mean as first guess of threshold index
        int indT = (int) histMean(histo, 0, histo.length);

        int maxIter = 10;
        for (int k = 0; k < maxIter; k++)
        {
            // compute mean value of lower-threshold values
            double meanLower = histMean(histo, 0, indT);
            
            // compute mean value of upper-threshold values
            double meanUpper = histMean(histo, indT, histo.length);
            
            // update threshold value
            indT = (int) Math.round((meanLower + meanUpper) / 2.0);
        }
        
        // convert index into intensity value
        double[] levels = Histograms.computeBinPositions(range, nLevels);
        return levels[indT];
    }
    
    
    /**
     * Computes the average value within an interval of the histogram of value
     * distribution.
     * 
     * Example: {code double vMean = histMean(histo, 0, histo.length);}
     * 
     * @param histo
     *            the histogram of value distribution
     * @param xMin
     *            the lower bound of the computation interval (inclusive)
     * @param xMax
     *            the upper bound of the computation interval (exclusive)
     * @return the average value can be converted into an index of the histogram
     *         after cast to int
     */
    private double histMean(int[] histo, int xMin, int xMax)
    {
        // compute number and integrated value of all values
        int count = 0;
        int sum = 0;
        for (int i = xMin; i < xMax; i++)
        {
            count += histo[i];
            sum += (histo[i] * i);
        }
        
        return sum / count;
    }

}
