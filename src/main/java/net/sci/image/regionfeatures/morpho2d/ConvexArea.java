/**
 * 
 */
package net.sci.image.regionfeatures.morpho2d;

import java.util.Arrays;
import java.util.Collection;

import net.sci.geom.geom2d.Bounds2D;
import net.sci.geom.geom2d.Point2D;
import net.sci.geom.polygon2d.Polygon2D;
import net.sci.image.Calibration;
import net.sci.image.regionfeatures.Feature;
import net.sci.image.regionfeatures.RegionFeatures;
import net.sci.image.regionfeatures.SingleValueFeature;
import net.sci.image.regionfeatures.morpho2d.core.ConvexHull;

/**
 * Computes the convex area, or area of the convex hull. The convex area is
 * computed as the number of pixels contained within the convex hull, multiplied
 * by the area of the unit pixel. The results may be slightly different from the
 * polygon area computed on the convex hull.
 * 
 */
public class ConvexArea extends SingleValueFeature
{

    /**
     * Default empty constructor.
     */
    public ConvexArea()
    {
        super("Convex_Area");
    }
    
    @Override
    public double[] compute(RegionFeatures data)
    {
        // retrieve required feature values
        data.ensureRequiredFeaturesAreComputed(this);
        Polygon2D[] hulls = (Polygon2D[]) data.results.get(ConvexHull.class);
        
        // retrieve label map data
        int[] labels = data.labels;
        Calibration calib = data.labelMap.getCalibration();
        double pixelArea = 1.0;
        if (calib != null)
        {
            pixelArea = calib.getXAxis().getSpacing() * calib.getYAxis().getSpacing();
        }
        
        // iterate over labels
        double[] convexAreas = new double[labels.length];
        for (int i = 0; i < labels.length; i++)
        {
            Polygon2D convexHull = hulls[i];
            
            // determine bounds
            Bounds2D box = convexHull.bounds();
            int xmin = (int) Math.floor(box.xMin());
            int xmax = (int) Math.ceil(box.xMax());
            int ymin = (int) Math.floor(box.yMin());
            int ymax = (int) Math.ceil(box.yMax());
            
            // counts the number of pixels with integer coordinates within the convex hull
            double convexArea = 0;
            for (int y = ymin; y < ymax; y++)
            {
                for (int x = xmin; x < xmax; x++)
                {
                    if (convexHull.contains(new Point2D(x, y)))
                    {
                        convexArea++;
                    }
                }
            }
            
            // compute calibrated convex area
            convexAreas[i] = convexArea * pixelArea;
        }
        
        return convexAreas;
    }

    @Override
    public Collection<Class<? extends Feature>> requiredFeatures()
    {
        return Arrays.asList(ConvexHull.class);
    }
    
    @Override
    public String columnUnitName(RegionFeatures data)
    {
        String unitName = data.labelMap.getCalibration().getXAxis().getUnitName();
        return unitName != null && !unitName.isBlank() ? unitName + "^2" : null;
    }
}
