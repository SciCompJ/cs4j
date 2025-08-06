/**
 * 
 */
package net.sci.image.regionfeatures.morpho2d.core;

import java.util.Arrays;
import java.util.Collection;

import net.sci.algo.AlgoStub;
import net.sci.array.Array;
import net.sci.array.binary.BinaryArray2D;
import net.sci.array.numeric.Float32Array2D;
import net.sci.array.numeric.Int;
import net.sci.array.numeric.IntArray;
import net.sci.array.numeric.IntArray2D;
import net.sci.image.Calibration;
import net.sci.image.binary.distmap.ChamferMask2D;
import net.sci.image.label.LabelValues;
import net.sci.image.label.LabelValues.PositionValuePair;
import net.sci.image.label.geoddist.GeodesicDistanceTransform2DFloat32Hybrid;
import net.sci.image.regionfeatures.Feature;
import net.sci.image.regionfeatures.RegionFeatures;
import net.sci.image.regionfeatures.RegionTabularFeature;
import net.sci.table.NumericColumn;
import net.sci.table.Table;

/**
 * Data to compute geodesic diameter. Stores the results in pixel coordinates.
 */
public class GeodesicDiameterData extends AlgoStub implements RegionTabularFeature
{
    /**
     * The names of the columns, without unit name.
     */
    private static final String[] colNames = new String[] {
            "Geodesic_Diameter", 
            "Initial_Point_X",
            "Initial_Point_Y",
            "Geodesic_Extremity1_X",
            "Geodesic_Extremity1_Y",
            "Geodesic_Extremity2_X",
            "Geodesic_Extremity2_Y"};

    /**
     * Default empty constructor.
     */
    public GeodesicDiameterData()
    {
    }
    

    @Override
    public Object compute(RegionFeatures data)
    {
        // retrieve label map and list of labels
        @SuppressWarnings({ "rawtypes", "unchecked" })
        IntArray2D labelMap = IntArray2D.wrap(IntArray.wrap((Array<Int>) data.labelMap.getData()));
        int[] labels = data.labels;
        int nLabels = labels.length;
        
        // Extract spatial calibration
        Calibration calib = data.labelMap.getCalibration();
        double sx = 1, sy = 1;
        if (calib != null)
        {
            sx = calib.getXAxis().getSpacing();
            sy = calib.getYAxis().getSpacing();
            if (sx != sy)
            {
                throw new RuntimeException("Requires image with square pixels");
            }
        }
        
        // retrieve required features
        data.ensureRequiredFeaturesAreComputed(this);
        PositionValuePair[] maxima = (PositionValuePair[]) data.results.get(DistanceMapMaxima.class);
        
        // Create new marker image
        this.fireStatusChanged(this, "Initializing marker image");
        int sizeX = labelMap.size(0);
        int sizeY = labelMap.size(1);
        BinaryArray2D marker = BinaryArray2D.create(sizeX, sizeY);
        
        // initialize marker image with position of maxima
        for (int i = 0; i < nLabels; i++) 
        {
            int[] pos = maxima[i].position();
            if (pos[0] == -1)
            {
                continue;
            }
            marker.setBoolean(pos[0], pos[1], true);
        }
    
        this.fireStatusChanged(this, "Computing first geodesic extremities...");
    
        // First geodesic distance propagation from region centers
        GeodesicDistanceTransform2DFloat32Hybrid geodesicDistanceTransform = new GeodesicDistanceTransform2DFloat32Hybrid(ChamferMask2D.CHESSKNIGHT, true);
        Float32Array2D distanceMap = geodesicDistanceTransform.process2d(marker, labelMap);
        
        // find position of maximal value for each label
        // this is expected to correspond to a geodesic extremity 
        int[][] firstGeodesicExtremities = LabelValues.maxValuePositions2d(labelMap, labels, distanceMap);
        
        // Create new marker image with position of maxima
        marker.fill(false);
        for (int i = 0; i < nLabels; i++)
        {
            if (firstGeodesicExtremities[i][0] == -1) 
            {
                continue;
            }
            marker.setBoolean(firstGeodesicExtremities[i][0], firstGeodesicExtremities[i][1], true);
        }
        
        this.fireStatusChanged(this, "Computing second geodesic extremities...");
    
        // second geodesic distance propagation from first extremity
        distanceMap = geodesicDistanceTransform.process2d(marker, labelMap);
        
        // also computes position of maxima
        PositionValuePair[] secondGeodesicExtremities = LabelValues.findMaxValues2d(labelMap, labels, distanceMap);
        
        // Create array of results and populate with computed values
        Result[] result = new Result[nLabels];
        for (int i = 0; i < nLabels; i++)
        {
            result[i] = new Result(maxima[i], firstGeodesicExtremities[i], secondGeodesicExtremities[i]);
        }
        
        // returns the results
        return result;
    }

    @Override
    public void updateTable(Table table, RegionFeatures data)
    {
        Object obj = data.results.get(this.getClass());
        if (obj instanceof Result[] results)
        {
            // add new empty columns to table
            for (String colName : colNames)
            {
                table.addColumn(NumericColumn.create(colName, results.length));
            }
            
            for (int r = 0; r < results.length; r++)
            {
                // current ellipse
                Result res = results[r];
                
                table.setValue(r, colNames[0], res.diameter);
                table.setValue(r, colNames[1], res.initialPoint.position()[0]);
                table.setValue(r, colNames[2], res.initialPoint.position()[1]);
                table.setValue(r, colNames[3], res.firstExtremity[0]);
                table.setValue(r, colNames[4], res.firstExtremity[1]);
                table.setValue(r, colNames[5], res.secondExtremity.position()[0]);
                table.setValue(r, colNames[6], res.secondExtremity.position()[1]);
            }
        }
        else
        {
            throw new RuntimeException("Requires object argument to be an array of Ellipse2D");
        }
    }
    
    @Override
    public Collection<Class<? extends Feature>> requiredFeatures()
    {
        return Arrays.asList(DistanceMapMaxima.class);
    }
    
    // ==================================================
    // Inner class used for representing computation results
    
    /**
     * Inner class used for representing results of geodesic diameters
     * computations. Each instance corresponds to a single region / particle.
     * 
     * @author dlegland
     *
     */
    public class Result
    {
        /** The geodesic diameter of the region */
        public double diameter;

        /**
         * The initial point used for propagating distances, corresponding the
         * center of one of the minimum inscribed circles.
         */
        public PositionValuePair initialPoint;

        /**
         * The first geodesic extremity found by the algorithm.
         */
        public int[] firstExtremity;

        /**
         * The second geodesic extremity found by the algorithm.
         */
        public PositionValuePair secondExtremity;
        
        public Result(PositionValuePair initialPosition, int[] firstExtremity, PositionValuePair secondExtremity)
        {
            this.initialPoint = initialPosition;
            this.firstExtremity = firstExtremity;
            this.secondExtremity = secondExtremity;
            
            // Get the maximum distance within each label, 
            // and add 1.0 to take into account pixel (side) thickness
            this.diameter = secondExtremity.value() + 1.0;
        }
    }
}
