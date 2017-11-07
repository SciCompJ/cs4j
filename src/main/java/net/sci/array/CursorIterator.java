/**
 * 
 */
package net.sci.array;

import java.util.Iterator;

/**
 * Iterator over the positions of an array.
 * 
 * @author dlegland
 *
 */
public interface CursorIterator <C extends Cursor> extends Iterator<C>
{
	public void forward();
	public abstract int[] getPosition();
}
