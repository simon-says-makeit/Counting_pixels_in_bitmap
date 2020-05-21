
public enum Colour {
	WHITE('.'),
	BLACK('*');

	// character for printing as text
	char texture;
	Colour(char texture) {
		this.texture = texture;
	}

	/**
	 * Returns a character representation of this colour, to be used for rendering as a string.
	 *
	 * @return a character representation of this colour
	 */
	public char getTexture() {
		return this.texture;
	}
	/**
	 * Returns a string representation of this colour.
	 *
	 * @return a string representation of this colour
	 */
	public String toString() {
		return Character.toString(texture);
	}

	/**
	 * Returns true if the unicode code point given by the input corresponds to the texture of 
	 * a valid colour.
	 * 
	 * @param c a unicode code point
	 * @return true if c corresponds to a valid colour, false otherwise
	 */
	public static boolean isTexture(int c) {
		if (!Character.isBmpCodePoint(c)) {
			return false;
		}
		char ch = (char)c;
		for (Colour colour : Colour.values()) {
			if (colour.texture == ch) {
				return true;
			}
		}
		return false;
	}
}
