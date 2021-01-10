package main;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class MainFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unused")
	private ServerConnection serverConnection;
	
	private CardLayout cardLayout = new CardLayout();
	private JPanel cards = new JPanel(cardLayout);
	
	private JPanel mainScreen;
	private ArrayList<MiniSeraPanel> miniSeraPanels = new ArrayList<MiniSeraPanel>();
	
	public MainFrame() {
		screenOne();
        add(cards, BorderLayout.CENTER);
        cardLayout.show(cards, "screenOne");
        
        setTitle("ManagementApp");
        setSize(400, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
	}
	
	public void createMainScreen() {
		mainScreen = new JPanel();
		mainScreen.setLayout(new GridLayout(0, 2));
		cards.add(mainScreen, "mainScreen");
	}
	
	public void refreshMainScreen() {
		mainScreen.removeAll();
		
		for (MiniSeraPanel msp : this.miniSeraPanels) {
			mainScreen.add(msp);
		}
		
		mainScreen.revalidate();
		mainScreen.repaint();
	}
	
	public void updateMainScreen(int id) {
		int flag = 0;
		
		for (MiniSeraPanel msp : this.miniSeraPanels) {
			if (msp.getSeraData().getSeraID() == id) {
				msp.refresh();
				flag = 1;
			}
		}
		
		if (flag == 0) {
			miniSeraPanels.add(new MiniSeraPanel(Main.getSeras().get(id)));
			mainScreen.add(miniSeraPanels.get(miniSeraPanels.size()-1));
		}
		
		mainScreen.revalidate();
		mainScreen.repaint();
	}
	
	private void screenOne() {
		JPanel firstCard = new JPanel();
        firstCard.setLayout(new CardLayout(40, 30));
        
        JButton serverButton = new JButton("Connect to Server");
        serverButton.addActionListener(new ServerButtonListener());
        firstCard.add(serverButton);
        
        cards.add(firstCard, "screenOne");
	}
	
	public void removeMiniSeraPanel(int seraID) {
		for (int i = this.miniSeraPanels.size()-1; i >=0; i--) {
			if (this.miniSeraPanels.get(i).getSeraData().getSeraID() == seraID) {
				this.miniSeraPanels.remove(i);
			}
		}
		Main.getSeras().remove(seraID);
		this.refreshMainScreen();
	}
	
	public CardLayout getCardLayout() {
		return this.cardLayout;
	}
	
	public JPanel getCards() {
		return this.cards;
	}
	
}
