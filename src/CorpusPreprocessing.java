import java.io.*;
import java.lang.reflect.Array;
import java.util.regex.*;

import java.util.*;


public class CorpusPreprocessing {
	private String srcText;
	public ArrayList<Document> docs;
	private String rsrcText;
	public ArrayList<Document> rdocs;
	public int docAmount = 0;
	public int rdocAmount = 0;
	public CorpusPreprocessing(){
		this.docAmount = 0;
		this.docs = new ArrayList<Document>();
		this.rdocAmount = 0;
		this.rdocs = new ArrayList<Document>();
	}
	
	public String inputFromHTMLFile(String fileName) throws IOException{
		String source = "";
		FileInputStream fin  = new FileInputStream(fileName);
		String currentLine;
		BufferedReader reader = new BufferedReader(new InputStreamReader(fin));
        currentLine = reader.readLine(); 
        while(currentLine !=null){
        	source = source + "\n" + currentLine;
        	currentLine = reader.readLine();
        }
        reader.close();
        fin.close();
        return source;
	}
	
	public static String removeHtmlTags(String srcFile) {
        String scriptRegex="<script[^>]*?>[\\s\\S]*?<\\/script>";
        String styleRegex="<style[^>]*?>[\\s\\S]*?<\\/style>";
        String tagRegex="<[^>]+>";

        srcFile = srcFile.replaceAll(scriptRegex, "");
        srcFile = srcFile.replaceAll(styleRegex, "");
        srcFile = srcFile.replaceAll(tagRegex, " ");
        srcFile = srcFile.replaceAll("&nbsp;", "");
        return srcFile.trim();
    }
	
	List<String> getMatchers(String regex, String source){
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(source);
        List<String> list = new ArrayList<>();
        while (matcher.find()) {
            list.add(matcher.group().replaceAll("@", "").replaceAll("\n", "!").replaceAll("\\(. units\\)", ""));
            //replace \n with ! to make things easier
        }
        return list;
    }
	
	
	public ArrayList<Document> work(String fileName){
		try{
			this.srcText = this.inputFromHTMLFile(fileName);
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		this.srcText = removeHtmlTags(this.srcText);
		this.srcText = this.srcText.replaceAll("\n *CSI ", "@@CSI ");
		//System.out.println(this.srcText);
		List<String> a = getMatchers("@CSI [1-9][0-4][0-9][0-9]([^@]*$*)*@", this.srcText);
		for (String string : a) {
			String str[] = string.split("!");
			String title = str[0].trim().replaceAll("([A-Z]{3}) ([0-9]{4})", "$1$2").replaceAll("[\\(\\)]", "").replaceAll("[\\:\\.\\,\\;] ", " ").replaceAll(" +", " ");
			String desc = str[str.length-1].trim().replaceAll("([A-Z]{3}) ([0-9]{4})", "$1$2").replaceAll("[\\(\\)]", "").replaceAll("[\\:\\.\\,\\;] ", " ").replaceAll(" +", " ");//replace "CSI 4107" with "CSI4107"
			docs.add(new Document(this.docAmount,title,desc));
			this.docAmount++;
		}
		
		return this.docs;
	}
	
	List<String> getReutersMatchers(String regex, String source){
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(source);
        List<String> list = new ArrayList<>();
        while (matcher.find()) {
            list.add(matcher.group());
            //replace \n with ! to make things easier
        }
        return list;
    }
	
	
	public ArrayList<Document> reuters(int fileAmount){
		try{
			//for(int i=0;i<=21;i++){
			for(int i=0;i<fileAmount;i++){
				String fileName = "reuters21578/reut2-0";
				if(i<10) fileName = fileName+"0";
				fileName = fileName+i+".sgm";
				this.rsrcText = "";
				FileInputStream fin  = new FileInputStream(fileName);
				String currentLine;
				BufferedReader reader = new BufferedReader(new InputStreamReader(fin));
				currentLine = reader.readLine(); 
				while(currentLine !=null){
					this.rsrcText = this.rsrcText + "\n" + currentLine;
					List<String> a = getReutersMatchers("<REUTERS[.\\s\\S]*?</REUTERS>", this.rsrcText);
					if(a.size()>0){
						for(String string :a){	
							List<String> tempList = getReutersMatchers("<TITLE>[.\\s\\S]*?</TITLE>", string);
							String title = tempList.size()>0?
									(tempList.get(0).replaceAll("<[^>]+>", "").replaceAll("&nbsp;", "").replaceAll("&(.{0,4}?);", "")):"";
							tempList = getReutersMatchers("<BODY>[.\\s\\S]*?</BODY>", string);
							String desc = tempList.size()>0?
									(tempList.get(0).replaceAll("<[^>]+>", "").replaceAll("&nbsp;", "").replaceAll("&(.{0,4}?);", "")):"";
							tempList = getReutersMatchers("<TOPICS>[.\\s\\S]*?</TOPICS>", string);
							String[] topics = null;
							String ttemp;
							if(tempList.size()>0){
								topics = tempList.get(0).replaceAll("&nbsp;", "").replaceAll("&(.{0,4}?);", "")
										.replaceAll("</D><D>", " ").replaceAll("<[^>]+>", "").split(" ");
								
							}
							rdocs.add(new Document(this.rdocAmount,title,desc,topics));
							this.rdocAmount++;
							this.rsrcText = "";
						}
					}
					currentLine = reader.readLine();
				}
				reader.close();
				fin.close();
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("docs:"+this.rdocAmount);
		return this.rdocs;
	}
}
