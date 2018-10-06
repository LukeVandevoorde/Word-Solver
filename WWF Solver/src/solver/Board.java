package solver;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
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
	
	private static final String[] VALID_WORDS;
	
	static {
		ArrayList<String> dict = new ArrayList<String>();
		
//		String pathSep = File.pathSeparator;
		
		
		try (
//			FileReader reader = new FileReader(".." + pathSep + "ws_dict.txt");
//			Scanner scan = new Scanner(reader);
//			) {
//			
//			while(scan.hasNextLine()) {
//				String line = scan.nextLine();
//				dict.add(line);
//			}
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("ws_dict.txt")));
			
		) {
			String line;
			while ((line = reader.readLine()) != null) {
				dict.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		VALID_WORDS = new String[dict.size()];
		
		for(int i = 0; i < dict.size(); i++) {
			VALID_WORDS[i] = dict.get(i);
		}
	}
	
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

//		ArrayList<String> allCombos = allCombinations(new ArrayList<String>(), availableLetters, "");
//		final String[] combos = new String[allCombos.size()];
		final String[][] combos = Board.allCombinations(availableLetters);
		
//		for (int i = 0; i < allCombos.size(); i++) {
//			combos[i] = allCombos.get(i);
//		}
		
		for (int y = 0; y < board.width; y++) {
			final int yPos = y;
			Thread rowProcessor = new Thread(new Runnable() {
				@Override
				public void run() {
//					Word bestWord = null;

					for (int x = 0; x < board.width; x++) {
						if (board.getLetter(x, yPos) == null) {
//							Word bestSpotWord = bestSpotWord(x, yPos, board, combos/*, wordRank*/);
							wordRank.add(bestSpotWord(x, yPos, board, combos));
//							bestWord = bestWord(bestSpotWord, bestWord);
//							wordRank.add(bestWord);
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
	
//	public static Word[] solve(Board board, String availableLetters) throws IllegalArgumentException {
//		validateAvailableLetters(availableLetters);
//		
//		Word[] bestWords = new Word[5];
//		
//		for (int i = 0; i < board.width; i++) {
//			for (int j = 0; j < board.width; j++) {
//				if (board.getLetter(i, j) == null) {
//					Word bestSpotWord = bestSpotWord(i, j, board, availableLetters, "");
//					
//					insertLoop: for (int index = 0; index < bestWords.length; index++) {
//						if (bestSpotWord != null && (bestWords[index] == null || bestSpotWord.compareTo(bestWords[index]) > 0)) {
//							for (int shift = bestWords.length - 1; shift > index; shift--) {
//								bestWords[shift] = bestWords[shift - 1];
//							}
//							
//							bestWords[index] = bestSpotWord;
//							
//							break insertLoop;
//						}
//					}
//				}
//			}
//		}
//		
//		return bestWords;
//	}
	
	private static void validateAvailableLetters (String letters) throws IllegalArgumentException {
		for (int i = 0; i < letters.length(); i++) {
			char c = letters.charAt(i);
			
			if (!(Character.isLowerCase(c) && Character.isAlphabetic(c) || c == '*'))
				throw new IllegalArgumentException("Solve attempted with invalid letter: " + c);
		}
	}
	
	private static Word bestSpotWord(int x, int y, Board board, String[][] availableStrings/*, WordRank wr*/) {
		if (board.getLetter(x, y) != null)
			return null;
		
		Word bestWord = null;
		
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
					bestWord = bestWord(bestWord, new Word(letters, board, x, y, true));
//					wr.add(bestWord);
				}
				
				if (i >= yMinLength && i <= yMaxLength) {
					bestWord = bestWord(bestWord, new Word(letters, board, x, y, false));
//					wr.add(bestWord);
				}
			}
		}
		
		return bestWord;
	}
	
//	private static Word bestSpotWord(int x, int y, Board board, String availableLetters, String lettersSoFar) {
//		Word bestWord = null;
//		
//		for (int i = 0; i < availableLetters.length(); i++) {
//			String wordSoFar = lettersSoFar + availableLetters.charAt(i);
//			String remainingLetters = availableLetters.substring(0, i) + availableLetters.substring(i + 1);
//
//			Word horizontalWord = new Word(wordSoFar, board, x, y, true);
//			Word verticalWord = new Word(wordSoFar, board, x, y, false);
//			
//			Word bestCurrentWord = bestWord(horizontalWord, verticalWord);
//			
//			Word longVsCurrent = bestWord(bestCurrentWord, bestSpotWord(x, y, board, remainingLetters, wordSoFar));
//			
//			bestWord = bestWord(bestWord, longVsCurrent);
//		}
//		
//		return bestWord;
//	}
	
//	private static ArrayList<String> allCombinations(ArrayList<String> existing, String availableLetters, String lettersSoFar) {
//		for (int i = 0; i < availableLetters.length(); i++) {
//			String wordSoFar = lettersSoFar + availableLetters.charAt(i);
//			String remainingLetters = availableLetters.substring(0, i) + availableLetters.substring(i + 1);
//			
//			if (!existing.contains(wordSoFar)) {
//				existing.add(wordSoFar);
//			}
//			
//			allCombinations(existing, remainingLetters, wordSoFar);
//		}
//		
//		return existing;
//	}
	
	public static String[][] allCombinations (String ltrs) {
		ArrayList<ArrayList<String>> combos = new ArrayList<ArrayList<String>>();
		for (int i = 0; i < ltrs.length(); i++) {
			combos.add(new ArrayList<String>());
		}
		
		letterSizeCombinations(combos, ltrs, "");
		
		String[][] allCombos = new String[combos.size()][];
		
		for (int i = 0; i < combos.size(); i++) {
			String[] iSizeCombos = new String[combos.get(i).size()];
			for (int j = 0; j < combos.get(i).size(); j++) {
				iSizeCombos[j] = combos.get(i).get(j);
			}
			allCombos[i] = iSizeCombos;
		}
		
		return allCombos;
	}
	
	private static ArrayList<ArrayList<String>> letterSizeCombinations(ArrayList<ArrayList<String>> existing, String availableLetters, String lettersSoFar) {
		for (int i = 0; i < availableLetters.length(); i++) {
			String wordSoFar = lettersSoFar + availableLetters.charAt(i);
			String remainingLetters = availableLetters.substring(0, i) + availableLetters.substring(i + 1);
			
			if (dictionaryContains(wordSoFar)) {
				if (!existing.get(wordSoFar.length()-1).contains(wordSoFar)) {
				existing.get(wordSoFar.length()-1).add(wordSoFar);
			}
			
			letterSizeCombinations(existing, remainingLetters, wordSoFar);
			}
		}
		
		return existing;
	}
	
	private static boolean dictionaryContains(String occurrence) {
		for (int i = 0; i < VALID_WORDS.length; i++) {
			if (VALID_WORDS[i].contains(occurrence))
				return true;
		}
		return false;
	}
	
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
		if (!this.validString(word.word()) || !word.touchesBoard()) {
			word.setScore(-1);
			word.setValid(false);
			return;
		}
		
		HashMap<Letter, Word> crossWords = word.crossWords();
		
		int score = 0;
		
		for (Word w: crossWords.values()) {
			if (!this.validString(w.word())) {
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
	
	public boolean validString(String str) {
		int minIndex = 0;
		int maxIndex = VALID_WORDS.length - 1;
		
		while (true) {
			if (maxIndex - minIndex < 2) {
				return str.equals(VALID_WORDS[minIndex]) || str.equals(VALID_WORDS[maxIndex]);
			}
			
			int guessIndex = (minIndex + maxIndex)/2;
			String guess = VALID_WORDS[guessIndex];
			
			if (guess.equals(str)) {
				return true;
			} else if (str.compareTo(guess) < 0) {
				maxIndex = guessIndex;
			} else {
				minIndex = guessIndex;
			}
		}
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
