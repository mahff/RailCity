/**
 * 
 */
package game;

import java.util.ArrayList;

import core.VariableRepository;

public class Town {
	private int length;
	private District[][] map;
	private int funds;
	private ArrayList<Line> townLines;
	
	
	public Town (int length) {
		VariableRepository.getInstance().register("NumberOfDistricts", 0);
		VariableRepository.getInstance().register("NumberOfLines", 0);
		VariableRepository.getInstance().register("NumberOfStations", 0);
		
		setTownLines(new ArrayList<Line>());
		
		funds = 750000;
		
		this.setLength(length);
		
		map = new District[length][length];
		
		for(int i=0 ; i<length ; i++){
			for(int j=0 ; j<length ; j++){
				setDistrict(i, j, null);
			}
		}
	}

	
	/**
	 * @return the length
	 */
	public int getLength() {
		return length;
	}
	/**
	 * @param length the length to set
	 */
	public void setLength(int length) {
		this.length = length;
	}

	/**
	 * @return the map
	 */
	public District[][] getMap() {
		return map;
	}
	/**
	 * @param map the map to set
	 */
	public void setMap(District[][] map) {
		this.map = map;
	}
	
	/**
	 * @return the funds
	 */
	public int getFunds() {
		return funds;
	}
	/**
	 * @param funds the funds to set
	 */
	public void setFunds(int funds) {
		this.funds = funds;
	}
	
	/**
	 * @return the townLines
	 */
	public ArrayList<Line> getTownLines() {
		return townLines;
	}
	/**
	 * @param townLines the townLines to set
	 */
	public void setTownLines(ArrayList<Line> townLines) {
		this.townLines = townLines;
	}


/*************************************************************************************************/
	
	
	/**
	 * Get the district at the given position
	 * @param positionX
	 * @param positionY
	 * @return the district of the map at the given position
	 */
	public District getDistrict(int positionX, int positionY) {
		return map[positionX][positionY];
	}

	
	/**
	 * Set the district at the given position
	 * @param positionX
	 * @param positionY
	 * @param district
	 */
	public void setDistrict(int positionX, int positionY, District district) {
		this.map[positionX][positionY] = district;
		System.out.println(getGeneralSatisfaction());
	}
	
	
/*************************************************************************************************/
	
	
	/*
	 * Print the current map 
	 */
	public void printMap() 
	{
		System.out.println("\nMAP :\n");
		for(int i=0 ; i<this.getLength() ; i++)
		{
			for(int j=0 ; j<this.getLength() ; j++)
			{
				System.out.print(" | " + getDistrict(i, j));
			}
			System.out.print(" |\n");
		}
	}
	
	
	/**
	 * Test if it's the end of the game, return 0 to contiue, -1 to end with a defeat and 1 to end with a victory
	 * @return -1, 1 or 0
	 */
	public int endGame() {
		if(this.getFunds()<=-20000 || (this.getGeneralSatisfaction()<=5 && this.getGeneralPopulation() > 100)) {
			return -1;
		}
		else if(this.getGeneralSatisfaction()>=100 /*|| this.getFunds()>=5000000*/) {
			return 1;
		}
		return 0;
	}
	
	
	/**
	 * Remove a line from the city (all stations)
	 * @param line
	 */
	public void removeLine(Line line) {
		ArrayList<Station> stations = line.getStations();
		
		for(Station station : stations) {
			station.removeLine(line);
		}
	}
	
	
	public void changeDensity(String type, int positionX, int positionY, boolean increment) { //increment = true when construction / = false when destruction
		District currentDistrict = this.getDistrict(positionX, positionY);
		int satisfaction = currentDistrict.getSatisfaction();
		
		if(type == "moving") {
			if(increment) {
				satisfaction *= 1.2;
			}
			else {
				satisfaction *= 0.90;
			}
		}
		else if(type == "station") {
			if(increment) {
				satisfaction *= 1.3;
			}
			else {
				satisfaction *= 0.85;
			}
			
			for(int i=positionX-1 ; i<=positionX+1 ; i++) {
				for(int j=positionY-1 ; j<=positionY+1 ; j++) {
					if(i>=0 && j>=0 && i<getLength() && j<getLength()) {
						District newCurrentDistrict = this.getDistrict(i, j);
						if(newCurrentDistrict != null) {
							int newSatisfaction = newCurrentDistrict.getSatisfaction();
							if(increment) {
								newSatisfaction *=1.15;
							}
							else {
								newSatisfaction *=0.98;
							}
							
							newCurrentDistrict.setSatisfaction(newSatisfaction);
							newCurrentDistrict.calculateDensity(newCurrentDistrict.getColor());
						}
					}
				}
			}
		}
		else if(type == "business") {
			if(increment) {
				satisfaction *= 1.4;
			}
			else {
				satisfaction *= 0.8;
			}
			
			for(int i=positionX-1 ; i<=positionX+1 ; i++) {
				for(int j=positionY-1 ; j<=positionY+1 ; j++) {
					if(i>=0 && j>=0 && i<getLength() && j< getLength()) {
						District newCurrentDistrict = this.getDistrict(i, j);
						if(newCurrentDistrict != null ) {
							int newSatisfaction = newCurrentDistrict.getSatisfaction();
							if(increment) {
								newSatisfaction *= 1.05;
							}
							else {
								newSatisfaction *= 0.98;
							}
							
							newCurrentDistrict.setSatisfaction(newSatisfaction);
							newCurrentDistrict.calculateDensity(newCurrentDistrict.getColor());
						}
					}
				}
			}
		}
		
		currentDistrict.setSatisfaction(satisfaction);
		currentDistrict.calculateDensity(currentDistrict.getColor());
	}
	
	
/*************************************************************************************************/
				/*City statitics*/
	
	
	/**
	 * Return the general satisfaction of the city (average)
	 * @return generalSatisfaction/div
	 */
	public int getGeneralSatisfaction() {
		int generalSatisfaction = 0;
		int div = 1;
		int nbDisctrict = 0; 
		int length = this.getLength();
		int satisf; 
		for(int i=0 ; i<length ; i++){
			for(int j=0 ; j<length ; j++){
				District currentDistrict = getDistrict(i, j);
				if(currentDistrict != null) {
					generalSatisfaction += currentDistrict.getSatisfaction();
					div++;
					nbDisctrict = div -1; 
				}
			}
		}
		if(div ==1) satisf = generalSatisfaction; 
		else satisf = generalSatisfaction/nbDisctrict; 
		return satisf;
	}
	
	
	/**
	 * Return the general population of the city
	 * @return generalPopulation
	 */
	public int getGeneralPopulation() {
		int generalPopulation = 0;
		int length = this.getLength();
		
		for(int i=0 ; i<length ; i++){
			for(int j=0 ; j<length ; j++){
				District currentDistrict = getDistrict(i, j);
				if(currentDistrict != null) {
					generalPopulation += currentDistrict.getPopulation();
				}
			}
		}
		return generalPopulation;
	}
	
	
	/**
	 * Return the number of stations in the city
	 * @return nbStations
	 */
	public int getGeneralNumberOfStation() {
		int nbStations = 0;
		int length = this.getLength();
		
		for(int i=0 ; i<length ; i++){
			for(int j=0 ; j<length ; j++){
				District currentDistrict = getDistrict(i, j);
				if(currentDistrict != null) {
					if(currentDistrict.getStation() != null) {
						nbStations++;
					}
				}
			}
		}
		return nbStations;
	}
	
	
	/**
	 * Return the number of lines in the city
	 * @return nbLines
	 */
	public int getGeneralNumberOfLines() {
		return getTownLines().size();
	}

	
/*************************************************************************************************/
	
	/**
	 * Get the construction price for a station
	 * @return corresponding construction price
	 */
	public int getStationConstructionPrice() {
		if(funds>=500000) return 100000;
		else if(funds>=250000) return 70000;
		else if(funds>=175000) return 60000;
		else return 50000;
	}
	
	/**
	 * Get the destruction price for a station
	 * @return corresponding destruction price
	 */
	public int getStationDestructionPrice() {
		if(funds>=500000) return 30000;
		else if(funds>=250000) return 25000;		 

		else if(funds>=175000) return 20000;
		else return 15000;
	}
	
	/**
	 * Get the maintenance price for a station
	 * @return corresponding maintenance price
	 */
	public int getStationMaintenancePrice() {
		if(funds>=500000) return 3500;
		else if(funds>=250000) return 3000;
		else if(funds>=175000) return 2000;
		else return 1500;
	}
	
	/**
	 * Get the construction price for a station
	 * @return corresponding construction price
	 */
	public int getStateDistrictConstructionPrice() {
		if(funds>=500000) return 60000;
		else if(funds>=250000) return 40000;
		else if(funds>=175000) return 35000;
		else return 30000;
	}
	
	/**
	 * Get the construction price for a station
	 * @return corresponding construction price
	 */
	public int getStateDistrictMaintenancePrice() {
		if(funds>=500000) return 7000;
		else if(funds>=250000) return 5000;
		else if(funds>=175000) return 4000;
		else return 3500;
	}
	
	/**
	 * Get the construction price for a line section
	 * @return corresponding construction price
	 */
	public int getLineSegmentConstructionPrice() {
		if(funds>=500000) return 40000;
		else if(funds>=250000) return 30000;
		else if(funds>=175000) return 250000;
		else return 20000;
	}
	
	/**
	 * Get the destruction price for a line section
	 * @return corresponding destruction price
	 */
	public int getLineSegmentDestructionPrice() {
		if(funds>=500000) return 10000;
		else if(funds>=250000) return 7000;
		else if(funds>=175000) return 6000;
		else return 5000;
	}
	
	/**
	 * Get the Maintenance price for a line section
	 * @return corresponding maintenance price
	 */
	public int getLineMaintenancePrice() {
		if(funds>=500000) return 2500;
		else if(funds>=250000) return 2000;
		else if(funds>=175000) return 1500;
		else return 1000;
	}
	
	/**
	 * Updates funds after buying a station
	 * @return true on success, false if funds are too low
	 */
	public boolean payStationConstruction(){
		int price = this.getStationConstructionPrice();
		if(funds>=price) {
			funds -= price;
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * Updates funds after destroying a station
	 * @return true on success, false if funds are too low
	 */
	public boolean payStationDestruction(){
		int price = this.getStationDestructionPrice();
		if(funds>=price) {
			funds -= price;
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Updates funds after buying a state district
	 * @return true on success, false if funds are too low
	 */
	public boolean payStateDistrictConstruction(){
		int price = this.getStateDistrictConstructionPrice();
		if(funds>=price) {
			funds -= price;
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * Updates funds after buying a line section
	 * @return true on success, false if funds are too low
	 */
	public boolean payLineSegmentConstruction(){
		int price = this.getLineSegmentConstructionPrice();
		if(funds>=price) {
			funds -= price;
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * Updates funds after destroying a line section
	 * @return true on success, false if funds are too low
	 */
	public boolean payLineSegmentDestruction(){
		int price = this.getLineSegmentDestructionPrice();
		if(funds>=price) {
			funds -= price;
			return true;
		}
		else {
			return false;
		}
	}
	
	
	/**
	 * Updates funds after maintenance on state district
	 */
	public void payStateDistrictMaintenance(){
		int quantity = 0;
		 for(int i=0;i<length;i++) {
			 for(int j=0;j<length;j++){
				 if(map[i][j]!=null) {
					 if(map[i][j].getClass().getName()=="game.State") {
						 quantity++;
					 }
				 }
			 }
		 }
		funds -= quantity*getStateDistrictMaintenancePrice();
	}
	
	
	/**
	 * Updates funds after maintenance on line section
	 */
	public void payLineMaintenance(){
		int quantity = 0;
		ArrayList<Line> visited = new ArrayList<Line>();
		for(int i=0;i<length;i++) {
			 for(int j=0;j<length;j++){
				 if(map[i][j]!=null) {
					 Station station = map[i][j].getStation();
					 if(station!=null) {
						 ArrayList<Line> lines = station.getLines();
						 if(lines!=null) {
							 for(Line line : lines) {
								 if(!visited.contains(line)) {
									 quantity += line.getStations().size();
									 visited.add(line);
								 }
							 }
						 }
					 }
				 }
			 }
		 }
		funds -= quantity*getLineMaintenancePrice();
	}
	
	/**
	 * Updates funds after maintenance on station
	 */
	public void payStationMaintenance(){
		int quantity = 0;
		for(int i=0;i<length;i++) {
			 for(int j=0;j<length;j++){
				 if(map[i][j]!=null) {
					 Station station = map[i][j].getStation();
					 if(station!=null) {
						 quantity++;
					 }
				 }
			 }
		 }
		funds -= quantity*getStationMaintenancePrice();
	}
	
	/**
	 * Updates funds after collecting residential taxes
	 */
	public void collectResidentialTaxes(){
		int amount = 0;
		 for(int i=0;i<length;i++) {
			 for(int j=0;j<length;j++){
				 if(map[i][j]!=null) {
					 if(map[i][j].getClass().getName()=="game.Resident") {
						 int currentAmount = 140*map[i][j].getPopulation();
						 if(map[i][j].getSatisfaction()<=10) currentAmount *= 0.95;
						 else if(map[i][j].getSatisfaction()<=5) currentAmount *= 0.7;
						 else if(map[i][j].getSatisfaction()<=3) currentAmount *= 0.6;
						 amount += currentAmount;
					 }
				 }
			 }
		 }
		 funds += amount;
	}
	
	/**
	 * Updates funds after collecting business taxes
	 */
	public void collectBusinessTaxes(){
		int amount = 0;
		int length = this.getLength();
		 for(int i=0;i<length;i++) {
			 for(int j=0;j<length;j++){
				 if(map[i][j]!=null) {
					 if(map[i][j].getClass().getName()=="game.Business") {
						 int currentAmount = 140*map[i][j].getPopulation();
						 if(map[i][j].getSatisfaction()<=10) currentAmount *= 0.95;
						 else if(map[i][j].getSatisfaction()<=5) currentAmount *= 0.7;
						 else if(map[i][j].getSatisfaction()<=3) currentAmount *= 0.6;
						 amount += currentAmount;
					 }
				 }
			 }
		 }
		 funds += amount;
	}
}
