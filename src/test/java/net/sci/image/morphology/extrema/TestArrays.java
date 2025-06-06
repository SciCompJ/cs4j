/**
 * 
 */
package net.sci.image.morphology.extrema;

import net.sci.array.numeric.Float32Array2D;
import net.sci.array.numeric.Float32Array3D;
import net.sci.array.numeric.Float64Array2D;
import net.sci.array.numeric.Float64Array3D;
import net.sci.array.numeric.UInt8Array2D;
import net.sci.array.numeric.UInt8Array3D;

/**
 * A collection of static methods for generating simple test images.
 */
public class TestArrays
{
    public static final UInt8Array2D createSimpleProfileArrray2D()
    {
        int[] values = new int[] {70, 20, 50, 40, 60, 30, 50, 10, 50, 40, 70};
        int nRows = 5;
        UInt8Array2D array = UInt8Array2D.create(values.length, nRows);
        array.fillInts((x,y) -> values[x]);
        return array;
    }
    
    public static final UInt8Array3D createSimpleProfileArrray3D()
    {
        int[] values = new int[] {70, 20, 50, 40, 60, 30, 50, 10, 50, 40, 70};
        int nRows = 5;
        UInt8Array3D array = UInt8Array3D.create(values.length, nRows, nRows);
        array.fillInts((x,y,z) -> values[x]);
        return array;
    }
    
    public static final UInt8Array2D create_ramp_7x5_UInt8()
    {
        int[] values = new int[] {10, 20, 30, 40, 50, 60, 70};
        int nRows = 5;
        UInt8Array2D array = UInt8Array2D.create(values.length, nRows);
        array.fillInts((x,y) -> values[x]);
        return array;
    }
    
    public static final Float32Array2D create_ramp_7x5_Float32()
    {
        double[] values = new double[] {0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7};
        int nRows = 5;
        Float32Array2D array = Float32Array2D.create(values.length, nRows);
        array.fillValues((x,y) -> values[x]);
        return array;
    }
    
    public static final Float64Array2D create_ramp_7x5_Float64()
    {
        double[] values = new double[] {0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7};
        int nRows = 5;
        Float64Array2D array = Float64Array2D.create(values.length, nRows);
        array.fillValues((x,y) -> values[x]);
        return array;
    }
    
    public static final UInt8Array3D create_ramp_7x5x5_UInt8()
    {
        int[] values = new int[] {10, 20, 30, 40, 50, 60, 70};
        int nRows = 5;
        UInt8Array3D array = UInt8Array3D.create(values.length, nRows, nRows);
        array.fillInts((x,y,z) -> values[x]);
        return array;
    }
    
    public static final Float32Array3D create_ramp_7x5x5_Float32()
    {
        double[] values = new double[] {0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7};
        int nRows = 5;
        Float32Array3D array = Float32Array3D.create(values.length, nRows, nRows);
        array.fillValues((x,y,z) -> values[x]);
        return array;
    }

    public static final Float64Array3D create_ramp_7x5x5_Float64()
    {
        double[] values = new double[] {0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7};
        int nRows = 5;
        Float64Array3D array = Float64Array3D.create(values.length, nRows, nRows);
        array.fillValues((x,y,z) -> values[x]);
        return array;
    }

    private TestArrays()
    {
    }
}
