/**
 * 
 */
package net.sci.image.morphology;

import net.sci.array.binary.BinaryArray;
import net.sci.array.binary.BinaryArray2D;
import net.sci.array.binary.BinaryArray3D;
import net.sci.array.numeric.Float32Array;
import net.sci.array.numeric.Float32Array2D;
import net.sci.array.numeric.Float32Array3D;
import net.sci.array.numeric.Float64Array;
import net.sci.array.numeric.Float64Array2D;
import net.sci.array.numeric.Float64Array3D;
import net.sci.array.numeric.IntArray;
import net.sci.array.numeric.IntArray2D;
import net.sci.array.numeric.IntArray3D;
import net.sci.array.numeric.ScalarArray2D;
import net.sci.array.numeric.ScalarArray3D;
import net.sci.array.numeric.process.AddValue;
import net.sci.image.Connectivity2D;
import net.sci.image.Connectivity3D;
import net.sci.image.morphology.extrema.RegionalExtrema2D;
import net.sci.image.morphology.extrema.RegionalExtrema3D;
import net.sci.image.morphology.reconstruction.MorphologicalReconstruction2D;
import net.sci.image.morphology.reconstruction.MorphologicalReconstruction2DHybrid;
import net.sci.image.morphology.reconstruction.MorphologicalReconstruction3D;
import net.sci.image.morphology.reconstruction.MorphologicalReconstruction3DHybrid;

/**
 * A collection of static methods for computing regional and extended minima and
 * maxima.
 * 
 * Regional extrema algorithms are based on flood-filling-like algorithms,
 * whereas extended extrema and extrema imposition algorithms use morphological
 * reconstruction algorithm.
 * 
 * See the books of Serra and Soille for further details.
 * 
 * <p>
 * Example of use:
 * 
 * <pre>
 * {@snippet lang="java" :
 * // Get current image processor
 * ScalarArray2D<?> image = IJ.getImage().getProcessor();
 * // Computes extended minima with a dynamic of 15, using the 4-connectivity
 * ScalarArray2D<?> minima = MinimaAndMaxima.extendedMinima(image, 15, 4); 
 * // Create a new image and display in a frame
 * Image res = new Image(minima, "Minima");
 * res.show();  
 * }</pre>
 * 
 * @see MorphologicalReconstruction
 * 
 * @author David Legland
 *
 */
public class MinimaAndMaxima
{
    // ==============================================================
    // Public enumerations

    /**
     * One of the two types of extrema.
     */
    public enum Type
    {
        MINIMA, MAXIMA;
    }
    

    // ==============================================================
    // Private constants

    /**
     * The default connectivity used by reconstruction algorithms in 2D arrays.
     */
    private final static Connectivity2D DEFAULT_CONNECTIVITY_2D = Connectivity2D.C4;
    
    /**
     * The default connectivity used by reconstruction algorithms in 3D arrays.
     */
    private final static Connectivity3D DEFAULT_CONNECTIVITY_3D = Connectivity3D.C6;
    

    // ==============================================================
    // Regional Minima and Maxima

    /**
     * Computes the regional maxima in grayscale array <code>array</code>, using
     * the default connectivity.
     * 
     * @param array
     *            the array to process
     * @return the regional maxima of input array
     */
    public final static BinaryArray2D regionalMaxima(ScalarArray2D<?> array)
    {
        return regionalMaxima(array, DEFAULT_CONNECTIVITY_2D);
    }

    /**
     * Computes the regional maxima in grayscale array <code>array</code>, using
     * the specified connectivity.
     * 
     * @param array
     *            the array to process
     * @param conn
     *            the connectivity for maxima, that should be either 4 or 8
     * @return the regional maxima of input array
     */
    public final static BinaryArray2D regionalMaxima(ScalarArray2D<?> array, Connectivity2D conn)
    {
        RegionalExtrema2D algo = new RegionalExtrema2D();
        algo.setConnectivity(conn);
        algo.setExtremaType(Type.MAXIMA);

        return algo.process(array);
    }

    /**
     * Computes the regional maxima in grayscale 3D array <code>array</code>,
     * using the default connectivity for 3D arrays.
     * 
     * @param array
     *            the array to process
     * @return the regional minima of input array
     */
    public final static BinaryArray3D regionalMaxima(ScalarArray3D<?> array) 
    {
        return regionalMaxima(array, DEFAULT_CONNECTIVITY_3D);
    }
    
    /**
     * Computes the regional maxima in 3D grayscale array <code>array</code>, 
     * using the specified connectivity.
     * 
     * @param array
     *            the arrayto process
     * @param conn
     *            the connectivity for minima, that should be either 4 or 8
     * @return the regional minima of input array
     */
    public final static BinaryArray3D regionalMaxima(ScalarArray3D<?> array, Connectivity3D conn) 
    {
        RegionalExtrema3D algo = new RegionalExtrema3D();
        algo.setConnectivity(conn);
        algo.setExtremaType(Type.MAXIMA);
        
        return algo.process(array);
    }

    /**
     * Computes the regional minima in grayscale array <code>array</code>, using
     * the default connectivity.
     * 
     * @param array
     *            the array to process
     * @return the regional minima of input array
     */
    public final static BinaryArray2D regionalMinima(ScalarArray2D<?> array)
    {
        return regionalMinima(array, DEFAULT_CONNECTIVITY_2D);
    }

    /**
     * Computes the regional minima in grayscale array <code>array</code>, using
     * the specified connectivity.
     * 
     * @param array
     *            the array to process
     * @param conn
     *            the connectivity for minima, that should be either 4 or 8
     * @return the regional minima of input array
     */
    public final static BinaryArray2D regionalMinima(ScalarArray2D<?> array, Connectivity2D conn) 
    {
        RegionalExtrema2D algo = new RegionalExtrema2D();
        algo.setConnectivity(conn);
        algo.setExtremaType(Type.MINIMA);
        
        return algo.process(array);
    }
    
    /**
     * Computes the regional minima in grayscale array <code>array</code>, 
     * using the default connectivity for 3D arrays.
     * 
     * @param array
     *            the array to process
     * @return the regional minima of input array
     */
    public final static BinaryArray3D regionalMinima(ScalarArray3D<?> array) 
    {
        return regionalMinima(array, DEFAULT_CONNECTIVITY_3D);
    }

    /**
     * Computes the regional minima in 3D grayscale array <code>array</code>, 
     * using the specified connectivity.
     * 
     * @param array
     *            the array to process
     * @param conn
     *            the connectivity for minima, that should be either 4 or 8
     * @return the regional minima of input array
     */
    public final static BinaryArray3D regionalMinima(ScalarArray3D<?> array, Connectivity3D conn) 
    {
        RegionalExtrema3D algo = new RegionalExtrema3D();
        algo.setConnectivity(conn);
        algo.setExtremaType(Type.MINIMA);
        
        return algo.process(array);
    }
    
    
    // ==============================================================
    // Extended Minima and Maxima

    /**
     * Computes the extended maxima in grayscale array <code>array</code>,
     * keeping maxima with the specified dynamic, and using the default
     * connectivity.
     * 
     * @param array
     *            the array to process
     * @param dynamic
     *            the minimal difference between a maxima and its boundary
     * @return the extended maxima of input array
     */
    public final static BinaryArray2D extendedMaxima(ScalarArray2D<?> array, double dynamic)
    {
        return extendedMaxima(array, dynamic, DEFAULT_CONNECTIVITY_2D);
    }

    /**
     * Computes the extended maxima in grayscale array <code>array</code>,
     * keeping maxima with the specified dynamic, and using the specified
     * connectivity.
     * 
     * @param array
     *            the array to process
     * @param dynamic
     *            the minimal difference between a maxima and its boundary
     * @param conn
     *            the connectivity for maxima, that should be either 4 or 8
     * @return the extended maxima of input array
     */
    public final static BinaryArray2D extendedMaxima(ScalarArray2D<?> array,
            double dynamic, Connectivity2D conn)
    {
        ScalarArray2D<?> mask = ScalarArray2D.wrapScalar2d(new AddValue(dynamic).createView(array));
        
        MorphologicalReconstruction2D algo = new MorphologicalReconstruction2DHybrid(
                MorphologicalReconstruction.Type.BY_DILATION, conn);
        ScalarArray2D<?> rec = algo.process(array, mask);
        
        return regionalMaxima(rec, conn);
    }

    /**
     * Computes the extended maxima in grayscale array <code>array</code>, 
     * keeping maxima with the specified dynamic, and using the default 
     * connectivity.
     * 
     * @param array
     *            the array to process
     * @param dynamic
     *            the minimal difference between a maxima and its boundary 
     * @return the extended maxima of input array
     */
    public final static BinaryArray3D extendedMaxima(ScalarArray3D<?> array,
            double dynamic)
    {
        return extendedMaxima(array, dynamic, DEFAULT_CONNECTIVITY_3D);
    }

    /**
     * Computes the extended maxima in grayscale array <code>array</code>, 
     * keeping maxima with the specified dynamic, and using the specified
     * connectivity.
     * 
     * @param array
     *            the array to process
     * @param dynamic
     *            the minimal difference between a maxima and its boundary 
     * @param conn
     *            the connectivity for maxima, that should be either 6 or 26
     * @return the extended maxima of input array
     */
    public final static BinaryArray3D extendedMaxima(ScalarArray3D<?> array,
            double dynamic, Connectivity3D conn)
    {
        ScalarArray3D<?> mask = ScalarArray3D.wrapScalar3d(new AddValue(dynamic).createView(array));
        
        MorphologicalReconstruction3D algo = new MorphologicalReconstruction3DHybrid(
                MorphologicalReconstruction.Type.BY_DILATION, conn);
        ScalarArray3D<?> rec = algo.process(array, mask);
        
        return regionalMaxima(rec, conn);
    }

    /**
     * Computes the extended minima in grayscale array <code>array</code>,
     * keeping minima with the specified dynamic, and using the default
     * connectivity.
     * 
     * @param array
     *            the array to process
     * @param dynamic
     *            the minimal difference between a minima and its boundary
     * @return the extended minima of input array
     */
    public final static BinaryArray2D extendedMinima(ScalarArray2D<?> array, double dynamic)
    {
        return extendedMinima(array, dynamic, DEFAULT_CONNECTIVITY_2D);
    }

    /**
     * Computes the extended minima in grayscale array <code>array</code>,
     * keeping minima with the specified dynamic, and using the specified
     * connectivity.
     * 
     * @param array
     *            the array to process
     * @param dynamic
     *            the minimal difference between a minima and its boundary
     * @param conn
     *            the connectivity for minima, that should be either 4 or 8
     * @return the extended minima of input array
     */
    public final static BinaryArray2D extendedMinima(ScalarArray2D<?> array, double dynamic, Connectivity2D conn)
    {
        ScalarArray2D<?> marker = ScalarArray2D.wrapScalar2d(new AddValue(dynamic).createView(array));

        MorphologicalReconstruction2D algo = new MorphologicalReconstruction2DHybrid(MorphologicalReconstruction.Type.BY_EROSION, conn);
        ScalarArray2D<?> rec = algo.process(marker, array);

        return regionalMinima(rec, conn);
    }

    /**
     * Computes the extended minima in grayscale array <code>array</code>,
     * keeping minima with the specified dynamic, and using the default
     * connectivity.
     * 
     * @param array
     *            the array to process
     * @param dynamic
     *            the minimal difference between a minima and its boundary
     * @return the extended minima of input array
     */
    public final static BinaryArray3D extendedMinima(ScalarArray3D<?> array,
            double dynamic)
    {
        return extendedMinima(array, dynamic, DEFAULT_CONNECTIVITY_3D);
    }

    /**
     * Computes the extended minima in grayscale array <code>array</code>, 
     * keeping minima with the specified dynamic, and using the specified 
     * connectivity.
     * 
     * @param array
     *            the array to process
     * @param dynamic
     *            the minimal difference between a minima and its boundary 
     * @param conn
     *            the connectivity for minima, that should be either 6 or 26
     * @return the extended minima of input array
     */
    public final static BinaryArray3D extendedMinima(ScalarArray3D<?> array,
            double dynamic, Connectivity3D conn)
    {
        ScalarArray3D<?> marker = ScalarArray3D.wrapScalar3d(new AddValue(dynamic).createView(array));
        
        MorphologicalReconstruction3D algo = new MorphologicalReconstruction3DHybrid(
                MorphologicalReconstruction.Type.BY_EROSION, conn);
        ScalarArray3D<?> rec = algo.process(marker, array);

        return regionalMinima(rec, conn);
    }
    

    // ==============================================================
    // Imposition of Minima and Maxima

    /**
     * Imposes the maxima given by marker array into the input array, using the
     * default connectivity.
     * 
     * @param array
     *            the array to process
     * @param maxima
     *            a binary array of maxima
     * @return the result of maxima imposition
     */
    public final static ScalarArray2D<?> imposeMaxima(ScalarArray2D<?> array, BinaryArray2D maxima)
    {
        return imposeMaxima(array, maxima, DEFAULT_CONNECTIVITY_2D);
    }

    /**
     * Imposes the maxima given by marker array into the input array, using the
     * specified connectivity.
     * 
     * @param array
     *            the array to process
     * @param maxima
     *            a binary array of maxima
     * @param conn
     *            the connectivity for maxima, that should be either 4 or 8
     * @return the result of maxima imposition
     */
    public final static ScalarArray2D<?> imposeMaxima(ScalarArray2D<?> array, BinaryArray2D maxima, Connectivity2D conn)
    {
        ScalarArray2D<?> marker;
        ScalarArray2D<?> mask;
        if (array instanceof IntArray2D)
        {
            marker = IntArray2D.wrap(array.newInstance(array.size()));
            mask = IntArray2D.wrap(array.newInstance(array.size()));
            initializeMarkerAndMask_maxima_int((IntArray<?>)array, maxima, (IntArray<?>) marker, (IntArray<?>) mask);
        }
        else if (array instanceof Float32Array)
        {
            marker = Float32Array2D.wrap(array.newInstance(array.size()));
            mask = Float32Array2D.wrap(array.newInstance(array.size()));
            initializeMarkerAndMask_maxima_float32((Float32Array) array, maxima, (Float32Array) marker, (Float32Array) mask);
        }
        else if (array instanceof Float64Array)
        {
            marker = Float64Array2D.wrap(array.newInstance(array.size()));
            mask = Float64Array2D.wrap(array.newInstance(array.size()));
            initializeMarkerAndMask_maxima_float64((Float64Array) array, maxima, (Float64Array) marker, (Float64Array) mask);
        }
        else
        {
            throw new RuntimeException("Can not process array with class: " + array.getClass().getName());
        }

        return MorphologicalReconstruction.reconstructByDilation(marker, mask, conn);
    }
    
    /**
     * Imposes the maxima given by marker array into the input array, using the
     * default connectivity.
     * 
     * @param array
     *            the array to process
     * @param maxima
     *            a binary array of maxima
     * @return the result of maxima imposition
     */
    public final static ScalarArray3D<?> imposeMaxima(ScalarArray3D<?> array, BinaryArray3D maxima)
    {
        return imposeMaxima(array, maxima, DEFAULT_CONNECTIVITY_3D);
    }
    
    /**
     * Imposes the maxima given by marker array into the input array, using
     * the specified connectivity.
     * 
     * @param array
     *            the array to process
     * @param maxima
     *            a binary array of maxima 
     * @param conn
     *            the connectivity for maxima, that should be either 4 or 8
     * @return the result of maxima imposition
     */
    public final static ScalarArray3D<?> imposeMaxima(ScalarArray3D<?> array, BinaryArray3D maxima,
            Connectivity3D conn)
    {
        ScalarArray3D<?> marker;
        ScalarArray3D<?> mask;
        if (array instanceof IntArray3D)
        {
            marker = IntArray3D.wrap(array.newInstance(array.size()));
            mask = IntArray3D.wrap(array.newInstance(array.size()));
            initializeMarkerAndMask_maxima_int((IntArray<?>)array, maxima, (IntArray<?>) marker, (IntArray<?>) mask);
        }
        else if (array instanceof Float32Array)
        {
            marker = Float32Array3D.wrap(array.newInstance(array.size()));
            mask = Float32Array3D.wrap(array.newInstance(array.size()));
            initializeMarkerAndMask_maxima_float32((Float32Array) array, maxima, (Float32Array) marker, (Float32Array) mask);
        }
        else if (array instanceof Float64Array)
        {
            marker = Float64Array3D.wrap(array.newInstance(array.size()));
            mask = Float64Array3D.wrap(array.newInstance(array.size()));
            initializeMarkerAndMask_maxima_float64((Float64Array) array, maxima, (Float64Array) marker, (Float64Array) mask);
        }
        else
        {
            throw new RuntimeException("Can not process array with class: " + array.getClass().getName());
        }
        
        return MorphologicalReconstruction.reconstructByDilation(marker, mask, conn);
    }

    /**
     * Imposes the minima given by marker array into the input array, using 
     * the default connectivity.
     * 
     * @param array
     *            the array to process
     * @param minima
     *            a binary array of minima 
     * @return the result of minima imposition
     */
    public final static ScalarArray2D<?> imposeMinima(ScalarArray2D<?> array, BinaryArray2D minima)
    {
        return imposeMinima(array, minima, DEFAULT_CONNECTIVITY_2D);
    }
    
    /**
     * Imposes the minima given by marker array into the input array, using 
     * the specified connectivity.
     * 
     * @param array
     *            the array to process
     * @param minima
     *            a binary array of minima 
     * @param conn
     *            the connectivity for minima, that should be either 4 or 8
     * @return the result of minima imposition
     */
    public final static ScalarArray2D<?> imposeMinima(ScalarArray2D<?> array, BinaryArray2D minima,
            Connectivity2D conn)
    {
        ScalarArray2D<?> marker;
        ScalarArray2D<?> mask;
        if (array instanceof IntArray2D)
        {
            marker = IntArray2D.wrap(array.newInstance(array.size()));
            mask = IntArray2D.wrap(array.newInstance(array.size()));
            initializeMarkerAndMask_minima_int((IntArray<?>)array, minima, (IntArray<?>) marker, (IntArray<?>) mask);
        }
        else if (array instanceof Float32Array)
        {
            marker = Float32Array2D.wrap(array.newInstance(array.size()));
            mask = Float32Array2D.wrap(array.newInstance(array.size()));
            initializeMarkerAndMask_minima_float32((Float32Array) array, minima, (Float32Array) marker, (Float32Array) mask);
        }
        else if (array instanceof Float64Array)
        {
            marker = Float64Array2D.wrap(array.newInstance(array.size()));
            mask = Float64Array2D.wrap(array.newInstance(array.size()));
            initializeMarkerAndMask_minima_float64((Float64Array) array, minima, (Float64Array) marker, (Float64Array) mask);
        }
        else
        {
            throw new RuntimeException("Can not process array with class: " + array.getClass().getName());
        }
        
        return MorphologicalReconstruction.reconstructByErosion(marker, mask, conn);
    }

    /**
     * Imposes the minima given by marker array into the input array, using the
     * default connectivity.
     * 
     * @param array
     *            the array to process
     * @param minima
     *            a binary array of minima
     * @return the result of minima imposition
     */
    public final static ScalarArray3D<?> imposeMinima(ScalarArray3D<?> array, BinaryArray3D minima)
    {
        return imposeMinima(array, minima, DEFAULT_CONNECTIVITY_3D);
    }

    /**
     * Imposes the minima given by marker array into the input array, using the
     * specified connectivity.
     * 
     * @param array
     *            the array to process
     * @param minima
     *            a binary array of minima
     * @param conn
     *            the connectivity for minima, that should be either 6 or 26
     * @return the result of minima imposition
     */
    public final static ScalarArray3D<?> imposeMinima(ScalarArray3D<?> array, BinaryArray3D minima, Connectivity3D conn)
    {
        ScalarArray3D<?> marker;
        ScalarArray3D<?> mask;
        if (array instanceof IntArray3D)
        {
            marker = IntArray3D.wrap(array.newInstance(array.size()));
            mask = IntArray3D.wrap(array.newInstance(array.size()));
            initializeMarkerAndMask_minima_int((IntArray<?>)array, minima, (IntArray<?>) marker, (IntArray<?>) mask);
        }
        else if (array instanceof Float32Array)
        {
            marker = Float32Array3D.wrap(array.newInstance(array.size()));
            mask = Float32Array3D.wrap(array.newInstance(array.size()));
            initializeMarkerAndMask_minima_float32((Float32Array) array, minima, (Float32Array) marker, (Float32Array) mask);
        }
        else if (array instanceof Float64Array)
        {
            marker = Float64Array3D.wrap(array.newInstance(array.size()));
            mask = Float64Array3D.wrap(array.newInstance(array.size()));
            initializeMarkerAndMask_minima_float64((Float64Array) array, minima, (Float64Array) marker, (Float64Array) mask);
        }
        else
        {
            throw new RuntimeException("Can not process array with class: " + array.getClass().getName());
        }
        
        return MorphologicalReconstruction.reconstructByErosion(marker, mask, conn);
    }

    private static final void initializeMarkerAndMask_maxima_int(IntArray<?> array, BinaryArray maxima, IntArray<?> marker, IntArray<?> mask)
    {
        for (int[] pos : array.positions())
        {
            if (maxima.getBoolean(pos))
            {
                marker.setInt(pos, Integer.MAX_VALUE);
                mask.setInt(pos, Integer.MAX_VALUE);
            }
            else
            {
                marker.setInt(pos, Integer.MIN_VALUE);
                mask.setInt(pos, array.getInt(pos) - 1);
            }
        }
    }

    private static final void initializeMarkerAndMask_maxima_float32(Float32Array array, BinaryArray maxima, Float32Array marker, Float32Array mask)
    {
        for (int[] pos : array.positions())
        {
            if (maxima.getBoolean(pos))
            {
                marker.setFloat(pos, Float.MAX_VALUE);
                mask.setFloat(pos, Float.MAX_VALUE);
            }
            else
            {
                marker.setFloat(pos, Float.NEGATIVE_INFINITY);
                mask.setFloat(pos, Math.nextDown(array.getFloat(pos)));
            }
        }
    }

    private static final void initializeMarkerAndMask_maxima_float64(Float64Array array, BinaryArray maxima, Float64Array marker, Float64Array mask)
    {
        for (int[] pos : array.positions())
        {
            if (maxima.getBoolean(pos))
            {
                marker.setValue(pos, Double.MAX_VALUE);
                mask.setValue(pos, Double.MAX_VALUE);
            }
            else
            {
                marker.setValue(pos, Double.NEGATIVE_INFINITY);
                mask.setValue(pos, Math.nextDown(array.getValue(pos)));
            }
        }
    }

    private static final void initializeMarkerAndMask_minima_int(IntArray<?> array, BinaryArray minima, IntArray<?> marker, IntArray<?> mask)
    {
        for (int[] pos : array.positions())
        {
            if (minima.getBoolean(pos))
            {
                marker.setInt(pos, Integer.MIN_VALUE);
                mask.setInt(pos, Integer.MIN_VALUE);
            }
            else
            {
                marker.setInt(pos, Integer.MAX_VALUE);
                mask.setInt(pos, array.getInt(pos) + 1);
            }
        }
    }

    private static final void initializeMarkerAndMask_minima_float32(Float32Array array, BinaryArray minima, Float32Array marker, Float32Array mask)
    {
        for (int[] pos : array.positions())
        {
            if (minima.getBoolean(pos))
            {
                marker.setFloat(pos, Float.NEGATIVE_INFINITY);
                mask.setFloat(pos, Float.NEGATIVE_INFINITY);
            }
            else
            {
                marker.setFloat(pos, Float.POSITIVE_INFINITY);
                mask.setFloat(pos, Math.nextUp(array.getFloat(pos)));
            }
        }
    }

    private static final void initializeMarkerAndMask_minima_float64(Float64Array array, BinaryArray minima, Float64Array marker, Float64Array mask)
    {
        for (int[] pos : array.positions())
        {
            if (minima.getBoolean(pos))
            {
                marker.setValue(pos, Double.NEGATIVE_INFINITY);
                mask.setValue(pos, Double.NEGATIVE_INFINITY);
            }
            else
            {
                marker.setValue(pos, Double.POSITIVE_INFINITY);
                mask.setValue(pos, Math.nextUp(array.getValue(pos)));
            }
        }
    }
    

    // ==============================================================
    // Constructor

    /**
     * Private constructor to prevent class instantiation.
     */
    private MinimaAndMaxima()
    {
    }
}
