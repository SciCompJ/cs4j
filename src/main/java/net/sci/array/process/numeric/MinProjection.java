/**
 * 
 */
package net.sci.array.process.numeric;

import java.util.Comparator;

import net.sci.array.Array;
import net.sci.array.Array2D;
import net.sci.array.generic.GenericArray2D;
import net.sci.array.scalar.ScalarArray;

/**
 * Computes minimum intensity projection along a specified dimension. 
 * 
 * Returns an array with the same number of dimension of the input. The size of
 * the output along the projection dimension is equal to 1.
 * 
 * @author dlegland
 *
 */
public class MinProjection extends ProjectionOperator
{
    // =============================================================
    // Constructor
    
    /**
     * Creates a new instance of MinProjection operator, that specifies the
     * dimension to project along.
     * 
     * @param dim
     *            the dimension for projection
     */
    public MinProjection(int dim)
    {
        super(dim);
    }

    
    // =============================================================
    // New methods
    
    public <C> Array<C> process(Array<C> array, Comparator<C> comparator)
    {
        int[] dims = computeOutputArrayDimensions(array);
        Array<C> res = array.newInstance(dims);
        process(array, res, comparator);
        return res;
    }
    
    public <C> void process(Array<? extends C> source, Array<? super C> target, Comparator<C> comparator)
    {
        // create position pointer for source image
        int nd = source.dimensionality();
        int[] srcPos = new int[nd];
        
        int indMax = source.size(this.dim);
        
        // iterate over positions in target image
        for (int[] pos : target.positions()) 
        {
            C min = source.get(pos);
            
            // convert to position in source image
            System.arraycopy(pos, 0, srcPos, 0, nd);
            
            // iterate over current line
            for (int i = 1; i < indMax; i++)
            {
                srcPos[this.dim] = i;
                C value = source.get(srcPos);
                if (comparator.compare(value, min) < 0)
                {
                    min = value;
                }
            }
            
            // copy value of selected position
            target.set(pos, min);
        }
    }


    // =============================================================
    // Implementation of ProjectionOperator interface
    
    public void processScalar(ScalarArray<?> source, ScalarArray<?> target)
    {
        // create position pointer for source image
        int nd = source.dimensionality();
        int[] srcPos = new int[nd];
        
        int indMax = source.size(this.dim);
        
        // iterate over positions in target image
        for (int[] pos : target.positions()) 
        {
            // convert to position in source image
            System.arraycopy(pos, 0, srcPos, 0, nd);
            
            double minValue = source.getValue(srcPos);
            
            // iterate over current line
            for (int i = 1; i < indMax; i++)
            {
                srcPos[this.dim] = i;
                minValue = Math.min(minValue, source.getValue(srcPos));
            }
            
            // copy value of selected position
            target.setValue(pos, minValue);
        }
    }
    
    public final static void main(String... args)
    {
        String[] digits = {"A", "B", "C", "D", "E", "F"};
        Array2D<String> array = GenericArray2D.create(6, 4, "");
        array.fill((x,y) -> digits[y] + digits[x]);
        System.out.println("input:");
        array.printContent(System.out);
        
        MinProjection proj = new MinProjection(1);
        Array<String> res = proj.process(array, Comparator.comparing((String x) -> x));
        
        System.out.println("output:");
        Array2D.wrap(res).printContent(System.out);
    }
}
