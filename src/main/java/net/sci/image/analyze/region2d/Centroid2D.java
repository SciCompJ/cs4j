/**
 * 
 */
package net.sci.image.analyze.region2d;

import java.util.HashMap;
import java.util.Map;

import net.sci.array.numeric.IntArray2D;
import net.sci.geom.geom2d.Point2D;
import net.sci.image.Calibration;
import net.sci.image.ImageAxis;
import net.sci.image.label.LabelImages;
import net.sci.table.Table;


/**
 * Computes centroid position of regions within binary or label images.
 * 
 * @author dlegland
 *
 */
public class Centroid2D extends RegionAnalyzer2D<Point2D>
{
	// ==================================================
	// Static methods
	
	/**
	 * Computes centroid of each label in input image and returns the result as
	 * an array of double for each label.
	 * 
	 * @param labelImage
	 *            the input image containing label of regions
	 * @param labels
	 *            the array of labels to process within image
     * @param calib
     *            the (spatial) calibration of the image. Can be null, in that
     *            case default calibration will be used.
	 * @return an array containing for each label, the coordinates of the
	 *         centroid, in calibrated coordinates
	 */
	public static final Point2D[] centroids(IntArray2D<?> labelImage, int[] labels, Calibration calib) 
	{
		return new Centroid2D().analyzeRegions(labelImage, labels, calib);
	}
	
	/**
	 * Computes centroid of each label in input image and returns the result as
	 * an array of double for each label. This version does not take into
	 * account the spatial calibration, and returns the centroids in pixel
	 * coordinates.
	 * 
	 * @param labelArray
	 *            the input image containing label of regions
	 * @param labels
     *            the array of labels to process within image
	 * @return an array containing for each label, the coordinates of the
	 *         centroid, in pixel coordinates
	 */
	public static final double[][] centroids(IntArray2D<?> labelArray, int[] labels) 
	{
		// create associative array to know index of each label
		int nLabels = labels.length;
        HashMap<Integer, Integer> labelIndices = LabelImages.mapLabelIndices(labels);

		// allocate memory for result
		int[] counts = new int[nLabels];
		double[][] centroids = new double[nLabels][2];

		// compute centroid of each region
		int sizeX = labelArray.size(0);
		int sizeY = labelArray.size(1);
		for (int y = 0; y < sizeY; y++) 
		{
			for (int x = 0; x < sizeX; x++)
			{
				int label = labelArray.getInt(x, y);
				if (label == 0)
					continue;

				// do not process labels that are not in the input list 
				if (!labelIndices.containsKey(label))
					continue;
				
				int index = labelIndices.get(label);
				centroids[index][0] += x;
				centroids[index][1] += y;
				counts[index]++;
			}
		}

		// normalize by number of pixels in each region
		for (int i = 0; i < nLabels; i++)
		{
			centroids[i][0] /= counts[i];
			centroids[i][1] /= counts[i];
		}

		return centroids;
	}
	

	// ==================================================
	// Constructor

	/**
	 * Default constructor
	 */
	public Centroid2D()
	{
	}

	
	// ==================================================
	// Implementation of RegionAnalyzer interface

	/**
	 * Utility method that transforms the mapping between labels and Point2D
	 * instances into a Table that can be displayed within a GUI.
	 * 
	 * @param map
	 *            the mapping between labels and centroids
	 * @return a Table that can be displayed with ImageJ.
	 */
	public Table createTable(Map<Integer, Point2D> map)
	{
		// Initialize a new result table
		Table table = Table.create(map.size(), 2);
		table.setColumnNames(new String[] {"Centroid.X", "Centroid.Y"});
	
        // convert the (key, value) pairs in the map into a table with one row
        // per label
		int row = 0;
		for (int label : map.keySet())
		{
            table.setRowName(row, Integer.toString(label));

            // current diameter
			Point2D point = map.get(label);
			
            table.setValue(row, 0, point.x());
            table.setValue(row, 1, point.y());
            row++;
		}
	
		// setup meta-data
        table.setName("MaxFeret");
        table.getRowAxis().setName("Label");
        
		return table;
	}

	
	/**
     * Computes centroid of each region in input label image.
     * 
     * @param image
     *            the input image containing label of particles
     * @param labels
     *            the array of labels within the image
     * @param calib
     *            the (spatial) calibration of the image. Can be null, in that
     *            case default calibration will be used.
     * @return an array of Point2D representing the calibrated centroid
     *         coordinates
     */
	public Point2D[] analyzeRegions(IntArray2D<?> image, int[] labels, Calibration calib)
	{
		// size of image
		int sizeX = image.size(0);
		int sizeY = image.size(1);

		// Extract spatial calibration
		double sx = 1, sy = 1;
		double ox = 0, oy = 0;
		if (calib != null)
		{
		    ImageAxis xAxis = calib.getXAxis(); 
		    ImageAxis yAxis = calib.getYAxis(); 
            sx = xAxis.getSpacing();
            sy = yAxis.getSpacing();
            ox = xAxis.getOrigin();
            oy = yAxis.getOrigin();
		}
		
		// create associative array to know index of each label
        HashMap<Integer, Integer> labelIndices = LabelImages.mapLabelIndices(labels);

		// allocate memory for result
		int nLabels = labels.length;
		int[] counts = new int[nLabels];
		double[] cx = new double[nLabels];
		double[] cy = new double[nLabels];

    	fireStatusChanged(this, "Compute centroids");
		// compute centroid of each region
    	for (int y = 0; y < sizeY; y++) 
		{
			for (int x = 0; x < sizeX; x++)
			{
				int label = image.getInt(x, y);
				if (label == 0)
					continue;

				int index = labelIndices.get(label);
				cx[index] += x * sx;
				cy[index] += y * sy;
				counts[index]++;
			}
		}

		// normalize by number of pixels in each region
    	Point2D[] points = new Point2D[nLabels];
		for (int i = 0; i < nLabels; i++)
		{
			points[i] = new Point2D(cx[i] / counts[i] + ox, cy[i] / counts[i] + oy);
		}

		return points;
	}
}
