/**
 * 
 */
package net.sci.array.process.binary;

import java.util.function.BiFunction;

import net.sci.algo.AlgoStub;
import net.sci.array.Arrays;
import net.sci.array.binary.BinaryArray;
import net.sci.array.binary.BinaryArray2D;
import net.sci.array.binary.BinaryArray3D;
import net.sci.array.binary.BinaryRow;
import net.sci.array.binary.RunLengthBinaryArray2D;
import net.sci.array.binary.RunLengthBinaryArray3D;

/**
 * Base class for operators that combines the values from two binary arrays the
 * same size, and put the result in a new binary array.
 * 
 * The result scalar array can also be specified. 
 * 
 * Example
 * <pre>{@code
    // create operator to compute exclusive or from two arrays 
    LogicalBinaryOperator op = new LogicalBinaryOperator((a,b) -> a ^ b);
    // initialize demo arrays
    BinaryArray2D array1 = BinaryArray2D.create(8, 6);
    array1.fillBooleans((x,y) -> x >= 4);
    BinaryArray2D array2 = BinaryArray2D.create(8, 6);
    array2.fillBooleans((x,y) -> y >= 3);
    // Apply operator and display result
    BinaryArray2D res = BinaryArray2D.wrap(op.process(array1, array2));
    res.print(System.out);
 * }</pre>
 * 
 * @author dlegland
 *
 */
public class LogicalBinaryOperator extends AlgoStub
{
    // =============================================================
    // Constants
    
    /**
     * Specialization of the logical AND operator, that provides specialized
     * implementations for Run-Length encoded binary arrays.
     */
    public static final LogicalBinaryOperator AND = new LogicalBinaryOperator((a,b) -> a && b)
    {
        public BinaryArray process(BinaryArray array1, BinaryArray array2)
        {
            checkSameSize(array1, array2);
            
            if (array1.dimensionality() == 2)
            {
                return process2d(BinaryArray2D.wrap(array1), BinaryArray2D.wrap(array2));
            }
            else if (array1.dimensionality() == 3)
            {
                return process3d(BinaryArray3D.wrap(array1), BinaryArray3D.wrap(array2));
            }
            else
            {
                return super.process(array1, array2);
            }
            
        }

        private BinaryArray2D process2d(BinaryArray2D array1, BinaryArray2D array2)
        {
            if (array1 instanceof RunLengthBinaryArray2D && array2 instanceof RunLengthBinaryArray2D)
            {
                return process2d_rle((RunLengthBinaryArray2D) array1, (RunLengthBinaryArray2D) array2);
            }
            else
            {
                BinaryArray2D res = BinaryArray2D.create(array1.size(0), array1.size(1));
                return super.process2d(array1, array2, res);
            }
        }

        private BinaryArray2D process2d_rle(RunLengthBinaryArray2D array1, RunLengthBinaryArray2D array2)
        {
            int sizeX = array1.size(0); 
            int sizeY = array1.size(1);
            RunLengthBinaryArray2D res = new RunLengthBinaryArray2D(sizeX, sizeY);
            for (int y = 0; y < sizeY; y++)
            {
                this.fireProgressChanged(this, y, sizeY);
                
                BinaryRow row1 = array1.getRow(y);
                BinaryRow row2 = array2.getRow(y);
                // for AND operator, do not need to process empty rows
                if (row1 == null || row2 == null) continue;
                res.setRow(y, row1.intersection(row2));
            }
            this.fireProgressChanged(this, 1, 1);
            return res;
        }
        
        private BinaryArray3D process3d(BinaryArray3D array1, BinaryArray3D array2)
        {
            if (array1 instanceof RunLengthBinaryArray3D && array2 instanceof RunLengthBinaryArray3D)
            {
                return process3d_rle((RunLengthBinaryArray3D) array1, (RunLengthBinaryArray3D) array2);
            }
            else
            {
                BinaryArray3D res = BinaryArray3D.create(array1.size(0), array1.size(1), array1.size(2));
                return super.process3d(array1, array2, res);
            }
        }

        private BinaryArray3D process3d_rle(RunLengthBinaryArray3D array1, RunLengthBinaryArray3D array2)
        {
            int sizeX = array1.size(0); 
            int sizeY = array1.size(1);
            int sizeZ = array1.size(2);
            RunLengthBinaryArray3D res = new RunLengthBinaryArray3D(sizeX, sizeY, sizeZ);
            for (int z = 0; z < sizeZ; z++)
            {
                this.fireProgressChanged(this, z, sizeZ);
                for (int y = 0; y < sizeY; y++)
                {
                
                BinaryRow row1 = array1.getRow(y, z);
                BinaryRow row2 = array2.getRow(y, z);
                // for AND operator, do not need to process empty rows
                if (row1 == null || row2 == null) continue;
                res.setRow(y, z, row1.intersection(row2));
            }
            }
            this.fireProgressChanged(this, 1, 1);
            return res;
        }
    };

    /**
     * Specialization of the logical OR operator, that provides specialized
     * implementations for Run-Length encoded binary arrays.
     */
    public static final LogicalBinaryOperator OR = new LogicalBinaryOperator((a,b) -> a || b)
    {
        public BinaryArray process(BinaryArray array1, BinaryArray array2)
        {
            checkSameSize(array1, array2);
            
            if (array1.dimensionality() == 2)
            {
                return process2d(BinaryArray2D.wrap(array1), BinaryArray2D.wrap(array2));
            }
            else if (array1.dimensionality() == 3)
            {
                return process3d(BinaryArray3D.wrap(array1), BinaryArray3D.wrap(array2));
            }
            else
            {
                return super.process(array1, array2);
            }
            
        }

        private BinaryArray2D process2d(BinaryArray2D array1, BinaryArray2D array2)
        {
            if (array1 instanceof RunLengthBinaryArray2D && array2 instanceof RunLengthBinaryArray2D)
            {
                return process2d_rle((RunLengthBinaryArray2D) array1, (RunLengthBinaryArray2D) array2);
            }
            else
            {
                BinaryArray2D res = BinaryArray2D.create(array1.size(0), array1.size(1));
                return super.process2d(array1, array2, res);
            }
        }

        private BinaryArray2D process2d_rle(RunLengthBinaryArray2D array1, RunLengthBinaryArray2D array2)
        {
            int sizeX = array1.size(0); 
            int sizeY = array1.size(1);
            RunLengthBinaryArray2D res = new RunLengthBinaryArray2D(sizeX, sizeY);
            for (int y = 0; y < sizeY; y++)
            {
                this.fireProgressChanged(this, y, sizeY);
                
                BinaryRow row1 = array1.getRow(y);
                BinaryRow row2 = array2.getRow(y);
                if (row1 != null)
                {
                    if (row2 != null)
                    {
                        res.setRow(y, row1.union(row2));
                    }
                    else
                    {
                        res.setRow(y, row1.duplicate());
                    }
                }
                else
                {
                    if (row2 != null)
                    {
                        res.setRow(y, row2.duplicate());
                    }
                    // else: no need to work
                }
            }
            this.fireProgressChanged(this, 1, 1);
            return res;
        }
        
        private BinaryArray3D process3d(BinaryArray3D array1, BinaryArray3D array2)
        {
            if (array1 instanceof RunLengthBinaryArray3D && array2 instanceof RunLengthBinaryArray3D)
            {
                return process3d_rle((RunLengthBinaryArray3D) array1, (RunLengthBinaryArray3D) array2);
            }
            else
            {
                BinaryArray3D res = BinaryArray3D.create(array1.size(0), array1.size(1), array1.size(2));
                return super.process3d(array1, array2, res);
            }
        }

        private BinaryArray3D process3d_rle(RunLengthBinaryArray3D array1, RunLengthBinaryArray3D array2)
        {
            int sizeX = array1.size(0); 
            int sizeY = array1.size(1);
            int sizeZ = array1.size(2);
            RunLengthBinaryArray3D res = new RunLengthBinaryArray3D(sizeX, sizeY, sizeZ);
            for (int z = 0; z < sizeZ; z++)
            {
                this.fireProgressChanged(this, z, sizeZ);
                for (int y = 0; y < sizeY; y++)
                {

                    BinaryRow row1 = array1.getRow(y, z);
                    BinaryRow row2 = array2.getRow(y, z);
                    if (row1 != null)
                    {
                        if (row2 != null)
                        {
                            res.setRow(y, z, row1.union(row2));
                        }
                        else
                        {
                            res.setRow(y, z, row1.duplicate());
                        }
                    }
                    else
                    {
                        if (row2 != null)
                        {
                            res.setRow(y, z, row2.duplicate());
                        }
                        // else: no need to work
                    }
                }
            }
            this.fireProgressChanged(this, 1, 1);
            return res;
        }
    };

    
    // =============================================================
    // Inner class members
    
    /**
     * The function to apply to each pair of values.
     */
    BiFunction<Boolean, Boolean, Boolean> fun;
    

    // =============================================================
    // Constructor
    
    /**
     * Creates a new operator from a function that associates a boolean to a pair
     * of boolean values.
     * 
     * Example:
     * 
     * <pre>
     * {@code 
     * LogicalBinaryOperator op = new LogicalBinaryOperator((a,b) -> a | b);
     * }</pre>
     * 
     * @param fun
     *            the function defining this operator.
     */
    public LogicalBinaryOperator(BiFunction<Boolean,Boolean,Boolean> fun)
    {
        this.fun = fun;
    }

    
    // =============================================================
    // Implementation methods
    
    public BinaryArray process(BinaryArray array1, BinaryArray array2)
    {
        checkSameSize(array1, array2);
        
        if (array1.dimensionality() == 2)
        {
            BinaryArray2D res = BinaryArray2D.wrap(array1.newInstance(array1.size()));
            return process2d(BinaryArray2D.wrap(array1), BinaryArray2D.wrap(array2), res);
        }
        else if (array1.dimensionality() == 3)
        {
            BinaryArray3D res = BinaryArray3D.wrap(array1.newInstance(array1.size()));
            return process3d(BinaryArray3D.wrap(array1), BinaryArray3D.wrap(array2), res);
        }
        else
        {
            BinaryArray res = array1.newInstance(array1.size());
            res.fillBooleans(pos -> fun.apply(array1.getBoolean(pos), array2.getBoolean(pos)));
            return res;
        }
    }
    
    public BinaryArray process(BinaryArray array1, BinaryArray array2, BinaryArray output)
    {
        checkSameSize(array1, array2);
        checkSameSize(array1, output);
        
        if (array1.dimensionality() == 2)
        {
            return process2d(BinaryArray2D.wrap(array1), BinaryArray2D.wrap(array2), BinaryArray2D.wrap(output));
        }
        else if (array1.dimensionality() == 3)
        {
            return process3d(BinaryArray3D.wrap(array1), BinaryArray3D.wrap(array2), BinaryArray3D.wrap(output));
        }
        else
        {
            output.fillBooleans(pos -> fun.apply(array1.getBoolean(pos), array2.getBoolean(pos)));
            return output;
        }
    }

    private BinaryArray2D process2d(BinaryArray2D array1, BinaryArray2D array2, BinaryArray2D res)
    {
        int sizeX = array1.size(0);
        int sizeY = array1.size(1);
        for (int y = 0; y < sizeY; y++)
        {
            this.fireProgressChanged(this, y, sizeY);
            for (int x = 0; x < sizeX; x++)
            {
                res.setBoolean(x, y, fun.apply(array1.getBoolean(x, y), array2.getBoolean(x, y)));
            }
        }
        this.fireProgressChanged(this, 1, 1);
                    
        return res;
    }
    
    private BinaryArray3D process3d(BinaryArray3D array1, BinaryArray3D array2, BinaryArray3D res)
    {
        int sizeX = array1.size(0);
        int sizeY = array1.size(1);
        int sizeZ = array1.size(2);
        for (int z = 0; z < sizeZ; z++)
        {
            this.fireProgressChanged(this, z, sizeZ);
            for (int y = 0; y < sizeY; y++)
            {
                for (int x = 0; x < sizeX; x++)
                {
                    res.setBoolean(x, y, z, fun.apply(array1.getBoolean(x, y, z), array2.getBoolean(x, y, z)));
                }
            }
        }
        this.fireProgressChanged(this, 1, 1);
                    
        return res;
    }
    
    private static final void checkSameSize(BinaryArray array1, BinaryArray array2)
    {
        if (!Arrays.isSameDimensionality(array1, array2))
        {
            throw new IllegalArgumentException("Arrays must have same dimension");
        }
        if (!Arrays.isSameSize(array1, array2))
        {
            throw new IllegalArgumentException("Arrays must have same size");
        }
    }
}
