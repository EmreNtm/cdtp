import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.StringTokenizer;

public class ClientObject implements Runnable {

	private Socket socket;
	private PrintWriter out;
	private BufferedReader in;
	
	public static enum ClientType {
		YONETIM,
		SERA
	};
	
	private ClientType type;
	private int seraID;
	
	public ClientObject(Socket socket) {
		this.socket = socket;
		try {
			this.out = new PrintWriter(socket.getOutputStream(), true);
			this.in = new BufferedReader(
					new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
        try {
        	String inputLine;
        	
			while (!socket.isClosed() && (inputLine = in.readLine()) != null) {
				System.out.println("Client says: " + inputLine);
			    if (inputLine.startsWith("Yonetim")) {
			    	//Tipi kaydet
			    	this.type = ClientType.YONETIM;
			    	//Eldeki sera bilgilerini gönder
			    	sendSeraInfos();
			    } else if (inputLine.startsWith("Sera")) {
			    	this.type = ClientType.SERA;
				} else if (inputLine.startsWith("seraID: ")){ //Sadece bu objenin serasý gönderir.
					//Gelen sera bilgilerini kaydedip yönetime gönder.
			    	handleNewSeraInfo(inputLine);
			    } else if (inputLine.startsWith("START: ")) {
			    	//Yönetimden gelen START isteðini doðru seraya ilet.
			    	handleStartStopSignal(inputLine);
			    } else if (inputLine.startsWith("STOP: ")) {
			    	//Yönetimdedn gelen STOP isteðini doðru seraya ilet.
			    	handleStartStopSignal(inputLine);
			    }
			}
			
			Server.removeClient(this);
		} catch (IOException e) {
			if (e instanceof SocketException) {
				Server.removeClient(this);
			} else {
				e.printStackTrace();
			}
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			out.close();
		}
	}
	
	private void sendSeraInfos() {
		//ServerDatayý güncelle
		Server.requestCurrentInfos();
//		for (SeraData sd : Server.getServerData().getSeras().values()) {
//			Server.sendMessageToYonetim("seraID: " + sd.getSeraID() + " seraTemp: " + sd.getSeraTemp());
//		} //request yapýnca zaten tek tek yönetime gönderilecekler.
	}
	
	private void sendSeraInfoToYonetim(SeraData sd) {
		Server.sendMessageToYonetim("seraID: " + sd.getSeraID() + " seraTemp: " + sd.getSeraTemp());
	}
	
	private void handleStartStopSignal(String msg) {
		StringTokenizer tkz = new StringTokenizer(msg);
		tkz.nextToken();
		//seraID
		String id = tkz.nextToken();
		int targetSeraID = Integer.parseInt(id);
		Server.sendMessageToSera(targetSeraID, msg);
	}
	
	private void handleNewSeraInfo(String msg) {
		//Data classlarý kaydet
		StringTokenizer tkz = new StringTokenizer(msg);
		tkz.nextToken();
		//seraID
		String id = tkz.nextToken();
		tkz.nextToken();
		//seraTemp
		String temp = tkz.nextToken();
		
		this.seraID = Integer.parseInt(id);
		Server.getServerData().updateSeraData(this.seraID, Double.parseDouble(temp));
		
		//Güncellenen sýcaklýðý yönetime ilet.
		sendSeraInfoToYonetim(Server.getServerData().getSeras().get(this.seraID));
	}
	
	public int getSeraID() {
		if (this.type == ClientType.SERA)
			return this.seraID;
		else
			return -1;
	}
	
	public Socket getSocket() {
		return this.socket;
	}
	
	public PrintWriter getPrintWriter() {
		return this.out;
	}

	public ClientType getType() {
		return type;
	}

}
