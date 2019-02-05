import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
		
		
		
//		System.out.println("board contains hpsoi? : " + Board.dictionaryWordsContain("hpsoi"));
//		System.out.println("ahiopst");
		
		
		Board testBoard = new Board(15, 15, "--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------feabtbsb--------------------ncabtbibobnc------------------------celcobgd----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
		
		System.out.println(testBoard.readableString());
		
		
//		Word w = new Word("do", 8, 5, testBoard, false);
		
		System.out.println("is aaaa valid? " + StringTools.validString("aaaa"));
		
		
		Word w = Board.solveThreaded(testBoard, "abcdefg", (progress) -> {}, new WordRank(5))[0];
		
		long startTime = System.currentTimeMillis();
		w = Board.solveThreaded(testBoard, "abcdefg", (progress) -> {}, new WordRank(5))[0];
		long endTime = System.currentTimeMillis();
//
		testBoard.add(w);
		System.out.println(testBoard.readableString());
		System.out.println(w);
		
//		Word[] words = Board.solveThreaded(new Board(15), "ca*onb", (progress) -> {}, new WordRank(5));
				
//		System.out.println(words[0]);
//		String[][] comboCombos = StringTools.sizeCombinations("ahiopst", true);
		System.out.println("Total time: " + (endTime - startTime));
		
//		ArrayList<String> words = downloadWordList();
//		Collections.sort(words);
//		
//		createResourceFile("webster_download_dict", words);
		
		
	}
	
	public static void createResourceFile(String filename, ArrayList<String> data) {
		try {
			File res = new File(System.getProperty("user.dir") + "\\resources\\" + filename + ".txt");
			BufferedWriter bw = new BufferedWriter(new FileWriter(res));
			
			for (String line: data) {
				bw.write(line + "\n");
			}
			
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void testBoardSolve() {
		Letter[][] letters = new Letter[15][15];
		Board b = new Board (15, 15, letters);
		
		ProgressMonitor pm = (progress) -> {
//			System.out.print(".");
		};
		
		System.out.println("Solving");
		
		long startMillis = System.currentTimeMillis();
		
		Word[] bestWords = Board.solveThreaded(b, "abcdefg", pm, new WordRank(5));
		System.out.println();
		
		System.out.println("Solve time: " + (System.currentTimeMillis() - startMillis));
		System.out.println();
		System.out.println(bestWords[0]);
	}
	
	public static ArrayList<String> downloadWordList() {
		ArrayList<String> wordList = new ArrayList<String>();
		for (char i = 'a'; i <= 'z'; i++) {
			String pageURLString = "http://scrabble.merriam.com/words/start-with/" + i;
			
			Scanner scan = null;
			
			try { 
				URL pageURL = new URL(pageURLString);
				InputStream is = pageURL.openStream();
				scan = new Scanner(is);
				
				String fileData = "";
				ArrayList<String> fileSource = new ArrayList<String>();
				while(scan.hasNext()) {
					String line = scan.nextLine();
					fileSource.add(line);
//					System.out.println(line);
					fileData += line + System.getProperty("file.seperator");
				}
				
				for (int j = 0; j < fileSource.size(); j++) {
//					if (fileSource.get(i).contains("<div class=\"sbl_word_groups\">")) {
//						sourceLineStart = j;
//					}
					
					String line = fileSource.get(j);
					int index;
					String comparator = "a href=\"/finder/";
					if ((index = line.indexOf(comparator)) > -1) {
						System.out.println(line);
						wordList.add(line.substring(index + comparator.length(), line.indexOf("\"", index+comparator.length())));
					}
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if(scan != null)
					scan.close();
			}
		}
		
		return wordList;
	}
	
	public static void writeCombosToResourceFile(String filename, String ltrs, boolean dictCheck) {
		String[][] comboCombos = StringTools.sizeCombinations(ltrs, dictCheck);
		try {
			File combo = new File(System.getProperty("user.dir") + "\\resources\\" + filename + ".txt");
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
		}
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
