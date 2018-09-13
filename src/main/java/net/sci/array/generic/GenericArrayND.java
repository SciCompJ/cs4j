/**
 * 
 */
package net.sci.array.generic;

import net.sci.array.ArrayND;

/**
 * @author dlegland
 *
 */
public abstract class GenericArrayND<T> extends ArrayND<T> implements GenericArray<T>
{
    // =============================================================
    // Static factory
    
    public static final <T> GenericArrayND<T> create(int[] sizes, T initValue)
    {
        return new BufferedGenericArrayND<T>(sizes, initValue);
    }
    
    // =============================================================
    // Constructor

    protected GenericArrayND(int[] sizes)
    {
        super(sizes);
    }
}
