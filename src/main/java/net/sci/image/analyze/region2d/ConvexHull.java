/**
 * 
 */
package net.sci.image.analyze.region2d;

import java.util.ArrayList;
import java.util.Map;

import net.sci.array.numeric.IntArray2D;
import net.sci.geom.geom2d.AffineTransform2D;
import net.sci.geom.geom2d.Point2D;
import net.sci.geom.geom2d.polygon.Polygon2D;
import net.sci.geom.geom2d.polygon.process.GiftWrappingConvexHull2D;
import net.sci.image.Calibration;
import net.sci.image.ImageAxis;
import net.sci.table.Table;
import net.sci.table.impl.DefaultTable;

/**
 * Compute the convex hull of each region within a label image and returns a
 * collection of polygons.
 * 
 * @see net.sci.geom.geom2d.polygon.Polygon2D
 * 
 * @author dlegland
 *
 */
public class ConvexHull extends RegionAnalyzer2D<Polygon2D>
{
    // ==================================================
    // Static methods
    
    /**
     * Computes the convex hull of each label in input image and returns the
     * result as an array of polygons.
     * 
     * @param labelImage
     *            the input image containing label of particles
     * @param labels
     *            the array of unique labels in image the number of directions
     *            to process, either 2 or 4
     * @param calib
     *            the calibration of the image
     * @return an array containing for each label, the convex hull of the
     *         corresponding region, in calibrated coordinates
     */
    public static final Polygon2D[] convexHull(IntArray2D<?> labelImage, int[] labels, Calibration calib) 
    {
        return new ConvexHull().analyzeRegions(labelImage, labels, calib);
    }
    
    /**
     * Computes the convex hull of each label in input image and returns the
     * result as an array of polygons. This version does not take into account
     * the spatial calibration, and returns the convex hulls in pixel coordinates.
     * 
     * @param labelArray
     *            the input image containing label of particles
     * @param labels
     *            the array of unique labels in image the number of directions
     *            to process, either 2 or 4
     * @return an array containing for each label, the convex hull of the
     *         corresponding region, in pixel coordinates
     */
    public static final Polygon2D[] convexHull(IntArray2D<?> labelArray, int[] labels) 
    {
        // create associative array to know index of each label
        int nLabels = labels.length;

        // allocate memory for result
        Polygon2D[] convexHulls = new Polygon2D[nLabels];
        
        ArrayList<Point2D>[] arrays = RegionBoundaries.boundaryPixelsMiddleEdges(labelArray, labels);
        
        GiftWrappingConvexHull2D algo = new GiftWrappingConvexHull2D();
        for (int i = 0; i < nLabels; i++)
        {
            convexHulls[i] = algo.process(arrays[i]);
        }

        return convexHulls;
    }
    
    // ==================================================
    // Constructor

    /**
     * Empty constructor.
     */
    public ConvexHull()
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
    public Table createTable(Map<Integer, Polygon2D> map)
    {
        // Initialize a new result table
        DefaultTable table = new DefaultTable(map.size(), 3);
        table.setColumnNames(new String[] {"Hull.Centroid.X", "Hull.Centroid.Y", "Hull.Area"});
    
        // Convert all results that were computed during execution of the
        // "computeGeodesicDistanceMap()" method into rows of the results table
        int i = 0;
        for (int label : map.keySet())
        {
            table.setRowName(i, Integer.toString(label));

            // current diameter
            Polygon2D poly = map.get(label);
            Point2D centroid = poly.centroid();
            table.setValue(i, 0, centroid.x());
            table.setValue(i, 1, centroid.y());
            table.setValue(i, 2, Math.abs(poly.signedArea()));
            i++;
        }
    
        return table;
    }

    
    /**
     * Computes the convex hull of each region in the input label image.
     * 
     * @param image
     *            the input image containing label of particles
     * @param labels
     *            the array of labels within the image
     * @param calib
     *            the calibration of the image
     * @return an array of Polygon2D representing the convex hull of each region in calibrated coordinates 
     */
    public Polygon2D[] analyzeRegions(IntArray2D<?> image, int[] labels, Calibration calib)
    {
//        // size of image
//        int sizeX = image.size(0);
//        int sizeY = image.size(1);
//
//        // create associative array to know index of each label
//        HashMap<Integer, Integer> labelIndices = LabelImages.mapLabelIndices(labels);
//        
//        int nLabels = labelIndices.size();
        
        Polygon2D[] convexHulls = convexHull(image, labels);

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
            
            AffineTransform2D sca = AffineTransform2D.createScaling(sx, sy);
            AffineTransform2D tra = AffineTransform2D.createTranslation(ox, oy);
            AffineTransform2D transfo = tra.compose(sca);
            
            for (int i = 0; i < labels.length; i++)
            {
                convexHulls[i] = convexHulls[i].transform(transfo);
            }
        }
        
        return convexHulls;
    }
}
