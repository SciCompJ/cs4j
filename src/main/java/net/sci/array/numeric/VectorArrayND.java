/**
 * 
 */
package net.sci.array.numeric;

import net.sci.array.ArrayND;

/**
 * Base implementation of <code>VectorArray</code> interface for ND arrays.
 * 
 * @see VectorArray
 * 
 * @param <V>
 *            the type of the vector contained within the array
 * @param <S>
 *            the type of the elements contained by the vector
 * @author dlegland
 *
 */
public abstract class VectorArrayND<V extends Vector<V,S>, S extends Scalar<S>> extends ArrayND<V> implements VectorArray<V,S>
{
    // =============================================================
    // Constructors
    
    protected VectorArrayND(int... sizes)
    {
        super(sizes);
    }
}
