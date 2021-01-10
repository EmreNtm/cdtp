import java.util.HashMap;

public class ServerData {

	private HashMap<Integer, SeraData> seras;
	
	public ServerData() {
		this.seras = new HashMap<Integer, SeraData>();
	}
	
	public HashMap<Integer, SeraData> getSeras() {
		return this.seras;
	}
	
	public void updateSeraData(int seraID, double temp) {
		if (!this.seras.containsKey(seraID)) {
			this.seras.put(seraID, new SeraData(seraID, temp));
		} else {
			this.seras.get(seraID).updateTemperature(temp);
		}
	}
	
}
