package core;

public class LineNamesHashMap {
	private static LineNamesHashMap instance = new LineNamesHashMap();
	private static int id;
	
	private LineNamesHashMap() {
		id = 0;
	}
	
	public void increment() {
		id++;	
	}
	
	public int getId() {
		return id;
	}
	
	public String giveIdToLine() {
		String toReturn = "Ligne " + Integer.valueOf(id).toString();
		this.increment();
		return toReturn;
	}
	
	public static LineNamesHashMap getInstance() {
		return instance;
	}
}
