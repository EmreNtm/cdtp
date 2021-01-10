import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
	
	private static final int PORT = 4999;
	
	private static ArrayList<ClientObject> clients;
	private static ExecutorService pool;
	
	private static ServerSocket serverSocket;
	
	private static ServerData serverData;
	
	public static void main(String[] args) {
		try {
			clients = new ArrayList<ClientObject>();
			pool = Executors.newFixedThreadPool(6);
			
			serverSocket = new ServerSocket(PORT);
			//InetAddress addr = InetAddress.getByName("25.112.203.137");
			//serverSocket = new ServerSocket(PORT, 5, addr);
			System.out.println("Server started.");
			//System.out.println(serverSocket.getInetAddress());
			
			serverData = new ServerData();
			
			while (true) {
				Socket newClient = serverSocket.accept();
				System.out.println("New client connected!");
				
				ClientObject clientThread = new ClientObject(newClient);
				clients.add(clientThread);
				
				pool.execute(clientThread);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (serverSocket != null)
					serverSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("Server is closed.");
		}
	}
	
	public static void sendMessageToYonetim(String msg) {
		System.out.println("Server says: '" + msg + "' to Yonetim");
		for (ClientObject co : clients) {
			if (co.getType() == ClientObject.ClientType.YONETIM) {
				co.getPrintWriter().println(msg);
			}
		}
	}
	
	public static void sendMessageToSera(int id, String msg) {
		System.out.println("Server says: '" + msg + "' to " + id);
		for (ClientObject co : clients) {
			if (co.getSeraID() == id) {
				co.getPrintWriter().println(msg);
			}
		}
	}
	
	public static void removeClient(ClientObject clientObject) {
		clients.remove(clientObject);
		if (clientObject.getType() != ClientObject.ClientType.YONETIM) {
			sendMessageToYonetim("DISCONNECT: " + clientObject.getSeraID());
		}
		System.out.println("A client is disconnected!");
	}
	
	public static void requestCurrentInfos() {
		for (ClientObject co : clients) {
			if (co.getType() == ClientObject.ClientType.SERA) {
				System.out.println("Server says: 'InfoRequest' to " + co.getSeraID());
				co.getPrintWriter().println("InfoRequest");
			}
		}
	}
	
	public static ServerData getServerData() {
		return serverData;
	}
	
}
