package solver;
import java.util.ArrayList;
import java.util.HashMap;

public class Word implements Comparable<Word> {

	private ArrayList<Letter> allLetters;

	private Board board;
	private String word;
	private boolean horizontal;
	private boolean touchesBoard;
	private int x, y;
	private int score;
	private int newLetterSize;
	private boolean valid;
	
	public Word (ArrayList<Letter> letters, boolean horizontal) {
		this.allLetters = letters;
		this.horizontal = horizontal;
		this.valid = true;
		this.touchesBoard = true;
		this.x = letters.get(0).getX();
		this.y = letters.get(0).getY();
		this.score = 0;
		this.word = "";
		
		for (Letter l: letters) {
			word += l.getLetter();
			score += l.value();
		}
	}
	
	public Word(String newLetters, int x, int y, Board board, boolean horizontal) {
		this.allLetters = new ArrayList<Letter>();
		this.x = x;
		this.y = y;
		this.board = board;
		this.horizontal = horizontal;
		this.valid = true;
		this.touchesBoard = false;
		this.score = 0;
		this.newLetterSize = newLetters.length();
		int wordMultiplier = 1;
		int crossScore = 0;
		
		StringBuilder wordBuilder = new StringBuilder();
		
		if (horizontal) {
			int xPos = x;
			if (board.getLetter(xPos-1, y) != null) {
				Word prefix = board.getHorizontalCrossword(board.getLetter(xPos-1, y));
				this.allLetters.addAll(prefix.allLetters);
				wordBuilder.append(prefix.word);
				this.score += prefix.score;
				this.touchesBoard = true;
				
				xPos = prefix.x + prefix.word.length();
			}
			
			for (int i = 0; i < newLetters.length(); i++) {
				if (xPos > board.width() - 1) {
					this.valid = false;
					this.newLetterSize = i + 1;
					break;
				}
				if (board.getLetter(xPos, y) != null) {
					Word w = board.getHorizontalCrossword(board.getLetter(xPos, y));
					this.allLetters.addAll(w.allLetters);
					this.score += w.score;
					xPos += w.word.length();
					wordBuilder.append(w.word);
				} else {
					Letter l = new Letter(xPos, y, newLetters.charAt(i));
					this.allLetters.add(l);
					int lScore = l.value() * board.letterMultiplier(xPos, y);
					wordMultiplier *= board.wordMultiplier(xPos, y);
					this.score += lScore;
					
					Letter up = board.getLetter(xPos, y-1);
					Letter down = board.getLetter(xPos, y+1);
					String upCross = "";
					String downCross = "";
					
//					if (up != null || down != null) {
//						this.score += lScore;
//						this.touchesBoard = true;
//					}
//					
//					if (up != null) {
//						Word upWord = board.getVerticalCrossword(up);
//						upCross = upWord.word;
//						this.score += upWord.score;
//					}
//					if (down != null) {
//						Word downWord = board.getVerticalCrossword(down);
//						downCross = downWord.word;
//						this.score += downWord.score;
//					}
					if (up != null || down != null) {
						crossScore += lScore;
						this.touchesBoard = true;
					}
					
					if (up != null) {
						Word upWord = board.getVerticalCrossword(up);
						upCross = upWord.word;
						crossScore += upWord.score;
					}
					if (down != null) {
						Word downWord = board.getVerticalCrossword(down);
						downCross = downWord.word;
						crossScore += downWord.score;
					}
					
					if ((up != null || down != null) && !StringTools.validString(upCross + l.getLetter() + downCross)) {
						this.valid = false;
					}
					
					wordBuilder.append(l.getLetter());
					xPos += 1;
				}
			}
			
			if (board.getLetter(xPos, y) != null) {
				Word suffix = board.getHorizontalCrossword(board.getLetter(xPos, y));
				this.allLetters.addAll(suffix.allLetters);
				wordBuilder.append(suffix.word);
				this.touchesBoard = true;
				this.score += suffix.score;
			}
		} else {
			int yPos = y;
			if (board.getLetter(x, yPos - 1) != null) {
				Word prefix = board.getVerticalCrossword(board.getLetter(x, yPos - 1));
				this.allLetters.addAll(prefix.allLetters);
				wordBuilder.append(prefix.word);
				this.touchesBoard = true;
				this.score += prefix.score;
				
				yPos = prefix.y + prefix.word.length();
			}
			
			for (int i = 0; i < newLetters.length(); i++) {
				if (yPos > board.height() - 1) {
					this.valid = false;
					this.newLetterSize = i + 1;
					break;
				}
				if (board.getLetter(x, yPos) != null) {
					Word w = board.getVerticalCrossword(board.getLetter(x, yPos));
					this.allLetters.addAll(w.allLetters);
					this.score += w.score;
					yPos += w.word.length();
					wordBuilder.append(w.word);
				} else {
					Letter l = new Letter(x, yPos, newLetters.charAt(i));
					this.allLetters.add(l);
					int lScore = l.value() * board.letterMultiplier(x, yPos);
					wordMultiplier *= board.wordMultiplier(x, yPos);
					this.score += lScore;
					
					Letter left = board.getLetter(x-1, yPos);
					Letter right = board.getLetter(x+1, yPos);
					
					String leftCross = "";
					String rightCross = "";
					
//					if (left != null || right != null) {
//						this.score += lScore;
//						this.touchesBoard = true;
//					}
//					
//					if (left != null) {
//						Word leftWord = board.getHorizontalCrossword(left);
//						leftCross = leftWord.word;
//						this.score += leftWord.score;
//					}
//					if (right != null) {
//						Word rightWord = board.getHorizontalCrossword(right);
//						rightCross = rightWord.word;
//						this.score += rightWord.score;
//					}
//					
//					if ((left != null || right != null) && !StringTools.validString(leftCross + l.getLetter() + rightCross)) {
//						this.valid = false;
//					}
					if (left != null || right != null) {
						crossScore += lScore;
						this.touchesBoard = true;
					}
					
					if (left != null) {
						Word leftWord = board.getHorizontalCrossword(left);
						leftCross = leftWord.word;
						crossScore += leftWord.score;
					}
					if (right != null) {
						Word rightWord = board.getHorizontalCrossword(right);
						rightCross = rightWord.word;
						crossScore += rightWord.score;
					}
					
					if ((left != null || right != null) && !StringTools.validString(leftCross + l.getLetter() + rightCross)) {
						this.valid = false;
					}
					
					wordBuilder.append(l.getLetter());
					yPos += 1;
				}
			}
			
			if (board.getLetter(x, yPos) != null) {
				Word suffix = board.getVerticalCrossword(board.getLetter(x, yPos));
				this.allLetters.addAll(suffix.allLetters);
				wordBuilder.append(suffix.word);
				this.touchesBoard = true;
				this.score += suffix.score;
			}
		}
		
		this.score *= wordMultiplier;
		if (newLetters.length() >= 7) {
			this.score += 35;
		}
		this.score += crossScore;
		
		this.word = wordBuilder.toString();
		
		if (!this.touchesBoard && board.isEmpty()) {
			this.touchesBoard = (this.horizontal && this.y == 7 && this.x <= 7 && this.x + this.word.length() >= 7) || 
					(!this.horizontal && this.x == 7 && this.y <= 7 && this.y + this.word.length() >= 7);
		}
		
		if (!StringTools.validString(this.word) || !this.touchesBoard) {
			this.valid = false;
		}
	}
	
	/**
	 * Returns the higher scoring, valid word, or null if neither are valid
	 * @param one First word to be compared
	 * @param two Second word to be compared
	 * @return
	 */
	public static Word bestWord(Word one, Word two) {
		if (one != null && two != null) {
			if (one.compareTo(two) > 0)
				return one;
			return two;
		} else if (one != null) {
			return one;
		} else if (two != null) {
			return two;
		}
		
		return null;
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
	
	public String word() {
		return this.word;
	}
	
	public boolean touchesBoard() {
		return this.touchesBoard;
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
	
	public int newLetterSize() {
		return this.newLetterSize;
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
}
