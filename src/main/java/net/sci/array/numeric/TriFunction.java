/**
 * 
 */
package net.sci.array.numeric;

/**
 * A simple functional interface declaration to be used with Array3D.populate() methods.
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
    public R apply(T t, U u, V v);
}
