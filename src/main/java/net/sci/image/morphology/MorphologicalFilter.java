/**
 * 
 */
package net.sci.image.morphology;

import net.sci.algo.AlgoStub;
import net.sci.array.Array;
import net.sci.array.ArrayOperator;
import net.sci.array.scalar.ScalarArray;

/**
 * Base class for morphological filters, based on a structuring element.
 * 
 * @see Strel
 * 
 * @author dlegland
 *
 */
public abstract class MorphologicalFilter extends AlgoStub implements ArrayOperator
{
    protected Strel strel;
    
    protected MorphologicalFilter(Strel strel)
    {
        this.strel = strel;
    }

    public abstract ScalarArray<?> processScalar(ScalarArray<?> array);

    public Strel getStrel()
    {
        return strel;
    }
    
    @Override
    public <T> Array<?> process(Array<T> array)
    {
        if (array instanceof ScalarArray)
        {
            return processScalar((ScalarArray<?>) array);
        }
        else
        {
            throw new RuntimeException(
                    "Requires an instance of ScalarArray");
        }
    }
    
}
