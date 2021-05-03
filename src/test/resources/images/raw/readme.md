A collection of raw data files, together with MHD headers.

This repository contains a collection of raw binary data files corresponding to images
with 2 or 3 dimensions, and with various data types.
They are used to test raw and meta-image image readers.

2D images contain linear ramps in x and y. Values equal 4*y + x.

3D images contain linear ramps in x, y, and z. Values within images shoud 
equal 100*z + 10*y + x.

Example for a 3D array with format 6x5x4:

    slice 0:
      0   1   2   3   4   5
     10  11  12  13  14  15
     20  21  22  23  24  25
     30  31  32  33  34  35
     40  41  42  43  44  45
	
    slice 1:
    100 101 102 103 104 105
    110 111 112 113 114 115
    120 121 122 123 124 125
    130 131 132 133 134 135	
    140 141 142 143 144 145	
      
    slice 2:
    200 201 202 203 204 205
    210 211 212 213 214 215
    220 221 222 223 224 225
    230 231 232 233 234 235	
    240 241 242 243 244 245	
       
    slice 3:
    300 301 302 303 304 305
    310 311 312 313 314 315
    320 321 322 323 324 325
    330 331 332 333 334 335	
    340 341 342 343 344 345	
 
