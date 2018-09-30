package solver;
import java.util.HashMap;

public class Letter {
	
	private static final HashMap<Character, Integer> LETTER_VALUES = new HashMap<Character, Integer>();
	
	static {
		LETTER_VALUES.put('a', 1);
		LETTER_VALUES.put('b', 4);
		LETTER_VALUES.put('c', 4);
		LETTER_VALUES.put('d', 2);
		LETTER_VALUES.put('e', 1);
		LETTER_VALUES.put('f', 4);
		LETTER_VALUES.put('g', 3);
		LETTER_VALUES.put('h', 3);
		LETTER_VALUES.put('i', 1);
		LETTER_VALUES.put('j', 10);
		LETTER_VALUES.put('k', 5);
		LETTER_VALUES.put('l', 2);
		LETTER_VALUES.put('m', 4);
		LETTER_VALUES.put('n', 2);
		LETTER_VALUES.put('o', 1);
		LETTER_VALUES.put('p', 4);
		LETTER_VALUES.put('q', 10);
		LETTER_VALUES.put('r', 1);
		LETTER_VALUES.put('s', 1);
		LETTER_VALUES.put('t', 1);
		LETTER_VALUES.put('u', 2);
		LETTER_VALUES.put('v', 5);
		LETTER_VALUES.put('w', 4);
		LETTER_VALUES.put('x', 8);
		LETTER_VALUES.put('y', 3);
		LETTER_VALUES.put('z', 10);
	}
	
	private int x, y, value;
	private char letter;
	
	public Letter(int x, int y, char letter) {
		this.x = x;
		this.y = y;
		this.letter = letter;
		this.value = LETTER_VALUES.get(letter);
	}
	
	public Letter(int x, int y, char letter, int value) {
		this.x = x;
		this.y = y;
		this.letter = letter;
		this.value = value;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY( ) {
		return y;
	}
	
	public char getLetter() {
		return letter;
	}
	
	public int value() {
		return value;
	}
}
