
public class Harmony {
	public int time;
	public int floor;
	public String type;
	public String direction;
	public Harmony(Request allRequest){
		boolean isER = allRequest.getCategory().equals("ER");
		if (isER){
			this.floor = allRequest.getElevator().getTarFloor();
			this.time = allRequest.getElevator().getReqTime();
			this.direction = "-";
			this.type = "ER";
		}
		else if(allRequest.getCategory().equals("FR")){
			this.floor = allRequest.getFloor().getCurFloor();
			this.time = allRequest.getFloor().getReqTime();
			this.direction = allRequest.getFloor().getUpOrDown();
			this.type = "FR";
		}		
	}
}
