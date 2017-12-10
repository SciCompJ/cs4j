/**
 * 
 */
package net.sci.table.cluster;

import java.util.Random;

import net.sci.table.Table;
import net.sci.table.TableOperator;

/**
 * @author dlegland
 *
 */
public class KMeans implements TableOperator
{
	int nClasses;
	
	int nIters= 10;
	
	/**
	 * 
	 */
	public KMeans(int nClasses)
	{
		this.nClasses = nClasses;
	}

	@Override
	public Table process(Table table)
	{
		int nr = table.getRowNumber();
		int np = table.getColumnNumber();
				
		// nclass * nParams array
		double[][] centroids = new double[nClasses][np];
		
		// choose random individuals as centroids
		Random rand = new Random();
		for (int k = 0; k < nClasses; k++)
		{
			int index = rand.nextInt(nr);
			// TODO:check not already chosen
			for (int p = 0; p < np; p++)
			{
				centroids[k][p] = table.getValue(index, p);
			}
		}
		
		// performs initial allocation
		int[] classes = affectClasses(table, centroids);
		
		// Performs several iterations (count initialization as first iteration)
		for (int iIter = 1; iIter < nIters; iIter++)
		{
			centroids = computeClassCentroids(table, classes, nClasses);
			classes = affectClasses(table, centroids);
		}
		
		// Create result table
		Table res = Table.create(nr, 1);
		res.setColumnNames(new String[]{"Class"});
		for (int i = 0; i < nr; i++)
		{
			res.setValue(i, 0, classes[i]);
		}
		return res;
	}

	private int[] affectClasses(Table table, double[][] centroids)
	{
		//TODO: use another version using pre-allocated array
		// allocate memory for result
		int nr = table.getRowNumber();
		int[] classes = new int[nr];
		
		// iterate over individual rows
		for (int i = 0; i < nr; i++)
		{
			double[] row = table.getRowValues(i);
			classes[i] = findClosestPoint(row, centroids);
		}
		
		// return allocated classes
		return classes;
	}
	
	private static final int findClosestPoint(double[] point, double[][] points)
	{
		int index = -1;
		double minDist = Double.POSITIVE_INFINITY;
		for (int iClass = 0; iClass < points.length; iClass++)
		{
			double dist = distanceSq(point, points[iClass]);
			if (dist < minDist)
			{
				index = iClass;
				minDist = dist;
			}
		}
		return index;
	}
	
	private static final double distanceSq(double[] p1, double[] p2)
	{
		double distSq = 0;
		for (int i = 0; i < p1.length; i++)
		{
			double dp = p2[i] - p1[i];
			distSq += dp * dp;
		}
		return distSq;
	}
	
	private static final double[][] computeClassCentroids(Table table, int[] classes, int nClasses)
	{
		int nr = table.getRowNumber();
		int np = table.getColumnNumber();
		
		double[][] cumsum = new double[nClasses][np];
		int[] counts = new int[nClasses];
		
		// update cumulated sum and count
		for (int i = 0; i < nr; i++)
		{
			int index = classes[i];
			for (int p = 0; p < np; p++)
			{
				cumsum[index][p] += table.getValue(i, p);
			}
			counts[index]++;
		}
		
		// divide cumulative sums by counts to obtain centroids
		for (int i = 0; i < nClasses; i++)
		{
			int count = counts[i];
			for (int p = 0; p < np; p++)
			{
				cumsum[i][p] /= count;
			}
		}
		
		return cumsum;
	}
}
