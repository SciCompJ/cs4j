/**
 * 
 */
package net.sci.algo;

/**
 * An interface for managing progression and status changes of algorithms.
 * 
 * @author David Legland
 *
 */
public interface Algo
{
    /**
     * Adds a listener to this algorithm.
     * 
     * @param listener the listener to add.
     */
	public void addAlgoListener(AlgoListener listener);

    /**
     * removes a listener from this algorithm.
     * 
     * @param listener the listener to remove.
     */
	public void removeAlgoListener(AlgoListener listener);
}
