
public class Elevator {
	private int eleSign;
	private int tarFloor;
	private int reqTime;
	//the private variable
	public Elevator(){
		this.eleSign = 0;
		this.tarFloor = 0;
		this.reqTime = 0;
	}
	public Elevator(int a, int b, int c){
		this.eleSign = a;
		this.tarFloor = b;
		this.reqTime = c;
	}
	//construction method
	public int getEleSign(){
		return this.eleSign;
	}
	public int getTarFloor(){
		return this.tarFloor;
	}
	public int getReqTime(){
		return this.reqTime;
	}
	//getting the private status
	public void setting(int e, int t, int r){
		this.eleSign = e;
		this.tarFloor = t;
		this.reqTime = r;
	}
	//setting the properties
}

//private String sign = "elevator";