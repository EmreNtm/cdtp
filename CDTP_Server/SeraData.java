
public class SeraData {

	private int seraID;
	private double seraTemp;
	
	public SeraData(int seraID, double seraTemp) {
		this.seraID = seraID;
		this.seraTemp = seraTemp;
	}
	
	public void updateTemperature(double newTemp) {
		this.seraTemp = newTemp;
	}
	
	public int getSeraID() {
		return this.seraID;
	}
	
	public double getSeraTemp() {
		return this.seraTemp;
	}
	
}
