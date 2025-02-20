/**
 * 
 */
package net.sci.image.analyze.region3d;

import java.util.ArrayList;
import java.util.HashMap;

import net.sci.algo.AlgoStub;
import net.sci.array.binary.BinaryArray3D;
import net.sci.array.numeric.IntArray3D;
import net.sci.image.label.LabelImages;

/**
 * Computes histogram of binary 2-by-2-by-2 configurations within a 3D image.
 * 
 * Implements the Algo interface, so the progress can be tracked.
 * 
 * @author dlegland
 *
 */
public class BinaryConfigurationsHistogram3D extends AlgoStub
{
    // ==================================================
    // Static methods
    
    /**
     * Applies look-up-table of values for each configuration, based on the
     * array of count for each binary configuration.
     * 
     * @param histogram
     *            the count of each type of 2-by-2-by-2 binary configurations,
     *            as a 256 array
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
     * Applies look-up-table of values for each configuration of each region,
     * based on the array of count for each binary configuration.
     * 
     * @param histograms
     *            the count of each type of 2-by-2-by-2 binary configurations,
     *            as a 256 array
     * @param lut
     *            the value to associate to each configuration
     * @return the sum of the products of counts by the associated value
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
    

    // ==================================================
    // Constructors

    /**
     * Empty constructor.
     */
    public BinaryConfigurationsHistogram3D()
    {
    }


    // ==================================================
    // General use methods

    /**
     * Applies a look-up-table for each of the 2x2x2 voxel configurations
     * containing at least one voxel of the input binary image, and returns the
     * sum of contributions for each label.
     * 
     * This method is used for computing Euler number, surface area and mean
     * beradth from binary images.
     * 
     * @param image
     *            the input 3D binary image
     * @return an array of 256 integers containing the number of each binary
     *         configurations
     */
    public int[] process(BinaryArray3D image)
    {   
        // Algorithm:
        // iterate on configurations of 2-by-2-by-2 voxels containing on voxel of 3D image. 
        // For each configuration, identify the labels within the configuration.
        // For each label, compute the equivalent binary configuration index, 
        // and adds is contribution to the measure associated to the label. 
        
        // create result histogram
        int[] histo = new int[256];

        // size of image
        int sizeX = image.size(0);
        int sizeY = image.size(1);
        int sizeZ = image.size(2);

        // values of pixels within current 2-by-2-by-2 configuration
        boolean[] configValues = new boolean[8];
        
        // Iterate over all 2-by-2-by-2 configurations containing at least one
        // voxel within the image.
        // Current pixel is the lower-right pixel in configuration
        // (corresponding to b111).
        for (int z = 0; z < sizeZ + 1; z++) 
        {
            this.fireProgressChanged(this, z, sizeZ + 1);
            
            for (int y = 0; y < sizeY + 1; y++) 
            {
                // initialize left voxels
                configValues[0] = false;
                configValues[2] = false;
                configValues[4] = false;
                configValues[6] = false;

                for (int x = 0; x < sizeX + 1; x++) 
                {
                    // update pixel values of configuration
                    if (x < sizeX)
                    {
                        configValues[1] = y > 0 & z > 0 ? image.getBoolean(x, y - 1, z - 1) : false;
                        configValues[3] = y < sizeY & z > 0 ? image.getBoolean(x, y, z - 1) : false;
                        configValues[5] = y > 0 & z < sizeZ ? image.getBoolean(x, y - 1, z) : false;
                        configValues[7] = y < sizeY & z < sizeZ ? image.getBoolean(x, y, z) : false;
                    }
                    else
                    {
                        // if reference voxel outside of image, the four new
                        // values are outside, and are set to background
                        configValues[1] = configValues[3] = configValues[5] = configValues[7] = false;   
                    }

                    // Compute index of local configuration
                    int index = configIndex(configValues);

                    // add the contribution of the configuration to the measure
                    histo[index]++;
                    
                    // update values of configuration for next iteration
                    configValues[0] = configValues[1];
                    configValues[2] = configValues[3];
                    configValues[4] = configValues[5];
                    configValues[6] = configValues[7];
                }
            }
        }
        
        this.fireProgressChanged(this, 1, 1);

        // return the histogram
        return histo;
    }

    /**
     * Applies a look-up-table for each of the 2x2x2 voxel configurations
     * with all voxels within the input binary image, and returns the
     * frequency count for each configuration.
     * 
     * This method is used for computing densities of Euler number, surface area
     * and mean breadth from binary images.
     * 
     * @see #process(BinaryArray3D)
     * 
     * @param array
     *            the input 3D binary array
     * @return an array of 256 integers containing the number of each binary
     *         configurations
     */
    public int[] processInnerFrame(BinaryArray3D array)
    {   
        // Algorithm:
        // iterate on configurations of 2-by-2-by-2 voxels fully contained within 3D image. 
        // For each configuration, identify the labels within the configuration.
        // For each label, compute the equivalent binary configuration index, 
        // and adds is contribution to the measure associated to the label. 
        
        // size of image
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        int sizeZ = array.size(2);
    
        // create result histogram
        int[] histo = new int[256];
        
        // values of pixels within current 2-by-2-by-2 configuration
        boolean[] configValues = new boolean[8];
        
        // Iterate over all 2-by-2-by-2 configurations containing at least one
        // voxel within the image.
        // Current pixel is the lower-right voxel in configuration
        // (corresponding to b111).
        for (int z = 1; z < sizeZ; z++) 
        {
            this.fireProgressChanged(this, z, sizeZ + 1);
            
            for (int y = 1; y < sizeY; y++) 
            {
                // initialize left voxels
                configValues[0] = array.getBoolean(0, y - 1, z - 1);
                configValues[2] = array.getBoolean(0, y, z - 1);
                configValues[4] = array.getBoolean(0, y - 1, z);
                configValues[6] = array.getBoolean(0, y, z);
    
                for (int x = 1; x < sizeX; x++) 
                {
                    // update pixel values of configuration
                    configValues[1] = array.getBoolean(x, y - 1, z - 1);
                    configValues[3] = array.getBoolean(x, y, z - 1);
                    configValues[5] = array.getBoolean(x, y - 1, z);
                    configValues[7] = array.getBoolean(x, y, z);
    
                    // Compute index of local configuration
                    int index = configIndex(configValues);
    
                    // add the contribution of the configuration to the measure
                    histo[index]++;
                    
                    // update values of configuration for next iteration
                    configValues[0] = configValues[1];
                    configValues[2] = configValues[3];
                    configValues[4] = configValues[5];
                    configValues[6] = configValues[7];
                }
            }
        }
        
        this.fireProgressChanged(this, 1, 1);

        // return the histogram
        return histo;
    }

    private static final int configIndex(boolean[] configValues)
    {
        // Compute index of local configuration
        int index = 0;
        index += configValues[0] ?   1 : 0;
        index += configValues[1] ?   2 : 0;
        index += configValues[2] ?   4 : 0;
        index += configValues[3] ?   8 : 0;
        index += configValues[4] ?  16 : 0;
        index += configValues[5] ?  32 : 0;
        index += configValues[6] ?  64 : 0;
        index += configValues[7] ? 128 : 0;
        return index;
    }

    /**
     * Applies a look-up-table for each of the 2x2x2 voxel configurations
     * containing at least one voxel of the input image, and returns the sum of
     * contributions for each label.
     * 
     * This method is used for computing Euler number and surface area.
     * 
     * @see #processInnerFrame(ImageStack)
     * 
     * @param image
     *            the input 3D image of labels
     * @param labels
     *            the set of labels to process
     * @return an array of nLabels-by-256 integers containing the number of
     *         binary configurations for each label
     */
    public int[][] process(IntArray3D<?> image, int[] labels)
    {   
        // Algorithm:
        // iterate on configurations of 2-by-2-by-2 voxels containing on voxel of 3D image. 
        // For each configuration, identify the labels within the configuration.
        // For each label, compute the equivalent binary configuration index, 
        // and adds is contribution to the measure associated to the label. 
        
        // create associative array to know index of each label
        HashMap<Integer, Integer> labelIndices = LabelImages.mapLabelIndices(labels);

        // initialize the result array containing one measure for each label
        int nLabels = labels.length;
        int[][] histos = new int[nLabels][256];

        // size of image
        int sizeX = image.size(0);
        int sizeY = image.size(1);
        int sizeZ = image.size(2);

        // for each configuration of 2x2x2 voxels, we identify the labels
        ArrayList<Integer> localLabels = new ArrayList<Integer>(8);
        
        // values of pixels within current 2-by-2-by-2 configuration
        int[] configValues = new int[8];
        
        // Iterate over all 2-by-2-by-2 configurations containing at least one
        // voxel within the image.
        // Current pixel is the lower-right pixel in configuration
        // (corresponding to b111).
        for (int z = 0; z < sizeZ + 1; z++) 
        {
            this.fireProgressChanged(this, z, sizeZ + 1);

            for (int y = 0; y < sizeY + 1; y++) 
            {
                // initialize left voxels
                configValues[0] = 0;
                configValues[2] = 0;
                configValues[4] = 0;
                configValues[6] = 0;

                for (int x = 0; x < sizeX + 1; x++) 
                {
                    // update pixel values of configuration
                    if (x < sizeX)
                    {
                        configValues[1] = y > 0 & z > 0 ? image.getInt(x, y - 1, z - 1) : 0;
                        configValues[3] = y < sizeY & z > 0 ? image.getInt(x, y, z - 1) : 0;
                        configValues[5] = y > 0 & z < sizeZ ? image.getInt(x, y - 1, z) : 0;
                        configValues[7] = y < sizeY & z < sizeZ ? image.getInt(x, y, z) : 0;
                    }
                    else
                    {
                        // if reference voxel outside of image, the four new
                        // values are outside, and are set to zero
                        configValues[1] = configValues[3] = configValues[5] = configValues[7] = 0;   
                    }

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

                        // add the contribution of the configuration to the
                        // accumulator for the label
                        histos[labelIndex][index]++;
                    }
                    
                    // update values of configuration for next iteration
                    configValues[0] = configValues[1];
                    configValues[2] = configValues[3];
                    configValues[4] = configValues[5];
                    configValues[6] = configValues[7];
                }
            }
        }
        
        this.fireProgressChanged(this, 1, 1);

        // return the histogram
        return histos;
    }

    private static final int configIndex(int[] configValues, int label)
    {
        // Compute index of local configuration
        int index = 0;
        index += configValues[0] == label ?   1 : 0;
        index += configValues[1] == label ?   2 : 0;
        index += configValues[2] == label ?   4 : 0;
        index += configValues[3] == label ?   8 : 0;
        index += configValues[4] == label ?  16 : 0;
        index += configValues[5] == label ?  32 : 0;
        index += configValues[6] == label ?  64 : 0;
        index += configValues[7] == label ? 128 : 0;
        return index;
    }
}
