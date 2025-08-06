/**
 * 
 */
package net.sci.image.regionfeatures.morpho2d;

import java.util.Arrays;
import java.util.Collection;

import net.sci.geom.geom2d.curve.Circle2D;
import net.sci.image.regionfeatures.Feature;
import net.sci.image.regionfeatures.RegionFeatures;
import net.sci.image.regionfeatures.SingleValueFeature;

/**
 * The GeodesicElongation, defined as the ratio of Geodesic diameter over
 * diameter of largest inscribed disk.
 * 
 * @see GeodesicDiameter
 * @see LargestInscribedDisk
 */
public class GeodesicElongation extends SingleValueFeature
{
    /**
     * Default empty constructor.
     */
    public GeodesicElongation()
    {
        super("Geodesic_Elongation");
    }
    
    @Override
    public double[] compute(RegionFeatures data)
    {
        // retrieve required feature values
        data.ensureRequiredFeaturesAreComputed(this);
        double[] geodDiams = (double[]) data.results.get(GeodesicDiameter.class);
        Circle2D[] inscrDisks = (Circle2D[]) data.results.get(LargestInscribedDisk.class);
        
        // iterate over labels to compute new feature
        int[] labels = data.labels;
        double[] res = new double[labels.length];
        for (int i = 0; i < labels.length; i++)
        {
            double gd = geodDiams[i];
            double cd = inscrDisks[i].radius() * 2;
            res[i] = gd / cd;
        }
        return res;
    }
    
    @Override
    public Collection<Class<? extends Feature>> requiredFeatures()
    {
        return Arrays.asList(GeodesicDiameter.class, LargestInscribedDisk.class);
    }

}
