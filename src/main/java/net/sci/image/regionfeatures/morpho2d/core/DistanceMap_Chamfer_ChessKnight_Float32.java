/**
 * 
 */
package net.sci.image.regionfeatures.morpho2d.core;

import net.sci.array.numeric.ScalarArray;
import net.sci.image.Image;
import net.sci.image.ImageType;
import net.sci.image.binary.distmap.ChamferMask2D;
import net.sci.image.label.distmap.*;
import net.sci.image.regionfeatures.Feature;
import net.sci.image.regionfeatures.RegionFeatures;

/**
 * Computes the distance map of a label image. For each pixel within a region,
 * the feature computes the distance to the nearest pixel outside the region
 * (either background, or within another region).
 * 
 * Uses a chamfer distance map computed with a 32-bit floating point result
 * array, and a "Chessknight" chamfer mask.
 */
public class DistanceMap_Chamfer_ChessKnight_Float32 implements Feature
{
    /**
     * Default empty constructor.
     */
    public DistanceMap_Chamfer_ChessKnight_Float32()
    {
    }

    @Override
    public Image compute(RegionFeatures data)
    {
        ChamferDistanceTransform2DFloat32 algo = new ChamferDistanceTransform2DFloat32(ChamferMask2D.CHESSKNIGHT);
        ScalarArray<?> distanceMap = algo.process(data.labelMap.getData());
        Image result = new Image(distanceMap, ImageType.DISTANCE);
        result.setName(data.labelMap.getName() + "-distMap");
        return result;
    }

}
