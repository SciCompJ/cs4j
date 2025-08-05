/**
 * 
 */
package net.sci.image.regionfeatures;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.sci.algo.Algo;
import net.sci.algo.AlgoEvent;
import net.sci.algo.AlgoListener;
import net.sci.algo.AlgoStub;
import net.sci.array.Array;
import net.sci.array.color.ColorMap;
import net.sci.array.color.ColorMaps;
import net.sci.array.numeric.Int;
import net.sci.array.numeric.IntArray;
import net.sci.axis.CategoricalAxis;
import net.sci.image.Image;
import net.sci.image.label.LabelImages;
import net.sci.table.Table;

/**
 * The main class of the plugin, that gathers all the information necessary to
 * analyze image as well as the results. The class contains:
 * <ul>
 * <li>a reference to the label map representing the regions to analyze</li>
 * <li>the list of features to analyze</li>
 * <li>for each feature, the result of computation</li>
 * <li>general options for computing features and presenting the results</li>
 * </ul>
 */
public class RegionFeatures extends AlgoStub
{
    // ==================================================
    // Static methods
    
    public static final RegionFeatures initialize(Image labelMapImage)
    {
        Array<?> array = labelMapImage.getData();
        
        if (!Int.class.isAssignableFrom(array.elementClass()))
        {
            throw new RuntimeException("Requires an image containing an instance of IntArray");
        }
        
        // cast to Int Array
        @SuppressWarnings({ "unchecked", "rawtypes" })
        IntArray<?> intArray = IntArray.wrap((Array<? extends Int>) array);
        return new RegionFeatures(labelMapImage, LabelImages.findAllLabels(intArray));
    }
    
    public static final RegionFeatures initialize(Image image, int[] labels)
    {
        return new RegionFeatures(image, labels);
    }
    
    
    // ==================================================
    // Class members
    
    /**
     * The image containing the map of region label for each pixel / voxel.
     */
    public Image labelMap;
    
    /**
     * The labels of the regions to be analyzed.
     */
    public int[] labels;
    
    /**
     * The classes of the features that will be used to populate the data table.
     */
    Collection<Class<? extends Feature>> featureClasses = new ArrayList<>();
    
    /**
     * The map of features indexed by their class. When feature is created for
     * the first time, it is indexed within the results class to retrieve it in
     * case it is requested later.
     */
    public Map<Class<? extends Feature>, Feature> features;
    
    /**
     * A map for storing optional data that can be used to compute additional
     * features, for example region intensities.
     */
    public Map<String, Image> imageData;
    
    /**
     * The results computed for each feature. 
     */
    public Map<Class<? extends Feature>, Object> results;
    
    public Color[] labelColors;
    
    
    // ==================================================
    // Constructors
    
    public RegionFeatures(Image labelMapImage, int[] labels)
    {
        // store locally label map data
        this.labelMap = labelMapImage;
        this.labels = labels;
        
        // initialize data structures
        this.features = new HashMap<Class<? extends Feature>, Feature>();
        this.imageData = new HashMap<String, Image>();
        this.results = new HashMap<Class<? extends Feature>, Object>();
        
        // additional setup
        createLabelColors(this.labels.length);
    }
    
    private void createLabelColors(int nLabels)
    {
        ColorMap lut = ColorMaps.GLASBEY.createColorMap(nLabels);
        this.labelColors = new Color[nLabels];
        for (int i = 0; i < nLabels; i++)
        {
            this.labelColors[i] = awtColor(lut.getColor(i));
        }
    }
    
    private static final java.awt.Color awtColor(net.sci.array.color.Color color)
    {
        return new java.awt.Color((float) color.red(), (float) color.green(), (float) color.blue());
    }
    
    
    // ==================================================
    // Processing methods
    
    /**
     * Updates the informations stored within this result class with the feature
     * identified by the specified class, if it is not already computed.
     * 
     * @param featureClass
     *            the class to compute
     */
    public void process(Class<? extends Feature> featureClass)
    {
        if (isComputed(featureClass)) return;
        
        Feature feature = getFeature(featureClass);
        ensureRequiredFeaturesAreComputed(feature);
        
        // compute feature, and index into results
        this.fireStatusChanged(this, "Compute feature: " + featureClass.getSimpleName());
        
        // propagate algorithm event of feature to the RegionFeature listeners
        if (feature instanceof Algo)
        {
            ((Algo) feature).addAlgoListener(new AlgoListener() {

                @Override
                public void algoProgressChanged(AlgoEvent evt)
                {
                    fireProgressChanged(evt);
                }

                @Override
                public void algoStatusChanged(AlgoEvent evt)
                {
                    fireStatusChanged(evt);
                }
            });
        }
        
        // store within the results class
        this.results.put(featureClass, feature.compute(this));
    }
    
    public boolean isComputed(Class<? extends Feature> featureClass)
    {
        return results.containsKey(featureClass);
    }
    
    public Feature getFeature(Class<? extends Feature> featureClass)
    {
        Feature feature = this.features.get(featureClass);
        if (feature == null)
        {
            feature = Feature.create(featureClass);
            this.features.put(featureClass, feature);
        }
        return feature;
    }
    
    public void ensureRequiredFeaturesAreComputed(Feature feature)
    {
        feature.requiredFeatures().stream()
            .filter(fc -> !isComputed(fc))
            .forEach(fc -> process(fc));
    }

    public RegionFeatures add(Class<? extends Feature> featureClass)
    {
        this.featureClasses.add(featureClass);
        return this;
    }
    
    public boolean contains(Class<? extends Feature> featureClass)
    {
        return this.featureClasses.contains(featureClass);
    }
    
    public RegionFeatures addImageData(String dataName, Image image)
    {
        this.imageData.put(dataName, image);
        return this;
    }
    
    public Image getImageData(String dataName)
    {
        return this.imageData.get(dataName);
    }
    
    public RegionFeatures computeAll()
    {
        this.featureClasses.stream().forEach(this::process);
        return this;
    }
    
    /**
     * Creates a new results table from the different features contained within
     * the class.
     * 
     * @return a new Table containing a summary of the computed features.
     */
    public Table createTable()
    {
        // ensure everything is computed
        this.fireStatusChanged(this, "RegionFeatures: compute all features");
        computeAll();
        
        this.fireStatusChanged(this, "RegionFeatures: create result tables");
        Table fullTable = initializeRegionTable();
        
        // update the global table with each feature
        for (Class<? extends Feature> featureClass : this.featureClasses)
        {
            if (!isComputed(featureClass))
            {
                throw new RuntimeException("Feature has not been computed: " + featureClass);
            }
            
            Feature feature = getFeature(featureClass);
            if (feature instanceof RegionTabularFeature tabularFeature)
            {
                tabularFeature.updateTable(fullTable, this);
            }
        }
        
        return fullTable;
    }
    
//    /**
//     * Returns an array containing two Tables: one with the feature
//     * results, another one containing the unit associated to each column in the
//     * first table.
//     * 
//     * @return an array of two Table.
//     */
//    public Table[] createTables()
//    {
//        // ensure everything is computed
//        this.fireStatusChanged(this, "RegionFeatures: compute all features");
//        computeAll();
//        
//        this.fireStatusChanged(this, "RegionFeatures: create result tables");
//        Table fullTable = initializeRegionTable();
//        ColumnsTable columnUnitsTable = new ColumnsTable();
//        
//        // update the global table with each feature
//        for (Class<? extends Feature> featureClass : this.featureClasses)
//        {
//            if (!isComputed(featureClass))
//            {
//                throw new RuntimeException("Feature has not been computed: " + featureClass);
//            }
//            
//            Feature feature = getFeature(featureClass);
//            if (feature instanceof RegionTabularFeature tabularFeature)
//            {
//                tabularFeature.updateTable(fullTable, this);
////                // create table associated to feature
////                Table table = tabularFeature.createTable(this);
////                for (Column col : table.columns())
////                {
////                    fullTable.addColumn(col);
////                }
//            }
//        }
//        
//        return new Table[] {fullTable, columnUnitsTable};
//    }
    
    public Table initializeRegionTable()
    {
        // Initialize label column in table
        int nLabels = this.labels.length;
        Table table = Table.create(nLabels, 0);
        String[] rowNames = new String[nLabels];
        for (int i = 0; i < this.labels.length; i++)
        {
            rowNames[i] = "" + this.labels[i];
        }
        table.setRowAxis(new CategoricalAxis("Label", rowNames));
        return table;
    }

    public void printComputedFeatures()
    {
        results.keySet().stream().forEach(c -> System.out.println(c.getSimpleName()));
    }
}
