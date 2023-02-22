/**
 * 
 */
package net.sci.image.morphology.watershed;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.PriorityQueue;

import net.sci.algo.AlgoStub;
import net.sci.array.binary.BinaryArray1D;
import net.sci.array.scalar.Float32Array1D;
import net.sci.array.scalar.IntArray1D;
import net.sci.array.scalar.ScalarArray1D;
import net.sci.array.scalar.UInt8Array1D;
import net.sci.image.binary.BinaryImages;
import net.sci.image.morphology.MinimaAndMaxima;
import net.sci.image.morphology.extrema.RegionalExtrema1D;
import net.sci.image.morphology.watershed.HierarchicalWatershed.Basin;
import net.sci.image.morphology.watershed.HierarchicalWatershed.Boundary;
import net.sci.image.morphology.watershed.HierarchicalWatershed.MergeRegion;
import net.sci.image.morphology.watershed.HierarchicalWatershed.Region;

/**
 * Hierarchical watershed algorithm on 1D arrays.
 * 
 * Main expected usage is watershed clustering.
 * 
 * @author dlegland
 *
 */
public class HierarchicalWatershed1D extends AlgoStub
{
    // ==============================================================
    // Methods
    
    /**
     * Computes the watershed on the given (scalar) array.
     * 
     * @param array
     *            the array containing 2D intensity map
     * @return the map of dynamic for each boundary region.
     */
    public ScalarArray1D<?> process(ScalarArray1D<?> array)
    {
        return computeResult(array).saliencyMap;
    }
    
    public WatershedGraph computeResult(ScalarArray1D<?> array)
    {
        fireStatusChanged(this, "Compute Basin Adjacencies");
        WatershedGraph data = new GraphBuilder().compute(array);
        
        fireStatusChanged(this, "Merge basins");
        data.root = new RegionMerger().mergeRegions(data.graph);
        
        fireStatusChanged(this, "Compute Saliency Map");
        computeSaliencyMap(data);
        
        return data;
    }
    
    private ScalarArray1D<?> computeSaliencyMap(WatershedGraph data)
    {
        int sizeX = data.labelMap.size(0);
        data.saliencyMap = Float32Array1D.create(sizeX);
        
        for (int x = 0; x < sizeX; x++)
        {
            int label = data.labelMap.getInt(x);
            if (label == 0)
            {
                System.err.println("non initialized label at (" + x + ")");
                continue;
            }
            
            Region region = data.graph.getRegion(label);
            if (region instanceof Boundary)
            {
                Boundary boundary = (Boundary) region;
                data.saliencyMap.setValue(x, boundary.dynamic);
            }
        }
        
        return data.saliencyMap;
    }

    
    // ==============================================================
    // Inner classes
    
    /**
     * Contains the results of the computation: the watershed graph, the label
     * map, the saliency map.
     */
    public class WatershedGraph
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
        IntArray1D<?> labelMap = null;
        
        ScalarArray1D<?> saliencyMap = null;
        
        /**
         * The data representing the hierarchical watershed, incrementally build
         * during computation.
         */
        HierarchicalWatershed graph;
        
        public Region root = null;
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

        public WatershedGraph compute(ScalarArray1D<?> array)
        {
            WatershedGraph data = new WatershedGraph();
            data.graph = new HierarchicalWatershed();
            
            fireStatusChanged(this, "Init label map");
            initLabelMap(array, data);
            fireStatusChanged(this, "Init Queue");
            PriorityQueue<Record> floodingQueue = initQueue(array, data);
            fireStatusChanged(this, "Process Queue");
            processQueue(floodingQueue, array, data);
            
            return data;
        }
        
        private void initLabelMap(ScalarArray1D<?> array, WatershedGraph data)
        {
            // compute regional minima within image
            fireStatusChanged(this, "Compute regional minima");
            RegionalExtrema1D minimaAlgo = new RegionalExtrema1D(MinimaAndMaxima.Type.MINIMA);
            BinaryArray1D minima = minimaAlgo.processScalar(array);
            // TODO: should be able to avoid creation of binary array
            
            // compute labels of the minima
            fireStatusChanged(this, "Connected component labeling of minima");
            data.labelMap = BinaryImages.componentsLabeling(minima, 32);
            
            // create basin regions
            // TODO: merge labeling and region creation into a single pass algorithm
            fireStatusChanged(this, "Create basin regions");
            createBasinRegions(array, data);
//            System.out.println("  number of basins: " + labelCount);
        }
        
        private void createBasinRegions(ScalarArray1D<?> array, WatershedGraph data)
        {
            // retrieve image size
            final int sizeX = array.size(0);
            
            // Iterate over image pixels
            for (int x = 0; x < sizeX; x++)
            {
                int label = data.labelMap.getInt(x);
                if (label > 0)
                {
                    if (!data.graph.hasBasin(label))
                    {
                        double value = array.getValue(x);
                        data.graph.createNewBasin(label, value);
                        labelCount++;
                    }
                }
            }
        }
        
        private PriorityQueue<Record> initQueue(ScalarArray1D<?> array, WatershedGraph data)
        {
            // retrieve array size
            final int sizeX = array.size(0);
            
            /**
             * The queue of pixels to process. The priority depends on pixel value,
             * and in case of equality on the timestamp.
             */
            PriorityQueue<Record> floodingQueue = new PriorityQueue<Record>();
            
            // Iterate over image pixels
            timeStamp = Long.MIN_VALUE;
            for (int x = 0; x < sizeX; x++)
            {
                int label = data.labelMap.getInt(x);
                if (label > 0)
                {
                    // Iterate over neighbors of current pixel
                    for (int x2 : new int[] { x - 1, x + 1 })
                    {
                        // check bounds
                        if (x2 < 0 || x2 >= sizeX)
                        {
                            continue;
                        }
                        
                        // add unlabeled neighbors to priority queue
                        int label2 = data.labelMap.getInt(x2);
                        if (label2 == 0)
                        {
                            floodingQueue.add(new Record(x2, array.getValue(x2), timeStamp));
                            data.labelMap.setInt(x2, INQUEUE);
                        }
                    }
                }
            }
            
            return floodingQueue;
        }
        
        private void processQueue(PriorityQueue<Record> floodingQueue, ScalarArray1D<?> array, WatershedGraph data)
        {
            // retrieve image size
            final int sizeX = array.size(0);

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

                // Iterate over the neighbors of current position to identify:
                // (1) its surrounding basins and (2) the neighbor positions to
                // enqueue
                for (int x2 : new int[] { x - 1, x + 1 })
                {                                       
                    // check neighbor is inside image
                    if (x2 >= 0 && x2 < sizeX)
                    {
                        int label = data.labelMap.getInt(x2); 
                        
                        if (label == 0)
                        {
                            // If pixel was not yet considered, add it to the queue
                            floodingQueue.add(new Record(x2, array.getValue(x2), timeStamp++));
                            data.labelMap.setInt(x2, INQUEUE);
                        }
                        else if (label > 0)
                        {
                            // If pixel belongs to a region, update the list of neighbor basins
                            // In case the pixel is a boundary, the basins it bounds are considered.
                            Region region = data.graph.getRegion(label);
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
                if (HierarchicalWatershed.isBasinElement(basins, boundaries))
                {
                    // if all the neighbors belong to the same basin,
                    // propagate its label to the current pixel
                    data.labelMap.setInt(x, basins.iterator().next().label);
                }
                else
                {
                    // If pixel belongs to a boundary region,                     
                    // we first need to check if such boundary exists
                    Collection<Basin> allBasins = data.graph.findAllBasins(regions);
                    Boundary boundary = data.graph.getBoundary(allBasins);

                    // otherwise, create the new boundary (boundaries of specified
                    // regions are updated during boundary creation)
                    if (boundary == null)
                    {
                        // create a new boundary region
                        boundary = data.graph.createBoundary(++labelCount, pixelRecord.value, allBasins);
                    }
                    data.labelMap.setInt(x, boundary.label);
                }   
            }
        }
        
        /**
         * Stores the coordinate of a 1D-array element together with its value
         * and a unique identifier.
         */
        private class Record implements Comparable<Record>
        {
            int x;
            double value = 0;
            
            /** unique ID for this record */
            final long time;
        
            /**
             * Creates a record from a position and a double value.
             * 
             * @param x
             *            the x-coordinate of the pixel position
             * @param value
             *            pixel intensity value
             * @param time
             *            the timestamp for this record
             */
            public Record(final int x, final double value, long time)
            {
                this.x = x;
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
        public Region mergeRegions(HierarchicalWatershed graph)
        {
            PriorityQueue<Region> mergeQueue = new PriorityQueue<>();
            for (Basin basin : graph.basins.values())
            {
//                basin.recomputeDynamic();
                graph.recomputeDynamic(basin);
                mergeQueue.add(basin);
            }
            
            // restart labeling from the number of regions
            int nodeCount = graph.basins.size();
            
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
            double minValue = HierarchicalWatershed.lowestMinValue(region.boundaries);
            
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
            for (Boundary boundary : HierarchicalWatershed.adjacentBoundaries(regions))
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
            double minPassValue = HierarchicalWatershed.lowestMinValue(merge.boundaries);
            merge.dynamic = minPassValue - merge.minValue;
            
            // return the new region
            return merge;
        }
    }
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        // create a simple array with five minima
        // (located at odd position indices)
        int[] data = new int[] {6, 2, 4, 3, 8, 4, 6, 0, 4, 1, 7};
        UInt8Array1D array = UInt8Array1D.fromIntArray(data);
        
        System.out.println(array);
        
        HierarchicalWatershed1D algo = new HierarchicalWatershed1D();
        HierarchicalWatershed1D.WatershedGraph res = algo.computeResult(array);
        
        System.out.println(res.labelMap);
        
        System.out.println(res.saliencyMap);
        
        System.out.println(res.root);
        System.out.println(HierarchicalWatershed.printRegionTree(res.root));
    }
}
