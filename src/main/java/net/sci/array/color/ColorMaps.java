/**
 * 
 */
package net.sci.array.color;

import java.util.ArrayList;

/**
 * A collection of color maps.
 * @author dlegland
 */
public class ColorMaps
{
    public static final ColorMapFactory RED = new ColorMapFactory()
    {
        @Override
        public ColorMap createColorMap(int nColors)
        {
            ArrayList<Color> colors = new ArrayList<Color>(nColors);
            for (int i = 0; i < nColors; i++)
            {
                colors.add(new RGB8(i * 255.0 / nColors, 0, 0));
            }
            return new DefaultColorMap(colors);
        }
    };
    
    public static final ColorMapFactory GREEN = new ColorMapFactory()
    {
        @Override
        public ColorMap createColorMap(int nColors)
        {
            ArrayList<Color> colors = new ArrayList<Color>(nColors);
            for (int i = 0; i < nColors; i++)
            {
                colors.add(new RGB8(0, i * 255.0 / nColors, 0));
            }
            return new DefaultColorMap(colors);
        }
    };
    
    public static final ColorMapFactory BLUE = new ColorMapFactory()
    {
        @Override
        public ColorMap createColorMap(int nColors)
        {
            ArrayList<Color> colors = new ArrayList<Color>(nColors);
            for (int i = 0; i < nColors; i++)
            {
                colors.add(new RGB8(0, 0, i * 255.0 / nColors));
            }
            return new DefaultColorMap(colors);
        }
    };
    
    public static final ColorMapFactory GRAY = new ColorMapFactory()
    {
        @Override
        public ColorMap createColorMap(int nColors)
        {
            ArrayList<Color> colors = new ArrayList<Color>(nColors);
            for (int i = 0; i < nColors; i++)
            {
                int gray = (int) (i * 256.0 / nColors + 0.5);
                colors.add(new RGB8(gray, gray, gray));
            }
            return new DefaultColorMap(colors);
        }
    };

    public static final ColorMapFactory BLUE_GRAY_RED = new ColorMapFactory()
    {
        @Override
        public ColorMap createColorMap(int nColors)
        {
            ArrayList<Color> colors = new ArrayList<Color>(nColors);
            colors.add(new RGB8(0, 0, 255));
            for (int i = 1; i < nColors-1; i++)
            {
                int gray = (int) (i * 255.0 / nColors);
                colors.add(new RGB8(gray, gray, gray));
            }
            colors.add(new RGB8(255, 0, 0));
            return new DefaultColorMap(colors);
        }
    };
    
    public static final ColorMapFactory JET = new ColorMapFactory()
    {
        @Override
        public ColorMap createColorMap(int nColors)
        {
            ArrayList<Color> colors = createJetColors();
            ColorMap baseMap = new DefaultColorMap(colors);
            if (nColors == 256)
                return baseMap;
            else
                return interpolate(baseMap, nColors);
        }
        
        private ArrayList<Color> createJetColors() 
        {
            // create map
            ArrayList<Color> map = new ArrayList<Color>(256);
            
            // shade of dark blue to blue
            for (int i = 0; i < 32; i++)
            {
                map.add(new RGB8(0, 0, 127 + i * 4));
            }
            for (int i = 32; i < 96; i++) 
            { 
                map.add(new RGB8(0, (i - 32) * 4, 255));
            }
            for (int i = 96; i < 160; i++) 
            { 
                map.add(new RGB8((i - 96) * 4, 255, 255 - (i - 96) * 4));
            }
            for (int i = 160; i < 224; i++) 
            { 
                map.add(new RGB8(255, 255 - (i - 160) * 4, 0));
            }
            for (int i = 224; i < 256; i++) 
            { 
                map.add(new RGB8(255 - (i - 224) * 4, 0, 0));
            }
            return map;
        }
    };
    
    public static final ColorMapFactory FIRE = new ColorMapFactory()
    {
        @Override
        public ColorMap createColorMap(int nColors)
        {
            ColorMap map = new DefaultColorMap(createColors());
            if (nColors != map.size())
            {
                map = interpolate(map, nColors);
            }
            return map;
        }

        private ArrayList<Color> createColors() 
        {
            // initial values
            int[] r = { 0, 0, 1, 25, 49, 73, 98, 122, 146, 162, 173, 184, 195, 207,
                    217, 229, 240, 252, 255, 255, 255, 255, 255, 255, 255, 255,
                    255, 255, 255, 255, 255, 255 };
            int[] g = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 14, 35, 57, 79, 101,
                    117, 133, 147, 161, 175, 190, 205, 219, 234, 248, 255, 255,
                    255, 255 };
            int[] b = { 0, 61, 96, 130, 165, 192, 220, 227, 210, 181, 151, 122, 93,
                    64, 35, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 35, 98, 160, 223,
                    255 };

            // create map
            ArrayList<Color> colors = new ArrayList<Color>(r.length);
            
            // cast elements
            for (int i = 0; i < r.length; i++) 
            {
                colors.add(new RGB8(r[i], g[i], b[i]));
            }
            
            return  colors;
        }
    };
    
    public static final ColorMapFactory BLUE_WHITE_RED = new ColorMapFactory()
    {
        @Override
        public ColorMap createColorMap(int nColors)
        {
            ArrayList<Color> baseColors = new ArrayList<Color>(5);
            baseColors.add(new RGB8(0, 0, 127));
            baseColors.add(new RGB8(0, 127, 255));
            baseColors.add(RGB8.WHITE);
            baseColors.add(new RGB8(255, 127, 0));
            baseColors.add(new RGB8(127, 0, 0));
            ColorMap map = new DefaultColorMap(baseColors);
            return interpolate(map, nColors);
        }
    };
    
    /**
     * Create lookup table with a  maximally distinct sets of colors (copied
     * from Fiji's Glasbey LUT).
     * 
     * Reference: 
     * [1] Glasbey, Chris, Gerie van der Heijden, Vivian FK Toh, and Alision 
     *     Gray. "Colour displays for categorical images." Color Research &amp; 
     *     Application 32, no. 4 (2007): 304-309.
     */
    public static final ColorMapFactory GLASBEY = new ColorMapFactory()
    {
        /**
         * 
         * @return Glasbey Color map
         */
        @Override
        public ColorMap createColorMap(int nColors)
        {
            ColorMap map = new DefaultColorMap(createColors());
            if (nColors != map.size())
            {
                map = periodicMap(map, nColors);
            }
            return map;
        }

        private ArrayList<Color> createColors() 
        {
            // initial values (copied from Fiji's Glasbey LUT, removing initial value
            int[] r = {  0, 255, 0, 0, 255, 0, 255, 0, 154, 0, 120, 31, 255, 
                    177, 241, 254, 221, 32, 114, 118, 2, 200, 136, 255, 133, 161, 
                    20, 0, 220, 147, 0, 0, 57, 238, 0, 171, 161, 164, 255, 71, 212, 
                    251, 171, 117, 166, 0, 165, 98, 0, 0, 86, 159, 66, 255, 0, 252, 
                    159, 167, 74, 0, 145, 207, 195, 253, 66, 106, 181, 132, 96, 255, 
                    102, 254, 228, 17, 210, 91, 32, 180, 226, 0, 93, 166, 97, 98, 
                    126, 0, 255, 7, 180, 148, 204, 55, 0, 150, 39, 206, 150, 180, 
                    110, 147, 199, 115, 15, 172, 182, 216, 87, 216, 0, 243, 216, 1, 
                    52, 255, 87, 198, 255, 123, 120, 162, 105, 198, 121, 0, 231, 217, 
                    255, 209, 36, 87, 211, 203, 62, 0, 112, 209, 0, 105, 255, 233, 
                    191, 69, 171, 14, 0, 118, 255, 94, 238, 159, 80, 189, 0, 88, 71, 
                    1, 99, 2, 139, 171, 141, 85, 150, 0, 255, 222, 107, 30, 173, 
                    255, 0, 138, 111, 225, 255, 229, 114, 111, 134, 99, 105, 200, 
                    209, 198, 79, 174, 170, 199, 255, 146, 102, 111, 92, 172, 210, 
                    199, 255, 250, 49, 254, 254, 68, 201, 199, 68, 147, 22, 8, 116, 
                    104, 64, 164, 207, 118, 83, 0, 43, 160, 176, 29, 122, 214, 160, 
                    106, 153, 192, 125, 149, 213, 22, 166, 109, 86, 255, 255, 255, 
                    202, 67, 234, 191, 38, 85, 121, 254, 139, 141, 0, 63, 255, 17, 
                    154, 149, 126, 58, 189 };
            int[] g = {  0, 0, 255, 0, 0, 83, 211, 159, 77, 255, 63, 150, 172, 
                    204, 8, 143, 0, 26, 0, 108, 173, 255, 108, 183, 133, 3, 249, 71, 
                    94, 212, 76, 66, 167, 112, 0, 245, 146, 255, 206, 0, 173, 118, 
                    188, 0, 0, 115, 93, 132, 121, 255, 53, 0, 45, 242, 93, 255, 191, 
                    84, 39, 16, 78, 149, 187, 68, 78, 1, 131, 233, 217, 111, 75, 
                    100, 3, 199, 129, 118, 59, 84, 8, 1, 132, 250, 123, 0, 190, 60, 
                    253, 197, 167, 186, 187, 0, 40, 122, 136, 130, 164, 32, 86, 0, 
                    48, 102, 187, 164, 117, 220, 141, 85, 196, 165, 255, 24, 66, 
                    154, 95, 241, 95, 172, 100, 133, 255, 82, 26, 238, 207, 128, 
                    211, 255, 0, 163, 231, 111, 24, 117, 176, 24, 30, 200, 203, 194, 
                    129, 42, 76, 117, 30, 73, 169, 55, 230, 54, 0, 144, 109, 223, 
                    80, 93, 48, 206, 83, 0, 42, 83, 255, 152, 138, 69, 109, 0, 76, 
                    134, 35, 205, 202, 75, 176, 232, 16, 82, 137, 38, 38, 110, 164, 
                    210, 103, 165, 45, 81, 89, 102, 134, 152, 255, 137, 34, 207, 
                    185, 148, 34, 81, 141, 54, 162, 232, 152, 172, 75, 84, 45, 60, 
                    41, 113, 0, 1, 0, 82, 92, 217, 26, 3, 58, 209, 100, 157, 219, 
                    56, 255, 0, 162, 131, 249, 105, 188, 109, 3, 0, 0, 109, 170, 
                    165, 44, 185, 182, 236, 165, 254, 60, 17, 221, 26, 66, 157, 
                    130, 6, 117};
            int[] b = { 255, 0, 0, 51, 182, 0, 0, 255, 66, 190, 193, 152, 253, 
                    113, 92, 66, 255, 1, 85, 149, 36, 0, 0, 159, 103, 0, 255, 158, 
                    147, 255, 255, 80, 106, 254, 100, 204, 255, 115, 113, 21, 197, 
                    111, 0, 215, 154, 254, 174, 2, 168, 131, 0, 63, 66, 187, 67, 
                    124, 186, 19, 108, 166, 109, 0, 255, 64, 32, 0, 84, 147, 0, 211, 
                    63, 0, 127, 174, 139, 124, 106, 255, 210, 20, 68, 255, 201, 122, 
                    58, 183, 0, 226, 57, 138, 160, 49, 1, 129, 38, 180, 196, 128, 
                    180, 185, 61, 255, 253, 100, 250, 254, 113, 34, 103, 105, 182, 
                    219, 54, 0, 1, 79, 133, 240, 49, 204, 220, 100, 64, 70, 69, 233, 
                    209, 141, 3, 193, 201, 79, 0, 223, 88, 0, 107, 197, 255, 137, 
                    46, 145, 194, 61, 25, 127, 200, 217, 138, 33, 148, 128, 126, 96, 
                    103, 159, 60, 148, 37, 255, 135, 148, 0, 123, 203, 200, 230, 68, 
                    138, 161, 60, 0, 157, 253, 77, 57, 255, 101, 48, 80, 32, 0, 255, 
                    86, 77, 166, 101, 175, 172, 78, 184, 255, 159, 178, 98, 147, 30, 
                    141, 78, 97, 100, 23, 84, 240, 0, 58, 28, 121, 0, 255, 38, 215, 
                    155, 35, 88, 232, 87, 146, 229, 36, 159, 207, 105, 160, 113, 207, 
                    89, 34, 223, 204, 69, 97, 78, 81, 248, 73, 35, 18, 173, 0, 51, 
                    2, 158, 212, 89, 193, 43, 40, 246, 146, 84, 238, 72, 101, 101 };

            // create map
            ArrayList<Color> colors = new ArrayList<Color>(r.length);
            
            // cast elements
            for (int i = 0; i < r.length; i++) 
            {
                colors.add(new RGB8(r[i], g[i], b[i]));
            }
            
            return colors;
        }
    };
    
    /**
     * Create lookup table with a maximally distinct sets of colors, retaining
     * only colors with low luminance, making it useful for displaying
     * categorical data or label maps over a white background.
     * 
     * References:
     * <ul>
     * <li><a href=
     * "https://colorcet.holoviz.org/user_guide/Categorical.html">Colorcet
     * Categorical colormaps</a></li>
     * <li>Kovesi, Peter. Good Colour Maps: How to Design Them. <a href=
     * "https://arxiv.org/abs/1509.03700">https://arxiv.org/abs/1509.03700</a></li>
     * <li>Glasbey, Chris, Gerie van der Heijden, Vivian FK Toh, and Alision
     * Gray. "Colour displays for categorical images." Color Research &amp;
     * Application 32, no. 4 (2007): 304-309.</li>
     * </ul>
     */
    public static final ColorMapFactory GLASBEY_DARK = new ColorMapFactory()
    {
        /**
         * 
         * @return Glasbey dark color map
         */
        @Override
        public ColorMap createColorMap(int nColors)
        {
            ColorMap map = new DefaultColorMap(createColors());
            if (nColors != map.size())
            {
                map = periodicMap(map, nColors);
            }
            return map;
        }
        
        private ArrayList<Color> createColors() 
        {
            int[][] data = {
                    { 215, 0, 0 }, { 140, 60, 255 }, { 2, 136, 0 }, { 0, 172, 199 }, { 231, 165, 0 }, { 255, 127, 209 },
                    { 108, 0, 79 }, { 88, 59, 0 }, { 0, 87, 89 }, { 21, 225, 141 }, { 0, 0, 221 }, { 162, 118, 106 },
                    { 188, 183, 255 }, { 192, 4, 185 }, { 100, 84, 115 }, { 121, 0, 0 }, { 7, 116, 216 }, { 115, 155, 125 },
                    { 255, 120, 82 }, { 0, 75, 0 }, { 143, 123, 1 }, { 243, 0, 123 }, { 143, 186, 0 }, { 166, 123, 184 },
                    { 90, 2, 163 }, { 227, 175, 175 }, { 160, 58, 82 }, { 162, 200, 200 }, { 158, 75, 0 }, { 84, 103, 69 },
                    { 187, 195, 137 }, { 95, 123, 136 }, { 96, 56, 60 }, { 131, 136, 255 }, { 57, 0, 0 }, { 227, 83, 255 },
                    { 48, 83, 130 }, { 127, 202, 255 }, { 197, 102, 143 }, { 0, 129, 106 }, { 146, 158, 183 },
                    { 204, 116, 7 }, { 127, 43, 142 }, { 0, 190, 164 }, { 45, 177, 82 }, { 78, 51, 255 }, { 0, 229, 0 },
                    { 255, 0, 206 }, { 200, 88, 72 }, { 229, 156, 255 }, { 29, 161, 255 }, { 110, 112, 171 },
                    { 200, 154, 105 }, { 120, 87, 59 }, { 4, 218, 230 }, { 193, 163, 196 }, { 255, 106, 138 },
                    { 187, 0, 254 }, { 146, 83, 128 }, { 159, 2, 116 }, { 148, 161, 80 }, { 55, 68, 37 }, { 175, 109, 255 },
                    { 89, 109, 0 }, { 255, 49, 71 }, { 131, 128, 87 }, { 0, 109, 46 }, { 137, 86, 175 }, { 90, 74, 163 },
                    { 119, 53, 22 }, { 134, 195, 154 }, { 95, 17, 35 }, { 213, 133, 129 }, { 164, 41, 24 }, { 0, 136, 177 },
                    { 203, 0, 68 }, { 255, 160, 86 }, { 235, 78, 0 }, { 108, 151, 0 }, { 83, 134, 73 }, { 117, 90, 0 },
                    { 200, 196, 64 }, { 146, 211, 112 }, { 75, 152, 148 }, { 77, 35, 13 }, { 97, 52, 92 }, { 132, 0, 207 },
                    { 139, 0, 49 }, { 159, 110, 50 }, { 172, 132, 153 }, { 198, 49, 137 }, { 2, 84, 56 }, { 8, 107, 132 },
                    { 135, 168, 236 }, { 100, 102, 239 }, { 196, 93, 186 }, { 1, 159, 112 }, { 129, 81, 89 },
                    { 131, 111, 140 }, { 179, 192, 218 }, { 185, 145, 41 }, { 255, 151, 178 }, { 167, 147, 225 },
                    { 105, 141, 190 }, { 76, 80, 1 }, { 72, 2, 204 }, { 97, 0, 110 }, { 69, 106, 102 }, { 157, 87, 67 },
                    { 123, 172, 181 }, { 205, 132, 189 }, { 0, 84, 193 }, { 123, 47, 79 }, { 251, 124, 0 }, { 52, 192, 0 },
                    { 255, 156, 136 }, { 225, 183, 105 }, { 83, 97, 119 }, { 92, 58, 124 }, { 237, 165, 218 },
                    { 240, 83, 163 }, { 93, 126, 105 }, { 196, 119, 80 }, { 209, 72, 104 }, { 110, 0, 235 }, { 31, 52, 0 },
                    { 193, 65, 4 }, { 109, 213, 194 }, { 70, 112, 159 }, { 162, 1, 196 }, { 10, 130, 137 }, { 175, 166, 1 },
                    { 166, 92, 107 }, { 254, 119, 255 }, { 139, 133, 174 }, { 199, 127, 233 }, { 154, 171, 133 },
                    { 135, 108, 217 }, { 1, 186, 247 }, { 175, 94, 210 }, { 89, 81, 43 }, { 182, 0, 95 }, { 124, 182, 106 },
                    { 73, 133, 255 }, { 0, 194, 130 }, { 210, 149, 171 }, { 163, 75, 168 }, { 227, 6, 227 }, { 22, 163, 0 },
                    { 57, 46, 0 }, { 132, 48, 51 }, { 94, 149, 170 }, { 90, 16, 0 }, { 123, 70, 0 }, { 111, 111, 49 },
                    { 51, 88, 38 }, { 77, 96, 182 }, { 162, 149, 100 }, { 98, 64, 40 }, { 69, 212, 88 }, { 112, 170, 208 },
                    { 46, 107, 78 }, { 115, 175, 158 }, { 253, 21, 0 }, { 216, 180, 146 }, { 122, 137, 59 },
                    { 125, 198, 217 }, { 220, 145, 55 }, { 236, 97, 94 }, { 236, 95, 212 }, { 229, 123, 167 },
                    { 166, 108, 152 }, { 0, 151, 68 }, { 186, 95, 34 }, { 188, 173, 83 }, { 136, 216, 48 },
                    { 135, 53, 115 }, { 174, 168, 210 }, { 227, 140, 99 }, { 209, 177, 236 }, { 55, 66, 159 },
                    { 58, 190, 194 }, { 102, 157, 77 }, { 158, 3, 153 }, { 78, 78, 122 }, { 123, 76, 134 }, { 195, 53, 49 },
                    { 141, 102, 119 }, { 170, 0, 45 }, { 127, 1, 117 }, { 1, 130, 77 }, { 115, 74, 103 }, { 114, 119, 145 },
                    { 110, 0, 153 }, { 160, 186, 82 }, { 225, 110, 49 }, { 197, 106, 113 }, { 109, 91, 150 },
                    { 163, 60, 116 }, { 50, 98, 0 }, { 136, 0, 80 }, { 51, 88, 105 }, { 186, 141, 124 }, { 25, 89, 255 },
                    { 145, 146, 2 }, { 44, 139, 213 }, { 23, 38, 255 }, { 33, 211, 255 }, { 164, 144, 175 },
                    { 139, 109, 79 }, { 94, 33, 62 }, { 220, 3, 179 }, { 111, 87, 202 }, { 101, 40, 33 }, { 173, 119, 0 },
                    { 163, 191, 247 }, { 181, 132, 70 }, { 151, 56, 220 }, { 178, 81, 148 }, { 114, 66, 163 },
                    { 135, 143, 209 }, { 138, 112, 177 }, { 107, 175, 54 }, { 90, 122, 201 }, { 199, 159, 255 },
                    { 86, 132, 26 }, { 0, 214, 167 }, { 130, 71, 57 }, { 17, 67, 29 }, { 90, 171, 117 }, { 145, 91, 1 },
                    { 246, 69, 112 }, { 255, 151, 3 }, { 225, 66, 49 }, { 186, 146, 207 }, { 52, 88, 77 },
                    { 248, 128, 125 }, { 145, 52, 0 }, { 179, 205, 0 }, { 46, 159, 211 }, { 121, 139, 159 },
                    { 81, 129, 125 }, { 193, 54, 215 }, { 236, 5, 83 }, { 185, 172, 126 }, { 72, 112, 50 },
                    { 132, 149, 101 }, { 217, 157, 137 }, { 0, 100, 163 }, { 76, 144, 120 }, { 143, 97, 152 },
                    { 255, 83, 56 }, { 167, 66, 59 }, { 0, 110, 112 }, { 152, 132, 62 }, { 220, 176, 200 } };
            
            // create colors from RGB values
            ArrayList<Color> colors = new ArrayList<Color>(data.length);
            for (int i = 0; i < data.length; i++) 
            {
                colors.add(new RGB8(data[i][0], data[i][1], data[i][2]));
            }
            
            return colors;
        }
    };
    
    /**
     * Create lookup table with a maximally distinct sets of colors, retaining
     * only colors with high luminance, making it useful for displaying
     * categorical data or label maps over a black background.
     * 
     * References:
     * <ul>
     * <li><a href=
     * "https://colorcet.holoviz.org/user_guide/Categorical.html">Colorcet
     * Categorical colormaps</a></li>
     * <li>Kovesi, Peter. Good Colour Maps: How to Design Them. <a href=
     * "https://arxiv.org/abs/1509.03700">https://arxiv.org/abs/1509.03700</a></li>
     * <li>Glasbey, Chris, Gerie van der Heijden, Vivian FK Toh, and Alision
     * Gray. "Colour displays for categorical images." Color Research &amp;
     * Application 32, no. 4 (2007): 304-309.</li>
     * </ul>
     */
    public static final ColorMapFactory GLASBEY_BRIGHT = new ColorMapFactory()
    {
        /**
         * 
         * @return Glasbey light color map
         */
        @Override
        public ColorMap createColorMap(int nColors)
        {
            ColorMap map = new DefaultColorMap(createColors());
            if (nColors != map.size())
            {
                map = periodicMap(map, nColors);
            }
            return map;
        }
        
        private ArrayList<Color> createColors() 
        {
            int[][] data = {
                    { 215, 0, 0 }, { 2, 136, 0 }, { 182, 0, 255 }, { 6, 172, 198 }, { 152, 255, 0 }, { 255, 165, 48 },
                    { 255, 143, 200 }, { 121, 82, 95 }, { 0, 254, 207 }, { 176, 165, 255 }, { 148, 173, 132 },
                    { 154, 105, 0 }, { 55, 106, 98 }, { 211, 0, 140 }, { 254, 245, 144 }, { 200, 111, 102 },
                    { 158, 227, 255 }, { 0, 201, 70 }, { 169, 119, 173 }, { 184, 187, 2 }, { 244, 192, 177 },
                    { 255, 40, 253 }, { 243, 206, 255 }, { 0, 159, 125 }, { 255, 98, 0 }, { 86, 101, 43 }, { 150, 63, 31 },
                    { 145, 49, 143 }, { 255, 52, 101 }, { 160, 228, 146 }, { 141, 155, 178 }, { 130, 145, 38 },
                    { 174, 9, 63 }, { 120, 199, 187 }, { 188, 146, 88 }, { 229, 143, 255 }, { 114, 185, 255 },
                    { 198, 165, 193 }, { 255, 145, 113 }, { 211, 195, 125 }, { 189, 238, 219 }, { 107, 133, 104 },
                    { 146, 110, 86 }, { 249, 255, 0 }, { 186, 194, 224 }, { 173, 87, 125 }, { 255, 206, 3 },
                    { 255, 74, 177 }, { 194, 87, 3 }, { 93, 140, 144 }, { 194, 68, 189 }, { 0, 117, 64 }, { 186, 111, 254 },
                    { 0, 212, 148 }, { 0, 255, 118 }, { 73, 162, 81 }, { 204, 152, 145 }, { 0, 235, 238 }, { 219, 126, 1 },
                    { 248, 117, 138 }, { 185, 150, 0 }, { 201, 66, 72 }, { 0, 208, 250 }, { 118, 88, 39 }, { 133, 212, 1 },
                    { 236, 255, 212 }, { 167, 123, 136 }, { 220, 114, 201 }, { 203, 227, 87 }, { 139, 191, 94 },
                    { 161, 33, 107 }, { 134, 91, 137 }, { 138, 187, 208 }, { 255, 186, 215 }, { 183, 207, 171 },
                    { 151, 65, 78 }, { 104, 171, 0 }, { 254, 225, 178 }, { 255, 55, 41 }, { 128, 122, 62 },
                    { 215, 232, 255 }, { 167, 149, 198 }, { 126, 165, 155 }, { 209, 131, 164 }, { 84, 130, 59 },
                    { 230, 169, 115 }, { 156, 255, 255 }, { 218, 85, 129 }, { 5, 180, 170 }, { 255, 171, 246 },
                    { 209, 175, 239 }, { 218, 2, 94 }, { 172, 27, 19 }, { 96, 179, 133 }, { 213, 66, 253 },
                    { 173, 171, 89 }, { 251, 157, 167 }, { 179, 114, 60 }, { 242, 106, 83 }, { 174, 210, 213 },
                    { 155, 255, 196 }, { 219, 179, 51 }, { 236, 2, 195 }, { 153, 0, 197 }, { 208, 255, 158 },
                    { 166, 90, 74 }, { 60, 109, 1 }, { 0, 133, 122 }, { 149, 146, 103 }, { 138, 220, 179 }, { 109, 116, 0 },
                    { 170, 94, 202 }, { 7, 240, 0 }, { 129, 79, 62 }, { 217, 129, 82 }, { 255, 200, 99 }, { 184, 0, 159 },
                    { 153, 172, 222 }, { 145, 79, 0 }, { 140, 69, 112 }, { 79, 110, 82 }, { 255, 136, 52 },
                    { 199, 143, 206 }, { 213, 226, 158 }, { 178, 130, 109 }, { 157, 251, 117 }, { 87, 222, 119 },
                    { 250, 0, 135 }, { 162, 205, 255 }, { 20, 203, 210 }, { 17, 143, 85 }, { 210, 84, 165 },
                    { 0, 223, 195 }, { 163, 132, 47 }, { 119, 151, 91 }, { 187, 171, 128 }, { 112, 163, 176 },
                    { 214, 251, 255 }, { 232, 2, 58 }, { 216, 71, 34 }, { 255, 131, 237 }, { 183, 56, 99 },
                    { 183, 206, 114 }, { 152, 98, 107 }, { 138, 116, 145 }, { 0, 163, 23 }, { 0, 245, 161 },
                    { 192, 145, 242 }, { 138, 228, 216 }, { 164, 78, 149 }, { 110, 94, 0 }, { 140, 198, 142 },
                    { 149, 170, 43 }, { 199, 115, 221 }, { 180, 59, 1 }, { 215, 154, 55 }, { 223, 173, 183 },
                    { 0, 155, 160 }, { 90, 144, 0 }, { 151, 188, 168 }, { 173, 141, 168 }, { 218, 213, 255 },
                    { 85, 125, 114 }, { 0, 187, 105 }, { 255, 196, 142 }, { 185, 0, 212 }, { 224, 208, 91 },
                    { 99, 154, 123 }, { 192, 238, 188 }, { 194, 190, 254 }, { 128, 211, 222 }, { 226, 133, 126 },
                    { 250, 235, 78 }, { 192, 109, 131 }, { 203, 255, 80 }, { 240, 114, 170 }, { 237, 104, 255 },
                    { 153, 71, 174 }, { 109, 105, 67 }, { 227, 87, 97 }, { 221, 102, 45 }, { 157, 219, 93 },
                    { 226, 157, 208 }, { 185, 118, 0 }, { 198, 0, 45 }, { 223, 189, 218 }, { 90, 182, 223 },
                    { 255, 90, 218 }, { 56, 194, 161 }, { 158, 106, 140 }, { 173, 170, 200 }, { 150, 99, 48 },
                    { 182, 86, 98 }, { 44, 127, 96 }, { 178, 228, 0 }, { 238, 165, 145 }, { 149, 254, 226 },
                    { 255, 85, 142 }, { 190, 111, 161 }, { 170, 60, 55 }, { 217, 207, 0 }, { 171, 128, 206 },
                    { 160, 128, 82 }, { 225, 0, 232 }, { 195, 92, 62 }, { 181, 58, 133 }, { 140, 120, 0 },
                    { 219, 188, 150 }, { 82, 158, 147 }, { 176, 189, 131 }, { 146, 182, 183 }, { 167, 84, 36 },
                    { 255, 213, 239 }, { 121, 174, 107 }, { 94, 181, 76 }, { 128, 251, 155 }, { 72, 255, 239 },
                    { 152, 150, 72 }, { 148, 136, 167 }, { 50, 213, 0 }, { 110, 234, 86 }, { 183, 212, 235 },
                    { 112, 85, 112 }, { 242, 219, 139 }, { 171, 213, 194 }, { 127, 205, 242 }, { 138, 187, 0 },
                    { 101, 183, 187 }, { 255, 182, 0 }, { 195, 130, 134 }, { 203, 171, 95 }, { 101, 120, 72 },
                    { 89, 227, 255 }, { 223, 78, 205 }, { 234, 255, 121 }, { 189, 102, 185 }, { 196, 149, 166 },
                    { 100, 198, 116 }, { 209, 149, 112 }, { 112, 207, 79 }, { 171, 110, 102 }, { 157, 97, 165 },
                    { 0, 184, 0 }, { 227, 153, 180 }, { 189, 0, 108 }, { 179, 233, 240 }, { 206, 191, 228 },
                    { 119, 163, 67 }, { 133, 98, 120 }, { 87, 143, 92 }, { 158, 176, 197 }, { 232, 48, 160 },
                    { 37, 124, 42 }, { 130, 104, 35 }, { 192, 188, 78 }, { 221, 211, 165 }, };
            
            // create colors from RGB values
            ArrayList<Color> colors = new ArrayList<Color>(data.length);
            for (int i = 0; i < data.length; i++) 
            {
                colors.add(new RGB8(data[i][0], data[i][1], data[i][2]));
            }
            
            return colors;
        }
    };
    
    public static final ColorMapFactory HSV = new ColorMapFactory()
    {
        @Override
        public ColorMap createColorMap(int nColors)
        {
            // create map
            ArrayList<Color> colors = new ArrayList<Color>(nColors);
            
            // cast elements
            for (int i = 0; i < nColors; i++) 
            {
                colors.add(hueColor(((double) i) / nColors));
            }
            return new DefaultColorMap(colors);
        }

        private RGB8 hueColor(double hue_unitRange)
        {
            int X = (int) ((1.0 - Math.abs((hue_unitRange * 6) % 2 - 1)) * 255);
            int portion = (int) Math.floor(hue_unitRange * 6);
            return switch (portion)
            {
                case 0 -> new RGB8(255, X, 0);
                case 1 -> new RGB8(X, 255, 0);
                case 2 -> new RGB8(0, 255, X);
                case 3 -> new RGB8(0, X, 255);
                case 4 -> new RGB8(X, 0, 255);
                case 5 -> new RGB8(255, 0, X);
                default -> throw new RuntimeException("Input value of hue must be within 0 and 1");
            };
        }
    };
            
    /**
     * Private constructor to prevent instantiation
     */
    private ColorMaps()
    {
    }

    /**
     * Interpolates a color map to ensure a given number of colors.
     * 
     * @param colorMap
     *            the original color map
     * @param nColors
     *            the number of colors of the new color map
     * @return the interpolated color map
     */
    public static final ColorMap interpolate(ColorMap colorMap, int nColors) 
    {
        // allocate memory for new colormap
        ArrayList<Color> newColors = new ArrayList<Color>(nColors);
        
        // linear interpolation of each color of new colormap
        int n0 = colorMap.size();
        for (int i = 0; i < nColors; i++) {
            // compute color index in original colormap
            float i0 = ((float) i) * (n0 - 1) / (nColors - 1);
            int i1 = (int) Math.floor(i0);
            // the ratio between the two surrounding colors
            float f = i0 - i1;
            
            // the two surrounding colors
            Color col1 = colorMap.getColor(i1);
            Color col2 = colorMap.getColor(Math.min(i1 + 1, n0 - 1));

            newColors.add(interpolateRGB8(col1, 1-f, col2, f));
        }
        return new DefaultColorMap(newColors);
    }
    
    /**
     * Re-samples a color map to ensure a given number of colors.
     * 
     * @param colorMap
     *            the original color map
     * @param nColors
     *            the number of colors of the new color map
     * @return the interpolated color map
     */
    public static final ColorMap periodicMap(ColorMap colorMap, int nColors) 
    {
        // allocate memory for new colormap
        ArrayList<Color> newColors = new ArrayList<Color>(nColors);
        
        // pick colors in linear order, starting from beginning if new map is larger
        int n0 = colorMap.size();
        for (int i = 0; i < nColors; i++)
        {
            newColors.add(colorMap.getColor(i % n0));
        }
        return new DefaultColorMap(newColors);
    }
    
    private static final RGB8 interpolateRGB8(Color color1, double w1, Color color2, double w2)
    {
        double sum = w1 + w2;
        double red = (color1.red() * w1 + color2.red() * w2) * 255 / sum;
        double green = (color1.green() * w1 + color2.green() * w2) * 255 / sum;
        double blue = (color1.blue() * w1 + color2.blue() * w2) * 255 / sum;
        return new RGB8(red, green, blue);
    }
}
