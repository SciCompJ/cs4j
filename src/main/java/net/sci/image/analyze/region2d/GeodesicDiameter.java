/**
 * 
 */
package net.sci.image.analyze.region2d;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sci.array.scalar.Binary;
import net.sci.array.scalar.BinaryArray2D;
import net.sci.array.scalar.IntArray2D;
import net.sci.array.scalar.ScalarArray2D;
import net.sci.geom.geom2d.Point2D;
import net.sci.image.Calibration;
import net.sci.image.binary.ChamferWeights2D;
import net.sci.image.data.Cursor2D;
import net.sci.image.label.LabelImages;
import net.sci.image.label.LabelValues;
import net.sci.image.label.LabelValues.PositionValuePair2D;
import net.sci.image.label.geoddist.GeodesicDistanceTransform2D;
import net.sci.image.label.geoddist.GeodesicDistanceTransform2DFloat32Hybrid5x5;
import net.sci.table.Table;


/**
 * @author dlegland
 *
 */
public class GeodesicDiameter extends  RegionAnalyzer2D<GeodesicDiameter.Result>
{
    // ==================================================
    // Class variables 
    
    /**
     * The algorithm used for computing geodesic distances.
     */
    GeodesicDistanceTransform2D geodesicDistanceTransform;

    boolean computePaths = false;
    
    /**
     * An array of shifts corresponding to the weights, for computing geodesic
     * longest paths.
     * 
     * Assumes computation of distance in a 5-by-5 neighborhood.
     */
    int[][] shifts = new int[][]{
                  {-1, -2}, {0, -2}, {+1, -2},  
        {-2, -1}, {-1, -1}, {0, -1}, {+1, -1}, {+2, -1}, 
        {-2,  0}, {-1,  0},          {+1,  0}, {+2,  0},  
        {-2, +1}, {-1, +1}, {0, +1}, {+1, +1}, {+2, +1},  
                  {-1, +2}, {0, +2}, {+1, +2},  
    };

    
    // ==================================================
    // Constructors 

    /**
     * Empty constructor with default settings.
     */
    public GeodesicDiameter()
    {
        this(new GeodesicDistanceTransform2DFloat32Hybrid5x5(ChamferWeights2D.CHESSKNIGHT.getFloatWeights(), true));
    }
    
    /**
     * Creates a new geodesic diameter computation operator.
     * 
     * @param weights
     *            the array of weights for orthogonal, diagonal, and eventually
     *            chess-knight moves neighbors
     */
    public GeodesicDiameter(ChamferWeights2D weights) 
    {
        this(new GeodesicDistanceTransform2DFloat32Hybrid5x5(weights, true));
    }
    
    /**
     * Creates a new geodesic diameter computation operator.
     * 
     * @param weights
     *            the array of weights for orthogonal, diagonal, and eventually
     *            chess-knight moves neighbors
     */
    public GeodesicDiameter(float[] weights) 
    {
        this(new GeodesicDistanceTransform2DFloat32Hybrid5x5(weights, true));
    }
    
    /**
     * Creates a new geodesic diameter computation operator.
     * 
     * @param gdt
     *            the instance of Geodesic Distance Transform calculator used
     *            for propagating distances
     */
    public GeodesicDiameter(GeodesicDistanceTransform2D gdt) 
    {
        this.geodesicDistanceTransform = gdt;
    }
    
    
    // ==================================================
    // Setters/Getters
    
    public boolean getComputePaths()
    {
        return this.computePaths;
    }
    
    public void setComputePaths(boolean bool)
    {
        this.computePaths = bool;
    }

    public void setChamferWeights(float[] weights)
    {
        this.geodesicDistanceTransform = new GeodesicDistanceTransform2DFloat32Hybrid5x5(weights, true);
    }


    // ==================================================
    // Implementation of the RegionAnalyzer2D class
    
    @Override
    public Result[] analyzeRegions(IntArray2D<?> labelMap, int[] labels,
            Calibration calib)
    {
        // Intitial check-up
        if (calib.getXAxis().getSpacing() != calib.getYAxis().getSpacing())
        {
            throw new RuntimeException("Requires image with square pixels");
        }

        // number of labels to process
        int nLabels = labels.length;
        
        // Create new marker image
        int sizeX = labelMap.size(0);
        int sizeY = labelMap.size(1);
        BinaryArray2D marker = BinaryArray2D.create(sizeX, sizeY);
        
        // Compute distance map from label borders to identify centers
        // (The distance map correctly processes adjacent borders)
        this.fireStatusChanged(this, "Initializing pseudo geodesic centers...");
        float[] weights = ChamferWeights2D.CHESSKNIGHT.getFloatWeights();
        ScalarArray2D<?> distanceMap = LabelImages.distanceMap2d(labelMap, weights, true);
    
        // Extract position of maxima
        PositionValuePair2D[] innerCircles = LabelValues.findMaxValues2d(labelMap, labels, distanceMap);
        
        // initialize marker image with position of maxima
        marker.fillValue(0);
        for (int i = 0; i < nLabels; i++) 
        {
            Cursor2D pos = innerCircles[i].getPosition();
            if (pos.getX() == -1)
            {
                System.err.println(
                        "Could not find maximum for particle label " + labels[i]);
                continue;
            }
            marker.setBoolean(pos.getX(), pos.getY(), true);
        }
    
        this.fireStatusChanged(this, "Computing first geodesic extremities...");
    
        // Second distance propagation from first maximum
        distanceMap = geodesicDistanceTransform.process2d(marker, labelMap);
        
        // find position of maximal value for each label
        // this is expected to correspond to a geodesic extremity 
        Cursor2D[] firstGeodesicExtremities = LabelValues.maxValuePositions2d(labelMap, labels, distanceMap);
        
        // Create new marker image with position of maxima
        marker.fill(new Binary(false));
        for (int i = 0; i < nLabels; i++)
        {
            if (firstGeodesicExtremities[i].getX() == -1) 
            {
                System.err.println(
                        "Could not find maximum for particle label " + labels[i]);
                continue;
            }
            marker.setBoolean(firstGeodesicExtremities[i].getX(), firstGeodesicExtremities[i].getY(), true);
        }
        
        this.fireStatusChanged(this, "Computing second geodesic extremities...");
    
        // third distance propagation from second maximum
        distanceMap = geodesicDistanceTransform.process2d(marker, labelMap);
        
        // also computes position of maxima
        PositionValuePair2D[] secondGeodesicExtremities = LabelValues.findMaxValues2d(labelMap, labels, distanceMap);
        
        // Create array of results and populate with computed values
        GeodesicDiameter.Result[] result = new GeodesicDiameter.Result[nLabels];
        for (int i = 0; i < nLabels; i++)
        {
            Result res = new Result();
                    
            // Get the maximum distance within each label, 
            // and add sqrt(2) to take into account maximum pixel thickness.
            res.diameter = secondGeodesicExtremities[i].getValue() + Math.sqrt(2);

            // also keep references to characteristic points
            res.initialPoint = point(innerCircles[i].getPosition());
            res.innerRadius = innerCircles[i].getValue();
            res.firstExtremity = point(firstGeodesicExtremities[i]);
            res.secondExtremity = point(secondGeodesicExtremities[i].getPosition());
            
            // store the result
            result[i] = res;
        }
        
        if (computePaths)
        {
            this.fireStatusChanged(this, "Computing geodesic paths...");

            // compute paths starting from points with larger distance value
            for (int i = 0; i < nLabels; i++)
            {
                
                // Current first geodesic extremity 
                // (corresponding to the minimum of the geodesic distance map)
                Point2D ext = result[i].firstExtremity;
                Cursor2D pos1 = new Cursor2D((int) ext.getX(), (int) ext.getX()) ;
                
                // Create new path
                List<Point2D> path = new ArrayList<Point2D>();
                
                // if the geodesic diameter of the current label is infinite, it is
                // not possible to create a path
                // -> use an empty path
                if (Double.isInfinite(result[i].diameter))
                {
                    result[i].path = path;
                    continue;
                }
                
                // initialize path with position of second geodesic extremity
                // (corresponding to the maximum of the geodesic distance map)
                ext = result[i].secondExtremity;
                path.add(ext);
                
                // iterate over neighbors of current position until we reach the minimum value
                Cursor2D pos = new Cursor2D((int) ext.getX(), (int) ext.getY());
                while (!pos.equals(pos1))
                {
                    pos = findLowestNeighborPosition(labelMap, distanceMap, pos);
                    path.add(point(pos));
                }
                
                result[i].path = path;
            }
        }
        // TODO: manage calibration
//        // calibrate the results
//        if (calib.scaled())
//        {
//            this.fireStatusChanged(this, "Re-calibrating results");
//            for (int i = 0; i < nLabels; i++)
//            {
//                result[i] = result[i].recalibrate(calib);
//            }
//        }
        
        // returns the results
        return result;
    }
    
    private Point2D point(Cursor2D cursor)
    {
        return new Point2D(cursor.getX(), cursor.getY());
    }

    @Override
    public Table createTable(Map<Integer, Result> results)
    {
        // Initialize a new result table
        Table table = Table.create(results.size(), 10);
        table.setColumnNames(new String[] { 
                "Label", "GeodesicDiameter", "Radius", 
                "InitPoint.X", "InitPoint.Y", "GeodesicElongation",
                "Extremity1.X", "Extremity1.Y", "Extremity2.X", "Extremity2.Y" });
    
        // Convert all results that were computed during execution of the
        // "analyzeRegions(...)" method into rows of the results table
        int iRow = 0;
        for (int label : results.keySet())
        {
            // current diameter
            Result res = results.get(label);
            
            // add an entry to the resulting data table
            table.setValue(iRow, "Label", label);
            table.setValue(iRow, "GeodesicDiameter", res.diameter);
            
            // coordinates of max inscribed circle
            table.setValue(iRow, "Radius", res.innerRadius);
            table.setValue(iRow, "InitPoint.X", res.initialPoint.getX());
            table.setValue(iRow, "InitPoint.Y", res.initialPoint.getY());
            table.setValue(iRow, "GeodesicElongation", Math.max(res.diameter / (res.innerRadius * 2), 1.0));
            
            // coordinate of first and second geodesic extremities 
            table.setValue(iRow, "Extremity1.X", res.firstExtremity.getX());
            table.setValue(iRow, "Extremity1.Y", res.firstExtremity.getY());
            table.setValue(iRow, "Extremity2.X", res.secondExtremity.getX());
            table.setValue(iRow, "Extremity2.Y", res.secondExtremity.getY());
            
            iRow++;
        }
    
        return table;
    }

    /**
     * Finds the position of the pixel in the neighborhood of pos that have the
     * smallest distance and that belongs to the same label as initial position.
     * 
     * @param pos
     *            the position of the reference pixel
     * @return the position of the neighbor with smallest value
     */
    private Cursor2D findLowestNeighborPosition(IntArray2D<?> labelImage, ScalarArray2D<?> distanceMap, Cursor2D pos)
    {
        int refLabel = labelImage.getInt(pos.getX(), pos.getY());
        double minDist = distanceMap.getValue(pos.getX(), pos.getY());

        // size of image
        int sizeX = distanceMap.size(0);
        int sizeY = distanceMap.size(1);

        // iterate over neighbors of current pixel
        Cursor2D nextPos = pos;
        for (int[] shift : shifts)
        {
            // Compute neighbor coordinates
            int x = pos.getX() + shift[0];
            int y = pos.getY() + shift[1];

            // check neighbor is within image bounds
            if (x < 0 || x >= sizeX)
            {
                continue;
            }
            if (y < 0 || y >= sizeY)
            {
                continue;
            }

            // ensure we stay within the same label
            if (labelImage.getInt(x, y) != refLabel)
            {
                continue;
            }

            // compute neighbor value, and compare with current min
            double dist = distanceMap.getValue(x, y);
            if (dist < minDist)
            {
                minDist = dist;
                nextPos = new Cursor2D(x, y);
            }
        }

        if (nextPos.equals(pos))
        {
            throw new RuntimeException("Could not find a neighbor with smaller value at (" + pos.getX() + "," + pos.getY() + ")");
        }

        return nextPos;
    }

    
    // ==================================================
    // Inner class used for representing computation results
    
    /**
     * Inner class used for representing results of geodesic diameters
     * computations. Each instance corresponds to a single region / particle.
     * 
     * @author dlegland
     */
    class Result
    {
        /** The geodesic diameter of the region. */
        public double diameter;

        /**
         * The initial point used for propagating distances, corresponding to the
         * center of one of the minimum inscribed circles.
         */
        public Point2D initialPoint;

        /**
         * The radius of the largest inner circle. Value may depends on the chamfer weights.
         */
        public double innerRadius;

        /**
         * The first geodesic extremity found by the algorithm.
         */
        public Point2D firstExtremity;

        /**
         * The second geodesic extremity found by the algorithm.
         */
        public Point2D secondExtremity;

        /**
         * The largest geodesic path within the particle, joining the first and
         * the second geodesic extremities. Its computation is optional.
         */
        public List<Point2D> path = null;

//        /**
//         * Computes the result corresponding to the spatial calibration. The
//         * current result instance is not modified.
//         * 
//         * @param calib
//         *            the spatial calibration of an image
//         * @return the result after applying the spatial calibration
//         */
//        public Result recalibrate(Calibration calib)
//        {
//            double size = calib.pixelWidth;
//            Result res = new Result();
//            
//            // calibrate the diameter
//            res.diameter = this.diameter * size;
//
//            // calibrate inscribed disk
//            res.initialPoint = calibrate(this.initialPoint, calib); 
//            res.innerRadius = this.innerRadius * size;
//
//            // calibrate geodesic extremities
//            res.firstExtremity = calibrate(this.firstExtremity, calib); 
//            res.secondExtremity = calibrate(this.secondExtremity, calib);
//            
//            // calibrate the geodesic path if any
//            if (this.path != null)
//            {
//                List<Point2D> newPath = new ArrayList<Point2D>(this.path.size());
//                for (Point2D point : this.path)
//                {
//                    newPath.add(calibrate(point, calib));
//                }
//                res.path = newPath;
//            }
//            
//            // return the calibrated result
//            return res;
//        }
        
//        private Point2D calibrate(Point2D point, Calibration calib)
//        {
//            return new Point2D.Double(
//                    point.getX() * calib.pixelWidth + calib.xOrigin, 
//                    point.getY() * calib.pixelHeight + calib.yOrigin);
//        }
    }
}
