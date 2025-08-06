/**
 * 
 */
package net.sci.image.regionfeatures.morpho2d;

import java.util.Arrays;
import java.util.Collection;

import net.sci.algo.AlgoStub;
import net.sci.geom.geom2d.Point2D;
import net.sci.geom.geom2d.curve.Circle2D;
import net.sci.image.Calibration;
import net.sci.image.label.LabelValues.PositionValuePair;
import net.sci.image.regionfeatures.Feature;
import net.sci.image.regionfeatures.RegionFeatures;
import net.sci.image.regionfeatures.RegionTabularFeature;
import net.sci.image.regionfeatures.morpho2d.core.DistanceMapMaxima;
import net.sci.table.NumericColumn;
import net.sci.table.Table;

/**
 * Computes the largest inscribed disk within each region of a label map.
 */
public class LargestInscribedDisk extends AlgoStub implements RegionTabularFeature
{
    /**
     * The names of the columns of the resulting table.
     */
    public static final String[] colNames = new String[] {"Inscribed_Disk_Center_X", "Inscribed_Disk_Center_Y", "Inscribed_Disk_Radius"};
    
    /**
     * Default empty constructor.
     */
    public LargestInscribedDisk()
    {
    }
    

    @Override
    public Circle2D[] compute(RegionFeatures data)
    {
        // retrieve meta data
        int nLabels = data.labels.length;
        Calibration calib = data.labelMap.getCalibration();
        
        // Extract spatial calibration
        double sx = 1, sy = 1;
        double ox = 0, oy = 0;
        if (calib != null)
        {
            sx = calib.getXAxis().getSpacing();
            sy = calib.getYAxis().getSpacing();
            ox = calib.getXAxis().getOrigin();
            oy = calib.getYAxis().getOrigin();
        }
        
        // retrieve required features
        data.ensureRequiredFeaturesAreComputed(this);
        PositionValuePair[] maxima = (PositionValuePair[]) data.results.get(DistanceMapMaxima.class);
        
        // Create array of calibrated circles
        Circle2D[] circles = new Circle2D[nLabels];
        for (int i = 0; i < nLabels; i++) 
        {
            PositionValuePair posVal = maxima[i];
            int[] pos = posVal.position();
            double xc = pos[0] * sx + ox;
            double yc = pos[1] * sy + oy;
            double radius = posVal.value() * sx;
            circles[i] = new Circle2D(new Point2D(xc, yc), radius);
        }

        return circles;
    }

    @Override
    public void updateTable(Table table, RegionFeatures data)
    {
        Object obj = data.results.get(this.getClass());
        if (obj instanceof Circle2D[] circles)
        {
            // add new empty columns to table
            String unitName = data.labelMap.getCalibration().getXAxis().getUnitName();
            for (int iCol = 0; iCol < 3; iCol++)
            {
                NumericColumn col = NumericColumn.create(colNames[iCol], circles.length);
                if (unitName != null && !unitName.isBlank())
                {
                    col.setUnitName(unitName);
                }
                table.addColumn(col);
            }
            
            for (int r = 0; r < circles.length; r++)
            {
                // current circle
                Circle2D circle = circles[r];
                
                // write circle features
                Point2D center = circle.center();
                table.setValue(r, colNames[0], center.x());
                table.setValue(r, colNames[1], center.y());
                table.setValue(r, colNames[2], circle.radius());
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
}
