/**
 * 
 */
package net.sci.image;

import java.io.PrintStream;

/**
 * Contains information for calibration of spatial axes, and eventually channels
 * and time axis.
 *
 * @author dlegland
 *
 */
public class Calibration
{
    // =============================================================
    // Class fields
    
    /**
     * The meta-data associated to each axis. The array must have as many
     * elements as the number of dimensions in the image.
     */
    ImageAxis[] axes;
    
    /**
     * Description of channels.
     * 
     * Default is a categorical axis with only one item named "value".
     */
    ImageAxis channelAxis = new ImageAxis.C(new String[] {"Value"});
    
    
    // =============================================================
    // Constructors

   /**
     * Creates default numerical axes for each image dimension.
     */
    public Calibration(int nDims)
    {
        setupAxes(nDims);
    }
    
    /**
     * Creates default numerical axes for each image dimension.
     */
    private void setupAxes(int nDims)
    {
        this.axes = new ImageAxis[nDims];
        for (int i = 0; i < nDims; i++)
        {
            this.axes[i] = new NumericalAxis("Axis-" + i, 1.0, 0.0);
        }
    }
 
    public Calibration(double[] resol, String unitName)
    {
        // create image axes
        int nd = resol.length;
        this.axes = new ImageAxis[nd];
        
        // Process "usual" axes
        if (nd > 0) axes[0] = new ImageAxis.X(resol[0], 0.0, unitName);
        if (nd > 1) axes[1] = new ImageAxis.Y(resol[1], 0.0, unitName);
        if (nd > 2) axes[2] = new ImageAxis.Z(resol[2], 0.0, unitName);
        
        // eventually process additional axes 
        for (int d = 3; d < nd; d++)
        {
            axes[d] = new NumericalAxis("Axis-" + d, ImageAxis.Type.SPACE, resol[d], 0.0, unitName);
        }
    }



    // =============================================================
    // Methods

    
    // =============================================================
    // Management of channels informations
    
    public ImageAxis getChannelAxis()
    {
        return channelAxis;
    }

    public void setChannelAxis(ImageAxis newChannelAxis)
    {
        this.channelAxis = newChannelAxis;
    }

    
    // =============================================================
    // Management of axes calibration

    /**
     * Tries to return the X-axis, or throws an exception if no X-axis is
     * present.
     * 
     * @return the first instance of ImageAxis.X class, if it exists
     */
    public NumericalAxis getXAxis()
    {
        for (ImageAxis axis : this.axes)
        {
            if (axis instanceof ImageAxis.X)
            {
                return (NumericalAxis) axis;
            }
        }
        throw new RuntimeException("Calibration does not contain any X-axis");
    }
    
    /**
     * Tries to return the Y-axis, or throws an exception if no Y-axis is
     * present.
     * 
     * @return the first instance of ImageAxis.Y class, if it exists
     */
    public NumericalAxis getYAxis()
    {
        for (ImageAxis axis : this.axes)
        {
            if (axis instanceof ImageAxis.Y)
            {
                return (NumericalAxis) axis;
            }
        }
        throw new RuntimeException("Calibration does not contain any Y-axis");
    }
    
    /**
     * Tries to return the Z-axis, or throws an exception if no Z-axis is
     * present.
     * 
     * @return the first instance of ImageAxis.Z class, if it exists
     */
    public NumericalAxis getZAxis()
    {
        for (ImageAxis axis : this.axes)
        {
            if (axis instanceof ImageAxis.Z)
            {
                return (NumericalAxis) axis;
            }
        }
        throw new RuntimeException("Calibration does not contain any Z-axis");
    }
    
    /**
     * Tries to return the time axis, or throws an exception if no time axis is
     * present.
     * 
     * @return the first instance of ImageAxis.T class, if it exists
     */
    public NumericalAxis getTimeAxis()
    {
        for (ImageAxis axis : this.axes)
        {
            if (axis instanceof ImageAxis.T)
            {
                return (NumericalAxis) axis;
            }
        }
        throw new RuntimeException("Calibration does not contain any time axis");
    }
    
    /**
     * @return the array of axes
     */
    public ImageAxis[] getAxes()
    {
        return axes;
    }

    /**
     * @param axes the axes to set
     */
    public void setAxes(ImageAxis[] axes)
    {
        this.axes = axes;
    }

    /**
     * @param dim
     *            the axis dimension
     * @return the axis at the specified dimension
     */
    public ImageAxis getAxis(int dim)
    {
        return axes[dim];
    }

    /**
     * @param dim
     *            the axis index
     * @param axis
     *            the axis to set
     */
    public void setAxis(int dim, ImageAxis axis)
    {
        this.axes[dim] = axis;
    }


    // =============================================================
    // generic methods

    public Calibration duplicate()
    {
        // create new Calibration object
        int nd = this.axes.length;
        Calibration res = new Calibration(nd);
        
        // duplicate direction axes
        for (int d = 0; d <nd; d++)
        {
            res.axes[d] = this.axes[d].duplicate();
        }
        
        // also duplicates channel axis
        res.channelAxis = this.channelAxis.duplicate();
        
        // return duplicate
        return res;
    }
    
    // =============================================================
    // debug methods

    public void print(PrintStream stream)
    {
        // header
        int nd = this.axes.length;
        stream.println("Calibration with " + nd + " axes:");

        // dimension axes
        for (int d = 0; d < nd; d++)
        {
            stream.printf("  Axis[%d]: %s\n", d, axes[d]);
        }

        // channel axis
        stream.printf("  Channel axis: %s\n", this.channelAxis);
    }

    public static final void main(String... args)
    {
        double[] resol = new double[] {2.5, 2.5, 2.8};
        Calibration calib = new Calibration(resol, "µm");
        calib.print(System.out);
    }
}
