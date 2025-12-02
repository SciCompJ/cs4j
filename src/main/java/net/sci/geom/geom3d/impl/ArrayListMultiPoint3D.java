/**
 * 
 */
package net.sci.geom.geom3d.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import net.sci.geom.geom3d.MultiPoint3D;
import net.sci.geom.geom3d.Point3D;

/**
 * Implementation of MultiPoint3D based on an ArrayList of Point3D.
 */
public class ArrayListMultiPoint3D implements MultiPoint3D
{
    // ===================================================================
    // Class variables

    /**
     * The inner array of points.
     */
    private ArrayList<Point3D> points;

    
    // ===================================================================
    // Constructors

    /** Empty constructor. */
    public ArrayListMultiPoint3D(int initialCapacity)
    {
        this.points = new ArrayList<Point3D>(initialCapacity);
    }

    /**
     * Constructor from a collection of points.
     * 
     * @param points
     *            the collection of points that compose the geometry
     */
    public ArrayListMultiPoint3D(Collection<Point3D> points)
    {
        this.points = new ArrayList<Point3D>(points.size());
        this.points.addAll(points);
    }


    // ===================================================================
    // Point management methods
    
    public void addPoint(Point3D p)
    {
        this.points.add(p);
    }
    
    @Override
    public Collection<Point3D> points()
    {
        return Collections.unmodifiableCollection(this.points);
    }

    public int pointCount()
    {
        return this.points.size();
    }
    

    // ===================================================================
    // Methods implementing the Geometry3D interface
    
    @Override
    public MultiPoint3D duplicate()
    {
       return new ArrayListMultiPoint3D(points);
    }
}
