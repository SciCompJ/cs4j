/**
 * 
 */
package net.sci.image.analyze;

import net.sci.algo.AlgoStub;
import net.sci.array.Array;
import net.sci.array.ArrayOperator;
import net.sci.array.scalar.Int32Array2D;
import net.sci.array.scalar.IntArray2D;
import net.sci.array.scalar.ScalarArray2D;

/**
 * @author dlegland
 *
 */
public class GrayLevelCooccurenceMatrix extends AlgoStub implements ArrayOperator
{
    int dx = 1;
    int dy = 0;
    
    /**
     * Empty constructor using default shift equal to (+1,0)
     */
    public GrayLevelCooccurenceMatrix()
    {
        // Nothing to do
    }
    
    /**
     * Constructors using the specified shift.
     */
    public GrayLevelCooccurenceMatrix(int[] shift)
    {
        if (shift.length < 2)
        {
            throw new IllegalArgumentException("Requires a vector of length 2");
        }
        this.dx = shift[0];
        this.dy = shift[1];
    }
    
    /* (non-Javadoc)
     * @see net.sci.array.ArrayOperator#process(net.sci.array.Array)
     */
    @Override
    public <T> IntArray2D<?> process(Array<T> array)
    {
        if (array instanceof ScalarArray2D)
        {
            return process2d((ScalarArray2D<?>) array);
        }
        
        throw new IllegalArgumentException("Requires an instance of ScalarArray2D as input");
    }
    
    public IntArray2D<?> process2d(ScalarArray2D<?> array)
    {
        // array size
        int sizeX = array.size(0);
        int sizeY = array.size(1);
       
        // compute iteration bounds
        int xmin = Math.max(0, -dx); 
        int xmax = Math.min(sizeX, sizeX-dx); 
        int ymin = Math.max(0, -dy); 
        int ymax = Math.min(sizeY, sizeY-dy);
       
        // allocate memory for result
        Int32Array2D result = Int32Array2D.create(256, 256);
        
        // iterate over pixel pairs
        for (int y = ymin; y < ymax; y++)
        {
            for (int x = xmin; x < xmax; x++)
            {
                int v1 = (int) Math.min(Math.max(array.getValue(x, y), 0), 255);
                int v2 = (int) Math.min(Math.max(array.getValue(x + dx, y + dy), 0), 255);
                
                result.setInt(v1, v2, result.getInt(v1, v2) + 1);
            }
        }
            
        return result;
    }
}
