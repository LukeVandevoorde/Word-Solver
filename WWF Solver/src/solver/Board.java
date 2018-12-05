package solver;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

public class Board {
	
	private static int[][] wordMultipliers = new int[][] {
		{1,1,1,3,1,1,1,1,1,1,1,3,1,1,1},
		{1,1,1,1,1,2,1,1,1,2,1,1,1,1,1},
		{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
		{3,1,1,1,1,1,1,2,1,1,1,1,1,1,3},
		{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
		{1,2,1,1,1,1,1,1,1,1,1,1,1,2,1},
		{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
		{1,1,1,2,1,1,1,1,1,1,1,2,1,1,1}, // MIDDLE ROW
		{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
		{1,2,1,1,1,1,1,1,1,1,1,1,1,2,1},
		{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
		{3,1,1,1,1,1,1,2,1,1,1,1,1,1,3},
		{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
		{1,1,1,1,1,2,1,1,1,2,1,1,1,1,1},
		{1,1,1,3,1,1,1,1,1,1,1,3,1,1,1}
	};
	
	private static int[][] letterMultipliers = new int[][] {
		
		{1,1,1,1,1,1,3,1,3,1,1,1,1,1,1},
		{1,1,2,1,1,1,1,1,1,1,1,1,2,1,1},
		{1,2,1,1,2,1,1,1,1,1,2,1,1,2,1},
		{1,1,1,3,1,1,1,1,1,1,1,3,1,1,1},
		{1,1,2,1,1,1,2,1,2,1,1,1,2,1,1},
		{1,1,1,1,1,3,1,1,1,3,1,1,1,1,1},
		{3,1,1,1,2,1,1,1,1,1,2,1,1,1,3},
		{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}, // MIDDLE ROW
		{3,1,1,1,2,1,1,1,1,1,2,1,1,1,3},
		{1,1,1,1,1,3,1,1,1,3,1,1,1,1,1},
		{1,1,2,1,1,1,2,1,2,1,1,1,2,1,1},
		{1,1,1,3,1,1,1,1,1,1,1,3,1,1,1},
		{1,2,1,1,2,1,1,1,1,1,2,1,1,2,1},
		{1,1,2,1,1,1,1,1,1,1,1,1,2,1,1},
		{1,1,1,1,1,1,3,1,3,1,1,1,1,1,1},
	};
	
	private Letter[][] board;
	private int width;
	private boolean empty;
	
	public Board(int width, String boardString) throws IllegalArgumentException {
		String illegalBoardString = "Invalid string representation of the board. Must be length <width*width*2> characters. Use '--' as a blank or represent each Letter with a lowercase char pair, letter and hexadecimal or single digit integer score.";
		
		if (boardString.length() != width*width*2)
			throw new IllegalArgumentException(illegalBoardString + " Expected length " + width*width*2 + " but found length " + boardString.length());
		
		Letter[][] letterBoard = new Letter[width][width];
		
		for (int y = 0; y < width; y++) {
			String str = boardString.substring(y*2*width, (y+1)*2*15);
			for (int i = 0; i < width; i++) {
				char c = str.charAt(2*i);
				char valueChar = str.charAt(2*i + 1);
				int value = Character.isDigit(valueChar) ? Integer.parseInt(valueChar + "") : 10 + (valueChar - 'a');
				
				if (Character.isAlphabetic(c)) {
					letterBoard[y][i] = new Letter(i, y, c, value);
				} else if (c != '-'){
					throw new IllegalArgumentException (illegalBoardString);
				}
			}
		}
		
		this.width = width;
		this.board = letterBoard;
		
		this.empty = true;
		outerLoop: for (Letter[] row: board) {
			for (Letter letter: row) {
				if (letter != null) {
					empty = false;
					break outerLoop;
				}
			}
		}
	}
	
	public Board(int width, Letter[][] letterGrid) {
				
		this.board = letterGrid;
		this.width = width;
		
		this.empty = true;
		outerLoop: for (Letter[] row: letterGrid) {
			for (Letter letter: row) {
				if (letter != null) {
					empty = false;
					break outerLoop;
				}
			}
		}
	}
	
	public static class WordRank {

	    private Word[] words;

	    public WordRank(int size) {
	        words = new Word[size];
        }

        public synchronized void add(Word wordToAdd) {
            for (int index = 0; index < words.length; index++) {
                if (wordToAdd != null && (words[index] == null || wordToAdd.compareTo(words[index]) > 0)) {
                    for (int shift = words.length - 1; shift > index; shift--) {
                        words[shift] = words[shift - 1];
                    }

                    words[index] = wordToAdd;

                    break;
                }
            }
        }

        public synchronized Word[] getWords() {
	        return words;
        }
    }

    private static class ProgressTracker {

		private int progress;
		private float totalOperations;

		public ProgressTracker(int progress, int totalOperations) {
			this.progress = progress;
			this.totalOperations = totalOperations;
		}

		public synchronized void increment() {
			progress += 1;
		}

		public int getProgress() {
			return (int) (100*progress/totalOperations + 0.5);
		}
	}
    
    public interface ProgressMonitor {
    	public void update(int progress);
    }

	public static Word[] solveThreaded(final Board board, final String availableLetters, final ProgressMonitor progressMonitor, final WordRank wordRank) {
		validateAvailableLetters(availableLetters);
		final ProgressTracker pt = new ProgressTracker(0, board.width*board.width);
		Thread[] rowProcessors = new Thread[board.width];
		
		final String[][] combos = StringTools.sizeCombinations(availableLetters, false);
		
		for (int y = 0; y < board.width; y++) {
			final int yPos = y;
			Thread rowProcessor = new Thread(new Runnable() {
				@Override
				public void run() {
					for (int x = 0; x < board.width; x++) {
						if (board.getLetter(x, yPos) == null) {
							wordRank.add(bestSpotWord(x, yPos, board, combos));
						}

						pt.increment();

						progressMonitor.update(pt.getProgress());
					}
				}
			});

			rowProcessors[y] = rowProcessor;
			rowProcessor.start();
		}

		for (Thread t: rowProcessors) {
			try {
				t.join();
			} catch (InterruptedException e) {
				System.out.println(e.getStackTrace());
			}
		}
		
		return wordRank.getWords();
    }
	
	private static void validateAvailableLetters (String letters) throws IllegalArgumentException {
		for (int i = 0; i < letters.length(); i++) {
			char c = letters.charAt(i);
			
			if (!(Character.isLowerCase(c) && Character.isAlphabetic(c) || c == '*'))
				throw new IllegalArgumentException("Solve attempted with invalid letter: " + c);
		}
	}
	
	
	
	private static Word bestSpotWord(int x, int y, Board board, String[][] availableStrings) {
		if (board.getLetter(x, y) != null)
			return null;
		
		Word bestWord = null;
		
		// Determining Min and Max possible lengths for words in this spot
		int xMinLength = 1;
		int xMaxLength = Math.min(board.width - x, availableStrings.length);
		
		String test = "";
		for (int i = 1; i <= xMaxLength; i++) {
			test += "a";
			if (!(new Word(test, board, x, y, true).touchesBoard())) {
				xMinLength += 1;
			}
		}
		xMaxLength = new Word(test, board, x, y, true).newLetters().size();
		
		int yMinLength = 1;
		int yMaxLength = Math.min(board.width - x, availableStrings.length);
		
		test = "";
		for (int i = 1; i <= xMaxLength; i++) {
			test += "a";
			if (!(new Word(test, board, x, y, false).touchesBoard())) {
				yMinLength += 1;
			}
		}
		yMaxLength = new Word(test, board, x, y, true).newLetters().size();
		
		for (int i = Math.min(xMinLength, yMinLength); i <= Math.max(xMaxLength, xMaxLength); i++) {
			
			for (String letters: availableStrings[i-1]) {
				if (i >= xMinLength && i <= xMaxLength) {
					bestWord = Word.bestWord(bestWord, new Word(letters, board, x, y, true));
				}
				
				if (i >= yMinLength && i <= yMaxLength) {
					bestWord = Word.bestWord(bestWord, new Word(letters, board, x, y, false));
				}
			}
		}
		
		return bestWord;
	}
	
	public void setGrid(Letter[][] letters) {
		this.board = letters;
		this.width = letters.length;
		this.empty = true;
		outerLoop: for (Letter[] row: board) {
			for (Letter letter: row) {
				if (letter != null) {
					empty = false;
					break outerLoop;
				}
			}
		}
	}
	
	public boolean empty() {
		return empty;
	}
	
	/**
	 * Sets the score and validity of word 
	 * @param word Word being updated
	 */
	public void updateWord(Word word) {
		if (!StringTools.validString(word.word()) || !word.touchesBoard()) {
			word.setScore(-1);
			word.setValid(false);
			return;
		}
		
		HashMap<Letter, Word> crossWords = word.crossWords();
		
		int score = 0;
		
		for (Word w: crossWords.values()) {
			if (!StringTools.validString(w.word())) {
				word.setScore(-1);
				word.setValid(false);
				return;
			}
			
			int subWordScore = 0;
			int mult = 1;
			
			for (Letter l: w.allLetters()) {
				if (word.newLetters().contains(l)) {
					mult *= this.wordMultiplier(l.getX(), l.getY());
					subWordScore += l.value() * this.letterMultiplier(l.getX(), l.getY());
				} else {
					subWordScore += l.value();
				}
			}
			score += subWordScore * mult;
		}
		
		int mult = 1;
		int wordSum = 0;
		
		for (Letter letter: word.allLetters()) {
			if (word.newLetters().contains(letter)) {
				mult *= this.wordMultiplier(letter.getX(), letter.getY());
				wordSum += letter.value() * this.letterMultiplier(letter.getX(), letter.getY());
			} else {
				wordSum += letter.value();
			}
		}
		
		score += wordSum * mult;
		
		if (word.newLetters().size() >= 7) {
			score += 35;
		}
		
		word.setScore(score);
		word.setValid(true);
	}
	
	public void add (Word word) {
		for (Letter n: word.newLetters()) {
			add(n);
		}
	}
	
	public void add (Letter l) {
		if (l.getX() < 0 || l.getX() >= width || l.getY() < 0 || l.getY() >= width || board[l.getY()][l.getX()] != null)
			return;
		board[l.getY()][l.getX()] = l;
	}
	
	public int letterMultiplier(int x, int y) {
		if (x < 0 || x >= width || y < 0 || y >= width) {
			return 1;
		}
		return letterMultipliers[y][x];
	}
	
	public int wordMultiplier(int x, int y) {
		if (x < 0 || x >= width || y < 0 || y >= width) {
			return 1;
		}
		return wordMultipliers[y][x];
	}
	
	public int width() {
		return width;
	}
	
	public Letter getLetter(int x, int y) {
		if (x < 0 || x >= width || y < 0 || y >= width)
			return null;
		
		return board[y][x];
	}
	
	public String readableString() {
		String out = "";
		for (int i = 0; i < width; i++) {
			out += "|";
			for (int j = 0; j < width; j++) {
				if (board[i][j] != null) {
					out += board[i][j].getLetter() + "|";
				} else {
					out += " |";
				}
			}
			out += "\n";
		}
		return out;
	}
	
	@Override
	public String toString() {
		String out = "";
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < width; j++) {
				if (board[i][j] != null) {
					out += board[i][j].getLetter() + "" + (char) ('a' + board[i][j].value());
				} else {
					out += "--";
				}
			}
		}
		return out;
	}
}
