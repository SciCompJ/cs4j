/**
 * 
 */
package net.sci.image.analyze.region2d;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;


import net.sci.array.scalar.IntArray2D;
import net.sci.geom.geom2d.Point2D;
import net.sci.geom.geom2d.polygon.OrientedBox2D;
import net.sci.geom.geom2d.polygon.Polygon2D;
import net.sci.geom.geom2d.polygon.Polygons2D;
import net.sci.image.Calibration;
import net.sci.image.ImageAxis;
import net.sci.table.Table;

/**
 * @author dlegland
 *
 */
public class OrientedBoundingBox2D extends RegionAnalyzer2D<OrientedBox2D>
{
    // ====================================================
    // Static methods

    /**
     * Computes the object-oriented bounding box of a set of points.
     * 
     * @param points
     *            a list of points (not necessarily ordered)
     * @return the oriented box of this set of points.
     */
    public static final OrientedBox2D orientedBoundingBox(ArrayList<? extends Point2D> points)
    {
        // Compute convex hull to reduce complexity
        Polygon2D convexHull = Polygons2D.convexHull(points);
        
        // compute convex hull centroid
        Point2D center = convexHull.centroid();
        double cx = center.getX();
        double cy = center.getY();
        
        List<Point2D> vertices = List.copyOf(convexHull.vertexPositions());
        AngleDiameterPair minFeret = FeretDiameters.minFeretDiameter(vertices);
        
        // recenter the convex hull
        ArrayList<Point2D> centeredHull = new ArrayList<Point2D>(vertices.size());
        for (Point2D p : vertices)
        {
            centeredHull.add(p.translate(-cx, -cy));
        }
        
        // orientation of the main axis
        // pre-compute trigonometric functions
        double cot = Math.cos(minFeret.angle);
        double sit = Math.sin(minFeret.angle);

        // compute elongation in direction of rectangle length and width
        double xmin = Double.MAX_VALUE;
        double ymin = Double.MAX_VALUE;
        double xmax = Double.MIN_VALUE;
        double ymax = Double.MIN_VALUE;
        for (Point2D p : centeredHull)
        {
            // coordinates of current point
            double x = p.getX(); 
            double y = p.getY();
            
            // compute rotated coordinates
            double x2 = x * cot + y * sit; 
            double y2 = - x * sit + y * cot;
            
            // update bounding box
            xmin = Math.min(xmin, x2);
            ymin = Math.min(ymin, y2);
            xmax = Math.max(xmax, x2);
            ymax = Math.max(ymax, y2);
        }
        
        // position of the center with respect to the centroid computed before
        double dl = (xmax + xmin) / 2;
        double dw = (ymax + ymin) / 2;

        // change coordinates from rectangle to user-space
        double dx  = dl * cot - dw * sit;
        double dy  = dl * sit + dw * cot;

        // coordinates of oriented box center
        cx += dx;
        cy += dy;

        // size of the rectangle
        double length = ymax - ymin;
        double width  = xmax - xmin;
        
        // store angle in degrees, between 0 and 180
        double angle = (Math.toDegrees(minFeret.angle) + 270) % 180;

        // Store results in a new instance of OrientedBox2D
        return new OrientedBox2D(cx, cy, length, width, angle);
    }
    
    /**
     * Computes the object-oriented bounding box of a set of points, computing
     * convex hull in pixel coordinates. Due to numerical computation, this
     * versions is usually more stable than computing the convex hull on the
     * calibrated points.
     * 
     * @param points
     *            a list of points, in pixel coordinates
     * @param calib
     *            the spatial calibration of the points
     * @return the oriented box of this set of points, in calibrated coordinates
     */
    public static final OrientedBox2D orientedBoundingBox(ArrayList<? extends Point2D> points, Calibration calib)
    {
        // Compute convex hull to reduce complexity
        Polygon2D convexHull = Polygons2D.convexHull(points);
        
        Polygon2D calibratedHull = Polygon2D.create(calibrate(convexHull.vertexPositions(), calib));
                
        // compute convex hull centroid
        Point2D center = calibratedHull.centroid();
        double cx = center.getX();
        double cy = center.getY();
        
        // coordinates of convex hull after spatial calibration and recentering
        ArrayList<Point2D> centeredHull = new ArrayList<Point2D>(convexHull.vertexCount());
        for (Point2D p : calibratedHull.vertexPositions())
        {
            centeredHull.add(p.translate(-cx, -cy));
        }

        AngleDiameterPair minFeret = FeretDiameters.minFeretDiameter(centeredHull);
        
        // orientation of the main axis
        // pre-compute trigonometric functions
        double cot = Math.cos(minFeret.angle);
        double sit = Math.sin(minFeret.angle);

        // compute elongation in direction of rectangle length and width
        double xmin = Double.MAX_VALUE;
        double ymin = Double.MAX_VALUE;
        double xmax = Double.MIN_VALUE;
        double ymax = Double.MIN_VALUE;
        for (Point2D p : centeredHull)
        {
            // coordinates of current point
            double x = p.getX(); 
            double y = p.getY();
            
            // compute rotated coordinates
            double x2 = x * cot + y * sit; 
            double y2 = - x * sit + y * cot;
            
            // update bounding box
            xmin = Math.min(xmin, x2);
            ymin = Math.min(ymin, y2);
            xmax = Math.max(xmax, x2);
            ymax = Math.max(ymax, y2);
        }
        
        // position of the center with respect to the centroid computed before
        double dl = (xmax + xmin) / 2;
        double dw = (ymax + ymin) / 2;

        // change coordinates from rectangle to user-space
        double dx  = dl * cot - dw * sit;
        double dy  = dl * sit + dw * cot;

        // coordinates of oriented box center
        cx += dx;
        cy += dy;

        // size of the rectangle
        double length = ymax - ymin;
        double width  = xmax - xmin;
        
        // store angle in degrees, between 0 and 180
        double angle = (Math.toDegrees(minFeret.angle) + 270) % 180;

        // Store results in a new instance of OrientedBox2D
        return new OrientedBox2D(cx, cy, length, width, angle);
    }

    private static final Collection<Point2D> calibrate(Collection<Point2D> points, Calibration calib)
    {
        if (!calib.isCalibrated())
        {
            return points;
        }
        
        ImageAxis xAxis = calib.getAxis(0);
        double ox = xAxis.getOrigin();
        double sx = xAxis.getSpacing();
        ImageAxis yAxis = calib.getAxis(1);
        double oy = yAxis.getOrigin();
        double sy = yAxis.getSpacing();
        
        ArrayList<Point2D> res = new ArrayList<Point2D>(points.size());
        for (Point2D point : points)
        {
            double x = point.getX() * sx + ox;
            double y = point.getY() * sy + oy;
            res.add(new Point2D(x, y));
        }
        return res;
    }
    

    // ==================================================
    // Implementation of RegionAnalyzer interface

    @Override
    public Table createTable(Map<Integer, OrientedBox2D> results)
    {
        // Create data table
        String[] colNames = new String[] { 
                "Label",
                "Box.CenterX", 
                "Box.CenterY",
                "Box.Size1",
                "Box.Size2",
                "Box.Orientation"};
        Table table = Table.create(results.size(), colNames);

        // compute ellipse parameters for each region
        int iRow = 0;
        for (int label : results.keySet()) 
        {
            // add an entry to the resulting data table
            int c = 0;
            table.setValue(iRow, c++, label);
            
            // add coordinates of origin pixel (IJ coordinate system)
            OrientedBox2D box = results.get(label);
            Point2D center = box.center();
            table.setValue(iRow, c++, center.getX());
            table.setValue(iRow, c++, center.getY());
            table.setValue(iRow, c++, box.size1());
            table.setValue(iRow, c++, box.size2());
            table.setValue(iRow, c++, box.orientation());
            
            // update for next row
            iRow++;
        }
        
        // return the created array
        return table;
    }

    @Override
    public OrientedBox2D[] analyzeRegions(IntArray2D<?> array, int[] labels, Calibration calib)
    {
        // Check validity of parameters
        if (array == null) return null;
        
        // For each label, create a list of corner points
        fireStatusChanged(this, "Find Label Corner Points");
        ArrayList<Point2D>[] cornerPointsArrays = RegionBoundaries.runlengthsCorners(array, labels);
                
        // allocate memory for result
        int nLabels = labels.length;
        OrientedBox2D[] boxes = new OrientedBox2D[nLabels];

        // Compute the oriented box of each set of corner points
        this.fireStatusChanged(this, "Compute oriented boxes");
        for (int i = 0; i < nLabels; i++)
        {
            this.fireProgressChanged(this, i, nLabels);
            boxes[i] = orientedBoundingBox(cornerPointsArrays[i], calib);
        }
        
        this.fireStatusChanged(this, "");
        this.fireProgressChanged(this, 1, 1);
        
        return boxes;
    }
}
