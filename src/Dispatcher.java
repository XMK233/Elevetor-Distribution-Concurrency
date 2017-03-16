//explain how elevator moves
public class Dispatcher { 
	private double[][] finSequence = new double[101][3];
	//
	public Dispatcher(){
		int i;
		int j;
		for (i = 0; i < 101; i++){
			for( j = 0; j < 3; j++){
				this.finSequence[i][j] = 0;
			}
		}
	}
	public double[][] getFinSequence(){
		return this.finSequence;
	}
	public void FoolSchedule(String str){
		this.finSequence[0][0] = 1;
		this.finSequence[0][1] = -1;//-1 is down while 1 is up, only -1 and 1 exist.
		this.finSequence[0][2] = 0;
		RequestQueue finalQueue = new RequestQueue(str);//一个类的数组和数组长度可用
		int i = 0;
		Request tempRequest = new Request();
		int tempFloor = 0, tempReqTime = 0;
		for(i = 0; i < finalQueue.getNum(); i++){
			tempRequest = finalQueue.getRequest(i);
			if (tempRequest.getCategory() == "ER"){
				tempFloor = tempRequest.getElevator().getTarFloor();
				tempReqTime = tempRequest.getElevator().getReqTime();
			}
			else if (tempRequest.getCategory() == "FR"){
				tempFloor = tempRequest.getFloor().getCurFloor();
				tempReqTime = tempRequest.getFloor().getReqTime();
			}// get temporary data
			this.finSequence[i + 1][0] = tempFloor;// give the value of floor 
			if (tempFloor > this.finSequence[i][0])
				this.finSequence[i + 1][1] = 1;
			else if (tempFloor < this.finSequence[i][0])
				this.finSequence[i + 1][1] = -1;
			else if (tempFloor == this.finSequence[i][0])
				this.finSequence[i + 1][1] = this.finSequence[i][1];// judge the up and down
			if (tempReqTime == 0) 
				this.finSequence[i + 1][2] = Math.abs(tempFloor - this.finSequence[i][0]) * 0.5;
			else if (tempReqTime >= (this.finSequence[i][2] + 1))
				this.finSequence[i + 1][2] = tempReqTime + Math.abs(tempFloor - this.finSequence[i][0]) * 0.5;
			else if (tempReqTime < (this.finSequence[i][2] + 1))
				this.finSequence[i + 1][2] = this.finSequence[i][2] + 1 + Math.abs(tempFloor - this.finSequence[i][0]) * 0.5;//calculating the time
		}
		this.PrintSequence(finalQueue.getNum());
	}
	//
	public void PrintSequence(int n){
		int i = 0;
		for (i = 0; i < n + 1; i++){
			System.out.print("(" + (int)this.finSequence[i][0] + ",");//
			if (this.finSequence[i][1] == 1) System.out.print("UP" + ",");
			else if (this.finSequence[i][1] == -1) System.out.print("DOWN" + ",");//
			System.out.println(this.finSequence[i][2] + ")");
		}
	}
	
}

/*int n = 0;
String direction = "";
double t = 0;

*
*/