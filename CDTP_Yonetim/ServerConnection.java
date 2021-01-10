package main;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class ServerConnection {

	public static final String HOST = "localhost";
	public static final int PORT = 4999;
	
	private Socket socket;
	private PrintWriter out;
	
	public ServerConnection() {
		try {
			connectToServer();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void connectToServer() throws UnknownHostException, IOException {
		this.socket = new Socket(HOST, PORT);	
		
		ServerListener serverListener = new ServerListener(this);
		
		Thread serverListenerThread = new Thread(serverListener);
		serverListenerThread.start();
		
		this.out =
	        new PrintWriter(socket.getOutputStream(), true);
		    
		//Client tipini servera belirt.
	    out.println("Yonetim");
	}
	
	public void disconnect() {
		out.close();
        if (!socket.isClosed()) {
        	try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
	}
	
	public void sendMessageToServer(String msg) {
		out.println(msg);
	}
	
	public Socket getSocket() {
		return this.socket;
	}
	
}
