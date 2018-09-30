package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import solver.Board;
import solver.Word;

public class WSServer {

//	------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------c3h4y3m2e1s1------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	public static enum requestType {PARSE, SOLVE};
	public static enum status {SOLVED, BAD_REQUEST, BOARD_PARSED}
	
	public static void main(String[] args) throws IOException {
		if (args.length != 1) {
			System.err.println("Usage: java WSServer <port number>");
			System.exit(1);
		}

		int portNum = Integer.parseInt(args[0]);
		
		while (true) {
			try (
				ServerSocket serverSocket = new ServerSocket(Integer.parseInt(args[0]));
				Socket clientSocket = serverSocket.accept();
				BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
				BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			) {
				String line;
				JsonParser jparser = new JsonParser();
				
				Board toSolve = null;
				Word[] bestWords = null;
				
				while ((line = in.readLine()) != null) {
					System.out.println("Received input");
					JsonObject json = jparser.parse(line).getAsJsonObject();
					
					JsonObject response = new JsonObject();
					
					System.out.println("Parsed json: " + json.toString());
					System.out.println("Type: " + json.get("type").toString());
					
					if (json.get("type").getAsString().equals(requestType.PARSE.toString())) {
						try {
							toSolve = new Board(15, json.get("board").getAsString());
							System.out.println("Parsed board:\n" + toSolve.readableString());
							response.addProperty("status", status.BOARD_PARSED.toString());
						} catch (IllegalArgumentException e) {
							response.addProperty("status", status.BAD_REQUEST.toString());
							e.printStackTrace();
						}
					} else if (json.get("type").getAsString().equals(requestType.SOLVE.toString())) {
						if (toSolve != null) {
							try {
								bestWords = Board.solve(toSolve, json.get("available letters").getAsString());
								response.addProperty("status", status.SOLVED.toString());
							} catch (IllegalArgumentException e) {
								response.addProperty("status", status.BAD_REQUEST.toString());
							}
						} else {
							response.addProperty("status", status.BAD_REQUEST.toString());
						}
						
						if (bestWords != null && bestWords[0] != null) {
							response.addProperty("status", status.SOLVED.toString());
							response.addProperty("best word", bestWords[0].toString());
							
							JsonArray words = new JsonArray();
							
							for (int i = 0; i < bestWords.length; i++) {
								JsonObject word = new JsonObject();
								word.addProperty("new letters", bestWords[i].newWord());
								word.addProperty("x", bestWords[i].getX());
								word.addProperty("y", bestWords[i].getY());
								word.addProperty("horizontal", bestWords[i].horizontal());
								
								words.add(word);
							}
							
							response.add("words", words);
						} else if (!response.has("status")) {
							response.addProperty("status", status.BAD_REQUEST.toString());
						}
					}
					
					System.out.println("Response: " + response.toString());
					
					out.write(response.toString());
					out.newLine();
					out.flush();
				}
			} catch (IOException e) {
				System.out.println("Exception caught when trying to listen on port " + portNum + " or listening for a connection");
				System.out.println(e.getMessage());
			}
		}
	}
}
