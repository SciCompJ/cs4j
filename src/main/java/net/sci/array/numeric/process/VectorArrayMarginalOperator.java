/**
 * 
 */
package net.sci.array.numeric.process;

import net.sci.array.Array;
import net.sci.array.Array.PositionIterator;
import net.sci.array.numeric.ScalarArray;
import net.sci.array.numeric.VectorArray;

/**
 * An interface that allows instances of ScalarArrayOperator to work on vector
 * arrays, by processing each channel independently.
 * 
 * @author dlegland
 *
 */
public interface VectorArrayMarginalOperator extends ScalarArrayOperator
{
    public default void processVector(VectorArray<?, ?> source, VectorArray<?, ?> target)
	{
		// iterate over channels
        for (int c = 0; c < source.channelCount(); c++)
		{
			// process current channel
			ScalarArray<?> resultChannel = processScalar(source.channel(c));

			// copy result values into target array
			PositionIterator posIter = resultChannel.positionIterator();
			while (posIter.hasNext())
			{
			    int[] pos = posIter.next();
			    target.setValue(pos, c, resultChannel.getValue(pos));
			}
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
    @Override
    public default <T> Array<?> process(Array<T> array)
    {
        if (array instanceof ScalarArray)
        {
            return processScalar((ScalarArray<?>) array);
        }
        if (array instanceof VectorArray)
        {
            VectorArray<?,?> output = (VectorArray<?,?>) array.duplicate();
            processVector((VectorArray<?,?>) array, output);
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
