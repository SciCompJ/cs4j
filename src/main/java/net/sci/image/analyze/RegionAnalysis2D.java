/**
 * 
 */
package net.sci.image.analyze;

import java.util.HashMap;

import net.sci.array.data.scalar2d.IntArray2D;
import net.sci.image.morphology.LabelImages;

/**
 * @author dlegland
 *
 */
public class RegionAnalysis2D
{
    /**
     * Computes the centroid of each label in the input image and returns the
     * result as an array of coordinates for each label.
     * 
     * @param labelImage
     *            the input image containing label of particles
     * @param labels
     *            the array of unique labels in image the number of directions
     *            to process, either 2 or 4
     * @return an array containing for each label, the coordinates of the
     *         centroid, in pixel coordinates
     */
    public final static double[][] centroids(IntArray2D<?> image, int[] labels)
    {
        // create associative array to know index of each label
        int nLabels = labels.length;
        HashMap<Integer, Integer> labelIndices = LabelImages.mapLabelIndices(labels);

        // allocate memory for result
        int[] counts = new int[nLabels];
        double[][] coords = new double[nLabels][2];

        // size of input image
        int sizeX = image.getSize(0);
        int sizeY = image.getSize(1);

        // compute centroid of each region
        for (int y = 0; y < sizeY; y++) 
        {
            for (int x = 0; x < sizeX; x++)
            {
                int label = image.getInt(x, y);
                if (label == 0)
                    continue;

                // do not process labels that are not in the input list 
                if (!labelIndices.containsKey(label))
                    continue;
                
                int index = labelIndices.get(label);
                coords[index][0] += x;
                coords[index][1] += y;
                counts[index]++;
            }
        }

        // normalize by the number of pixels in each region
        for (int i = 0; i < nLabels; i++)
        {
            coords[i][0] /= counts[i];
            coords[i][1] /= counts[i];
        }
        return coords;
    }
}
