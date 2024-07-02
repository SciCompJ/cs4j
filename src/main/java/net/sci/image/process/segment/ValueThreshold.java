/**
 * 
 */
package net.sci.image.process.segment;


import net.sci.algo.AlgoStub;
import net.sci.array.binary.BinaryArray;
import net.sci.array.binary.BinaryArray2D;
import net.sci.array.binary.BinaryArray3D;
import net.sci.array.numeric.ScalarArray;
import net.sci.array.numeric.ScalarArray2D;
import net.sci.array.numeric.ScalarArray3D;
import net.sci.array.process.ScalarArrayOperator;
import net.sci.image.ImageArrayOperator;

/**
 * Thresholds an image, by retaining only values greater than or equal to a
 * given threshold value.
 * 
 * Can be replaced by the "ScalarToBinary" operator.
 * 
 * @see net.sci.array.process.type.ScalarToBinary
 * 
 * @author dlegland
 *
 */
public class ValueThreshold extends AlgoStub implements ImageArrayOperator, ScalarArrayOperator
{
    /**
     * The value to use for thresholding.
     */
	double value;
	
	/**
     * The flag indicating whether values larger than threshold will be set to
     * true (default is true).
     */
	boolean upperThreshold = true;
	
    /**
     * Creates a new instance of ValueThreshold.
     * 
     * @param value
     *            the value for threshold.
     */
    public ValueThreshold(double value)
    {
        this.value = value;
    }

    /**
     * Creates a new instance of ValueThreshold.
     * 
     * @param value
     *            the value for threshold.
     * @param upperThreshold
     *            boolean flag indicating whether values larger than threshold
     *            will be set to true (default is true).
     */
    public ValueThreshold(double value, boolean upperThreshold)
    {
        this.value = value;
        this.upperThreshold = upperThreshold;
    }

	public void processScalar(ScalarArray<?> source, BinaryArray target)
	{
	    switch (source.dimensionality())
	    {
            case 2:
            {
                if (this.upperThreshold)
                {
                    upperThreshold2d(ScalarArray2D.wrapScalar2d(source), BinaryArray2D.wrap(target));
                }
                else
                {
                    lowerThreshold2d(ScalarArray2D.wrapScalar2d(source), BinaryArray2D.wrap(target));
                }
                break;
            }   
            case 3:
            {
                if (this.upperThreshold)
                {
                    upperThreshold3d(ScalarArray3D.wrapScalar3d(source), BinaryArray3D.wrap(target));
                }
                else
                {
                    lowerThreshold3d(ScalarArray3D.wrapScalar3d(source), BinaryArray3D.wrap(target));
                }
                break;
            }   
            default:
            {
                if (this.upperThreshold)
                {
                    target.fillBooleans(pos -> source.getValue(pos) >= this.value);
                }
                else
                {
                    target.fillBooleans(pos -> source.getValue(pos) <= this.value);
                }
            }
	    }
	}
	
    private void upperThreshold2d(ScalarArray2D<?> source, BinaryArray2D target)
    {
        int sizeX = source.size(0);
        int sizeY = source.size(1);
        
        for (int y = 0; y < sizeY; y++)
        {
            this.fireProgressChanged(this, y, sizeY);
            for (int x = 0; x < sizeX; x++)
            {
                target.setBoolean(x,  y, source.getValue(x, y) >= this.value);
            }
        }
        this.fireProgressChanged(this, sizeY, sizeY);
    }
    
    private void lowerThreshold2d(ScalarArray2D<?> source, BinaryArray2D target)
    {
        int sizeX = source.size(0);
        int sizeY = source.size(1);
        
        for (int y = 0; y < sizeY; y++)
        {
            this.fireProgressChanged(this, y, sizeY);
            for (int x = 0; x < sizeX; x++)
            {
                target.setBoolean(x,  y, source.getValue(x, y) <= this.value);
            }
        }
        this.fireProgressChanged(this, sizeY, sizeY);
    }
    
    private void upperThreshold3d(ScalarArray3D<?> source, BinaryArray3D target)
    {
        int sizeX = source.size(0);
        int sizeY = source.size(1);
        int sizeZ = source.size(2);
        
        for (int z = 0; z < sizeZ; z++)
        {
            this.fireProgressChanged(this, z, sizeZ);
            
            for (int y = 0; y < sizeY; y++)
            {
                for (int x = 0; x < sizeX; x++)
                {
                    target.setBoolean(x, y, z, source.getValue(x, y, z) >= this.value);
                }
            }
        }
        this.fireProgressChanged(this, sizeZ, sizeZ);
    }

    private void lowerThreshold3d(ScalarArray3D<?> source, BinaryArray3D target)
    {
        int sizeX = source.size(0);
        int sizeY = source.size(1);
        int sizeZ = source.size(2);
        
        for (int z = 0; z < sizeZ; z++)
        {
            this.fireProgressChanged(this, z, sizeZ);
            
            for (int y = 0; y < sizeY; y++)
            {
                for (int x = 0; x < sizeX; x++)
                {
                    target.setBoolean(x, y, z, source.getValue(x, y, z) <= this.value);
                }
            }
        }
        this.fireProgressChanged(this, sizeZ, sizeZ);
    }

    @Override
    public BinaryArray processScalar(ScalarArray<?> array)
    {
        BinaryArray result = BinaryArray.create(array.size());
        processScalar(array, result);
        return result;
    }
}
