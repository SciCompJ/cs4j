/**
 * 
 */
package net.sci.image.analyze.region2d;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sci.array.binary.Binary;
import net.sci.array.binary.BinaryArray2D;
import net.sci.array.numeric.IntArray2D;
import net.sci.array.numeric.ScalarArray2D;
import net.sci.geom.geom2d.Point2D;
import net.sci.image.Calibration;
import net.sci.image.ImageAxis;
import net.sci.image.binary.distmap.ChamferMask2D;
import net.sci.image.label.LabelImages;
import net.sci.image.label.LabelValues;
import net.sci.image.label.LabelValues.PositionValuePair;
import net.sci.image.label.geoddist.ChamferGeodesicDistanceTransform2D;
import net.sci.image.label.geoddist.GeodesicDistanceTransform2DFloat32Hybrid;
import net.sci.table.Table;


/**
 * Computes the geodesic Diameter for each region of a binary or label image.
 * 
 * Computation is based on the geodesic propagation of a marker within each
 * region. Chamfer distances are used for propagating distances.
 *
 * @see net.sci.image.binary.distmap.ChamferMask2D
 * @see net.sci.image.label.geoddist.GeodesicDistanceTransform2D
 * 
 * @author dlegland
 */
public class GeodesicDiameter extends RegionAnalyzer2D<GeodesicDiameter.Result>
{
    // ==================================================
    // Class variables 
    
    /**
     * The algorithm used for computing geodesic distances.
     */
    ChamferGeodesicDistanceTransform2D geodesicDistanceTransform;

    boolean computePaths = false;
    
    
    // ==================================================
    // Constructors 

    /**
     * Empty constructor with default settings.
     */
    public GeodesicDiameter()
    {
        this(new GeodesicDistanceTransform2DFloat32Hybrid(ChamferMask2D.CHESSKNIGHT, true));
    }
    
    /**
     * Creates a new geodesic diameter computation operator.
     * 
     * @param weights
     *            the array of weights for orthogonal, diagonal, and eventually
     *            chess-knight moves neighbors
     */
    public GeodesicDiameter(ChamferMask2D mask) 
    {
        this(new GeodesicDistanceTransform2DFloat32Hybrid(mask, true));
    }
    
    /**
     * Creates a new geodesic diameter computation operator.
     * 
     * @param gdt
     *            the instance of Geodesic Distance Transform calculator used
     *            for propagating distances
     */
    public GeodesicDiameter(ChamferGeodesicDistanceTransform2D gdt) 
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
        ChamferMask2D mask = ChamferMask2D.fromWeights(weights);
        this.geodesicDistanceTransform = new GeodesicDistanceTransform2DFloat32Hybrid(mask, true);
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
        ChamferMask2D mask = ChamferMask2D.CHESSKNIGHT;
        ScalarArray2D<?> distanceMap = LabelImages.distanceMap2d(labelMap, mask, true, true);
    
        // Extract position of maxima
        PositionValuePair[] innerCircles = LabelValues.findMaxValues2d(labelMap, labels, distanceMap);
        
        // initialize marker image with position of maxima
        marker.fillValue(0);
        for (int i = 0; i < nLabels; i++) 
        {
            int[] pos = innerCircles[i].position();
            if (pos[0] == -1)
            {
                System.err.println(
                        "Could not find maximum for particle label " + labels[i]);
                continue;
            }
            marker.setBoolean(pos[0], pos[1], true);
        }
    
        this.fireStatusChanged(this, "Computing first geodesic extremities...");
    
        // Second distance propagation from first maximum
        distanceMap = geodesicDistanceTransform.process2d(marker, labelMap);
        
        // find position of maximal value for each label
        // this is expected to correspond to a geodesic extremity 
        int[][] firstGeodesicExtremities = LabelValues.maxValuePositions2d(labelMap, labels, distanceMap);
        
        // Create new marker image with position of maxima
        marker.fill(new Binary(false));
        for (int i = 0; i < nLabels; i++)
        {
            if (firstGeodesicExtremities[i][0] == -1) 
            {
                System.err.println(
                        "Could not find maximum for particle label " + labels[i]);
                continue;
            }
            marker.setBoolean(firstGeodesicExtremities[i][0], firstGeodesicExtremities[i][1], true);
        }
        
        this.fireStatusChanged(this, "Computing second geodesic extremities...");
    
        // third distance propagation from second maximum
        distanceMap = geodesicDistanceTransform.process2d(marker, labelMap);
        
        // also computes position of maxima
        PositionValuePair[] secondGeodesicExtremities = LabelValues.findMaxValues2d(labelMap, labels, distanceMap);
        
        // Create array of results and populate with computed values
        GeodesicDiameter.Result[] result = new GeodesicDiameter.Result[nLabels];
        for (int i = 0; i < nLabels; i++)
        {
            Result res = new Result();
                    
            // Get the maximum distance within each label, 
            // and add sqrt(2) to take into account maximum pixel thickness.
            res.diameter = secondGeodesicExtremities[i].value() + Math.sqrt(2);

            // also keep references to characteristic points
            res.initialPoint = point(innerCircles[i].position());
            res.innerRadius = innerCircles[i].value();
            res.firstExtremity = point(firstGeodesicExtremities[i]);
            res.secondExtremity = point(secondGeodesicExtremities[i].position());
            
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
                int[] pos1 = new int[] {(int) ext.x(), (int) ext.y()};
                
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
                int[] pos = new int[] {(int) ext.x(), (int) ext.y()};
                while (!(pos[0] == pos1[0] && pos[1] == pos1[1]))
                {
                    pos = findLowestNeighborPosition(labelMap, distanceMap, pos);
                    path.add(point(pos));
                }
                
                result[i].path = path;
            }
        }
        
        // calibrate the results
        if (calib.isCalibrated())
        {
            this.fireStatusChanged(this, "Re-calibrating results");
            for (int i = 0; i < nLabels; i++)
            {
                result[i] = result[i].recalibrate(calib);
            }
        }
        
        // returns the results
        return result;
    }
    
    private Point2D point(int[] pos)
    {
        return new Point2D(pos[0], pos[1]);
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
            table.setRowName(iRow, Integer.toString(label));
            
            // current diameter
            Result res = results.get(label);
            
            // add an entry to the resulting data table
            table.setValue(iRow, "GeodesicDiameter", res.diameter);
            
            // coordinates of max inscribed circle
            table.setValue(iRow, "Radius", res.innerRadius);
            table.setValue(iRow, "InitPoint.X", res.initialPoint.x());
            table.setValue(iRow, "InitPoint.Y", res.initialPoint.y());
            table.setValue(iRow, "GeodesicElongation", Math.max(res.diameter / (res.innerRadius * 2), 1.0));
            
            // coordinate of first and second geodesic extremities 
            table.setValue(iRow, "Extremity1.X", res.firstExtremity.x());
            table.setValue(iRow, "Extremity1.Y", res.firstExtremity.y());
            table.setValue(iRow, "Extremity2.X", res.secondExtremity.x());
            table.setValue(iRow, "Extremity2.Y", res.secondExtremity.y());
            
            iRow++;
        }
    
        // setup meta-data
        table.setName("GeodesicDiameter");
        table.getRowAxis().setName("Label");
        
        // return the created array
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
    private int[] findLowestNeighborPosition(IntArray2D<?> labelImage, ScalarArray2D<?> distanceMap, int[] pos)
    {
        // retrieve image size
        int sizeX = distanceMap.size(0);
        int sizeY = distanceMap.size(1);
        
        // retrieve current label and associated distance 
        int refLabel = labelImage.getInt(pos[0], pos[1]);
        double minDist = distanceMap.getValue(pos[0], pos[1]);

        // iterate over neighbors of current pixel
        int[] nextPos = pos;
        for (ChamferMask2D.Offset offset : this.geodesicDistanceTransform.mask().getOffsets())
        {
            // Compute neighbor coordinates
            int x = pos[0] + offset.dx;
            int y = pos[1] + offset.dy;

            // check neighbor is within image bounds
            if (x < 0 || x >= sizeX) continue;
            if (y < 0 || y >= sizeY) continue;

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
                nextPos = new int[] {x, y};
            }
        }

        if (nextPos.equals(pos))
        {
            throw new RuntimeException("Could not find a neighbor with smaller value at (" + pos[0] + "," + pos[1] + ")");
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
    public class Result
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

        /**
         * Computes the result corresponding to the spatial calibration. The
         * current result instance is not modified.
         * 
         * @param calib
         *            the spatial calibration of an image
         * @return the result after applying the spatial calibration
         */
        public Result recalibrate(Calibration calib)
        {
            double sx = calib.getXAxis().getSpacing();
            Result res = new Result();
            
            // calibrate the diameter
            res.diameter = this.diameter * sx;

            // calibrate inscribed disk
            res.initialPoint = calibrate(this.initialPoint, calib); 
            res.innerRadius = this.innerRadius * sx;

            // calibrate geodesic extremities
            res.firstExtremity = calibrate(this.firstExtremity, calib); 
            res.secondExtremity = calibrate(this.secondExtremity, calib);
            
            // calibrate the geodesic path if any
            if (this.path != null)
            {
                List<Point2D> newPath = new ArrayList<Point2D>(this.path.size());
                for (Point2D point : this.path)
                {
                    newPath.add(calibrate(point, calib));
                }
                res.path = newPath;
            }
            
            // return the calibrated result
            return res;
        }
        
        private Point2D calibrate(Point2D point, Calibration calib)
        {
            ImageAxis xAxis = calib.getXAxis();
            ImageAxis yAxis = calib.getYAxis();
            return new Point2D(
                    point.x() * xAxis.getSpacing() + xAxis.getOrigin(), 
                    point.y() * yAxis.getSpacing() + yAxis.getOrigin());
        }
    }
}
