/**
 * 
 */
package net.sci.image.analyze;

import net.sci.array.data.color.RGB8Array2D;
import net.sci.array.data.scalar2d.ScalarArray2D;
import net.sci.array.type.RGB8;
import net.sci.geom.geom2d.Box2D;
import net.sci.geom.geom2d.Domain2D;

/**
 * Computes the histogram of array values contained in specific region.
 * 
 * @author dlegland
 *
 */
public class RegionHistogram
{
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
	public static final int[] histogram2d(ScalarArray2D<?> array, Domain2D domain, double[] range, int nBins)
	{
		// compute the width of an individual bin
		double binWidth = (range[1] - range[0]) / (nBins - 1);
		
		// allocate memory for result
		int[] histo = new int[nBins];
		
		// compute bounding box f domain to avoid unnecessary computations
		Box2D bbox = domain.boundingBox();
		int xmin = (int) Math.max(0, Math.floor(bbox.getMinX()));
		int xmax = (int) Math.min(array.getSize(0), Math.ceil(bbox.getMaxX()));
		int ymin = (int) Math.max(0, Math.floor(bbox.getMinY()));
		int ymax = (int) Math.min(array.getSize(1), Math.ceil(bbox.getMaxY()));
		
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
					histo[binIndex]++;
				}
			}
		}
		
		return histo;
	}
	
	public static final int[][] histogram2d(RGB8Array2D array, Domain2D domain)
	{
		// allocate memory for result
		int[][] histo = new int[3][256];
		
		// compute bounding box f domain to avoid unnecessary computations
		Box2D bbox = domain.boundingBox();
		int xmin = (int) Math.max(0, Math.floor(bbox.getMinX()));
		int xmax = (int) Math.min(array.getSize(0), Math.ceil(bbox.getMaxX()));
		int ymin = (int) Math.max(0, Math.floor(bbox.getMinY()));
		int ymax = (int) Math.min(array.getSize(1), Math.ceil(bbox.getMaxY()));
		
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
		return histo;
	}
	

	/**
	 * 
	 */
	public RegionHistogram()
	{
	}

}