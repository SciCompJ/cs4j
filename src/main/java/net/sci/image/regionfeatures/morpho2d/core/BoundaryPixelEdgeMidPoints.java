package net.sci.image.regionfeatures.morpho2d.core;

import java.util.ArrayList;
import java.util.HashMap;

import net.sci.algo.AlgoStub;
import net.sci.array.Array;
import net.sci.array.numeric.Int;
import net.sci.array.numeric.IntArray;
import net.sci.array.numeric.IntArray2D;
import net.sci.geom.geom2d.Point2D;
import net.sci.image.label.LabelImages;
import net.sci.image.regionfeatures.Feature;
import net.sci.image.regionfeatures.RegionFeatures;

/**
 * Extracts boundary points from the different regions.
 * 
 * Single pixel with integer coordinates (x,y) corresponds to an equivalent area
 * given by [x-0.5,x+0.5[ x [y-0.5,y+0.5[, and a center located at (x,y).
 * 
 * This method considers middle points of boundary pixel edges, assuming a
 * "diamond shape" for pixels.
 * 
 * The boundaries extracted by this methods have following coordinates:
 * <ul>
 * <li><i>(x, y-0.5)</i>: "top" boundary</li>
 * <li><i>(x-0.5, y)</i>: left boundary</li>
 * <li><i>(x+0.5, y)</i>: right boundary</li>
 * <li><i>(x, y+0.5)</i>: "bottom" boundary</li>
 * </ul>
 * 
 * @see ConvexHull
 */
public class BoundaryPixelEdgeMidPoints extends AlgoStub implements Feature
{
    // ==================================================
    // Implementation of the Feature interface
    
    @Override
    public ArrayList<Point2D>[] compute(RegionFeatures data)
    {
        // retrieve image data
        Array<?> array = data.labelMap.getData();
        @SuppressWarnings({ "unchecked", "rawtypes" })
        IntArray2D<?> labelMap = IntArray2D.wrap(IntArray.wrap((Array<? extends Int>) array));
        int[] labels = data.labels;
        
        // size of image
        int sizeX = labelMap.size(0);
        int sizeY = labelMap.size(1);

        // create the map of labels
        HashMap<Integer, Integer> labelIndices = LabelImages.mapLabelIndices(labels);

        // allocate memory for result
        int nLabels = labels.length;
        @SuppressWarnings("unchecked")
        ArrayList<Point2D>[] result = (ArrayList<Point2D>[]) new ArrayList[nLabels];
        for (int i = 0; i < nLabels; i++)
        {
            result[i] = new ArrayList<Point2D>();
        }
        
        // create an array of local configuration, containing values of pixels
        // within a 2-by-2 neighborhood of the current pixel
        int[] configValues = new int[4];
        
        // iterate on image pixel configurations
        for (int y = 0; y < sizeY + 1; y++) 
        {
            configValues[2] = 0;
            
            for (int x = 0; x < sizeX + 1; x++) 
            {
                // update pixel values of configuration
                configValues[1] = x < sizeX & y > 0 ? labelMap.getInt(x, y - 1) : 0;
                configValues[3] = x < sizeX & y < sizeY ? labelMap.getInt(x, y) : 0;
                
                // check boundary with upper pixel
                if (configValues[1] != configValues[3])
                {
                    if (configValues[1] != 0)
                    {
                        int index = labelIndices.get(configValues[1]); 
                        result[index].add(new Point2D(x, y - 0.5));
                    }
                    if (configValues[3] != 0)
                    {
                        int index = labelIndices.get(configValues[3]); 
                        result[index].add(new Point2D(x, y - 0.5));
                    }
                }

                // check boundary with pixel on the left
                if (configValues[2] != configValues[3])
                {
                    if (configValues[2] != 0)
                    {
                        int index = labelIndices.get(configValues[2]); 
                        result[index].add(new Point2D(x - 0.5, y));
                    }
                    if (configValues[3] != 0)
                    {
                        int index = labelIndices.get(configValues[3]); 
                        result[index].add(new Point2D(x - 0.5, y));
                    }
                }

                // update values of configuration for next iteration
                configValues[2] = configValues[3];
            }
        }

        return result;
    }
}
