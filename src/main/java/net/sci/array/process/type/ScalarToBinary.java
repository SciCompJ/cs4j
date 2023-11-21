/**
 * 
 */
package net.sci.array.process.type;

import java.util.TreeMap;
import java.util.function.Function;

import net.sci.algo.AlgoStub;
import net.sci.array.binary.BinaryArray;
import net.sci.array.binary.BinaryArray2D;
import net.sci.array.binary.BinaryArray3D;
import net.sci.array.binary.BinaryRow;
import net.sci.array.binary.Run;
import net.sci.array.binary.RunLengthBinaryArray2D;
import net.sci.array.binary.RunLengthBinaryArray3D;
import net.sci.array.process.ScalarArrayOperator;
import net.sci.array.scalar.ScalarArray;
import net.sci.array.scalar.ScalarArray2D;
import net.sci.array.scalar.ScalarArray3D;

/**
 * Converts a scalar array into a binary array, by using an inner conversion
 * function.
 *
 * Example:
 * <pre>{@code
 * ScalarToBinary threshold20 = new ScalarToBinary(x -> x >= 20);
 * UInt8Array2D array = UInt8Array2D.create(10, 10);
 * array.fillInts((x,y) -> x + y * 10);
 * BinaryArray2D res = BinaryArray2D.wrap(threshold20.processScalar(array));
 * res.print(System.out);
 * }</pre>
 * @author dlegland
 *
 */
public class ScalarToBinary extends AlgoStub implements ScalarArrayOperator
{
    Function<Double,Boolean> fun;
    
    /**
     * Creates a new converter, based on the default conversion function that
     * tests if the value is strictly greater than zero.
     */
    public ScalarToBinary()
    {
        this(x -> x > 0);
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
        return new BinaryArray()
        {

            @Override
            public boolean getBoolean(int[] pos)
            {
                return fun.apply(array.getValue(pos));
            }

            @Override
            public void setBoolean(int[] pos, boolean state)
            {
                throw new RuntimeException("Can not modify a binary view of a scalar array");
            }
            
            @Override
            public int dimensionality()
            {
                return array.dimensionality();
            }

            @Override
            public int[] size()
            {
                return array.size();
            }

            @Override
            public int size(int dim)
            {
                return array.size(dim);
            }
        };
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
    
    private BinaryArray2D processScalar2d(ScalarArray2D<?> source)
    {
        int sizeX = source.size(0);
        int sizeY = source.size(1);
        RunLengthBinaryArray2D res = new RunLengthBinaryArray2D(sizeX, sizeY);
        
        for (int y = 0; y < sizeY; y++)
        {
            this.fireProgressChanged(this, y, sizeY);
            for (int x = 0; x < sizeX; x++)
            {
                res.setBoolean(x,  y, this.fun.apply(source.getValue(x, y)));
            }
        }
        this.fireProgressChanged(this, sizeY, sizeY);
        return res;
    }
    
    private BinaryArray3D processScalar3d(ScalarArray3D<?> source)
    {
        // retrieve array size
        int sizeX = source.size(0);
        int sizeY = source.size(1);
        int sizeZ = source.size(2);
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
                    while(!(this.fun.apply(source.getValue(x1, y, z))))
                    {
                        if (++x1 == sizeX) break currentRow;
                    }
                    
                    // find the end of current run
                    int x2 = x1;
                    while (this.fun.apply(source.getValue(x2, y, z)))
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
        BinaryArray res = BinaryArray.create(array.size());
        res.fillBooleans(pos -> fun.apply(array.getValue(pos)));
        return res;
    }
}
