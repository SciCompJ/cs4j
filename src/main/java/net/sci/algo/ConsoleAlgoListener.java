/**
 * 
 */
package net.sci.algo;

/**
 * Algorithm listener that displays all status messages and progress changes
 * directly on the console.
 * 
 * @author dlegland
 *
 */
public class ConsoleAlgoListener implements AlgoListener
{
	/**
	 * 
	 */
	public ConsoleAlgoListener()
	{
	}

	/* (non-Javadoc)
	 * @see net.sci.algo.AlgoListener#algoProgressChanged(net.sci.algo.AlgoEvent)
	 */
	@Override
	public void algoProgressChanged(AlgoEvent evt)
	{
		System.out.println("progress: " + evt.getCurrentProgress() + " / " + evt.getTotalProgress());
	}

	/* (non-Javadoc)
	 * @see net.sci.algo.AlgoListener#algoStatusChanged(net.sci.algo.AlgoEvent)
	 */
	@Override
	public void algoStatusChanged(AlgoEvent evt)
	{
		System.out.println(evt.getStatus());
	}

}
