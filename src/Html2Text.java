
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;

import javax.swing.text.html.HTMLEditorKit;  

/**
 * @author pratapgurung
 *	
 */
public class Html2Text extends HTMLEditorKit.ParserCallback {

	private static  ArrayList<String> STOP_LIST = new ArrayList<String>();
	//to store stoplist 
	private static HashMap<Integer, String> stopListMap = new HashMap<>(); 
	private static final String StopDoc = "stoplist.txt";
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		// pass the path to the file as a parameter 

		getStopList(StopDoc);

		// 1. using Iterator 
		Iterator<String> itr = stopListMap.values().iterator(); 
		//while(itr.hasNext()) { System.out.println(itr.next()); }


		StringBuilder contentBuilder = new StringBuilder();
		FileWriter writer = new FileWriter("MyFile.txt", false);
		try {
			BufferedReader in = new BufferedReader(new FileReader("corpus/An introduction to information retrieval_ applications in genomics.html"));
			String str; 
			while ((str = in.readLine()) != null) {
				contentBuilder.append(str+ " ");

			}
			writer.write(contentBuilder.toString());
			String str2 = cleanUpString(contentBuilder.toString());
			//System.out.println(str2);
			in.close();

		} catch (IOException e) {
		}
		writer.close();
		//System.out.println( contentBuilder.toString());
		//String content = contentBuilder.toString();
		//System.out.println(content);

	}
	private static void getStopList(String doc) {
		File file = new File(doc);


		Scanner sc;
		try {
			sc = new Scanner(file);
			int i = 1;
			while (sc.hasNextLine()) {
				stopListMap.put(i, sc.nextLine().toUpperCase());
				//STOP_LIST.add(sc.nextLine());
				//System.out.println(sc.nextLine()); 
				i++;
			}
			//System.out.println(STOP_LIST.size());

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 




	}
	
	private static String cleanUpString(String str) throws IOException {
		// TODO Auto-generated method stub

		
		//final touch of regex to clean it up the string before adding to content builder
		str = str.strip();
		str = str.replaceAll("<script\\b[^<]*(?:(?!<\\/script>)<[^<]*)*<\\/script>", "");
		str = str.replaceAll("<style\\b[^<]*(?:(?!<\\/style>)<[^<]*)*<\\/style>", "");
		str = str.replaceAll("<[^>]*>|<[^>]*", " "); 
		str = str.replaceAll("&[a-z]*\\d*;|&#\\d*;|&[a-z]*\\d*", " "); 
		
		str = str.replaceAll("[^a-zA-Z\\s+]", " ");//removing special characters
		
		//str = str.replaceAll("[\\d\\t]*", "");//removing whitespaces and number
		//str = str.replaceAll("src|href|style|alt", "");
		

		String strArray[] = str.split(" ");
		
		HashMap<Integer, String> termMap = new HashMap<>();
		//put elements of String array in term hashmaps
		for(int i=0; i < strArray.length; i++){
			if(!strArray[i].isBlank()) {
				if(!stopListMap.containsValue(strArray[i].toUpperCase()) && !termMap.containsValue(strArray[i])) {
					if(!strArray[i].contains("http") && strArray[i].length() > 1) {
						//termMap.put(i, strArray[i]);
						System.out.println(strArray[i].strip()); 
					}
					
				}
			}
				

		}
		

		return str;
	}

}