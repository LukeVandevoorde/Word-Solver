import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

import solver.Board;
import solver.Letter;
import solver.Word;

public class Main {
	
	
	
	public static void main(String[] args) {
	
		
		
		String boardString = "------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------c3h4y3m2e1s1------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------";
		
		System.out.println("Current directory:\n" + System.getProperty("user.dir"));
		
		
		Board board = new Board (15, boardString);
		
		String availableLetters = "abcde";
		
		System.out.println(Board.solve(board, availableLetters)[0]);
		
	}
	
}
