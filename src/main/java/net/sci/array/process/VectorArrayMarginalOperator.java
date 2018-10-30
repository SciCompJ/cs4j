/**
 * 
 */
package net.sci.array.process;

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
        // create iterators on channels
        Iterator<? extends ScalarArray<?>> sourceChannelIter = source.channelIterator();
        Iterator<? extends ScalarArray<?>> targetChannelIter = target.channelIterator();
		
		// iterate over each collection of channels in parallel
		int c = 0;
		while (sourceChannelIter.hasNext() && targetChannelIter.hasNext())
		{
			// extract current channels
			ScalarArray<?> sourceChannel = sourceChannelIter.next();

			// process current channel
			ScalarArray<?> targetChannel = processScalar(sourceChannel);

			// copy result of current channel onto target vector array
			// TODO: use position iterator
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
     * Overrides default behavior to process each channel of a vector array as a scalar array.
     * 
     * The input array must be either an instance of ScalarArray or of VectorArray.
     * 
     * @param array
     *            the input array
     * @return the operator result as a new instance of Array
     * @throws IllegalArgumentException
     *             if the input array is not an instance of ScalarArray or VectorArray
     */
    @SuppressWarnings("unchecked")
    @Override
    public default <T> Array<?> process(Array<T> array)
    {
        if (array instanceof ScalarArray)
        {
            return processScalar((ScalarArray<?>) array);
        }
        if (array instanceof VectorArray)
        {
            VectorArray<?> output = (VectorArray<? extends Vector<?>>) array.duplicate();
            processVector((VectorArray<? extends Vector<?>>) array, output);
            return output;
        }

        throw new IllegalArgumentException("Can not process array of class: " + array.getClass().getName());
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
