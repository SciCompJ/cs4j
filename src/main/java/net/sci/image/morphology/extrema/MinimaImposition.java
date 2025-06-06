/**
 * 
 */
package net.sci.image.morphology.extrema;

import net.sci.algo.AlgoStub;
import net.sci.array.binary.BinaryArray;
import net.sci.array.binary.BinaryArray2D;
import net.sci.array.binary.BinaryArray3D;
import net.sci.array.numeric.Float32;
import net.sci.array.numeric.Float32Array;
import net.sci.array.numeric.Float64;
import net.sci.array.numeric.Float64Array;
import net.sci.array.numeric.Int;
import net.sci.array.numeric.IntArray;
import net.sci.array.Array;
import net.sci.array.numeric.ScalarArray;
import net.sci.array.numeric.ScalarArray2D;
import net.sci.array.numeric.ScalarArray3D;
import net.sci.image.connectivity.Connectivity;
import net.sci.image.connectivity.Connectivity2D;
import net.sci.image.connectivity.Connectivity3D;
import net.sci.image.morphology.MorphologicalReconstruction.Type;
import net.sci.image.morphology.reconstruction.MorphologicalReconstruction2DHybrid;
import net.sci.image.morphology.reconstruction.MorphologicalReconstruction3DHybrid;
import net.sci.image.morphology.reconstruction.MorphologicalReconstructionHybridScalar;

/**
 * Imposition of minima on scalar images. Minima are specified by a binary array
 * with the same dimensions as the scalar array.
 * 
 * {@snippet lang="java" :
 * ScalarArray2D inputArray = sampleArray;
 * BinaryArray2D minima = sampleMinima;
 * MinimaImposition algo = new MinimaImposition(Connectivity2D.C4);
 * ScalarArray res = algo.processScalar(inputArray, minima);
 * }
 * 
 */
public class MinimaImposition extends AlgoStub
{
    // ==============================================================
    // Private constants

   /**
     * The default connectivity used by reconstruction algorithms in 3D arrays.
     */
    private final static Connectivity3D DEFAULT_CONNECTIVITY = Connectivity3D.C6;
    
    
    // ==============================================================
    // Class variables

    private Connectivity conn;
    
    
    // ==============================================================
    // Constructors

    /**
     * Default empty constructor.
     */
    public MinimaImposition()
    {
        this(DEFAULT_CONNECTIVITY);
    }
    
    /**
     * Default empty constructor.
     */
    public MinimaImposition(Connectivity conn)
    {
        this.conn = conn;
    }
    
    
    // ==============================================================
    // Processing methods

    /**
     * Imposes the minima given by marker array into the input array, using 
     * the specified connectivity.
     * 
     * @param array
     *            the array to process
     * @param minima
     *            a binary array of minima
     * @return the result of minima imposition
     */
    public ScalarArray<?> processScalar(ScalarArray<?> array, BinaryArray minima)
    {
        return switch (array.dimensionality())
        {
            case 2 -> processScalar2d(ScalarArray2D.wrap(array), BinaryArray2D.wrap(minima));
            case 3 -> processScalar3d(ScalarArray3D.wrap(array), BinaryArray3D.wrap(minima));
            default -> processScalarNd(array, minima);
        };
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
    public ScalarArray2D<?> processScalar2d(ScalarArray2D<?> array, BinaryArray2D minima)
    {
        Connectivity2D conn2d = this.conn != null ? Connectivity2D.convert(this.conn) : Connectivity2D.C4; 
        ScalarArray<?>[] data = initializeMarkerAndMask(array, minima);
        ScalarArray2D<?> result = ScalarArray2D.wrap(data[0]);
        
        MorphologicalReconstruction2DHybrid algo = new MorphologicalReconstruction2DHybrid(Type.BY_EROSION, conn2d);
        algo.processInPlace(result, ScalarArray2D.wrap(data[1]));
        return result;
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
    public ScalarArray3D<?> processScalar3d(ScalarArray3D<?> array, BinaryArray3D minima)
    {
        Connectivity3D conn3d = this.conn != null ? Connectivity3D.convert(this.conn) : Connectivity3D.C6; 
        ScalarArray<?>[] data = initializeMarkerAndMask(array, minima);
        ScalarArray3D<?> result = ScalarArray3D.wrap(data[0]);
        
        MorphologicalReconstruction3DHybrid algo = new MorphologicalReconstruction3DHybrid(Type.BY_EROSION, conn3d);
        algo.processInPlace(result, ScalarArray3D.wrap(data[1]));
        return result;
    }

    /**
     * Imposes the minima given by marker array into the input array, using 
     * the specified connectivity.
     * 
     * @param array
     *            the array to process
     * @param minima
     *            a binary array of minima
     * @return the result of minima imposition
     */
    public ScalarArray<?> processScalarNd(ScalarArray<?> array, BinaryArray minima)
    {
        int nd = array.dimensionality();
        Connectivity conn2 = this.conn != null ? Connectivity.convertDimensionality(this.conn, nd) : Connectivity.createOrtho(nd); 
        ScalarArray<?>[] data = initializeMarkerAndMask(array, minima);
        new MorphologicalReconstructionHybridScalar(Type.BY_EROSION, conn2).processInPlace(data[0], data[1]);
        return data[0];
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static final ScalarArray<?>[] initializeMarkerAndMask(ScalarArray<?> array, BinaryArray minima)
    {
        if (Int.class.isAssignableFrom(array.elementClass()))
        {
            return initializeMarkerAndMask_int(IntArray.wrap((Array<Int>) array), minima);
        }
        else if (array.elementClass() == Float32.class)
        {
            return initializeMarkerAndMask_float32(Float32Array.wrap(array), minima);
        }
        else if (array.elementClass() == Float64.class)
        {
            return initializeMarkerAndMask_float64(Float64Array.wrap(array), minima);
        }
        else
        {
            throw new RuntimeException("Can not process array with class: " + array.getClass().getName() + " and element type: " + array.elementClass().getName());
        }
    }
    
    private static final ScalarArray<?>[] initializeMarkerAndMask_int(IntArray<?> array, BinaryArray minima)
    {
        IntArray<?> marker = array.newInstance(array.size());
        IntArray<?> mask = array.newInstance(array.size());
        
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
        
        return new ScalarArray[] {marker, mask};
    }

    private static final ScalarArray<?>[] initializeMarkerAndMask_float32(Float32Array array, BinaryArray minima)
    {
        Float32Array marker = Float32Array.create(array.size());
        Float32Array mask = Float32Array.create(array.size());
        
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
        
        return new ScalarArray[] {marker, mask};
    }
    
    private static final ScalarArray<?>[] initializeMarkerAndMask_float64(Float64Array array, BinaryArray minima)
    {
        Float64Array marker = Float64Array.create(array.size());
        Float64Array mask = Float64Array.create(array.size());

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
        
        return new ScalarArray[] {marker, mask};
    }
}
