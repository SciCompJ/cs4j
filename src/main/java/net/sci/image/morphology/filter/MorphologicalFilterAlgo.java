/**
 * 
 */
package net.sci.image.morphology.filter;

import net.sci.algo.AlgoEvent;
import net.sci.algo.AlgoListener;
import net.sci.algo.AlgoStub;
import net.sci.array.Array;
import net.sci.array.ArrayOperator;
import net.sci.array.scalar.ScalarArray;
import net.sci.array.vector.VectorArray;
import net.sci.image.morphology.Strel;

/**
 * Base class for morphological filters, based on a structuring element.
 * 
 * @see Strel
 * 
 * @author dlegland
 *
 */
public abstract class MorphologicalFilterAlgo extends AlgoStub implements ArrayOperator, AlgoListener
{
    /**
     * The structuring element used by concrete implementations.
     */
    protected Strel strel;
    
    protected MorphologicalFilterAlgo(Strel strel)
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
    protected VectorArray<?> processVector(VectorArray<?> array)
    {
        // allocate memory for result
        VectorArray<?> res = array.duplicate();
        
        // iterate over channels
        for (int c = 0; c < array.channelNumber(); c++)
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
            return processVector((VectorArray<?>) array);
        }
        else
        {
            throw new RuntimeException(
                    "Requires an instance of ScalarArray");
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
