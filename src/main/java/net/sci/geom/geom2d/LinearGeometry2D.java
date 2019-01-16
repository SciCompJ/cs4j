/**
 * 
 */
package net.sci.geom.geom2d;

/**
 * A curve that can be inscribed in a straight line, like a ray, a straight
 * line, or a line segment. 
 * 
 * 
 * @see StraightLine2D
 * 
 * @author dlegland
 *
 */
public interface LinearGeometry2D extends Curve2D
{
    /**
     * Transforms this geometry with the specified affine transform.
     * 
     * @param trans
     *            an affine transform
     * @return the transformed geometry
     */
    public LinearGeometry2D transform(AffineTransform2D trans);
    
    /**
     * Returns the position of the point used as origin for this linear
     * geometry.
     * 
     * @return the origin of this linear geometry
     */
    public Point2D origin();
    
    /**
     * @return the direction vector of this geometry
     */
    public Vector2D direction();
    
    /**
     * @return the straight line that contains this geometry
     */
    public StraightLine2D supportingLine();
}
