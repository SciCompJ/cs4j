/**
 * 
 */
package net.sci.image.analyze.region2d;

import java.util.ArrayList;
import java.util.Map;

import net.sci.array.numeric.IntArray2D;
import net.sci.geom.geom2d.Point2D;
import net.sci.geom.geom2d.polygon.Polygon2D;
import net.sci.geom.geom2d.polygon.Polygons2D;
import net.sci.image.Calibration;
import net.sci.image.ImageAxis;
import net.sci.table.Table;

/**
 * Computes maximum Feret Diameter for each region of a binary or label image.
 * 
 * @see FeretDiameters2D
 * @see PointPair2D
 * 
 * @author dlegland
 *
 */
public class MaxFeretDiameter extends RegionAnalyzer2D<PointPair2D>
{
	// ==================================================
	// Static methods 
	
	public final static PointPair2D[] maxFeretDiameters(IntArray2D<?> image, int[] labels, Calibration calib)
	{
		return new MaxFeretDiameter().analyzeRegions(image, labels, calib);
	}
	
	
	// ==================================================
	// Constructor

	/**
	 * Default constructor
	 */
	public MaxFeretDiameter()
	{
	}

	// ==================================================
	// Implementation of RegionAnalyzer interface

	/**
	 * Converts the result of maximum Feret diameters computation to a
	 * ResultsTable that can be displayed within ImageJ.
	 * 
	 * @param maxDiamsMap
	 *            the map of PointPair2D for each label within a label image
	 * @return a ResultsTable instance
	 */
	public Table createTable(Map<Integer, PointPair2D> results)
	{
		// Create data table
        String[] colNames = new String[] { "Label", "FeretDiameter.Max", "Orientation", "P1.X", "P1.Y", "P2.X",
                "P2.Y" };
        Table table = Table.create(results.size(), colNames);

		// compute ellipse parameters for each region
	    int iRow = 0;
        for (int label : results.keySet()) 
		{
            // add an entry to the resulting data table
            table.setRowName(iRow, Integer.toString(label));
			
			// add coordinates of origin pixel (IJ coordinate system)
			PointPair2D maxDiam = results.get(label);
			table.setValue(iRow, "FeretDiameter.Max", maxDiam.diameter());
			table.setValue(iRow, "Orientation", Math.toDegrees(maxDiam.angle()));
			table.setValue(iRow, "P1.X", maxDiam.p1.x());
			table.setValue(iRow, "P1.Y", maxDiam.p1.y());
			table.setValue(iRow, "P2.X", maxDiam.p2.x());
			table.setValue(iRow, "P2.Y", maxDiam.p2.y());
			
			// update for next row
			iRow++;
		}
		
        // setup meta-data
        table.setName("MaxFeret");
        table.getRowAxis().setName("Label");
        
        // return the created array
        return table;
	}
	
	/**
	 * Computes maximum Feret Diameter for each label of the input label image.
	 * 
	 * Computes diameter between corners of image pixels, so the result is
	 * always greater than or equal to one.
	 * 
	 * @param image
	 *            a label image (8, 16 or 32 bits)
	 * @param labels
	 *            the set of labels within the image
	 * @param calib
	 *            the spatial calibration of the image
	 * @return an array of PointPair2D representing the coordinates of extreme
	 *         points, in calibrated coordinates.
	 */
	public PointPair2D[] analyzeRegions(IntArray2D<?> image, int[] labels, Calibration calib)
	{
		// Check validity of parameters
		if (image == null)
			return null;

		// Extract spatial calibration of image
		double sx = 1, sy = 1;
		double ox = 0, oy = 0;
		if (calib.isCalibrated())
		{
		    ImageAxis xAxis = calib.getXAxis();
		    sx = xAxis.getSpacing();
            ox = xAxis.getOrigin();
            ImageAxis yAxis = calib.getYAxis();
            sy = yAxis.getSpacing();
            oy = yAxis.getOrigin();
		}
		
		int nLabels = labels.length;

        // For each label, create a list of corner points
		fireStatusChanged(this, "Find Label Corner Points");
        ArrayList<Point2D>[] cornerPointsArrays = RegionBoundaries.runlengthsCorners(image, labels);
                
        // Compute the oriented box of each set of corner points
        PointPair2D[] labelMaxDiams = new PointPair2D[nLabels];
        fireStatusChanged(this, "Compute feret Diameters");
        for (int i = 0; i < nLabels; i++)
        {
        	this.fireProgressChanged(this, i, nLabels);
        	
        	// retrieve corners
        	ArrayList<Point2D> corners = cornerPointsArrays[i];
        	
        	// simplify by computing the convex hull
        	Polygon2D convHull = Polygons2D.convexHull(corners);
        	
    		// calibrate coordinates of hull vertices
        	corners.clear();
    		for (Point2D vertex : convHull.vertexPositions())
    		{
    			corners.add(new Point2D(vertex.x() * sx + ox, vertex.y() * sy + oy));
    		}

    		// compute Feret diameter of calibrated hull
        	labelMaxDiams[i] = FeretDiameters.maxFeretDiameter(corners);
        }
        
        fireProgressChanged(this, 1, 1);
        fireStatusChanged(this, "");
        return labelMaxDiams;
	}
	
}
