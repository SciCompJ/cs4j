/**
 * 
 */
package net.sci.image.morphology.reconstruction;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;

import net.sci.array.binary.BinaryArray2D;
import net.sci.array.binary.BinaryRow;
import net.sci.array.binary.Run;
import net.sci.array.binary.RunLengthBinaryArray2D;

/**
 * Morphological reconstruction for binary arrays using run-length encoding of
 * output.
 * 
 * @author dlegland
 *
 */
public class RunLengthBinaryReconstruction2D
{
    /** The connectivity to use for reconstruction */
    int conn = 4;
    
    /**
     * Creates a new reconstruction algorithm with default connectivity equal to 4.
     */
    public RunLengthBinaryReconstruction2D()
    {
    }
    
    /**
     * Creates a new reconstruction algorithm with the specified connectivity
     * option.
     * 
     * @param conn
     *            the integer code for the connectivity, that should be either 4
     *            or 8.
     */
    public RunLengthBinaryReconstruction2D(int connectivity)
    {
        if (connectivity != 4 && connectivity != 8)
        {
            throw new IllegalArgumentException("Connectivty option must be either 4 or 8");
        }
        this.conn = connectivity;
    }
    
    public BinaryArray2D processBinary2d(BinaryArray2D marker, BinaryArray2D mask)
    {
        // check input sizes, based on the size of the mask
        int sizeX = mask.size(0);
        int sizeY = mask.size(1);
        if (marker.size(0) != sizeX || marker.size(1) != sizeY)
        {
            throw new RuntimeException("Input arrays must have the same size");
        }
        
        // a list of shifts for the y-shift of neighbor rows. 
        // in 2D, no ambiguity, but could depend on connectivity for higher dimensions
        final int[] neighborRowShifts = new int[] {-1, +1};
        
        // Ensure both input arrays are run-length encoded
        RunLengthBinaryArray2D marker2 = RunLengthBinaryArray2D.convert(marker);
        RunLengthBinaryArray2D mask2 = RunLengthBinaryArray2D.convert(mask);
        
        // initialize empty result
        RunLengthBinaryArray2D result = new RunLengthBinaryArray2D(sizeX, sizeY);
        
        // the queue containing "markers" for reconstruction.
        // Markers are expected to be completely included within runs (they can
        // correspond to a single run).
        Deque<RunHandle> markers = new ArrayDeque<RunHandle>();
        
        // populate the queue with runs within the marker
        for (int y : marker2.nonEmptyRowIndices())
        {
            BinaryRow row = marker2.getRow(y);
            
            // combine with mask
            BinaryRow row2 = mask2.getRow(y);
            if (row2 != null)
            {
                row = BinaryRow.intersection(row, row2);
                for (Run run : row.runs())
                {
                    markers.add(new RunHandle(run, y));
                }
            }
        }

        // process the queue of marker
        while(!markers.isEmpty())
        {
            // retrieve next marker
            RunHandle rh = markers.removeFirst();
            Run markerRun = rh.run;
            int y = rh.y;
            
            // retrieve result row at index y, making sure it is not null
            BinaryRow row = result.getRow(y);
            row = row == null ? new BinaryRow() : row;
            
            // check if result already contains the current marker 
            if (row.containsRange(markerRun.left, markerRun.right))
            {
                continue;
            }
            
            // we will update the row at index y within the result, based on
            // the corresponding row within the mask
            BinaryRow maskRow = mask2.getRow(y);
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
                
                // update row according to current run
                row.setRange(run.left, run.right, true);
                
                // compute intersection with neighbor rows
                for (int dy : neighborRowShifts)
                {
                    // index of neighbor row
                    int yn = y + dy;
                    if (yn < 0 || yn > sizeY - 1) continue;
                        
                    // retrieve neighbor row within array
                    BinaryRow neighRow = mask2.getRow(yn);
                    if (neighRow == null)
                    {
                        // nothing to reconstruct
                        continue;
                    }
                    
                    // in case of 8-connectivity, add dilation of current run to touch by diagonal
                    if (conn == 8)
                    {
                        run = new Run(run.left - 1, run.right + 1);
                    }

                    // find the runs within arrayRow that intersect the current run
                    for (Run neighRun : neighRow.intersectingRuns(run))
                    {
                        BinaryRow row2 = result.getRow(yn);
                        if (row2 != null)
                        {
                            if (row2.containsRange(neighRun.left, neighRun.right))
                            {
                                continue;
                            }
                        }
                        
                        markers.add(new RunHandle(neighRun, yn));
                    }
                }
            }
            
            result.setRow(y, row);
        }
        
        return result;
    }
    
    /**
     * Encapsulates a run together with the y-index of the row.
     */
    class RunHandle
    {
        Run run;
        int y;
        
        public RunHandle(Run run, int y)
        {
            this.run = run;
            this.y = y;
        }
        
        @Override
        public String toString()
        {
            return "RunHandle(" + y + ", " + run + ")";
        }
    }
}
