/**
 * 
 */
package net.sci.image.morphology.reconstruct;

import java.util.ArrayDeque;
import java.util.ArrayList;
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
    /** The connectivity to use for reconstruction */
    int conn = 6;
    
    /**
     * Creates a new reconstruction algorithm with default connectivity equal to 6.
     */
    public RunLengthBinaryReconstruction3D()
    {
    }
    
    /**
     * Creates a new reconstruction algorithm with the specified connectivity
     * option.
     * 
     * @param conn
     *            the integer code for the connectivity, that should be either 6
     *            or 26.
     */
    public RunLengthBinaryReconstruction3D(int conn)
    {
        if (conn != 6 && conn != 26)
        {
            throw new RuntimeException("Connectivty must be either 6 or 26");
        }
        this.conn = conn;
    }
    
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
        // Shifts contain shifts in the y and z coordinates, as well as two
        // shifts for the left and end extremities of the current run
        Collection<RowShift> rowShifts = conn == 6 ? getRowShiftsC6() : getRowShiftsC26();
        
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
                    row = BinaryRow.intersection(row, row2);
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
                for (RowShift shift : rowShifts)
                {
                    // index of neighbor row
                    int yn = y + shift.dy;
                    if (yn < 0 || yn > sizeY - 1) continue;
                    int zn = z + shift.dz;
                    if (zn < 0 || zn > sizeZ - 1) continue;
                    
                    // retrieve neighbor row within array
                    BinaryRow neighRow = mask2.getRow(yn, zn);
                    if (neighRow == null)
                    {
                        // nothing to reconstruct
                        continue;
                    }
                    
                    // dilate current run by the required amount of pixels in each direction
                    Run run2 = new Run(run.left + shift.xneg, run.right + shift.xpos);

                    // find the runs within arrayRow that intersect the current run
                    for (Run neighRun : neighRow.intersectingRuns(run2))
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
    
    private static Collection<RowShift> getRowShiftsC6()
    {
        ArrayList<RowShift> shifts = new ArrayList<RowShift>(4);
        shifts.add(new RowShift( 0, -1, 0, 0));
        shifts.add(new RowShift(-1,  0, 0, 0));
        shifts.add(new RowShift(+1,  0, 0, 0));
        shifts.add(new RowShift( 0, +1, 0, 0));
        return shifts;
    }
    
    private static Collection<RowShift> getRowShiftsC26()
    {
        ArrayList<RowShift> shifts = new ArrayList<RowShift>(8);
        shifts.add(new RowShift(-1, -1, -1, +1));
        shifts.add(new RowShift( 0, -1, -1, +1));
        shifts.add(new RowShift(+1, -1, -1, +1));
        shifts.add(new RowShift(-1,  0, -1, +1));
        shifts.add(new RowShift(+1,  0, -1, +1));
        shifts.add(new RowShift(-1, +1, -1, +1));
        shifts.add(new RowShift( 0, +1, -1, +1));
        shifts.add(new RowShift(+1, +1, -1, +1));
        return shifts;
    }

    private static class RowShift
    {
        int dy;
        int dz;
        
        int xneg;
        int xpos;
        
        public RowShift(int dy, int dz, int xneg, int xpos)
        {
            this.dy = dy;
            this.dz = dz;
            this.xneg = xneg;
            this.xpos = xpos;
        }
    }
}
