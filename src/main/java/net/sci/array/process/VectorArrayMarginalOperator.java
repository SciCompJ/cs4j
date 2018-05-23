/**
 * 
 */
package net.sci.array.process;

import java.util.Collection;
import java.util.Iterator;

import net.sci.array.Array;
import net.sci.array.scalar.Scalar;
import net.sci.array.scalar.ScalarArray;
import net.sci.array.vector.Vector;
import net.sci.array.vector.VectorArray;

/**
 * An interface that allows instances of ScalarArrayOperator to work on vector
 * arrays, by processing each channel independently.
 * 
 * @author dlegland
 *
 */
public interface VectorArrayMarginalOperator extends ScalarArrayOperator
{
	public default void processVector(VectorArray<? extends Vector<?>> source,
			VectorArray<? extends Vector<?>> target)
	{
	    // extract channels of each array
		Collection<ScalarArray<?>> sourceChannels = VectorArray.splitChannels(source);
		Collection<ScalarArray<?>> targetChannels = VectorArray.splitChannels(target);

		// create iterators on channels
		Iterator<ScalarArray<?>> sourceChannelIter = sourceChannels.iterator();
		Iterator<ScalarArray<?>> targetChannelIter = targetChannels.iterator();
		
		// iterate over each collection of channels in parallel
		int c = 0;
		while (sourceChannelIter.hasNext() && targetChannelIter.hasNext())
		{
			// extract current channels
			ScalarArray<?> sourceChannel = sourceChannelIter.next();

			// process current channel
			ScalarArray<?> targetChannel = process(sourceChannel);

			// copy result of current channel onto target vector array
			ScalarArray.Iterator<? extends Scalar> channelIter = targetChannel.iterator();
			VectorArray.Iterator<? extends Vector<? extends Scalar>> targetIter = target.iterator();
			while(targetIter.hasNext() && channelIter.hasNext())
			{
				double value = channelIter.nextValue();
				targetIter.forward();
				targetIter.setValue(c, value);
			}
			c++;
		}
	}

	/**
	 * Override default behavior to check if input array is either scalar or
	 * vector array.
	 * 
	 * @return true if input array is scalar
	 */
	@Override
	public default boolean canProcess(Array<?> array)
	{
		return array instanceof ScalarArray || array instanceof VectorArray;
	}

}
