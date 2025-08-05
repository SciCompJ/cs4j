/**
 * 
 */
package net.sci.image.regionfeatures.morpho2d;

import java.util.Arrays;
import java.util.Collection;

import net.sci.geom.geom2d.PointPair2D;
import net.sci.image.regionfeatures.Feature;
import net.sci.image.regionfeatures.RegionFeatures;
import net.sci.image.regionfeatures.SingleValueFeature;
import net.sci.image.regionfeatures.morpho2d.core.FurthestPointPair;

/**
 * Computes the largest Feret diameter of each region within a label map.
 * 
 */
public class MaxFeretDiameter extends SingleValueFeature
{
    /**
     * Default empty constructor.
     */
    public MaxFeretDiameter()
    {
        super("Max_Feret_Diameter");
    }
    
    @Override
    public double[] compute(RegionFeatures data)
    {
        // retrieve required feature values
        data.ensureRequiredFeaturesAreComputed(this);
        PointPair2D[] pairs = (PointPair2D[]) data.results.get(FurthestPointPair.class);
        
        return Arrays.stream(pairs)
                .mapToDouble(pair -> pair.diameter())
                .toArray();    
    }
    
    @Override
    public Collection<Class<? extends Feature>> requiredFeatures()
    {
        return Arrays.asList(FurthestPointPair.class);
    }
    
    public String[] columnUnitNames(RegionFeatures data)
    {
        String unitName = data.labelMap.getCalibration().getXAxis().getUnitName();
        return unitName != null && !unitName.isBlank() ? new String[] {unitName} : null;
    }
}
