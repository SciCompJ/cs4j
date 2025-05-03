/**
 * 
 */
package net.sci.array.binary.process;

import java.util.TreeMap;
import java.util.function.Function;

import net.sci.algo.AlgoStub;
import net.sci.array.binary.Binary;
import net.sci.array.binary.BinaryArray;
import net.sci.array.binary.BinaryArray2D;
import net.sci.array.binary.BinaryArray3D;
import net.sci.array.binary.BinaryRow;
import net.sci.array.binary.Run;
import net.sci.array.binary.RunLengthBinaryArray3D;
import net.sci.array.binary.RunLengthBinaryArrayFactory;
import net.sci.array.impl.ArrayWrapperStub;
import net.sci.array.numeric.ScalarArray;
import net.sci.array.numeric.ScalarArray2D;
import net.sci.array.numeric.ScalarArray3D;
import net.sci.array.numeric.process.ScalarArrayOperator;

/**
 * Converts a scalar array into a binary array, by using an inner conversion
 * function.
 *
 * Example:
 * {@snippet lang=java :
 * ScalarToBinary threshold20 = new ScalarToBinary(x -> x >= 20);
 * UInt8Array2D array = UInt8Array2D.create(10, 10);
 * array.fillInts((x,y) -> x + y * 10);
 * BinaryArray2D res = BinaryArray2D.wrap(threshold20.processScalar(array));
 * res.printContent(System.out);
 * }
 * @author dlegland
 *
 */
public class ScalarToBinary extends AlgoStub implements ScalarArrayOperator
{
    Function<Double,Boolean> fun;
    
    BinaryArray.Factory factory = BinaryArray.defaultFactory;
    
    /**
     * Creates a new converter, based on the default conversion function that
     * tests if the value is strictly greater than zero.
     */
    public ScalarToBinary()
    {
        this(x -> x > 0);
    }
    
    public ScalarToBinary setFactory(BinaryArray.Factory factory)
    {
        this.factory = factory;
        return this;
    }
    
    /**
     * Creates a new converter, using the specified conversion function.
     * 
     * @param the
     *            conversion function from a double value to boolean
     */
    public ScalarToBinary(Function<Double,Boolean> fun)
    {
        this.fun = fun;
    }
    
    /**
     * Creates a view on a scalar array by using the inner conversion function.
     * 
     * Syntax:
     * <pre>{@code
     * ScalarToBinary converter = new ScalarToBinary(fun);
     * BinaryArray view = converter.createView(array); 
     * }</pre>
     * 
     * @author dlegland
     *
     */
    public BinaryArray createView(ScalarArray<?> array)
    {
        return new View(array);
    }

    @Override
    public BinaryArray processScalar(ScalarArray<?> array)
    {
        // Dispatch to specialized methods depending on array dimensionality
        switch (array.dimensionality())
        {
            case 2:
            {
                return processScalar2d(ScalarArray2D.wrapScalar2d(array));
            }   
            case 3:
            {
                return processScalar3d(ScalarArray3D.wrapScalar3d(array));
            }   
            default:
            {
                return processScalarNd(array);
            }
        }
    }
    
    private BinaryArray2D processScalar2d(ScalarArray2D<?> array)
    {
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        BinaryArray2D res = BinaryArray2D.wrap(factory.create(sizeX, sizeY));
        
        for (int y = 0; y < sizeY; y++)
        {
            this.fireProgressChanged(this, y, sizeY);
            for (int x = 0; x < sizeX; x++)
            {
                res.setBoolean(x, y, this.fun.apply(array.getValue(x, y)));
            }
        }
        this.fireProgressChanged(this, sizeY, sizeY);
        return res;
    }
    
    private BinaryArray3D processScalar3d(ScalarArray3D<?> source)
    {
        if (factory instanceof RunLengthBinaryArrayFactory)
        {
            return processScalar3d_rle(source);
        }
        
        // retrieve array size
        int sizeX = source.size(0);
        int sizeY = source.size(1);
        int sizeZ = source.size(2);
        BinaryArray3D res = BinaryArray3D.wrap(factory.create(sizeX, sizeY, sizeZ));
//        RunLengthBinaryArray3D res = new RunLengthBinaryArray3D(sizeX, sizeY, sizeZ);
        
        // iterate over slices
        for (int z = 0; z < sizeZ; z++)
        {
            this.fireProgressChanged(this, z, sizeZ);
            
            for (int y = 0; y < sizeY; y++)
            {
                for (int x = 0; x < sizeX; x++)
                {
                    res.setBoolean(x, y, z, this.fun.apply(source.getValue(x, y, z)));
                }
            }
        }
        this.fireProgressChanged(this, sizeZ, sizeZ);
        return res;
    }
    
    private BinaryArray3D processScalar3d_rle(ScalarArray3D<?> array)
    {
        // retrieve array size
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        int sizeZ = array.size(2);
        RunLengthBinaryArray3D res = new RunLengthBinaryArray3D(sizeX, sizeY, sizeZ);
        
        // iterate over slices
        for (int z = 0; z < sizeZ; z++)
        {
            this.fireProgressChanged(this, z, sizeZ);
            
            for (int y = 0; y < sizeY; y++)
            {
                TreeMap<Integer,Run> runs = new TreeMap<Integer,Run>(); 
                
                int x1 = 0;
                currentRow:
                while (true)
                {
                    // find beginning of first run
                    while(!(this.fun.apply(array.getValue(x1, y, z))))
                    {
                        if (++x1 == sizeX) break currentRow;
                    }
                    
                    // find the end of current run
                    int x2 = x1;
                    while (this.fun.apply(array.getValue(x2, y, z)))
                    {
                        if (++x2 == sizeX) break;
                    }
                    
                    // keep current run, and look for next one
                    runs.put(x1, new Run(x1, x2 - 1));
                    if (x2 == sizeX) break;
                    x1 = x2;
                }
                
                // put row in target array.
                // if runs is empty, row will be removed from array
                res.setRow(y, z, new BinaryRow(runs));
            }
        }
        this.fireProgressChanged(this, sizeZ, sizeZ);
        return res;
    }
    
    private BinaryArray processScalarNd(ScalarArray<?> array)
    {
        BinaryArray res = factory.create(array.size());
        res.fillBooleans(pos -> fun.apply(array.getValue(pos)));
        return res;
    }
    
    private class View extends ArrayWrapperStub<Binary> implements BinaryArray
    {
        protected View(ScalarArray<?> array)
        {
            super(array);
        }

        @Override
        public boolean getBoolean(int[] pos)
        {
            return fun.apply(((ScalarArray<?>) array).getValue(pos));
        }

        @Override
        public void setBoolean(int[] pos, boolean state)
        {
            throw new RuntimeException("Can not modify a binary view of a scalar array");
        }
    }
}
