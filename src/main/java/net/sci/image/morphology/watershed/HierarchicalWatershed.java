/**
 * 
 */
package net.sci.image.morphology.watershed;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;

/**
 * Static class whose purpose is to encapsulate the classes used by
 * dimension-specific hierarchical watershed algorithms.
 * 
 * @author dlegland
 *
 */
public class HierarchicalWatershed
{
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
            else
            {
                throw new IllegalArgumentException("Input regions must be Basin or Boundary instances only.");
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
