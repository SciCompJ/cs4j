/**
 * 
 */
package net.sci.image.regionfeatures.morpho2d;

import java.util.Arrays;
import java.util.Collection;

import net.sci.image.regionfeatures.Feature;
import net.sci.image.regionfeatures.RegionFeatures;
import net.sci.image.regionfeatures.SingleValueFeature;
import net.sci.image.regionfeatures.morpho2d.core.Perimeter_Crofton_D4;

/**
 * Computes the perimeter of a 2D region. In practice, this feature is an alias
 * for the Perimeter_Crofton_D4 feature.
 * 
 */
public class Perimeter extends SingleValueFeature
{
    /**
     * Default empty constructor.
     */
    public Perimeter()
    {
        super("Perimeter");
    }
    
    @Override
    public double[] compute(RegionFeatures data)
    {
        // retrieve required feature values
        data.ensureRequiredFeaturesAreComputed(this);
        return (double[]) data.results.get(Perimeter_Crofton_D4.class);
    }
    
    @Override
    public Collection<Class<? extends Feature>> requiredFeatures()
    {
        return Arrays.asList(Perimeter_Crofton_D4.class);
    }
    
    @Override
    public String columnUnitName(RegionFeatures data)
    {
        String unitName = data.labelMap.getCalibration().getXAxis().getUnitName();
        return unitName != null && !unitName.isBlank() ? unitName : null;
    }
}
