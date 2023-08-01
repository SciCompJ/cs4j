/**
 * 
 */
package net.sci.geom.geom2d.polygon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import net.sci.geom.geom2d.AffineTransform2D;
import net.sci.geom.geom2d.LineSegment2D;
import net.sci.geom.geom2d.Point2D;
import net.sci.geom.geom2d.curve.Contour2D;


/**
 * <p>
 * A LinearRing2D is a polyline whose last point is connected to the first one.
 * This is typically the boundary of a (Simple)Polygon2D. For open polylines,
 * the class LineString2D may be used.
 * </p>
 * 
 * <p>
 * The name 'LinearRing2D' was used for 2 reasons:
 * <ul>
 * <li>it is short</li>
 * <li>it is consistent with the JTS name</li>
 * </ul>
 * </p>
 * 
 * <p>
 * This interface declares methods for linear rings, and provides default
 * implementations for some high-level operations that are independent of the
 * representation of vertices: smoothing, resampling....
 * </p>
 * 
 * @see DefaultLinearRing2D
 * @see Polyline2D
 * @see LineString2D
 * 
 * @author dlegland
 */
public interface LinearRing2D extends Polyline2D, Contour2D
{
    // ===================================================================
    // Static methods

    /**
     * Creates a new linear curve by allocating enough memory for the specified
     * number of vertices.
     * 
     * @param nVertices the number of vertices in the polyline
     */
    public static LinearRing2D create(int nVertices)
    {
        return new DefaultLinearRing2D(nVertices);
    }
    
    public static LinearRing2D create(Point2D... vertices)
    {
        return new DefaultLinearRing2D(vertices);
    }
    
    public static LinearRing2D create(Collection<? extends Point2D> vertices)
    {
        return new DefaultLinearRing2D(vertices);
    }
    

    /**
     * Interpolates a new linear ring between two linear rings, assuming the vertices
     * are in correspondence.
     * 
     * @param ring0
     *            the first linear ring, at position t=0
     * @param ring1
     *            the second linear ring, at position t=1
     * @param t
     *            the position of the linear ring to interpolate, between 0 and 1
     * @return the interpolated linear ring
     */
    public static LinearRing2D interpolate(LinearRing2D ring0, LinearRing2D ring1, double t)
    {
        // check number of vertices
        int nv = ring0.vertexCount();
        if (ring1.vertexCount() != nv)
        {
            throw new RuntimeException("Both rings must have same number of vertices");
        }
        
        // check interpolation interval
        if (t < 0 || t > 1)
        {
            throw new RuntimeException("Interpolation value must be comprised between 0 and 1.");
        }
        
        // allocate memory for result
        LinearRing2D res = LinearRing2D.create(nv);
        
        // iterate over vertices
        for (int iv = 0; iv < nv; iv++)
        {
            Point2D p1 = ring0.vertexPosition(iv);
            Point2D p2 = ring1.vertexPosition(iv);
            res.addVertex(Point2D.interpolate(p1, p2, t));
        }

        return res;
    }
    
    
    /**
     * Computes the list of x-coordinates of intersection points of the input
     * linear ring with a horizontal line of a given height. Specific care is taken
     * for intersection points located at the extremity of edges.
     * 
     * For references about the algorithm: <a href=
     * "https://web.cs.ucdavis.edu/~ma/ECS175_S00/Notes/0411_b.pdf">Scanline
     * Fill Algorithm (pdf)</a>.
     * 
     * Implementation based on the following page:
     * <a href= "https://alienryderflex.com/polygon_fill/"> Efficient Polygon
     * Fill Algorithm With C Code Sample</a>, by Darel Rex Finley.
     * 
     * @param ring
     *            the polygon, as a linear ring
     * @param yLine
     *            the y-coordinate of the intersection line
     * @return the (unsorted) list of x-coordinates of the intersection points.
     *         Order corresponds to iteration order along polygon.
     */
    public static ArrayList<Double> xIntersectionsWithHorizontalLine(LinearRing2D ring, double yLine)
    {
        // number of vertices in polygon
        int nVertices = ring.vertexCount();

        //  create an array for storing x-coordinates of intersections
        ArrayList<Double> xNodes = new ArrayList<>();

        // initialize data for previous vertex
        Point2D prevPos = ring.vertexPosition(nVertices - 1);
        double yvp = prevPos.y();

        // iterate over indices of first edge vertex
        for (Point2D pos : ring.vertexPositions())
        {
            // y-coordinate of current vertex
            double yv = pos.y();
            
            // check conditions for intersection
            // either if:
            // 1) previous vertex is above or on, and current vertex is strictly below
            // 2) previous vertex is strictly below, and current vertex is above or on
            if  (yvp >= yLine && yv < yLine || yvp < yLine && yv >= yLine)
            {
                // slope of current edge (dy cannot be zero due to above condition)
                double edgeDx = pos.x() - prevPos.x();
                double edgeDy = pos.y() - prevPos.y();
                // x-coordinate of intersection
                double xNewNode = pos.x() + (yLine - yv) * edgeDx / edgeDy ;
                // add to list of intersections
                xNodes.add(xNewNode);
            }
            
            // switch current vertex to previous vertex
            prevPos = pos;
            yvp = yv;
        }
        
        return xNodes;
    }
    
    
    // ===================================================================
    // Methods specific to LinearRing2D
    
    /**
     * Computes the signed area of the linear ring. Algorithm is taken from page:
     * <a href="http://local.wasp.uwa.edu.au/~pbourke/geometry/polyarea/">
     * http://local.wasp.uwa.edu.au/~pbourke/geometry/polyarea/</a>. Signed area
     * is positive if polyline is oriented counter-clockwise, and negative
     * otherwise. Result is wrong if polyline is self-intersecting.
     * 
     * @return the signed area of the polyline.
     */
    public default double signedArea() 
    {
        // start from edge joining last and first vertices
        Point2D prev = vertexPosition(this.vertexCount() - 1);
        
        // Iterate over all couples of adjacent vertices
        double area = 0;
        for (Point2D point : this.vertexPositions()) 
        {
            // add area of elementary parallelogram
            area += prev.x() * point.y() - prev.y() * point.x();
            prev = point;
        }
        
        // divides by 2 to consider only elementary triangles
        return area /= 2;
    }

 
    // ===================================================================
    // Methods specific to LinearRing2D that could be declared in Polyline2D
    
    public default LinearRing2D smooth(int smoothingSize)
    {
    	// compute the number of elements before and after central vertex
    	// (ensuring M1+M2 = smoothingSize)
    	int M1 = (int) Math.floor((smoothingSize - 1) / 2);
    	int M2 = (int) Math.ceil((smoothingSize - 1) / 2);
    	
    	int nv = this.vertexCount();
    	LinearRing2D res = LinearRing2D.create(nv);
    	
    	for (int i = 0; i < nv; i++)
    	{
    		double x = 0;
    		double y = 0;
    		for (int i2 = i - M1; i2 <= i + M2; i2++)
    		{
    			Point2D v = vertexPosition((i2 % nv + nv) % nv);
    			x += v.x();
    			y += v.y();
    		}
    		x /= smoothingSize;
    		y /= smoothingSize;
    		
    		res.addVertex(new Point2D(x, y));
    	}

    	return res;
    }
    
    /**
     * Computes the index of the closest vertex to the input point.
     * 
     * @param point
     *            the query point
     * @return the index of the closest vertex to the query point
     */
    public default int closestVertexIndex(Point2D point)
    {
        double minDist = Double.POSITIVE_INFINITY;
        int index = -1;
        
        for (int i = 0; i < vertexCount(); i++)
        {
            double dist = vertexPosition(i).distance(point);
            if (dist < minDist)
            {
                index = i;
                minDist = dist;
            }
        }
        
        return index;
    }

    public default LinearRing2D mergeMultipleVertices(double minDist)
    {
        // Allocate memory for new vertex array
        int nv = vertexCount();
        LinearRing2D res = LinearRing2D.create(nv);
        
        // start with the position of the last vertex
        Point2D lastPosition = vertexPosition(nv - 1);
        
        for (Point2D pos : vertexPositions())
        {
            double dist = pos.distance(lastPosition);
            if (dist > minDist)
            {
                res.addVertex(pos);
                lastPosition = pos;
            }
        }
        
        return res;
    }

    
    // ===================================================================
    // Management of vertices
    

    // ===================================================================
    // Management of vertex normals
    
    

    // ===================================================================
    // Methods implementing the Polyline2D interface
    
    // ===================================================================
    // Methods implementing the Boundary2D interface
    
    @Override
    public default double signedDistance(Point2D point)
    {
        return signedDistance(point.x(), point.y());
    }
    
    public default double signedDistance(double x, double y)
    {
        double minDist = Double.POSITIVE_INFINITY; 
                
        double area = 0;
        
        // the winding number counter
        int winding = 0;
    
        // initialize iteration with last vertex
        Point2D p0 = this.vertexPosition(this.vertexCount() - 1);
        Point2D previous = p0;
        double xprev = previous.x();
        double yprev = previous.y();

        // iterate over vertex pairs
        for (Point2D current : this.vertexPositions())
        {
            // update distance to nearest edge 
            double dist = new LineSegment2D(previous, current).distance(x, y);
            minDist = Math.min(dist, minDist);
            
            // coordinates of current vertex
            double xcurr = current.x();
            double ycurr = current.y();
    
            // update area computation
            area += xprev * ycurr - yprev * xcurr;
            
            // update winding number
            if (yprev <= y)
            {
                // detect upward crossing
                if (ycurr > y) 
                    if (isLeft(xprev, yprev, xcurr, ycurr, x, y) > 0)
                        winding++;
            }
            else
            {
                // detect downward crossing
                if (ycurr <= y)
                    if (isLeft(xprev, yprev, xcurr, ycurr, x, y) < 0)
                        winding--;
            }
            
            // for next iteration
            xprev = xcurr;
            yprev = ycurr;
            previous = current;
        }
        
        boolean inside = area > 0 ^ winding == 0;
        return inside ? -minDist : minDist;
    }

	@Override
	public default boolean isInside(Point2D point)
	{
		return isInside(point.x(), point.y());
	}

	@Override
	public default boolean isInside(double x, double y)
	{
        double area = 0;
        
        // the winding number counter
        int winding = 0;
    
        // initialize with the last vertex
        Point2D previous = this.vertexPosition(this.vertexCount() - 1);
        double xprev = previous.x();
        double yprev = previous.y();
    
        // iterate on vertices, keeping coordinates of previous vertex in memory
        for (Point2D current : this.vertexPositions())
        {
            // coordinates of current vertex
            double xcurr = current.x();
            double ycurr = current.y();
    
            // update area computation
            area += xprev * ycurr - yprev * xcurr;
            
            
            if (yprev <= y)
            {
                // detect upward crossing
                if (ycurr > y) 
                    if (isLeft(xprev, yprev, xcurr, ycurr, x, y) > 0)
                        winding++;
            }
            else
            {
                // detect downward crossing
                if (ycurr <= y)
                    if (isLeft(xprev, yprev, xcurr, ycurr, x, y) < 0)
                        winding--;
            }
            
            // for next iteration
            xprev = xcurr;
            yprev = ycurr;
            previous = current;
        }
    
        if (area > 0) 
        {
            return winding == 1;
        }
        else 
        {
            return winding == 0;
        }
	}
    
    /**
     * Tests if the point p3 is Left|On|Right of the infinite line formed by p1 and p2.
     * 
     * Input:  three points P0, P1, and P2
     * Return: >0 for P2 left of the line through P0 and P1
     *         =0 for P2 on the line
     *         <0 for P2 right of the line
     *         
     * See: the January 2001 Algorithm "Area of 2D and 3D Triangles and Polygons"
     * 
     * @see SimplePolygon2D.isLeft(double, double, double, double, double, double)
     */
    static int isLeft(double x1, double y1, double x2, double y2, double x3, double y3)
    {
        return (int) Math.signum((x2 - x1) * (y3 - y1) - (x3 - x1) * (y2 - y1));
    }

    
    // ===================================================================
    // Methods implementing the Polyline2D interface
    
    /**
     * Returns a new linear ring with same vertices but in reverse order. The
     * first vertex remains the same.
     */
    @Override
    public default LinearRing2D reverse()
    {
        // create a new collection of vertices in reverse order, keeping first
        // vertex unchanged.
        int n = this.vertexCount();
        LinearRing2D res = LinearRing2D.create(n);
        res.addVertex(this.vertexPosition(0));
        for (int i = 1; i < n; i++)
        {
            res.addVertex(this.vertexPosition(n - i));
        }

        return res;
    }

    public default Point2D getPointAtLength(double pos)
    {
        double cumSum = 0;
        Iterator<Point2D> vertexIter = this.vertexPositions().iterator();
        Point2D prev = vertexIter.next();
        while(vertexIter.hasNext())
        {
            Point2D vertex = vertexIter.next();
            double dist = vertex.distance(prev);
            cumSum += dist;
            if (cumSum >= pos)
            {
                double pos0 = pos - cumSum + dist;
                double t = pos0 / dist;
                return Point2D.interpolate(prev, vertex, t);
            }
            prev = vertex;
        }
        
        // specific processing of last edge
        Point2D vertex = this.vertexPositions().iterator().next();
        double dist = vertex.distance(prev);
        cumSum += dist;
        if (cumSum >= pos)
        {
            double pos0 = pos - cumSum + dist;
            double t = pos0 / dist;
            return Point2D.interpolate(prev, vertex, t);
        }
        
        // otherwise return the first/last vertex
        return vertex;
    }

    
    // ===================================================================
    // Management of edges
    

    // ===================================================================
    // Methods implementing the Curve2D interface
    
    public default LinearRing2D resampleBySpacing(double spacing)
    {
        // compute vertex number of resulting curve
        double length = this.length();
        int nv = (int) Math.round(length / spacing);
        LinearRing2D res = LinearRing2D.create(nv);
        
        // adjust step length to avoid last edge to have different size
        double spacing2 = length / (nv + 1);
        
        // create new vertices
        for (int i = 0; i < nv; i++)
        {
            double pos = Math.min(i * spacing2, length);
            res.addVertex(this.getPointAtLength(pos));
        }
        
        return res;
    }
    
    public default double length()
    {
        // init
        double cumSum = 0.0;
        Iterator<Point2D> vertexIter = this.vertexPositions().iterator();
        Point2D firstPoint = vertexIter.next();
        Point2D prev = firstPoint;
        
        // iterate over pairs of adjacent vertices
        while(vertexIter.hasNext())
        {
            Point2D vertex = vertexIter.next();
            double dist = vertex.distance(prev);
            cumSum += dist;
            prev = vertex;
        }
        
        // add distance between last and first vertices 
        double dist = prev.distance(firstPoint);
        cumSum += dist;
        
        return cumSum;
    }
    
    @Override
    public default Point2D getPoint(double t)
    {
        int nv = vertexCount();
        t = Math.min(Math.max(t, 0), nv);

        // index of vertex before point
        int ind0 = (int) Math.floor(t + Double.MIN_VALUE);
        double tl = t - ind0;

        if (ind0 == nv)
            ind0 = 0;
        Point2D p0 = vertexPosition(ind0);

        // index of vertex after point
        int ind1 = ind0 + 1;
        if (ind1 == nv)
            ind1 = 0;
        Point2D p1 = vertexPosition(ind1);

        // interpolate on current line;
        return Point2D.interpolate(p0, p1, tl);
    }

    @Override
    public default double t0()
    {
        return 0;
    }

    @Override
    public default double t1()
    {
        return vertexCount();
    }

    @Override
    public default boolean isClosed()
    {
        return true;
    }
    
    
    // ===================================================================
    // Geometry interface implementation
    
    /**
     * Transforms this geometry with the specified affine transform.
     * 
     * @param trans
     *            an affine transform
     * @return the transformed line string
     */
    public default LinearRing2D transform(AffineTransform2D trans)
    {
        int n = this.vertexCount();
        LinearRing2D res = LinearRing2D.create(n);
        for (Point2D pos : this.vertexPositions())
        {
            res.addVertex(pos.transform(trans));
        }
        
        return res;
    }

    @Override
    public LinearRing2D duplicate();
}
