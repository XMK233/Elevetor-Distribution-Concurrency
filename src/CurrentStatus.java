public class CurrentStatus implements Status {
	public int curFloor, tarFloor;
	public String dirStatus;
	public double time;
	//the properties
	public CurrentStatus(){
		this.curFloor = this.tarFloor = 0;
		this.dirStatus = "UP";
		this.time = 0;
	}
	//constructions. 1 and -1 means up and down respectively
	public void setCurStatus(int i, String j, int k){
		this.curFloor = i;
		this.dirStatus = j;
		this.tarFloor = k;
	}
	//setting the properties.
	public int getCurFloor() {
		return this.curFloor;
	}

	public String getMotStatus() {
		return this.dirStatus;
	}

	public int getTarFloor() {
		return this.tarFloor;
	}
	public void goUp(int num){
		this.curFloor += num;
		this.time += 0.5 * num;
	}
	public void goDown(int num){
		this.curFloor -= num;
		this.time += 0.5 * num;
	}
}
