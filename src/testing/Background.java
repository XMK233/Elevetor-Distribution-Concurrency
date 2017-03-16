package testing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Background {
	public static void main(String[] argv) throws IOException {
		try{
			Scanner in = new Scanner(System.in);
			RequestQueue rq = new RequestQueue();
			String str;
			while(!(str = in.nextLine()).equals("")&&!str.equals("run")){
				str = str.replaceAll("\\s+","");
				if(!str.matches("(\\(FR\\,[2-9]\\,(UP|DOWN)\\,\\d{0,10}\\))|(\\(FR\\,1\\,UP\\,\\d{0,10}\\))|(\\(FR\\,10\\,DOWN\\,\\d{0,10}\\))|(\\(ER\\,([1-9]|10)\\,\\d{0,10}\\))")){
					System.out.println("已过滤一条无效指令");
					continue;
				}
				rq.addRequest(str);
			}
			if(rq.requestTeam.isEmpty()){
				in.close();
				throw new Exception("empty");
			}
			if(!rq.effectivenessTest()){
				in.close();
				throw new Exception("time");
			}
			ALS_Scheduler sd = new ALS_Scheduler();
			Elevator_A eva = new Elevator_A();
			sd.moveElevator(rq, eva);
			System.out.println("done");
			in.close();
		}
		catch (Exception e){
			if(e.getMessage().equals("empty")){
				System.out.println("未收到有效指令");
			}
			else if(e.getMessage().equals("time")){
				System.out.println("首指令时间不为零或时间排序错误");
			}
			else{
				System.out.println("未知错误 ");
			}
		}
	}
}

class Floor {
	int floor;
	Floor(int floor){
		this.floor = floor;
	}
}

interface Elevator {
	int getFloor();
	double getTime();
	String getDirection();
	void renewSituation(Request req);
	void renewSituation_cr(Request req);
	int calculateFloor(Request pr,Request request);
	void setDirection(Request pr);
}

class Elevator_A implements Elevator {
	Floor realtimeFloor;
	double time;
	String direction;
	Elevator_A(){
		realtimeFloor = new Floor(1);
		time = 0;
		direction = "STAY";
	}
	public int getFloor(){
		return realtimeFloor.floor;
	}
	public double getTime(){
		return time;
	}
	public String getDirection(){
		return direction;
	}
	public void renewSituation(Request req){
		time = 1+Math.abs(realtimeFloor.floor-req.floor)*0.5+Math.max(req.time,time);
		if(req.floor>realtimeFloor.floor){
			direction = "UP";
		}
		else if(req.floor==realtimeFloor.floor){
			direction = "STAY";
		}
		else {
			direction = "DOWN";
		}
		realtimeFloor.floor = req.floor;
	}
	public void renewSituation_cr(Request req){
		time = 1+Math.abs(realtimeFloor.floor-req.floor)*0.5+time;
		if(req.floor>realtimeFloor.floor){
			direction = "UP";
		}
		else if(req.floor==realtimeFloor.floor){
			direction = "STAY";
		}
		else {
			direction = "DOWN";
		}
		realtimeFloor.floor = req.floor;
	}
	//估算在没有其它指令干扰下，某指令发生时电梯所属楼层
	public int calculateFloor(Request pr,Request req){
		if(req.time<=this.time){
			return getFloor();
		}
		if(direction.equals("UP")){
			return (int) Math.min(pr.floor,getFloor()+(req.time-this.time)/0.5);
		}
		if(direction.equals("DOWN")){
			return (int) Math.max(pr.floor,getFloor()-(req.time-this.time)/0.5);
		}
		return getFloor();
	}
	public void setDirection(Request pr){
		if(pr.floor>realtimeFloor.floor){
			direction = "UP";
		}
		if(pr.floor<realtimeFloor.floor){
			direction = "DOWN";
		}
		if(pr.floor==realtimeFloor.floor){
			direction = "STAY";
		}
	}
	public String toString(){
		return "("+realtimeFloor.floor+","+direction+","+time+")";
	}
}

class Fool_Scheduler {
	void moveElevator(Request req,Elevator elevator){
		if(req.floor>elevator.getFloor()){
			elevator.renewSituation(req);
			System.out.println(elevator);
		}
		else if(req.floor<elevator.getFloor()){
			elevator.renewSituation(req);
			System.out.println(elevator);
		}
		else{
			elevator.renewSituation(req);
		}
	}
}

class ALS_Scheduler extends Fool_Scheduler{
	//当前主请求
	Request pr; 
	//主请求预期完成时间
	double expectedArrivingTime;
	//捎带请求队列
	RequestQueue crRq;
	//非捎带请求队列
	RequestQueue ncrRq;
	ALS_Scheduler(){
		crRq = new RequestQueue();
		ncrRq = new RequestQueue();
	}
	void addTocrRq(Request req,String direction){
		if(crRq.requestTeam.size()==0){
			crRq.requestTeam.add(req);
		}
		if(direction.equals("UP")){
			for(int i = 0;i<crRq.requestTeam.size();i++){
				if(crRq.requestTeam.get(i).floor==req.floor){
					break;
				}
				if(crRq.requestTeam.get(i).floor>req.floor){
					crRq.requestTeam.add(i,req);
					break;
				}
				if(i==crRq.requestTeam.size()-1){
					crRq.requestTeam.add(req);
				}
			}
		}
		else if(direction.equals("DOWN")){
			for(int i = 0;i<crRq.requestTeam.size();i++){
				if(crRq.requestTeam.get(i).floor==req.floor){
					break;
				}
				if(crRq.requestTeam.get(i).floor<=req.floor){
					crRq.requestTeam.add(i,req);
					break;
				}
				if(i==crRq.requestTeam.size()-1){
					crRq.requestTeam.add(req);
				}
			}
		}
	}
	void moveElevator(RequestQueue rq,Elevator ev){
		pr = rq.requestTeam.get(0);
		rq.requestTeam.remove(0);
		expectedArrivingTime = Math.abs(ev.getFloor()-pr.floor)*0.5+Math.max(pr.time,ev.getTime());
		ev.setDirection(pr);
		while(rq.requestTeam.size()>0||ncrRq.requestTeam.size()>0||crRq.requestTeam.size()>0){
			if(rq.requestTeam.size()==0||rq.requestTeam.get(0).time>=expectedArrivingTime){
				if(crRq.requestTeam.size()>0){
					if(!crRq.requestTeam.get(0).direction.equals("ex")){
						System.out.println(pr+"("+crRq.requestTeam.get(0)+")");
					}
					if(crRq.requestTeam.get(0).floor==pr.floor){
						crRq.requestTeam.remove(0);
						continue;
					}else if(crRq.requestTeam.get(0).floor<pr.floor&&pr.direction.equals("UP")||crRq.requestTeam.get(0).floor>pr.floor&&pr.direction.equals("DOWN")){
						ev.renewSituation_cr(crRq.requestTeam.get(0));
						System.out.println(ev);
						expectedArrivingTime++;
						crRq.requestTeam.remove(0);
						rq.requestTeam.addAll(0,ncrRq.requestTeam);
						ncrRq.requestTeam.removeAll(ncrRq.requestTeam);
						continue;
					}else{
						//将不再重要的direction属性标记为ex，表明该条cr属于前一条pr
						for(int i = 1;i<crRq.requestTeam.size();i++){
							System.out.println(pr+"("+crRq.requestTeam.get(i)+")");
							crRq.requestTeam.get(i).direction = "ex";
						}
						ev.renewSituation(pr);
						System.out.println(ev);
						pr = crRq.requestTeam.get(0);
						crRq.requestTeam.remove(0);
						expectedArrivingTime = Math.abs(ev.getFloor()-pr.floor)*0.5+Math.max(pr.time,ev.getTime());
						rq.requestTeam.addAll(0,ncrRq.requestTeam);
						ncrRq.requestTeam.removeAll(ncrRq.requestTeam);
						continue;
					}
				}else{
					ev.renewSituation(pr);
					System.out.println(ev);
					rq.requestTeam.addAll(0,ncrRq.requestTeam);
					ncrRq.requestTeam.removeAll(ncrRq.requestTeam);
					pr = rq.requestTeam.get(0);
					rq.requestTeam.remove(0);
					expectedArrivingTime = Math.abs(ev.getFloor()-pr.floor)*0.5+Math.max(pr.time,ev.getTime());
					ev.setDirection(pr);
					continue;
				}
			}
			if(rq.requestTeam.get(0).ID.equals("FR")&&!rq.requestTeam.get(0).direction.equals(ev.getDirection())){
				ncrRq.requestTeam.add(rq.requestTeam.get(0));
				rq.requestTeam.remove(0);
				continue;
			}
			if(ev.getDirection()=="UP"){
				if(rq.requestTeam.get(0).ID.equals("FR")&&rq.requestTeam.get(0).floor>pr.floor){
					ncrRq.requestTeam.add(rq.requestTeam.get(0));
					rq.requestTeam.remove(0);
				}else if(ev.calculateFloor(pr,rq.requestTeam.get(0))<rq.requestTeam.get(0).floor){
					addTocrRq(rq.requestTeam.get(0),ev.getDirection());
					rq.requestTeam.remove(0);
				}else if(ev.getTime()-1<=rq.requestTeam.get(0).time&&ev.getTime()>=rq.requestTeam.get(0).time&&ev.calculateFloor(pr,rq.requestTeam.get(0))==rq.requestTeam.get(0).floor){
					addTocrRq(rq.requestTeam.get(0),ev.getDirection());
					rq.requestTeam.remove(0);
				}else if(ev.calculateFloor(pr,rq.requestTeam.get(0))>=rq.requestTeam.get(0).floor){
					ncrRq.requestTeam.add(rq.requestTeam.get(0));
					rq.requestTeam.remove(0);
				}	
			}
			if(ev.getDirection()=="DOWN"){
				if(rq.requestTeam.get(0).ID.equals("FR")&&rq.requestTeam.get(0).floor<pr.floor){
					ncrRq.requestTeam.add(rq.requestTeam.get(0));
					rq.requestTeam.remove(0);
				}else if(ev.calculateFloor(pr,rq.requestTeam.get(0))>rq.requestTeam.get(0).floor){
					addTocrRq(rq.requestTeam.get(0),ev.getDirection());
					rq.requestTeam.remove(0);
				}else if(ev.getTime()-1<=rq.requestTeam.get(0).time&&ev.getTime()>=rq.requestTeam.get(0).time&&ev.calculateFloor(pr,rq.requestTeam.get(0))==rq.requestTeam.get(0).floor){
					addTocrRq(rq.requestTeam.get(0),ev.getDirection());
					rq.requestTeam.remove(0);
				}else if(ev.calculateFloor(pr,rq.requestTeam.get(0))<=rq.requestTeam.get(0).floor){
					ncrRq.requestTeam.add(rq.requestTeam.get(0));
					rq.requestTeam.remove(0);
				}
			}
		}
		ev.renewSituation(pr);
		System.out.println(ev);
	}
}


class RequestQueue {
	ArrayList<Request> requestTeam;
	int bound;
	RequestQueue(){
		requestTeam = new ArrayList<Request>();
		bound = 0;
	}
	void addRequest(String str) {
		 String[] str1 = str.split("[(),]");
		 if(str1[1].equals("FR"))requestTeam.add(new Request(str1[1],Integer.parseInt(str1[2]),str1[3],Integer.parseInt(str1[4])));
		 if(str1[1].equals("ER"))requestTeam.add(new Request(str1[1],Integer.parseInt(str1[2]),Integer.parseInt(str1[3])));
		 bound++;
	}
	boolean effectivenessTest(){
		if(requestTeam.get(0).time!=0){
			return false;
		}
		for(int i = 0;i<bound-1;i++){
			if(requestTeam.get(i).time>requestTeam.get(i+1).time){
				return false;
			}
		}
		return true;
	}
}

class Request {
	String ID;
	int floor;
	String direction;
	int time;
	Request(String ID,int floor,String direction,int time){
		this.ID = ID;
		this.floor = floor;
		this.direction = direction;
		this.time = time;
	}
	Request(String ID,int floor,int time){
		this.ID = ID;
		this.floor = floor;
		this.direction = "NONE";
		this.time = time;
	}
	public String toString(){
		if(ID.equals("FR"))return "("+ID+","+floor+","+direction+","+time+")";
		if(ID.equals("ER"))return "("+ID+","+floor+","+time+")";
		return null;
	}
}