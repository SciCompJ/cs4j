package net.sci.image.morphology.watershed;

import java.util.ArrayList;
import java.util.PriorityQueue;

import net.sci.array.Arrays;
import net.sci.array.scalar.IntArray;
import net.sci.array.scalar.IntArray1D;
import net.sci.array.scalar.ScalarArray1D;

/**
 * Tentative implementation of Watershed algorithm for planar scalar images.
 * 
 * Reference: Fernand Meyer and Serge Beucher. "Morphological segmentation." 
 * Journal of visual communication and image representation 1.1 (1990): 21-46.
 * 
 * @author dlegland
 *
 */
public class MarkerBasedWatershed1D
{
    /** Value of the pixels belonging to the watershed */
    static final int WATERSHED = 0;
    
    /** Initial value of the labeled pixels */
    static final int INIT = -1;
    
    /** Value assigned to the pixels stored into the queue */
    static final int INQUEUE = -3;
    
    /** used to setup pixel timestamps. */
    long timeStamp = Long.MIN_VALUE;


    
    public IntArray1D<?> process(ScalarArray1D<?> array, IntArray1D<?> markers)
    {
        // extract array size
        int sizeX = array.size(0);
        
        if (!Arrays.isSameSize(array, markers))
        {
            throw new IllegalArgumentException("Marker and input arrays must have the same size");
        }
        
        // Initialize output array
        IntArray1D<?> labelArray = markers.duplicate();
        labelArray.fillValue(INIT);

        // initial queue of original pixels values and corresponding coordinates
        PriorityQueue<Pixel> queue = createQueue(array, markers, labelArray);
        
        // list to store neighbor labels
        ArrayList<Integer> labels = new ArrayList<Integer>();
        ArrayList<Pixel> neighbors = new ArrayList<Pixel>();
        
        // Process pixels and eventually add neighbors until the queue is empty 
        while (!queue.isEmpty())
        {
            if (Thread.currentThread().isInterrupted())
            {
                return null;
            }
            
            // reset state
            labels.clear();
            neighbors.clear();
            
            // get next record
            Pixel pixelRecord = queue.poll();

            // coordinates of current pixel
            int x = pixelRecord.x;

            // Iterate over neighbors of current pixel
            for (int x2 : new int[] {x - 1, x + 1})
            {
                // check neighbor is inside image
                if (x2 >= 0 && x2 < sizeX)
                {
                    // Unlabeled neighbors go into the queue if they are not there yet
                    int label = labelArray.getInt(x2); 
                    if (label == INIT)
                    {
                        neighbors.add(new Pixel(x2, array.getValue(x2), timeStamp++));
                    }
                    else if (label > 0 && !labels.contains(label))
                    {
                        // store labels of neighbor in a list without repetitions
                        labels.add(label);
                    }
                }
            }
            
            // if the neighbors of the extracted pixel that have already been labeled 
            // all have the same label, then the pixel is labeled with their label
            if (labels.size() == 1)
            {
                labelArray.setInt(x, labels.get(0));
                
                // now that we know the pixel is labeled, add unlabeled neighbors to list
                for (Pixel neighbor : neighbors)
                {   
                    labelArray.setInt(neighbor.x, INQUEUE);
                    queue.add(neighbor);
                }
            }
            else if( labels.size() > 1 )
            {
                // If neighbors have more than two labels, then the current
                // pixel is set to watershed
                labelArray.setInt(x, WATERSHED);
            }   
        }
        
        // assign unlabeled pixels the WSHED flag
        IntArray.Iterator<?> iter = labelArray.iterator(); 
        while (iter.hasNext())
        {
            if (iter.nextInt() == INIT)
            {
                iter.setInt(WATERSHED);
            }
        }

        return labelArray;
    }
    
    private PriorityQueue<Pixel> createQueue(final ScalarArray1D<?> array,
            final IntArray1D<?> markers, IntArray1D<?> result)
    {
        // get image size
        final int sizeX = array.size(0);

        // create priority queue of pixels
        PriorityQueue<Pixel> queue = new PriorityQueue<Pixel>();

        // Iterate over image pixels
        timeStamp = Long.MIN_VALUE;
        for (int x = 0; x < sizeX; x++)
        {
            int label = markers.getInt(x);
            if (label > 0)
            {
                // Iterate over neighbors of current pixel
                for (int x2 : new int[] {x - 1, x + 1})
                {
                    // add unlabeled neighbors to priority queue
                    if (x2 >= 0 && x2 < sizeX
                            && markers.getInt(x2) == 0 
                            && result.getInt(x2) != INQUEUE)
                    {
                        queue.add(new Pixel(x2, array.getValue(x2), timeStamp));
                        result.setInt(x2, INQUEUE);
                    }
                }

                result.setInt(x, label);
            }
        }
        
        return queue;
    }
    
    /**
     * Stores the coordinates of a pixel together with its value and a unique
     * identifier.
     * 
     * Based on the class "PixelRecord" from Ignacio Arganda-Carreras, in the
     * MorphoLibJ library.
     */
    private class Pixel implements Comparable<Pixel>
    {
        int x;
        double value = 0;
        
        /** unique ID for this record */
        final long time;

        /**
         * Create pixel record with from a position and a double value
         * 
         * @param x
         *            the x-coordinate of the pixel position
         * @param value
         *            pixel intensity value
         * @param time
         *            the timestamp for this pixel
         */
        public Pixel(final int x, final double value, long time)
        {
            this.x = x;
            this.value = value;
            this.time = time;
        }
        
        /**
         * Compare with a pixel based on its value and timestamp
         * 
         * @param other
         *            another pixel to compare with
         * @return a value smaller than 0 if the other pixel value is larger
         *         this pixel value, a value larger than 0 if it is lower. If
         *         equal, the pixel created before is set as smaller.
         */
        @Override
        public int compareTo(Pixel other)
        {
            int res = Double.compare(value, other.value);
            if (res == 0)
                return time < other.time ? -1 : 1;
            return res;
        }
    }
}
