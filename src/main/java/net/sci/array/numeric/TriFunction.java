/**
 * 
 */
package net.sci.array.numeric;

/**
 * A simple functional interface declaration to be used with Array3D.populate()
 * methods.
 * 
 * @param <T>
 *            The type of the first input argument
 * @param <U>
 *            The type of the second input argument
 * @param <V>
 *            The type of the third input argument
 * @param <R>
 *            The type of the result.
 * 
 * @see net.sci.array.numeric.ScalarArray3D#fillValues(TriFunction)
 * @see net.sci.array.numeric.IntArray3D#fillInts(TriFunction)
 * @see net.sci.array.binary.BinaryArray3D#fillBooleans(TriFunction)
 * 
 * @author dlegland
 *
 */
@FunctionalInterface
public interface TriFunction<T, U, V, R>
{
    /**
     * Computes a result value based on the three inpue arguments.
     * 
     * @param t
     *            the first argument
     * @param u
     *            the second argument
     * @param v
     *            the third argument
     * @return the result of function computation
     */
    public R apply(T t, U u, V v);
}
