
public class Floor {
	private int floorSign;
	private int curFloor;
	private String upOrDown; 
	private int reqTime;
	//properties
	public Floor(){
		this.floorSign = 0;
		this.curFloor = 0;
		this.upOrDown = "";
		this.reqTime =0;
	}
	//initialize 
	public int getFloorSign(){
		return this.floorSign;
	}
	public int getCurFloor(){
		return this.curFloor;
	}
	public String getUpOrDown(){
		return this.upOrDown;
	}
	public int getReqTime(){
		return this.reqTime;
	}
	//get some status
	public void setting(int f, int c, String u, int r){
		this.floorSign = f;
		this.curFloor = c;
		this.upOrDown = u;
		this.reqTime = r;
	}
	//setting some properties
}
