
import java.sql.Date;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.concurrent.ExecutorService;  
import java.util.concurrent.Executors;  
import java.util.concurrent.ThreadFactory;

class ExceptionThread2 implements Runnable  
{  
      
    /* (non-Javadoc) 
     * @see java.lang.Runnable#run() 
     */  
    public void run()  
    {     
        throw new RuntimeException();  
    }  
}
class MyUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler  
{  
  
    public void uncaughtException(Thread thread, Throwable throwable)  
    {  
        // TODO Auto-generated method stub  
        System.out.println("caught "+throwable);  
    }  
      
}  
class HandlerThreadFactory implements ThreadFactory  
{  
      
    
    public Thread newThread(Runnable runnable)  
    {  
        // TODO Auto-generated method stub  
        Thread t = new Thread(runnable);  
        t.setUncaughtExceptionHandler(new MyUncaughtExceptionHandler());  
        return t;  
    }  
      
} 
class RequestV extends Request{
	private String type;
	private int floor;
	private int EleNum;
	private String direction;
	public boolean used = false;
	//
	public String get_Type(){
		return this.type;
	}
	public int get_Floor(){
		return this.floor;
	}
	public int get_EleNum(){
		return this.EleNum;
	}
	public String get_Direction(){
		return this.direction;
	}
	public RequestV(String a, int b, int c, String d){
		this.type = a;
		this.floor = b;
		this.EleNum = c;
		this.direction = d;
	}
}
class RequestVQueue{
	private RequestV []queue = new RequestV[5];
	private int num = 0;
	//
	public RequestV[] getAllRequestV(){
		return this.queue;
	}
	public int getNum(){
		return this.num;
	}
	//
	public RequestVQueue(String string){
		String []lines = string.split("\\.");
		Pattern neg_1 = Pattern.compile("ER");
		Pattern neg_2 = Pattern.compile("FR");
		Pattern neg_3 = Pattern.compile("[0-9\\.]+");
		Pattern neg_4 = Pattern.compile("UP");
		Pattern neg_5 = Pattern.compile("DOWN");
		
		for (int i = 0; i < lines.length; i++){
			Matcher neg_1m = neg_1.matcher(lines[i]);
			Matcher neg_2m = neg_2.matcher(lines[i]);
			Matcher neg_3m = neg_3.matcher(lines[i]);
			Matcher neg_4m = neg_4.matcher(lines[i]);
			Matcher neg_5m = neg_5.matcher(lines[i]);
			int j = 0;
 			if (neg_1m.find()){
				int []temp1 = new int[2];
				j = 0;
 				while (neg_3m.find()){
 					temp1[j] = Integer.parseInt(neg_3m.group());
 					j++;
 				}
				this.queue[i] = new RequestV("ER", temp1[1], temp1[0], "-");
				this.num++;
			}//find the ER
 			else if(neg_2m.find()){
 				int []temp2 = new int[2];
 				j = 0;
 				while (neg_3m.find()){
 					temp2[j] = Integer.parseInt(neg_3m.group());
 					j++;
 				}
				String direction = (neg_4m.find()) ? "UP" :
									(neg_5m.find()) ? "DOWN" :
									"";
				this.queue[i] = new RequestV("FR", temp2[0], 0, direction);
				this.num++;
 			}//find the FR
		}//
		this.num = lines.length;
	}//we consider that this string is valid 
}
class Tray1 {
	public ArrayList <RequestV> requests  = new ArrayList<RequestV> ();
	public Tray1(){
		this.requests.clear();
	} 
 	public void generateTray(String string){
		RequestVQueue finalQueue = new RequestVQueue(string);//the queue
		RequestV allRequestV[] = finalQueue.getAllRequestV();//all the request
		int i = 0;
		for (i = 0; i < finalQueue.getNum(); i++){
			this.requests.add(allRequestV[i]);
		}//create the whole 
	}
	public ArrayList<RequestV> getArrayRequests(){
		return this.requests;
	}
	public synchronized boolean allUsed(){
		for(int i = 0; i < this.requests.size(); i++){
			if (this.requests.get(i).used == false) return false;
		}
		return true;
	}
	public synchronized void use(int i){
		this.requests.get(i).used = true;
	}
} 
//
/*class Clock extends Thread{
	
}*/
//
class RequestImitator extends Thread{
	public volatile Tray1 tray = new Tray1();
	public Tray1 getTray(){
		return this.tray;
	}
	public boolean hasInput = false;
	public static boolean wrongRequest(String string){
		Pattern neg_3 = Pattern.compile("[0-9\\.]+");
		Matcher neg_3m = neg_3.matcher(string);
		Pattern neg_4 = Pattern.compile("(UP|DOWN)");
		Matcher neg_4m = neg_4.matcher(string);
		int []temp2 = new int[2];
		int j = 0;
		while (neg_3m.find()){
			temp2[j] = Integer.parseInt(neg_3m.group());
			j++;  
		}
		if ((j == 1 && (temp2[0] >20 || temp2[0] < 1)) || 
			(j == 2 && (temp2[1] >20 || temp2[1] < 1)) || 
			(j == 2 && (temp2[0] >3 || temp2[0] < 1))) 
			return true; 
		if( neg_4m.find()) {
			if (temp2[0] == 20 && neg_4m.group().equals("UP")) 
				return true;//10th floor and up
			if (temp2[0] == 1 && neg_4m.group().equals("DOWN")) 
				return true;//first floor and down	
		}
		//the match of floor and direction
		return false;
		
	}
	public static String validateTheString (String string){
		String validated = "";//·µ»ØµÄ×Ö´®
		String []lines = string.split("\\.");//·ÖÁÑ×Ö·û´®
		//int time =  -1;//Ê±¼äÐòÁÐ´æ´¢Æ÷
		//int firstFlag = 0;
		Pattern neg_1 = Pattern.compile("^(\\(FR,\\d{1,2},(UP|DOWN)\\))$");//FR format 
		Pattern neg_2 = Pattern.compile("^(\\(ER,\\#\\d{1},\\d{1,2}\\))$");//ER format
		Pattern neg_3 = Pattern.compile("[0-9\\.]+");
		if (string.length() > 100) 
			return "";
		//
		String newsc = string;
	  	//Pattern neg_n = Pattern.compile("^(((\\(FR,\\d{1,2},(UP|DOWN),\\d{1,6}\\))|(\\(ER,\\d{1,2},\\d{1,6}\\))).)*((\\(FR,\\d{1,2},(UP|DOWN),\\d{1,6}\\))|(\\(ER,\\d{1,2},\\d{1,6}\\)))$");
		Pattern neg_n = Pattern.compile("^(((\\(FR,\\d{1,2},(UP|DOWN)\\))|(\\(ER,\\#\\d{1},\\d{1,2}\\)))\\.)*((\\(FR,\\d{1,2},(UP|DOWN)\\))|(\\(ER,\\#\\d{1},\\d{1,2}\\)))$");
		Matcher neg_nx = neg_n.matcher(newsc);
		//boolean match = neg_nx.find();
		if (!neg_nx.find()) 
			return "";
		//
		int i = 0;
		int j = 0;
		for (i = 0; i < lines.length && i < 5; i++ ){
			Matcher neg_1m = neg_1.matcher(lines[i]);
			Matcher neg_2m = neg_2.matcher(lines[i]);
			Matcher neg_3m = neg_3.matcher(lines[i]);
			if (neg_1m.find()){
				if (wrongRequest(lines[i])){
					lines[i] = "";
					continue;
				}
			}//FR
			else if (neg_2m.find()){
				if (wrongRequest(lines[i])/* || firstFlag == 0*/) {
					lines[i] = "";//delete the wrong request
					continue;
				}
			}//ER
			else return "";//return "" means invalid input
		}
		for (i = 0; i < lines.length && i < 5; i++ ){
			if (lines[i] != "")
				validated += (lines[i] + ".");
		}
		if (validated != "" && validated.charAt(validated.length() - 1) == '.') return validated.substring(0, validated.length() - 1);
		return validated;
	}//validate the string;
	public void run(){
		System.out.println("ready to go: ");   
		while (true){ 
			String str=new Scanner(System.in).nextLine();
			String newsc = str.replaceAll("\\s+", "");
			String newsc1 = validateTheString(newsc);
			System.out.println("valid request(s) is(are) as follow " + newsc1);
			if(!newsc1.equals("")){
				this.tray.generateTray(newsc1);
				this.hasInput = true;
			}
		}
	}
}
//
class Elevators extends Thread{
	private int name;
	private int curFloor = 1;
	private int tarFloor = 0;
	private String movDir = "STAY";//move direction
	private int movAmo = 0;//move amount
	private double time = 0;
	//
	public volatile ArrayList <RequestV> requestQueue = new ArrayList<RequestV>();//|]00 array
	public int reqNum = 0;
	//public boolean MainConfirmed= false;
	public boolean MainAccomplished = false,
			       mainConfirmed = false,
			       empRequest = true;
	//
	public Elevators(int n) { this.name = n; time = System.currentTimeMillis();}
	public int getCurFloor(){ return this.curFloor; }
	public int getTarFloor(){ return this.tarFloor; }
	public String getMovDir() {return this.movDir; }
	public double getTime() {return this.time; }
	public synchronized void printInfo( RequestV harmony, String type){
		if (type.equals("stop")){
			System.out.println("(" + "No."+ this.name + ", " + "fr." + this.curFloor +", " + this.movDir + ", " + this.movAmo + " in-total" + ", " +  (double)Math.round((System.currentTimeMillis() - this.time)/100)/10 + "s" + ")");
			System.out.print("the request that has been accomplished: ");
		}
		else if (type.equals("requests")){
			System.out.print("(" + harmony.get_Type()+", " + harmony.get_Floor() + ", " + harmony.get_EleNum() + ", " + harmony.get_Direction() + ")");
		}
	}
	public synchronized void goTo(int num, String direction){
		try {
			sleep( num * 3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}//sleep for 3s
		if (direction.equals("UP"))
			this.curFloor++;
		else if (direction.equals("DOWN"))
			this.curFloor--;
		this.movAmo+=num;
	}
	public synchronized void moveDoor(){
		try {
			sleep(6000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//System.out.println("request(s) has(have) been dealed with.");
	}
	public synchronized void addRequest(RequestV r){ 
		requestQueue.add(r);
		this.empRequest = false;
	}
	public synchronized void removeUsedRequest(ArrayList <RequestV> a) {
		for (int i = 0; i < a.size(); i++){
			if (a.get(i).used) {
				a.remove(i);
				i--;
			}
		}
	}
	public /*synchronized*/ void run(){
		int i = 0, j = 0;
		RequestV harmony;// it depends. It remains to be fulfilled.
		boolean onTheMove = false, 
				doorMov = false;
		int mainModified = 0;
		while (true){
			if (this.requestQueue.isEmpty()){
				this.empRequest = true;
			}
			else if (!this.requestQueue.isEmpty()){
				for (i = 0; i < requestQueue.size(); i++){
					harmony = requestQueue.get(i);
					if (!mainConfirmed){
						this.mainConfirmed = true;
						this.tarFloor = harmony.get_Floor();
						this.movDir = harmony.get_Floor() > this.curFloor ? "UP" :
							  		  harmony.get_Floor() < this.curFloor ? "DOWN" : 
							  		  "STAY";
						this.MainAccomplished = false;
					}
					else 
					{
						if (this.movDir.equals("UP")){
							if (harmony.get_Floor() > this.tarFloor) {
								this.tarFloor = harmony.get_Floor();
								mainModified++;
							}
						}
						else if (this.movDir.equals("DOWN")){
							if (harmony.get_Floor() < this.tarFloor) {
								this.tarFloor = harmony.get_Floor();
								mainModified++;
							}
						}
					}
					//this.tarFloor = harmony.get_Floor() > this.tarFloor ? harmony.get_Floor() : this.tarFloor;
					//mainModified++;
					/*this.movDir = harmony.get_Floor() > this.curFloor ? "UP" :
								  harmony.get_Floor() < this.curFloor ? "DOWN" : 
								  "STAY";*/
					//mainModified++;
					if (mainModified > 0){
						mainConfirmed = true;
						this.MainAccomplished = false;
						mainModified = 0;
					}
					/*this.time = ;*///time still remains to be fulfilled.
				}//confirm the main request
			}
			if(mainConfirmed){
				if (this.movDir.equals("UP")){
					onTheMove  = true;
					this.goTo(1, "UP");
					onTheMove = false;
					int sameFloorFlag;
					int tempFlag = 0;
					for (j = 0, sameFloorFlag = 0; j < requestQueue.size(); j++){
						harmony = requestQueue.get(j);
						if (this.curFloor == harmony.get_Floor()){
							if (sameFloorFlag == 0){
								//System.out.println("(" + "No."+ this.name + ", " + "fr." + this.curFloor +", " + this.movDir + ", " + this.movAmo + " in-total" + ", " +  (double)Math.round((System.currentTimeMillis() - this.time)/100)/10 + ")");
								//System.out.print("the request that has been accomplished: ");
								this.printInfo(harmony, "stop");
								tempFlag = 1;
							}
							//System.out.print("(" + harmony.get_Type()+", " + harmony.get_Floor() + ", " + harmony.get_EleNum() + ", " + harmony.get_Direction() + ")");
							this.printInfo(harmony, "requests");
							sameFloorFlag++;
							requestQueue.get(j).used = true;
						}
						if(this.curFloor == this.tarFloor/*harmony.get_Floor() == this.tarFloor*/){
							mainConfirmed = false;
							this.MainAccomplished = true;
							this.movDir = "STAY";
						}
					}
					if (tempFlag > 0)System.out.println();
					if (tempFlag > 0)moveDoor();
				}//upstairs
				else if (this.movDir.equals("DOWN")){
					onTheMove = true;
					this.goTo(1, "DOWN");
					onTheMove = false;
					int sameFloorFlag;
					int tempFlag = 0;
					for (j = 0, sameFloorFlag = 0; j < requestQueue.size(); j++){
						harmony = requestQueue.get(j);
						if (this.curFloor == harmony.get_Floor()){
							if (sameFloorFlag == 0){
								//System.out.println("(" + "No."+ this.name + ", " + "fr." + this.curFloor +", " + this.movDir + ", " + this.movAmo + " in-total" + ", " +  (double)Math.round((System.currentTimeMillis() - this.time)/100)/10 + ")");
								//System.out.print("the request that has been accomplished: ");
								this.printInfo(harmony, "stop");
								tempFlag = 1;
							}
							//System.out.print("(" + harmony.get_Type()+", " + harmony.get_Floor() + ", " + harmony.get_EleNum() + ", " + harmony.get_Direction() + ")");
							this.printInfo(harmony, "requests");
							sameFloorFlag++;
							requestQueue.get(j).used = true;
						}
						if(this.curFloor == this.tarFloor/*harmony.get_Floor() == this.tarFloor*/){
							mainConfirmed = false;
							this.MainAccomplished = true;
							this.movDir = "STAY";
						}
					}
					if (tempFlag > 0)System.out.println();
					if (tempFlag > 0)moveDoor();
				}//downstairs
				else{
					onTheMove = false;
					int sameFloorFlag;
					int tempFlag = 0;
					for (j = 0, sameFloorFlag = 0; j < requestQueue.size(); j++){
						harmony = requestQueue.get(j);
						if (this.curFloor == harmony.get_Floor()){
							if (sameFloorFlag == 0){
								//System.out.println("(" + "No."+ this.name + ", " + "fr." + this.curFloor +", " + this.movDir + ", " + this.movAmo + " in-total" + ", " +  (double)Math.round((System.currentTimeMillis() - this.time)/100)/10 + ")");
								//System.out.print("the request that has been accomplished: ");
								this.printInfo(harmony, "stop");
								tempFlag = 1;
							}
							//System.out.print("(" + harmony.get_Type()+", " + harmony.get_Floor() + ", " + harmony.get_EleNum() + ", " + harmony.get_Direction() + ")");
							this.printInfo(harmony, "requests");
							sameFloorFlag++;
							requestQueue.get(j).used = true;
						}
						if(this.curFloor == this.tarFloor/*harmony.get_Floor() == this.tarFloor*/){
							mainConfirmed = false;
							this.MainAccomplished = true;
							this.movDir = "STAY";
						}
					}
					if (tempFlag > 0)System.out.println();
					if (tempFlag > 0)moveDoor();
				}//stay
			}
			this.removeUsedRequest(requestQueue);
			//eliminate the used requests, the function of this method is to ease the laden.
		}
	}//all the movement that relating to the elevator
}
class Dispatcher2 extends Dispatcher1{//type is the selection signal, 1 means using the curFloor, 2 means using the target floor; 
	public int MvmAmtCal(RequestV r, Elevators e, int type){
		if (type == 1){
			return Math.abs(e.getCurFloor() - r.get_Floor());
		}
		else if (type == 2){
			return Math.abs(e.getTarFloor() - r.get_Floor());
		}
		return 0;
	}
	public boolean canBeTaken(RequestV r, Elevators e){
		if (r.get_Type().equals("ER")){
			if(e.getMovDir().equals("UP") && 
			   r.get_Floor() > e.getCurFloor() ) return true;
			else if(e.getMovDir().equals("DOWN") && 
					r.get_Floor() < e.getCurFloor() ) return true;
			else return false;
		}
		else if (r.get_Type().equals("FR")){
			if(r.get_Direction().equals("UP") && 
			   r.get_Direction().equals(e.getMovDir()) && 
			   r.get_Floor() > e.getCurFloor() ) return true;
			if(r.get_Direction().equals("DOWN") && 
			   r.get_Direction().equals(e.getMovDir()) && 
			   r.get_Floor() < e.getCurFloor() ) return true;
			return false;
		}
		return false;
	}
	public void initArray(int []j, int num){
		for (int i = 0; i < num; i++){
			j[i] = 0;
		}
	}
	public void startDispatcher(){
		ArrayList  <RequestV>allCurrentRequests;
		int i = 0, j = 0;
		RequestImitator RI = new RequestImitator();
		//RI.start();//ready to get input
		
		ExecutorService exec1 = Executors.newCachedThreadPool(new HandlerThreadFactory());
		exec1.execute(RI);
		
		Elevators []elevators = new Elevators[3];// three elevators
		ExecutorService exec2 = Executors.newCachedThreadPool(new HandlerThreadFactory());
		for(i = 0; i < 3; i++){
			elevators[i] = new Elevators(i + 1);
			exec2.execute(elevators[i]);
		}
		
		while (true){
			if (RI.hasInput){
				allCurrentRequests = (ArrayList) RI.getTray().getArrayRequests().clone();
				RI.tray.requests.clear();
				RI.hasInput = false;
				//
				RequestV []requests = new RequestV[5];
				int num = allCurrentRequests.size();
				for (i = 0; i < num; i++){
					requests[i] = allCurrentRequests.get(i);
				}//newly input requests
				//ArrayDeque <RequestV> untakeableReq = new ArrayDeque();// untakeable requests
				ArrayList <RequestV> untakeableReq = new ArrayList();
				for (i = 0; i < num; i++){
					if (requests[i].get_Type().equals("ER")){
						if (this.canBeTaken(requests[i], elevators[requests[i].get_EleNum() - 1])){
							elevators[requests[i].get_EleNum() - 1].addRequest(requests[i]);
						}
						else {
							untakeableReq.add(requests[i]);
						}
					}//judge the same elevator is enough
					else if (requests[i].get_Type().equals("FR")){
						int []takeFlag = new int[3];
						int []moveAmount = new int[3];
						int index = -1;
						int minMovAmo = 30;//larger than the biggest movement amount;
						for (j = 0; j < 3; j++){
							if (this.canBeTaken(requests[i], elevators[j])){
								takeFlag[j] = 1;
								moveAmount[j] = this.MvmAmtCal(requests[i], elevators[j], 1);
							}
						}//all the three
						for (j = 0; j < 3; j++){
							if (takeFlag[j] == 1){
								if (moveAmount[j] < minMovAmo) {
									index = j;
									minMovAmo = moveAmount[j];
								}
							}
						}
						if (index >= 0) elevators[index].addRequest(requests[i]);
						else untakeableReq.add(requests[i]);
					}
				}
				for (i = 0;i < untakeableReq.size(); i++){
					RequestV req = untakeableReq.get(i);
					//System.out.println("(" + req.get_Type()+", " + req.get_Floor() + ", " + req.get_EleNum() + ", " + req.get_Direction() + ")");//////////////////////////////////////////////////
					int []takeFlag = new int[3];
					int []moveAmount = new int[3];
					int index = -1;
					int minMovAmo = 30;
					//
					if (req.get_Type().equals("FR")){
						for (j = 0; j < 3; j++){
							if (/*!elevators[j].mainConfirmed*/elevators[j].empRequest){
								takeFlag[j] = 1;
								moveAmount[j] = this.MvmAmtCal(req, elevators[j], 1);
							}
						}
						for (j = 0; j < 3; j++){
							if (takeFlag[j] == 1){
								if (moveAmount[j] < minMovAmo){
									index = j;
									minMovAmo = moveAmount[j];
								}
							}
						}
						if (index >= 0) {
							elevators[index].addRequest(req);
							//System.out.println(elevators[index].mainConfirmed);//////////////////////////////////
							untakeableReq.remove(i);
							i--;
						}
					}
					else{
						if (!elevators[req.get_EleNum() - 1].mainConfirmed){
							elevators[req.get_EleNum() - 1].addRequest(req);
							untakeableReq.remove(i);
							i--;
						}
					}
					//
				}
			}
		}
	}
}
//
public class XElevatorsDispatcherMain {
	public static void main(String [] args){
		try{
			Dispatcher2 requestsDispatching = new Dispatcher2();
			requestsDispatching.startDispatcher();
		}catch(Throwable e){
			System.out.print("you are fucking wrong\n");
		}
	}
}
/*if (requests[i].get_Type().equals("ER")){
	elevators[requests[i].get_EleNum() - 1].addRequest(requests[i]);
}
else {
	
}*/
/*public synchronized void goUpstairs(int num){
try {
	sleep( num * 300);
} catch (InterruptedException e) {
	e.printStackTrace();
}//sleep for 3s
this.curFloor++;
this.movAmo++;
//this.time += 3;
}
public synchronized void goDownstairs(int num){
try {
	sleep(num * 300);
} catch (InterruptedException e) {
	e.printStackTrace();
}//sleep for 3s
this.curFloor--;
this.movAmo++;
//this.time += 3;
}*/
/*
for (i = 0; i < lines.length && i < 5; i++ ){
	Matcher neg_1m = neg_1.matcher(lines[i]);
	Matcher neg_2m = neg_2.matcher(lines[i]);
	Matcher neg_3m = neg_3.matcher(lines[i]);
	if (neg_1m.find()){
		if (wrongRequest(lines[i])){
			lines[i] = "";
			continue;
		}
		//firstFlag = 1;// delete the first ER
		int []temp1 = new int[2];
		j = 0;
			while (neg_3m.find()){
				temp1[j] = Integer.parseInt(neg_3m.group());
				j++;
			}
			//if (temp1[1] > time) time = temp1[1];
			//else return "";// not fit the time logic, invalid input
	}//FR
	else if (neg_2m.find()){
		if (wrongRequest(lines[i]) || firstFlag == 0) {
			lines[i] = "";//delete the wrong request
			continue;
		}
		int []temp2 = new int[2];
		j = 0;
			while (neg_3m.find()){
				temp2[j] = Integer.parseInt(neg_3m.group());
				j++;
			}
			//if (temp2[1] > time) time = temp2[1];
			//else return "";// not fit the time logic, invalid input
	}//ER
	else return "";//return "" means invalid input
}*///that codes are used to validate: whether the time is going-up, whether the first request is ER
//it is us