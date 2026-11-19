/**
 * 
 */
package net.sci.algo;

/**
 * An event class for storing information about the change of status and
 * progression of an algorithm.
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
     * {@code total} variable.
     */
    private double step;

    /**
     * The largest value that can be reached by the step variable.
     */
    private double total;

    /**
     * Creates a new {@code AlgoEvent} indicating both a change of status and a
     * change of progression.
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
     * Creates a new {@code AlgoEvent} indicating a change of status of the algorithm.
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
     * Creates a new {@code AlgoEvent} indicating a progression of the algorithm.
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
     * Returns the object that notified this event.
     * 
     * @return the source object
     */
    public Object getSource()
    {
        return source;
    }

    /**
     * Returns the current status of the algorithm.
     * 
     * @return the current status of the algorithm
     */
    public String getStatus()
    {
        return status;
    }

    /**
     * Return the current progression of the algorithm.
     * 
     * @return the current progression of the algorithm
     */
    public double getCurrentProgress()
    {
        return step;
    }

    /**
     * Returns the total progression of the algorithm
     * 
     * @return the total progression of the algorithm
     */
    public double getTotalProgress()
    {
        return total;
    }

    /**
     * Returns the progression ratio of the algorithm, obtained as the ratio of
     * the current progress over the total progress.
     * 
     * @return the progression ratio of the algorithm.
     */
    public double getProgressRatio()
    {
        return this.step / this.total;
    }
}
