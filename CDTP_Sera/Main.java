import java.util.Scanner;

import jssc.SerialPort;
import jssc.SerialPortException;

public class Main {

	public static ServerConnection serverConnection;
	public static SeraData sera;
	
	public static SerialPort serialPort;
	
	public static long startTime = System.currentTimeMillis();
	public static long currentTime = startTime;
	public static long lastServerInfoSendTime = System.currentTimeMillis();
	public static long lastSeraInfoSendTime = System.currentTimeMillis();
	public static long serverDelay = 1000;
	public static long seraDelay = 3000;
	public static double lastSentSeraTemp;
	
	public static enum SeraState {
		START,
		STOP
	}
	
	public static SeraState state = SeraState.STOP;
	
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
	    System.out.print("Enter seraID: ");

	    int id = scanner.nextInt();
	    System.out.println("SeraID is: " + id);
	    scanner.close();
		
		serverConnection = new ServerConnection();
		
		sera = new SeraData(id, 0);
		serverConnection.sendSeraInfo();
		
		//Program kapandýðýnda disconnect ol.
		Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
            	//Disconnect olma bilgisi gönder.
                serverConnection.disconnect();
            }
        });
		
//		String[] portNames = SerialPortList.getPortNames();
//	    for(int i = 0; i < portNames.length; i++)
//	        System.out.println(portNames[i]);
	    
	    serialPort = new SerialPort("COM2");
	    try {
	        serialPort.openPort();
	        serialPort.setParams(9600, 8, 1, 0);
	        int mask = SerialPort.MASK_RXCHAR + SerialPort.MASK_CTS + SerialPort.MASK_DSR;
	        serialPort.setEventsMask(mask);
	        serialPort.addEventListener(new SerialPortReader());
	    }
	    catch (SerialPortException ex) {
	        System.out.println(ex);
	    }

		while (true) {
			//Belli aralýklarla servera sera bilgisini gönder.
			if (state == SeraState.STOP && isReadyToSendDataToServer()
					&& lastSentSeraTemp != sera.getSeraTemp()) {
				serverConnection.sendSeraInfo();
				updateLastInfoSendTime();
			}
			currentTime = System.currentTimeMillis();
		}
		
	}
	
	public static boolean isReadyToSendDataToServer() {
		return currentTime >= lastServerInfoSendTime + serverDelay;
	}
	
	public static boolean isReadyToSendDataToSera() {
		return currentTime >= lastSeraInfoSendTime + seraDelay;
	}
	
	public static void updateLastInfoSendTime() {
		lastServerInfoSendTime = currentTime;
	}
	
}
