/**
 * 
 */
package net.sci.image.regionfeatures.morpho2d;

import java.util.Arrays;
import java.util.Collection;

import net.sci.image.Calibration;
import net.sci.image.regionfeatures.Feature;
import net.sci.image.regionfeatures.RegionFeatures;
import net.sci.image.regionfeatures.SingleValueFeature;
import net.sci.image.regionfeatures.morpho2d.core.GeodesicDiameterData;

/**
 * Computes the geodesic diameter of each region.
 * 
 */
public class GeodesicDiameter extends SingleValueFeature
{
    /**
     * Default empty constructor.
     */
    public GeodesicDiameter()
    {
        super("Geodesic_Diameter");
    }
    
    @Override
    public String columnUnitName(RegionFeatures data)
    {
        String unitName = data.labelMap.getCalibration().getXAxis().getUnitName();
        return unitName != null && !unitName.isBlank() ? unitName : null;
    }
    
    @Override
    public double[] compute(RegionFeatures data)
    {
        // retrieve required features
        data.ensureRequiredFeaturesAreComputed(this);
        GeodesicDiameterData.Result[] results = (GeodesicDiameterData.Result[]) data.results.get(GeodesicDiameterData.class);
        
        // Extract spatial calibration
        Calibration calib = data.labelMap.getCalibration();
        double pixelSize = calib != null ? calib.getXAxis().getSpacing() : 1.0;
        
        return Arrays.stream(results)
                .mapToDouble(res -> res.diameter * pixelSize)
                .toArray();
    }
    
    @Override
    public Collection<Class<? extends Feature>> requiredFeatures()
    {
        return Arrays.asList(GeodesicDiameterData.class);
    }
}
