/**
 * 
 */
package net.sci.image.binary.distmap;

import net.sci.algo.AlgoStub;
import net.sci.array.ArrayOperator;
import net.sci.array.binary.BinaryArray;
import net.sci.array.binary.BinaryArray3D;
import net.sci.array.numeric.Float32Array;
import net.sci.array.numeric.ScalarArray;
import net.sci.array.numeric.ScalarArray3D;
import net.sci.array.numeric.UInt8Array2D;

/**
 * Computes Euclidean distance transform of a 3D binary array using algorithm of
 * Saito and Toriwaki (1994). Uses floating point computations.
 */
public class SaitoToriwakiDistanceTransform3D extends AlgoStub implements ArrayOperator, DistanceTransform3D
{
    /**
     * The factory to use for creating the result array.
     */
    private ScalarArray.Factory<?> factory = Float32Array.defaultFactory;

    /**
     * Default empty constructor.
     */
    public SaitoToriwakiDistanceTransform3D()
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
        BinaryArray3D array3d = BinaryArray3D.wrap(array);
        ScalarArray3D<?> output = ScalarArray3D.wrapScalar3d(factory.create(array3d.size()));
        
        processStep1(array3d, output);
        processStep2(array3d, output);
        double distMax = processStep3(array3d, output);

        // convert squared distance to distance
        output.apply(Math::sqrt, output);
        distMax = Math.sqrt(distMax);
        
        return new Result(output, distMax);
    }

    @Override
    public ScalarArray3D<?> process3d(BinaryArray3D array)
    {
        ScalarArray3D<?> output = ScalarArray3D.wrapScalar3d(factory.create(array.size()));
        
        processStep1(array, output);
        processStep2(array, output);
        processStep3(array, output);

        // convert squared distance to distance
        output.apply(Math::sqrt, output);
        
        return output;
    }
    
    private void processStep1(BinaryArray3D array, ScalarArray3D<?> output)
    {
        // retrieve size of array
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        int sizeZ = array.size(2);
        double absoluteMaximum = sizeX + sizeY;
        
        // forward scan
        for (int z = 0; z < sizeZ; z++)
        {
            for (int y = 0; y < sizeY; y++)
            {
                double df = absoluteMaximum;
                for (int x = 0; x < sizeX; x++)
                {
                    df = array.getBoolean(x, y, z) ? df + 1 : 0;
                    output.setValue(x, y, z, df * df);
                }
            }
        }
        
        // backward scan
        for (int z = 0; z < sizeZ; z++)
        {
            for (int y = sizeY - 1; y >= 0; y--)
            {
                double db = absoluteMaximum;
                for (int x = sizeX - 1; x >= 0; x--)
                {
                    db = array.getBoolean(x, y, z) ? db + 1 : 0;
                    output.setValue(x, y, z, Math.min(output.getValue(x, y, z), db * db));
                }
            }
        }
    }

    private void processStep2(BinaryArray3D array, ScalarArray3D<?> output)
    {
        // retrieve size of array
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        int sizeZ = array.size(2);

        double[] buffer = new double[sizeY];
        double distMax = 0.0;

        // iterate over y-columns of the array
        for (int z = 0; z < sizeZ; z++)
        {
            for (int x = 0; x < sizeX; x++)
            {
                // init buffer
                for (int y = 0; y < sizeY; y++)
                {
                    buffer[y] = output.getValue(x, y, z);
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
                            output.setValue(x, y, z, dist);
                        }

                        if (dist > distMax)
                        {
                            distMax = dist;
                        }
                    }
                }
            }
        }
    }

    private double processStep3(BinaryArray3D array, ScalarArray3D<?> output)
    {
        // retrieve size of array
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        int sizeZ = array.size(2);

        double[] buffer = new double[sizeZ];
        double distMax = 0.0;

        // iterate over y-columns of the array
        for (int y = 0; y < sizeY; y++)
        {
            for (int x = 0; x < sizeX; x++)
            {
                // init buffer
                for (int z = 0; z < sizeZ; z++)
                {
                    buffer[z] = output.getValue(x, y, z);
                }

                for (int z = 0; z < sizeZ; z++)
                {
                    double dist = buffer[z];
                    if (dist > 0)
                    {
                        int rMax = (int) Math.ceil(Math.sqrt(dist) + 1);
                        int rStart = Math.min(rMax, z);
                        int rEnd = Math.min(rMax, sizeZ - z);

                        for (int n = -rStart; n < rEnd; n++)
                        {
                            double w = buffer[z + n] + n * n;
                            if (w < dist) dist = w;
                        }

                        if (dist < buffer[z])
                        {
                            output.setValue(x, y, z, dist);
                        }

                        if (dist > distMax)
                        {
                            distMax = dist;
                        }
                    }
                }
            }
        }

        return distMax;
    }
    
    
    public static final void main(String... args)
    {
        BinaryArray3D array = BinaryArray3D.create(10, 10, 10);
        array.fillBooleans((x,y,z) -> Math.hypot(Math.hypot(x-4.23, y-4.34), z-4.45) < 4.0);
        
        array.printContent();
        
        SaitoToriwakiDistanceTransform3D algo = new SaitoToriwakiDistanceTransform3D();
        algo.setFactory(UInt8Array2D.defaultFactory);
        
        ScalarArray3D<?> res = algo.process3d(array);
        res.printContent();
    }
}
