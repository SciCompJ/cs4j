/**
 * 
 */
package net.sci.image;

/**
 * Spatial calibration of N-dimensional image.
 * @author dlegland
 *
 */
public class SpatialCalibration
{
    double[] resolutions;
            
    String unit = null;
    
    /**
     * Creates a default spatial calibration for an image with the given number of dimension.
     * 
     * @param nd the number of dimensions of the new spatial calibration
     */
    public SpatialCalibration(int nd)
    {
        this.resolutions = new double[nd];
        this.unit = "";
    }
    
    /**
     * Copy constructor.
     * 
     * @param calib the spatial calibration to copy
     */
    public SpatialCalibration(SpatialCalibration calib)
    {
        this.resolutions = calib.resolutions;
        this.unit = calib.unit;
    }
    
    /**
     * @return the resolutions
     */
    public double[] getResolutions()
    {
        return resolutions;
    }

    /**
     * @param resolutions the resolutions to set
     */
    public void setResolutions(double[] resolutions)
    {
        this.resolutions = resolutions;
    }

    /**
     * @return the unit
     */
    public String getUnit()
    {
        return unit;
    }

    /**
     * @param unit the unit to set
     */
    public void setUnit(String unit)
    {
        this.unit = unit;
    }

}
