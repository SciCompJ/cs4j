/**
 * 
 */
package net.sci.array.process.type;

import net.sci.algo.AlgoStub;
import net.sci.array.Array;
import net.sci.array.ArrayOperator;
import net.sci.array.color.RGB8Array;
import net.sci.array.color.RGB8Array2D;
import net.sci.array.color.RGB8Array3D;
import net.sci.array.scalar.ScalarArray;
import net.sci.array.scalar.ScalarArray2D;
import net.sci.array.scalar.ScalarArray3D;
import net.sci.array.scalar.UInt8;
import net.sci.array.scalar.UInt8Array;
import net.sci.array.scalar.UInt8Array2D;
import net.sci.array.scalar.UInt8Array3D;

/**
 * Converts an array into a UInt8Array the same size.
 * 
 * Provides specialized functions for the management of specific cases.
 *  
 * @see ConvertToBinary
 * 
 * @author dlegland
 *
 */
public class ConvertToUInt8 extends AlgoStub implements ArrayOperator
{
    @Override
    public <T> UInt8Array process(Array<T> array)
    {
        // Simply cast instances of UInt8Array
        if (array instanceof UInt8Array)
        {
            return (UInt8Array) array;
        }
        
        // Convert array containing UInt8 values
        if (array.dataType().isAssignableFrom(UInt8.class)) 
        {
            return processArrayOfUInt8(array);
        }
        
        // convert scalar array
        if (array instanceof ScalarArray<?>)
        {
            return processScalar((ScalarArray<?>) array);
        }
        
        // convert scalar array
        if (array instanceof RGB8Array)
        {
            return processRGB8((RGB8Array) array);
        }
        
        throw new IllegalArgumentException("Can not convert to UInt8Array array from class: " + array.getClass());
    }
    
    /**
     * Converts an array containing instances of UInt8 into a UInt8Array
     * instance.
     * 
     * @param array
     *            the array to convert
     * @return the UInt8Array resulting from the conversion.
     */
    private UInt8Array processArrayOfUInt8(Array<?> array)
    {
        UInt8Array result = UInt8Array.create(array.size());
        for (int[] pos : array.positions())
        {
            result.setByte(pos, ((UInt8) array.get(pos)).getByte());
        }
        return result;
    }
    
    /**
     * Converts a scalar array into a UInt8Array with same dimension, by
     * converting scalar values to UInt8 equivalent. Conversion is performed by
     * rounding.
     * 
     * @param array
     *            the array to convert.
     * @return the UInt8Array resulting from the conversion.
     */
    public UInt8Array processScalar(ScalarArray<?> array)
    {
        // dispatch processing according to dimensionality
        int nd = array.dimensionality();
        if (nd == 2)
        {
            return processScalar2d(ScalarArray2D.wrapScalar2d(array));
        }
        else if (nd == 3)
        {
            return processScalar3d(ScalarArray3D.wrapScalar3d(array));
        }
        
        // use generic version (without monitoring)
        UInt8Array result = UInt8Array.create(array.size());
        for (int[] pos : array.positions())
        {
            result.setInt(pos, UInt8.convert(array.getValue(pos)));
        }
        return result;
    }
    
    private UInt8Array2D processScalar2d(ScalarArray2D<?> array)
    {
        // allocate memory for result
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        UInt8Array2D result = UInt8Array2D.create(sizeX, sizeY);
        
        // iterate over elements
        for (int y = 0; y < sizeY; y++)
        {
            this.fireProgressChanged(this, y, sizeY);
            for (int x = 0; x < sizeX; x++)
            {
                result.setInt(x, y, UInt8.convert(array.getValue(x, y)));
            }
        }
        
        // return result
        return result;
    }
    
    private UInt8Array3D processScalar3d(ScalarArray3D<?> array)
    {
        // allocate memory for result
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        int sizeZ = array.size(2);
        UInt8Array3D result = UInt8Array3D.create(sizeX, sizeY, sizeZ);
        
        // iterate over elements
        for (int z = 0; z < sizeZ; z++)
        {
            this.fireProgressChanged(this, z, sizeZ);
            for (int y = 0; y < sizeY; y++)
            {
                for (int x = 0; x < sizeX; x++)
                {
                    result.setInt(x, y, z, UInt8.convert(array.getValue(x, y, z)));
                }
            }
        }

        // return result
        return result;
    }

    /**
     * Converts a scalar array into a UInt8Array, by considering a value range
     * that will be mapped to 0 and 255.
     * 
     * @param array
     *            the array to convert
     * @param minValue
     *            the value within input array that will be associated to 0 in
     *            result array
     * @param maxValue
     *            the value within input array that will be associated to 255 in
     *            result array
     * @return the converted UInt8Array
     */
    public UInt8Array processScalar(ScalarArray<?> array, double minValue, double maxValue)
    {
        // dispatch processing according to dimensionality
        int nd = array.dimensionality();
        if (nd == 2)
        {
            return processScalar2d(ScalarArray2D.wrapScalar2d(array), minValue, maxValue);
        }
        else if (nd == 3)
        {
            return processScalar3d(ScalarArray3D.wrapScalar3d(array), minValue, maxValue);
        }
        
        // use generic version (without monitoring)
        double k = 256.0 / (maxValue - minValue);
        UInt8Array result = UInt8Array.create(array.size());
        for (int[] pos : array.positions())
        {
            result.setInt(pos, (int) ((array.getValue(pos) - minValue) * k));
        }
        return result;
    }
    
    private UInt8Array2D processScalar2d(ScalarArray2D<?> array, double minValue, double maxValue)
    {
        // allocate memory for result
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        UInt8Array2D result = UInt8Array2D.create(sizeX, sizeY);
        
        // iterate over elements
        double k = 255.0 / (maxValue - minValue);
        for (int y = 0; y < sizeY; y++)
        {
            this.fireProgressChanged(this, y, sizeY);
            for (int x = 0; x < sizeX; x++)
            {
                result.setInt(x, y, (int) ((array.getValue(x, y) - minValue) * k));
            }
        }
        
        // return result
        return result;
    }
    
    private UInt8Array3D processScalar3d(ScalarArray3D<?> array, double minValue, double maxValue)
    {
        // allocate memory for result
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        int sizeZ = array.size(2);
        UInt8Array3D result = UInt8Array3D.create(sizeX, sizeY, sizeZ);
        
        // iterate over elements
        double k = 255.0 / (maxValue - minValue);
        for (int z = 0; z < sizeZ; z++)
        {
            this.fireProgressChanged(this, z, sizeZ);
            for (int y = 0; y < sizeY; y++)
            {
                for (int x = 0; x < sizeX; x++)
                {
                    result.setInt(x, y, z, (int)((array.getValue(x, y, z) - minValue) * k));
                }
            }
        }

        // return result
        return result;
    }
    
    /**
     * Converts a RGB8 array into a UInt8 array by retaining the maximum value
     * along channels.
     * 
     * @param array
     *            the color array to convert.
     * @return the result of the conversion.
     */
    public UInt8Array processRGB8(RGB8Array array)
    {
        // dispatch processing according to dimensionality, allowing to monitor
        // the progression of the conversion
        int nd = array.dimensionality();
        if (nd == 2)
        {
            return processRGB8_2d(RGB8Array2D.wrap(array));
        }
        else if (nd == 3)
        {
            return processRGB8_3d(RGB8Array3D.wrap(array));
        }
        
        // use generic processing
        UInt8Array result = UInt8Array.create(array.size());
        for (int[] pos : array.positions())
        {
            result.setInt(pos, array.getMaxSample(pos));
        }
        return result;
    }

    private UInt8Array2D processRGB8_2d(RGB8Array2D array)
    {
        // allocate memory for result
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        UInt8Array2D result = UInt8Array2D.create(sizeX, sizeY);
        
        // iterate over elements
        for (int y = 0; y < sizeY; y++)
        {
            this.fireProgressChanged(this, y, sizeY);
            for (int x = 0; x < sizeX; x++)
            {
                result.setInt(x, y, array.getMaxSample(x, y));
            }
        }
        
        // return result
        return result;
    }
    
    private UInt8Array3D processRGB8_3d(RGB8Array3D array)
    {
        // allocate memory for result
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        int sizeZ = array.size(2);
        UInt8Array3D result = UInt8Array3D.create(sizeX, sizeY, sizeZ);
        
        // iterate over elements
        for (int z = 0; z < sizeZ; z++)
        {
            this.fireProgressChanged(this, z, sizeZ);
            for (int y = 0; y < sizeY; y++)
            {
                for (int x = 0; x < sizeX; x++)
                {
                    result.setInt(x, y, z, array.getMaxSample(x, y, z));
                }
            }
        }

        // return result
        return result;
    }
}
