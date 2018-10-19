package solver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

public class StringTools {
	
	private static final String[] VALID_WORDS;
	
	static {
		ArrayList<String> dict = new ArrayList<String>();
		
		try (
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
	
	public static boolean dictionaryWordsContain(String occurrence) {
		for (int i = 0; i < VALID_WORDS.length; i++) {
			if (VALID_WORDS[i].contains(occurrence)) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean validString(String str) {
		return Arrays.binarySearch(VALID_WORDS, str) > -1;
//		int minIndex = 0;
//		int maxIndex = VALID_WORDS.length - 1;
//		
//		while (true) {
//			if (maxIndex - minIndex < 2) {
//				return str.equals(VALID_WORDS[minIndex]) || str.equals(VALID_WORDS[maxIndex]);
//			}
//			
//			int guessIndex = (minIndex + maxIndex)/2;
//			String guess = VALID_WORDS[guessIndex];
//			
//			if (guess.equals(str)) {
//				return true;
//			} else if (str.compareTo(guess) < 0) {
//				maxIndex = guessIndex;
//			} else {
//				minIndex = guessIndex;
//			}
//		}
	}
	
	public static ArrayList<String> allCombinations(String ltrs, boolean dictionaryCheck) {
		String[][] sizeCombos = sizeCombinations(ltrs, dictionaryCheck);

		ArrayList<String> combos = new ArrayList<String>();
		for (int i = 0; i < sizeCombos.length; i++) {
			for (int j = 0; j < sizeCombos[i].length; j++) {
				combos.add(sizeCombos[i][j]);
			}
		}

		return combos;
	}

	public static String[][] sizeCombinations(String ltrs, boolean dictionaryCheck) {
		ArrayList<ArrayList<String>> combos = new ArrayList<ArrayList<String>>();
		for (int i = 0; i < ltrs.length(); i++) {
			combos.add(new ArrayList<String>());
		}

		if (dictionaryCheck) {
			letterSizeCombinations(combos, ltrs, "", dictionaryCheck);
		} else {
			letterSizeCombinations(combos, ltrs, "");
		}

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

	private static void letterSizeCombinations(ArrayList<ArrayList<String>> existing, String availableLetters,
			String lettersSoFar) {
		for (int i = 0; i < availableLetters.length(); i++) {
			String wordSoFar = lettersSoFar + availableLetters.charAt(i);
			String remainingLetters = availableLetters.substring(0, i) + availableLetters.substring(i + 1);

			if (!existing.get(wordSoFar.length() - 1).contains(wordSoFar)) {
				existing.get(wordSoFar.length() - 1).add(wordSoFar);
			}

			letterSizeCombinations(existing, remainingLetters, wordSoFar);

		}
	}

	private static void letterSizeCombinations(ArrayList<ArrayList<String>> existing, String availableLetters,
			String lettersSoFar, boolean dictionaryCheck) {
		
//		System.out.println("running dictionary checking size combos!");
		for (int i = 0; i < availableLetters.length(); i++) {
			String wordSoFar = lettersSoFar + availableLetters.charAt(i);
			
			if (dictionaryWordsContain(wordSoFar)) {
//				System.out.println("Dictionary contains: " + wordSoFar);
				if (!existing.get(wordSoFar.length() - 1).contains(wordSoFar)) {
					existing.get(wordSoFar.length() - 1).add(wordSoFar);
				}
				String remainingLetters = availableLetters.substring(0, i) + availableLetters.substring(i + 1);
				letterSizeCombinations(existing, remainingLetters, wordSoFar, dictionaryCheck);
			}
		}
	}
}
