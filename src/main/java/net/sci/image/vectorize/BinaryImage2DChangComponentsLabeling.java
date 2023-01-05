/**
 * 
 */
package net.sci.image.vectorize;

import java.util.ArrayList;
import java.util.Collection;

import jdk.incubator.vector.VectorOperators.Binary;
import net.sci.algo.AlgoStub;
import net.sci.array.Array;
import net.sci.array.binary.BinaryArray;
import net.sci.array.binary.BinaryArray2D;
import net.sci.array.numeric.Int32Array2D;
import net.sci.array.numeric.IntArray;
import net.sci.geom.geom2d.Point2D;
import net.sci.geom.polygon2d.LinearRing2D;
import net.sci.geom.polygon2d.Polyline2D;

/**
 * Performs a combined connected-component labeling and a contour tracing as
 * described by Chang et al (2004) [1]. See also Sec. 8.2.2 of [2] for
 * additional details.
 * 
 * <p>
 * [1] F. Chang, C. J. Chen, and C. J. Lu. A linear-time component labeling
 * algorithm using contour tracing technique. Computer Vision, Graphics, and
 * Image Processing: Image Understanding 93(2), 206-220 (2004). <br>
 * [2] W. Burger, M.J. Burge, <em>Digital Image Processing &dash; An
 * Algorithmic Introduction</em>, 3rd ed, Springer (2022).
 * </p>
 *
 * 
 * @author dlegland
 *
 */
public class BinaryImage2DChangComponentsLabeling extends AlgoStub
{
    /**
     * Default empty constructor.
     */
    public BinaryImage2DChangComponentsLabeling()
    {
    }
    
    // =============================================================
    // Main processing method
    
    /**
     * Processes the input binary array, the label map resulting from the
     * connected components labeling algorithm.
     * 
     * @param array
     *            the binary array to process.
     * @return the label map corresponding to the connected components labeling.
     * @throws RuntimeException
     *             if the input array does not contain Binary elements, or is
     *             not 2D
     */
    public IntArray<?> process(Array<?> array)
    {
        if (array.dimensionality() != 2) throw new RuntimeException("Requires a 2D array as input");
        if (array.elementClass() != Binary.class) throw new RuntimeException("Requires an array containin Binary elements as input");
        
        return processBinary2d(BinaryArray2D.wrap(BinaryArray.wrap(array)));
    }

    /**
     * Processes the input binary array, the label map resulting from the
     * connected components labeling algorithm.
     * 
     * @param array
     *            the binary array to process.
     * @return the label map corresponding to the connected components labeling.
     */
    public IntArray<?> processBinary2d(BinaryArray2D array)
    {
        return new Worker(array).run().labelMap;
    }
    
    /**
     * Processes the input binary array, and returns a <code>Result</code>
     * instance that contains both the result of connected components labeling
     * and the contours of the regions.
     * 
     * @param array
     *            the binary array to process.
     * @return a <code>Result</code> instance that contains both the result of
     *         connected components labeling and the contours of the regions.
     */
    public Result getResult(BinaryArray2D array)
    {
        return new Worker(array).run();
    }
    
    // =============================================================
    // Inner working  class
    
    /**
     * Inner class that performs computation on a given binary image.
     */
    private class Worker
    {
        BinaryArray2D array;
        Int32Array2D labelMap;
        int labelCount = 0;
        
        Collection<Polyline2D> outerContours = new ArrayList<>(1);
        Collection<Polyline2D> innerContours = new ArrayList<>(1);
        
        public Worker(BinaryArray2D array)
        {
            this.array = array;
        }
        
        public Result run()
        {
            int sizeX = array.size(0);
            int sizeY = array.size(1);
            
            this.labelMap = Int32Array2D.create(sizeX, sizeY);
            
            // the label of the current region (may be 0)
            int currentLabel;
            int maxPossibleLabel = Integer.MAX_VALUE;
            
            // iterate over all elements of binary array
            for (int y = 0; y < sizeY; y++)
            {
                // reset current label
                currentLabel = 0;
                
                for (int x = 0; x < sizeX; x++)
                {
                    if (array.getBoolean(x, y))
                    {
                        // case of foreground pixels
                        if (currentLabel != 0)
                        {
                            // propagate current label
                            this.labelMap.setInt(x, y, currentLabel);
                        }
                        else
                        {
                            // enters a new region, possibly not yet labeled
                            currentLabel = this.labelMap.getInt(x, y);
                            if (currentLabel == 0)
                            {
                                // creates a new label
                                if (this.labelCount == maxPossibleLabel)
                                {
                                    throw new RuntimeException("Reached the maximum number of labels.");
                                }
                                this.labelCount++;
                                currentLabel = this.labelCount;
                                
                                Cursor start = new Cursor(x, y, 0);
                                Polyline2D poly = traceContour(start, currentLabel);
                                this.outerContours.add(poly);
                                this.labelMap.setInt(x, y, currentLabel);
                            }
                        }
                    }
                    else
                    {
                        // case of background pixels
                        if (currentLabel != 0 && this.labelMap.getInt(x, y) == 0)
                        {
                            // initialize a new inner contour
                            Cursor start = new Cursor(x-1, y, 1);
                            Polyline2D poly = traceContour(start, currentLabel);
                            this.innerContours.add(poly);
                        }
                        currentLabel = 0;
                    }
                }
            }

            // cleanup label map (replaces negative values with 0)
            for (int y = 0; y < sizeY; y++)
            {
                for (int x = 0; x < sizeX; x++)
                {
                    if (labelMap.getInt(x, y) < 0)
                    {
                        labelMap.setInt(x, y, 0);
                    }
                }
            }

            return new Result(labelMap, labelCount, outerContours, innerContours);
        }
        
        private Polyline2D traceContour(Cursor start, int label)
        {
            Cursor init = findNextContourPoint(start);
            
            // initialize cursors of current contour
            ArrayList<Cursor>cursors = new ArrayList<>();
            cursors.add(start);
            
            // create a pair of consecutive contour cursors for starting iteration
            Cursor previous = start;
            Cursor current = init;
            
            // check case of isolated pixels
            boolean done = previous.samePosition(current);
            
            // iterate until coming back
            while (!done)
            {
                cursors.add(current);
                this.labelMap.setInt(current.x, current.y, label);
                Cursor next = findNextContourPoint(new Cursor(current.x, current.y, (current.d+6)%8));
                previous = current;
                current = next;
                done = previous.samePosition(start) && current.samePosition(init); 
            }
            cursors.removeLast();
            
            return createPolyline(cursors);
        }

        private Cursor findNextContourPoint(Cursor c)
        {
            // index of current direction
            int d = c.d;
            
            // search into the 7 first directions
            for (int i = 0; i < 7; i++)
            {
                // coordinate of neighbor
                int xn = c.x + dx[d];
                int yn = c.y + dy[d];
                
                if (array.getBoolean(xn, yn))
                {
                    // found a new foreground pixel
                    return new Cursor(xn, yn, d);
                }
                else
                {
                    this.labelMap.setInt(xn, yn, -1);
                    d = (d + 1) % 8;
                }
            }
            
            // if no foreground pixel was found, return start position (with a new direction ?)
            return new Cursor(c.x, c.y, d);
        }
        
        private static final int[] dx = new int[] {1, 1, 0, -1, -1, -1, 0, 1};
        private static final int[] dy = new int[] {0, 1, 1, 1, 0, -1, -1, -1};
        
        /**
         * Encapsulates a 2D position together with a direction index.
         */
        private class Cursor
        {
            int x;
            int y;
            int d;
            
            public Cursor(int x, int y, int d)
            {
                this.x = x;
                this.y = y;
                this.d = d;
            }
            
            public boolean samePosition(Cursor that)
            {
                if (this.x != that.x) return false;
                return this.y == that.y;
            }
        }

        private static final Polyline2D createPolyline(ArrayList<Cursor> cursors)
        {
            LinearRing2D ring = LinearRing2D.create(cursors.size());
            for (Cursor c : cursors)
            {
                ring.addVertex(new Point2D(c.x, c.y));
            }
            return ring;
        }
    }
    
    
    // =============================================================
    // Inner class for storing results
    
    public class Result
    {
        public Int32Array2D labelMap;
        public int labelCount = 0;
        
        public Collection<Polyline2D> outerContours = new ArrayList<>(1);
        public Collection<Polyline2D> innerContours = new ArrayList<>(1);
        
        private Result(Int32Array2D labelMap, int labelCount, Collection<Polyline2D> outerContours, Collection<Polyline2D> innerContours)
        {
            this.labelMap = labelMap;
            this.labelCount = labelCount;
            this.outerContours = outerContours;
            this.innerContours = innerContours;
        }
    }
}
