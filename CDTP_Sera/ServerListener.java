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
				if (inputLine.startsWith("InfoRequest")) {
					System.out.println("Server says: " + inputLine);
					Main.serverConnection.sendSeraInfo();
				} else if (inputLine.startsWith("START: ")) {
					System.out.println("Server says: " + inputLine);
					handleStartSignal(inputLine);
				} else if (inputLine.startsWith("STOP: ")) {
					System.out.println("Server says: " + inputLine);
					handleStopSignal(inputLine);
				}
			}

		} catch (IOException e) {
			if (e instanceof SocketException) {
				System.out.println("Server is closed!");
				serverConnection.disconnect();
				System.exit(0);
			}
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void handleStartSignal(String msg) {
		Main.state = Main.SeraState.START;
		
		StringTokenizer tkz = new StringTokenizer(msg);
		tkz.nextToken();
		tkz.nextToken();
		tkz.nextToken();
		String temp = tkz.nextToken();
		float targetTemp = Float.parseFloat(temp);
		if (SerialPortReader.writeToSerialPort(String.format("%.2f", targetTemp)))
			System.out.println(String.format("'%.2f", targetTemp) + "' sent to microcontroller.");
	}
	
	private void handleStopSignal(String msg) {
		Main.state = Main.SeraState.STOP;
		
		if (SerialPortReader.writeToSerialPort("stop"))
			System.out.println("'stop" + "' sent to microcontroller.");
	}
	
}