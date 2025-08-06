/**
 * 
 */
package net.sci.image.regionfeatures.morpho2d.core;

import net.sci.array.Array;
import net.sci.array.numeric.Int;
import net.sci.array.numeric.IntArray;
import net.sci.array.numeric.IntArray2D;
import net.sci.image.Image;
import net.sci.image.label.skeleton.ImageJSkeleton;
import net.sci.image.regionfeatures.Feature;
import net.sci.image.regionfeatures.RegionFeatures;

/**
 * Computes the skeleton of each region within a label map, and returns another
 * image containing the skeleton of each region.
 * 
 * Uses an adaptation of the algorithm from ImageJ.
 */
public class Skeleton implements Feature
{
    /**
     * Default empty constructor.
     */
    public Skeleton()
    {
    }
    
    @Override
    public Image compute(RegionFeatures data)
    {
        // retrieve image data
        Array<?> array = data.labelMap.getData();
        @SuppressWarnings({ "unchecked", "rawtypes" })
        IntArray2D<?> labelMap = IntArray2D.wrap(IntArray.wrap((Array<? extends Int>) array));
        
        ImageJSkeleton skel = new ImageJSkeleton();
        IntArray2D<?> res = skel.process2d(labelMap);

        // create result image
        Image resultImage = new Image(res, data.labelMap);
        resultImage.setName(data.labelMap.getName() + "-skeleton");
        
        return resultImage;
    }

}
