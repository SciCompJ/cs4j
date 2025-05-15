/**
 * 
 */
package net.sci.image.io.tiff;

/**
 * The metadata that can be read from a Tiff File as saved by ImageJ.
 * 
 * @see ImageJTags.ImageJMetaData
 */
public class ImagejMetadata
{
    public String info = null;
    
    public String[] sliceLabels = null;
    
    public double[] displayRanges = null;
    
    public byte[][] channelLuts = null;
    
    public byte[] roiData = null;

    public byte[] plotData = null;

    public byte[][] overlayData = null;
    
    public String[] properties = null;
    
    public int[] extraMetadataTypes = null;
    public byte[][] extraMetadata = null;
    
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder("ImageJ metadata\n");
        
        if (this.info != null) sb.append("  Info property:\n").append(this.info);
        
        if (this.sliceLabels != null)
        {
            sb.append("  Slice labels:\n");
            for (String label : this.sliceLabels)
                sb.append("   - " + label + "\n");
        }
        
        if (this.displayRanges != null)
        {
            sb.append("  Display ranges:");
            for (double v : this.displayRanges)
                sb.append(" " + v);
            sb.append("\n");
        }
        
        if (this.channelLuts != null)
        {
            sb.append("  (contains LUT)\n");
        }
        
        if (this.plotData != null)
        {
            sb.append("  (contains Plot)\n");
        }
        
        if (this.roiData != null)
        {
            sb.append("  (contains ROI)\n");
        }
        
        if (this.overlayData != null)
        {
            sb.append("  (contains Overlay)\n");
        }
        
        if (this.properties != null)
        {
            sb.append("  Properties:\n");
            for (String label : this.properties)
                sb.append("   - " + label + "\n");
        }
        
        if (this.extraMetadata != null && this.extraMetadata.length > 0)
        {
            sb.append("  (contains extra metadata): " + this.extraMetadata .length + "\n");
        }
        
        return sb.toString();
    }
}
