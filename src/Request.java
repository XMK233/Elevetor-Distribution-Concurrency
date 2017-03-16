
public class Request {
	private String category = "";
	private Elevator elevator = new Elevator();
	private Floor floor = new Floor();
	public boolean used = false;
	//properties
	public String getCategory(){
		return this.category;
	}
	public Elevator getElevator(){
		return this.elevator;
	}
	public Floor getFloor(){
		return this.floor;
	}
	//
	public Request(){
		this.elevator.setting(0,0,0);
		this.floor.setting(0, 0, "", 0);
	}
	public Request(int eleSign, int tarFloor, int reqTime){
		this.category = "ER";
		this.elevator.setting(eleSign, tarFloor, reqTime);
	}
	public Request(int floorSign, int curFloor, String upOrDown, int reqTime){
		this.category = "FR";
		this.floor.setting(floorSign, curFloor, upOrDown, reqTime);
	} 
	//the properties of the request is just the floor's or elevator's
}