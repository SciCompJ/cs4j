/**
 * 
 */
package net.sci.array;

/**
 * @author dlegland
 *
 */
public interface Positionable<T>
{
	public T get(int[] pos);
	public void set(int[] pos, T value);
	
	public Cursor getCursor();
	
	public default T get(Cursor cursor)
	{
		return get(cursor.getPosition());
	}
	
	public default void set(Cursor cursor, T value)
	{
		set(cursor.getPosition(), value);
	}
	
	/**
	 * Iterates over the positions within the positionable.
	 */
	public interface Cursor
	{
		public boolean hasNext();
		public void forward();
		
		public abstract int[] getPosition();
	};
}
