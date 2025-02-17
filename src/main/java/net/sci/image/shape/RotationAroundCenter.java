/**
 * 
 */
package net.sci.image.shape;

import net.sci.algo.AlgoStub;
import net.sci.array.Array;
import net.sci.array.numeric.Float32Array2D;
import net.sci.array.numeric.ScalarArray;
import net.sci.array.numeric.ScalarArray2D;
import net.sci.array.numeric.VectorArray;
import net.sci.array.numeric.VectorArray2D;
import net.sci.array.numeric.interp.LinearInterpolatedArray2D;
import net.sci.image.ImageArrayOperator;

/**
 * @author dlegland
 *
 */
public class RotationAroundCenter extends AlgoStub implements ImageArrayOperator
{
	/**
	 * The rotation angle, in degrees counter-clockwise.
	 */
	double angle;
	
	/**
	 * 
	 * @param angle The rotation angle, in degrees counter-clockwise.
	 */
	public RotationAroundCenter(double angle)
	{
		this.angle = angle;
	}

	public void process(Array<?> source, Array<?> target)
	{
		if (source instanceof ScalarArray2D && target instanceof ScalarArray2D)
		{
			processScalar2d((ScalarArray2D<?>) source, (ScalarArray2D<?>) target);
		}
		else if (source instanceof VectorArray2D && target instanceof VectorArray2D)
		{
			processVector2d((VectorArray2D<?,?>) source, (VectorArray2D<?,?>) target);
		}
		else
		{
			throw new RuntimeException("Unable to compute array rotation");
		}
	}

	public void processScalar2d(ScalarArray2D<?> source, ScalarArray2D<?> target)
	{
		// Create interpolator for input array
		LinearInterpolatedArray2D interp = new LinearInterpolatedArray2D(source);
				
		// output array size
		int sizeX = target.size(0);
		int sizeY = target.size(1);
		
		// compute transform parameters
		double angle = Math.toRadians(this.angle);
		double cosTheta = Math.cos(angle);
		double sinTheta = Math.sin(angle);
		
		double centerSourceX = source.size(0) * .5;
		double centerSourceY = source.size(1) * .5;
		double centerTargetX = sizeX * .5;
		double centerTargetY = sizeY * .5;
		
		// compute interpolated transformed image
		for (int y = 0; y < sizeY; y++)
		{
			for (int x = 0; x < sizeX; x++)
			{
				// recenter current point
				double xc = x - centerTargetX;
				double yc = y - centerTargetY;
				
				// compute rotation and replace to source center 
				double x2 = xc * cosTheta + yc * sinTheta + centerSourceX;
				double y2 = -xc * sinTheta + yc * cosTheta + centerSourceY;
				
				double val = interp.evaluate(x2, y2); 
				target.setValue(x, y, val);
			}
		}
	}
	
	public void processVector2d(VectorArray2D<?,?> source, VectorArray2D<?,?> target)
	{
		// input array size
		int sourceSizeX = target.size(0);
		int sourceSizeY = target.size(1);
		Float32Array2D sourceChannel = Float32Array2D.create(sourceSizeX, sourceSizeY);
		
		// output array size
		int targetSizeX = target.size(0);
		int targetSizeY = target.size(1);
		Float32Array2D targetChannel = Float32Array2D.create(targetSizeX, targetSizeY);

		int nChannels = source.channelCount();
		for (int c = 0; c < nChannels; c++)
		{
			copyToChannel(source, c, sourceChannel);
			processScalar2d(sourceChannel, targetChannel);
			copyFromChannel(targetChannel, target, c);
		}
	}
	
	private void copyToChannel(VectorArray2D<?,?> source, int channelIndex, ScalarArray2D<?> channel)
	{
		int sizeX = source.size(0);
		int sizeY = source.size(1);
		
		for (int y = 0; y < sizeY; y++)
		{
			for (int x = 0; x < sizeX; x++)
			{
				channel.setValue(x, y, source.getValue(x, y, channelIndex));
			}
		}
	}
	
	private void copyFromChannel(ScalarArray2D<?> channel, VectorArray2D<?,?> target, int channelIndex)
	{
		int sizeX = channel.size(0);
		int sizeY = channel.size(1);
		
		for (int y = 0; y < sizeY; y++)
		{
			for (int x = 0; x < sizeX; x++)
			{
				target.setValue(x, y, channelIndex, channel.getValue(x, y));
			}
		}
	}

    @Override
    public <T> Array<?> process(Array<T> array)
    {
        if (array.dimensionality() != 2)
        {
            throw new IllegalArgumentException("Requires an array of dimensionality 2");
        }
        
        if (array instanceof ScalarArray<?>)
        {
            ScalarArray2D<?> scalar2d = ScalarArray2D.wrap((ScalarArray<?>) array);
            ScalarArray2D<?> result = ScalarArray2D.wrap(scalar2d.newInstance(scalar2d.size())); 
            processScalar2d(scalar2d, result);
            return result;
        }
        else if (array instanceof VectorArray<?,?>)
        {
            VectorArray2D<?,?> vector2d = VectorArray2D.wrap((VectorArray<?,?>) array);
            VectorArray2D<?,?> result = VectorArray2D.wrap(vector2d.newInstance(vector2d.size())); 
            processVector2d(vector2d, result);
            return result;
        }
        else
        {
            throw new RuntimeException("Unable to compute array rotation");
        }
    }
    
    public boolean canProcess(Array<?> array)
    {
        if (array.dimensionality() != 2)
            return false;
        return array instanceof ScalarArray || array instanceof VectorArray;
    }
}
