%CREATETESTIMAGES  One-line description here, please.
%
%   output = createTestImages(input)
%
%   Example
%   createTestImages
%
%   See also
%
 
% ------
% Author: David Legland
% e-mail: david.legland@inra.fr
% Created: 2019-01-08,    using Matlab 8.6.0.267246 (R2015b)
% Copyright 2019 INRA - Cepia Software Platform.

% For 3D images, use a small size equal to 4x3
dims = [4 3];
lx = 0:dims(1)-1;
ly = 0:dims(2)-1;
[x, y] = meshgrid(lx, ly);
img = x * 10 + y * 100;

metaImageWrite(uint8(img), 'xyRamp_4x3_uint8.mhd');
metaImageWrite(uint16(img), 'xyRamp_4x3_uint16_msb.mhd', 'ElementType', 'MET_USHORT', 'ElementByteOrderMSB', true);
metaImageWrite(uint16(img), 'xyRamp_4x3_uint16_lsb.mhd', 'ElementType', 'MET_USHORT', 'ElementByteOrderMSB', false);
metaImageWrite(int16(img), 'xyRamp_4x3_int16_msb.mhd', 'ElementType', 'MET_SHORT', 'ElementByteOrderMSB', true);
metaImageWrite(int16(img), 'xyRamp_4x3_int16_lsb.mhd', 'ElementType', 'MET_SHORT', 'ElementByteOrderMSB', false);
metaImageWrite(int32(img), 'xyRamp_4x3_int32_msb.mhd', 'ElementType', 'MET_INT', 'ElementByteOrderMSB', true);
metaImageWrite(int32(img), 'xyRamp_4x3_int32_lsb.mhd', 'ElementType', 'MET_INT', 'ElementByteOrderMSB', false);
metaImageWrite(single(img), 'xyRamp_4x3_float32_msb.mhd', 'ElementType', 'MET_FLOAT', 'ElementByteOrderMSB', true);
metaImageWrite(single(img), 'xyRamp_4x3_float32_lsb.mhd', 'ElementType', 'MET_FLOAT', 'ElementByteOrderMSB', false);
metaImageWrite(img, 'xyRamp_4x3_float64_msb.mhd', 'ElementType', 'MET_DOUBLE', 'ElementByteOrderMSB', true);
metaImageWrite(img, 'xyRamp_4x3_float64_lsb.mhd', 'ElementType', 'MET_DOUBLE', 'ElementByteOrderMSB', false);


% For 3D uint8 images, use 5x4x3 size and specify additional arguments
dims = [5 4 3];
lx = 0:dims(1)-1;
ly = 0:dims(2)-1;
lz = 0:dims(3)-1;
[x, y, z] = meshgrid(lx, ly, lz);
img = x + y * 10 + z * 100;
metaImageWrite(uint8(img), 'xyzRamp_5x4x3_uint8.mhd', 'NDims', 3, 'DimSize', [5 4 3], 'ElementNumberOfChannels', 1);

% For other 3D images, use 6x5x4 size to avoid ambiguity with color images
dims = [6 5 4];
lx = 0:dims(1)-1;
ly = 0:dims(2)-1;
lz = 0:dims(3)-1;
[x, y, z] = meshgrid(lx, ly, lz);
img = x + y * 10 + z * 100;

metaImageWrite(uint16(img), 'xyzRamp_6x5x4_uint16_msb.mhd', 'ElementType', 'MET_USHORT', 'ElementByteOrderMSB', true);
metaImageWrite(uint16(img), 'xyzRamp_6x5x4_uint16_lsb.mhd', 'ElementType', 'MET_USHORT', 'ElementByteOrderMSB', false);
metaImageWrite(int16(img), 'xyzRamp_6x5x4_int16_msb.mhd', 'ElementType', 'MET_SHORT', 'ElementByteOrderMSB', true);
metaImageWrite(int16(img), 'xyzRamp_6x5x4_int16_lsb.mhd', 'ElementType', 'MET_SHORT', 'ElementByteOrderMSB', false);
metaImageWrite(int32(img), 'xyzRamp_6x5x4_int32_msb.mhd', 'ElementType', 'MET_INT', 'ElementByteOrderMSB', true);
metaImageWrite(int32(img), 'xyzRamp_6x5x4_int32_lsb.mhd', 'ElementType', 'MET_INT', 'ElementByteOrderMSB', false);
metaImageWrite(single(img), 'xyzRamp_6x5x4_float32_msb.mhd', 'ElementType', 'MET_FLOAT', 'ElementByteOrderMSB', true);
metaImageWrite(single(img), 'xyzRamp_6x5x4_float32_lsb.mhd', 'ElementType', 'MET_FLOAT', 'ElementByteOrderMSB', false);
metaImageWrite(img, 'xyzRamp_6x5x4_float64_msb.mhd', 'ElementType', 'MET_DOUBLE', 'ElementByteOrderMSB', true);
metaImageWrite(img, 'xyzRamp_6x5x4_float64_lsb.mhd', 'ElementType', 'MET_DOUBLE', 'ElementByteOrderMSB', false);

