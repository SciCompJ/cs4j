/**
 * 
 */
package net.sci.array.process;

import net.sci.array.color.RGB16;
import net.sci.array.color.RGB16Array;
import net.sci.array.color.RGB8;
import net.sci.array.color.RGB8Array;
import net.sci.array.scalar.ScalarArray;

/**
 * Computes histograms of values from arrays.
 * 
 * @author dlegland
 *
 */
public class Histogram
{
	public static final int[] histogram(ScalarArray<?> array, double[] range, int nBins)
	{
		// compute the width of an individual bin
		double binWidth = (range[1] - range[0]) / (nBins - 1);
		
		// allocate memory for result
		int[] histo = new int[nBins];
		
		// iterate over samples to update the histogram
		for(int[] pos : array.positions())
		{
			int binIndex = (int) java.lang.Math.round((array.getValue(pos) - range[0] - binWidth / 2) / binWidth);
			binIndex = java.lang.Math.min(java.lang.Math.max(binIndex, 0), nBins - 1);
			histo[binIndex]++;
		}
		
		return histo;
	}
	
    public static final int[][] histogram(RGB8Array array)
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
    
    public static final int[][] histogramRGB16(RGB16Array array)
    {
        // determines max red, green and blue values
        int rMax = 0, gMax = 0, bMax = 0;
        for (RGB16 rgb : array)
        {
            rMax = java.lang.Math.max(rMax, rgb.getSample(0));
            gMax = java.lang.Math.max(gMax, rgb.getSample(1));
            bMax = java.lang.Math.max(bMax, rgb.getSample(2));
        }
        double k = 255.0 / java.lang.Math.max(java.lang.Math.max(rMax,  gMax),  bMax);
        
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
            int r = java.lang.Math.min((int) (rgb.getSample(0) * k), 255);
            int g = java.lang.Math.min((int) (rgb.getSample(1) * k), 255);
            int b = java.lang.Math.min((int) (rgb.getSample(2) * k), 255);
            histo[1][r]++;
            histo[2][g]++;
            histo[3][b]++;
        }
        
        return histo;
    }
    
	public static final double[] computeBinPositions(double[] range, int nBins)
	{
		// compte the array of possible thresholds
		double[] levels = new double[nBins];
		double levelStep = (range[1] - range[0]) / (nBins - 1);
		for (int i = 0; i < nBins; i++)
		{
			levels[i] = range[0] + i * levelStep; 
		}
		return levels;
	}
}
