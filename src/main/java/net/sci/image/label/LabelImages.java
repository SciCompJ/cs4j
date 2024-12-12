/**
 * 
 */
package net.sci.image.label;

import static java.lang.Math.max;
import static java.lang.Math.min;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;

import net.sci.array.binary.BinaryArray;
import net.sci.array.binary.BinaryArray2D;
import net.sci.array.numeric.Float32Array2D;
import net.sci.array.numeric.Int;
import net.sci.array.numeric.IntArray;
import net.sci.array.numeric.IntArray2D;
import net.sci.array.numeric.IntArray3D;
import net.sci.array.numeric.ScalarArray;
import net.sci.array.numeric.ScalarArray2D;
import net.sci.image.binary.distmap.ChamferMask2D;
import net.sci.image.label.distmap.ChamferDistanceTransform2DFloat32;
import net.sci.image.label.distmap.ChamferDistanceTransform2DUInt16;
import net.sci.image.label.distmap.DistanceTransform2D;
import net.sci.image.label.geoddist.GeodesicDistanceTransform2D;
import net.sci.image.label.geoddist.GeodesicDistanceTransform2DFloat32Hybrid;
import net.sci.image.label.geoddist.GeodesicDistanceTransform2DUInt16Hybrid;

/**
 * A collection of static methods for processing label images.
 * 
 * @author dlegland
 *
 */
public class LabelImages
{
    // ==============================================================
    // Global methods
    
    /**
     * Returns the binary array with value equals to {@code true} only when the
     * corresponding value in the input array equals {@code label}.
     * 
     * @param labelMap
     *            the input label map
     * @param label
     *            the label of the region to binarize. Using a value equal to
     *            zero binarizes the background.
     * @return a binary image of the selected label.
     */
    public static final BinaryArray binarize(IntArray<?> labelMap, int label)
    {
        BinaryArray res = BinaryArray.create(labelMap.size());
        res.fillBooleans(pos -> labelMap.getInt(pos) == label);
        return res;
    }
    
    public static final <I extends Int<I>> IntArray<I> cropLabel(IntArray<I> labelMap, int label, int border)
    {
        // Compute bounds of region identified by label
        int[][] bounds = labelBounds(labelMap, label);
        
        // Compute size of result, taking into account border
        int nd = labelMap.dimensionality();
        int[] dims = new int[nd];
        for (int d = 0; d < nd; d++)
        {
            dims[d] = bounds[d][1] - bounds[d][0] + 1 + 2 * border; 
        }
        
        IntArray<I> res = labelMap.newInstance(dims);
        int[] pos2 = new int[nd];
        for (int[] pos : res.positions())
        {
            // compute position within original array
            for (int d = 0; d < nd; d++)
            {
                pos2[d] = pos[d] + bounds[d][0] - border;
            }
            if (labelMap.getInt(pos2) == label)
            {
                res.setInt(pos, label);
            }
        }

        return res;
    }
    
    private static final <I extends Int<I>> int[][] labelBounds(IntArray<I> labelMap, int label)
    {
        if (labelMap.dimensionality() == 2)
        {
            // Initialize label bounds
            int xmin = Integer.MAX_VALUE;
            int xmax = Integer.MIN_VALUE;
            int ymin = Integer.MAX_VALUE;
            int ymax = Integer.MIN_VALUE;

            int sizeX = labelMap.size(0);
            int sizeY = labelMap.size(1);
            IntArray2D<I> labelMap2d = IntArray2D.wrap(labelMap); 
            for (int y = 0; y < sizeY; y++)
            {
                for (int x = 0; x < sizeX; x++)
                {
                    // process only specified label
                    int val = labelMap2d.getInt(x, y);
                    if (val == label)
                    {
                        // update bounds of current label
                        xmin = min(xmin, x);
                        xmax = max(xmax, x);
                        ymin = min(ymin, y);
                        ymax = max(ymax, y);
                    }
                }
            }
            return new int[][] { { xmin, xmax }, { ymin, ymax } };
        }
        else if (labelMap.dimensionality() == 3)
        {
            // Initialize label bounds
            int xmin = Integer.MAX_VALUE;
            int xmax = Integer.MIN_VALUE;
            int ymin = Integer.MAX_VALUE;
            int ymax = Integer.MIN_VALUE;
            int zmin = Integer.MAX_VALUE;
            int zmax = Integer.MIN_VALUE;

            int sizeX = labelMap.size(0);
            int sizeY = labelMap.size(1);
            int sizeZ = labelMap.size(2);
            IntArray3D<I> labelMap3d = IntArray3D.wrap(labelMap); 
            for (int z = 0; z < sizeZ; z++)
            {
                for (int y = 0; y < sizeY; y++)
                {
                    for (int x = 0; x < sizeX; x++)
                    {
                        // process only specified label
                        int val = labelMap3d.getInt(x, y, z);
                        if (val == label)
                        {
                            // update bounds of current label
                            xmin = min(xmin, x);
                            xmax = max(xmax, x);
                            ymin = min(ymin, y);
                            ymax = max(ymax, y);
                            zmin = min(zmin, z);
                            zmax = max(zmax, z);
                        }
                    }
                }
            }
            return new int[][] { { xmin, xmax }, { ymin, ymax } , { zmin, zmax } };
        }
        else
        {
            throw new RuntimeException("Implemented onlty for dimensions 2 and 3");
        }
    }
    
    
    // ==============================================================
    // Utility methods
    
    /**
     * Returns the set of unique labels existing in the given image, excluding 
     * the value zero (used for background).
     * 
     * @param labelMap
     *            a label image
     * @return the list of unique labels present in image (without background)
     */
    public final static int[] findAllLabels(IntArray<?> labelMap)
    {
        TreeSet<Integer> labels = new TreeSet<Integer> ();
        
        // for integer arrays, uses an instance of IntArray.Iterator to avoid
        // creation of many Int instances
        IntArray.Iterator<?> iter = labelMap.iterator();
        while (iter.hasNext())
        {
            labels.add(iter.nextInt());
        }
        
        // remove 0 if it exists
        if (labels.contains(0))
        {
            labels.remove(0);
        }
        
        // convert to array of integers
        int[] array = new int[labels.size()];
        Iterator<Integer> iterator = labels.iterator();
        for (int i = 0; i < labels.size(); i++)
        {
            array[i] = iterator.next();
        }
        
        return array;
    }
    
    /**
     * Create associative array to retrieve the index corresponding to each label.
     * 
     * Usage:
     * <pre>{@code
     * int[] labels = new int[]{3, 4, 6, 7};
     * HashMap<Integer, Integer> labelInds = LabelImages.mapLabelIndices(labels);
     * int thirdLabel = labelInds.get(6);
     * // should return 2, the index of value 6 within original array of labels
     * }</pre>
     * 
     * @param labels
     *            an array of labels
     * @return a HashMap instance with each label as key, and the index of the
     *         label in array as value.
     */
    public static final HashMap<Integer, Integer> mapLabelIndices(int[] labels)
    {
        int nLabels = labels.length;
        HashMap<Integer, Integer> labelIndices = new HashMap<Integer, Integer>();
        for (int i = 0; i < nLabels; i++) 
        {
            labelIndices.put(labels[i], i);
        }

        return labelIndices;
    }

    /**
     * Computes the number of elements (pixels or voxels) composing each region
     * within the label map.
     * 
     * @param labelMap
     *            a label image containing region label of each element, or zero
     *            for background.
     * @param labels
     *            the array of region labels to process
     * @return an array the same size as labels, containing the number of
     *         elements of each region
     */
    public static final int[] elementCounts(IntArray<?> labelMap, int[] labels)
    {
        // create associative array to identify the index of each label
        HashMap<Integer, Integer> labelIndices = mapLabelIndices(labels);

        // initialize result
        int nLabels = labels.length;
        int[] counts = new int[nLabels];
    
        // iterate over array elements
        IntArray.Iterator<?> iter = labelMap.iterator();
        while(iter.hasNext())
        {
            int label = iter.nextInt();
            if (label == 0)
                continue;
            
            if (labelIndices.containsKey(label))
            {
                counts[labelIndices.get(label)]++;
            }
        }

        return counts;
    }
    
    /**
     * Creates a new array of integers containing only the specified labels.
     * 
     * @param labelMap
     *            a planar label image
     * @param labels
     *            the collection of label values to keep
     * @return a new int array containing only the specified labels
     */
    public static final <I extends Int<I>> IntArray<I> keepLabels(IntArray<I> labelMap,
            Collection<Integer> labelsToKeep)
    {
        IntArray<I> res = labelMap.newInstance(labelMap.size());
        
        for (int[] pos : res.positions())
        {
            int label = labelMap.getInt(pos);
            if (label == 0) continue;
            if (labelsToKeep.contains(label))
            {
                res.setInt(pos, label);
            }
        }
        
        return res;
    }
    
    /**
     * Creates a new array of integers containing only the specified labels.
     * 
     * @param labelMap
     *            a planar label image
     * @param labels
     *            the array of label values to keep
     * @return a new int array containing only the specified labels
     */
    public static final <I extends Int<I>> IntArray<I> keepLabels(IntArray<I> labelMap, int[] labels)
    {
        // convert int array to collection
        HashSet<Integer> labelSet = new HashSet<Integer>(labels.length);
        labelSet.addAll(Arrays.stream(labels).boxed().toList());
        
        // call the version of the method based on collection
        return keepLabels(labelMap, labelSet);
    }
    
    
    // ==============================================================
    // Distance maps
        
    /**
     * <p>
     * Computes the distance map (or distance transform) from a  2D label map.
     * Distance is computed for each foreground (white) pixel, as the
     * chamfer distance to the nearest background (black) pixel.
     * </p>
     * 
     * <p>
     * This method uses default 5x5 chamfer weights, and normalizes the
     * resulting map. Result is given in a new instance of IntArray2D.
     * </p>
     * 
     * @param labelMap
     *            the input array of labels
     * @return a new Array2D containing the distance map result
     */
    public static final ScalarArray2D<?> distanceMap2d(IntArray2D<?> labelMap) 
    {
        return distanceMap2d(labelMap, ChamferMask2D.CHESSKNIGHT, false, true);
    }
    
    /**
     * <p>
     * Computes the distance map from a boolean 2D array, by specifying
     * weights and normalization.
     * </p>
     * 
     * <p>
     * Distance is computed for each foreground (white) pixel, as the chamfer
     * distance to the nearest background (black) pixel. Result is given as a
     * new instance of IntArray2D.
     * </p>
     * 
     * @param labelMap
     *            the input array of labels
     * @param chamferMask
     *            the 2D chamfer mask to use for propagating distances
     * @param floatingPoint
     *            boolean flag indicating whether result should be provided as
     *            <code>Float32</code> (if true) or as <code>UInt16</code> (if
     *            false).
     * @param normalize
     *            indicates whether the resulting distance map should be
     *            normalized (divide distances by the first chamfer weight)
     * @return the distance map obtained after applying the distance transform
     */
    public static final ScalarArray2D<?> distanceMap2d(IntArray2D<?> labelMap,
            ChamferMask2D chamferMask, boolean floatingPoint, boolean normalize)
    {
        return DistanceTransform2D.create(chamferMask, floatingPoint, normalize).process2d(labelMap);
    }

    /**
     * <p>
     * Computes the distance map from a boolean 2D array, by specifying
     * weights and normalization.
     * </p>
     * 
     * <p>
     * Distance is computed for each foreground (white) pixel, as the chamfer
     * distance to the nearest background (black) pixel. Result is given as a
     * new instance of IntArray2D.
     * </p>
     * 
     * @param labelMap
     *            the input array of labels
     * @param weights
     *            an array of chamfer weights, with at least two values
     * @param normalize
     *            indicates whether the resulting distance map should be
     *            normalized (divide distances by the first chamfer weight)
     * @return the distance map obtained after applying the distance transform
     */
    public static final IntArray2D<?> distanceMap2d(IntArray2D<?> labelMap,
            short[] weights, boolean normalize)
    {
        ChamferDistanceTransform2DUInt16 algo = new ChamferDistanceTransform2DUInt16(weights, normalize);
        return algo.process2d(labelMap);
    }

    /**
     * <p>
     * Computes the distance map from a boolean array, by specifying
     * weights and normalization.
     * </p>
     * 
     * <p>
     * Distance is computed for each foreground (white) pixel, as the chamfer
     * distance to the nearest background (black) pixel. Result is given in a
     * new instance of FloatArray2D.
     * </p>
     * 
     * @param labelMap
     *            the input array of labels
     * @param weights
     *            an array of chamfer weights, with at least two values
     * @param normalize
     *            indicates whether the resulting distance map should be
     *            normalized (divide distances by the first chamfer weight)
     * @return the distance map obtained after applying the distance transform
     */
    public static final Float32Array2D distanceMap2d(IntArray2D<?> labelMap,
            float[] weights, boolean normalize) 
    {
        ChamferDistanceTransform2DFloat32 algo = new ChamferDistanceTransform2DFloat32(weights, normalize);
        return algo.process2d(labelMap);
    }

    
    // ==============================================================
    // Geodesic Distance maps
   
    /**
     * Computes the geodesic distance transform (or geodesic distance map) of a
     * binary image of marker, constrained to a mask of labels.
     * 
     * Returns the result in a new instance of ScalarArray.
     * 
     * @param marker
     *            the binary image of marker
     * @param labelMap
     *            the array of labels used as masks
     * @return the geodesic distance map in a new ScalarArray
     */
    public static final ScalarArray<?> geodesicDistanceMap2d(BinaryArray2D marker, IntArray2D<?> labelMap) 
    {
        return geodesicDistanceMap2d(marker, labelMap, ChamferMask2D.CHESSKNIGHT, true, true);
    }
    
    /**
     * Computes the geodesic distance transform (or geodesic distance map) of a
     * binary image of marker, constrained to a mask of labels. Returns the
     * result in a new instance of Float32Array2D or Int16Array2D, depending on
     * the value of the <code>floatingPoint</code> option.
     * 
     * @param marker
     *            the binary image of marker
     * @param mask
     *            the array of labels used as masks
     * @param chamferMask
     *            the 2D chamfer mask to use for propagating distances
     * @param floatingPoint
     *            boolean flag indicating whether result should be provided as
     *            <code>Float32</code> (if true) or as <code>UInt16</code> (if
     *            false).
     * @param normalize
     *            indicates whether the resulting distance map should be
     *            normalized (divide distances by the first chamfer weight)
     * @return the geodesic distance map in a new ScalarArray2D
     */
    public static final ScalarArray2D<?> geodesicDistanceMap2d(BinaryArray2D marker,
            IntArray2D<?> mask, ChamferMask2D chamferMask, boolean floatingPoint, boolean normalize) 
    {
        GeodesicDistanceTransform2D algo;
        algo = floatingPoint
                ? new GeodesicDistanceTransform2DFloat32Hybrid(chamferMask, normalize)
                : new GeodesicDistanceTransform2DUInt16Hybrid(chamferMask, normalize);
        return algo.process2d(marker, mask);
    }
    
    /**
     * Computes the geodesic distance transform (or geodesic distance map) of a
     * binary image of marker, constrained to a mask of labels.
     * Returns the result in a new instance of UInt16Array2D.
     * 
     * @param marker
     *            the binary image of marker
     * @param labelMap
     *            the array of labels used as masks
     * @param weights
     *            an array of chamfer weights, with at least two values
     * @param normalize
     *            indicates whether the resulting distance map should be
     *            normalized (divide distances by the first chamfer weight)
     * @return the geodesic distance map in a new ScalarArray
     */
    public static final ScalarArray2D<?> geodesicDistanceMap2d(BinaryArray2D marker,
            IntArray2D<?> labelMap, short[] weights, boolean normalize) 
    {
        ChamferMask2D mask = ChamferMask2D.fromWeights(weights);
        GeodesicDistanceTransform2D algo = new GeodesicDistanceTransform2DFloat32Hybrid(mask, normalize);
        return algo.process2d(marker, labelMap);
    }
    
    /**
     * Computes the geodesic distance transform (or geodesic distance map) of a
     * binary image of marker, constrained to a mask of labels.
     * Returns the result in a new instance of Float32Array2D.
     * 
     * @param marker
     *            the binary image of marker
     * @param labelMap
     *            the array of labels used as masks
     * @param weights
     *            an array of chamfer weights, with at least two values
     * @param normalize
     *            indicates whether the resulting distance map should be
     *            normalized (divide distances by the first chamfer weight)
     * @return the geodesic distance map in a new ImageProcessor
     */
    public static final ScalarArray2D<?> geodesicDistanceMap2d(BinaryArray2D marker,
            IntArray2D<?> labelMap, float[] weights, boolean normalize) 
    {
        ChamferMask2D mask = ChamferMask2D.fromWeights(weights);
        GeodesicDistanceTransform2D algo = new GeodesicDistanceTransform2DFloat32Hybrid(mask, normalize);
        return algo.process2d(marker, labelMap);
    }
   

    // ==============================================================
    // Constructor
    
    /**
     * Private constructor to prevent instantiation.
     */
    private LabelImages()
    {
    }    

}
