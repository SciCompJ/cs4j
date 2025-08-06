/**
 * 
 */
package net.sci.image.regionfeatures.morpho2d;

import java.util.Arrays;
import java.util.Collection;

import net.sci.geom.geom2d.polygon.OrientedBox2D;
import net.sci.image.regionfeatures.Feature;
import net.sci.image.regionfeatures.RegionFeatures;
import net.sci.image.regionfeatures.SingleValueFeature;

/**
 * Elongation of oriented bounding box.
 * 
 * @see OrientedBoundingBox
 */
public class OrientedBoxElongation extends SingleValueFeature
{
    /**
     * Default empty constructor.
     */
    public OrientedBoxElongation()
    {
        super("Oriented_Box_Elongation");
    }
    
    @Override
    public double[] compute(RegionFeatures data)
    {
        // retrieve required feature values
        data.ensureRequiredFeaturesAreComputed(this);
        OrientedBox2D[] boxes = (OrientedBox2D[]) data.results.get(OrientedBoundingBox.class);
        
        // iterate over labels to compute new feature
        return Arrays.stream(boxes)
            .mapToDouble(box -> box.size1() / box.size2())
            .toArray();
    }

    @Override
    public Collection<Class<? extends Feature>> requiredFeatures()
    {
        return Arrays.asList(OrientedBoundingBox.class);
    }
}
