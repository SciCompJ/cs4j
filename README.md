# cs4j
C4J is a computer sciences library for java, whose initial design goal is image processing and analysis.
The library provides several features:
* management of multi-dimensional arrays, whose elements can be of any type
* algorithms for array processing: crop, slice, conversion...
* management of numerical arrays
* algorithms for image processing and analysis
* management of data tables
* a basic geometry kernel for 2D/3D geometric computing

The library is composed of several modules with clear perimeters.
The number of dependencies is kept as low as possible. 

The library is at the basis of the development of the [Imago application](https://github.com/SciCompJ/Imago).

## Installation

Simply clone the project, or download the latest version.

The managment of dependencies is performed by maven (see the pom.xml file).

## Quick example

Below is an example application that demonstrate several features of the library: 
image import, image filtering and segmentation, connected component labeling,
computation of morphometric features from label map, save results table as text file.
Note that the imports are not explicited.

```java
public static final void main(String... args) throws IOException
{
    System.out.println( "Start CS4J image processing demo." );

    System.out.println("Read input image");
    String fileName = ProcessRiceGrains.class.getResource("/grains.png").getFile();
    Image image = Image.readImage(new File(fileName));
        
    // get image data, by using direct class cast
    UInt8Array2D array = (UInt8Array2D) image.getData();
        
    // retrieve image data size
    int sizeX = array.size(0);
    int sizeY = array.size(1);
    System.out.println("  image size: " + sizeX + "x" + sizeY);
        
    // apply top-hat filtering
    WhiteTopHat topHat = new WhiteTopHat(Strel2D.Shape.SQUARE.fromDiameter(31));
    UInt8Array2D arrayWTH = (UInt8Array2D) topHat.process(array);
        
    // median filtering
    BoxMedianFilter filter = new BoxMedianFilter(new int[] {3, 3});
    UInt8Array2D filteredArray = (UInt8Array2D) filter.process(arrayWTH);
        
    // segment by using Otsu threshold
    BinaryArray2D segmentedArray = BinaryArray2D.wrap(new OtsuThreshold().process(filteredArray));
       
    // compute connected component labeling
    UInt8Array2D grainLabels = (UInt8Array2D) BinaryImages.componentsLabeling(segmentedArray, 4, 8);
      
    // create new image from parent image to keep spatial calibration
    Image grainLabelImage = new Image(grainLabels, image);
    
    // compute morphometry from calibrated label map
    Table resTable = new EquivalentEllipse2D().computeTable(grainLabelImage);
   
    // save results into a text file
    new DelimitedTableWriter().writeTable(resTable, new File("grain-ellipses.tsv"));
}
```
