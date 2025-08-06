/**
 * 
 */
package net.sci.image.regionfeatures.morpho2d;

import java.util.Arrays;
import java.util.Collection;

import net.sci.image.regionfeatures.Feature;
import net.sci.image.regionfeatures.RegionFeatures;
import net.sci.image.regionfeatures.SingleValueFeature;

/**
 * The tortuosity, defined as the ratio of Geodesic diameter over Max Feret
 * Diameter.
 * 
 * @see GeodesicDiameter
 * @see MaxFeretDiameter
 */
public class Tortuosity extends SingleValueFeature
{
    /**
     * Default empty constructor.
     */
    public Tortuosity()
    {
        super("Tortuosity");
    }
    
    @Override
    public double[] compute(RegionFeatures data)
    {
        // retrieve required feature values
        data.ensureRequiredFeaturesAreComputed(this);
        double[] geodDiams = (double[]) data.results.get(GeodesicDiameter.class);
        double[] feretDiams = (double[]) data.results.get(MaxFeretDiameter.class);
        
        // iterate over labels to compute new feature
        int[] labels = data.labels;
        double[] res = new double[labels.length];
        for (int i = 0; i < labels.length; i++)
        {
            double gd = geodDiams[i];
            double fd = feretDiams[i];
            res[i] = gd / fd;
        }
        return res;
    }
    
    @Override
    public Collection<Class<? extends Feature>> requiredFeatures()
    {
        return Arrays.asList(GeodesicDiameter.class, MaxFeretDiameter.class);
    }

}
