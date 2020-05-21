
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import java.util.Scanner;
import java.io.InputStream;

public class QuadtreeBitmap {
	// location
	private final int x;
	private final int y;
	// height and width
	private final int size;
	// if leaf
	private boolean leaf;
	// either Colour.BLACK or Colour.WHITE
	private Colour colour;
	// otherwise
	private QuadtreeBitmap northWest;
	private QuadtreeBitmap northEast;
	private QuadtreeBitmap southWest;
	private QuadtreeBitmap southEast;

	/**
	 * Constructs a new quadtree bitmap with height and width equal to the specified size, and 
	 * every pixel initialized to the given colour. The specified size must be a power of 2, 
	 * and must be greater than zero.
	 * 
	 * @param size the height and width of this quadtree bitmap
	 * @param colour the colour with which to initialize every pixel in this quadtree bitmap
	 */
	public QuadtreeBitmap(int size, Colour colour) {
		this(0, 0, size, colour);
	}
	/**
	 * Constructs a new quadtree bitmap with height and width equal to the specified size, and 
	 * every pixel initialized to white. The specified size must be a power of 2, and must be 
	 * greater than zero.
	 * 
	 * @param size the height and width of this quadtree bitmap
	 */
	public QuadtreeBitmap(int size) {
		this(0, 0, size, Colour.WHITE);
	}

	// specifying location only supported internally
	private QuadtreeBitmap(int x, int y, int size, Colour colour) {
		// only supporting power-of-2 dimensions
		if (!powerOfTwo(size)) {
			throw new IllegalArgumentException("Size not power of 2.");
		}
		this.x = x;
		this.y = y;
		this.size = size;
		this.leaf = true;
		this.colour = colour;
		this.northWest = null;
		this.northEast = null;
		this.southWest = null;
		this.southEast = null;
	}
	
	// combining quads to form tree only supported internally, assumes well-positioned
	private QuadtreeBitmap(int x, int y, int size, List<QuadtreeBitmap> quads) {
		this(x, y, size, Colour.WHITE);
		northWest = quads.get(0);
		northEast = quads.get(1);
		southWest = quads.get(2);
		southEast = quads.get(3);
		this.leaf = false;
	}

	// for any basic task which needs to be repeated all four quadrants
	private List<QuadtreeBitmap> quadrants() {
		return Arrays.asList(northWest, northEast, southWest, southEast);
	}

	// retrieves the quadrant within which the specified location lies
	private QuadtreeBitmap quadrantOf(int x, int y) {
		for (QuadtreeBitmap quad : quadrants()) {
			if (quad.containsPoint(x, y)) {
				return quad;
			}
		}
		return null;
	}
	
	// sets colour of quadbit map to specified colour
	public void setColour(Colour  colour){
		this.colour = colour;
	}
	
	/**
	 * Returns true of this QuadtreeBitmap contains the location specified by the input 
	 * coordinates.
	 *
	 * @param x how far right of the origin the query location is
	 * @param y how far below the origin the query location is
	 * @return true if the given coordinates lie within this QuadtreeBitmap, false otherwise
	 */
	public boolean containsPoint(int x, int y) {
		return this.x <= x 
			&& this.y <= y
			&& x < this.x + this.size 
			&& y < this.y + this.size;
	}

	/**
	 * Returns the height and width of this quadtree bitmap.
	 *
	 * @return the size of this quadtree bitmap.
	 */
	public int getSize() {
		return size;
	}
	
	/////////////////////////////////////////////////////////////////////////
	// Assignment methods start here
	/////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the colour of every pixel in the north-west quadrant of this quadtree bitmap to 
	 * black.
	 */
	
	
	// single slice slices the quadtree bitmap into 4 new quadrants only once - is used in the blacken method for simplicity
	
	public void singleSlice(QuadtreeBitmap bitMap){
		if (bitMap.leaf == true) {
			if (bitMap.size > 1) {
				bitMap.leaf = false;
				
				bitMap.northWest = new QuadtreeBitmap(bitMap.x, bitMap.y, bitMap.getSize()/2, bitMap.colour);
				bitMap.northEast = new QuadtreeBitmap(bitMap.x + bitMap.getSize()/2, bitMap.y, bitMap.getSize()/2, bitMap.colour);
				bitMap.southWest = new QuadtreeBitmap(bitMap.x, bitMap.y + bitMap.getSize()/2, bitMap.getSize()/2, bitMap.colour);
				bitMap.southEast = new QuadtreeBitmap(bitMap.x + bitMap.getSize()/2, bitMap.y + bitMap.getSize()/2, bitMap.getSize()/2, bitMap.colour);
			}
		}
	}
	
	// continuesly slices the bitmap into 4 new quadrants every recursion until every pixel is a quadtreebitmap
	
	public void slice() {
		sliceH(this);
	}
	
	// helper method for slice 
	
	public void sliceH(QuadtreeBitmap bitMap) {
		if (bitMap.leaf == true) {
			if (bitMap.size > 1) {
				bitMap.leaf = false;
				
				bitMap.northWest = new QuadtreeBitmap(bitMap.x, bitMap.y, bitMap.getSize()/2, bitMap.colour);
				bitMap.northEast = new QuadtreeBitmap(bitMap.x + bitMap.getSize()/2, bitMap.y, bitMap.getSize()/2, bitMap.colour);
				bitMap.southWest = new QuadtreeBitmap(bitMap.x, bitMap.y + bitMap.getSize()/2, bitMap.getSize()/2, bitMap.colour);
				bitMap.southEast = new QuadtreeBitmap(bitMap.x + bitMap.getSize()/2, bitMap.y + bitMap.getSize()/2, bitMap.getSize()/2, bitMap.colour);
				
				sliceH(bitMap.northWest);
				sliceH(bitMap.northEast);
				sliceH(bitMap.southEast);
				sliceH(bitMap.southWest);
			} else {
				bitMap.leaf = true;
				return;
			}
		} else {
			sliceH(bitMap.northWest);
			sliceH(bitMap.northEast);
			sliceH(bitMap.southEast);
			sliceH(bitMap.southWest);
		}
	}
	
	
	public void blackenNorthWestQuadrant(){
		if(this.leaf){
			if(this.colour.equals(colour.WHITE)){
				singleSlice(this);
				this.northWest.colour = colour.BLACK;
				this.northWest.leaf = true;
				this.northWest.southWest = null;
				this.northWest.southEast = null;
				this.northWest.northWest = null;
				this.northWest.northEast = null;
				return;
			}
			
		}
		
		QuadtreeBitmap northWest = this.northWest;
		northWest.colour = colour.BLACK;
		northWest.leaf = true;
		northWest.southWest = null;
		northWest.southEast = null;
		northWest.northWest = null;
		northWest.northEast = null;
		simplifyBitmap(this);
		return;
		
		
	}
	
	// reduces bitmap until it has only the necessay quadtreebitmaps to represent the space
	
	public void simplifyBitmap(QuadtreeBitmap bitmap){
		if(bitmap.leaf == false){
		List<QuadtreeBitmap> quads = bitmap.quadrants();
		simplifyBitmap(quads.get(0));
		simplifyBitmap(quads.get(1));
		simplifyBitmap(quads.get(2));
		simplifyBitmap(quads.get(3));				
		if(quads.get(0).colour.equals(quads.get(1).colour) && quads.get(1).colour.equals(quads.get(2).colour) && quads.get(2).colour.equals(quads.get(3).colour)){
			if(quads.get(0).leaf && quads.get(1).leaf && quads.get(2).leaf && quads.get(3).leaf){
				bitmap.colour = quads.get(0).colour;
				bitmap.leaf = true;
				quads.set(0,null);
				quads.set(1,null);
				quads.set(2,null);
				quads.set(3,null);
				return;	
			}
			return;
		} 
		return;
	} else {
		return;
		}
	}


	/**
	 * Counts the number of pixels of the given colour in the bitmap represented by this 
	 * quadtree.
	 *
	 * @param colour the colour to count the number of pixels of
	 * @return the number of pixels of the given colour
	 */
	
	
	public int countPixels(Colour colour) {
		
		String stringAsBitmap = this.toString();
		
		//System.out.println(stringAsBitmap.length());
		int black = 0;
		int white = 0;
		char[] array = stringAsBitmap.toCharArray();
		for(int i=0;i<array.length;i++){
			if(array[i] == '.'){
				white++;
			} else if(array[i] == '*'){
				black++;
			}
		}
		
		if(colour.texture == '.'){
			return white;
		} else if(colour.texture == '*'){
			return black;
		}
		
		return 0;
	}

	/**
	 * Inverts the colours in the bitmap represented by this quadtree, i.e. turns every black 
	 * pixel white and every white pixel black.
	 */	 
	public void invertColours() {
		
		if(this.leaf){
			if(this.colour.equals(colour.WHITE)){
				this.colour = colour.BLACK;
				return;
			}
			if(this.colour.equals(colour.BLACK)){
				this.colour = colour.WHITE;
				return;
			}
		}
		invertColours(this.northWest);
		invertColours(this.northEast);
		invertColours(this.southWest);
		invertColours(this.southEast);
		return;
			
	}
	
	// helper function to invert colours
	
	public void invertColours(QuadtreeBitmap current){
		if(current.leaf){
			if(current.colour.equals(colour.WHITE)){
				current.colour = colour.BLACK;
				return;
			}
			if(current.colour.equals(colour.BLACK)){
				current.colour = colour.WHITE;
				return;
			}
		}
		
		invertColours(current.northWest);
		invertColours(current.northEast);
		invertColours(current.southWest);
		invertColours(current.southEast);
		return;
	}
	
	

	/**
	 * Sets the colour of a single pixel at the specified location to the given colour.
	 *
	 * @param x the distance right of the origin of the given location
	 * @param y the distance below the origin of the given location
	 * @param colour the colour to set the pixel at the given location to
	 */
	
	public void setPixel(int x, int y, Colour colour) {
		sliceH(this);					// continuesly slices the bitmap into 4 new quadrants every recursion until every pixel is a quadtreebitmap 
		setPixelH(x, y, colour, this);	// helper method
		simplifyBitmap(this);
				
	}
	
	// calls every bitmap until current bitmap's co-ordinates equal the specified co-ordinates in the parameters then changes that pixels colour
	
	public void setPixelH(int x, int y, Colour colour, QuadtreeBitmap bitMap){
		if(bitMap.leaf == true){
			if(bitMap.x == x && bitMap.y == y){
				bitMap.colour = colour;
			} else {
				return;
				}
		} else {
			setPixelH(x, y, colour, bitMap.northWest);
			setPixelH(x, y, colour, bitMap.northEast);
			setPixelH(x, y, colour, bitMap.southEast);
			setPixelH(x, y, colour, bitMap.southWest);
			}
	}

	/**
	 * Constructs and returns the overlay of the two given quadtree bitmaps. The overlay of 
	 * two bitmaps is the bitmap which has a black pixel at every location at which either 
	 * input bitmap has a black pixel, and a white pixel at every location at which both 
	 * input bitmaps have a white pixel. Can be thought of as the bitwise OR of two bitmaps.
	 *
	 * Only supports bitmaps that are the same size.
	 *
	 * @param bmp1 the first of two quadtree bitmaps to compute the overlay of
	 * @param bmp2 the second of two quadtree bitmaps to compute the overlay of
	 * @return the newly constructed overlay of the two given quadtree bitmaps
	 */
	
	public static QuadtreeBitmap computeOverlay(QuadtreeBitmap bmp1, QuadtreeBitmap bmp2) {
		if(bmp1.getSize() == bmp2.getSize()){
			bmp1.slice();
			bmp2.slice();
			QuadtreeBitmap overlayedBitmap = new QuadtreeBitmap(bmp1.getSize());
			overlayedBitmap.slice();
			overlayedBitmap.computeOverlayHelper(bmp1, bmp2); 
			return overlayedBitmap;
			
		} else{
			return null;
		}
		
	}
	
	public QuadtreeBitmap computeOverlayHelper(QuadtreeBitmap quad1, QuadtreeBitmap quad2){
		if(quad1.leaf && quad2.leaf){
			if(quad1.colour.equals(colour.BLACK) || quad2.colour.equals(colour.BLACK)){
				this.colour = colour.BLACK;
				return this;
			}
			if(quad1.colour.equals(colour.WHITE) && quad1.colour.equals(quad2.colour)){
				this.colour = colour.WHITE;
				return this;
			}
		} else {
			//this.slice();
			this.northEast.computeOverlayHelper(quad1.northEast, quad2.northEast);
			this.northWest.computeOverlayHelper(quad1.northWest, quad2.northWest);
			this.southEast.computeOverlayHelper(quad1.southEast, quad2.southEast);
			this.southWest.computeOverlayHelper(quad1.southWest, quad2.southWest);
			simplifyBitmap(this);
			return this;
			}
		return null;
	}
	
	///////////////////////////////////////////////////
	// End of assignment methods
	///////////////////////////////////////////////////

	public static void main(String[] args) {
		/* This is here for you to optionally use for your own testing / running. 
		 * This method will NOT be tested. Feel free to experment here.
		 */
		System.out.println("Enter a string representation for a bitmap, "
				+ "followed by EOF or \"end\".");
		QuadtreeBitmap inputBmp = readBmpFromStream(System.in);
		System.out.println("  ");
		//System.out.println(inputBmp);
		
		//String inverted = inputBmp.invertColours();
		
		//System.out.print(inverted);
		inputBmp.slice();
		System.out.println(inputBmp.toTreeString());
		
		
	}
	
	// convenience method for constructing quadtrees from string representations of 
	// bitmaps from stdin
	public static QuadtreeBitmap readBmpFromStream(InputStream stream) {
		Scanner scanner = new Scanner(stream);
		List<String> lines = new ArrayList<String>();
		while (scanner.hasNext()) {
			String line = scanner.nextLine();
			if (line.equals("end")) {
				break;
			} else {
				lines.add(line);
			}
		}
		return fromString(String.join(System.lineSeparator(), lines));
	}
	
	////////////////////////////////////////////////////////////////////////
	// You do not need to concern yourself with the code beyond this point
	////////////////////////////////////////////////////////////////////////
	
	private static boolean powerOfTwo(int n) {
		try {
			int x = 1;
			while (x < n) {
				// throws ArithmeticException on overflow
				x = Math.multiplyExact(x, 2);
			}
			if (x == n) {
				return true;
			} else {
				return false;
			}
		} catch (ArithmeticException ex) {
			return false;
		}
	}
	
	/**
	 * Constructs a quadtree from the bitmap represented by the input string. Fails with an
	 * {@code IllegalArgumentException} if the input string does not properly encode a valid 
	 * bitmap.
	 * 
	 * @param bmpString input string to be converted into a quadtree bitmap
	 * @return a quadtree bitmap representation of the input string
	 */
	public static QuadtreeBitmap fromString(String bmpString) {
		validateBmpString(bmpString);
		return fromRowStrings(0, 0, Arrays.asList(bmpString.split("\\R")));
	}

	// recursive helper method for fromString
	private static QuadtreeBitmap fromRowStrings(int x, int y, List<String> rows) {
		int size = rows.size();
		if (!rows.stream().anyMatch(str -> str.contains(Colour.BLACK.toString()))) {
			// all white
			return new QuadtreeBitmap(x, y, size, Colour.WHITE);
		} else if (!rows.stream().anyMatch(str -> str.contains(Colour.WHITE.toString()))) {
			// all black
			return new QuadtreeBitmap(x, y, size, Colour.BLACK);
		} else {
			int xMid = x + size/2;
			int yMid = y + size/2;
			// split rows into quadrants
			List<String> nwRows = quadRowStrings(0, 0, rows);
			List<String> neRows = quadRowStrings(size/2, 0, rows);
			List<String> swRows = quadRowStrings(0, size/2, rows);
			List<String> seRows = quadRowStrings(size/2, size/2, rows);
			// build each subtree
			QuadtreeBitmap northWest = fromRowStrings(x, y, nwRows); 
			QuadtreeBitmap northEast = fromRowStrings(xMid, y, neRows);
			QuadtreeBitmap southWest = fromRowStrings(x, yMid, swRows);
			QuadtreeBitmap southEast = fromRowStrings(xMid, yMid, seRows);
			// combine
			List<QuadtreeBitmap> quads = Arrays.asList(northWest, northEast, southWest, southEast);
			return new QuadtreeBitmap(x, y, size, quads);
		}
	}

	// extracts row strings for quadrant from row strings for bitmap
	private static List<String> quadRowStrings(int xRel, int yRel, List<String> rows) {
		int size = rows.size();
		// sublist selects rows, substring selects columns
		return rows.subList(yRel, yRel + size/2).stream()
				.map(row -> row.substring(xRel, xRel + size/2))
				.collect(Collectors.toList());
	}

	// does nothing if input valid, communicates invalidity via exceptions
	private static void validateBmpString(String bmpString) {
		String[] rows = bmpString.split("\\R");
		if (rows.length == 0) {
			throw new IllegalArgumentException("Empty bitmap string.");
		} else if (!powerOfTwo(rows.length)) {
			throw new IllegalArgumentException("Number of rows not a power of 2.");
		} else if (!powerOfTwo(rows[0].length())) {
			throw new IllegalArgumentException("Row width not a power of 2.");
		} else {
			// using first row to determine row width
			int width = rows[0].length();
			for (int i = 1; i < rows.length; i++) {
				if (rows[i].length() != width) {
					throw new IllegalArgumentException(
							"Row " + i + " not same width as other rows.");
				}
			}
			for (String row : rows) {
				if (!row.codePoints().allMatch(Colour::isTexture)) {
					int ic = row.codePoints()
							.filter(c -> !Colour.isTexture(c))
							.findFirst().getAsInt();
					throw new IllegalArgumentException("Illegal character detected: " 
							+ "'" + String.valueOf(Character.toChars(ic)) + "'");
				}
			}
			if (rows.length != width) {
				throw new IllegalArgumentException("Number of rows not equal to row width.");
			}
		}
	}

	/**
	 * Returns a string representation of the tree structure of this quadtree bitmap. 
	 * The string representation is similar to the representation returned by {@link #toString},
	 * but with boxing interspersed to indicate the boundaries of the regions represented by 
	 * leaf nodes in the quadtree.
	 * 
	 * @return a string representation of this quadtree
	 */
	public String toTreeString() {
		char[][] canvas = new char[2*size + 1][2*size + 1];
		printTreeToCanvas(canvas, 2*x, 2*y);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < canvas.length; i++) {
			char[] row = canvas[i];
			for (char ch : row) {
				sb.append(ch);
			}
			if (i + 1 < canvas.length) {
				sb.append(System.lineSeparator());
			}
		}
		return sb.toString();
	}

	private final char CORNER = '+', V_WALL = '|', H_WALL = '-', FILLER = ' ';

	private void printTreeToCanvas(char[][] canvas, int xOffset, int yOffset) {
		if (leaf) {
			// top left is 2x - xOffset, 2y - yOffset
			int topY = 2*y - yOffset;
			int bottomY = 2*y + 2*size - yOffset;
			int leftX = 2*x - xOffset;
			int rightX = 2*x + 2*size - xOffset;
			// corners
			canvas[topY][leftX] = CORNER;
			canvas[topY][rightX] = CORNER;
			canvas[bottomY][leftX] = CORNER;
			canvas[bottomY][rightX] = CORNER;
			// top
			for (int i = leftX + 1; i < rightX; i++) {
				if (canvas[topY][i] != CORNER) {
					canvas[topY][i] = H_WALL;
				}
			}
			// bottom
			for (int i = leftX + 1; i < rightX; i++) {
				if (canvas[bottomY][i] != CORNER) {
					canvas[bottomY][i] = H_WALL;
				}
			}
			// left
			for (int i = topY + 1; i < bottomY; i++) {
				if (canvas[i][leftX] != CORNER) {
					canvas[i][leftX] = V_WALL;
				}
			}
			// right
			for (int i = topY + 1; i < bottomY; i++) {
				if (canvas[i][rightX] != CORNER) {
					canvas[i][rightX] = V_WALL;
				}
			}
			// fill every odd coordinate in interior
			for (int i = topY + 1; i < bottomY; i++) {
				for (int j = leftX + 1; j < rightX; j++) {
					canvas[i][j] = (i % 2 == 1 && j % 2 == 1) ? colour.getTexture() : FILLER;
				}
			}
		} else {
			for (QuadtreeBitmap quad : quadrants()) {
				quad.printTreeToCanvas(canvas, xOffset, yOffset);
			}
		}
	}

	/**
	 * Returns a string representation of this bitmap. The string representation consists
	 * of a newline-separated sequence of rows, where each row consists of a sequence of 
	 * characters which each encode the colour of a pixel.
	 *
	 * For a string representation which depicts the quadtree structure of this bitmap,
	 * see {@link #toTreeString}.
	 *
	 * @return a string representation of this bitmap
	 */
	@Override
	public String toString() {
		char[][] canvas = new char[size][size];
		printToCanvas(canvas, x, y);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < size; i++) {
			char[] row = canvas[i];
			for (char ch : row) {
				sb.append(ch);
			}
			if (i + 1 < size) {
				sb.append(System.lineSeparator());
			}
		}
		return sb.toString();
	}

	private void printToCanvas(char[][] canvas, int xOffset, int yOffset) {
		if (leaf) {
			for (int i = y - yOffset; i < y + size - yOffset; i++) {
				Arrays.fill(canvas[i], x - xOffset, x + size - xOffset, colour.getTexture());
			}
		} else {
			for (QuadtreeBitmap quad : quadrants()) {
				quad.printToCanvas(canvas, xOffset, yOffset);
			}
		}
	}

}