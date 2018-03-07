package net.sci.image.io;

/**
 * TIFFBaseline
 *
 * @author <a href="mailto:harald.kuhr@gmail.com">Harald Kuhr</a>
 * @author last modified by $Author: haraldk$
 * @version $Id: TIFFBaseline.java,v 1.0 08.05.12 16:43 haraldk Exp$
 */
interface TIFFBaseline
{
    int COMPRESSION_NONE = 1;
    int COMPRESSION_CCITT_MODIFIED_HUFFMAN_RLE = 2;
    int COMPRESSION_PACKBITS = 32773;
    
    int PHOTOMETRIC_WHITE_IS_ZERO = 0;
    int PHOTOMETRIC_BLACK_IS_ZERO = 1;
    int PHOTOMETRIC_RGB = 2;
    int PHOTOMETRIC_PALETTE = 3;
    int PHOTOMETRIC_MASK = 4;
    
    int SAMPLEFORMAT_UINT = 1; // Spec says only UINT required for baseline
    
    int PLANARCONFIG_CHUNKY = 1;
    
    int EXTRASAMPLE_UNSPECIFIED = 0;
    int EXTRASAMPLE_ASSOCIATED_ALPHA = 1;
    int EXTRASAMPLE_UNASSOCIATED_ALPHA = 2;
    
    int PREDICTOR_NONE = 1;
    
    int RESOLUTION_UNIT_NONE = 1;
    int RESOLUTION_UNIT_DPI = 2; // Default
    int RESOLUTION_UNIT_CENTIMETER = 3;
    
    int FILL_LEFT_TO_RIGHT = 1; // Default
    
    // NOTE: These are bit flags that can be ORed together!
    int FILETYPE_REDUCEDIMAGE = 1;
    int FILETYPE_PAGE = 2;
    int FILETYPE_MASK = 4;
    
    int ORIENTATION_TOPLEFT = 1;
}