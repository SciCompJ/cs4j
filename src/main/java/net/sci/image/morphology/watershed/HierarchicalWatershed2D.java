/**
 * 
 */
package net.sci.image.morphology.watershed;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;

import net.sci.algo.AlgoStub;
import net.sci.array.binary.BinaryArray2D;
import net.sci.array.scalar.Float32Array2D;
import net.sci.array.scalar.IntArray2D;
import net.sci.array.scalar.ScalarArray2D;
import net.sci.image.binary.BinaryImages;
import net.sci.image.data.Connectivity2D;
import net.sci.image.morphology.MinimaAndMaxima;
import net.sci.image.morphology.extrema.RegionalExtrema2D;
import net.sci.image.morphology.watershed.HierarchicalWatershed.Basin;
import net.sci.image.morphology.watershed.HierarchicalWatershed.Boundary;
import net.sci.image.morphology.watershed.HierarchicalWatershed.MergeRegion;
import net.sci.image.morphology.watershed.HierarchicalWatershed.Region;

/**
 * Hierarchical Watershed for 2D scalar arrays. The result is a 2D saliency map
 * the same size as the original array, containing either 0 if the pixel is
 * within a catchment basin, or the value of the contour dynamic of the pixel 
 * is a watershed pixel.
 * 
 * Example:
 * <pre>{@code
 *  // requires a scalar array as input (can be floating point as well) 
 *  UInt8Array2D array = ...;
 *  
 *  // create the operator, and display steps on console
 *  HierarchicalWatershed2D algo = new HierarchicalWatershed2D();
 *  ConsoleAlgoListener.monitor(algo);
 *  
 *  // process the array
 *  ScalarArray2D<?> result = algo.process(array);
 *
 *  // display the result
 *  Image resultImage = new Image(result);
 *  resultImage.show();
 * }</pre>
 * @author dlegland
 *
 */
public class HierarchicalWatershed2D extends AlgoStub
{
    // ==============================================================
    // Class members (algorithm settings)
    
    /** 
     * Connectivity of regions (expected 4 or 8). 
     * The watershed usually has complementary connectivity. 
     */
    Connectivity2D connectivity = Connectivity2D.C4;
    
    
    // ==============================================================
    // Methods
    
    /**
     * Computes the watershed on the given (scalar) array.
     * 
     * @param array
     *            the array containing 2D intensity map
     * @return the map of dynamic for each boundary region.
     */
    public ScalarArray2D<?> process(ScalarArray2D<?> array)
    {
        fireStatusChanged(this, "Compute Basin Adjacencies");
        WatershedGraph2D data = new GraphBuilder().compute(array);
        
        fireStatusChanged(this, "Merge basins");
        new RegionMerger().mergeRegions(data);
        
        fireStatusChanged(this, "Compute Saliency Map");
        return computeSaliencyMap(data);
    }
    
    private ScalarArray2D<?> computeSaliencyMap(WatershedGraph2D data)
    {
        int sizeX = data.labelMap.size(0);
        int sizeY = data.labelMap.size(1);
        ScalarArray2D<?> saliencyMap = Float32Array2D.create(sizeX, sizeY);
        
        for (int y = 0; y < sizeY; y++)
        {
            for (int x = 0; x < sizeX; x++)
            {
                int label = data.labelMap.getInt(x, y);
                if (label == 0)
                {
                    System.err.println("non initialized label at (" + x + ", " + y + ")");
                    continue;
                }
                
                Region region = data.getRegion(label);
                if (region instanceof Boundary)
                {
                    Boundary boundary = (Boundary) region;
                    saliencyMap.setValue(x, y, boundary.dynamic);
                }
            }
        }
        
        return saliencyMap;
    }

    
    // ==============================================================
    // Inner classes
    
    /**
     * The data necessary to compute the hierarchical watershed.
     */
    public class WatershedGraph2D
    {
        // ==============================================================
        // Class members
        
        /**
         * An array of labels the same size as the input array, containing
         * either the index of the region the pixel belong to, or 0 if the pixel
         * was not yet processed.
         * 
         * At the end of the process, all elements should have a value greater
         * than 0.
         */
        IntArray2D<?> labelMap = null;
        
        HashMap<Integer, Basin> basins = new HashMap<Integer, Basin>();
        
        HashMap<Integer, Boundary> boundaries = new HashMap<Integer, Boundary>();
        
        
        private Region getRegion(int label)
        {
            if (this.basins.containsKey(label))
            {
                return this.basins.get(label);
            }
            if (this.boundaries.containsKey(label))
            {
                return this.boundaries.get(label);
            }
            return null;
        }
    }
    
    private class GraphBuilder
    {
        // ==============================================================
        // Static Constants
        
        /** Value assigned to the pixels stored into the queue */
        static final int INQUEUE = -1;

        // ==============================================================
        // Processing Methods
        
        int labelCount = 0;

        /** Used to setup pixel timestamps. */
        long timeStamp = Long.MIN_VALUE;

        public WatershedGraph2D compute(ScalarArray2D<?> array)
        {
            WatershedGraph2D data = new WatershedGraph2D();
            
            fireStatusChanged(this, "Init label map");
            initLabelMap(array, data);
            fireStatusChanged(this, "Init Queue");
            PriorityQueue<Record> floodingQueue = initQueue(array, data);
            fireStatusChanged(this, "Process Queue");
            processQueue(floodingQueue, array, data);
            
            return data;
        }
        
        private void initLabelMap(ScalarArray2D<?> array, WatershedGraph2D data)
        {
            // compute regional minima within image
            fireStatusChanged(this, "Compute regional minima");
            RegionalExtrema2D minimaAlgo = new RegionalExtrema2D(MinimaAndMaxima.Type.MINIMA, connectivity);
            BinaryArray2D minima = minimaAlgo.processScalar(array);
            // TODO: should be able to avoid creation of binary array
            
            // compute labels of the minima
            fireStatusChanged(this, "Connected component labeling of minima");
            data.labelMap = BinaryImages.componentsLabeling(minima, connectivity, 32);
            
            // create basin regions
            // TODO: merge labeling and region creation into a single pass algorithm
            fireStatusChanged(this, "Create basin regions");
            createBasinRegions(array, data);
//            System.out.println("  number of basins: " + labelCount);
        }
        
        private void createBasinRegions(ScalarArray2D<?> array, WatershedGraph2D data)
        {
            // retrieve image size
            final int sizeX = array.size(0);
            final int sizeY = array.size(1);
            
            // Iterate over image pixels
            for (int y = 0; y < sizeY; y++)
            {
                for (int x = 0; x < sizeX; x++)
                {
                    int label = data.labelMap.getInt(x, y);
                    if (label > 0)
                    {
                        if (!data.basins.containsKey(label))
                        {
                            double value = array.getValue(x, y);
                            Basin basin = new Basin(label, value);
                            data.basins.put(label, basin);
                            labelCount++;
                        }
                    }
                }
            }
        }
        
        private PriorityQueue<Record> initQueue(ScalarArray2D<?> array, WatershedGraph2D data)
        {
            // retrieve image size
            final int sizeX = array.size(0);
            final int sizeY = array.size(1);
            
            /**
             * The queue of pixels to process. The priority depends on pixel value,
             * and in case of equality on the timestamp.
             */
            PriorityQueue<Record> floodingQueue = new PriorityQueue<Record>();
            
            // Iterate over image pixels
            timeStamp = Long.MIN_VALUE;
            for (int y = 0; y < sizeY; y++)
            {
                for (int x = 0; x < sizeX; x++)
                {
                    int label = data.labelMap.getInt(x, y);
                    if (label > 0)
                    {
                        // Iterate over neighbors of current pixel
                        for (int[] pos : connectivity.getNeighbors(x, y))
                        {
                            // coordinates of neighbor pixel
                            int x2 = pos[0];
                            int y2 = pos[1];
                            
                            // check bounds
                            if (x2 < 0 || x2 >= sizeX || y2 < 0 || y2 >= sizeY)
                            {
                                continue;
                            }
                            
                            // add unlabeled neighbors to priority queue
                            int label2 = data.labelMap.getInt(x2, y2);
                            if (label2 == 0)
                            {
                                floodingQueue.add(new Record(x2, y2, array.getValue(x2, y2), timeStamp));
                                data.labelMap.setInt(x2, y2, INQUEUE);
                            }
                        }
                    }
                }
            }
            
            return floodingQueue;
        }
        
        private void processQueue(PriorityQueue<Record> floodingQueue, ScalarArray2D<?> array, WatershedGraph2D data)
        {
            // retrieve image size
            final int sizeX = array.size(0);
            final int sizeY = array.size(1);

            // the list of regions of all types (Basin or Boundary) around current pixel
            HashSet<Region> regions = new HashSet<Region>();
            // the list of basins around current pixel
            HashSet<Basin> basins = new HashSet<Basin>();
            HashSet<Boundary> boundaries = new HashSet<Boundary>();
            // the list of new records to create around current pixel
            ArrayList<Record> neighbors = new ArrayList<Record>();
            
            // Process pixels and eventually add neighbors until the queue is empty 
            while (!floodingQueue.isEmpty())
            {
                if (Thread.currentThread().isInterrupted())
                {
                    return;
                }
                
                // reset state
                regions.clear();
                basins.clear();
                boundaries.clear();
                neighbors.clear();
                
                // get next record
                Record pixelRecord = floodingQueue.poll();

                // coordinates of current pixel
                int x = pixelRecord.x;
                int y = pixelRecord.y;

                // Iterate over the neighbors of current pixel to identify:
                // (1) its surrounding basins and (2) the neighbor pixels to
                // enqueue
                for (int[] pos : connectivity.getNeighbors(x, y))
                {                                       
                    // Look in neighborhood for labeled pixels with
                    // smaller or equal original value
                    int x2 = pos[0];
                    int y2 = pos[1];
                    
                    // check neighbor is inside image
                    if (x2 >= 0 && x2 < sizeX && y2 >= 0 && y2 < sizeY)
                    {
                        int label = data.labelMap.getInt(x2, y2); 
                        
                        if (label == 0)
                        {
                            // If pixel was not yet considered, add it to the queue
                            neighbors.add(new Record(x2, y2, array.getValue(x2, y2), timeStamp++));
                        }
                        else if (label > 0)
                        {
                            // If pixel belongs to a region, update the list of neighbor basins
                            // In case the pixel is a boundary, the basins it bounds are considered.
                            Region region = data.getRegion(label);
                            regions.add(region);
                            if (region instanceof Basin)
                            {
                                basins.add((Basin) region);
                            }
                            else
                            {
                                boundaries.add((Boundary) region);
                            }
                        }
                        // the remaining case is "in queue", but we do not need to process
                    }
                }
                
                // detects if the current pixel belongs to a basin or a boundary.
                if (isBasinPixel(basins, boundaries))
                {
                    // if all the neighbors belong to the same basin,
                    // propagate its label to the current pixel
                    data.labelMap.setInt(x, y, basins.iterator().next().label);
                }
                else
                {
                    // If pixel belongs to a boundary region,                     
                    // we first need to check if such boundary exists
                    Collection<Basin> allBasins = Basin.findAllBasins(regions);
                    Boundary boundary = Boundary.getBoundary(allBasins);

                    // otherwise, create the new boundary (boundaries of specified
                    // regions are updated during boundary creation)
                    if (boundary == null)
                    {
                        // create a new boundary region
                        boundary = createBoundary(++labelCount, pixelRecord.value, allBasins);
                        data.boundaries.put(boundary.label, boundary);
                    }
                    data.labelMap.setInt(x, y, boundary.label);
                }   
                
                // also add the neighbors without labels to the queue
                // (note: we also add neighbors of boundary pixels, contrary to most algorithms)
                for (Record neighbor : neighbors)
                {   
                    data.labelMap.setInt(neighbor.x, neighbor.y, INQUEUE);
                    floodingQueue.add(neighbor);
                }
            }
        }
        
        // detects if the current pixel belongs to a basin or a boundary.
        // the pixel should have only one neighbor basin, and if a
        // boundary is present, it should contain the basin.
        private boolean isBasinPixel(Collection<Basin> basins, Collection<Boundary> boundaries)
        {
            // the pixel should have only one neighbor basin
            if (basins.size() != 1)
            {
                return false;
            }
            Basin theBasin = basins.iterator().next();
            
            // if no boundary, pixel is basin
            if (boundaries.size() == 0)
            {
                return true;
            }
            
            // if a boundary is present, it should contain the basin.
            if (boundaries.size() == 1)
            {
                Boundary theBoundary = boundaries.iterator().next();
                if (theBoundary.regions.contains(theBasin))
                {
                    return true;
                }
            }
            return false;
        }

        private Boundary createBoundary(int label, double minValue, Collection<? extends Region> basins)
        {
            // create a new boundary instance, leaving dynamic not yet initialized
            Boundary boundary = new Boundary(label, minValue, basins);
            //            System.out.println("Create boundary " + label + " between basins: " + regionLabelsString(basins));

            // compute the dynamic of the new boundary, using the minimum value
            // on the boundary, and the basins with the highest minimum value.
            Basin highestBasin = Basin.highestBasin(boundary.basins());
            boundary.dynamic = minValue - highestBasin.minValue;
            highestBasin.dynamic = boundary.dynamic;

            return boundary;
        }

        /**
         * Stores the coordinates of a pixel together with its value and a unique
         * identifier.
         * 
         * Based on the class "PixelRecord" from Ignacio Arganda-Carreras, in the
         * MorphoLibJ library.
         */
        private class Record implements Comparable<Record>
        {
            int x;
            int y;
            double value = 0;
            
            /** unique ID for this record */
            final long time;
        
            /**
             * Create pixel record with from a position and a double value
             * 
             * @param x
             *            the x-coordinate of the pixel position
             * @param y
             *            the y-coordinate of the pixel position
             * @param value
             *            pixel intensity value
             * @param time
             *            the timestamp for this pixel
             */
            public Record(final int x, final int y, final double value, long time)
            {
                this.x = x;
                this.y = y;
                this.value = value;
                this.time = time;
            }
            
            /**
             * Compares with a pixel based on its value and its timestamp.
             * 
             * @param other
             *            another pixel to compare with
             * @return a value smaller than 0 if the other pixel value is larger
             *         this pixel value, a value larger than 0 if it is lower. If
             *         equal, the pixel created before is set as smaller.
             */
            @Override
            public int compareTo(Record other)
            {
                int res = Double.compare(value, other.value);
                if (res == 0)
                    return time < other.time ? -1 : 1;
                return res;
            }
        }
    }
    
    private class RegionMerger
    {
        public Region mergeRegions(WatershedGraph2D data)
        {
            PriorityQueue<Region> mergeQueue = new PriorityQueue<>();
            for (Basin basin : data.basins.values())
            {
                basin.recomputeDynamic();
                mergeQueue.add(basin);
            }
            
            // restart labeling from the number of regions
            int nodeCount = data.basins.size();
            
            // merge until only one region remains
            while (mergeQueue.size() > 1)
            {
                Region region = mergeQueue.poll();
                Collection<Region> regions = findRegionsToMerge(region);
                
                // create the merge region
                MergeRegion mergeRegion = mergeRegions(++nodeCount, region.dynamic, regions);
                
                // update queue
                mergeQueue.removeAll(regions);
                mergeQueue.add(mergeRegion);
            }
            
            return mergeQueue.poll();
        }

        /**
         * Identifies the set of region to merge, as the set of regions
         * connected to the initial region with a boundary whose min value is
         * the current min value.
         * 
         * @param region
         *            the region that initiates the merge
         * @return a collection of regions connected with boundaries with the
         *         same saddle value
         */
        private Collection<Region> findRegionsToMerge(Region region)
        {
            // get the pass / saddle value, as the lowest value along boundaries of current region
            double minValue = Region.lowestMinValue(region.boundaries);
            
            // collect regions with same boundary saddle value
            HashSet<Region> regions = new HashSet<>();
            for (Boundary boundary : region.boundaries)
            {
                if (boundary.minValue == minValue)
                {
                    regions.addAll(boundary.regions);
                }
            }
            return regions;
        }
        
        /**
         * Merges the specified regions and returns the newly created region.
         * The boundaries are updated.
         * 
         * @param label
         *            the label of the new region
         * @param dynamic
         *            the dynamic of the boundary that will be merged
         * @param regions
         *            the regions to merge
         * @return the result of the merge, as a MergeRegion instance
         */
        private MergeRegion mergeRegions(int label, double dynamic, Collection<Region> regions)
        {
            // create the merge
            MergeRegion merge = new MergeRegion(label, regions);
            
            // need to update the references between basins and boundaries
            // (1) remove boundaries whose basins are within the merge
            // (2) replace boundaries with basins outside and inside the merge, 
            //      taking into account the possible duplicates
            for (Boundary boundary : Boundary.adjacentBoundaries(regions))
            {
                if (boundary.hasNoOtherRegionThan(regions))
                {
                    // the boundary has to be merged within the new region.
                    // just need to update its dynamic
                    boundary.dynamic = dynamic;
                }
                else
                {
                    // need to update references to inner regions into reference
                    // to the merge region
                    boundary.regions.removeAll(regions);
                    boundary.regions.add(merge);
                    merge.boundaries.add(boundary);
                }
            }
            
            // compute the dynamic of the new region, by finding the minimum
            // value along boundaries
            double minPassValue = Region.lowestMinValue(merge.boundaries);
            merge.dynamic = minPassValue - merge.minValue;
            
            // return the new region
            return merge;
        }
    }
}
