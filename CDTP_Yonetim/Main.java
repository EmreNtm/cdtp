package main;

import java.util.HashMap;

public class Main {

	public static MainFrame frame;
	
	public static ServerConnection serverConnection;
	
	private static HashMap<Integer, SeraData> seras;
	
    public static void main(String[] args) {
    	seras = new HashMap<Integer, SeraData>();
    	
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                frame = new MainFrame();
            }
        });
    }
    
    public static void updateSeraData(int seraID, double temp) {
		if (!seras.containsKey(seraID)) {
			seras.put(seraID, new SeraData(seraID, temp));
		} else {
			seras.get(seraID).updateTemperature(temp);
		}
	}
	
    public static HashMap<Integer, SeraData> getSeras() {
    	return seras;
    }
    
}
