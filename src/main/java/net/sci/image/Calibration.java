/**
 * 
 */
package net.sci.image;

import java.io.PrintStream;

import net.sci.axis.CategoricalAxis;
import net.sci.axis.NumericalAxis;

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
    CategoricalAxis channelAxis = new CategoricalAxis("Channels", new String[] {"Value"});
    
    
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
     * Creates default numerical axes for each image dimension, and initializes
     * channel axis.
     * 
     * @param nDims
     *            the number of dimension of the image
     * @param channelAxis
     *            description of channel(s)
     */
    public Calibration(int nDims, CategoricalAxis channelAxis)
    {
        setupAxes(nDims);
        setChannelAxis(channelAxis);
    }
    
    /**
     * Creates default numerical axes for each image dimension.
     */
    private void setupAxes(int nDims)
    {
        this.axes = new ImageAxis[nDims];
        double[] spacing = new double[nDims];
        for (int d = 0; d < nDims; d++) 
        {
            spacing[d] = 1.0;
        }
        setSpatialCalibration(spacing, "");
    }
 
    public Calibration(double[] spacing, String unitName)
    {
        // create image axes
        int nd = spacing.length;
        this.axes = new ImageAxis[nd];
        
        setSpatialCalibration(spacing, unitName);
    }

    public Calibration(double[] spacing, double[] origin, String unitName)
    {
        // create image axes
        int nd = spacing.length;
        this.axes = new ImageAxis[nd];
        
        setSpatialCalibration(spacing, origin, unitName);
    }


    /**
     * Creates a new Calibration instance from an array of already defined
     * ImageAxis objects.
     * 
     * @param axes
     *            the axes definition for this calibration
     */
    public Calibration(ImageAxis[] axes)
    {
        this.axes = axes;
    }


    // =============================================================
    // Methods

    /**
     * Converts the size of an array into its equivalent physical size.
     * 
     * @param arraySize
     *            the size of the array
     * @return the physical size of the array
     */
    public double[] physicalSize(int[] arraySize)
    {
        // extract space axes
        ImageAxis[] spaceAxes = getSpaceAxes();
        
        // check input validity
        int nd = spaceAxes.length;
        if (nd != arraySize.length)
        {
            throw new IllegalArgumentException("Size array must have same size as number of space axes in calibration");
        }
        
        double[] res = new double[nd];
        for (int d = 0; d < nd; d++)
        {
            NumericalAxis numax = (NumericalAxis) spaceAxes[d];
            res[d] = arraySize[d] * numax.getSpacing();
        }
        
        return res;
    }
    

    /**
     * Converts the size of an array into its equivalent physical extent.
     * 
     * @param arraySize
     *            the size of the array
     * @return the physical extent of the array
     */
    public double[][] physicalExtent(int[] arraySize)
    {
        // extract space axes
        ImageAxis[] spaceAxes = getSpaceAxes();
        
        // check input validity
        int nd = spaceAxes.length;
        if (nd != arraySize.length)
        {
            throw new IllegalArgumentException("Size array must have same size as number of space axes in calibration");
        }
        
        double[][] res = new double[nd][2];
        for (int d = 0; d < nd; d++)
        {
            NumericalAxis numax = (NumericalAxis) spaceAxes[d];
            res[d] = numax.physicalRange(arraySize[d]);
        }
        
        return res;
    }
    
    public void setSpatialCalibration(double[] spacing, String unitName)
    {
        setSpatialCalibration(spacing, new double[spacing.length], unitName);
    }
    
    public void setSpatialCalibration(double[] spacing, double[] origin, String unitName)
    {
        // number of axes
        int nd = this.axes.length;
        if (nd != spacing.length || nd != origin.length)
        {
            throw new IllegalArgumentException("Resolution array must have same size as number of space axes in calibration");
        }
        
        // Process "usual" axes
        if (nd > 0) axes[0] = new ImageAxis.X(spacing[0], origin[0], unitName);
        if (nd > 1) axes[1] = new ImageAxis.Y(spacing[1], origin[1], unitName);
        if (nd > 2) axes[2] = new ImageAxis.Z(spacing[2], origin[2], unitName);
        
        // eventually process additional axes 
        for (int d = 3; d < nd; d++)
        {
            axes[d] = new ImageAxis("Axis-" + d, ImageAxis.Type.SPACE, spacing[d], origin[d], unitName);
        }
    }
    
    
    // =============================================================
    // Management of channels informations
    
    public CategoricalAxis getChannelAxis()
    {
        return channelAxis;
    }

    public void setChannelAxis(CategoricalAxis newChannelAxis)
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
     * @throws RuntimeException
     *             if this Calibration does not contain any ImageAxis.X axis.
     */
    public ImageAxis getXAxis()
    {
        for (ImageAxis axis : this.axes)
        {
            if (axis instanceof ImageAxis.X)
            {
                return (ImageAxis) axis;
            }
        }
        throw new RuntimeException("Calibration does not contain any X-axis");
    }
    
    /**
     * Tries to return the Y-axis, or throws an exception if no Y-axis is
     * present.
     * 
     * @return the first instance of ImageAxis.Y class, if it exists
     * @throws RuntimeException
     *             if this Calibration does not contain any ImageAxis.Y axis.
     */
    public ImageAxis getYAxis()
    {
        for (ImageAxis axis : this.axes)
        {
            if (axis instanceof ImageAxis.Y)
            {
                return (ImageAxis) axis;
            }
        }
        throw new RuntimeException("Calibration does not contain any Y-axis");
    }
    
    /**
     * Tries to return the Z-axis, or throws an exception if no Z-axis is
     * present.
     * 
     * @return the first instance of ImageAxis.Z class, if it exists
     * @throws RuntimeException
     *             if this Calibration does not contain any ImageAxis.Z axis.
     */
    public ImageAxis getZAxis()
    {
        for (ImageAxis axis : this.axes)
        {
            if (axis instanceof ImageAxis.Z)
            {
                return (ImageAxis) axis;
            }
        }
        throw new RuntimeException("Calibration does not contain any Z-axis");
    }
    
    public ImageAxis[] getSpaceAxes()
    {
        int n = getSpaceAxisNumber();
        ImageAxis[] spaceAxes = new ImageAxis[n];
        
        int d = 0;
        for (ImageAxis axis : this.axes)
        {
            if (axis.type() == ImageAxis.Type.SPACE)
            {
                spaceAxes[d++] = axis;
            }
        }
        return spaceAxes;
    }
    
    public int getSpaceAxisNumber()
    {
        int n = 0;
        for (ImageAxis axis : this.axes)
        {
            if (axis.type() == ImageAxis.Type.SPACE) n++;
        }
        return n;
    }

    /**
     * Tries to return the time axis, or throws an exception if no time axis is
     * present.
     * 
     * @return the first instance of ImageAxis.T class, if it exists
     * @throws RuntimeException
     *             if this Calibration does not contain any ImageAxis.T axis.
     */
    public ImageAxis getTimeAxis()
    {
        for (ImageAxis axis : this.axes)
        {
            if (axis instanceof ImageAxis.T)
            {
                return (ImageAxis) axis;
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
    
    public boolean isCalibrated()
    {
        for (ImageAxis axis : this.axes)
        {
            if (axis.isCalibrated())
            {
                return true;
            }
        }
        return false;
    }

    // =============================================================
    // generic methods

    public Calibration duplicate()
    {
        // create new Calibration object
        int nd = this.axes.length;
        Calibration res = new Calibration(nd);
        
        // duplicate direction axes
        for (int d = 0; d < nd; d++)
        {
            res.axes[d] = this.axes[d].duplicate();
        }
        
        // also duplicates channel axis
        res.channelAxis = this.channelAxis.duplicate();
        
        // return duplicate
        return res;
    }
    
    public ImageAxis[] duplicateAxes()
    {
        // create new Calibration object
        int nd = this.axes.length;
        ImageAxis[] res = new ImageAxis[nd];
        
        // duplicate direction axes
        for (int d = 0; d < nd; d++)
        {
            res[d] = this.axes[d].duplicate();
        }
        
        // return duplicated axes
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
        Calibration calib = new Calibration(resol, "\u00B5m");
        calib.print(System.out);
    }
}
