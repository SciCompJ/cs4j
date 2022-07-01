/**
 * 
 */
package net.sci.algo;

/**
 * Interface for managing progression and status changes of an algorithm.
 *  
 * 
 * @author David Legland
 *
 */
public interface AlgoListener
{
    /**
     * Callback for a change in the progression of an algorithm.
     * 
     * The progression can be obtained by the <code>evt.getProgressRatio()</code>
     * method.
     * 
     * @param evt
     *            the AlgoEvent describing the change.
     */
	public void algoProgressChanged(AlgoEvent evt);

    /**
     * Callback for a change in the status of an algorithm.
     * 
     * The new status can be obtained by the <code>evt.getStatus()</code>
     * method.
     * 
     * @param evt
     *            the AlgoEvent describing the change.
     */
	public void algoStatusChanged(AlgoEvent evt);
}
