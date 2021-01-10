package main;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class MiniSeraPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private SeraData sera;
	private double fTextTemp;
	
	private boolean isActive = false;
	
	private JLabel idLabel;
	private JLabel tempLabel;
	
	public MiniSeraPanel(SeraData sera) {
		this.sera = sera;
		MiniSeraPanel thisPanel = this;
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(BorderFactory.createLineBorder(Color.black));
		setBackground(Color.ORANGE);
		
		//add(Box.createVerticalStrut(10));
		
		idLabel = new JLabel("seraID: " + sera.getSeraID());
		idLabel.setForeground(Color.BLUE);
		idLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		idLabel.setAlignmentY(Component.CENTER_ALIGNMENT);
		add(idLabel);
		
		//add(Box.createVerticalStrut(10));
		
		tempLabel = new JLabel("Temperature: " + String.format("%.2f", sera.getSeraTemp()));
		tempLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		add(tempLabel);
		
		JLabel targetLabel = new JLabel(" ");
		targetLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		targetLabel.setForeground(Color.BLUE);
		add(targetLabel);
		
		add(Box.createVerticalStrut(9));
		
		//Sýcaklýk girme alaný
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		
		JLabel label = new JLabel("Temperature: ");
		JFormattedTextField temperatureField = new JFormattedTextField(NumberFormat.getNumberInstance());
		temperatureField.setValue(sera.getSeraTemp());
		this.fTextTemp = sera.getSeraTemp();
		temperatureField.setColumns(10);
		temperatureField.setMaximumSize(new Dimension(70, 30));
		temperatureField.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				Object source = event.getSource();
		        if (source == temperatureField) {
		            fTextTemp = ((Number)temperatureField.getValue()).doubleValue();
		        }
			}
		});
		
		panel.add(label);
		panel.add(temperatureField);
		add(panel);
		
		//Sýcaklýk butonlarý
		panel = new JPanel();
		panel.setBackground(Color.ORANGE);
		panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
		panel.setMinimumSize(new Dimension(0, 50));
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		
		panel.add(Box.createVerticalStrut(50));
		//panel.add(Box.createHorizontalGlue());
		
		JButton button = new JButton("START");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String msg = "START: " + sera.getSeraID() + " targetTemp: " + fTextTemp;
				Main.serverConnection.sendMessageToServer(msg);
				isActive = true;
				targetLabel.setText("Target Temperature: " + String.format("%.2f", fTextTemp));
				thisPanel.revalidate();
				thisPanel.repaint();
			}
		});
		panel.add(button);
		
		panel.add(Box.createHorizontalStrut(10));
		
		button = new JButton("STOP");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String msg = "STOP: " + sera.getSeraID();
				Main.serverConnection.sendMessageToServer(msg);
				isActive = false;
				targetLabel.setText(" ");
				thisPanel.revalidate();
				thisPanel.repaint();
			}
		});
		panel.add(button);
		panel.add(Box.createHorizontalGlue());
		
		add(panel);
		
		panel = new JPanel() {
			private static final long serialVersionUID = 1L;
			
			public void paint(Graphics g) {
		      //g.fillRect (5, 15, 50, 75);
				if (isActive) {
					g.setColor(Color.decode("#47e835"));
				}
				else {
					g.setColor(Color.decode("#e84435"));
				}
				g.fillOval(this.getWidth() / 2 - 10, this.getHeight() / 2 - 10, 20, 20);
			}
		};
		panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
		panel.setMinimumSize(new Dimension(0, 25));
		
		add(panel);
	}
	
	public void refresh() {
		this.idLabel.setText("seraID: " + this.sera.getSeraID());
		this.idLabel.revalidate();
		this.idLabel.repaint();
		
		this.tempLabel.setText("Temperature: " + String.format("%.2f", sera.getSeraTemp()));
		this.tempLabel.revalidate();
		this.tempLabel.repaint();
	}
	
	public SeraData getSeraData() {
		return this.sera;
	}
	
}
