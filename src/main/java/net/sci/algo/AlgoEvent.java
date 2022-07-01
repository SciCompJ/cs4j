/**
 * 
 */
package net.sci.algo;

/**
 * An event class for storing information about the status and progression of
 * an algorithm.
 * 
 * @author David Legland
 *
 */
public class AlgoEvent 
{
	/**
	 * The algorithm object that threw this event
	 */
	private Object source;
	
	/**
     * The status of the algorithm. Can be used to describe the current step of
     * the worflow.
     */
	private String status;
	
	/**
     * The progression of the algorithm. Should be between 0 and the
     * <code>total</code> variable.
     */
	private double step;
	
	/**
	 * The largest value that can be reached by the step variable.
	 */
	private double total;
	
	/**
     * Creates a new AlgoEvent with all the event information.
     * 
     * @param source
     *            the source of the event, usually an instance of the Algo
     *            interface
     * @param status
     *            the new status of the algorithm
     * @param step
     *            the new progression step of the algorithm
     * @param total
     *            the new number of steps of the algorithm
     */
	public AlgoEvent(Object source, String status, double step, double total) 
	{
		this.source = source;
		this.step = step;
		this.total = total;
	}
	
    /**
     * Creates a new AlgoEvent with the status.
     * 
     * @param source
     *            the source of the event, usually an instance of the Algo
     *            interface
     * @param status
     *            the new status of the algorithm
     */
	public AlgoEvent(Object source, String status) 
	{
		this.source = source;
		this.status = status;
		this.step = 0;
		this.total = 0;
	}
	
    /**
     * Creates a new AlgoEvent from the progression.
     * 
     * @param source
     *            the source of the event, usually an instance of the Algo
     *            interface
     * @param step
     *            the new progression step of the algorithm
     * @param total
     *            the new number of steps of the algorithm
     */
	public AlgoEvent(Object source, double step, double total) 
	{
		this.source = source;
		this.status = "";
		this.step = step;
		this.total = total;
	}
	
	/**
	 * @return the source object
	 */
	public Object getSource() 
	{
		return source;
	}

	/**
	 * @return the current status of the algorithm
	 */
	public String getStatus() 
	{
		return status;
	}
	
	/**
	 * @return the current progression of the algorithm
	 */
	public double getCurrentProgress() 
	{
		return step;
	}

	/**
	 * @return the total progression of the algorithm
	 */
	public double getTotalProgress() 
	{
		return total;
	}
	
	/**
	 * @return the progression ratio of the algorithm.
	 */
	public double getProgressRatio() 
	{
		return this.step / this.total;
	}
}
