/**
 * 
 */
package net.sci.image.morphology.watershed;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.PriorityQueue;

import net.sci.algo.AlgoStub;
import net.sci.array.binary.BinaryArray2D;
import net.sci.array.scalar.Float32Array2D;
import net.sci.array.scalar.IntArray2D;
import net.sci.array.scalar.ScalarArray2D;
import net.sci.array.scalar.UInt8Array2D;
import net.sci.image.binary.BinaryImages;
import net.sci.image.data.Connectivity2D;
import net.sci.image.morphology.MinimaAndMaxima;
import net.sci.image.morphology.extrema.RegionalExtrema2D;
import net.sci.image.morphology.watershed.HierarchicalWatershed.Basin;
import net.sci.image.morphology.watershed.HierarchicalWatershed.Boundary;
import net.sci.image.morphology.watershed.HierarchicalWatershed.MergeBasin;
import net.sci.image.morphology.watershed.HierarchicalWatershed.MergeBoundary;
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
     * @see #computeResult(ScalarArray2D)
     * 
     * @param array
     *            the array containing 2D intensity map
     * @return the map of dynamic for each boundary region.
     */
    public ScalarArray2D<?> process(ScalarArray2D<?> array)
    {
        Results data = computeResult(array);
        return data.saliencyMap;
    }
    
    /**
     * Computes the Hierarchical watershed from a given array, and returns the
     * result into an instance of <code>WatershedGraph2D</code>.
     * 
     * @param array
     *            the array to analyze
     * @return the <code>WatershedGraph2D</code> corresponding to the input
     *         array.
     */
    public Results computeResult(ScalarArray2D<?> array)
    {
        fireStatusChanged(this, "Compute Basin Adjacencies");
        Results data = new GraphBuilder().compute(array);
        
//        System.out.println("Initial graph:");
//        System.out.println(data.graph);
        
//        HierarchicalWatershed.Graph tree = data.graph.minimumSpanningTree();
//        System.out.println("MST:");
//        System.out.println(tree);
       
        fireStatusChanged(this, "Merge basins");
        data.root = new MergeTreeBuilder(data.graph).mergeAllRegions();
        
        fireStatusChanged(this, "Compute Saliency Map");
        data.computeSaliencyMap();
        
        return data;
    }
    
    public ScalarArray2D<?> computeSaliencyMap(Results data)
    {
        int sizeX = data.labelMap.size(0);
        int sizeY = data.labelMap.size(1);
        data.saliencyMap = Float32Array2D.create(sizeX, sizeY);
        
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
                
                Region region = data.graph.getRegion(label);
                if (region instanceof Boundary)
                {
                    Boundary boundary = (Boundary) region;
                    data.saliencyMap.setValue(x, y, boundary.dynamic);
                }
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
    public class Results
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
        
        /**
         * The map of saliency of each boundary pixel, or zero if the pixel
         * belongs to a region.
         */
        ScalarArray2D<?> saliencyMap = null;
        
        /**
         * The data representing the hierarchical watershed, incrementally build
         * during computation.
         */
        HierarchicalWatershed graph;
        
        /**
         * The basin region with the largest dynamic value.
         */
        public Region root = null;
        
        public ScalarArray2D<?> computeSaliencyMap()
        {
            int sizeX = labelMap.size(0);
            int sizeY = labelMap.size(1);
            saliencyMap = Float32Array2D.create(sizeX, sizeY);
            
            for (int y = 0; y < sizeY; y++)
            {
                for (int x = 0; x < sizeX; x++)
                {
                    int label = labelMap.getInt(x, y);
                    if (label == 0)
                    {
                        System.err.println("non initialized label at (" + x + ", " + y + ")");
                        continue;
                    }
                    
                    Region region = graph.getRegion(label);
                    if (region instanceof Boundary)
                    {
                        Boundary boundary = (Boundary) region;
                        saliencyMap.setValue(x, y, boundary.dynamic);
                    }
                }
            }
            
            return saliencyMap;
        }

    }
    
    /**
     * Inner processing class that computes the WetershedGraph2D data structure
     * associated to an input array of scalar values.
     */
    public class GraphBuilder
    {
        // ==============================================================
        // Static Constants
        
        /** Value assigned to the pixels stored into the queue */
        static final int INQUEUE = -1;

        // ==============================================================
        // Processing Methods
        
        /** The number of basins that were detected.*/
        int labelCount = 0;

        /** Used to setup pixel timestamps. */
        long timeStamp = Long.MIN_VALUE;

        public Results compute(ScalarArray2D<?> array)
        {
            Results data = new Results();
            data.graph = new HierarchicalWatershed();
            
            fireStatusChanged(this, "Init label map");
            initLabelMap(array, data);
            fireStatusChanged(this, "Init Queue");
            PriorityQueue<Record> floodingQueue = initQueue(array, data);
            fireStatusChanged(this, "Process Queue");
            processQueue(floodingQueue, array, data);
            
            return data;
        }
        
        private void initLabelMap(ScalarArray2D<?> array, Results data)
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
        
        private void createBasinRegions(ScalarArray2D<?> array, Results data)
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
                        if (!data.graph.hasBasin(label))
                        {
                            double value = array.getValue(x, y);
                            data.graph.createNewBasin(label, value);
                            labelCount++;
                        }
                    }
                }
            }
        }
        
        /**
         * Creates the queue that contains all candidate pixels for flooding.
         * Initial candidates are neighbors of basin pixels.
         * 
         * Pixels are stored with their value, and a time stamps that allows to
         * order them.
         * 
         * @param array
         *            the array containing intensity values
         * @param data
         *            the waterhsed data, containing the array of basin labels.
         * @return the initial queue of pixel records
         */
        private PriorityQueue<Record> initQueue(ScalarArray2D<?> array, Results data)
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
        
        private void processQueue(PriorityQueue<Record> floodingQueue, ScalarArray2D<?> array, Results data)
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
                            // (note: we also add neighbors of boundary pixels, contrary to most algorithms)
                            floodingQueue.add(new Record(x2, y2, array.getValue(x2, y2), timeStamp++));
                            data.labelMap.setInt(x2, y2, INQUEUE);
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
                    data.labelMap.setInt(x, y, basins.iterator().next().label);
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
//                        boundary = data.graph.createBoundary(++labelCount, pixelRecord.value, regions);
                        boundary = data.graph.createBoundary(++labelCount, pixelRecord.value, allBasins);
                    }
//                    else
//                    {
//                        for (Region r : regions)
//                        {
//                            if (!(boundary.regions.contains(r)))
//                            {
//                                boundary.regions.add(r);
//                            }
//                        }
//                    }
                    data.labelMap.setInt(x, y, boundary.label);
                }
            }
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
    
    public static class MergeTreeBuilder
    {
        HierarchicalWatershed graph;
        
        PriorityQueue<Boundary> boundaryQueue;
        
        int basinCount;
        int boundaryCount;
        
        public MergeTreeBuilder(HierarchicalWatershed graph)
        {
            this.graph = graph;
            
            // initialize queue
            this.boundaryQueue = new PriorityQueue<Boundary>();
            this.boundaryQueue.addAll(graph.boundaries.values());
            
            // restart labeling from the number of regions
            this.basinCount = graph.basins.size();
            this.boundaryCount = basinCount + graph.boundaries.size();
        }
        
        public Region mergeAllRegions()
        {
            Region root =  null;
            
            // merge until no more boundary
            while (!boundaryQueue.isEmpty())
            {
                Boundary boundary = boundaryQueue.poll();
                
                System.out.println("merge boundary #" + boundary.label + " with dynamic " + boundary.dynamic + ", count: " + boundaryQueue.size());
                root = mergeBoundary(boundary);
            }
            
            return root;
        }
        
        public Region mergeBoundary(Boundary boundary)
        {
            // retrieve regions managed by this boundary (may contains other boundaries)
            Collection<Region> regions = retrieveAllRegions(boundary);
            
            // create the basin resulting from the merge
            double minValue = HierarchicalWatershed.lowestMinValue(regions);
            MergeBasin mergeBasin = new MergeBasin(++basinCount, minValue, regions);
            
            // Retrieve all the boundaries that contain at least one of the merged regions
            Collection<Boundary> boundaries = retrieveBoundaries(regions);
            
            // remove boundaries from the graph
            for (Boundary bnd : boundaries)
            {
                // remove boundary from graph and queue
                removeBoundaryFromGraph(bnd);
                boundaryQueue.remove(bnd);
            }
            
            // remove all regions that will be merged
            // flooding basin can be Basin or MergeBasin
            Region floodingBasin = removeRegionsFromGraph(regions);
            
            // update dynamic of removed regions with dynamic of the boundary,
            // except for the flooding region
            for (Region region : regions)
            {
                region.dynamic = boundary.dynamic;
            }
            
            // compute the new set of boundaries from the old boundaries 
            ArrayList<MergeBoundary> newBoundaries = new ArrayList<MergeBoundary>();
            for (Boundary bnd : boundaries)
            {
                // check if the boundary will be merged, or will be replaced by a new one
                if (regions.containsAll(bnd.regions))
                {
                    // all the regions of the boundary are merged
                    // -> just need to update the "merge" region and remove from the graph
                    bnd.merge = mergeBasin;
                }
                else
                {
                    // need to create (or retrieve) a boundary between merged basin and remaining basins
                    
                    // create new region list that contains the merged region
                    // and regions outside the merged one
                    Collection<Region> newRegions = remainingRegions(bnd, regions);
                    newRegions.add(mergeBasin);
                    
                    // Find or create the replacement boundary
                    MergeBoundary newBnd = findBoundary(newBoundaries, newRegions);
                    if (newBnd == null)
                    {
                        newBnd = new MergeBoundary(++boundaryCount, bnd.minValue, newRegions);
                        newBoundaries.add(newBnd);
                    }
                    else
                    {
                        // keep minimum value along all possible boundaries
                        newBnd.minValue = Math.min(newBnd.minValue, bnd.minValue);
                    }
                    
                    bnd.merge = newBnd;
                    newBnd.mergedBoundaries.add(bnd);
                }
            }
            
            // add the new region
            graph.regionBoundaries.put(mergeBasin, new ArrayList<>(newBoundaries.size()));
            
            // for each new boundary, add to the graph, update dynamic, and enqueue
            for (MergeBoundary bnd : newBoundaries)
            {
                graph.boundaryRegions.put(bnd, bnd.regions);
                for (Region reg : bnd.regions)
                {
                    graph.regionBoundaries.get(reg).add(bnd);
                }
                
                Basin highestBasin = HierarchicalWatershed.highestBasin(filterBasins(bnd.regions));
                double dynamic = bnd.minValue - highestBasin.minValue;
                
                // update dynamic of merged boundaries
                bnd.updateDynamic(dynamic);
                
                boundaryQueue.add(bnd);
            }
            
            return mergeBasin;
        }
        
        private Collection<Region> retrieveAllRegions(Boundary boundary)
        {
            HashSet<Region> res = new HashSet<>();
            for (Region region : graph.boundaryRegions.get(boundary))
            {
                res.add(region);
                if (region instanceof Boundary)
                {
                    res.addAll(retrieveAllRegions((Boundary) region));
                }
            }
            return res;
        }
        
        /**
         * Retrieve all the boundaries that contain one the regions.
         * @param regions
         * @return
         */
        private Collection<Boundary> retrieveBoundaries(Collection<Region> regions)
        {
            HashSet<Boundary> boundaries = new HashSet<>();
            for (Region region : regions)
            {
                boundaries.addAll(retrieveBoundaries(region));
            }
            
            return boundaries;
        }
        
        private Collection<Boundary> retrieveBoundaries(Region region)
        {
            if (region instanceof Basin)
            {
                return graph.regionBoundaries.get(region);
            }
            else
            {
                throw new RuntimeException("Can not manage Region of class: " + region.getClass());
            }
        }
        
        private void removeBoundaryFromGraph(Boundary boundary)
        {
            for (Region region : graph.boundaryRegions.get(boundary))
            {
                graph.regionBoundaries.get(region).remove(boundary);
            }
            graph.boundaryRegions.remove(boundary);
        }
        
        private Region removeRegionsFromGraph(Collection<Region> regions)
        {
            Region floodingBasin = null;
            double minValue = Double.POSITIVE_INFINITY;
            for (Region region : regions)
            {
                if (region.minValue < minValue)
                {
                    floodingBasin = (Basin) region;
                    minValue = region.minValue;
                }
                
                // remove only the region->boundary mapping
                graph.regionBoundaries.remove(region);
            }
            return floodingBasin;
        }
        
        private Collection<Region> remainingRegions(Boundary boundary, Collection<Region> regions)
        {
            Collection<Region> newRegions = new HashSet<Region>();
            for (Region reg : boundary.regions)
            {
                if (!regions.contains(reg))
                {
                    newRegions.add(reg);
                }
            }
            return newRegions;
        }
        
        /**
         * Try to find a boundary with the same regions as the ones specified.
         * 
         * @param boundaries
         *            the collection of boundaries to search in
         * @param regions
         *            the regions adjacent to the boundary
         * @return the boundary in the collection that has the same regions as
         *         the specified ones, or null if no such boundary exists
         */
        private MergeBoundary findBoundary(Collection<MergeBoundary> boundaries, Collection<Region> regions)
        {
            for (MergeBoundary boundary : boundaries)
            {
                if (boundary.hasSameRegions(regions))
                {
                    return boundary;
                }
            }
            return null;
        }
        
        private Collection<Basin> filterBasins(Collection<Region> regions)
        {
            ArrayList<Basin> basins = new ArrayList<Basin>(regions.size());
            for (Region region : regions)
            {
                if (region instanceof Basin)
                {
                    basins.add((Basin) region);
                }
            }
            return basins;
        }
    }

    
    public static final void main(String... args)
    {
//        int[][] buffer = new int[][] {
//            {  5,   6,  60,  50}, 
//            { 20,  30, 100,  40}, 
//            { 10,  11,  80,  55}, 
//        };
//        int[][] buffer = new int[][] {
//            {250,  70,  60,  50,  40,   0,  40,  50,  60,  73, 250}, 
//            { 35, 150,  71,  60,  50,  40,  50,  60,  74, 210,  55}, 
//            { 21,  34, 250,  72,  60,  73,  60,  75, 250,  54,  41}, 
//            { 20,  22,  33, 250, 260, 240, 260, 250,  53,  42,  30}, 
//            { 23,  32, 250,  70,  61,  53,  62,  71, 250,  52,  43}, 
//            { 31, 220,  71,  64,  52,  40,  54,  67,  72, 150,  51}, 
//            {250,  72,  65,  51,  41,  10,  42,  55,  66,  73, 250}, 
//        };
        int[][] buffer = new int[][] {
            {250, 200,  10, 200, 250}, 
            { 20,  40, 100,  60,  30}, 
            {250,  30, 250,  40, 250}, 
        };
        UInt8Array2D array = UInt8Array2D.fromIntArray(buffer);
        System.out.println(array);

        HierarchicalWatershed2D ws = new HierarchicalWatershed2D();
        Results data = ws.new GraphBuilder().compute(array);
        
        System.out.println("Initial graph:");
        System.out.println(data.graph);
        
        MergeTreeBuilder builder = new MergeTreeBuilder(data.graph);
        Region root = builder.mergeAllRegions();
        
        System.out.println("Root region:");
        System.out.println(root);
        
        data.computeSaliencyMap();
        System.out.println(data.saliencyMap);
        
        
        
//        HierarchicalWatershed.Graph tree = data.graph.minimumSpanningTree();
//        System.out.println("Spanning tree:");
//        System.out.println(tree);

    }
}
