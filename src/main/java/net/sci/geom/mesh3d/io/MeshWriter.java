/**
 * 
 */
package net.sci.geom.mesh3d.io;

import java.io.IOException;

import net.sci.geom.mesh3d.Mesh3D;

/**
 * Interface for writing a 3D mesh into a file.
 * 
 * Typical expected usage:
 * <code><pre>
 * Mesh3D mesh = ...
 * File file = ...
 * MeshWriter writer = new XXXMeshWriter(file);
 * writer.writeMesh(mesh);
 * writer.close();
 * </pre></code>
 * 
 * @author dlegland
 *
 */
public interface MeshWriter
{
	/**
	 * Writes the content of a 3D mesh into the specified writer.
	 * 
	 * @param mesh
	 *            the mesh to write
	 * @throws IOException
	 *             if a problem occurred during mesh writing
	 */
	public void writeMesh(Mesh3D mesh) throws IOException;
}
