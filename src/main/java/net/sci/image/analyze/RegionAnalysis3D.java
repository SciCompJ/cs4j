/**
 * 
 */
package net.sci.image.analyze;

import java.util.HashMap;

import net.sci.array.scalar.IntArray3D;
import net.sci.geom.geom3d.Box3D;
import net.sci.geom.geom3d.Point3D;
import net.sci.image.morphology.LabelImages;

/**
 * A collections of static methods for the analysis of 3D regions.
 * 
 * @author dlegland
 *
 */
public class RegionAnalysis3D
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
    public final static Point3D[] centroids(IntArray3D<?> labelImage, int[] labels)
    {
        // create associative array to know index of each label
        int nLabels = labels.length;
        HashMap<Integer, Integer> labelIndices = LabelImages.mapLabelIndices(labels);

        // allocate memory for result
        int[] counts = new int[nLabels];
        double[] xcoords = new double[nLabels];
        double[] ycoords = new double[nLabels];
        double[] zcoords = new double[nLabels];

        // size of input image
        int sizeX = labelImage.size(0);
        int sizeY = labelImage.size(1);
        int sizeZ = labelImage.size(2);

        // compute centroid of each region
        for (int z = 0; z < sizeZ; z++) 
        {
            for (int y = 0; y < sizeY; y++) 
            {
                for (int x = 0; x < sizeX; x++)
                {
                    int label = labelImage.getInt(x, y, z);
                    if (label == 0)
                        continue;

                    // do not process labels that are not in the input list 
                    if (!labelIndices.containsKey(label))
                        continue;

                    int index = labelIndices.get(label);
                    xcoords[index] += x;
                    ycoords[index] += y;
                    zcoords[index] += z;
                    counts[index]++;
                }
            }
        }

        // normalize by the number of pixels in each region
        // and convert to point array
        Point3D[] centroids = new Point3D[nLabels];
        for (int i = 0; i < nLabels; i++)
        {
            int nc = counts[i];
            centroids[i] = new Point3D(xcoords[i] / nc, ycoords[i] / nc, zcoords[i] / nc);
        }
        
        return centroids;
    }
    
    public final static Box3D[] boundingBoxes(IntArray3D<?> image, int[] labels)
    {
        // create associative array to know index of each label
        int nLabels = labels.length;
        HashMap<Integer, Integer> labelIndices = LabelImages.mapLabelIndices(labels);

        // allocate memory for result
        double[] xmin = new double[nLabels];
        double[] xmax = new double[nLabels];
        double[] ymin = new double[nLabels];
        double[] ymax = new double[nLabels];
        double[] zmin = new double[nLabels];
        double[] zmax = new double[nLabels];
        for (int i = 0; i < nLabels; i++)
        {
            xmin[i] = Double.POSITIVE_INFINITY;
            xmax[i] = Double.NEGATIVE_INFINITY;
            ymin[i] = Double.POSITIVE_INFINITY;
            ymax[i] = Double.NEGATIVE_INFINITY;
            zmin[i] = Double.POSITIVE_INFINITY;
            zmax[i] = Double.NEGATIVE_INFINITY;
        }

        // size of input image
        int sizeX = image.size(0);
        int sizeY = image.size(1);
        int sizeZ = image.size(1);

        // compute centroid of each region
        for (int z = 0; z < sizeZ; z++) 
        {
            for (int y = 0; y < sizeY; y++) 
            {
                for (int x = 0; x < sizeX; x++)
                {
                    int label = image.getInt(x, y, z);
                    if (label == 0)
                        continue;

                    // do not process labels that are not in the input list 
                    if (!labelIndices.containsKey(label))
                        continue;

                    int index = labelIndices.get(label);

                    xmin[index] = Math.min(xmin[index], x - .5);
                    xmax[index] = Math.max(xmax[index], x + .5);
                    ymin[index] = Math.min(ymin[index], y - .5);
                    ymax[index] = Math.max(ymax[index], y + .5);
                    zmin[index] = Math.min(zmin[index], z - .5);
                    zmax[index] = Math.max(zmax[index], z + .5);
                }
            }
        }
        
        Box3D[] boxes = new Box3D[nLabels];
        for (int i = 0; i < nLabels; i++)
        {
        	boxes[i] = new Box3D(xmin[i], xmax[i], ymin[i], ymax[i], zmin[i], zmax[i]);
        }
        
        return boxes;
    }
}
