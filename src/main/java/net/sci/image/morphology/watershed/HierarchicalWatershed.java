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

/**
 * Static class whose purpose is to encapsulate the classes used by
 * dimension-specific hierarchical watershed algorithms.
 * 
 * @author dlegland
 *
 */
public class HierarchicalWatershed
{
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
     * @param regions
     *            a set of regions
     * @return the set of boundaries that are adjacent to at least one of
     *         the regions within the list.
     */
    @Deprecated
    public static final Collection<Boundary> adjacentBoundaries(Collection<Region> regions)
    {
        HashSet<Boundary> boundaries = new HashSet<>();
        for (Region region : regions)
        {
            boundaries.addAll(region.boundaries);
        }
        return boundaries;
    }
    
    /**
     * Utility class that determines if an element belongs to a basin by
     * considering the list of neighbor basins and boundaries.
     * 
     * Conditions for belonging to a basin:
     * <ul>
     * <li>number of neighbor basins equal to one,</li>
     * <li>if a boundary is present, it should be adjacent to the basin.</li>
     * </ul>
     */
    public static final boolean isBasinElement(Collection<Basin> basins, Collection<Boundary> boundaries)
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
        
        // if two boundaries are present, the new element is boundary.
        return false;
    }

    /**
     * Returns the lowest minimal value among the collection of regions.
     * Corresponds to the global minimum of the set of regions.
     * 
     * @param regions
     *            a collection of regions.
     * @return the lowest minimal value among the collection of regions.
     */
    public static final double lowestMinValue(Collection<? extends Region> regions)
    {
        double minDepth = Double.POSITIVE_INFINITY;
        for (Region region : regions)
        {
            minDepth = Math.min(minDepth, region.minValue);
        }
        return minDepth;
    }
    
    /**
     * Computes a string representation of the list of label of the regions
     * within the input collection.
     * 
     * @param regions
     *            a list of regions
     * @return a string representation of region labels, separated by commas.
     */
    public static final String regionLabelsString(Collection<? extends Region> regions)
    {
        StringBuilder sb = new StringBuilder();
        Iterator<? extends Region> iter = regions.iterator();
        if (iter.hasNext())
        {
            sb.append(iter.next().label);
        }
        while (iter.hasNext())
        {
            sb.append(", ").append(iter.next().label);
        }
        return sb.toString();
    }
    
    /**
     * Computes a string-based representation of the merge tree based on an
     * initial region.
     * 
     * @param region
     *            the initial region for computing the tree.
     * @return a string-based representation of the merge tree from the input
     *         region.
     */
    public static final String printRegionTree(Region region)
    {
        StringBuilder sb = new StringBuilder();
        appendRegionTree(sb, region, 0);
        return sb.toString();
    }
    
    private static final void appendRegionTree(StringBuilder sb, Region region, int indentLevel)
    {
        if (indentLevel > 0)
        {
            sb.append(". ".repeat(indentLevel));
        }
        
        sb.append("+ " + region.toString() + "\n");
        if (region instanceof MergeRegion)
        {
            for (Region child : ((MergeRegion) region).regions)
            {
                appendRegionTree(sb, child, indentLevel+1);
            }
        }
    }
    
    /**
     * The adjacency graph of regions within the watershed.
     * 
     * Nodes correspond to basins. Edges correspond to boundaries, but
     * boundaries may be adjacent to more than two basins. This structure
     * therefore corresponds to a Hypergraph.
     */
    public static class Graph
    {
        /**
         * The basins of the watershed, indexed by an integer label.
         */
        HashMap<Integer, Basin> basins = new HashMap<Integer, Basin>();
        
        /**
         * The boundaries of the watershed, indexed by an integer label. As
         * boundaries are processed after initialization of basins, the first
         * boundary label is larger than the last basin label.
         */
        HashMap<Integer, Boundary> boundaries = new HashMap<Integer, Boundary>();
        
        /**
         * Map the index of a region (basin or boundary) to the list of indices
         * of its boundary regions.
         */
        HashMap<Integer, ArrayList<Integer>> regionBoundaries = new HashMap<>();
        
        /**
         * Map the index of a boundary to the list of indices of the regions it
         * is adjacent to.
         */
        HashMap<Integer, ArrayList<Integer>> boundaryRegions = new HashMap<>();
        
        
        // ==============================================================
        // General methods
        
        /**
         * Computes the minimum spanning tree of this watershed graph, used as
         * weights the dynamic associated to each boundary.
         * 
         * @return a new Graph with same basins and the boundaries that span the
         *         original graph with minimum weight
         */
        public Graph minimumSpanningTree()
        {
            Graph tree = new Graph();
            
            // initialize with an arbitrary basin
            Basin firstBasin = this.basins.values().iterator().next();
//            tree.basins.put(firstBasin.label, firstBasin);
            tree.addBasin(firstBasin);
//            // keep list of remaining basins (?)
//            HashSet<Integer> remainingBasinLabels = new HashSet<Integer>(this.basins.size()-1);
//            for (int label : this.basins.keySet())
//            {
//                if (label != firstBasin.label)
//                {
//                    remainingBasinLabels.add(label);
//                }
//            }
            
            // Create list of candidate edges / boundaries:
            // they must link at least one basin from the tree and one remaining basin
            PriorityQueue<Boundary> boundaryQueue = new PriorityQueue<Boundary>();
            
            for (Boundary boundary : getRegionBoundaries(firstBasin.label))
            {
                boundaryQueue.add(boundary);
            }
            System.out.println("initial queue size: " + boundaryQueue.size());
            
            
            while (tree.basins.size() < this.basins.size())
            {
                // choose candidate boundary with lowest dynamic
                Boundary boundary = boundaryQueue.poll();
                
                // identifies a basin not yet in tree
                Basin nextBasin = null;
                for (Basin basin : boundary.basins())
                {
                    if (!tree.basins.containsKey(basin.label))
                    {
                        nextBasin = basin;
                        break;
                    }
                }
                
                // if all basins were already processed, the boundary does not belong to the tree
                if (nextBasin == null)
                {
                    continue;
                }
                
                // otherwise we can add the basin
                //                tree.basins.put(nextBasin.label, nextBasin);
                tree.addBasin(nextBasin);

                // also add the boundary to the tree
                //              tree.boundaries.put(boundary.label, boundary);
                tree.addBoundary(boundary);
              
                
                // and we can enqueue all the boundaries that refer to a remaining basin
                for (Boundary bnd : getRegionBoundaries(nextBasin.label))
                {
                    for (Basin basin : bnd.basins())
                    {
                        if (!tree.basins.containsKey(basin.label))
                        {
                            boundaryQueue.add(bnd);
                            break;
                        }
                    }
                }
            }
            
            return tree;
        }
        
        /**
         * Adds a boundary, by updating the necessary hashmaps.
         * 
         * @param boundary
         *            the boundary to add. The basins are supposed to already
         *            belong to the graph.
         */
        private void addBoundary(Boundary boundary)
        {
            this.boundaries.put(boundary.label, boundary);
            Collection<Basin> basins = boundary.basins(); 
            this.boundaryRegions.put(boundary.label, new ArrayList<Integer>(basins.size()));
            
            for (Basin basin : basins)
            {
                if (!this.basins.containsKey(basin.label))
                {
                    System.err.println("Adding boundary #" + boundary.label + ", but basin #" + basin.label + " do not belong to graph...");
                    continue;
                }
                
                regionBoundaries.get(basin.label).add(boundary.label);
                boundaryRegions.get(boundary.label).add(basin.label);
            }
        }
        
        /**
         * Adds the basin, and initializes corresponding hashmap.
         * 
         * @param basin
         *            the basin to add
         */
        private void addBasin(Basin basin)
        {
            this.basins.put(basin.label, basin);
            this.regionBoundaries.put(basin.label, new ArrayList<Integer>(4));
        }
        
        
        // ==============================================================
        // General management of regions
        
        /**
         * Retrieve a region from its label. The result may be either a Basin
         * region or a Boundary region.
         * 
         * @param label
         *            the label of the region
         * @return the region corresponding to the label.
         */
        public Region getRegion(int label)
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
        
        
        // ==============================================================
        // Management of boundaries
        
        /**
         * Creates a new Boundary region with a specified label, a minimum
         * value, and a set of adjacent regions.
         * 
         * @param label
         *            the label of the new boundary region.
         * @param minValue
         *            the minimum value on the boundary, usually found at saddle
         *            pixels.
         * @param basins
         *            the collection of adjacent regions (usually basins, but
         *            may contain other boundaries as well)
         * @return the new Boundary
         */
        public Boundary createBoundary(int label, double minValue, Collection<? extends Region> basins)
        {
            // create a new boundary instance, leaving dynamic not yet initialized
            Boundary boundary = new Boundary(label, minValue, basins);
            
            // compute the dynamic of the new boundary, using the minimum value
            // on the boundary, and the basins with the highest minimum value.
            Basin highestBasin = highestBasin(boundary.basins());
            boundary.dynamic = minValue - highestBasin.minValue;
            highestBasin.dynamic = boundary.dynamic;
            
            this.boundaries.put(label, boundary);
            
            // update the basins->boundary mapping
            // for each region bounded by this boundary, add a reference to this
            // boundary label.
            for (Region region : basins)
            {
                this.regionBoundaries.get(region.label).add(label);
            }
            
            // also update the boundary->basins mapping
            ArrayList<Integer> basinIndices = new ArrayList<Integer>(boundary.basins().size());
            for (Basin basin : boundary.basins())
            {
                basinIndices.add(basin.label);
            }
            this.boundaryRegions.put(label, basinIndices);
        
            return boundary;
        }
        
        /**
         * @param regions
         *            a set of regions
         * @return the set of boundaries that are adjacent to at least one of
         *         the regions within the list.
         */
        public Collection<Boundary> adjacentBoundaries(Collection<Region> regions)
        {
            HashSet<Boundary> boundaries = new HashSet<>();
            for (Region region : regions)
            {
                boundaries.addAll(getRegionBoundaries(region.label));
            }
            return boundaries;
        }


        /**
         * Returns the boundary that bounds the specified regions, or null if no
         * such boundary exist. The search is performed on the boundary of an
         * arbitrary region from the specified collection.
         * 
         * @param basins
         *            a list of regions (two or more) that define a boundary
         * @return the boundary that bounds the given regions, or null if no
         *         such boundary exist
         */
        public Boundary getBoundary(Collection<Basin> basins)
        {
            if (basins.isEmpty())
            {
                return null;
            }
            
            // retrieve the boundaries of an arbitrary basin
            Basin basin = basins.iterator().next();
            
            // for each boundary, check if regions match the specified ones
            for (Boundary boundary : getRegionBoundaries(basin.label))
            {
                if (boundary.hasSameBasins(basins))
                {
                    return boundary;
                }
            }
            
            // if no boundary was found, return null
            return null;
        }
        
        private Collection<Boundary> getRegionBoundaries(int regionLabel)
        {
            ArrayList<Integer> labels = regionBoundaries.get(regionLabel);
            ArrayList<Boundary> res = new ArrayList<Boundary>(labels.size());
            for (int label : labels)
            {
                res.add(boundaries.get(label));
            }
            return res;
        }
        
        public Collection<Region> getBoundaryRegions(Boundary boundary)
        {
            ArrayList<Integer> labels = boundaryRegions.get(boundary.label);
            ArrayList<Region> regions = new ArrayList<>(labels.size());
            for (int label : labels)
            {
                regions.add(getRegion(label));
            }
            return regions;
        }

        // ==============================================================
        // Management of basins
       
        /**
         * Retrieves all the regions that are instances of Basin.
         * 
         * @see Basin
         * 
         * @param regions
         *            a collection of regions, that should be instances of either
         *            Basin or Boundary.
         * @return the regions within the collection that are instances of Basin.
         */
        public Collection<Basin> findAllBasins(Collection<Region> regions)
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
                    basins.addAll(findAllBasins(getBoundaryRegions((Boundary) region)));
                }
                else
                {
                    throw new IllegalArgumentException("Input regions must be Basin or Boundary instances only.");
                }
            }
            return basins;
        }

        public boolean hasBasin(int label)
        {
            return this.basins.containsKey(label);
        }
        
        public Basin createNewBasin(int label, double minValue)
        {
            Basin basin = new Basin(label, minValue);
            this.basins.put(label, basin);
            this.regionBoundaries.put(label, new ArrayList<Integer>(4));
            return basin;
        }

        /**
         * Recomputes the dynamic associated to this basin.
         * 
         * @param basin
         *            the basin to update
         */
        public void recomputeDynamic(Basin basin)
        {
            double maxPass = lowestMinValue(basin.boundaries);
            basin.dynamic = maxPass - basin.minValue;
        }
        
         @Override
        public String toString()
        {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("Watershed graph with %d basin and %d boundaries\n", basins.size(), boundaries.size()));
            sb.append("Basins:\n");
            for (Basin basin : this.basins.values())
            {
                sb.append("  " + basin.toString() + "\n");
            }
            sb.append("Boundaries:\n");
            for (Boundary boundary : this.boundaries.values())
            {
                sb.append("  " + boundary.toString() + "\n");
            }
            return sb.toString();
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
        
        /**
         * As this instance is a basin, returns a collection containing only
         * this basin.
         * 
         * @return a collection containing only this basin.
         */
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
         * The collection of regions adjacent to this boundary. Contains usually
         * basins, but may contain other boundaries as well (case of multiple
         * boundary points).
         */
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
            String regionString = regionLabelsString(this.regions);
            return String.format(Locale.ENGLISH, "Boundary(label=%d, minValue=%f, regions={%s})", this.label, this.minValue, regionString);
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
            this.minValue = HierarchicalWatershed.lowestMinValue(regions);
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
