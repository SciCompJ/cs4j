/**
 * 
 */
package net.sci.image.process.segment;

import net.sci.array.process.Histogram;
import net.sci.array.scalar.ScalarArray;

/**
 * Compute threshold value using Otsu's method, that consists in minimizing the
 * intra-class variance while maximizing the extra-class variance.
 * 
 * @author dlegland
 *
 */
public class OtsuThreshold extends AutoThreshold
{

	/* (non-Javadoc)
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
        // compute the array of possible thresholds
        double[] levels = Histogram.computeBinPositions(range, nLevels);
        
        // Compute count histogram
        int[] histo = Histogram.histogram(array, range, nLevels);
        
        // compute total number of elements from the histogram
        int nElements = 0;
        for (int i = 0; i < nLevels; i++)
        {
            nElements += histo[i];
        }
        
        // compute frequency histogram
        double[] freqHisto = new double[nLevels];
        for (int i = 0; i < nLevels; i++)
        {
            freqHisto[i] = ((double) histo[i]) / nElements;
        }
        
        // Average value
        double mu = 0;
        for (int i = 0; i < nLevels; i++)
        {
            mu += levels[i] * freqHisto[i];
        }
        
        // allocate memory for variance between and within classes, 
        // for each possible threshold level
        double[] sigmab = new double[nLevels - 1];
        double[] sigmaw = new double[nLevels - 1];

        // iterate over possible threshold values
        // first class range from 0 to i (inclusive)
        // second class range from i+1 to nLevels-1
        for (int i = 0; i < nLevels - 1; i++)
        {
            // probability and mean value for first class
            double p0 = 0, mu0 = 0;
            for (int j = 0; j <= i; j++)
            {
                p0 += freqHisto[j];
                mu0 += freqHisto[j] * levels[j];
            }
            mu0 /= p0;
            
            // probability and mean value for second class
            double p1 = 0, mu1 = 0;
            for (int j = i + 1; j < nLevels; j++)
            {
                p1 += freqHisto[j];
                mu1 += freqHisto[j] * levels[j];
            }
            mu1 /= p1;
            
            // compute variance for first class
            double var0 = 0;
            for (int j = 0; j <= i; j++)
            {
                double level2 = levels[j] - mu0;
                var0 += freqHisto[j] * level2 * level2 / p0;
            }
            
            // compute variance for second class
            double var1 = 0;
            for (int j = i + 1; j < nLevels; j++)
            {
                double level2 = levels[j] - mu1;
                var1 += freqHisto[j] * level2 * level2 / p1;
            }
            
            // between (inter) class variance
            sigmab[i] = p0 * (mu0 - mu) * (mu0 - mu) + p1 * (mu1 - mu) * (mu1 - mu);
            
            // within (intra) class variance
            sigmaw[i] = p0 * var0 + p1 * var1;
        }
        
        // compute threshold value by identifying the level that produces the
        // minimal intra-class variance
        double minSigmaW = Double.POSITIVE_INFINITY;
        int indMin = 0;
        for (int i = 0; i < nLevels - 1; i++)
        {
            if (sigmaw[i] < minSigmaW)
            {
                minSigmaW = sigmaw[i];
                indMin = i;
            }
        }
        
        return levels[indMin+1];
    }

}
