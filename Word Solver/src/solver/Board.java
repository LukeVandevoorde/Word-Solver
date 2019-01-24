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
	
	// Rotationally symmetric, x versus y order doesn't matter when retrieving
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
	
	// Rotationally symmetric like above
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
	private HashMap<Letter, Word> horizontalWords;
	private HashMap<Letter, Word> verticalWords;
	private int width, height;
	private boolean empty;
	
	public Board(int width, int height, String boardString) throws IllegalArgumentException {
		String illegalBoardString = "Invalid string representation of the board. Must be length <width*width*2> characters. Use '--' as a blank or represent each Letter with a lowercase char pair, letter and hexadecimal or single digit integer score.";
		
		if (boardString.length() != width*height*2)
			throw new IllegalArgumentException(illegalBoardString + " Expected length " + width*width*2 + " but found length " + boardString.length());
		
		Letter[][] letterBoard = new Letter[width][height];
		
		for (int y = 0; y < height; y++) {
			String str = boardString.substring(y*2*width, (y+1)*2*width);
			for (int i = 0; i < width; i++) {
				char c = str.charAt(2*i);
				char valueChar = str.charAt(2*i + 1);
				int value = Character.isDigit(valueChar) ? Integer.parseInt(valueChar + "") : (valueChar - 'a');
				
				if (Character.isAlphabetic(c)) {
					letterBoard[i][y] = new Letter(i, y, c, value);
				} else if (c != '-'){
					throw new IllegalArgumentException (illegalBoardString);
				}
			}
		}
		
		this.width = width;
		this.height = height;
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
		
		this.initializeCrosswords();
	}
	
	public Board (int width, int height) {
		this.board = new Letter[width][height];
		this.width = width;
		this.height = height;
		this.empty = true;
		this.initializeCrosswords();
	}
	
	public Board(int width, int height, Letter[][] letterGrid) {
		this.board = letterGrid;
		this.width = width;
		this.height = height;
		
		this.empty = true;
		outerLoop: for (Letter[] row: letterGrid) {
			for (Letter letter: row) {
				if (letter != null) {
					empty = false;
					break outerLoop;
				}
			}
		}
		
		this.initializeCrosswords();
	}
	
	private void initializeCrosswords () {
		System.out.println("Initializing crosswords");
		if (horizontalWords == null) {
			horizontalWords = new HashMap<Letter, Word>();
		}
		if (verticalWords == null) {
			verticalWords = new HashMap<Letter, Word>();
		}
		horizontalWords.clear();
		ArrayList<Letter> horizontalLetters = new ArrayList<Letter>();
		for (int j = 0; j < height; j++) {
			for (int i = 0; i < width; i++) {
				if (horizontalLetters.size() > 0) {
					if (this.getLetter(i, j) != null) {
						horizontalLetters.add(this.getLetter(i, j));
					} else {
						Word w = new Word(horizontalLetters, true);
						for (Letter l: horizontalLetters) {
							this.horizontalWords.put(l, w);
						}
						horizontalLetters.clear();
					}
				} else {
					if (this.getLetter(i, j) != null) {
						horizontalLetters.add(this.getLetter(i, j));
					}
				}
			}
		}
		verticalWords.clear();
		ArrayList<Letter> verticalLetters = new ArrayList<Letter>();
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				if (verticalLetters.size() > 0) {
					if (this.getLetter(i, j) != null) {
						verticalLetters.add(this.getLetter(i, j));
					} else {
						Word w = new Word(verticalLetters, true);
						for (Letter l: verticalLetters) {
							this.verticalWords.put(l, w);
						}
						verticalLetters.clear();
					}
				} else {
					if (this.getLetter(i, j) != null) {
						verticalLetters.add(this.getLetter(i, j));
					}
				}
			}
		}
	}
	
	public static class WordRank {

	    private Word[] words;
	    private int size;

	    public WordRank(int size) {
	    	this.size = size;
	        words = new Word[size];
        }
	    
	    public int size() {
	    	return size;
	    }
	    
	    public synchronized void addAll(WordRank words) {
	    	addAll(words.getWords());
	    }
	    
	    public synchronized void addAll(Word[] words) {
	    	for (Word w: words) {
	    		add(w);
	    	}
	    }
	    
        public synchronized void add(Word wordToAdd) {
        	if (wordToAdd == null || (words[words.length - 1] != null && wordToAdd.compareTo(words[words.length - 1]) <= 0)) {
        		return;
        	}
        	
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
    
    public static Word[] solveMegaThreaded(final Board board, final String availableLetters, final ProgressMonitor progressMonitor, final WordRank wordRank) {
    	validateAvailableLetters(availableLetters);
		
		if (availableLetters.contains("*")) {
			String avLetters = availableLetters;
			
			ArrayList<String> allSolveCases = new ArrayList<String>();
			String base = avLetters.replaceAll("\\*", "");
			int num = availableLetters.length() - base.length();
			
			if (num == 1) {
				for (char c = 'A'; c <= 'Z'; c++) {
					allSolveCases.add(base + c);
				}
			} else if (num == 2) {
				for (char c = 'A'; c <= 'Z'; c++) {
					for (char d = c; d <= 'Z'; d++) {
						allSolveCases.add(base + c + d);
					}
				}
			} else {
				for (char i = 'A'; i <= 'Z'; i++) {
					allSolveCases.add(base + i);
				}
				
				for (int i = 0; i < num - 1; i++) {
					for (char c = 'A'; c <= 'Z'; c++) {
						for (int j = 0; j < allSolveCases.size(); j++) {
							allSolveCases.set(j, allSolveCases.get(j) + c);
						}
					}
				}
			}
			
			WordRank bestWords = new WordRank(wordRank.size());
			float count = 0;
			for (String word: allSolveCases) {
				ProgressMonitor pm = (progress) -> {};
				count += 1;
				bestWords.addAll(solveMegaThreaded(board, word, pm, new WordRank(wordRank.size)));
				progressMonitor.update((int)(100*count/allSolveCases.size()));
			}
			
			return bestWords.words;
		} else {
			final ProgressTracker pt = new ProgressTracker(0, board.width*board.width);
			Thread[] rowProcessors = new Thread[board.width*board.height];
			
			final String[][] combos = StringTools.sizeCombinations(availableLetters, false);
			
			for (int y = 0; y < board.height; y++) {
				for (int x  = 0; x < board.width; x++) {
					final int xPos = x;
					final int yPos = y;
					Thread rowProcessor = new Thread(new Runnable() {
						@Override
						public void run() {
								if (board.getLetter(xPos, yPos) == null) {
									wordRank.add(bestSpotWord(xPos, yPos, board, combos));
								}

								pt.increment();

								progressMonitor.update(pt.getProgress());
							}
						}
					);

					rowProcessors[y*board.width + x] = rowProcessor;
					rowProcessor.start();
				}
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
    }
    
	public static Word[] solveThreaded(final Board board, final String availableLetters, final ProgressMonitor progressMonitor, final WordRank wordRank) {
		validateAvailableLetters(availableLetters);
		
		if (availableLetters.contains("*")) {
			String avLetters = availableLetters;
			
			ArrayList<String> allSolveCases = new ArrayList<String>();
			String base = avLetters.replaceAll("\\*", "");
			int num = availableLetters.length() - base.length();
			
			if (num == 1) {
				for (char c = 'A'; c <= 'Z'; c++) {
					allSolveCases.add(base + c);
				}
			} else if (num == 2) {
				for (char c = 'A'; c <= 'Z'; c++) {
					for (char d = c; d <= 'Z'; d++) {
						allSolveCases.add(base + c + d);
					}
				}
			} else {
				for (char i = 'A'; i <= 'Z'; i++) {
					allSolveCases.add(base + i);
				}
				
				for (int i = 0; i < num - 1; i++) {
					for (char c = 'A'; c <= 'Z'; c++) {
						for (int j = 0; j < allSolveCases.size(); j++) {
							allSolveCases.set(j, allSolveCases.get(j) + c);
						}
					}
				}
			}
			
			
			WordRank bestWords = new WordRank(wordRank.size());
			float count = 0;
			for (String word: allSolveCases) {
				ProgressMonitor pm = (progress) -> {};
				count += 1;
				bestWords.addAll(solveThreaded(board, word, pm, new WordRank(wordRank.size)));
				progressMonitor.update((int)(100*count/allSolveCases.size()));
			}
			
			return bestWords.words;
		} else {
			final ProgressTracker pt = new ProgressTracker(0, board.width*board.width);
			Thread[] rowProcessors = new Thread[board.width];
			
			final String[][] combos = StringTools.sizeCombinations(availableLetters, false);
			
			for (int y = 0; y < board.height; y++) {
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
    }
	
	private static void validateAvailableLetters (String letters) throws IllegalArgumentException {
		for (int i = 0; i < letters.length(); i++) {
			char c = letters.charAt(i);
			
			if (!(Character.isAlphabetic(c) || c == '*'))
				throw new IllegalArgumentException("Solve attempted with invalid letter: " + c);
		}
	}
	
	
	
	private static Word bestSpotWord(int x, int y, Board board, String[][] availableStrings) {
		if (board.getLetter(x, y) != null)
			return null;
		
		Word bestWord = null;
		
		// Determining Min and Max possible lengths for words in this spot
		int xMinLength = 1;
		int yMinLength = 1;
		
		String test = "";
		for (int i = 1; i <= availableStrings.length; i++) {
			test += "a";
			if (!(new Word(test, x, y, board, true).touchesBoard())) {
				xMinLength += 1;
			}
			if (!(new Word(test, x, y, board, false).touchesBoard())) {
				yMinLength += 1;
			}
		}
		int xMaxLength = new Word(test, x, y, board, true).newLetterSize();//.newLetters().size();
		int yMaxLength = new Word(test, x, y, board, false).newLetterSize();
		
		for (int i = Math.min(xMinLength, yMinLength); i <= Math.max(xMaxLength, xMaxLength); i++) {
			for (String letters: availableStrings[i-1]) {
				if (i >= xMinLength && i <= xMaxLength) {
					bestWord = Word.bestWord(bestWord, new Word(letters, x, y, board, true));
				}
				
				if (i >= yMinLength && i <= yMaxLength) {
					bestWord = Word.bestWord(bestWord, new Word(letters, x, y, board, false));
				}
			}
		}
		
		
//		for (int i = 0; i < availableStrings.length; i++) {
//			for (String letters: availableStrings[i]) {
//				bestWord = Word.bestWord(bestWord,  new Word(letters, x, y, board, true));
//				bestWord = Word.bestWord(bestWord, new Word(letters, x, y, board, false));
//			}
//		}
		
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
		
		this.initializeCrosswords();
	}
	
	public boolean isEmpty() {
		return empty;
	}
	
	public Word getHorizontalCrossword(Letter l) {
		return this.horizontalWords.get(l);
	}
	
	public Word getVerticalCrossword(Letter l) {
		return this.verticalWords.get(l);
	}
	
	public void add (Word word) {
		for (Letter n: word.allLetters()) {
			this.add(n);
		}
	}
	
	public void add (Letter l) {
		if (board[l.getX()][l.getY()] != null || l.getX() < 0 || l.getX() >= width || l.getY() < 0 || l.getY() >= height)
			return;
		board[l.getX()][l.getY()] = l;
		this.initializeCrosswords();
	}
	
	public int letterMultiplier(int x, int y) {
		if (x < 0 || x >= width || y < 0 || y >= height) {
			return 1;
		}
		return letterMultipliers[y][x];
	}
	
	public int wordMultiplier(int x, int y) {
		if (x < 0 || x >= width || y < 0 || y >= height) {
			return 1;
		}
		return wordMultipliers[x][y];
	}
	
	public int width() {
		return width;
	}
	
	public int height() {
		return height;
	}
	
	public Letter getLetter(int x, int y) {
		if (x < 0 || x >= width || y < 0 || y >= height)
			return null;
		
		return board[x][y];
	}
	
	public String readableString() {
		String out = "";
		for (int i = 0; i < height; i++) {
			out += "|";
			for (int j = 0; j < width; j++) {
				Letter l = this.getLetter(j, i);
				if (l != null) {
					out += l.getLetter() + "|";
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
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				if (board[j][i] != null) {
					out += board[j][i].getLetter() + "" + (char) ('a' + board[j][i].value());
				} else {
					out += "--";
				}
			}
		}
		return out;
	}
}
