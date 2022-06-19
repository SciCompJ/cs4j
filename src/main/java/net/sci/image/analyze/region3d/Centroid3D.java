/**
 * 
 */
package net.sci.image.analyze.region3d;

import java.util.HashMap;
import java.util.Map;

import net.sci.array.scalar.IntArray3D;
import net.sci.axis.NumericalAxis;
import net.sci.geom.geom3d.Point3D;
import net.sci.image.Calibration;
import net.sci.image.label.LabelImages;
import net.sci.table.DefaultTable;
import net.sci.table.Table;


/**
 * Computes centroid position of regions within binary or label images.
 * 
 * @author dlegland
 *
 */
public class Centroid3D extends RegionAnalyzer3D<Point3D>
{
	// ==================================================
	// Static methods
	
	/**
	 * Computes centroid of each region in input image and returns the result as
	 * an array of double for each label.
	 * 
	 * @param labelImage
	 *            the input image containing label of regions
	 * @param labels
     *            the array of labels to process within image
	 * @param calib
	 *            the calibration of the image
	 * @return an array containing for each region, the coordinates of the
	 *         centroid, in calibrated coordinates
	 */
	public static final Point3D[] centroids(IntArray3D<?> labelImage, int[] labels, Calibration calib) 
	{
		return new Centroid3D().analyzeRegions(labelImage, labels, calib);
	}
	
	/**
	 * Computes centroid of each region in input image and returns the result as
	 * an array of double for each label. This version does not take into
	 * account the spatial calibration, and returns the centroids in pixel
	 * coordinates.
	 * 
	 * @param labelArray
	 *            the input image containing label of regions
	 * @param labels
	 *            the array of labels to process within image
	 * @return an array containing for each region, the coordinates of the
	 *         centroid, in pixel coordinates
	 */
	public static final double[][] centroidCoordinates(IntArray3D<?> labelArray, int[] labels) 
	{
		// create associative array to know index of each label
		int nLabels = labels.length;
        HashMap<Integer, Integer> labelIndices = LabelImages.mapLabelIndices(labels);

		// allocate memory for result
		int[] counts = new int[nLabels];
		double[][] centroids = new double[nLabels][3];
		
		// retrieve array dimensions
		int sizeX = labelArray.size(0);
        int sizeY = labelArray.size(1);
        int sizeZ = labelArray.size(2);
        
        // compute centroid of each region
        for (int z = 0; z < sizeZ; z++) 
        {
            for (int y = 0; y < sizeY; y++) 
            {
                for (int x = 0; x < sizeX; x++)
                {
                    int label = labelArray.getInt(x, y, z);
                    if (label == 0)
                        continue;

                    // do not process labels that are not in the input list 
                    if (!labelIndices.containsKey(label))
                        continue;

                    int index = labelIndices.get(label);
                    centroids[index][0] += x;
                    centroids[index][1] += y;
                    centroids[index][2] += z;
                    counts[index]++;
                }
            }
        }

		// normalize by number of pixels in each region
		for (int i = 0; i < nLabels; i++)
		{
			centroids[i][0] /= counts[i];
            centroids[i][1] /= counts[i];
            centroids[i][2] /= counts[i];
		}

		return centroids;
	}

	
	// ==================================================
	// Implementation of RegionAnalyzer interface

	/**
	 * Utility method that transforms the mapping between labels and Point3D
	 * instances into a Table that can be displayed within a GUI.
	 * 
	 * @param map
	 *            the mapping between labels and centroids
	 * @return a Table that can be displayed with ImageJ.
	 */
	public Table createTable(Map<Integer, Point3D> map)
	{
		// Initialize a new result table
		DefaultTable table = new DefaultTable(map.size(), 3);
		table.setColumnNames(new String[] {"Centroid.X", "Centroid.Y", "Centroid.Z"});
	
		// Convert all results that were computed during execution of the
		// "computeGeodesicDistanceMap()" method into rows of the results table
		int i = 0;
		for (int label : map.keySet())
		{
            table.setRowName(i, Integer.toString(label));

            // current diameter
			Point3D point = map.get(label);
			
            table.setValue(i, 0, point.getX());
            table.setValue(i, 1, point.getY());
            table.setValue(i, 2, point.getZ());
            i++;
		}
	
		return table;
	}

	
	/**
     * Computes centroid of each region in input label image.
     * 
     * @param array
     *            the input array containing label of regions
     * @param labels
     *            the array of labels to compute
     * @param calib
     *            the calibration of the image
     * @return an array of Point3D representing the calibrated centroid
     *         coordinates for each region to process
     */
	public Point3D[] analyzeRegions(IntArray3D<?> array, int[] labels, Calibration calib)
	{
		// Extract spatial calibration
		double sx = 1, sy = 1, sz = 1;
		double ox = 0, oy = 0, oz = 1;
		if (calib != null)
		{
		    NumericalAxis xAxis = calib.getXAxis(); 
            NumericalAxis yAxis = calib.getYAxis(); 
            NumericalAxis zAxis = calib.getZAxis(); 
            sx = xAxis.getSpacing();
            sy = yAxis.getSpacing();
            sz = zAxis.getSpacing();
            ox = xAxis.getOrigin();
            oy = yAxis.getOrigin();
            oz = zAxis.getOrigin();
		}

		// allocate memory for result
		int nLabels = labels.length;

		// compute centroid of each region
    	fireStatusChanged(this, "Compute centroids");
        double[][] coords = centroidCoordinates(array, labels);
        
		// normalize by number of pixels in each region
    	Point3D[] points = new Point3D[nLabels];
		for (int i = 0; i < nLabels; i++)
		{
			points[i] = new Point3D(coords[i][0] * sx + ox, coords[i][1] * sy + oy, coords[i][2] * sz + oz);
		}

		return points;
	}
}
