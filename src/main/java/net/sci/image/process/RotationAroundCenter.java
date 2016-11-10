/**
 * 
 */
package net.sci.image.process;

import net.sci.array.Array;
import net.sci.array.ArrayOperator;
import net.sci.array.data.scalar2d.FloatArray2D;
import net.sci.array.data.scalar2d.ScalarArray2D;
import net.sci.array.data.vector.VectorArray2D;
import net.sci.array.interp.LinearInterpolator2D;

/**
 * @author dlegland
 *
 */
public class RotationAroundCenter implements ArrayOperator
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
	
	/* (non-Javadoc)
	 * @see net.sci.array.ArrayOperator#process(net.sci.array.Array, net.sci.array.Array)
	 */
	@Override
	public void process(Array<?> source, Array<?> target)
	{
		if (source instanceof ScalarArray2D && target instanceof ScalarArray2D)
		{
			processScalar2d((ScalarArray2D<?>) source, (ScalarArray2D<?>) target);
		}
		else if (source instanceof VectorArray2D && target instanceof VectorArray2D)
		{
			processVector2d((VectorArray2D<?>) source, (VectorArray2D<?>) target);
		}
		else
		{
			throw new RuntimeException("Unable to compute array rotation");
		}
	}

	public void processScalar2d(ScalarArray2D<?> source, ScalarArray2D<?> target)
	{
		// Create interpolator for input array
		LinearInterpolator2D interp = new LinearInterpolator2D(source);
				
		// output array size
		int sizeX = target.getSize(0);
		int sizeY = target.getSize(1);
		
		// compute transform parameters
		double angle = Math.toRadians(this.angle);
		double cosTheta = Math.cos(angle);
		double sinTheta = Math.sin(angle);
		
		double centerSourceX = source.getSize(0) * .5;
		double centerSourceY = source.getSize(1) * .5;
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
	
	public void processVector2d(VectorArray2D<?> source, VectorArray2D<?> target)
	{
		// input array size
		int sourceSizeX = target.getSize(0);
		int sourceSizeY = target.getSize(1);
		FloatArray2D sourceChannel = FloatArray2D.create(sourceSizeX, sourceSizeY);
		
		// output array size
		int targetSizeX = target.getSize(0);
		int targetSizeY = target.getSize(1);
		FloatArray2D targetChannel = FloatArray2D.create(targetSizeX, targetSizeY);

		int nChannels = source.getVectorLength();
		for (int c = 0; c < nChannels; c++)
		{
			copyToChannel(source, c, sourceChannel);
			processScalar2d(sourceChannel, targetChannel);
			copyFromChannel(targetChannel, target, c);
		}
	}
	
	private void copyToChannel(VectorArray2D<?> source, int channelIndex, ScalarArray2D<?> channel)
	{
		int sizeX = source.getSize(0);
		int sizeY = source.getSize(1);
		
		for (int y = 0; y < sizeY; y++)
		{
			for (int x = 0; x < sizeX; x++)
			{
				channel.setValue(x, y, source.getValue(x, y, channelIndex));
			}
		}
	}
	
	private void copyFromChannel(ScalarArray2D<?> channel, VectorArray2D<?> target, int channelIndex)
	{
		int sizeX = channel.getSize(0);
		int sizeY = channel.getSize(1);
		
		for (int y = 0; y < sizeY; y++)
		{
			for (int x = 0; x < sizeX; x++)
			{
				target.setValue(x, y, channelIndex, channel.getValue(x, y));
			}
		}
	}
}
