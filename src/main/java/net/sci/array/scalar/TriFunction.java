/**
 * 
 */
package net.sci.array.scalar;

/**
 * A simple functional interface declaration to be used with ScalarArray3D.populate.
 * 
 * @see net.sci.array.scalar.ScalarArray3D#populate(TriFunction)
 * 
 * @author dlegland
 *
 */
@FunctionalInterface
public interface TriFunction<T, U, V, R>
{
    public R apply(T t, U u, V v);
}
