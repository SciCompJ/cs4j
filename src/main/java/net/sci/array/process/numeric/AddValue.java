/**
 * 
 */
package net.sci.array.process.numeric;

import net.sci.algo.AlgoStub;
import net.sci.array.ArrayWrapperStub;
import net.sci.array.numeric.Scalar;
import net.sci.array.numeric.ScalarArray;
import net.sci.array.process.ScalarArrayOperator;

/**
 * Adds a scalar value to an array containing Scalar value.
 * 
 * @author dlegland
 *
 */
public class AddValue extends AlgoStub implements ScalarArrayOperator
{
    /** the value to add to each element of the original array.*/
    double value;
    
    public AddValue(double value)
    {
        this.value = value;
    }
    
    public <S extends Scalar<S>> ScalarArray<?> createView(ScalarArray<S> array)
    {
        return new View<S>(array);
    }
    
    @Override
    public ScalarArray<?> processScalar(ScalarArray<? extends Scalar<?>> array)
    {
        ScalarArray<?> res = array.newInstance(array.size());
        res.fillValues(pos -> array.getValue(pos) + value);
        return res;
    }
    
    public class View <S extends Scalar<S>> extends ArrayWrapperStub<S> implements ScalarArray<S>
    {
        ScalarArray<S> array;
        
        public View(ScalarArray<S> array)
        {
            super(array);
            this.array = array;
        }
        
        @Override
        public double getValue(int[] pos)
        {
            return array.getValue(pos) + value;
        }

        @Override
        public void setValue(int[] pos, double value)
        {
            throw new RuntimeException("Can not modify value of a view");
        }

        @Override
        public S get(int[] pos)
        {
            return array.createElement(array.getValue(pos) + value);
        }

        @Override
        public void set(int[] pos, S value)
        {
            throw new RuntimeException("Can not modify value of a view");
        }

        @Override
        public Class<S> elementClass()
        {
            return array.elementClass();
        }

        @Override
        public ScalarArray<S> newInstance(int... dims)
        {
            return array.newInstance(dims);
        }

        @Override
        public ScalarArray.Factory<S> factory()
        {
            return array.factory();
        }
    }
}
