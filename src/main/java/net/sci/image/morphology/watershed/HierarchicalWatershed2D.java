/**
 * 
 */
package net.sci.image.morphology.watershed;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
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
    
    /**
     * A Region of the watershed. Each region is characterized by its label, its
     * minimum value, its dynamic, and other fileds depending on concrete classes.
     * 
     * During flooding process, a region can be either a Basin or a Boundary.
     * During merge process, basins are hierarchically aggregated into
     * MergeRegion instances.
     */
    public static abstract class Region implements Comparable<Region>
    {
        public static final double lowestMinValue(Collection<? extends Region> regions)
        {
            double minDepth = Double.POSITIVE_INFINITY;
            for (Region region : regions)
            {
                minDepth = Math.min(minDepth, region.minValue);
            }
            return minDepth;
        }

        public static final String regionLabelsString(Collection<? extends Region> regions)
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
        public int compareTo(Region other)
        {
            return Double.compare(this.dynamic, other.dynamic);
        }
    }

    /**
     * A Basin region, corresponding to one of the regional minima within input
     * array.
     */
    public static class Basin extends Region
    {
        public static final Collection<Basin> findAllBasins(Collection<Region> regions)
        {
            HashSet<Basin> basins = new HashSet<>();
            for (Region region : regions)
            {
                if (region instanceof Basin)
                {
                    basins.add((Basin) region);
                }
                else if (region instanceof Boundary)
                {
                    basins.addAll(findAllBasins(((Boundary) region).regions));
                }
            }
            return basins;
        }

        /***
         * Finds the basin with the highest minimum value.
         * 
         * @param basins
         *            the list of basins
         * @return the basin within the list with the highest minimum value
         */
        public static final Basin highestBasin(Collection<Basin> basins)
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

        /**
         * Creates a new Basin
         * 
         * @param label
         *            the label associated to this basin
         * @param value
         *            the minimum value within the basin
         */
        public Basin(int label, double value)
        {
            this.label = label;
            this.minValue = value;
            
            // average number of neighbor regions is 6
            this.boundaries = new ArrayList<Boundary>(6);
        }
        
        /**
         * Recomputes the dynamic associated to this basin.
         */
        public void recomputeDynamic()
        {
            double maxPass = lowestMinValue(this.boundaries);
            this.dynamic = maxPass - this.minValue;
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
    public static class Boundary extends Region
    {
        
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
        public static final Boundary getBoundary(Collection<Basin> adjRegions)
        {
            if (adjRegions.isEmpty())
            {
                return null;
            }
            
            // check the boundaries of an arbitrary region
            return adjRegions.iterator().next().findBoundary(adjRegions);
        }
        
        /**
         * @param regions
         *            a set of regions
         * @return the set of boundaries that are adjacent to at least one of
         *         the regions within the list.
         */
        public static final Collection<Boundary> adjacentBoundaries(Collection<Region> regions)
        {
            HashSet<Boundary> boundaries = new HashSet<>();
            for (Region region : regions)
            {
                boundaries.addAll(region.boundaries);
            }
            return boundaries;
        }

        ArrayList<Region> regions;
        
        /**
         * Creates a new Boundary.
         * 
         * @param label
         *            the label associated to this basin
         * @param minValue
         *            the minimum value within the basin
         * @param adjacentRegions
         *            the set of regions the boundary bounds.
         */
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
        
        /**
         * @param basinList
         *            a list of basins
         * @return true if this boundary bounds exactly the same basins as the
         *         ones specified in the list
         */
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
    public static class MergeRegion extends Region
    {
        ArrayList<Region> regions;
        
        /**
         * Creates a new MergeRegion. The value of minValue is initialized at
         * creation.
         * 
         * @param label
         *            the label of the region (used for debug)
         * @param regions
         *            the regions to merge.
         */
        public MergeRegion(int label, Collection<Region> regions)
        {
            this.label = label;
            
            this.regions = new ArrayList<>(regions.size());
            this.regions.addAll(regions);
            
            this.boundaries = new ArrayList<Boundary>();
            
            // compute min value of the merge from min value of its regions
            this.minValue = Region.lowestMinValue(regions);
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
        
        @Override
        public String toString()
        {
            String regionString = regionLabelsString(this.regions);
            return String.format(Locale.ENGLISH, "MergeRegion(label=%d, minValue=%f, dynamic=%f, regions={%s})", this.label, this.minValue, this.dynamic, regionString);
        }
    }
}
