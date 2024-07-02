/**
 * 
 */
package net.sci.array.process.numeric;

import java.util.function.Function;

import net.sci.algo.AlgoStub;
import net.sci.array.ArrayOperator;
import net.sci.array.impl.ArrayWrapperStub;
import net.sci.array.numeric.Scalar;
import net.sci.array.numeric.ScalarArray;
import net.sci.array.process.ScalarArrayOperator;

/**
 * Applies a scalar function to each element of a scalar array.
 * 
 * @see net.sci.array.numeric.ScalarArray#apply(java.util.function.UnaryOperator)
 */
public class ApplyFunction extends AlgoStub implements ArrayOperator, ScalarArrayOperator
{
    Function<Double, Double> function;
    
    public ApplyFunction(Function<Double, Double> function)
    {
        this.function = function;
    }
 
    public <S extends Scalar<S>> ScalarArray<?> createView(ScalarArray<S> array)
    {
        return new ScalarView<S>(array);
    }
    
    @Override
    public ScalarArray<?> processScalar(ScalarArray<?> array)
    {
        ScalarArray<?> res = array.newInstance(array.size());
        for (int[] pos : res.positions())
        {
            res.setValue(pos, function.apply(array.getValue(pos)));
        }
        return res;
    }
    
    /**
     * Creates a view on the result of function applied on the specified array,
     * using the same type as the array.
     * 
     * @param <S>
     *            the type of the array, and of the view
     */
    public class ScalarView<S extends Scalar<S>> extends ArrayWrapperStub<S> implements ScalarArray<S>
    {
        ScalarArray<S> array;
        
        protected ScalarView(ScalarArray<S> array)
        {
            super(array);
            this.array = array;
        }

        @Override
        public Class<S> elementClass()
        {
            return array.elementClass();
        }

        @Override
        public S get(int[] pos)
        {
            return array.createElement(function.apply(array.getValue(pos)));
        }

        @Override
        public void set(int[] pos, S value)
        {
            throw new RuntimeException("Can not modify value of a view");
        }

        @Override
        public double getValue(int[] pos)
        {
            return function.apply(array.getValue(pos));
        }

        @Override
        public void setValue(int[] pos, double value)
        {
            throw new RuntimeException("Can not modify value of a view");
        }

        @Override
        public ScalarArray<S> newInstance(int... dims)
        {
            return array.newInstance(dims);
        }

        @Override
        public net.sci.array.numeric.ScalarArray.Factory<S> factory()
        {
            return array.factory();
        }
        
    }
}
