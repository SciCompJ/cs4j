%CREATE_MULTIPAGE_TIFFFILE  One-line description here, please.
%
%   output = create_MultiPage_TiffFile(input)
%
%   Example
%   create_MultiPage_TiffFile
%
%   See also
%
 
% ------
% Author: David Legland
% e-mail: david.legland@inrae.fr
% INRAE - BIA Research Unit - BIBS Platform (Nantes)
% Created: 2026-04-20,    using Matlab 25.1.0.2973910 (R2025a) Update 1
% Copyright 2026 INRAE.

% the name of the file to create
fileName = 'multiPageTiffFile.tif';

% create three images with different sizes and data types
img1 = zeros(200, 300, 'uint8');
img2 = zeros(20, 30, 'uint16');
img3 = false(40, 50);

% writes the three files as "pages" within a Tiff file
imwrite(img1, fileName, "Compression", "none", "WriteMode", "overwrite");
imwrite(img2, fileName, "Compression", "none", "WriteMode", "append");
imwrite(img3, fileName, "Compression", "none", "WriteMode", "append");

