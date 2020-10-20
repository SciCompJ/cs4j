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
     * The relative progress of the current operator, between 0 and 100. Value
     * equal to -1 corresponds to non initialized value.
     */
    int progressRatio = -1;
    
	/**
	 * 
	 */
	public ConsoleAlgoListener()
	{
	}
	
    /**
     * Static method that creates a new instance of DefaultAlgoListener, and 
     * add it to the given algorithm.
     * 
     * 
     * <pre><code>
     * // read demo image
     * String imageURL = "http://imagej.nih.gov/ij/images/NileBend.jpg";
     * Array array = Image.read(imageURL).getData();
     * // init operator class
     * Closing closing = new Closing(SquareStrel.fromDiameter(15));
     * // Add monitoring of the operator
     * ConsoleAlgoListener.monitor(closing);
     * // run process. The standard output will display progress
     * Array<?> result = op.process(array);
     * </code></pre>
     * 
     * @param algo the algorithm to monitor
     */
    public static final void monitor(Algo algo)
    {
        ConsoleAlgoListener listener = new ConsoleAlgoListener();
        algo.addAlgoListener(listener);
    }

	/* (non-Javadoc)
	 * @see net.sci.algo.AlgoListener#algoProgressChanged(net.sci.algo.AlgoEvent)
	 */
	@Override
	public void algoProgressChanged(AlgoEvent evt)
	{
	    // compute current relative progress
        double cp = evt.getCurrentProgress();
        double tp = evt.getTotalProgress();
        int cpr = (int) Math.min(Math.round((cp / tp) * 20), 20);
        
        if (cpr <= 0)
        {
            // do nothing...
        }
        if (cpr < this.progressRatio)
        {
            // If the ratio is smaller than previous, start a new progress line
            System.out.println("");
            for (int i = 0; i <= cpr; i++)
            {
                System.out.print(".");
            }
        }
        else if (cpr > this.progressRatio)
        {
            // If the ratio is greater than previous, adds a dot
            System.out.print(".");
            if (cpr == 20)
            {
                System.out.println();
            }
        }

        this.progressRatio = cpr;
	}

	/* (non-Javadoc)
	 * @see net.sci.algo.AlgoListener#algoStatusChanged(net.sci.algo.AlgoEvent)
	 */
	@Override
	public void algoStatusChanged(AlgoEvent evt)
	{
	    String msg = evt.getStatus();
	    if (msg != null && !msg.isEmpty())
	    {
	        System.out.println(evt.getStatus());
	    }
	}

}
