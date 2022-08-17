/**
 * 
 */
package net.sci.image.morphology.watershed;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.PriorityQueue;

import net.sci.algo.AlgoStub;
import net.sci.algo.ConsoleAlgoListener;
import net.sci.array.binary.BinaryArray2D;
import net.sci.array.scalar.Float32Array2D;
import net.sci.array.scalar.IntArray2D;
import net.sci.array.scalar.ScalarArray;
import net.sci.array.scalar.ScalarArray2D;
import net.sci.array.scalar.UInt8Array;
import net.sci.array.scalar.UInt8Array2D;
import net.sci.image.Image;
import net.sci.image.binary.BinaryImages;
import net.sci.image.data.Connectivity2D;
import net.sci.image.io.TiffImageReader;
import net.sci.image.morphology.MinimaAndMaxima;
import net.sci.image.morphology.extrema.RegionalExtrema2D;

/**
 * Hierarchical Watershed for 2D scalar arrays. The result is a 2D saliency map
 * the same size as the original array, containing either 0 if the pixel is
 * within a catchment basin, or the value of the contour dynamic of the pixel 
 * is a watershed pixel.
 * 
 * @author dlegland
 *
 */
public class HierarchicalWatershed2D extends AlgoStub
{
    // ==============================================================
    // Static Constants
    
    /** Value assigned to the pixels stored into the queue */
    static final int INQUEUE = -1;

    
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
        Processor processor = new Processor(array);
        
        fireStatusChanged(this, "Init label map");
        processor.initLabelMap();
        fireStatusChanged(this, "Init Queue");
        processor.initQueue();
        fireStatusChanged(this, "Process Queue");
        processor.processQueue();
        
        System.out.println("After Watershed:\n" + processor.labelMap);
        
        fireStatusChanged(this, "Merge basins");
        processor.mergeRegions();
        
        fireStatusChanged(this, "Compute Saliency Map");
        processor.computeSaliencyMap();
        
        return processor.saliencyMap;
    }
    
    
    // ==============================================================
    // Inner classes
    
    public class Processor
    {
        // ==============================================================
        // Class members
        
        /**
         * The array containing the values, associated to "heights".
         */
        ScalarArray2D<?> array;
        
        /**
         * An array of labels the same size as the input array, containing
         * either the index of the region the pixel belong to, or 0 if the pixel
         * was not yet processed.
         * 
         * At the end of the process, all elements should have a value greater
         * than 0.
         */
        IntArray2D<?> labelMap = null;
        
//        /**
//         * The array of regions, initialized with minima, then updated during
//         * flooding process when new boundaries are created.
//         */
//        HashMap<Integer, Region> regions = new HashMap<Integer, Region>();
        
//        ArrayList<Basin> basins;
        HashMap<Integer, Basin> basins = new HashMap<Integer, Basin>();
        
        HashMap<Integer, Boundary> boundaries = new HashMap<Integer, Boundary>();
        
        
        int labelCount = 0;
        
        int mergeLabel = 0;
        
        /**
         * The queue of pixels to process. The priority depends on pixel value,
         * and in case of equality on the timestamp.
         */
        PriorityQueue<Record> floodingQueue = new PriorityQueue<Record>();
        
        /** Used to setup pixel timestamps. */
        long timeStamp = Long.MIN_VALUE;

        ScalarArray2D<?> saliencyMap = null;
        
        
        // ==============================================================
        // Constructor
        
        public Processor(ScalarArray2D<?> array)
        {
            this.array = array;
        }
        
        
        // ==============================================================
        // Processing Methods
        
        private void initLabelMap()
        {
            // compute regional minima within image
            fireStatusChanged(this, "Compute regional minima");
            RegionalExtrema2D minimaAlgo = new RegionalExtrema2D(MinimaAndMaxima.Type.MINIMA, connectivity);
            BinaryArray2D minima = minimaAlgo.processScalar(array);
            // TODO: should be able to avoid creation of binary array
            
            // compute labels of the minima
            fireStatusChanged(this, "Connected component labeling of minima");
            this.labelMap = BinaryImages.componentsLabeling(minima, connectivity, 32);
            
            System.out.println("Initial Labels:\n" + this.labelMap);
//            UInt8Array2D label8 = UInt8Array2D.wrap(UInt8Array.wrapScalar(this.labelMap));
//            label8.print(System.out);
            
            // create basin regions
            // TODO: merge labeling and region creation into a single pass algorithm
            fireStatusChanged(this, "Create basin regions");
            createBasinRegions();
        }
        
        private void createBasinRegions()
        {
            // retrieve image size
            final int sizeX = array.size(0);
            final int sizeY = array.size(1);
            
            // Iterate over image pixels
            for (int y = 0; y < sizeY; y++)
            {
                for (int x = 0; x < sizeX; x++)
                {
                    int label = labelMap.getInt(x, y);
                    if (label > 0)
                    {
                        if (!basins.containsKey(label))
                        {
                            double value = array.getValue(x, y);
                            Basin basin = new Basin(label, value);
                            basins.put(label, basin);
                            
                            labelCount++;
                        }
//                        if (!regions.containsKey(label))
//                        {
//                            double value = array.getValue(x, y);
//                            Basin basin = new Basin(label, value);
//                            regions.put(label, basin);
//                            
//                            labelCount++;
//                        }
                    }
                }
            }
            
            mergeLabel = labelCount;
        }
        
        private void initQueue()
        {
            // retrieve image size
            final int sizeX = array.size(0);
            final int sizeY = array.size(1);

            // Iterate over image pixels
            timeStamp = Long.MIN_VALUE;
            for (int y = 0; y < sizeY; y++)
            {
                for (int x = 0; x < sizeX; x++)
                {
                    int label = labelMap.getInt(x, y);
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
                            int label2 = labelMap.getInt(x2, y2);
                            if (label2 == 0)
                            {
                                floodingQueue.add(new Record(x2, y2, array.getValue(x2, y2), timeStamp));
                                labelMap.setInt(x2, y2, INQUEUE);
                            }
                        }
                    }
                }
            }
        }
        
        private void processQueue()
        {
            // retrieve image size
            final int sizeX = array.size(0);
            final int sizeY = array.size(1);

            // the list of basins around current pixel
            HashSet<Basin> basins = new HashSet<Basin>();
            // the list of new records to create around current pixel
            ArrayList<Record> neighbors = new ArrayList<Record>();
            
            // Process pixels and eventually add neighbors until the queue is empty 
            while (!floodingQueue.isEmpty())
            {
//                if (Thread.currentThread().isInterrupted())
//                {
//                    return;
//                }
                
                // reset state
                basins.clear();
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
                        int label = labelMap.getInt(x2, y2); 
                        
                        if (label == 0)
                        {
                            // If pixel was not yet considered, add it to the queue
                            neighbors.add(new Record(x2, y2, array.getValue(x2, y2), timeStamp++));
                        }
                        else if (label > 0)
                        {
                            // If pixel belongs to a region, update the list of neighbor basins
                            // In case the pixel is a boundary, the basins it bounds are considered.
                            Region region = getRegion(label);
                            if (region instanceof Basin)
                            {
                                basins.add((Basin) region);
                            }
//                            addBasins(region, basins);
                        }
                        // the remaining case is "in queue", but we do not need to process
                    }
                }
                
                // detects if the current pixel belongs to a basin or a boundary.
                if (basins.size() == 1)
                {
                    // if all the neighbors belong to the same basin,
                    // propagate its label to the current pixel
                    labelMap.setInt(x, y, basins.iterator().next().label);
                    
                    // add the neighbors without labels to the queue
                    for (Record neighbor : neighbors)
                    {   
                        labelMap.setInt(neighbor.x, neighbor.y, INQUEUE);
                        floodingQueue.add(neighbor);
                    }
                }
                else if (basins.size() > 1)
                {
                    // If neighbors have more than two labels, then the pixel belongs to a boundary region.
                    
                    // we first need to check if such boundary exists
                    Boundary boundary = getBoundary(basins);

                    // otherwise, create the new region (boundaries of specified
                    // regions are updated during boundary creation)
                    if (boundary == null)
                    {
                        // create a new boundary region
                        double value = pixelRecord.value;
                        boundary = createBoundary(++labelCount, value, basins);
                    }
                    labelMap.setInt(x, y, boundary.label);
                    
//                    // also add the neighbors without labels to the queue
//                    // (note: this behavior depends on the the algorithms)
//                    for (Record neighbor : neighbors)
//                    {   
//                        labelMap.setInt(neighbor.x, neighbor.y, INQUEUE);
//                        floodingQueue.add(neighbor);
//                    }
                }   
            }
        }
        
        /**
         * Returns the boundary that bounds the specified regions, or null if no
         * such boundary exist. The search is performed on the boundary of an
         * arbitrary region from the specified collection.
         * 
         * @param adjRegions
         *            a list of regions (two or more) that define a boundary
         * @return the boundary that bounds the given regions, or null if no
         *         such boundary exist
         */
        private Boundary getBoundary(Collection<Basin> adjRegions)
        {
            if (adjRegions.isEmpty())
            {
                return null;
            }
            
            // check the boundaries of an arbitrary region
            return adjRegions.iterator().next().findBoundary(adjRegions);
        }
        
//        private void addBasins(Region region, ArrayList<Basin> basins)
//        {
//            if (region instanceof Basin)
//            {
//                // store labels of neighbor in a list without duplicates
//                if (!basins.contains(region))
//                {
//                    basins.add((Basin) region);
//                }
//            }
//            else if (region instanceof Boundary)
//            {
//                for (Region reg : ((Boundary) region).regions)
//                {
//                    addBasins(reg, basins);
//                }
//            }
//        }
        
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
                
        private Boundary createBoundary(int label, double minValue, Collection<? extends Region> basins)
        {
            // create a new boundary instance, leaving dynamic not yet initialized
            Boundary boundary = new Boundary(label, minValue, basins);
            System.out.println("Create boundary " + label + " between basins: " + regionLabelsString(basins));
            this.boundaries.put(label, boundary);
            
            // compute the dynamic of the new boundary, using the minimum value
            // on the boundary, and the basins with the highest minimum value.
            Basin highestBasin = highestBasin(boundary.basins());
            boundary.dynamic = minValue - highestBasin.minValue;
            highestBasin.dynamic = boundary.dynamic;
            
          
//            // retrieve flooding region from each adjacent region
//            ArrayList<Basin> floodingBasins = floodingBasins(basins);
//            System.out.println(" ; flooding basins: " + regionLabelsString(floodingBasins));
//            
//            if (floodingBasins.size() > 1)
//            {
//                // compute dynamic of boundary, 
//                // as the difference between the minimum value along the
//                // boundary and the largest minimum value of flooding regions
//                double maxDepth = largestMinValue(floodingBasins);
//                boundary.dynamic = minValue - maxDepth;
//                
//                // identify the main flooding region as the flooding region from
//                // adjacent regions with the lowest value
//                Basin floodingRegion = findLowestRegion(floodingBasins);
//                
//                // keep boundary dynamic as current dynamic of flooding region
//                // (keep previous value if greater)
//                floodingRegion.dynamic = Math.max(floodingRegion.dynamic, boundary.dynamic);
//                
//                // update the flooding region of adjacent regions
//                for (Basin basin : basins)
//                {
//                    // identifies the flooding basin
//                    basin = basin.globalFloodingBasin();
//                    if (basin != floodingRegion)
//                    {
//                        basin.floodingBasin = floodingRegion;
//                        basin.dynamic = boundary.dynamic;
//                    }
//                }
//            }
//            else
//            {
//                // all regions are flooded by the same region
//                // -> use the dynamic from the flooding region
//                Region floodingRegion = floodingBasins.get(0);
//                boundary.dynamic = floodingRegion.dynamic;
//            }
            
            return boundary;
        }

        
        private void mergeRegions()
        {
            PriorityQueue<Region> mergeQueue = new PriorityQueue<>();
            for (Basin basin : this.basins.values())
            {
                basin.recomputeDynamic();
                mergeQueue.add(basin);
            }
            
            // merge until only one region remains
            while (mergeQueue.size() > 1)
            {
                Region region = mergeQueue.poll();
                System.out.println("Process region: " + region);
                
                // get the pass value, as the lowest value along boundaries of current region
                double minValue = lowestMinValue(region.boundaries);
                
                double dynamic = region.dynamic; 
                
                // find all connected regions, by retrieving all boundaries with
                // dynamic equal to the smallest dynamic
                // TODO: should check complex case of several regions connected by triple boundaries...
//                double minDyn = Double.POSITIVE_INFINITY;
//                double minValue = Double.POSITIVE_INFINITY;
//                for (Boundary boundary : region.boundaries)
//                {
////                    minDyn = Math.min(minDyn, boundary.dynamic);
//                    minValue = Math.min(minValue, boundary.minValue);
//                }
                // identifies the set of region to merge, as the set of regions connected to the
                // initial region with a boundary whose min value is the current min value.
                HashSet<Region> regions = new HashSet<>();
                for (Boundary boundary : region.boundaries)
                {
                    if (boundary.minValue == minValue)
                    {
                        regions.addAll(boundary.regions);
                    }
                }
                
                // create the merge region
                MergeRegion mergeRegion = mergeRegions(minValue, dynamic, regions);
                
                // update queue
                mergeQueue.removeAll(regions);
                mergeQueue.add(mergeRegion);
            }
        }
        
        /**
         * Merges the specified regions and returns the newly created region.
         * The boundaries are updated.
         * 
         * @param regions
         *            the regions to merge
         * @return the result of the merge, as a MergeRegion instance
         */
        private MergeRegion mergeRegions(double mergeValue, double dynamic, Collection<Region> regions)
        {
            // create the merge
            MergeRegion merge = new MergeRegion(++mergeLabel, mergeValue, regions);
            
            // need to update the references between basins and boundaries
            // (1) remove boundaries whose basins are within the merge
            // (2) replace boundaries with basins outside and inside the merge, 
            //      taking into account the possible duplicates
            for (Boundary boundary : adjacentBoundaries(regions))
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
            double minPassValue = Double.POSITIVE_INFINITY;
            for (Boundary boundary : merge.boundaries)
            {
                minPassValue = Math.min(minPassValue, boundary.minValue);
            }
            merge.dynamic = minPassValue - merge.minValue;
            
            // return the new region
            return merge;
        }
        
        private void computeSaliencyMap()
        {
            int sizeX = this.array.size(0);
            int sizeY = this.array.size(1);
            this.saliencyMap = Float32Array2D.create(sizeX, sizeY);
            
            for (int y = 0; y < sizeY; y++)
            {
                for (int x = 0; x < sizeX; x++)
                {
                    int label = this.labelMap.getInt(x, y);
                    if (label == 0)
                    {
                        System.err.println("non initialized label at (" + x + ", " + y + ")");
                        continue;
                    }
                    
                    Region region = getRegion(label);
                    if (region instanceof Boundary)
                    {
                        Boundary boundary = (Boundary) region;
                        this.saliencyMap.setValue(x, y, boundary.dynamic);
                    }
                }
            }
        }
    }
    
/**
     * @param regions
     *            a set of regions
     * @return the set of boundaries that are adjacent to at least one of
     *         the regions within the list.
     */
    private static final Collection<Boundary> adjacentBoundaries(Collection<Region> regions)
    {
        HashSet<Boundary> boundaries = new HashSet<>();
        for (Region region : regions)
        {
            boundaries.addAll(region.boundaries);
        }
        return boundaries;
    }

//    /**
//     * Retrieve the unique flooding regions from the collection of regions.
//     * 
//     * @param regions
//     *            a list of regions
//     * @return the list if unique global flooding regions from the regions
//     */
//    private static final ArrayList<Basin> floodingBasins(ArrayList<Basin> regions)
//    {
//        ArrayList<Basin> floodingRegions = new ArrayList<>(regions.size());
//        for (Basin region : regions)
//        {
//            Basin flooding = region.globalFloodingBasin();
//            if (!floodingRegions.contains(flooding))
//            {
//                floodingRegions.add(flooding);
//            }
//        }
//        return floodingRegions;
//    }
    
    private static final double largestMinValue(Collection<? extends Region> regions)
    {
        double maxDepth = Double.NEGATIVE_INFINITY;
        for (Region region : regions)
        {
            maxDepth = Math.max(maxDepth, region.minValue);
        }
        return maxDepth;
    }
    
    private static final double lowestMinValue(Collection<? extends Region> regions)
    {
        double minDepth = Double.POSITIVE_INFINITY;
        for (Region region : regions)
        {
            minDepth = Math.min(minDepth, region.minValue);
        }
        return minDepth;
    }
    
    /***
     * Finds the basin with the highest minimum value.
     * 
     * @param basins
     *            the list of basins
     * @return the basin within the list with the highest minimum value
     */
    private static final Basin highestBasin(Collection<Basin> basins)
    {
        double maxDepth = Double.NEGATIVE_INFINITY;
        Basin highestBasin = null;
        for (Basin basin : basins)
        {
            if (basin.minValue > maxDepth)
            {
                highestBasin = basin;
                maxDepth = basin.minValue;
            }
        }
        return highestBasin;
    }
    
//    private static final Basin findLowestRegion(ArrayList<Basin> regions)
//    {
//        double minDepth = Double.MAX_VALUE;
//        Basin lowestRegion = null;
//        for (Basin region : regions)
//        {
//            double depth = region.minValue;
//            if (depth < minDepth)
//            {
//                minDepth = depth;
//                lowestRegion = region;
//            }
//        }
//        return lowestRegion;
//    }
    
    private static final String regionLabelsString(Collection<? extends Region> regions)
    {
        String res = "";
        Iterator<? extends Region> iter = regions.iterator();
        if (iter.hasNext())
        {
            res += iter.next().label;
        }
        while (iter.hasNext())
        {
            res += ", " + iter.next().label;
        }
        return res;
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
    
    
    public abstract class Region implements Comparable<Region>
    {
        /** The label associated to this region. Initialized at creation. */
        int label;
        
        /** The list of boundaries around this region. */
        ArrayList<Boundary> boundaries; 
        
        /** The minimum value within the region. Initialized at creation. */
        double minValue;
        
        /**
         * The dynamic of the region, given as the difference between minimum
         * value within region and either the minimum value of the boundary
         * regions (for basin regions), or the minimum value of the bounded
         * regions (for boundary regions)
         */
        double dynamic = Double.POSITIVE_INFINITY;
        
//        /**
//         * The Basin that floods this region. Updated during "merge" process.
//         */
//        Basin floodingBasin = null;
        
        /**
         * @return the list of basins this region contains.
         */
        public abstract Collection<Basin> basins();
        
        /**
         * Returns the boundary from this region that bounds the specified
         * regions, or null if no such boundary exist.
         * 
         * @param basins
         *            a list of (at least two) basins that define a boundary
         * @return the boundary that bounds the given basins, or null if no
         *         such boundary exist
         */
        public Boundary findBoundary(Collection<Basin> basins)
        {
            // for each boundary, check if regions match the specified ones
            for (Boundary boundary : this.boundaries)
            {
                if (boundary.hasSameBasins(basins))
                {
                    return boundary;
                }
            }
            
            // if no boundary was found, return null
            return null;
        }


        @Override
        public int compareTo(Region region)
        {
            return Double.compare(this.dynamic, region.dynamic);
        }
    }

    /**
     * A Basin region, corresponding to one of the regional minima within input
     * array.
     */
    public class Basin extends Region
    {
        public Basin(int label, double value)
        {
            this.label = label;
            this.minValue = value;
            
            // average number of neighbor regions is 6
            this.boundaries = new ArrayList<Boundary>(6);
        }
        
        public double recomputeDynamic()
        {
            double maxPass = lowestMinValue(this.boundaries);
            this.dynamic = maxPass - this.minValue;
            return this.dynamic;
        }
        
        public Collection<Basin> basins()
        {
            return java.util.Arrays.asList(this);
        }
        
        @Override
        public String toString()
        {
            return String.format(Locale.ENGLISH, "Basin(label=%d, minValue=%f, dynamic=%f)", this.label, this.minValue, this.dynamic);
        }
    }

    /**
     * A boundary between two (or more) regions.
     * 
     * The bounded regions can be either basins, or the result of the merge of
     * several basins (as a MergeRegion instance).
     */
    public class Boundary extends Region
    {
        ArrayList<Region> regions;
        
        public Boundary(int label, double minValue, Collection<? extends Region> adjacentRegions)
        {
            this.label = label;
            this.minValue = minValue;
            
            this.regions = new ArrayList<>(adjacentRegions.size());
            this.regions.addAll(adjacentRegions);
            
            // we expect at most two boundaries, one for each extremity
            this.boundaries = new ArrayList<Boundary>(2);
            
            for (Region region : this.regions)
            {
                region.boundaries.add(this);
            }
        }
        
        public double recomputeDynamic()
        {
            double maxDepth = largestMinValue(this.regions);
            this.dynamic = this.minValue - maxDepth;
            return this.dynamic;
        }
        
        public boolean hasSameBasins(Collection<Basin> basinList)
        {
            if (this.regions.size() != basinList.size())
            {
                return false;
            }
            
            for (Region region : this.regions)
            {
                if (!basinList.contains(region))
                {
                    return false;
                }
            }
            return true;
        }
        
        /**
         * @return all the basins "bounded" by this boundary, including the
         *         basins within the merged regions.
         */
        public Collection<Basin> basins()
        {
            HashSet<Basin> res = new HashSet<Basin>();
            for (Region region : this.regions)
            {
                res.addAll(region.basins());
            }
            return res;
        }
        
        
        /**
         * @param regions
         *            a set of regions
         * @return true only if all the regions bounded by this boundary are
         *         contained within the collection
         */
        public boolean hasNoOtherRegionThan(Collection<Region> regions)
        {
            for (Region region : this.regions)
            {
                if (!regions.contains(region))
                {
                    return false;
                }
            }
            return true;
        }
        
        public boolean containsAll(Collection<Region> regions)
        {
            for (Region region : regions)
            {
                if (!this.regions.contains(region))
                {
                    return false;
                }
            }
            return true;
        }
        
        @Override
        public String toString()
        {
            String regionString = "{";
            Iterator<Region> iter = this.regions.iterator();
            if (iter.hasNext()) 
            {
                regionString += iter.next().label;
            }
            while (iter.hasNext())
            {
                regionString += ", " + iter.next().label;
            }
            regionString += "}";
            return String.format(Locale.ENGLISH, "Boundary(%d, %f, %s)", this.label, this.minValue, regionString);
        }
    }
    
    /**
     * The result of the merge of several regions. Comprises the pixels of the
     * enclosed basins, as well as the pixels on their mutual boundaries.
     */
    public class MergeRegion extends Region
    {
        ArrayList<Region> regions;
        
        /** the value at which the merge is performed */ 
        double mergeValue;
        
        /**
         * Creates a new MergeRegion. The value of minValue is initialized at
         * creation.
         * 
         * @param label
         *            a label (not used...)
         * @param mergeValue
         *            the height value at which the merge is performed (from the
         *            saddle point of the boundary)
         * @param regions
         *            the regions to merge.
         */
        public MergeRegion(int label, double mergeValue, Collection<Region> regions)
        {
            this.label = label;
            this.mergeValue = mergeValue;
            
            this.regions = new ArrayList<>(regions.size());
            this.regions.addAll(regions);
            
            this.boundaries = new ArrayList<Boundary>();
            
            // compute min value of the merge from min value of its regions
            this.minValue = Double.POSITIVE_INFINITY;
            for (Region region : regions)
            {
                this.minValue = Math.min(this.minValue, region.minValue);
            }
        }
        
        public Collection<Basin> basins()
        {
            HashSet<Basin> res = new HashSet<Basin>();
            for (Region region : this.regions)
            {
                res.addAll(region.basins());
            }
            return res;
        }
        
//        public boolean containsAll(ArrayList<Basin> basins)
//        {
//            for (Basin basin : basins)
//            {
//                if (!containsBasin(basin))
//                {
//                    return false;
//                }
//            }
//            return true;
//        }
        
//        public boolean containsBasin(Basin basin)
//        {
//            for (Region region : regions)
//            {
//                if (region.containsBasin(basin))
//                {
//                    return true;
//                }
//            }
//            return false;
//        }
        
        @Override
        public String toString()
        {
            String regionString = regionLabelsString(this.regions);
            return String.format(Locale.ENGLISH, "MergeRegion(label=%d, minValue=%f, dynamic=%f, regions={%s})", this.label, this.minValue, this.dynamic, regionString);
        }
    }
    
    public static final void main(String... args) throws IOException
    {
        String fileName = HierarchicalWatershed2D.class.getResource("/appleCells_crop_smooth_sub05.tif").getFile();
        
        TiffImageReader reader = new TiffImageReader(fileName);
        Image image = reader.readImage();
        
        UInt8Array2D array = UInt8Array2D.wrap(UInt8Array.wrap((ScalarArray<?>) image.getData()));

        HierarchicalWatershed2D algo = new HierarchicalWatershed2D();
        ConsoleAlgoListener.monitor(algo);
        
        UInt8Array2D result = UInt8Array2D.wrap(UInt8Array.wrap(algo.process(array)));
        
        Image resultImage = new Image(result, image);
        resultImage.show();
        
        System.out.println("finish.");
    }
}
