/**
 * 
 */
package net.sci.image.regionfeatures.morpho2d;

import java.util.Arrays;
import java.util.Collection;

import net.sci.geom.geom2d.curve.Ellipse2D;
import net.sci.image.regionfeatures.Feature;
import net.sci.image.regionfeatures.RegionFeatures;
import net.sci.image.regionfeatures.SingleValueFeature;

/**
 * Elongation of Equivalent ellipse, computed as the ratio of semi major axis
 * length over semi minor axis length.
 * 
 * @see EquivalentEllipse
 */
public class EllipseElongation extends SingleValueFeature
{
    /**
     * Default empty constructor.
     */
    public EllipseElongation()
    {
        super("Ellipse_Elongation");
    }
    
    @Override
    public double[] compute(RegionFeatures data)
    {
        // retrieve required feature values
        data.ensureRequiredFeaturesAreComputed(this);
        Ellipse2D[] ellipses = (Ellipse2D[]) data.results.get(EquivalentEllipse.class);
        
        // iterate over labels to compute new feature
        return Arrays.stream(ellipses)
            .mapToDouble(elli -> elli.semiMajorAxisLength() / elli.semiMinorAxisLength())
            .toArray();
    }

    @Override
    public Collection<Class<? extends Feature>> requiredFeatures()
    {
        return Arrays.asList(EquivalentEllipse.class);
    }
}
