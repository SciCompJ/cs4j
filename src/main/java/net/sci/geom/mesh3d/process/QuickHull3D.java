/**
 * 
 */
package net.sci.geom.mesh3d.process;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import net.sci.geom.geom3d.Bounds3D;
import net.sci.geom.geom3d.Point3D;
import net.sci.geom.geom3d.Polygon3D;
import net.sci.geom.geom3d.Vector3D;
import net.sci.geom.geom3d.impl.DefaultPolygon3D;
import net.sci.geom.mesh3d.Mesh3D;
import net.sci.geom.mesh3d.SimplePolygonalMesh3D;
import net.sci.geom.mesh3d.process.QuickHull3D.Mesh.Face;
import net.sci.geom.mesh3d.process.QuickHull3D.Mesh.Face.State;
import net.sci.geom.mesh3d.process.QuickHull3D.Mesh.HalfEdge;
import net.sci.geom.mesh3d.process.QuickHull3D.Mesh.Vertex;

/**
 * <p>Computes the 3D convex hull of an array of 3D points, using the QuickHull
 * algorithm. The reference paper was written by Barber, Dobkin, and Huhdanpaa.</p>
 * 
 * <p>This implementation is based on "quickhull3d - A Robust 3D Convex Hull
 * Algorithm in Java" by Richard van Nieuwenhoven 
 * (<a href="https://github.com/Quickhull3d/quickhull3d">https://github.com/Quickhull3d/quickhull3d</a>)
 * itself based on the original paper and on the qhull code source in C.</p>
 * 
 * <p>
 * The present implementation includes a number of modifications:
 * </p>
 * <ul>
 * <li>use the core classes ofthe "net.sci.geom" package</li>
 * <li>re-organize the computation code</li>
 * <li>returns the result as an instance of the {@code Mesh} class, 
 * that implements the {@code net.sci.geom.mesh3d.Mesh3D} interface</li>
 * <li>generlize the usage of the {@code Collection} API</li>
 * <li>make management of computation tolerance more generic</li>
 * </ul>
 * 
 * Example of use:
 * {@snippet lang = "java" :
 * Collection<Point3D> points = ...
 * QuickHull3D op = new QuickHull3D();
 * QuickHull3D.Mesh mesh = op.process(points);
 * Collection<Point3D> hullVertices = mesh.vertexPositions();
 * int[][] hullFaces = mesh.getFaceVertexIndices();
 * }
 */
public class QuickHull3D
{
    // ===================================================================
    // Constants

    /**
     * Precision of a double. Used to initialize the tolerance.
     */
    static private final double DOUBLE_PREC = 2.2204460492503131e-16;

    
    // ===================================================================
    // Class variables

    protected boolean debug = false;

    protected ArrayList<HalfEdge> horizon = new ArrayList<HalfEdge>();

    /**
     * The list of points associated to a face, than need to be check for
     * addition to hull.
     */
    private VertexList claimedPoints = new VertexList();

    /**
     * A list a unclaimed points, resulting from deletion of a face. Unclaimed
     * points are resolved after computation of new faces.
     */
    private VertexList unclaimedPoints = new VertexList();
    
    /**
     * An array of faces to discard, during "mergeFaces" method.
     * @see #mergeFaces(Face, HalfEdge)
     */
    private Face[] discardedFaces = new Face[3];

    
    // Tolerance management
    
    /**
     * The tolerance value used for numerical predicates (non-convex edges...).
     * Initialized either from the absolute tolerance value, or relative to the
     * extent of the input data.
     * 
     * initialized with by the {@code toleranceComputation} class.
     */
    private double tolerance;
    
    private boolean recomputeVertexIndices = true;
    
    private Tolerance toleranceComputation = new RelativeTolerance(3 * DOUBLE_PREC);

    
    // ===================================================================
    // Constructor
    
    /**
     * Creates an empty QuickHull3D object.
     */
    public QuickHull3D()
    {
    }
    
    
    // ===================================================================
    // main processing methods
    
    /**
     * Computes the convex hull of the specified collection of points.
     * 
     * @param points
     *            a collection of 3D points
     * @return the 3D polygon mesh representing the convex hull of the points.
     */
    public Mesh process(Collection<Point3D> points)
    {
        // check validity of input arguments
        if (points.size() < 4)
        {
            throw new IllegalArgumentException("Requires at least four points to build convex hull");
        }
        
        // initialize algorithm data
        claimedPoints.clear();
        
        // create a new mesh containing only vertices
        Mesh mesh = Mesh.init(points);
        
        // initialize simplex hull from initial points
        buildInitialMesh(mesh);
        
        // process the claimed points
        buildConvexHull(mesh);
        
        // cleanup
        mesh.removeNonVisibleFaces();
        
        // once hull vertices are identified, their index can be computed. Index
        // of other vertices is set to -1.
        if (this.recomputeVertexIndices)
        {
            mesh.recomputeVertexIndices();
        }
        
        if (debug)
        {
            System.out.println("convex hull computed");
        }
        
        return mesh;
    }
    
    /**
     * Computes the convex hull of the specified point array.
     * 
     * @param points
     *            an array of 3D points
     * @return the 3D polygon mesh representing the convex hull of the points.
     */
    public Mesh process(Point3D[] points)
    {
        // check validity of input arguments
        if (points.length < 4)
        {
            throw new IllegalArgumentException("Requires at least four points to build convex hull");
        }
        
        // initialize algorithm data
        claimedPoints.clear();
        
        // create a new mesh containing only vertices
        Mesh mesh = Mesh.init(points);
        
        return computeConvexHull(mesh);
    }
    
    /**
     * Computes the convex hull of a mesh initialized with the position of the
     * points.
     * 
     * @param mesh
     *            the initial mesh, containing only the initial vertices
     * @return the convex hull of the points
     */
    private Mesh computeConvexHull(Mesh mesh)
    {
        // initialize simplex hull from initial points
        buildInitialMesh(mesh);
        
        // process the claimed points
        buildConvexHull(mesh);
        
        // cleanup
        mesh.removeNonVisibleFaces();
        
        // once hull vertices are identified, their index can be computed. Index
        // of other vertices is set to -1.
        if (this.recomputeVertexIndices)
        {
            mesh.recomputeVertexIndices();
        }
        
        if (debug)
        {
            System.out.println("convex hull computed");
        }
        
        return mesh;
    }
    

    // ===================================================================
    // Hull initialization methods
    
    private Mesh buildInitialMesh(Mesh mesh)
    {
        // identifies extreme vertices along each direction
        Vertex[][] extremeVertices = findExtremeVertices(mesh.vertices);
        
        // use extreme vertices to setup tolerance
        Bounds3D bounds = extremeVerticesBounds(extremeVertices); 
        this.tolerance = this.toleranceComputation.computeTolerance(bounds);
        if (debug)
        {
            System.out.println("tolerance: " + tolerance);
        }
        
        // find vertices of the initial simplex
        Vertex[] initialVertices = findInitialVertices(mesh.vertices, extremeVertices, tolerance);
        if (debug)
        {
            System.out.println("initial vertices:");
            System.out.println(initialVertices[0].index + ": " + initialVertices[1].position);
            System.out.println(initialVertices[1].index + ": " + initialVertices[1].position);
            System.out.println(initialVertices[2].index + ": " + initialVertices[2].position);
            System.out.println(initialVertices[3].index + ": " + initialVertices[3].position);
        }
        
        // create faces of the initial simplex
        mesh.computeInitialSimplexFaces(initialVertices);
        
        // associate each vertex (except the initial ones) to one of the faces
        // of the initial simplex
        initializeClaimedPoints(mesh, initialVertices);
        
        return mesh;
    }
    
    /**
     * Identifies the vertices with minimum and maximum coordinates along each
     * dimension.
     * 
     * @return a 3-by-2 array of vertices: for each dimension, the vertices with
     *         min and max coordinates
     */
    private static final Vertex[][] findExtremeVertices(Vertex[] vertices)
    {
        // initialize min and max coordinates to that of first vertex
        Point3D pos0 = vertices[0].position;
        double minX = pos0.x(), maxX = pos0.x();
        double minY = pos0.y(), maxY = pos0.y();
        double minZ = pos0.z(), maxZ = pos0.z();

        // array of extreme vertices:
        // first index is for dimension,
        // second index is for min or max
        Vertex[][] extremeVertices = new Vertex[3][2];
        
        // init extreme vertices to first vertex
        for (int i = 0; i < 3; i++)
        {
            extremeVertices[i][0] = vertices[0];
            extremeVertices[i][1] = vertices[0];
        }
        
        for (Vertex v : vertices)
        {
            Point3D pos = v.position;
            
            // check x-coordinates
            if (pos.x() > maxX)
            {
                maxX = pos.x();
                extremeVertices[0][1] = v;
            }
            else if (pos.x() < minX)
            {
                minX = pos.x();
                extremeVertices[0][0] = v;
            }
            
            // check y-coordinates
            if (pos.y() > maxY)
            {
                maxY = pos.y();
                extremeVertices[1][1] = v;
            }
            else if (pos.y() < minY)
            {
                minY = pos.y();
                extremeVertices[1][0] = v;
            }

            // check z-coordinates
            if (pos.z() > maxZ)
            {
                maxZ = pos.z();
                extremeVertices[2][1] = v;
            }
            else if (pos.z() < minZ)
            {
                minZ = pos.z();
                extremeVertices[1][0] = v;
            }
        }

        return extremeVertices;
    }
    
    private static final Bounds3D extremeVerticesBounds(Vertex[][] extremeVertices)
    {
        // retrieve min/max coordinates
        double xMin = extremeVertices[0][0].position.x();
        double xMax = extremeVertices[0][1].position.x();
        double yMin = extremeVertices[1][0].position.y();
        double yMax = extremeVertices[1][1].position.y();
        double zMin = extremeVertices[2][0].position.z();
        double zMax = extremeVertices[2][1].position.z();
        return new Bounds3D(xMin, xMax, yMin,yMax, zMin, zMax);
    }
    
    private static final Vertex[] findInitialVertices(Vertex[] vertices, Vertex[][] extremeVertices, double tolerance)
    {
        // identify the direction with the largest extent of vertex coordinates
        int imax = findDirectionWithLargestExtent(extremeVertices, tolerance);
    
        // choose the first two vertices as extreme vertices along this direction
        Vertex v0 = extremeVertices[imax][1];
        Vertex v1 = extremeVertices[imax][0];
    
        // choose the third vertex as the farthest vertex from the line between the
        // first two vertices
        double maxNorm = 0;
        Vertex v2 = null;
        for (Vertex vertex : vertices)
        {
            // make sure vertex was not already chosen
            if (vertex != v0 && vertex != v1)
            {
                // use cross product for computing distance from point to line
                double dist = Vertex.crossProduct(v0, v1, vertex).norm();
    
                // keep the point farthest from the line 
                if (dist > maxNorm)
                {
                    maxNorm = dist;
                    v2 = vertex;
                }
            }
        }
        
        // check for co-linearity
        Vector3D normal = Vertex.crossProduct(v0, v1, v2);
        if (normal.norm() <= 100 * tolerance)
        {
            throw new IllegalArgumentException("Input points appear to be colinear");
        }
        normal = normal.normalize();
    
        // choose fourth point as the farthest vertex from the plane formed by
        // the first three vertices
        double maxDist = 0;
        double d0 = Vector3D.of(v2.position).dotProduct(normal);
        
        Vertex v3 = null;
        for (Vertex vertex : vertices)
        {
            if (vertex != v0 && vertex != v1 && vertex != v2)
            {
                double dist = Math.abs(Vector3D.of(vertex.position).dotProduct(normal) - d0);
                if (dist > maxDist)
                {
                    maxDist = dist;
                    v3 = vertex;
                }
            }
        }
        
        // check for co-planarity
        if (Math.abs(maxDist) <= 100 * tolerance)
        {
            throw new IllegalArgumentException("Input points are found to be coplanar");
        }
    
        return new Vertex[] {v0, v1, v2, v3};
    }
    
    private static final int findDirectionWithLargestExtent(Vertex[][] extremeVertices, double tolerance)
    {
        // identify the direction with the largest extent of vertex coordinates
        double maxExtent = 0;
        int imax = 0;
        for (int d = 0; d < 3; d++)
        {
            double extent = extremeVertices[d][1].position.get(d) - extremeVertices[d][0].position.get(d);
            if (extent > maxExtent)
            {
                maxExtent = extent;
                imax = d;
            }
        }
    
        if (maxExtent <= tolerance)
        { 
            throw new IllegalArgumentException("Input points appear to be coincident"); 
        }
        return imax;
    }
    
    /**
     * Associates each vertex that is not a vertex of the initial simplex to the
     * face with the largest signed distance.
     * 
     * @param simplex
     *            the initial simplex.
     */
    private void initializeClaimedPoints(Mesh mesh, Vertex[] initialVertices)
    {
        // Initialize the set of claimed points
        for (Vertex v : mesh.vertices)
        {
            // do not process vertices of the initial simplex
            if (v == initialVertices[0] || v == initialVertices[1] || v == initialVertices[2] || v == initialVertices[3])
            {
                continue;
            }

            // find the face with largest point-to-plane distance
            Face maxDistFace = findMaxDistFace(v, mesh.faces);
            
            // if no face was found (meaning distance is negative or below
            // tolerance), then the point is within the hull
            if (maxDistFace != null)
            {
                // associate vertex to face 
                addPointToFace(v, maxDistFace);
            }
        }
    }
    
    /**
     * Finds the face with largest (signed) point to plane distance. If it does
     * not exist, this means that the distance is negative or below tolerance,
     * and that the point is within the hull.
     * 
     * @param v
     *            the vertex to check
     * @param faces
     *            the array of faces to consider
     * @return the face with largest point to plane distance, or null if no such
     *         face exist.
     */
    private Face findMaxDistFace(Vertex v, Iterable<Face> faces)
    {
        double maxDist = tolerance;
        Face maxDistFace = null;
        for (Face face : faces)
        {
            double dist = face.distanceToPlane(v.position);
            if (dist > maxDist)
            {
                maxDistFace = face;
                maxDist = dist;
            }
        }
        return maxDistFace;
    }
    

    // ===================================================================
    // Hull building methods

    /**
     * Adds candidate points to the hull until the list is empty.
     */
    private void buildConvexHull(Mesh mesh)
    {
        // main iteration, adding candidates point to hull until list is empty
        int count = 0;
        while (!claimedPoints.isEmpty())
        {
            Vertex eyeVertex = findNextPointToAdd(claimedPoints);
            
            addPointToHull(mesh, eyeVertex);
            count++;
            if (debug)
            {
                System.out.println("iteration " + count + " done");
            }
        }
    }

    /**
     * Identifies the next point to add to the hull.
     * 
     * Pick one point from the "claimed" list, and check its validity.
     * 
     * @return the next vertex that has to be added
     */
    private Vertex findNextPointToAdd(VertexList claimedPoints)
    {
        // pick a point
        if (!claimedPoints.isEmpty())
        {
            // face associated to first claimed point
            Face face = claimedPoints.first().face;
            
            // find the vertex the farthest from the face
            Vertex eyeVertex = null;
            double maxDist = 0;
            
            // find vertex associated to face with largest distance to face
            for (Vertex vertex = face.firstClaimed; vertex != null && vertex.face == face; vertex = vertex.next)
            {
                double dist = face.distanceToPlane(vertex.position);
                if (dist > maxDist)
                {
                    maxDist = dist;
                    eyeVertex = vertex;
                }
            }
            return eyeVertex;
        }
        else
        {
            return null;
        }
    }
    
    /**
     * Adds an identified "eye vertex" to the convex hull.
     * 
     * @param hull
     *            the convex hull to update
     * @param eyeVertex
     *            the vertex to add.
     */
    private void addPointToHull(Mesh mesh, Vertex eyeVertex)
    {
        unclaimedPoints.clear();
         
        if (debug)
        {
            System.out.println("Adding point: " + eyeVertex.index);
            System.out.println(" which is " + eyeVertex.face.distanceToPlane(eyeVertex.position) + " above face " + eyeVertex.face.toString());
        }
        
        // remove eye vertex from list of points to process
        eyeVertex.face.removePoint(eyeVertex);
        claimedPoints.delete(eyeVertex);

        // compute horizon seen be eye vertex
        horizon.clear();
        computeHorizon(eyeVertex, null, eyeVertex.face);
        
        // creates the new faces
        ArrayList<Face> newFaces = createNewFaces(mesh, eyeVertex, horizon);

        // Creating new faces may create non convexities, 
        // making it necessary to merge some adjacent faces
        removeConcavities(newFaces);
        
        // cleanup orphan points
        resolveUnclaimedPoints(newFaces);
    }
    
    private void computeHorizon(Vertex eyePnt, HalfEdge edge0, Face face)
    {
        // make points associated to face as unclaimed
        setFacePointsAsUnclaimed(face);
        
        // face is marked as deleted to avoid infinite iteration
        face.state = State.DELETED;
        if (debug)
        {
            System.out.println("  visiting face: " + face.toString());
        }
        
        // prepare for iteration
        HalfEdge edge;
        if (edge0 == null)
        {
            edge0 = face.getEdge(0);
            edge = edge0;
        }
        else
        {
            edge = edge0.next;
        }
        
        // iterate on half-edges of the face
        do
        {
            Face oppFace = edge.twinFace();
            if (oppFace.state == State.VISIBLE)
            {
                // check for concavity
                if (oppFace.distanceToPlane(eyePnt.position) > tolerance)
                {
                    // propagate horizon on faces visible from the eye vertex
                    computeHorizon(eyePnt, edge.twin(), oppFace);
                }
                else
                {
                    horizon.add(edge);
                    if (debug)
                    {
                        System.out.println("  adding edge to horizon:" + edge.toString());
                    }
                }
            }
            edge = edge.next;
        } while (edge != edge0);
    }
    
    private void removeConcavities(ArrayList<Face> newFaces)
    {
        // Creating new faces may create non convexities, 
        // making it necessary to merge some adjacent faces
        
        // first merge pass: 
        // merge faces which are non-convex as determined by the largest face
        for (Face face : newFaces)
        {
            if (face.state == State.VISIBLE)
            {
                while (mergeAdjacentFaces_LargestFace(face))
                    ;
            }
        }

        // second merge pass: 
        // merge faces which are non-convex wrt either face
        for (Face face : newFaces)
        {
            if (face.state == State.NON_CONVEX)
            {
                face.state = State.VISIBLE;
                while (mergeAdjacentNonConvexFaces(face))
                    ;
            }
        }
    }
    
    /**
     * Removes all points associated to the specified face, updating the list of
     * unclaimed vertices.
     * 
     * @param face
     *            the face to update
     */
    private void setFacePointsAsUnclaimed(Face face)
    {
        Vertex faceVertex = removeFacePointsFromClaimed(face);
        if (faceVertex != null)
        {
            unclaimedPoints.addAll(faceVertex);
        }
    }

    /**
     * Removes points associated to the specified face from the list of
     * claimed points.
     * 
     * @param face
     *            the face to update
     * @return a pointer to the list of points associated to the face
     */
    private Vertex removeFacePointsFromClaimed(Face face)
    {
        // if no vertex associated to face, nothing to do 
        if (face.firstClaimed == null) 
        {
            return null;
        }
        
        // identify the contiguous list of vertices associated to the specified
        // face
        Vertex vertex = face.firstClaimed;
        while (vertex.next != null && vertex.next.face == face)
        {
            vertex = vertex.next;
        }
        
        // remove them from the list of claimed vertices
        claimedPoints.delete(face.firstClaimed, vertex);
        vertex.next = null;
        
        // return the vertex list (as a pointer to the first point)
        return face.firstClaimed;
    }

    /**
     * Creates new faces from the horizon and the eye vertex.
     * 
     * @param hull
     *            the convex hull to update
     * @param eyeVertex
     *            the eye vertex corresponding to the new hull vertex
     * @param horizon
     *            the list of horizon edges
     */
    private ArrayList<Face> createNewFaces(Mesh hull, Vertex eyeVertex, ArrayList<HalfEdge> horizon)
    {
        ArrayList<Face> newFaces = new ArrayList<Face>(horizon.size());
        
        HalfEdge sideEdgePrev = null;
        HalfEdge sideEdgeBegin = null;

        for (HalfEdge edge : horizon)
        {
            HalfEdge sideEdge = addAdjoiningFace(hull, eyeVertex, edge);
            if (debug)
            {
                System.out.println("new face: " + sideEdge.face.toString());
            }
            if (sideEdgePrev != null)
            {
                HalfEdge.setAsTwin(sideEdge.next, sideEdgePrev);
            }
            else
            {
                sideEdgeBegin = sideEdge;
            }
            newFaces.add(sideEdge.face());
            sideEdgePrev = sideEdge;
        }
        HalfEdge.setAsTwin(sideEdgeBegin.next, sideEdgePrev);
        
        return newFaces;
    }

    /**
     * Creates a face from the specified half-edge (from the horizon) and the
     * eye vertex.
     * 
     * @param hull
     *            the convex hull to update
     * @param eyeVertex
     *            the eye vertex
     * @param he
     *            the half-edge
     * @return the first hald-edge of the face
     */
    private HalfEdge addAdjoiningFace(Mesh hull, Vertex eyeVertex, HalfEdge he)
    {
        Face face = hull.addTriangleFace(eyeVertex, he.tail(), he.head());
        HalfEdge.setAsTwin(face.getEdge(-1), he.twin());
        return face.getEdge(0);
    }
    
    /**
     * Merge adjacent faces with respect to the largest face.
     * 
     * @param face
     *            the initial face to merge
     * @return a boolean indicating whether merger was applied
     */
    private boolean mergeAdjacentFaces_LargestFace(Face face)
    {
        HalfEdge edge = face.edge0;

        boolean merge = false;
        boolean convex = true;
        
        do
        {
            Face twinFace = edge.twinFace();

            // merge faces if they are parallel or non-convex wrt to the larger face;
            // otherwise, just mark the face as non-convex for the second merge pass.
            if (face.area > twinFace.area)
            {
                if (isNonConvex(edge))
                {
                    merge = true;
                }
                else if (isNonConvex(edge.twin()))
                {
                    convex = false;
                }
            }
            else
            {
                if (isNonConvex(edge.twin()))
                {
                    merge = true;
                }
                else if (isNonConvex(edge))
                {
                    convex = false;
                }
            }

            if (merge)
            {
                mergeFaces(face, edge);
                return true;
            }
            edge = edge.next;
        } while (edge != face.edge0);
        
        // mark the face for next merge phase
        if (!convex)
        {
            face.state = State.NON_CONVEX;
        }
        
        return false;
    }
    
    /**
     * Merge adjacent faces if they are definitely non convex.
     * 
     * @param face
     *            the initial face to merge
     * @return a boolean indicating whether merge was applied
     */
    private boolean mergeAdjacentNonConvexFaces(Face face)
    {
        HalfEdge edge = face.edge0;
        do
        {
            // check for merge
            if (isNonConvex(edge) || isNonConvex(edge.twin))
            {
                mergeFaces(face, edge);
                return true;
            }
            edge = edge.next;
        } while (edge != face.edge0);
        
        return false;
    }
    
    /**
     * Determines if the specified edge is non-conve with respect to the current
     * algorithm tolerance. Criteria is the signed distance of the twin face
     * centroid to the plane containing the edge face.
     * 
     * @param edge
     *            the half-edge to check
     * @return true is the edge is found to be non convex
     */
    private boolean isNonConvex(HalfEdge edge)
    {
        return edge.face.distanceToPlane(edge.twin.face.centroid()) > -tolerance;
    }


    private void mergeFaces(Face face, HalfEdge edge)
    {
        if (debug)
        {
            System.out.println("  merging " + face + "  and  " + edge.twinFace());
        }

        int nDiscarded = face.mergeAdjacentFace(edge, discardedFaces);
        for (int i = 0; i < nDiscarded; i++)
        {
            deleteFacePoints(discardedFaces[i], face);
        }
        if (debug)
        {
            System.out.println("  result: " + face);
        }
    }
    
    /**
     * Removes all the points associated to a face from this face, associating
     * them either to the absorbing face or to the list of unclaimed vertices.
     * 
     * @param face
     *            the face containing the points to remove
     * @param absorbingFace
     *            a face that can reference some of the removed points
     */
    private void deleteFacePoints(Face face, Face absorbingFace)
    {
        Vertex faceVertex = removeFacePointsFromClaimed(face);
        if (faceVertex != null)
        {
            Vertex nextVertex = faceVertex;
            for (Vertex vertex = nextVertex; vertex != null; vertex = nextVertex)
            {
                nextVertex = vertex.next;
                double dist = absorbingFace.distanceToPlane(vertex.position);
                if (dist > tolerance)
                {
                    addPointToFace(vertex, absorbingFace);
                }
                else
                {
                    unclaimedPoints.add(vertex);
                }
            }
        }
    }

    /**
     * Iterates over the points stored in the "unclaimedVertices" array, and
     * either associates them to one of the faces of newFaces, or removes them
     * from the points to process.
     * 
     * @param newFaces
     *            the list of faces that can be associated to new points
     */
    private void resolveUnclaimedPoints(ArrayList<Face> newFaces)
    {
        Vertex nextVertex = unclaimedPoints.first();
        for (Vertex vertex = nextVertex; vertex != null; vertex = nextVertex)
        {
            nextVertex = vertex.next;

            double maxDist = tolerance;
            Face maxFace = null;
            for (Face face : newFaces)
            {
                if (face.isVisible())
                {
                    double dist = face.distanceToPlane(vertex.position);
                    if (dist > maxDist)
                    {
                        maxDist = dist;
                        maxFace = face;
                    }
                    
                    // if maxDist is large enough, breaks the loop to avoid too
                    // many computations
                    if (maxDist > 1000 * tolerance)
                    {
                        break;
                    }
                }
            }
            
            if (maxFace != null)
            {
                addPointToFace(vertex, maxFace);
            }
        }
    }

    /**
     * Adds the vertex to the the list of claimed vertices, associating it to the
     * face pointing towards it.
     * 
     * @param vertex
     *            the vertex to add
     * @param face
     *            the face associated to the vertex
     */
    private void addPointToFace(Vertex vertex, Face face)
    {
        vertex.face = face;
    
        if (face.firstClaimed == null)
        {
            claimedPoints.add(vertex);
        }
        else
        {
            // insert into the list, grouping vertices by face
            claimedPoints.insertBefore(vertex, face.firstClaimed);
        }
        face.firstClaimed = vertex;
    }


    // ===================================================================
    // Management of tolerance
    
    /**
     * Sets the strategy for computing the distance tolerance value. The
     * distance tolerance is used to determine when faces are unambiguously
     * convex with respect to each other, and when points are unambiguously
     * above or below a face plane, in the presence of numerical imprecision.
     * Typical strategies for choosing the tolerance are Absolute, and Relative
     * (computed from the input points).
     * 
     * @param tolerance
     *            the tolerance computation algorithm
     * @see #getToleranceValue()
     */
    public void setToleranceStrategy(Tolerance tolerance)
    {
        this.toleranceComputation = tolerance;
    }

    /**
     * Returns the distance tolerance that was used for the most recently
     * computed hull. The distance tolerance is used to determine when faces are
     * unambiguously convex with respect to each other, and when points are
     * unambiguously above or below a face plane, in the presence of
     * <a href=#distTol>numerical imprecision</a>. Normally, this tolerance is
     * computed automatically for each set of input points, but it can be set
     * explicitly by the application.
     *
     * @return distance tolerance
     * @see #setToleranceStrategy
     */
    public double getToleranceValue()
    {
        return tolerance;
    }
    
    /**
     * Sets the flag for recomputing the index of convex hull vertices at the
     * end of convex hull computation. This will make the vertices vary between
     * 0 and the number of convex hull vertices minus one..
     * 
     * @param bool
     *            the boolean flag
     */
    public void setRecomputeVertexIndices(boolean bool)
    {
        this.recomputeVertexIndices = bool;
    }
    
    
    // ===================================================================
    // Classes for management of tolerance

    /**
     * The interface for computing distance tolerance during computation.
     */
    public static interface Tolerance
    {
        /**
         * Computes the absolute tolerance for distance comparisons, based on
         * the bounds of the original point set.
         * 
         * @param bounds
         *            the 3D bounds of the point set coordinates
         * @return the absolute tolerance for distance comparisons
         */
        public double computeTolerance(Bounds3D bounds);
    }
    
    /**
     * Tolerance computation strategy that uses a fixed absolute value.
     */
    public static class AbsoluteTolerance implements Tolerance
    {
        double value;
        
        /**
         * Default constructor that specifies the absolute value of distance
         * tolerance.
         * 
         * @param value
         *            the absolute tolerance for comparing distances.
         */
        public AbsoluteTolerance(double value)
        {
            this.value = value;
        }

        @Override
        public double computeTolerance(Bounds3D bounds)
        {
            return value;
        }
    }

    /**
     * Tolerance computation strategy that uses a tolerance value relative to
     * the sum of the absolute value of the largest coordinates along each
     * dimension.
     */
    public static class RelativeTolerance implements Tolerance
    {
        double factor;
        
        /**
         * Default constructor that specifies the relative value for tolerance
         * computation.
         * 
         * @param factor
         *            the relative tolerance for comparing distances.
         */
        public RelativeTolerance(double factor)
        {
            this.factor = factor;
        }

        @Override
        public double computeTolerance(Bounds3D bounds)
        {
            return factor * (
                    Math.max(Math.abs(bounds.xMin()), Math.abs(bounds.xMax())) + 
                    Math.max(Math.abs(bounds.yMin()), Math.abs(bounds.yMax())) +
                    Math.max(Math.abs(bounds.zMin()), Math.abs(bounds.zMax())));
        }
    }

    
    // ===================================================================
    // Inner Mesh class

    /**
     * Stores the result of convex hull computation. Contains a reference to all
     * vertices, and to each face of the convex hull. Some vertices may
     * therefore not being referenced by the faces.
     */
    public static class Mesh implements Mesh3D
    {
        /**
         * Creates a new mesh based only on the position of initial vertices.
         * @param points the position of initial vertices.
         */
        private static final Mesh init(Collection<Point3D> points)
        {
            Mesh mesh = new Mesh();
            
            // initialize vertex array, by keeping index of corresponding points
            // within source array
            int np = points.size();
            mesh.vertices  = new Vertex[np];
            int i = 0;
            for (Point3D p : points)
            {
                mesh.vertices[i] = new Vertex(p, i++);    
            }
            
            // also initialize index mapping to identity
            mesh.vertexPointIndices = IntStream.range(0, np).toArray();
            mesh.hullVertexCount = np;

            // initialize empty face array
            mesh.faces = new ArrayList<>();
            
            // also compute bounding box
            mesh.bounds = Bounds3D.of(points);
            return mesh;
        }
        
        /**
         * Creates a new mesh based only on the position of initial vertices.
         * @param points the position of initial vertices.
         */
        private static final Mesh init(Point3D[] points)
        {
            Mesh mesh = new Mesh();
            // initialize vertex array, by keeping index of corresponding points
            // within source array
            mesh.vertices = IntStream.range(0, points.length)
                    .mapToObj(i->new Vertex(points[i], i))
                    .toArray(Vertex[]::new);
            
            // also initialize index mapping to identity
            mesh.vertexPointIndices = IntStream.range(0, points.length).toArray();
            mesh.hullVertexCount = points.length;

            // initialize empty face array
            mesh.faces = new ArrayList<>();
            
            // also compute bounding box
            mesh.bounds = Bounds3D.of(List.of(points));
            return mesh;
        }
        
        
        // -------------------------------------------------------------------
        // Class variables
        
        /**
         * An array of vertices, with a direct mapping to the input points.
         * Vertices are not updated during computation of convex hull; instead, 
         * vertices within the hull become unreferenced by the faces. 
         */
        Vertex[] vertices;
        
        /**
         * The faces of the convex hull, build incrementally.
         */
        ArrayList<Face> faces = new ArrayList<Face>();
        
        /**
         * The mapping between vertex index and index of original point.
         */
        int[] vertexPointIndices = new int[0];

        /**
         * The number of vertices of the convex hull. Computed at the end of the
         * method {@code updateVertexIndices()}.
         */
        protected int hullVertexCount;

        Bounds3D bounds;

        // -------------------------------------------------------------------
        // Constructor
        
        /**
         * Creates a new mesh based only on the position of initial vertices.
         * @param points the position of initial vertices.
         */
        public Mesh(Point3D[] points)
        {
            // initialize vertex array, by keeping index of corresponding points
            // within source array
            this.vertices = IntStream.range(0, points.length)
                    .mapToObj(i->new Vertex(points[i], i))
                    .toArray(Vertex[]::new);
            
            // also initialize index mapping to identity
            this.vertexPointIndices = IntStream.range(0, points.length).toArray();
            this.hullVertexCount = points.length;

            // initialize empty face array
            this.faces = new ArrayList<>();
            
            // also compute bounding box
            this.bounds = Bounds3D.of(List.of(points));
        }
        
        private Mesh()
        {
        }
        
        
        // -------------------------------------------------------------------
        // Computation methods

        private Face[] computeInitialSimplexFaces(Vertex[] initialVertices)
        {
            // retrieve normal and "position" of first face
            Vector3D normal = Vertex.crossProduct(initialVertices[0], initialVertices[1], initialVertices[2]);
            normal = normal.normalize();
            double d0 = Vector3D.of(initialVertices[2].position).dotProduct(normal);
            
            // Create the four faces of the initial tetrahedron
            Face[] faces = new Face[4];
            if (Vector3D.of(initialVertices[3].position).dotProduct(normal) - d0 < 0)
            {
                faces[0] = addTriangleFace(initialVertices[0], initialVertices[1], initialVertices[2]);
                faces[1] = addTriangleFace(initialVertices[3], initialVertices[1], initialVertices[0]);
                faces[2] = addTriangleFace(initialVertices[3], initialVertices[2], initialVertices[1]);
                faces[3] = addTriangleFace(initialVertices[3], initialVertices[0], initialVertices[2]);
        
                for (int i = 0; i < 3; i++)
                {
                    int k = (i + 1) % 3;
                    HalfEdge.setAsTwin(faces[i + 1].getEdge(1), faces[k + 1].getEdge(0));
                    HalfEdge.setAsTwin(faces[i + 1].getEdge(2), faces[0].getEdge(k));
                }
            }
            else
            {
                faces[0] = addTriangleFace(initialVertices[0], initialVertices[2], initialVertices[1]);
                faces[1] = addTriangleFace(initialVertices[3], initialVertices[0], initialVertices[1]);
                faces[2] = addTriangleFace(initialVertices[3], initialVertices[1], initialVertices[2]);
                faces[3] = addTriangleFace(initialVertices[3], initialVertices[2], initialVertices[0]);
        
                for (int i = 0; i < 3; i++)
                {
                    int k = (i + 1) % 3;
                    HalfEdge.setAsTwin(faces[i + 1].getEdge(0), faces[k + 1].getEdge(1));
                    HalfEdge.setAsTwin(faces[i + 1].getEdge(2), faces[0].getEdge((3 - i) % 3));
                }
            }
            
            return faces;
        }

        private Face addTriangleFace(Vertex v0, Vertex v1, Vertex v2)
        {
            Face face = new Face();
            HalfEdge he0 = new HalfEdge(v0, face);
            HalfEdge he1 = new HalfEdge(v1, face);
            HalfEdge he2 = new HalfEdge(v2, face);

            he0.prev = he2;
            he0.next = he1;
            he1.prev = he0;
            he1.next = he2;
            he2.prev = he1;
            he2.next = he0;

            face.edge0 = he0;

            // compute the normal and offset
            face.computeGeometricData();
            this.faces.add(face);
            return face;
        }

        /**
         * Returns the array of vertex positions.
         * 
         * @return the array of vertex positions.
         */
        public Point3D[] getVertexPositions()
        {
            return IntStream.range(0, hullVertexCount)
                    .mapToObj(i -> vertices[vertexPointIndices[i]].position)
                    .toArray(Point3D[]::new);
        }
        
        /**
         * Returns the face information as an array of vertex indices for each face.
         * Vertex indices are 0-based. The number of vertex indices for each face
         * may be variable (ie not only triangle faces).
         * 
         * @return the array of face indices for each face.
         */
        public int[][] getFaceVertexIndices()
        {
            int[][] faceIndices = new int[faceCount()][];
            int k = 0;
            for (Face face : faces())
            {
                faceIndices[k++] = face.vertexIndices();
            }
            return faceIndices;
        }
        
        public Collection<Face> faces()
        {
            return this.faces;
        }
        
        public int faceCount()
        {
            return this.faces.size();
        }
        
        /**
         * Removes non-visiblefaces.
         */
        private void removeNonVisibleFaces()
        {
            Iterator<Face> iter = faces().iterator();
            while(iter.hasNext())
            {
                Face face = iter.next();
                if (!face.isVisible())
                {
                    iter.remove();
                }
            }
        }
        
        // ===================================================================
        // Finalization

        /**
         * Computes the index of vertices belonging to visible faces. Other vertices
         * have index -1. Returns the number of vertices that forms the hull.
         */
        private int recomputeVertexIndices()
        {
            // first initialize the state of all vertices to dummy index
            for (Vertex vertex : vertices)
            {
                vertex.index = -1;
            }
            
            // set index of vertices within hull to 0
            for (Face face : faces())
            {
                face.setVertexIndicesToZero();
            }
            
            // recompute the index of visible vertices
            this.hullVertexCount = 0;
            for (int i = 0; i < vertices.length; i++)
            {
                Vertex vertex = vertices[i];
                if (vertex.index == 0)
                {
                    vertexPointIndices[hullVertexCount] = i;
                    vertex.index = hullVertexCount++;
                }
            }
            return this.hullVertexCount;
        }

        
        // ===================================================================
        // Methods implementing the Mesh3D interface
        
        @Override
        public Collection<? extends Mesh3D.Face> vertexFaces(Mesh3D.Vertex vertex)
        {
            throw new UnsupportedOperationException("Unimplemented operation");
        }

        @Override
        public Collection<? extends Mesh3D.Vertex> vertexNeighbors(Mesh3D.Vertex vertex)
        {
            throw new UnsupportedOperationException("Unimplemented operation");
        }


        @Override
        public Collection<? extends Mesh3D.Vertex> faceVertices(Mesh3D.Face face)
        {
            int[] inds = ((Face) face).vertexIndices();
            ArrayList<Mesh3D.Vertex> verts = new ArrayList<Mesh3D.Vertex>(inds.length);
            for (int index : inds)
            {
                verts.add(vertices[vertexPointIndices[index]]);
            }
            return verts;
        }

        @Override
        public int vertexCount()
        {
            return this.hullVertexCount;
        }

        @Override
        public Iterable<? extends Mesh3D.Vertex> vertices()
        {
            return IntStream.range(0, hullVertexCount)
                    .mapToObj(i -> vertices[vertexPointIndices[i]])
                    .toList();
        }

        @Override
        public Iterable<Point3D> vertexPositions()
        {
            return new Iterable<Point3D>()
            {
                @Override
                public Iterator<Point3D> iterator()
                {
                    return new VertexPositionIterator(vertices());
                }
            };
        }
        
        private class VertexPositionIterator implements Iterator<Point3D>
        {
            Iterator<? extends Mesh3D.Vertex> vertexIterator;
            
            VertexPositionIterator(Iterable<? extends Mesh3D.Vertex> vertices)
            {
                this.vertexIterator = vertices.iterator();
            }
            
            @Override
            public boolean hasNext()
            {
                return vertexIterator.hasNext();
            }

            @Override
            public Point3D next()
            {
                return vertexIterator.next().position();
            }
        }

        @Override
        public net.sci.geom.mesh3d.Mesh3D.Vertex addVertex(Point3D point)
        {
            throw new UnsupportedOperationException("Convex hull result can not be modified");
        }

        @Override
        public void removeVertex(net.sci.geom.mesh3d.Mesh3D.Vertex vertex)
        {
            throw new UnsupportedOperationException("Convex hull result can not be modified");
        }

        @Override
        public void removeFace(net.sci.geom.mesh3d.Mesh3D.Face face)
        {
            throw new UnsupportedOperationException("Convex hull result can not be modified");
        }

        /**
         * Returns the polygonal domain corresponding to a given face.
         * 
         * @param face
         *            the face to convert
         * @return the polygon corresponding to the face
         */
        public Polygon3D facePolygon(Mesh3D.Face face)
        {
            int[] inds = ((Face) face).vertexIndices();
            List<Point3D> pts = IntStream.of(inds)
                    .mapToObj(i -> vertices[vertexPointIndices[i]].position)
                    .toList();
            return new DefaultPolygon3D(pts);
        }
        

        // ===================================================================
        // Methods implementing the Geometry3D interface
        
        @Override
        public boolean contains(Point3D point, double eps)
        {
            return faces.stream().anyMatch(f -> facePolygon(f).contains(point, eps));
        }


        @Override
        public double distance(double x, double y, double z)
        {
            return faces.stream()
                    .mapToDouble(f -> facePolygon(f).distance(x, y, z))
                    .min()
                    .orElse(Double.NaN);
        }


        @Override
        public Bounds3D bounds()
        {
            return this.bounds;
        }

        @Override
        public boolean isBounded()
        {
            return true;
        }

        @Override
        public Mesh3D duplicate()
        {
            // create new empty graph
            SimplePolygonalMesh3D dup = new SimplePolygonalMesh3D(vertexCount(), faceCount());
            
            // copy vertices, keeping mapping between old and new references
            Map<Integer, Mesh3D.Vertex> vertexMap = new HashMap<>();
            for (int iv : vertexPointIndices)
            {
                Vertex v = vertices[vertexPointIndices[iv]];
                Mesh3D.Vertex v2 = dup.addVertex(v.position());
                vertexMap.put(iv, v2);
            }
            
            // copy edges using vertex mapping
            for (Face face : faces)
            {
                int[] inds = face.vertexIndices();
                Mesh3D.Vertex[] faceVertices = new Mesh3D.Vertex[inds.length];
                for (int fv = 0; fv < inds.length; fv++)
                {
                    faceVertices[fv] = vertexMap.get(inds[fv]);
                }
                dup.addFace(faceVertices);
            }
            
            // return graph
            return dup;
        }


        /**
         * A vertex of the hull, part of a {@code VertexList}. Contains the
         * position of the vertex, references to the adjacent elements within
         * the list, and references to the incident elements (faces and
         * half-edges).
         */
        static class Vertex implements Mesh3D.Vertex
        {
            /**
             * Computes the cross-product of the vectors formed by the position of vertices 1 and 2 on one side, and vertices 1 and 3 on the other side.
             * @param v1 the first vertex
             * @param v2 the second vertex
             * @param v3 the third vertex
             * @return the cross product obtained from vertex positions.
             */
            public static final Vector3D crossProduct(Vertex v1, Vertex v2, Vertex v3)
            {
                return Vector3D.crossProduct(v1.position, v2.position, v3.position);
            }
            
            /**
             * The 3D position of the vertex.
             */
            Point3D position;
            
            /**
             * The index of the vertex into the vertex list of the mesh.
             */
            int index;
            
            /**
             * The previous vertex within the list.
             * @see VertexList
             */
            Vertex prev;
            
            /**
             * The next vertex within the list.
             * @see VertexList
             */
            Vertex next;
            
            /**
             * The face associated to this vertex. Corresponds to the face pointing
             * towards an outside vertex. If vertex is within the hull, should be null.
             */
            Face face;
            
            
            /**
             * Creates a new vertex located at the origin.
             */
            public Vertex()
            {
                this.position = new Point3D();
            }
            
            /**
             * Creates a new vertex, specifying its position and index.
             */
            public Vertex(double vx, double vy, double vz, int index)
            {
                this.position = new Point3D(vx, vy, vz);
                this.index = index;
            }
            
            /**
             * Creates a new vertex, specifying its position and index.
             */
            public Vertex(Point3D position, int index)
            {
                this.position = position;
                this.index = index;
            }
            
            public Point3D position()
            {
                return this.position;
            }

            @Override
            public Vector3D normal()
            {
                throw new UnsupportedOperationException("Unimplemented operation");
            }
        } // end of Vertex class
        
        /**
         * A triangular face within a mesh.
         * 
         * Each face keeps reference to a doubly-linked list of three half-edges, and
         * stores its index with respect to the list of mesh faces.
         * 
         * It also encapsulates geometric information: the centroid, the normal, the
         * area, the plane offset.
         */
        class Face implements Mesh3D.Face
        {
            // ===================================================================
            // Class variables

            /**
             * The first half-edge of the doubly linked list.
             */
            HalfEdge edge0;

            /**
             * The index of the face within the global array. Setup at the end of the
             * hull building process.
             */
            int index;
            
            /**
             * The number of vertices of the face. Used for check-ups, and for
             * computation of face vertex indices.
             */
            public int vertexCount;
            
            private Point3D centroid;
            double area;
            
            private Vector3D normal;
            double planeOffset;
            
            
            /**
             * A pointer to the next Face within the list.
             * 
             * @see FaceList
             */
            Face next;

            /**
             * Enumeration for face "State"
             */
            public static enum State
            {
                /** Standard state of a face*/
                VISIBLE,
                /** The face is within a concavity, and will be merged */
                NON_CONVEX,
                /** The face is within the cone of an eye-vertex, and will be deleted */
                DELETED;
            }
            
            /**
             * A state associated to the face
             */
            State state = State.VISIBLE;
            
            /**
             * A reference to the first outside vertex associated to this face. 
             */
            Vertex firstClaimed;
            
            
            // ===================================================================
            // Constructors

            public Face()
            {
                normal = new Vector3D();
                centroid = new Point3D();
                state = State.VISIBLE;
            }
            
            
            // ===================================================================
            // management of points associated to the face

            /**
             * Removes the specified vertex from the list of vertices associated to this
             * face. It is assumed this is the first vertex of the list of vertices
             * associated to face.
             * 
             * @param vertex
             *            the vertex to remove
             */
            public void removePoint(Vertex vertex)
            {
                // case of vertex corresponding to first vertex in the list
                if (vertex == this.firstClaimed)
                {
                    // update the reference to vertex list
                    if (vertex.next != null && vertex.next.face == this)
                    {
                        this.firstClaimed = vertex.next;
                    }
                    else
                    {
                        this.firstClaimed = null;
                    }
                }
            }
            
            /**
             * Sets the index of all vertices of this face to zero. Used to mark
             * vertices as visible.
             */
            void setVertexIndicesToZero()
            {
                HalfEdge edge = edge0;
                do
                {
                    edge.head().index = 0;
                    edge = edge.next;
                } while (edge != edge0);
            }

            /**
             * For finalization.
             * @return the array of index for each vertex.
             */
            public int[] vertexIndices()
            {
                int[] indices = new int[vertexCount()];
                
                HalfEdge edge = this.edge0;
                int k = 0;
                do
                {
                    indices[k++] = edge.head().index;
                    edge = edge.next;
                } while (edge != this.edge0);
                
                return indices;
            }

            // ===================================================================
            // Computation methods

            /**
             * Computes the distance between the specified point and the supporting
             * plane of this face.
             *
             * @param p
             *            the query point
             * @return the distance from the point to the plane
             */
            public double distanceToPlane(Point3D p)
            {
                return normal.x() * p.x() + normal.y() * p.y() + normal.z() * p.z() - planeOffset;
            }

            
            // ===================================================================
            // Data access methods

            /**
             * Gets the i-th half-edge associated with the face.
             * 
             * @param i
             *            the half-edge index, in the range 0-2 (can be negative in some calls, however...)
             * @return the half-edge
             */
            public HalfEdge getEdge(int i)
            {
                HalfEdge he = edge0;
                while (i > 0)
                {
                    he = he.next;
                    i--;
                }
                while (i < 0)
                {
                    he = he.prev;
                    i++;
                }
                return he;
            }
            
            public HalfEdge firstEdge()
            {
                return edge0;
            }

            public int vertexCount()
            {
                return vertexCount;
            }

            public Point3D centroid()
            {
                return centroid;
            }
            

            // ===================================================================
            // Computation methods (at initialization)
            
            private void computeGeometricData()
            {
                computeNormal();
                this.centroid = computeCentroid();
                planeOffset = normal.dotProduct(Vector3D.of(centroid));
                
                // count number of vertices
                int numv = 0;
                HalfEdge he = edge0;
                do
                {
                    numv++;
                    he = he.next;
                } while (he != edge0);
                
                // check number of edges matches number of vertices
                if (numv != vertexCount)
                {
                    throw new RuntimeException(
                            "face " + toString() + " numVerts=" + vertexCount + " should be " + numv);
                }
            }

            private Point3D computeCentroid()
            {
                Point3D centroid = new Point3D();
                int nv = 0;
                HalfEdge edge = this.edge0;
                do
                {
                    centroid = centroid.plus(edge.head().position);
                    edge = edge.next;
                    nv++;
                } while (edge != edge0);
                
                return centroid.divideBy(nv);
            }
            
            private void computeNormal()
            {
                // Initial pair of consecutive edges
                HalfEdge edge1 = edge0.next;
                HalfEdge edge2 = edge1.next;

                // extremity points of first edge
                Point3D p0 = edge0.head().position;
                Point3D p2 = edge1.head().position;

                // initialize normal coordinates
                normal = new Vector3D(0, 0, 0);

                // iterate over pairs of edges around face
                vertexCount = 2;
                while (edge2 != edge0)
                { 
                    // update pair of extremity points
                    Point3D p1 = p2;
                    p2 = edge2.head().position;
                    normal = normal.plus(Vector3D.crossProduct(p0, p1, p2));

                    // iterate to next edge
                    edge1 = edge2;
                    edge2 = edge2.next;
                    vertexCount++;
                }

                area = normal.norm();
                normal = normal.times(1.0 / area);
            }

            public int mergeAdjacentFace(HalfEdge adjEdge, Face[] discarded)
            {
                Face twinFace = adjEdge.twinFace();
                int numDiscarded = 0;

                discarded[numDiscarded++] = twinFace;
                twinFace.state = State.DELETED;

                HalfEdge twinEdge = adjEdge.twin();

                HalfEdge prevAdjEdge = adjEdge.prev;
                HalfEdge nextAdjEdge = adjEdge.next;
                HalfEdge prevTwinEdge = twinEdge.prev;
                HalfEdge nextTwinEdge = twinEdge.next;

                while (prevAdjEdge.twinFace() == twinFace)
                {
                    prevAdjEdge = prevAdjEdge.prev;
                    nextTwinEdge = nextTwinEdge.next;
                }

                while (nextAdjEdge.twinFace() == twinFace)
                {
                    prevTwinEdge = prevTwinEdge.prev;
                    nextAdjEdge = nextAdjEdge.next;
                }

                // associate this face to each edge within the list
                HalfEdge hedge;
                for (hedge = nextTwinEdge; hedge != prevTwinEdge.next; hedge = hedge.next)
                {
                    hedge.face = this;
                }

                if (adjEdge == edge0)
                {
                    edge0 = nextAdjEdge;
                }

                Face discardedFace;

                // handle the half edges at the head
                discardedFace = connectHalfEdges(prevTwinEdge, nextAdjEdge);
                if (discardedFace != null)
                {
                    discarded[numDiscarded++] = discardedFace;
                }

                // handle the half edges at the tail
                discardedFace = connectHalfEdges(prevAdjEdge, nextTwinEdge);
                if (discardedFace != null)
                {
                    discarded[numDiscarded++] = discardedFace;
                }

                computeGeometricData();
                checkConsistency();

                return numDiscarded;
            }

            private Face connectHalfEdges(HalfEdge hedgePrev, HalfEdge hedge)
            {
                Face discardedFace = null;

                if (hedgePrev.twinFace() == hedge.twinFace())
                { // then there is a redundant edge that we can get rid off

                    Face oppFace = hedge.twinFace();
                    HalfEdge hedgeOpp;

                    if (hedgePrev == edge0)
                    {
                        edge0 = hedge;
                    }
                    
                    if (oppFace.vertexCount() == 3)
                    { // then we can get rid of the opposite face altogether
                        hedgeOpp = hedge.twin().prev.twin();

                        oppFace.state = State.DELETED;
                        discardedFace = oppFace;
                    }
                    else
                    {
                        hedgeOpp = hedge.twin().next;

                        if (oppFace.edge0 == hedgeOpp.prev)
                        {
                            oppFace.edge0 = hedgeOpp;
                        }
                        hedgeOpp.prev = hedgeOpp.prev.prev;
                        hedgeOpp.prev.next = hedgeOpp;
                    }
                    hedge.prev = hedgePrev.prev;
                    hedge.prev.next = hedge;

                    hedge.twin = hedgeOpp;
                    hedgeOpp.twin = hedge;

                    // oppFace was modified, so need to recompute
                    oppFace.computeGeometricData();
                }
                else
                {
                    hedgePrev.next = hedge;
                    hedge.prev = hedgePrev;
                }
                return discardedFace;
            }
            
            public boolean isVisible()
            {
                return this.state == State.VISIBLE;
            }
            

            void checkConsistency()
            {
                // perform a sanity check on the face
                HalfEdge edge = edge0;
                double maxd = 0;
                int numv = 0;

                if (vertexCount < 3)
                {
                    throw new RuntimeException("degenerate face: " + toString());
                }
                
                do
                {
                    HalfEdge opEdge = edge.twin();
                    if (opEdge == null)
                    {
                        throw new RuntimeException("face " + toString() + ": " + "unreflected half edge " + edge);
                    }
                    else if (opEdge.twin() != edge)
                    {
                        throw new RuntimeException("face " + toString() + ": " + "opposite half edge " + opEdge + " has opposite " + opEdge.twin());
                    }
                    if (opEdge.head() != edge.tail() || edge.head() != opEdge.tail())
                    {
                        throw new RuntimeException("face " + toString() + ": " + "half edge " + edge + " reflected by " + opEdge);
                    }
                    
                    Face opFace = opEdge.face;
                    if (opFace == null)
                    {
                        throw new RuntimeException("face " + toString() + ": " + "no face on half edge " + opEdge);
                    }
                    else if (opFace.state == State.DELETED)
                    {
                        throw new RuntimeException("face " + toString() + ": " + "opposite face " + opFace.toString() + " not on hull");
                    }
                    double d = Math.abs(distanceToPlane(edge.head().position));
                    if (d > maxd)
                    {
                        maxd = d;
                    }
                    numv++;
                    edge = edge.next;
                } while (edge != edge0);

                if (numv != vertexCount)
                {
                    throw new RuntimeException("face " + toString() + " numVerts=" + vertexCount + " should be " + numv);
                }

            }

            @Override
            public String toString()
            {
                return vertexListString();
            }
            
            /**
             * Builds a string summarizing vertex indices around this face.
             * 
             * @return a summary string summarizing vertex indices around this face.
             */
            public String vertexListString()
            {
                StringBuilder sb = new StringBuilder();
                HalfEdge he = edge0;
                do
                {
                    if (sb.isEmpty())
                    {
                        sb.append(he.head().index);
                    }
                    else
                    {
                        sb.append(" ").append(he.head().index);
                    }
                    he = he.next;
                } while (he != edge0);
                
                return sb.toString();
            }

            @Override
            public Polygon3D polygon()
            {
                Point3D[] verts = new Point3D[this.vertexCount];
                int iv = 0;
                
                HalfEdge edge = this.edge0;
                do
                {
                    verts[iv++] = edge.head().position;
                    edge = edge.next;
                } while (edge != this.edge0);
                
                return Polygon3D.create(verts);
            }

            @Override
            public Vector3D normal()
            {
                return this.normal;
            }

            @Override
            public Iterable<Mesh3D.Vertex> vertices()
            {
                ArrayList<Mesh3D.Vertex> verts = new ArrayList<>(this.vertexCount);
                
                HalfEdge edge = this.edge0;
                do
                {
                    verts.add(edge.head());
                    edge = edge.next;
                } while (edge != this.edge0);
                
                return verts;
            }
        } // end of Face class
        
        /**
         * An Half-Edge, that borders a face in counter-clockwise orientation.
         * 
         * An Half-Edge contains several information:
         * <ul>
         * <li>the vertex corresponding to the head of the edge.</li>
         * <li>the face associated to the left side of the edge.</li>
         * <li>the next and the previous half-edges that border the same face.</li>
         * <li>the "twin" half-edge, that shares the same extremity vertices but in
         * opposite direction. The twin half-edge borders the opposite face.</li>
         * </ul>
         */
        static class HalfEdge
        {
            /**
             * Sets the 'twin' of each edge to be the other specified one.
             * 
             * @param edge1
             *            the first half-edge
             * @param edge2
             *            the second half-edge
             */
            public static final void setAsTwin(HalfEdge edge1, HalfEdge edge2)
            {
                edge1.twin = edge2;
                edge2.twin = edge1;
            }
            
            /**
             * The vertex corresponding to the head of this half-edge.
             */
            Vertex head;
            
            /**
             * The triangular face associated with this half-edge.
             */
            Face face;

            /**
             * The next half-edge that borders the same face as this half-edge. 
             */
            HalfEdge next;

            /**
             * The previous half-edge that borders the same face as this half-edge.
             */
            HalfEdge prev;

            /**
             * The twin half-edge, opposite to this one.
             */
            HalfEdge twin;
            
            
            /**
             * Creates a new half-edge bounding the face <code>f</code>, and using the
             * vertex <code>v</code> as head vertex.
             * 
             * @param v
             *            the vertex associated to the head of the half-edge
             * @param f
             *            the (left-hand) face associated with this half-edge
             */
            public HalfEdge(Vertex v, Face f)
            {
                this.head = v;
                this.face = f;
            }
            
            /**
             * Returns the face associated to this half-edge.
             * 
             * @return the face associated to this half-edge.
             */
            public Face face()
            {
                return face;
            }
            
            /**
             * Returns the half-edge twin to this half-edge.
             *
             * @return the half-edge twin to this one.
             */
            public HalfEdge twin()
            {
                return twin;
            }
            
            /**
             * Returns the head vertex of this half-edge.
             * 
             * @return the head vertex.
             */
            public Vertex head()
            {
                return head;
            }
            
            /**
             * Returns the tail vertex of this half-edge, or null if the previous edge
             * is not defined.
             * 
             * @return the tail vertex of this half-edge, or null.
             */
            public Vertex tail()
            {
                return prev != null ? prev.head : null;
            }
            
            /**
             * Returns the face associated to the twin half-edge.
             * 
             * @return the face associated to the twin half-edge, or null if it is not
             *         defined.
             */
            public Face twinFace()
            {
                return twin != null ? twin.face : null;
            }
            
            /**
             * Returns the length of this edge, or NaN if the tail vertex is not
             * defined.
             * 
             * @return the length of this edge
             */
            public double length()
            {
                return tail() != null ? head().position.distance(tail().position) : Double.NaN;
            }
            
            /**
             * Returns the square of the length of this edge, or NaN if the tail is
             * null.
             * 
             * @return the square of the length of this edge, or NaN if the tail is
             *         null.
             */
            public double squaredLength()
            {
                return tail() != null ? Point3D.squaredDistance(head().position, tail().position) : Double.NaN;
            }
            
            @Override
            public String toString()
            {
                if (tail() != null)
                {
                    return String.format("%d-%d", tail().index, head().index);
                }
                else
                {
                    return String.format("?-%d", head().index);
                }
            }
        } // end of Half-egde  class
        
   } // end of Mesh class
    
    /**
     * A doubly linked list of vertices, used by the QuickHull3D class.
     * 
     * @see QuickHull3D
     */
    class VertexList
    {
        private Vertex head;
        private Vertex tail;
        
        /**
         * Clears this list.
         */
        public void clear()
        {
            head = null;
            tail = null;
        }
        
        /**
         * Adds a vertex at the end of this list.
         * 
         * @param v
         *            the vertex to add
         */
        public void add(Vertex v)
        {
            if (head == null)
            {
                head = v;
            }
            else
            {
                tail.next = v;
            }
            
            v.prev = tail;
            v.next = null;
            tail = v;
        }
        
        /**
         * Adds a chain of vertices to the end of this list.
         * 
         * @param v the first vertex of the chain
         */
        public void addAll(Vertex v)
        {
            if (head == null)
            {
                head = v;
            }
            else
            {
                tail.next = v;
            }
            
            v.prev = tail;
            while (v.next != null)
            {
                v = v.next;
            }
            tail = v;
        }

        /**
         * Removes the specified vertex from the list.
         * 
         * @param v
         *            the vertex to remove;
         */
        public void delete(Vertex v)
        {
            // update link of previous vertex
            if (v.prev == null)
            {
                head = v.next;
            }
            else
            {
                v.prev.next = v.next;
            }
            
            // update link of next vertex
            if (v.next == null)
            {
                tail = v.prev;
            }
            else
            {
                v.next.prev = v.prev;
            }
        }
        
        /**
         * Deletes a chain of vertices from this list.
         * 
         * @param v1
         *            the first vertex to delete
         * @param v2
         *            the last vertex to delete
         */
        public void delete(Vertex v1, Vertex v2)
        {
            if (v1.prev == null)
            {
                head = v2.next;
            }
            else
            {
                v1.prev.next = v2.next;
            }
            
            if (v2.next == null)
            {
                tail = v1.prev;
            }
            else
            {
                v2.next.prev = v1.prev;
            }
        }

        /**
         * Inserts a vertex into this list before the specified vertex.
         * 
         * @param v the vertex to insert
         * @param next the vertex that will be placed next to the vertex v after the insertion
         * 
         */
        public void insertBefore(Vertex v, Vertex next)
        {
            v.prev = next.prev;
            if (next.prev == null)
            {
                head = v;
            }
            else
            {
                next.prev.next = v;
            }
            v.next = next;
            next.prev = v;
        }

        /**
         * Returns the first element within this list.
         * 
         * @return the first element within this list.
         */
        public Vertex first()
        {
            return this.head;
        }
        
        /**
         * Checks if this list is empty.
         * 
         * @return <code>true</code> if this list is empty.
         */
        public boolean isEmpty()
        {
            return head == null;
        }
    } // end of VertexList class
}
