import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class main{
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
		if (temp2[0] >10 || temp2[0] < 1) return true; 
		if( neg_4m.find()) {
			if (temp2[0] == 10 && neg_4m.group().equals("UP")) return true;//10th floor and up
			if (temp2[0] == 1 && neg_4m.group().equals("DOWN")) return true;//first floor and down	
		}
		//the match of floor and direction
		return false;
		
	}
	public static String validateTheString (String string){
		String validated = "";//·µ»ØµÄ×Ö´®
		String []lines = string.split("\\.");//·ÖÁÑ×Ö·û´®
		int time =  -1;//Ê±¼äÐòÁÐ´æ´¢Æ÷
		int firstFlag = 0;
		Pattern neg_1 = Pattern.compile("(\\(FR,\\d{1,2},(UP|DOWN),\\d{1,6}\\))");//FR format 
		Pattern neg_2 = Pattern.compile("(\\(ER,\\d{1,2},\\d{1,6}\\))");//ER format
		Pattern neg_3 = Pattern.compile("[0-9\\.]+");
		if (string.length() > 3000) 
			return "";
		//
		String newsc = string;
	  	//Pattern neg_n = Pattern.compile("^(((\\(FR,\\d{1,2},(UP|DOWN),\\d{1,6}\\))|(\\(ER,\\d{1,2},\\d{1,6}\\))).)*((\\(FR,\\d{1,2},(UP|DOWN),\\d{1,6}\\))|(\\(ER,\\d{1,2},\\d{1,6}\\)))$");
		Pattern neg_n = Pattern.compile("^(((\\(FR,\\d{1,2},(UP|DOWN),\\d{1,6}\\))|(\\(ER,\\d{1,2},\\d{1,6}\\)))\\.)*((\\(FR,\\d{1,2},(UP|DOWN),\\d{1,6}\\))|(\\(ER,\\d{1,2},\\d{1,6}\\)))$");
		Matcher neg_nx = neg_n.matcher(newsc);
		//boolean match = neg_nx.find();
		if (!neg_nx.find()) 
			return "";
		//
		int i = 0;
		int j = 0;
		for (i = 0; i < lines.length && i < 99; i++ ){
			Matcher neg_1m = neg_1.matcher(lines[i]);
			Matcher neg_2m = neg_2.matcher(lines[i]);
			Matcher neg_3m = neg_3.matcher(lines[i]);
			if (neg_1m.find()){
				if (wrongRequest(lines[i])){
					lines[i] = "";
					continue;
				}
				firstFlag = 1;// delete the first ER
				int []temp1 = new int[2];
				j = 0;
	 			while (neg_3m.find()){
	 				temp1[j] = Integer.parseInt(neg_3m.group());
	 				j++;
	 			}
	 			if (temp1[1] > time) time = temp1[1];
	 			else return "";// not fit the time logic, invalid input
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
	 			if (temp2[1] > time) time = temp2[1];
	 			else return "";// not fit the time logic, invalid input
			}//ER
			else return "";//return "" means invalid input
		}
		//Matcher neg_1m = neg_1.matcher(string);
		//Matcher neg_2m = neg_2.matcher(string); 
		for (i = 0; i < lines.length && i < 99; i++ ){
			if (lines[i] != "")
				validated += (lines[i] + ".");
		}
		if (validated != "" && validated.charAt(validated.length() - 1) == '.') return validated.substring(0, validated.length() - 1);
		return validated;
	}//validate the string;
	//
	public static void main(String []args){
		try{
			System.out.println("please enter: ");    
			String str=new Scanner(System.in).nextLine();
			String newsc = str.replaceAll("\\s+", "");
			String newsc1 = validateTheString(newsc); 
			Dispatcher dispatcher = new Dispatcher();
			Dispatcher1 dispatcher1 = new Dispatcher1();
			if (newsc1 != "")  
				dispatcher1.ALS_Schedule((newsc1));
			else System.out.println("invalid input, request sequence has been abandoned");
			System.out.println("elevator stop");
		}catch (Throwable e){
			System.out.println("invalid request");
		}
	}
}
