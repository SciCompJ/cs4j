/**
 * 
 */
package net.sci.image.analyze.region2d;

import java.util.ArrayList;
import java.util.HashMap;

import net.sci.algo.AlgoStub;
import net.sci.array.binary.BinaryArray2D;
import net.sci.array.numeric.IntArray2D;
import net.sci.image.label.LabelImages;

/**
 * Computes histogram of binary configurations composed of 2-by-2 pixels (planar
 * images).
 * 
 * Implements the Algo interface, so the progress can be tracked.
 * 
 * @author dlegland
 *
 */
public class BinaryConfigurationsHistogram2D extends AlgoStub
{
    // ==================================================
    // Static methods
    
    /**
     * Applies look-up-table of values for each configuration, based on the
     * array of count for each binary configuration.
     * 
     * @param histogram
     *            the count of each type of 2-by-2 binary configurations, as an
     *            array
     * @param lut
     *            the value to associate to each configuration
     * @return the sum of the products of counts by the associated value
     */
    public static final double applyLut(int[] histogram, double[] lut)
    {
        double sum = 0;
        for (int i = 0; i < histogram.length; i++)
        {
            sum += histogram[i] * lut[i];
        }
        return sum;
    }
    
    /**
     * Applies look-up-table of values for each configuration, based on the
     * array of count for each binary configuration.
     * 
     * @param histogram
     *            the count of each type of 2-by-2 binary configurations, as an
     *            array
     * @param lut
     *            the value to associate to each configuration
     * @return the sum of the products of counts by the associated value
     */
    public static final int applyLut(int[] histogram, int[] lut)
    {
        int sum = 0;
        for (int i = 0; i < histogram.length; i++)
        {
            sum += histogram[i] * lut[i];
        }
        return sum;
    }
    
    /**
     * Applies look-up-table of values for each configuration for each label,
     * based on the 16-array of count for each binary configuration.
     * 
     * @param histograms
     *            the count of each type of 2-by-2 binary configuration of each
     *            label, as a nLabels-by-16 array
     * @param lut
     *            the value to associate to each configuration
     * @return the sum of the products of counts by the associated value for
     *         each label
     */
    public static final double[] applyLut(int[][] histograms, double[] lut)
    {
        double[] sums = new double[histograms.length];
        for (int iLabel = 0; iLabel < histograms.length; iLabel++)
        {
            sums[iLabel] = applyLut(histograms[iLabel], lut);
        }
        return sums;
    }
    
    /**
     * Applies look-up-table of values for each configuration for each label,
     * based on the 16-array of count for each binary configuration.
     * 
     * @param histograms
     *            the count of each type of 2-by-2 binary configuration of each
     *            label, as a nLabels-by-16 array
     * @param lut
     *            the value to associate to each configuration
     * @return the sum of the products of counts by the associated value for
     *         each label
     */
    public static final int[] applyLut(int[][] histograms, int[] lut)
    {
        int[] sums = new int[histograms.length];
        for (int iLabel = 0; iLabel < histograms.length; iLabel++)
        {
            sums[iLabel] = applyLut(histograms[iLabel], lut);
        }
        return sums;
    }

    
    // ==================================================
    // Constructors

    /**
     * Default empty constructor.
     */
    public BinaryConfigurationsHistogram2D()
    {
    }


    // ==================================================
    // General methods
    
    /**
     * Computes the histogram of binary configurations for the region within the
     * input binary image.
     * 
     * Takes into account the border of the image: histogram considers all the
     * 2-by-2 configurations that contain at least one pixel of the image.
     * 
     * @see #processInnerFrame(ImageProcessor)
     * 
     * @param binaryImage
     *            the input image containing the region the analyze
     * @return an array of integers containing the 16-elements histogram of
     *         binary configurations
     */
    public int[] process(BinaryArray2D binaryImage)
    {
        // initialize result
        int[] histogram = new int[16];
    
        // size of image
        int sizeX = binaryImage.size(0);
        int sizeY = binaryImage.size(1);
    
        // values of pixels within current 2-by-2 configuration
        // (first digit for y, second digit for x)
        boolean[] configValues = new boolean[4];
        
        // Iterate over all 2-by-2 configurations containing at least one pixel
        // within the image.
        // Current pixel is the lower-right pixel in configuration
        // (corresponding to b11).
        for (int y = 0; y < sizeY + 1; y++) 
        {
            this.fireProgressChanged(this, y, sizeY + 1);
    
            configValues[0] = false;
            configValues[2] = false;
            
            for (int x = 0; x < sizeX + 1; x++) 
            {
                // update pixel values of configuration
                configValues[1] = x < sizeX & y > 0 ? binaryImage.getBoolean(x, y - 1) : false;
                configValues[3] = x < sizeX & y < sizeY ? binaryImage.getBoolean(x, y) : false;
    
                // Compute index of local configuration
                int index = configIndex(configValues);
    
                // update histogram
                histogram[index]++;
    
                // update values of configuration for next iteration
                configValues[0] = configValues[1];
                configValues[2] = configValues[3];
            }
        }
    
        this.fireProgressChanged(this, 1, 1);
        return histogram;
    }
    
    /**
     * Applies a look-up-table for each of the 2x2 pixel configurations with all
     * pixels within the input binary image, and returns the sum of
     * contributions for each label.
     * 
     * This method is used for computing densities of Euler number, perimeter
     * and area from binary images.
     * 
     * @see #process(ImageProcessor)
     * 
     * @param binaryImage
     *            the input 2D binary image
     * @return an array of 16 integers containing the number of each binary
     *         configurations
     */
    public int[] processInnerFrame(BinaryArray2D binaryImage)
    {
        // initialize result
        int[] histogram = new int[16];
        
        // size of image
        int sizeX = binaryImage.size(0);
        int sizeY = binaryImage.size(1);

        // values of pixels within current 2-by-2 configuration
        // (first digit for y, second digit for x)
        boolean[] configValues = new boolean[4];

        // iterate on image pixel configurations
        for (int y = 1; y < sizeY; y++) 
        {
            this.fireProgressChanged(this, y, sizeY);
            
            configValues[0] = binaryImage.getBoolean(0, y - 1);
            configValues[2] = binaryImage.getBoolean(0,     y);

            for (int x = 1; x < sizeX; x++) 
            {
                // update pixel values of configuration
                configValues[1] = binaryImage.getBoolean(x, y - 1);
                configValues[3] = binaryImage.getBoolean(x,     y);

                // Compute index of local configuration
                int index = configIndex(configValues);

                // update histogram
                histogram[index]++;

                // update values of configuration for next iteration
                configValues[0] = configValues[1];
                configValues[2] = configValues[3];
            }
        }

        this.fireProgressChanged(this, 1, 1);
        return histogram;
    }

    private static final int configIndex(boolean[] configValues)
    {
        // Compute index of local configuration
        int index = 0;
        index += configValues[0] ? 1 : 0;
        index += configValues[1] ? 2 : 0;
        index += configValues[2] ? 4 : 0;
        index += configValues[3] ? 8 : 0;
        return index;
    }

    /**
     * Computes the histogram of binary configurations for each region of the
     * input label image.
     * 
     * Takes into account the border of the image: histograms consider all the
     * 2-by-2 configurations that contain at least one pixel of the image.
     * 
     * @param labelImage
     *            the input image containing region labels
     * @param labels
     *            the list of region labels
     * @return an array of integer containing for each region, the 16-elements
     *         histogram of binary configurations
     */
    public int[][] process(IntArray2D<?> labelImage, int[] labels)
    {
        // create associative array to know index of each label
        int nLabels = labels.length;
        HashMap<Integer, Integer> labelIndices = LabelImages.mapLabelIndices(labels);

        // initialize result
        int[][] histograms = new int[nLabels][16];

        // size of image
        int sizeX = labelImage.size(0);
        int sizeY = labelImage.size(1);

        // for each configuration of 2x2 pixels, we identify the labels
        ArrayList<Integer> localLabels = new ArrayList<Integer>(4);

        // values of pixels within current 2-by-2 configuration
        // (first digit for y, second digit for x)
        int[] configValues = new int[4];
        
        // Iterate over all 2-by-2 configurations containing at least one pixel
        // within the image.
        // Current pixel is the lower-right pixel in configuration
        // (corresponding to b11).
        for (int y = 0; y < sizeY + 1; y++) 
        {
            this.fireProgressChanged(this, y, sizeY + 1);

            configValues[0] = 0;
            configValues[2] = 0;
            
            for (int x = 0; x < sizeX + 1; x++) 
            {
                // update pixel values of configuration
                configValues[1] = x < sizeX & y > 0 ? labelImage.getInt(x, y - 1) : 0;
                configValues[3] = x < sizeX & y < sizeY ? labelImage.getInt(x, y) : 0;

                // identify labels in current config
                localLabels.clear();
                for (int label : configValues)
                {
                    // process only the requested labels
                    if (!labelIndices.containsKey(label)) continue;
                    
                    // keep only one instance of each label
                    if (!localLabels.contains(label))
                        localLabels.add(label);
                }

                // For each label, compute binary confi
                for (int label : localLabels) 
                {
                    // Compute index of local configuration
                    int index = configIndex(configValues, label);

                    // retrieve label index from label value
                    int labelIndex = labelIndices.get(label);

                    // update histogram of current label
                    histograms[labelIndex][index]++;
                }

                // update values of configuration for next iteration
                configValues[0] = configValues[1];
                configValues[2] = configValues[3];
            }
        }

        this.fireProgressChanged(this, 1, 1);
        return histograms;
    }

    private static final int configIndex(int[] configValues, int label)
    {
        // Compute index of local configuration
        int index = 0;
        index += configValues[0] == label ? 1 : 0;
        index += configValues[1] == label ? 2 : 0;
        index += configValues[2] == label ? 4 : 0;
        index += configValues[3] == label ? 8 : 0;
        return index;
    }
}
