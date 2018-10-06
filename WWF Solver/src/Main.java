import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

import solver.Board;
import solver.Letter;
import solver.Word;

public class Main {
	
	
	
	public static void main(String[] args) {
	
		
		
//		String boardString = "------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------c3h4y3m2e1s1------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------";
//		
//		System.out.println("Current directory:\n" + System.getProperty("user.dir"));
//		
//		
//		Board board = new Board (15, boardString);
//		
//		String availableLetters = "abcde";
//		
//		System.out.println(Board.solve(board, availableLetters)[0]);
		
		String ltrs = "abcdefghijklmnopqrstuvwxyz";
		
//		String initial = "aaaaaaa";
		
		StringBuilder sb = new StringBuilder("aaaaaaa");
		
		ArrayList<String> list = new ArrayList<String>();
		
		while (!sb.toString().equals("zzzzzzz")) {
			list.add(sb.toString());
			for (int i = 6; i >= 0; i++) {
				
			}
		}
		
//		String[][] allCombos = Board.allCombinations("abcdefg");
//		int count = 0;
//		for (int i = 0; i < allCombos.length; i++) {
//			for(int j = 0; j < allCombos[i].length; j++) {
//				count += 1;
//				System.out.println(count + ", " + allCombos[i][j]);
//			}
//		}
		
	}
	
}
