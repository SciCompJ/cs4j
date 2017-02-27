/**
 * File: DegeneratedLine2DException.java Project: javaGeom
 * 
 * Distributed under the LGPL License.
 *
 * Created: 19 août 2010
 */
package net.sci.geom.geom2d.line;

/**
 * A degenerated line, whose direction vector is undefined, had been
 * encountered.
 * 
 * This kind of exception can occur during polygon or polylines algorithms, when
 * polygons have multiple vertices.
 * 
 * @author dlegland
 */
public class DegeneratedLine2DException extends RuntimeException
{
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    protected LinearGeometry2D line;
    
    /**
     * @param msg
     *            the error message
     * @param line
     *            the degenerated line
     */
    public DegeneratedLine2DException(String msg, LinearGeometry2D line)
    {
        super(msg);
        this.line = line;
    }
    
    /**
     * @param line
     *            the degenerated line
     */
    public DegeneratedLine2DException(LinearGeometry2D line)
    {
        super();
        this.line = line;
    }
    
    /**
     * @return the instance of LinearGeometry2D that has caused this exception
     */
    public LinearGeometry2D getLine()
    {
        return line;
    }
}
