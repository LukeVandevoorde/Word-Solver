import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

import solver.Board;
import solver.Board.ProgressMonitor;
import solver.Board.WordRank;
import solver.Letter;
import solver.StringTools;
import solver.Word;

public class Main {
	
	private static final HashMap<Character, Integer> numLetters;
	
	 static {
		numLetters = new HashMap<Character, Integer>();
		numLetters.put('a', 9);
		numLetters.put('b', 2);
		numLetters.put('c', 2);
		numLetters.put('d', 5);
		numLetters.put('e', 13);
		numLetters.put('f', 2);
		numLetters.put('g', 3);
		numLetters.put('h', 4);
		numLetters.put('i', 8);
		numLetters.put('j', 1);
		numLetters.put('k', 1);
		numLetters.put('l', 4);
		numLetters.put('m', 2);
		numLetters.put('n', 5);
		numLetters.put('o', 8);
		numLetters.put('p', 2);
		numLetters.put('q', 1);
		numLetters.put('r', 6);
		numLetters.put('s', 5);
		numLetters.put('t', 7);
		numLetters.put('u', 4);
		numLetters.put('v', 2);
		numLetters.put('w', 2);
		numLetters.put('x', 1);
		numLetters.put('y', 2);
		numLetters.put('z', 1);
	 }
	
	public static void main(String[] args) {
	
		
		
		System.out.println("Current directory:\n" + System.getProperty("user.dir"));
		
		
		
//		ArrayList<String> combos = new ArrayList<String>();
//		
//		fillCombos(combos, 7, 0, "");
//		
//		System.out.println(combos.size());
//		
//		
//		try {
//			BufferedWriter bw = new BufferedWriter(new FileWriter(System.getProperty("user.dir") + "\\resources\\available_letter_combos.txt"));
//			for (String str: combos) {
//				bw.write(str);
//				bw.newLine();
//			}
//			bw.close();
//		} catch (IOException e) {
//			System.out.println(e);
//		}
		
		
//		System.out.println("board contains hpsoi? : " + Board.dictionaryWordsContain("hpsoi"));
//		System.out.println("ahiopst");
//		long startTime = System.currentTimeMillis();
//		String[][] comboCombos = StringTools.sizeCombinations("ahiopst", true);
//		System.out.println("Total time: " + (System.currentTimeMillis() - startTime));
		

		
		
		Letter[][] letters = new Letter[15][15];
		Board b = new Board (15, letters);
		
		ProgressMonitor pm = (progress) -> {
			
		};
		
		System.out.println("Solving");
		
		long startMillis = System.currentTimeMillis();
		
		Word[] bestWords = Board.solveThreaded(b, "ahiopst", pm, new WordRank(5));
		
		System.out.println("Solve time: " + (System.currentTimeMillis() - startMillis));
		
//		ArrayList<String> combos = new ArrayList<String>();
//		
//		fillCombos(combos, 7, 0, "");
//		
//		System.out.println(combos.size());
		
		/*
		try {
			File combo = new File(System.getProperty("user.dir") + "\\resources\\ahiopst.txt");
			BufferedWriter bw = new BufferedWriter(new FileWriter(combo));
			int len = 0;
			for (int i = 0; i < comboCombos.length; i++) {
				for (int j = 0; j < comboCombos[i].length; j++) {
					if (comboCombos[i][j].equals("hpsoi")) {
						System.out.println("Found it!");
					}
					bw.write(comboCombos[i][j]);
					bw.newLine();
				}
				len += comboCombos[i].length;
			}
			System.out.println("Total combos: " + len);
			bw.close();
		} catch (IOException e) {
			System.out.println(e);
		}*/
		
		
	}
	
	public static void fillCombos(ArrayList<String> existing, int numRemainingLetters, int letterIndex, String lettersSoFar) {
		if (numRemainingLetters == 0 || letterIndex == 26) {
			System.out.println("Word: " + lettersSoFar);
			existing.add(lettersSoFar);
		} else if (letterIndex < 26) {
			char c = (char)('a' + letterIndex);
			for (int i = Math.min(numRemainingLetters, numLetters.get(c)); i >= 0 ; i--) {
				String str = "";
				for (int j = 0; j < i; j++) {
					str += c;
				}
				fillCombos(existing, numRemainingLetters - i, letterIndex+1, lettersSoFar+str);
			}
		}
	}	
}
