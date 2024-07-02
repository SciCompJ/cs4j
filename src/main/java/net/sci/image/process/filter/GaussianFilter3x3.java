/**
 * 
 */
package net.sci.image.process.filter;

import net.sci.algo.AlgoStub;
import net.sci.array.numeric.ScalarArray;
import net.sci.array.numeric.ScalarArray2D;
import net.sci.array.numeric.process.ScalarArrayOperator;
import net.sci.image.ImageArrayOperator;

/**
 * @author dlegland
 *
 */
public class GaussianFilter3x3 extends AlgoStub
        implements ImageArrayOperator, ScalarArrayOperator
{
    /**
     * Default empty constructor.
     */
    public GaussianFilter3x3()
    {
    }
    
    @Override
    public ScalarArray<?> processScalar(ScalarArray<?> array)
    {
        if (array.dimensionality() != 2)
        {
            throw new IllegalArgumentException("Requires a 2D scalar array as input");
        }
        ScalarArray2D<?> array2d = ScalarArray2D.wrapScalar2d(array).duplicate();

        processScalar2dInPlace(array2d);
        
        return array2d;
    }
    
    private void processScalar2dInPlace(ScalarArray2D<?> array)
    {
        // retrieve array size
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        
        double[] buffer = new double[3];

        // iteration along rows
        for (int y = 0; y < sizeY; y++)
        {
            // init buffer
            buffer[1] = array.getValue(0, y);
            buffer[2] = array.getValue(1, y);
            for (int x = 0; x < sizeX; x++)
            {
                // shift buffer
                buffer[0] = buffer[1];
                buffer[1] = buffer[2];
                
                // update last value in buffer
                int x2 = Math.min(x + 1, sizeX - 1);
                buffer[2] = array.getValue(x2, y);
                
                // compute weighted sum
                double sum = buffer[0] + 2 * buffer[1] + buffer[2];
                array.setValue(x, y, sum / 4);
            }
        }
        
        // iteration along columns
        for (int x = 0; x < sizeX; x++)
        {
            // init buffer
        	buffer[1] = array.getValue(x, 0);
            buffer[2] = array.getValue(x, 1);
            
            for (int y = 0; y < sizeY; y++)
            {
                // shift buffer
                buffer[0] = buffer[1];
                buffer[1] = buffer[2];
                
                // update last value in buffer
                int y2 = Math.min(y + 1, sizeY - 1);
                buffer[2] = array.getValue(x, y2);
                
                // compute weighted sum
                double sum = buffer[0] + 2 * buffer[1] + buffer[2];
                array.setValue(x, y, sum / 4);
            }
        }
    }
}
