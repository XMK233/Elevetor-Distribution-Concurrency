import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// only tackle with the time line
public class RequestQueue{
	private Request []queue = new Request[100];
	private int Num = 0;
	//
	public Request getRequest(int i){
		return this.queue[i];
	}
	public Request[] getAllRequest(){
		return this.queue;
	}
	public int getNum(){
		return this.Num;
	}
	//getting the properties
	public void cutAndSort(String string){
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
				this.queue[i] = new Request(1, temp1[0], temp1[1]);
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
				this.queue[i] = new Request(1, temp2[0], direction, temp2[1]);
 			}//find the FR
		}//
		this.Num = lines.length;
	}//we consider that this string is valid 
	//
	public RequestQueue(String string){
		this.cutAndSort(/*this.validateTheString(string)*/ string);
	}//construction: using cut() to fulfill the queue[];
		
}
/*
		Pattern neg_4 = Pattern.compile("\\.");
		Pattern neg_5 = Pattern.compile("");/
		
*/
/*
		//int [][]origin = new int[99][4];//99 request, each request has 4 elements for most
*/
		 