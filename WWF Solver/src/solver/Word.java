package solver;
import java.util.ArrayList;
import java.util.HashMap;

public class Word implements Comparable<Word> {

	private ArrayList<Letter> allLetters;
	private ArrayList<Letter> newLetters;

	private Board board;
	private String word;
	private boolean horizontal;
	private int x, y;
	private int score;
	private boolean valid;

	public Word(String newLetterString, Board board, int x, int y, boolean horizontal) {
		this.x = x;
		this.y = y;
		this.horizontal = horizontal;

		this.board = board;
		this.allLetters = new ArrayList<Letter>();
		this.newLetters = new ArrayList<Letter>();

		this.word = "";

		if (horizontal) {
			// letters touching and in the same orientation are included as word start
			while (board.getLetter(this.x - 1, this.y) != null) {
				this.x -= 1;
			}

			int xpos = this.x;
			int ypos = this.y;

			for (int i = 0; i < newLetterString.length(); i++) {

				// Add existing letters if there is not enough room for the entire word
				Letter boardLetter = board.getLetter(xpos, ypos);

				while (boardLetter != null) {
					this.allLetters.add(boardLetter);
					this.word += boardLetter.getLetter();
					xpos += 1;
					boardLetter = board.getLetter(xpos, ypos);
				}

				if (xpos >= board.width()) {
					break;
				}

				Letter newLetter = new Letter(xpos, ypos, newLetterString.charAt(i));
				this.allLetters.add(newLetter);
				this.newLetters.add(newLetter);
				this.word += newLetter.getLetter();
				xpos += 1;
			}
			// Letters after the end of the word
			Letter boardLetter = board.getLetter(xpos, ypos);
			while (boardLetter != null) {
				this.allLetters.add(boardLetter);
				this.word += boardLetter.getLetter();
				xpos += 1;
				boardLetter = board.getLetter(xpos, ypos);
			}
		} else {
			// letters touching and in the same orientation are included as word start
			while (board.getLetter(this.x, this.y - 1) != null) {
				this.y -= 1;
			}

			int xpos = this.x;
			int ypos = this.y;

			for (int i = 0; i < newLetterString.length(); i++) {

				// Add existing letters if there is not enough room for the entire word
				Letter boardLetter = board.getLetter(xpos, ypos);

				while (boardLetter != null) {
					this.allLetters.add(boardLetter);
					this.word += boardLetter.getLetter();
					ypos += 1;
					boardLetter = board.getLetter(xpos, ypos);
				}

				if (ypos >= board.width()) {
					break;
				}

				Letter newLetter = new Letter(xpos, ypos, newLetterString.charAt(i));
				this.allLetters.add(newLetter);
				this.newLetters.add(newLetter);
				this.word += newLetter.getLetter();
				ypos += 1;
			}
			// Letters after the end of the word
			Letter boardLetter = board.getLetter(xpos, ypos);
			while (boardLetter != null) {
				this.allLetters.add(boardLetter);
				this.word += boardLetter.getLetter();
				ypos += 1;
				boardLetter = board.getLetter(xpos, ypos);
			}
		}
		
		board.updateWord(this);
	}
	
	private Word(ArrayList<Letter> letters, String word, Board board) {
		this.board = board;
		this.allLetters = letters;
		this.word = word;
	}

	/**
	 * Should be used basically as a wrapper for a list of letters. All
	 * functionality not guaranteed.
	 * 
	 * @return Word with potentially limited functionality
	 */
	public static Word letterStorage(ArrayList<Letter> letters, String word, Board board) {
		return new Word(letters, word, board);
	}

	@Override
	public String toString() {
		return this.word + ", x: " + this.x + ", y: " + this.y + ", HORIZONTAL: " + horizontal + ", SCORE: " + this.score + ", VALID: " + this.valid;
	}
	
	public int getX() {
		return this.x;
	}
	
	public int getY() {
		return this.y;
	}
	
	public boolean horizontal() {
		return this.horizontal;
	}
	
	public String newWord() {
		String out = "";
		for(Letter l: newLetters) {
			out += l.getLetter();
		}
		return out;
	}
	
	public String word() {
		return this.word;
	}

	public boolean touchesBoard() {
		if (allLetters.size() - newLetters.size() > 0)
			return true;

		if (horizontal) {
			if (board.empty())
				return (this.y == 7 && this.x <= 7 && this.x >= (7-this.allLetters.size()));
			
			for (Letter horizontalLetter : newLetters) {
				if (board.getLetter(horizontalLetter.getX(), horizontalLetter.getY() + 1) != null
						|| board.getLetter(horizontalLetter.getX(), horizontalLetter.getY() - 1) != null) {
					return true;
				}
			}
		} else {
			if (board.empty())
				return (this.x == 7 && this.y <= 7 && this.y >= (7-this.allLetters.size()));
			
			for (Letter horizontalLetter : newLetters) {
				if (board.getLetter(horizontalLetter.getX() + 1, horizontalLetter.getY()) != null
						|| board.getLetter(horizontalLetter.getX() - 1, horizontalLetter.getY()) != null) {
					return true;
				}
			}
		}

		return false;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}
	
	@Override
	public int compareTo(Word other) {
		if (this.valid) {
			if (other.valid) {
				return this.score - other.score;
			}
			return 1;
		} else {
			if (!other.valid) {
				return 0;
			}
			return -1;
		}
	}

	public HashMap<Letter, Word> crossWords() {
		HashMap<Letter, Word> crossWords = new HashMap<Letter, Word>();
		if (horizontal) {
			for (Letter l : newLetters) {
				ArrayList<Letter> crossWord = new ArrayList<Letter>();
				String crossString = "";
				int xpos = l.getX();
				int ypos = l.getY();
				Letter boardLetter;
				while ((boardLetter = board.getLetter(xpos, ypos - 1)) != null) {
					ypos -= 1;
				}
				while ((boardLetter = board.getLetter(xpos, ypos)) != null || ypos == l.getY()) {
					if (ypos == l.getY())
						boardLetter = l;
					
					crossWord.add(boardLetter);
					crossString += boardLetter.getLetter();
					ypos += 1;
				}
				if (crossWord.size() > 1) {
					crossWords.put(l, Word.letterStorage(crossWord, crossString, board));
				}
			}
		} else {
			for (Letter l : newLetters) {
				ArrayList<Letter> crossWord = new ArrayList<Letter>();
				String crossString = "";
				int xpos = l.getX();
				int ypos = l.getY();
				Letter boardLetter;
				while ((boardLetter = board.getLetter(xpos - 1, ypos)) != null) {
					xpos -= 1;
				}
				while ((boardLetter = board.getLetter(xpos, ypos)) != null || xpos == l.getX()) {
					if (xpos == l.getX())
						boardLetter = l;
					crossWord.add(boardLetter);
					crossString += boardLetter.getLetter();
					xpos += 1;
				}
				if (crossWord.size() > 1) {
					crossWords.put(l, Word.letterStorage(crossWord, crossString, board));
				}
			}
		}
		return crossWords;
	}

	public boolean valid() {
		return valid;
	}

	public int score() {
		return score;
	}

	public ArrayList<Letter> allLetters() {
		return this.allLetters;
	}

	public ArrayList<Letter> newLetters() {
		return this.newLetters;
	}
}
