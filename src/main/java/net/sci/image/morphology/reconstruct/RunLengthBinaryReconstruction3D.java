/**
 * 
 */
package net.sci.image.morphology.reconstruct;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;

import net.sci.array.binary.BinaryArray3D;
import net.sci.array.binary.BinaryRow;
import net.sci.array.binary.Run;
import net.sci.array.binary.RunLengthBinaryArray3D;

/**
 * Morphological reconstruction for binary arrays using run-length encoding of
 * output.
 * 
 * @author dlegland
 *
 */
public class RunLengthBinaryReconstruction3D
{
    public BinaryArray3D processBinary3d(BinaryArray3D marker, BinaryArray3D mask)
    {
        // check input sizes, based on the size of the mask
        int sizeX = mask.size(0);
        int sizeY = mask.size(1);
        int sizeZ = mask.size(2);
        if (marker.size(0) != sizeX || marker.size(1) != sizeY || marker.size(2) != sizeZ)
        {
            throw new RuntimeException("Input arrays must have the same size");
        }
        
        // a list of (y,z)-shifts for the neighbor rows.
        // For each array, first index corresponds to y-shift, second index corresponds to z-shift.
        final int[][] neighborRowShifts = new int[][] {
            {-1, -1}, { 0, -1}, {+1, -1},
            {-1,  0},           {+1,  0},
            {-1, +1}, { 0, +1}, {+1, +1}
        };
        
        // Ensure both input arrays are run-length encoded
        RunLengthBinaryArray3D marker2 = RunLengthBinaryArray3D.convert(marker);
        RunLengthBinaryArray3D mask2 = RunLengthBinaryArray3D.convert(mask);
        
        // initialize empty result
        RunLengthBinaryArray3D result = new RunLengthBinaryArray3D(sizeX, sizeY, sizeZ);
        
        // the queue containing "markers" for reconstruction.
        // Markers are expected to be completely included within runs (they can
        // correspond to a single run).
        Deque<RunHandle3D> markers = new ArrayDeque<RunHandle3D>();
        
        // populate the queue with runs within the marker
        for (int z : marker2.nonEmptySliceIndices())
        {
            for (int y : marker2.nonEmptySliceRowIndices(z))
            {
                BinaryRow row = marker2.getRow(y, z);
                
                // combine with mask
                BinaryRow row2 = mask2.getRow(y, z);
                if (row2 != null)
                {
                    row = row.intersection(row2);
                    for (Run run : row.runs())
                    {
                        markers.add(new RunHandle3D(run, y, z));
                    }
                }
            }
               
        }

        // process the queue of marker
        while(!markers.isEmpty())
        {
            // retrieve next marker
            RunHandle3D rh = markers.removeFirst();
            Run markerRun = rh.run;
            int y = rh.y;
            int z = rh.z;
            
            // retrieve result row at index y, making sure it is not null
            BinaryRow row = result.getRow(y, z);
            row = row == null ? new BinaryRow() : row;
            
            // check if result already contains the current marker 
            if (row.containsRange(markerRun.left, markerRun.right))
            {
                continue;
            }
            
            // we will update the row at index y within the result, based on
            // the corresponding row within the mask
            BinaryRow maskRow = mask2.getRow(y, z);
            if (maskRow == null)
            {
                continue;
            }
            
            // find all the runs that intersect the current marker
            Collection<Run> runList = maskRow.intersectingRuns(markerRun);
            
            for (Run run : runList)
            {
                // do not process runs already within the result
                if (row.containsRange(run.left, run.right))
                {
                    continue;
                }
                
                row.setRange(run.left, run.right, true);
                
                // compute intersection with neighbor rows
                for (int[] shift : neighborRowShifts)
                {
                    // index of neighbor row
                    int yn = y + shift[0];
                    int zn = z + shift[1];
                    
                    // retrieve neighbor row within array
                    BinaryRow neighRow = mask2.getRow(yn, zn);
                    if (neighRow == null)
                    {
                        // nothing to reconstruct
                        continue;
                    }
                    
                    // TODO: should add dilation of current run for 26 connectivity

                    // find the runs within arrayRow that intersect the current run
                    for (Run neighRun : neighRow.intersectingRuns(run))
                    {
                        BinaryRow row2 = result.getRow(yn, zn);
                        if (row2 != null)
                        {
                            if (row2.containsRange(neighRun.left, neighRun.right))
                            {
                                continue;
                            }
                        }
                        
                        markers.add(new RunHandle3D(neighRun, yn, zn));
                    }
                }
            }
            
            result.setRow(y, z, row);
        }
        
        return result;
    }
    
    /**
     * Encapsulates a run together with the (y,z)-index of the row.
     */
    class RunHandle3D
    {
        Run run;
        int y;
        int z;
        
        public RunHandle3D(Run run, int y, int z)
        {
            this.run = run;
            this.y = y;
            this.z = z;
        }
        
        @Override
        public String toString()
        {
            return "RunHandle(" + y + ", " + z + ", " + run + ")";
        }
    }
}
