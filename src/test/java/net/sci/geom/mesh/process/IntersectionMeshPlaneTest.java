/**
 * 
 */
package net.sci.geom.mesh.process;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Collection;

import org.junit.Test;

import net.sci.geom.geom3d.Plane3D;
import net.sci.geom.geom3d.Point3D;
import net.sci.geom.geom3d.Vector3D;
import net.sci.geom.geom3d.polyline.Polyline3D;
import net.sci.geom.mesh.Meshes3D;
import net.sci.geom.mesh.TriMesh3D;

/**
 * @author dlegland
 *
 */
public class IntersectionMeshPlaneTest
{
    /**
     * Test method for {@link net.sci.geom.mesh.process.IntersectionMeshPlane#intersectionMeshPlane(net.sci.geom.mesh.TriMesh3D, net.sci.geom.geom3d.Plane3D)}.
     */
    @Test
    public final void testIntersectionMeshPlane_octahedron_xyPlane_z05()
    {
        TriMesh3D mesh = Meshes3D.createOctahedron();
        Plane3D plane = new Plane3D(new Point3D(0, 0, 0.5), new Vector3D(0, 0, 1));
        
        Collection<Polyline3D> curves = IntersectionMeshPlane.intersectionMeshPlane(mesh, plane);
        
        assertNotNull(curves);
        assertEquals(1, curves.size());
        
        Polyline3D ring1 = curves.iterator().next();
        assertEquals(4, ring1.vertexCount());
    }

    /**
     * Test method for {@link net.sci.geom.mesh.process.IntersectionMeshPlane#intersectionMeshPlane(net.sci.geom.mesh.TriMesh3D, net.sci.geom.geom3d.Plane3D)}.
     */
    @Test
    public final void testIntersectionMeshPlane_octahedron_yzPlane_x05()
    {
        TriMesh3D mesh = Meshes3D.createOctahedron();
        Plane3D plane = new Plane3D(new Point3D(0.5, 0, 0), new Vector3D(1, 0, 0));
        
        Collection<Polyline3D> curves = IntersectionMeshPlane.intersectionMeshPlane(mesh, plane);
        
        assertNotNull(curves);
        assertEquals(1, curves.size());
        
        Polyline3D ring1 = curves.iterator().next();
        assertEquals(4, ring1.vertexCount());
    }
}
