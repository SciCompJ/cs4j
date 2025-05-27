/**
 * 
 */
package net.sci.geom.mesh3d.process;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import net.sci.geom.geom3d.Plane3D;
import net.sci.geom.geom3d.Point3D;
import net.sci.geom.geom3d.polyline.LineString3D;
import net.sci.geom.geom3d.polyline.LinearRing3D;
import net.sci.geom.geom3d.polyline.Polyline3D;
import net.sci.geom.mesh3d.Mesh3D;
import net.sci.geom.mesh3d.TriMesh3D;

/**
 * @author dlegland
 *
 */
public class IntersectionMeshPlane
{
    public static final Collection<Polyline3D> intersectionMeshPlane(TriMesh3D mesh, Plane3D plane)
    {
        // isolate list of edges that intersect plane
        ArrayList<Mesh3D.Edge> intersectingEdges = new ArrayList<Mesh3D.Edge>();
        ArrayList<Mesh3D.Edge> extremityEdges = new ArrayList<Mesh3D.Edge>();
        for (Mesh3D.Edge edge : mesh.edges())
        {
            if (edge.curve().intersects(plane))
            {
                intersectingEdges.add(edge);
                
                if (mesh.edgeFaces(edge).size() == 1)
                {
                    extremityEdges.add(edge);
                }
            }
        }
        
        // initialize result array
        ArrayList<Polyline3D> curves = new ArrayList<Polyline3D>();
        
        // Process open polylines
        // pick an extremity and process it
        while (!extremityEdges.isEmpty())
        {
            Mesh3D.Edge currentEdge = extremityEdges.iterator().next();
            extremityEdges.remove(currentEdge);
            intersectingEdges.remove(currentEdge);
        
            // add intersection point to current list
            ArrayList<Point3D> intersections = new ArrayList<Point3D>();
            intersections.add(currentEdge.curve().intersection(plane));
            
            // retrieve faces
            Collection<? extends Mesh3D.Face> faces = mesh.edgeFaces(currentEdge);
            if (faces.size() != 1)
            {
                throw new RuntimeException("Initial edge must be adjacent to only one face");
            }
            
            // choose one of the two faces to start iteration
            Iterator<? extends Mesh3D.Face> iter = faces.iterator();
            Mesh3D.Face currentFace = iter.next();
            
            // iterate while we find initial edge
            while (true)
            {
                // switch to next edge
                currentEdge = oppositeEdge(mesh, currentFace, currentEdge, intersectingEdges);
                
                // process next edge
                intersectingEdges.remove(currentEdge);
                intersections.add(currentEdge.curve().intersection(plane));
                
                // retrieve faces
                faces = mesh.edgeFaces(currentEdge);
                if (faces.size() == 1)
                {
                    extremityEdges.remove(currentEdge);
                    break;
                }
                
                // identify the two adjacent faces
                iter = faces.iterator();
                Mesh3D.Face face1 = iter.next();
                Mesh3D.Face face2 = iter.next();
                currentFace = face1.equals(currentFace) ? face2 : face1;
            }
            
            // add a new polyline to the result
            LineString3D poly = LineString3D.create(intersections);
            curves.add(poly);
        }
        
        
        // process rings:
        // pick an edge and process until list is empty
        while (!intersectingEdges.isEmpty())
        {
            // pick an arbitrary edge
            Mesh3D.Edge currentEdge = intersectingEdges.iterator().next();
            intersectingEdges.remove(currentEdge);
            
            // add intersection point to current list
            ArrayList<Point3D> intersections = new ArrayList<Point3D>();
            intersections.add(currentEdge.curve().intersection(plane));
            
            // retrieve faces
            Collection<? extends Mesh3D.Face> faces = mesh.edgeFaces(currentEdge);
            checkFaceCountIsTwo(faces);
            
            // choose one of the two faces to start iteration
            Iterator<? extends Mesh3D.Face> iter = faces.iterator();
            Mesh3D.Face lastFace = iter.next();
            Mesh3D.Face currentFace = iter.next();
            
            // iterate while we find initial edge
            while (!currentFace.equals(lastFace))
            {
                // switch to next edge
                currentEdge = oppositeEdge(mesh, currentFace, currentEdge, intersectingEdges);
                
                // process next edge
                intersectingEdges.remove(currentEdge);
                intersections.add(currentEdge.curve().intersection(plane));
                
                // retrieve faces
                faces = mesh.edgeFaces(currentEdge);
                checkFaceCountIsTwo(faces);
                
                // identify the two adjacent faces
                iter = faces.iterator();
                Mesh3D.Face face1 = iter.next();
                Mesh3D.Face face2 = iter.next();
                currentFace = face1.equals(currentFace) ? face2 : face1;
            }
            
            // add a new polyline to the result
            LinearRing3D ring = LinearRing3D.create(intersections);
            curves.add(ring);
        }
        
        return curves;
    }
    
    private static final void checkFaceCountIsTwo(Collection<? extends Mesh3D.Face> faces)
    {
        if (faces.size() == 1)
        {
            throw new RuntimeException("Can not manage open meshes");
        }
        if (faces.size() != 2)
        {
            throw new RuntimeException("Found an edge connected to " + faces.size() + " faces");
        }
    }
    
    private static final Mesh3D.Edge oppositeEdge(Mesh3D mesh, Mesh3D.Face face, Mesh3D.Edge edge, Collection<Mesh3D.Edge> intersectingEdges)
    {
        for (Mesh3D.Edge edg : mesh.faceEdges(face))
        {
            if (edg.equals(edge))
                continue;
            if (intersectingEdges.contains(edg))
                return edg;
        }
        throw new RuntimeException("Could not find opposite edge");
    }
}
