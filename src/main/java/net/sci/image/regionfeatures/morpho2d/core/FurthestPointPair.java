/**
 * 
 */
package net.sci.image.regionfeatures.morpho2d.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import net.sci.algo.AlgoStub;
import net.sci.geom.geom2d.FeretDiameters;
import net.sci.geom.geom2d.Point2D;
import net.sci.geom.geom2d.PointPair2D;
import net.sci.geom.geom2d.polygon.Polygon2D;
import net.sci.image.Calibration;
import net.sci.image.regionfeatures.Feature;
import net.sci.image.regionfeatures.RegionFeatures;
import net.sci.image.regionfeatures.RegionTabularFeature;
import net.sci.table.NumericColumn;
import net.sci.table.Table;

/**
 * 
 */
public class FurthestPointPair extends AlgoStub implements RegionTabularFeature
{
    /**
     * The names of the columns of the resulting table.
     */
    public static final String[] colNames = new String[] {"FurthestPoint1_X", "FurthestPoint1_Y", "FurthestPoint2_X", "FurthestPoint2_Y"};

    /**
     * Default empty constructor.
     */
    public FurthestPointPair()
    {
    }
    
    @Override
    public PointPair2D[] compute(RegionFeatures data)
    {
        // retrieve required feature values
        data.ensureRequiredFeaturesAreComputed(this);
        Polygon2D[] hulls = (Polygon2D[]) data.results.get(ConvexHull.class);
        int nLabels = hulls.length;
        
        // retrieve spatial calibration of image
        Calibration calib = data.labelMap.getCalibration();
        double sx = 1, sy = 1;
        double ox = 0, oy = 0;
        if (calib != null)
        {
            sx = calib.getXAxis().getSpacing();
            sy = calib.getYAxis().getSpacing();
            ox = calib.getXAxis().getOrigin();
            oy = calib.getYAxis().getOrigin();
        }

        // Compute the oriented box of each set of corner points
        PointPair2D[] labelMaxDiams = new PointPair2D[nLabels];

        // iterate over label
        for (int i = 0; i < nLabels; i++)
        {
            this.fireProgressChanged(this, i, nLabels);
            
            // calibrate the convex hull
            Polygon2D hull = hulls[i];
            ArrayList<Point2D> corners = new ArrayList<Point2D>(hull.vertexCount());
            for (Point2D vertex : hull.vertexPositions())
            {
                vertex = new Point2D(vertex.x() * sx + ox, vertex.y() * sy + oy);
                corners.add(vertex);
            }

            // compute Feret diameter of calibrated hull
            labelMaxDiams[i] = FeretDiameters.maxFeretDiameter(corners);
        }
        
        return labelMaxDiams;
    }

    @Override
    public void updateTable(Table table, RegionFeatures data)
    {
        Object obj = data.results.get(this.getClass());
        if (obj instanceof PointPair2D[] pointPairs)
        {
            // add new empty columns to table
            String unitName = data.labelMap.getCalibration().getXAxis().getUnitName();
            for (String colName : colNames)
            {
                NumericColumn col = NumericColumn.create(colName, pointPairs.length);
                if (unitName != null && !unitName.isBlank())
                {
                    col.setUnitName(unitName);
                }
                table.addColumn(col);
            }
            
            for (int i = 0; i < pointPairs.length; i++)
            {
                table.setValue(i, colNames[0], pointPairs[i].p1.x());
                table.setValue(i, colNames[1], pointPairs[i].p1.y());
                table.setValue(i, colNames[2], pointPairs[i].p2.x());
                table.setValue(i, colNames[3], pointPairs[i].p2.y());
            }
        }
        else
        {
            throw new RuntimeException("Requires object argument to be an array of PointPair2D");
        }
    }
    
    @Override
    public Collection<Class<? extends Feature>> requiredFeatures()
    {
        return Arrays.asList(ConvexHull.class);
    }
}
