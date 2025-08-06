/**
 * 
 */
package net.sci.image.regionfeatures.morpho2d.core;

import java.util.Arrays;
import java.util.Collection;

import net.sci.algo.AlgoStub;
import net.sci.array.Array;
import net.sci.array.numeric.Int;
import net.sci.array.numeric.IntArray;
import net.sci.array.numeric.IntArray2D;
import net.sci.array.numeric.ScalarArray2D;
import net.sci.image.Image;
import net.sci.image.label.LabelValues;
import net.sci.image.regionfeatures.Feature;
import net.sci.image.regionfeatures.RegionFeatures;

/**
 * Computes the position of the maximum values of the distance map. Returns the
 * result as an array of {@code PositionValuePair}, that gathers the maxima
 * position and value within the same data structure. The positions are computed
 * in pixel coordinates.
 * 
 * @see DistanceMap
 */
public class DistanceMapMaxima extends AlgoStub implements Feature
{
    /**
     * Default empty constructor.
     */
    public DistanceMapMaxima()
    {
    }
    
    @Override
    public LabelValues.PositionValuePair[] compute(RegionFeatures data)
    {
        // retrieve label map and list of labels
        @SuppressWarnings({ "rawtypes", "unchecked" })
        IntArray2D labelMap = IntArray2D.wrap(IntArray.wrap((Array<Int>) data.labelMap.getData()));
        int[] labels = data.labels;
        
        // retrieve required feature values
        data.ensureRequiredFeaturesAreComputed(this);
        ScalarArray2D<?> distanceMap = (ScalarArray2D<?>) ((Image) data.results.get(DistanceMap.class)).getData();
        
        // Extract position of maxima
        return LabelValues.findMaxValues2d(labelMap, labels, distanceMap);

    }

    @Override
    public Collection<Class<? extends Feature>> requiredFeatures()
    {
        return Arrays.asList(DistanceMap.class);
    }
}
