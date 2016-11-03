/**
 * 
 */
package net.sci.image.process.segment;

import net.sci.array.data.ScalarArray;
import net.sci.array.process.Histogram;

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
		double minValue = Double.POSITIVE_INFINITY; 
		double maxValue = Double.NEGATIVE_INFINITY;
		ScalarArray.Iterator<?> iter = array.iterator();
		while(iter.hasNext())
		{
			double value = iter.nextValue();
			minValue = Math.min(minValue, value);
			maxValue = Math.max(maxValue, value);
		}
		double[] range = new double[]{minValue, maxValue};
		
		// choose 256 levels by default (convenient for 8-bits, should be enough for other types)
		int nLevels = 256;
		
		// compte the array of possible thresholds
		double[] levels = new double[nLevels];
		double levelStep = (range[1] - range[0]) / (nLevels - 1);
		for (int i = 0; i < nLevels; i++)
		{
			levels[i] = range[0] + i * levelStep; 
		}
		
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
			// compute probability and mean value for each class
			double p0 = 0, mu0 = 0;
			for (int j = 0; j <= i; j++)
			{
				p0 += freqHisto[j];
				mu0 += freqHisto[j] * levels[j];
			}
			mu0 /= p0;
			
			double p1 = 0, mu1 = 0;
			for (int j = i + 1; j < nLevels; j++)
			{
				p1 += freqHisto[j];
				mu1 += freqHisto[j] * levels[j];
			}
			mu1 /= p1;
			
			// compute variance for each class
			double var0 = 0;
			for (int j = 0; j <= i; j++)
			{
				double level2 = levels[j] - mu0;
				var0 += freqHisto[j] * level2 * level2 / p0;
			}
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
		
		// compute threshold value
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
//
//		if nargout > 1
//		    segImg = img >= value;
//		end

//		% vector of gray levels for variance computation
//		levels = 0:L-1;
//
//		% compute normalized histogram
//		h = imHistogram(img, varargin{:})';
//		h = h / sum(h);
//
//		% average value within whole image
//		mu = sum(h .* levels);
//
//		% vector of thresholds to consider (size is number of graylevels minus one)
//		threshInds = 2:L;
//
//		% allocate memory
//		sigmab = zeros(1, L - 1);
//		sigmaw = zeros(1, L - 1);
//
//		for i = 1:length(threshInds)
//		    % index of current threshold in histogram values, from 2 to L
//		    t = threshInds(i);
//		    
//		    % linear indices for each class
//		    ind0 = 1:(t-1); % background
//		    ind1 = t:L;     % foreground, including threshold
//		    
//		    % probabilities associated with each class
//		    p0 = sum(h(ind0));
//		    p1 = sum(h(ind1));
//		    
//		    % average value of each class
//		    mu0 = sum(h(ind0) .* levels(ind0)) / p0;
//		    mu1 = sum(h(ind1) .* levels(ind1)) / p1;
//		    
//		    % inner variance of each class
//		    var0 = sum( h(ind0) .* (levels(ind0) - mu0) .^ 2) / p0;
//		    var1 = sum( h(ind1) .* (levels(ind1) - mu1) .^ 2) / p1;
//		    
//		    % between (inter) class variance
//		    sigmab(i) = p0 * (mu0 - mu) ^ 2 + p1 * (mu1 - mu) ^ 2;
//		    
//		    % within (intra) class variance
//		    sigmaw(i) = p0 * var0 + p1 * var1;
//		end
//
//		% compute threshold value
//		[mini, ind] = min(sigmaw); %#ok<ASGLU>
//		value = levels(ind + 1);
//
//		if nargout > 1
//		    segImg = img >= value;
//		end
	}

}
