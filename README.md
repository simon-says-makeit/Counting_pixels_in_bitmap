# Counting_pixels_in_bitmap
Assignment Implements a method to count pixels of a given colour in the bitmap represented by a quadtree. a QuadtreeBitmap itself can be thought of as a node. A QuadtreeBitmap is either a leaf, in which case it represents a square region of pixels all of the same colour; or an internal node, in which case it has four child QuadtreeBitmaps, one per quadrant of the bitmap image. 

**Not all of the code is developed by Simon Nortje. There was an already existing scaffold provided by the University of Sydney.**

Functions implemented:

blackenNorthWestQuadrant() : blacken the entire north-west quadrant.

countPixels(Colour) : count pixels of a given colour in the bitmap represented by the quadtree.

invertColours() : invert the colours in the bitmap represented by the quadtree, i.e. turn every black pixel white and every white pixel black.

setPixel(int x, int y, Colour) : change the colour of a single pixel in the bitmap represented by the quadtree, to the specified colour.

computeOverlay(QuadtreeBitmap bmp1, QuadtreeBitmap bmp2) : construct and return the overlay of the two input images of the same size. In the overlay a pixel is black if either of the input images has a black pixel in the same location. That is, a pixel in the output image is white only when the corresponding pixel in both input images is white, otherwise the output pixel is black. Rather than do the operation pixel by pixel, one can compute the overlay more efficiently by leveraging the quadtree's ability to represent multiple pixels with a single node.
