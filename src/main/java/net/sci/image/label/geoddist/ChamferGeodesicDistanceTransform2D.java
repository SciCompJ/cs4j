/**
 * 
 */
package net.sci.image.label.geoddist;

import net.sci.image.binary.distmap.ChamferMask2D;

/**
 * @author dlegland
 *
 */
public interface ChamferGeodesicDistanceTransform2D extends GeodesicDistanceTransform2D
{
    /**
     * Return the chamfer mask used by this distance transform algorithm.
     * 
     * @return the chamfer mask used by this distance transform algorithm.
     */
    public ChamferMask2D mask();
}
