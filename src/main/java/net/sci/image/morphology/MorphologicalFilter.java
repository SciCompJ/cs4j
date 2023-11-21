/**
 * 
 */
package net.sci.image.morphology;

import net.sci.algo.AlgoEvent;
import net.sci.algo.AlgoListener;
import net.sci.algo.AlgoStub;
import net.sci.array.Array;
import net.sci.array.ArrayOperator;
import net.sci.array.scalar.ScalarArray;
import net.sci.array.vector.VectorArray;

/**
 * Base class for morphological filters, based on a structuring element.
 * 
 * Can process ScalarArray instances, or VectorArray instances, and return array
 * the same type and the same size as input arrays. In the case of VectorArray
 * instances the process is applied on each channel / component image of the
 * vector image).
 * 
 * @see Strel
 * 
 * @author dlegland
 *
 */
public abstract class MorphologicalFilter extends AlgoStub implements ArrayOperator, AlgoListener
{
    /**
     * The structuring element used by concrete implementations.
     */
    protected Strel strel;
    
    protected MorphologicalFilter(Strel strel)
    {
        this.strel = strel;
    }

    public abstract ScalarArray<?> processScalar(ScalarArray<?> array);

    public Strel getStrel()
    {
        return this.strel;
    }
    
    /**
     * Default implementation for processing vector arrays, using marginal
     * processing of each channel.
     * 
     * @param array
     *            the input vector array
     * @return a vector array with as many channels as original array.
     */
    protected VectorArray<?,?> processVector(VectorArray<?,?> array)
    {
        // allocate memory for result
        VectorArray<?,?> res = array.duplicate();
        
        // iterate over channels
        for (int c = 0; c < array.channelCount(); c++)
        {
            // process current channel and copy into result array
            ScalarArray<?> resC = processScalar(array.channel(c));
            res.setChannel(c, resC);
        }
        return res;
    }
    
    @Override
    public <T> Array<?> process(Array<T> array)
    {
        if (array instanceof ScalarArray)
        {
            return processScalar((ScalarArray<?>) array);
        }
        else if (array instanceof VectorArray)
        {
            return processVector((VectorArray<?,?>) array);
        }
        else
        {
            throw new RuntimeException(
                    "Requires an instance of ScalarArray or VectorArray");
        }
    }

    @Override
    public void algoProgressChanged(AlgoEvent evt)
    {
        fireProgressChanged(evt);
    }

    @Override
    public void algoStatusChanged(AlgoEvent evt)
    {
        fireStatusChanged(evt);
    }
    

}
