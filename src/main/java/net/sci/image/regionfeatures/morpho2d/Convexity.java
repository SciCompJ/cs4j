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
 * Computes the convexity, as the ratio of area over convex area.
 * 
 * @see Area
 * @see ConvexArea
 */
public class Convexity extends SingleValueFeature
{
    /**
     * Default empty constructor.
     */
    public Convexity()
    {
        super("Convexity");
    }
    
    @Override
    public double[] compute(RegionFeatures data)
    {
        // retrieve required feature values
        data.ensureRequiredFeaturesAreComputed(this);
        double[] areas = (double[]) data.results.get(Area.class);
        double[] convexAreas = (double[]) data.results.get(ConvexArea.class);
        
        // iterate over labels
        double[] convexities = new double[areas.length];
        for (int i = 0; i < areas.length; i++)
        {
            convexities[i] = areas[i] / convexAreas[i];
        }
        
        return convexities;
    }
    
    @Override
    public Collection<Class<? extends Feature>> requiredFeatures()
    {
        return Arrays.asList(Area.class, ConvexArea.class);
    }
}
