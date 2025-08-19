/**
 * 
 */
package net.sci.image.binary.distmap;

import net.sci.algo.AlgoStub;
import net.sci.array.ArrayOperator;
import net.sci.array.binary.BinaryArray;
import net.sci.array.binary.BinaryArray2D;
import net.sci.array.numeric.Float32Array;
import net.sci.array.numeric.ScalarArray;
import net.sci.array.numeric.ScalarArray2D;
import net.sci.array.numeric.UInt8Array2D;

/**
 * Computes Euclidean distance transform of a binary array using algorithm of
 * Saito and Toriwaki (1994). Uses floating point computations.
 */
public class SaitoToriwakiDistanceTransform2D extends AlgoStub implements ArrayOperator, DistanceTransform2D
{
    /**
     * The factory to use for creating the result array.
     */
    private ScalarArray.Factory<?> factory = Float32Array.defaultFactory;

    /**
     * Default empty constructor.
     */
    public SaitoToriwakiDistanceTransform2D()
    {
    }
    
    /**
     * Set the factory for creating the result array.
     * 
     * @param factory
     *            the factory for creating the result array.
     */
    public void setFactory(ScalarArray.Factory<?> factory)
    {
        this.factory = factory;
    }
    

    @Override
    public Result computeResult(BinaryArray array)
    {
        BinaryArray2D array2d = BinaryArray2D.wrap(array);
        ScalarArray2D<?> output = ScalarArray2D.wrapScalar2d(factory.create(array2d.size()));
        
        processStep1(array2d, output);
        double distMax = processStep2(array2d, output);

        // convert squared distance to distance
        output.apply(Math::sqrt, output);
        distMax = Math.sqrt(distMax);
        
        return new Result(output, distMax);
    }

    @Override
    public ScalarArray2D<?> process2d(BinaryArray2D array)
    {
        ScalarArray2D<?> output = ScalarArray2D.wrapScalar2d(factory.create(array.size()));
        
        processStep1(array, output);
        processStep2(array, output);

        // convert squared distance to distance
        output.apply(Math::sqrt, output);
        
        return output;
    }
    
    private void processStep1(BinaryArray2D array, ScalarArray2D<?> output)
    {
        // retrieve size of array
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        double absoluteMaximum = sizeX + sizeY;
        
        // forward scan
        for (int y = 0; y < sizeY; y++)
        {
            double df = absoluteMaximum;
            for (int x = 0; x < sizeX; x++)
            {
                df = array.getBoolean(x, y) ? df + 1 : 0;
                output.setValue(x, y, df * df);
            }
        }
        
        // backward scan
        for (int y = sizeY-1; y >= 0; y--)
        {
            double db = absoluteMaximum;
            for (int x = sizeX - 1; x >= 0; x--)
            {
                db = array.getBoolean(x, y) ? db + 1 : 0;
                output.setValue(x, y, Math.min(output.getValue(x, y), db * db));
            }
        }
    }

    private double processStep2(BinaryArray2D array, ScalarArray2D<?> output)
    {
        // retrieve size of array
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        
        double[] buffer = new double[sizeY];
        double distMax = 0.0;
        
        // iterate over y-columns of the array
        for (int x = 0; x < sizeX; x++)
        {
            // init buffer
            for (int y = 0; y < sizeY; y++)
            {
                buffer[y] = output.getValue(x, y);
            }
            
            for (int y = 0; y < sizeY; y++)
            {
                double dist = buffer[y];
                if (dist > 0)
                {
                    int rMax = (int) Math.ceil(Math.sqrt(dist) + 1);
                    int rStart = Math.min(rMax, y);
                    int rEnd = Math.min(rMax, sizeY - y);
                    
                    for (int n = -rStart; n < rEnd; n++)
                    {
                        double w = buffer[y + n] + n * n;
                        if (w < dist) dist = w;
                    }
                    
                    if (dist < buffer[y])
                    {
                        output.setValue(x, y, dist);
                    }
                    
                    if (dist > distMax)
                    {
                        distMax = dist;
                    }
                }
            }
        }
        
        return distMax;
    }
    
    
    public static final void main(String... args)
    {
        BinaryArray2D array = BinaryArray2D.create(20, 20);
        array.fillBooleans((x,y) -> Math.hypot(x-9.23, y-9.34) < 9.0);
        
        array.printContent();
        
        SaitoToriwakiDistanceTransform2D algo = new SaitoToriwakiDistanceTransform2D();
        algo.setFactory(UInt8Array2D.defaultFactory);
        
        ScalarArray2D<?> res = algo.process2d(array);
        res.printContent();
    }
}
