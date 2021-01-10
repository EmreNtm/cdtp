package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;
import java.util.StringTokenizer;

public class ServerListener implements Runnable {

	private ServerConnection serverConnection;
	
	private Socket server;
	private BufferedReader in;
	
	public ServerListener(ServerConnection connection) {
		this.serverConnection = connection;
		this.server = serverConnection.getSocket();
		try {
			in = new BufferedReader(
			        new InputStreamReader(server.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		try {
			String inputLine;
			
			while (!server.isClosed() && (inputLine = in.readLine()) != null) {
				if (inputLine.startsWith("seraID: ")) {
					handleNewSeraInfo(inputLine);
				} else if (inputLine.startsWith("DISCONNECT: ")) {
					handleDisconnectRequest(inputLine);
				}
			}

		} catch (IOException e) {
			if (e instanceof SocketException) {
				Main.serverConnection.disconnect();
			} else {
				e.printStackTrace();
			}
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void handleNewSeraInfo(String msg) {
		StringTokenizer tkz = new StringTokenizer(msg);
		tkz.nextToken();
		//seraID
		String id = tkz.nextToken();
		tkz.nextToken();
		//seraTemp
		String temp = tkz.nextToken();
		
		int seraID = Integer.parseInt(id);
		Main.updateSeraData(seraID, Double.parseDouble(temp));
		
		Main.frame.updateMainScreen(seraID);
	}
	
	private void handleDisconnectRequest(String msg) {
		StringTokenizer tkz = new StringTokenizer(msg);
		tkz.nextToken();
		//seraID
		String temp = tkz.nextToken();
		int seraID = Integer.parseInt(temp);
		
		Main.frame.removeMiniSeraPanel(seraID);
	}
	
}