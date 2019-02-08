/**
 * 
 */
package net.sci.image.analyze;

import static java.lang.Math.sqrt;

import java.util.HashMap;

import net.sci.array.scalar.IntArray2D;
import net.sci.geom.geom2d.Box2D;
import net.sci.geom.geom2d.Point2D;
import net.sci.geom.geom2d.curve.Ellipse2D;
import net.sci.image.morphology.LabelImages;

/**
 * A collections of static methods for the analysis of 3D regions.
 * 
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
     * @return an array of Point2D corresponding to the centroid of each label
     *         in pixel coordinates
     */
    public final static Point2D[] centroids(IntArray2D<?> labelImage, int[] labels)
    {
        // create associative array to know index of each label
        int nLabels = labels.length;
        HashMap<Integer, Integer> labelIndices = LabelImages.mapLabelIndices(labels);

        // allocate memory for result
        int[] counts = new int[nLabels];
        double[] xcoords = new double[nLabels];
        double[] ycoords = new double[nLabels];

        // size of input image
        int sizeX = labelImage.size(0);
        int sizeY = labelImage.size(1);

        // compute centroid of each region
        for (int y = 0; y < sizeY; y++) 
        {
            for (int x = 0; x < sizeX; x++)
            {
                int label = labelImage.getInt(x, y);
                if (label == 0)
                    continue;

                // do not process labels that are not in the input list 
                if (!labelIndices.containsKey(label))
                    continue;
                
                int index = labelIndices.get(label);
                xcoords[index] += x;
                ycoords[index] += y;
                counts[index]++;
            }
        }

        // normalize by the number of pixels in each region
        // and convert to point array
        Point2D[] centroids = new Point2D[nLabels];
        for (int i = 0; i < nLabels; i++)
        {
            int nc = counts[i];
            centroids[i] = new Point2D(xcoords[i] / nc, ycoords[i] / nc);
        }
        
        return centroids;
    }
    
    public final static Box2D[] boundingBoxes(IntArray2D<?> image, int[] labels)
    {
        // create associative array to know index of each label
        int nLabels = labels.length;
        HashMap<Integer, Integer> labelIndices = LabelImages.mapLabelIndices(labels);

        // allocate memory for result
        double[] xmin = new double[nLabels];
        double[] xmax = new double[nLabels];
        double[] ymin = new double[nLabels];
        double[] ymax = new double[nLabels];
        for (int i = 0; i < nLabels; i++)
        {
        	xmin[i] = Double.POSITIVE_INFINITY; 
        	xmax[i] = Double.NEGATIVE_INFINITY;
        	ymin[i] = Double.POSITIVE_INFINITY; 
        	ymax[i] = Double.NEGATIVE_INFINITY;
        }

        // size of input image
        int sizeX = image.size(0);
        int sizeY = image.size(1);

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

                xmin[index] = Math.min(xmin[index], x - .5);
                xmax[index] = Math.max(xmax[index], x + .5);
                ymin[index] = Math.min(ymin[index], y - .5);
                ymax[index] = Math.max(ymax[index], y + .5);
            }
        }
        
        Box2D[] boxes = new Box2D[nLabels];
        for (int i = 0; i < nLabels; i++)
        {
        	boxes[i] = new Box2D(xmin[i], xmax[i], ymin[i], ymax[i]);
        }
        
        return boxes;
    }
    
    /**
     * Computes inertia ellipse of each region in input label image.
     * 
     * @param labelMap
     *            the input image containing label of particles
     * @param labels
     *            the list of labels to process
     * @return the inertia ellipsoids corresponding to each label, in pixel coordinates
     */
    public final static Ellipse2D[] inertiaEllipses(IntArray2D<?> labelMap, int[] labels)
    {
        // Check validity of parameters
        if (labelMap == null)
            return null;

        // size of image
        int sizeX = labelMap.size(0);
        int sizeY = labelMap.size(1);

        // extract particle labels
        int nLabels = labels.length;

        // create associative array to know index of each label
        HashMap<Integer, Integer> labelIndices = LabelImages.mapLabelIndices(labels);

        // allocate memory for result
        int[] counts = new int[nLabels];
        double[] cx = new double[nLabels];
        double[] cy = new double[nLabels];
        double[] Ixx = new double[nLabels];
        double[] Iyy = new double[nLabels];
        double[] Ixy = new double[nLabels];

        // compute the centroid of each region
        for (int y = 0; y < sizeY; y++) 
        {
            for (int x = 0; x < sizeX; x++)
            {
                int label = labelMap.getInt(x, y);
                if (label == 0)
                    continue;

                int index = labelIndices.get(label);
                cx[index] += x;
                cy[index] += y;
                counts[index]++;
            }
        }

        // normalize by the number of pixels in each region
        for (int i = 0; i < nLabels; i++)
        {
            cx[i] = cx[i] / counts[i];
            cy[i] = cy[i] / counts[i];
        }

        // compute the centered inertia matrix of each label
        for (int y = 0; y < sizeY; y++) 
        {
            for (int x = 0; x < sizeX; x++)
            {
                int label = labelMap.getInt(x, y);
                if (label == 0)
                    continue;

                int index = labelIndices.get(label);
                double x2 = x - cx[index];
                double y2 = y - cy[index];
                Ixx[index] += x2 * x2;
                Ixy[index] += x2 * y2;
                Iyy[index] += y2 * y2;
            }
        }

        // normalize by the number of pixels in each region
        for (int i = 0; i < nLabels; i++)
        {
            Ixx[i] = Ixx[i] / counts[i] + 1. / 12.;
            Ixy[i] = Ixy[i] / counts[i];
            Iyy[i] = Iyy[i] / counts[i] + 1. / 12.;
        }

        // Create data table
        Ellipse2D[] ellipses = new Ellipse2D[nLabels]; 

        // compute ellipse parameters for each region
        final double sqrt2 = sqrt(2);
        for (int i = 0; i < nLabels; i++) 
        {
            double xx = Ixx[i];
            double xy = Ixy[i];
            double yy = Iyy[i];

            // compute ellipse semi-axes lengths
            double common = sqrt((xx - yy) * (xx - yy) + 4 * xy * xy);
            double ra = sqrt2 * sqrt(xx + yy + common);
            double rb = sqrt2 * sqrt(xx + yy - common);

            // compute ellipse angle and convert into degrees
            double theta = Math.toDegrees(Math.atan2(2 * xy, xx - yy) / 2);
            ellipses[i] = new Ellipse2D(cx[i], cy[i], ra, rb, theta);
        }

        return ellipses;
    }
   
}
