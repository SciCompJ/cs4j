/**
 * 
 */
package net.sci.image.regionfeatures.morpho2d;


import java.util.Arrays;
import java.util.Collection;

import net.sci.image.Calibration;
import net.sci.image.regionfeatures.ElementCount;
import net.sci.image.regionfeatures.Feature;
import net.sci.image.regionfeatures.RegionFeatures;
import net.sci.image.regionfeatures.SingleValueFeature;

/**
 * A feature that computes the area of 2D regions.
 * 
 * @see ElementCount
 */
public class Area extends SingleValueFeature
{
    /**
     * Default empty constructor.
     */
    public Area()
    {
        super("Area");
    }
    
    @Override
    public double[] compute(RegionFeatures data)
    {
        // retrieve required feature values
        data.ensureRequiredFeaturesAreComputed(this);
        int[] counts = (int[]) data.results.get(ElementCount.class);
        
        // area of unit voxel
        Calibration calib = data.labelMap.getCalibration();
        double pixelArea = calib.getXAxis().getSpacing() * calib.getYAxis().getSpacing(); 
        
        // compute area from pixel count
        return Arrays.stream(counts)
                .mapToDouble(count -> count * pixelArea)
                .toArray();
    }
    
    @Override
    public Collection<Class<? extends Feature>> requiredFeatures()
    {
        return Arrays.asList(ElementCount.class);
    }
    
    @Override
    public String columnUnitName(RegionFeatures data)
    {
        String unitName = data.labelMap.getCalibration().getXAxis().getUnitName();
        return unitName != null && !unitName.isBlank() ? unitName + "^2" : null;
    }
}
