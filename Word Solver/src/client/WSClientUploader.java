package client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import server.WSServer;
import solver.Board;
import solver.Word;

public class WSClientUploader implements AutoCloseable {
	
	private BufferedWriter out;
	private BufferedReader in;
	private JsonParser jparser;
	
	public WSClientUploader (Socket server) throws IOException {
		out = new BufferedWriter(new OutputStreamWriter(server.getOutputStream()));
		in = new BufferedReader(new InputStreamReader(server.getInputStream()));
		jparser = new JsonParser();
	}
	
	public Word[] requestBestWords (Board board, String availableLetters) throws IOException {
		JsonObject parseRequest = new JsonObject();
		parseRequest.addProperty("type", WSServer.requestType.PARSE.toString());
		parseRequest.addProperty("board", board.toString());
		
		out.write(parseRequest.toString());
		out.newLine();
		out.flush();
		
		try {
			JsonObject parseResponse = jparser.parse(in.readLine()).getAsJsonObject();
			
			if (parseResponse.get("status").getAsString().equals(WSServer.status.BOARD_PARSED.toString())) {
				JsonObject solveRequest = new JsonObject();
				solveRequest.addProperty("type", WSServer.requestType.SOLVE.toString());
				solveRequest.addProperty("available letters", availableLetters);
				out.write(solveRequest.toString());
				out.newLine();
				out.flush();
			} else {
				throw new IOException ("Server failed to parse board\n" + parseResponse.toString());
			}
			
			JsonObject parsedWords = jparser.parse(in.readLine()).getAsJsonObject();
			
			if (parsedWords.get("status").getAsString().equals(WSServer.status.SOLVED.toString())) {
				JsonArray jarray = parsedWords.get("words").getAsJsonArray();
				
				Word[] bestWords = new Word[jarray.size()];
				
				for (int i = 0; i < jarray.size(); i++) {
					JsonObject jobj = (JsonObject) jarray.get(i);
					bestWords[i] = new Word(jobj.get("new letters").getAsString(), board, jobj.get("x").getAsInt(), jobj.get("y").getAsInt(), jobj.get("horizontal").getAsBoolean());
				}
				
				return bestWords;
			} else {
				throw new IOException ("Server failed to solve words\n" + parsedWords.toString());
			}
		} catch (JsonSyntaxException e) {
			throw new IOException ("Could not parse server response\n" + e.getStackTrace());
		}
	}

	@Override
	public void close() throws IOException {
		out.close();
		in.close();
	}
	
}
