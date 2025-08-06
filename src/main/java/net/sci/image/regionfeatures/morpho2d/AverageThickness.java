/**
 * 
 */
package net.sci.image.regionfeatures.morpho2d;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.stream.DoubleStream;

import net.sci.array.numeric.IntArray2D;
import net.sci.array.numeric.ScalarArray2D;
import net.sci.image.Calibration;
import net.sci.image.Image;
import net.sci.image.label.LabelImages;
import net.sci.image.regionfeatures.Feature;
import net.sci.image.regionfeatures.RegionFeatures;
import net.sci.image.regionfeatures.SingleValueFeature;
import net.sci.image.regionfeatures.morpho2d.core.DistanceMap;
import net.sci.image.regionfeatures.morpho2d.core.Skeleton;

/**
 * Computes the average thickness of regions, by computing the average of the
 * distance map on the inner skeleton of each region.
 * 
 * @see Skeleton
 * @see DistanceMap
 */
public class AverageThickness extends SingleValueFeature
{
    /**
     * Default empty constructor.
     */
    public AverageThickness()
    {
        super("Average_Thickness");
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
        IntArray2D<?> skeleton = (IntArray2D<?>) ((Image) data.results.get(Skeleton.class)).getData();
        ScalarArray2D<?> distanceMap = (ScalarArray2D<?>) ((Image) data.results.get(DistanceMap.class)).getData();
       
        // Extract spatial calibration
        Calibration calib = data.labelMap.getCalibration();
        double pixelSize = calib != null ? calib.getXAxis().getSpacing() : 1.0;
        
        // compute average thickness in pixel coordinates
        double[] res = averageThickness(skeleton, distanceMap, data.labels);
        
        // calibrate the array of thicknesses
        return DoubleStream.of(res)
                .map(v -> v * pixelSize)
                .toArray();
    }
    
    /**
     * Computes the average thickness in pixel coordinates.
     * 
     * @param skeleton
     *            the ImageProcessor containing the skeleton of each region
     * @param distanceMap
     *            the ImageProcessor containing the distance map of each region
     * @param labels
     *            the array of labels to compute
     * @return the average thickness of each region with a label within the
     *         {@code labels} array.
     */
    private static final double[] averageThickness(IntArray2D<?> skeleton, ScalarArray2D<?> distanceMap, int[] labels)
    {
        // retrieve image size
        int sizeX = skeleton.size(0);
        int sizeY = skeleton.size(1);

        // allocate memory for result values
        int nLabels = labels.length;
        double[] sums = new double[nLabels];
        int[] counts = new int[nLabels];

        // Iterate over skeleton pixels
        Map<Integer, Integer> labelIndices = LabelImages.mapLabelIndices(labels);
        for (int y = 0; y < sizeY; y++)
        {
            for (int x = 0; x < sizeX; x++)
            {
                // label of current pixel 
                int label = skeleton.getInt(x, y);
                if (label == 0)
                {
                    continue;
                }

                int index = labelIndices.get(label);
                
                // update results for current region
                sums[index] += distanceMap.getValue(x, y);
                counts[index]++;
            }
        }
        
        // compute average thickness from the ratio of sum of skeleton values
        // divided by number of skeleton pixels
        double[] res = new double[nLabels];
        for (int i = 0; i < nLabels; i++)
        {
            res[i] = (sums[i] / counts[i]) * 2 - 1;
        }
        return res;
    }
    
    @Override
    public Collection<Class<? extends Feature>> requiredFeatures()
    {
        return Arrays.asList(Skeleton.class, DistanceMap.class);
    }
}
