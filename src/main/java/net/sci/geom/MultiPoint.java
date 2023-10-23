/**
 * 
 */
package net.sci.geom;

/**
 * The geometry obtained by the union of several points.
 * 
 * @author dlegland
 *
 */
public interface MultiPoint extends Geometry
{
    /**
     * Returns the number of points that compose this multi-point.
     * 
     * @return the number of points that compose this multi-point.
     */
    public int pointCount();

}
