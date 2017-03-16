import java.util.ArrayList;
public class Dispatcher1 extends Dispatcher{
	//inherit PrintSequence but not the others
	private double[][] finSequenceA = super.getFinSequence();// time outcome
	public String[] ALS_Strings = new String[101];//ALS outcome
	public int num3 = 0;
	
	public int num1 = 0;//the numbers of main requests.
	public Dispatcher1(){
		int i;
		int j;
		for (i = 0; i < 101; i++){
			for( j = 0; j < 3; j++){
				this.finSequenceA[i][j] = 0;
			}
		}
	}//construction
	public String toString(ArrayList<Request> lines){
		String str = "";
		int length = lines.size();
		
		if (lines.get(0).getCategory().equals("FR")){
			str += ("(" + "FR," + Integer.toString(lines.get(0).getFloor().getCurFloor()) + "," + lines.get(0).getFloor().getUpOrDown() + "," +
					Integer.toString(lines.get(0).getFloor().getReqTime()) + ")");
		}
		else if(lines.get(0).getCategory().equals("ER")){
			str += ("(" + "ER," + Integer.toString(lines.get(0).getElevator().getTarFloor()) + ","  +
					Integer.toString(lines.get(0).getElevator().getReqTime()) + ")");
		}
		str += "(";
		for(int i = 1; i < length; i++){
			Request temp = new Request();
			if (lines.get(i) instanceof Request){
				temp = (Request) lines.get(i);
				if (temp.getCategory().equals("FR")){
					str += ("(" + "FR," + Integer.toString(temp.getFloor().getCurFloor()) + "," + temp.getFloor().getUpOrDown() + "," +
							Integer.toString(temp.getFloor().getReqTime()) + ")");
				}
				else if(temp.getCategory().equals("ER")){
					str += ("(" + "ER," + Integer.toString(temp.getElevator().getTarFloor()) + ","  +
							Integer.toString(temp.getElevator().getReqTime()) + ")");
				}
			}
		}
		str += ")";
		if (str != "" && length == 1) return str.substring(0, str.length() - 2);
		return str;
	}// make a toStringLines function
	public boolean allUsed(Request allRequest[], int num1){
		int i = 0;
		for (i = 0; i < num1; i++){
			if (allRequest[i].used == false)
				return false;
		}
		return true;
	}//
	public void PrintSequence(int n, double [][]finSequence){
		int i = 0;
		for (i = 0; i < n; i++){
			System.out.print("(" + (int)finSequence[i][0] + ",");//
			if (finSequence[i][1] == 1) System.out.print("UP" + ",");
			else if (finSequence[i][1] == -1) System.out.print("DOWN" + ",");
			else if (finSequence[i][1] == 0) System.out.print("STAY,");
			System.out.println(finSequence[i][2] + ")");
		}
	}//
	public void ALS_Schedule(String string){
		RequestQueue finalQueue = new RequestQueue(string);//the queue
		
		Request allRequest[] = finalQueue.getAllRequest();//all the request
		int num1 = finalQueue.getNum();
		
		/*Request reqList[] = new Request[200]; 
		int head = 0, rear = 0;*/
		
		ArrayList<Request> ALS_Lines = new ArrayList<Request>();//the temporary storage array of the Requests
		
		CurrentStatus curStatus = new CurrentStatus();// status register
		int i = 0, j = 0;
		
		Harmony harmonyi = null, harmonyj = null;
		
		double [][]StopSequence = new double[101][3];
		int num2 = 0;
		
		int sameFloorFlag = 0;
		
		while (true){
			if(this.allUsed(allRequest, num1))
				break;//
			if (ALS_Lines.size() != 0){
				harmonyj = new Harmony(ALS_Lines.get(0));
				ALS_Lines.clear();
				curStatus.curFloor = curStatus.curFloor;
				curStatus.dirStatus = (harmonyj.floor < curStatus.curFloor) ? "DOWN" : 
					(harmonyj.floor > curStatus.curFloor) ? "UP" : "STAY"/*curStatus.dirStatus*/;
				curStatus.tarFloor = harmonyj.floor;
				curStatus.time = (harmonyj.time <= curStatus.time) ? curStatus.time ://remain to be seen
					(harmonyj.time > curStatus.time) ? harmonyj.time : 0 ;
			}
			else{
				for(i = 0; i < num1; i++){
					if(allRequest[i].used == false){
						harmonyi = new Harmony(allRequest[i]);
						if (i == 0){
							curStatus.curFloor = 0;//1
							curStatus.dirStatus = "UP";
							curStatus.tarFloor = harmonyi.floor;
							curStatus.time = -0.5;//0
						}
						else{
							curStatus.curFloor = curStatus.curFloor;
							curStatus.dirStatus = (harmonyi.floor < curStatus.curFloor) ? "DOWN" : 
								(harmonyi.floor > curStatus.curFloor) ? "UP" : "STAY"/*curStatus.dirStatus*/;
							curStatus.tarFloor = harmonyi.floor;
							curStatus.time = (harmonyi.time <= curStatus.time) ? curStatus.time ://remain to be seen
								(harmonyi.time > curStatus.time) ? harmonyi.time : 0 ;
						}
						ALS_Lines.add(allRequest[i]);// main request has been added in.
						break;
					}
				}
			}//main request
			while(true){
				if (curStatus.dirStatus.equals("UP")) curStatus.goUp(1);
				else if (curStatus.dirStatus.equals("DOWN")) curStatus.goDown(1);
				sameFloorFlag = 0;
				if (i == num1 - 1){
					harmonyj = new Harmony(allRequest[i]);
					if (harmonyj.floor == curStatus.curFloor){
						if ((harmonyj.type.equals("FR") && harmonyj.direction.equals(curStatus.dirStatus)) || 
							(harmonyj.type.equals("ER")) ||
							(harmonyj.type.equals("FR") && harmonyj.floor == 10 && curStatus.dirStatus.equals("UP"))
							) {//////////////////////this information can be copy to the latter codes.
							if (harmonyj.time < curStatus.time){
								//ALS_Lines.add(allRequest[i]);//
								StopSequence[num2][0] = curStatus.curFloor;
								if (curStatus.dirStatus.equals("UP"))
									StopSequence[num2][1] = 1;
								else if (curStatus.dirStatus.equals("DOWN"))
									StopSequence[num2][1] = -1;
								else if (curStatus.dirStatus.equals("STAY"))
									StopSequence[num2][1] = 0;
								StopSequence[num2][2] = curStatus.time;
								num2++;
								allRequest[i].used = true;
								curStatus.time += 1;
								break;
							}
						}
					}
					//continue;
				}// the last request
				else{
					for (j = i + 1; j < num1; j++){
						if (allRequest[j].used) continue; 
						harmonyj = new Harmony(allRequest[j]);
						if (harmonyj.floor == curStatus.curFloor){
							if ((harmonyj.type.equals("FR") && harmonyj.direction.equals(curStatus.dirStatus)) || 
								(harmonyj.type.equals("ER"))||
								(harmonyj.type.equals("FR") && harmonyj.floor == 10 && curStatus.dirStatus.equals("UP"))
								) {    
								if (harmonyj.time < curStatus.time){
									ALS_Lines.add(allRequest[j]);//
									allRequest[j].used = true;
									/*if(sameFloorFlag == 0){
										sameFloorFlag++;
									}// if the floor resemble the tarFloor, do so//////////////////////////////////////////
									else {
										continue;
									}*/
									if (sameFloorFlag < 1){
										StopSequence[num2][0] = curStatus.curFloor;
										if (curStatus.dirStatus.equals("UP"))
											StopSequence[num2][1] = 1;
										else if (curStatus.dirStatus.equals("DOWN"))
											StopSequence[num2][1] = -1;
										else if (curStatus.dirStatus.equals("STAY"))
											StopSequence[num2][1] = 0;
										StopSequence[num2][2] = curStatus.time;
										num2++;
										curStatus.time += 1;
									}
									sameFloorFlag++;
								}
							}
						}  
					}
				}
				// 
				if(curStatus.curFloor == curStatus.tarFloor){
					if (sameFloorFlag < 1){
						StopSequence[num2][0] = curStatus.curFloor;
						if (curStatus.dirStatus.equals("UP"))
							StopSequence[num2][1] = 1;
						else if (curStatus.dirStatus.equals("DOWN"))
							StopSequence[num2][1] = -1;
						else if (curStatus.dirStatus.equals("STAY"))
							StopSequence[num2][1] = 0;
						StopSequence[num2][2] = curStatus.time;
						num2++;
						curStatus.time += 1;
					}
					allRequest[i].used = true;
					for(int k = 0; k < num1; k++){
						if (allRequest[k].used == true) continue;
						harmonyj = new Harmony(allRequest[k]);
						if(harmonyj.floor > curStatus.curFloor && curStatus.dirStatus.equals("UP") ||
						   harmonyj.floor < curStatus.curFloor && curStatus.dirStatus.equals("DOWN")
						   ){
							if (harmonyj.time < curStatus.time){
								ALS_Lines.add(allRequest[k]);
							}
						}
					}
					curStatus.dirStatus = "STAY";
					break;
				}
			}// counting the floors.	
			this.ALS_Strings[this.num3] = toString(ALS_Lines);
			this.num3++;
			for (int k = 0; k < ALS_Lines.size(); k++){
				if (ALS_Lines.get(k).used == true) {ALS_Lines.remove(k); k--;}
			}
			//ALS_Lines.clear();
		}//main request has accomplished. so store it;
		System.out.println();
		for(i = 0; i < this.num3; i++){
			System.out.println(this.ALS_Strings[i]);
		}
		System.out.println();
		this.PrintSequence(num2, StopSequence);
	}//generate the ALS_Strings, and every elements in this array should be FoolScheduled
	
	
}