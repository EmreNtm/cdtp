package main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ServerButtonListener implements ActionListener {
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		MainFrame frame = Main.frame;
		frame.createMainScreen();
		
		Main.serverConnection = new ServerConnection();
		
		//Program kapandýðýnda disconnect ol.
		Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                Main.serverConnection.disconnect();
            }
        });
		
		frame.getCardLayout().show(frame.getCards(), "mainScreen");
	}

}
