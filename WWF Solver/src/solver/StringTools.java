package solver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class StringTools {
	
	private static final String[] VALID_WORDS;
	private static final HashMap<String, String> WORD_MAP;
	
	static {
		ArrayList<String> dict = new ArrayList<String>();
		
		try (
			BufferedReader reader = new BufferedReader(new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("ws_dict.txt")));
			
		) {
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.length() > 1) {
					dict.add(line);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		VALID_WORDS = new String[dict.size()];
		
		WORD_MAP = new HashMap<String, String>(dict.size());
		
		for(int i = 0; i < dict.size(); i++) {
			String str = dict.get(i);
			WORD_MAP.put(str, str);
			
			VALID_WORDS[i] = str;
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
		return WORD_MAP.get(str) != null;
	}
	
//	public static boolean validString(String str) {
//		int low = 0;
//		int high = VALID_WORDS.length - 1;
//		
//		while (str.compareTo(VALID_WORDS[low]) >= 0 && str.compareTo(VALID_WORDS[high]) <= 0) {
//			int digits = Math.min(Math.min(VALID_WORDS[low].length(), VALID_WORDS[high].length()), str.length());
//			System.out.println(String.format("Total Dist: %s LOW-MID: %s MID-HIGH: %s", stringDifference(VALID_WORDS[low], VALID_WORDS[high], digits), stringDifference(VALID_WORDS[low], str, digits), stringDifference(str, VALID_WORDS[low], digits)));
//			int mid = midIndex(low, high, str);
//			int compare = str.compareTo(VALID_WORDS[mid]);
//			if (compare > 0) {
//				high = mid - 1;
//			} else if (compare < 0) {
//				low = mid + 1;
//			} else {
//				return true;
//			}
//		}
//		
//		return false;
//	}
//	
//	private static int midIndex (int low, int high, String str) {
//		int digits = Math.min(Math.min(VALID_WORDS[low].length(), VALID_WORDS[high].length()), str.length());
//		
//		return low + (low - high) * (stringDifference(VALID_WORDS[low], VALID_WORDS[high], digits))/stringDifference(VALID_WORDS[low], VALID_WORDS[high], digits);
//	}
//	
//	private static int stringDifference(String a, String b, int digits) {
//		int total = 0;
//		
//		for (int i = 0; i < digits; i++) {
//			total += (int)(b.charAt(i) - a.charAt(i)) * Math.pow(10, digits-i);
//		}
//		
//		return total;
//	}
	
//	public static boolean validString(String str) {
//		System.out.println("rapido! " + str);
//		int low = 0;
//		int mid;
//		int high = VALID_WORDS.length - 1;
//		
//		while (VALID_WORDS[low].compareTo(VALID_WORDS[high]) != 0 && (str.compareTo(VALID_WORDS[low]) > 0 && str.compareTo(VALID_WORDS[high]) < 0)) {
//			int term1 = str.compareTo(VALID_WORDS[low]);
//			int term2 = (high-low);
//			int term3 = (VALID_WORDS[high].compareTo(VALID_WORDS[low]));
//			System.out.println(VALID_WORDS[high]);
//			mid = low + ((term1 * term2/term3));
//			System.out.println(String.format("low: %s mid: %s high: %s 1: %s 2: %s 3: %s", low, mid, high, term1, term2, term3));
//			
//			int bob = VALID_WORDS[mid].compareTo(str);
//			if (bob > 0) {
//				high = mid - 1;
//			} else if (bob < 0) {
//				low = mid + 1;
//			} else {
//				return true;
//			}
//		}
//		
//		return str.equals(VALID_WORDS[low]) || str.equals(VALID_WORDS[high]);
//	}
	
//	public static boolean validString(String str) {
//		return Arrays.binarySearch(VALID_WORDS_ALPHA[(int)(str.charAt(0) - 'a')], str) > -1;
//	}
	
//	public static boolean validString(String str) {
//		return Arrays.binarySearch(VALID_WORDS, str) > -1;
//	}
	
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
		
		for (int i = 0; i < availableLetters.length(); i++) {
			String wordSoFar = lettersSoFar + availableLetters.charAt(i);
			
			if (dictionaryWordsContain(wordSoFar)) {
				if (!existing.get(wordSoFar.length() - 1).contains(wordSoFar)) {
					existing.get(wordSoFar.length() - 1).add(wordSoFar);
				}
				String remainingLetters = availableLetters.substring(0, i) + availableLetters.substring(i + 1);
				letterSizeCombinations(existing, remainingLetters, wordSoFar, dictionaryCheck);
			}
		}
	}
}
