/**
 * 
 */
package net.sci.image.regionfeatures.morpho2d.core;

import java.util.Arrays;
import java.util.Collection;

import net.sci.image.Image;
import net.sci.image.regionfeatures.Feature;
import net.sci.image.regionfeatures.RegionFeatures;

/**
 * Computes the distance map that associates to each pixel within a region, the
 * distance to the nearest pixel outside the region.
 * 
 * This class is an alias for the feature
 * {@code DistanceMap_Chamfer_ChessKnight_Float32}, that computes distance
 * map using a chamfer mask with size 5-by-5, and the system of weights (5, 7,
 * 11).
 */
public class DistanceMap implements Feature
{
    /**
     * Default empty constructor.
     */
    public DistanceMap()
    {
    }
    
    @Override
    public Image compute(RegionFeatures data)
    {
        return (Image) data.results.get(DistanceMap_Chamfer_ChessKnight_Float32.class);
    }
    
    @Override
    public Collection<Class<? extends Feature>> requiredFeatures()
    {
        return Arrays.asList(DistanceMap_Chamfer_ChessKnight_Float32.class);
    }
}
