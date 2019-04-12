import java.io.*;
import java.util.*;
import java.util.logging.Logger;

import org.tartarus.snowball.SnowballStemmer;
import org.tartarus.snowball.ext.*;

public class Dictionary {
	public HashMap<String,HashSet<Integer>> dict;
	public HashSet<String> stopwords;
	public ArrayList<Double> weight;
	public String dictString;
	public HashMap<String, ArrayList<Integer>> tf;
	public HashMap<String, ArrayList<Double>> tfidf;
	public boolean stemming,stopword,normalization;
	
	public Dictionary(){
		dict = new HashMap<String,HashSet<Integer>>();
		stopwords = new HashSet<String>();
		dictString = "";
	}
	
	public void inputFromStopwordsFile(String fileName) throws IOException{
		FileInputStream fin  = new FileInputStream(fileName);
		String currentLine;
		BufferedReader reader = new BufferedReader(new InputStreamReader(fin));
        currentLine = reader.readLine(); 
        while(currentLine !=null){
        	stopwords.add(currentLine);
        	currentLine = reader.readLine();
        }
        reader.close();
        fin.close();
	}
	
	public void build(ArrayList<Document> docs, boolean stopword, boolean stemming, boolean normalization){
		this.stopword = stopword;
		this.stemming = stemming;
		this.normalization = normalization;
		this.weight = new ArrayList<Double>();
		
		dict = new HashMap<String,HashSet<Integer>>();
		dictString = "";
		
		tf = new HashMap<String, ArrayList<Integer>>();
		SnowballStemmer stemmer = new englishStemmer();
		for (int i = 0; i < docs.size(); i++) {
			Document doc = docs.get(i);
			String[] wordlist = doc.title.split("[ \\/]");
			for (String str : wordlist) {
				str = str.toLowerCase();
				if(stopword && stopwords.contains(str)) continue;
				if(normalization){
					str = str.replaceAll("[\\-\\.]", "");
				}
				if(stemming){
					stemmer.setCurrent(str);
					if(stemmer.stem()){
						str = stemmer.getCurrent();
						//System.out.println(str);
					}
				}
				if(dict.containsKey(str)){
					HashSet<Integer> index = dict.get(str);
					index.add(i);
					dict.replace(str, index);
					
				}
				else{
					HashSet<Integer> index = new HashSet<Integer>();
					index.add(i);
					dict.put(str, index);
					dictString = dictString+" " +str;
					
				}
				//System.out.println(str);
			}
			
			wordlist = doc.description.split("[ \\/]");
			for (String str : wordlist) {
				str = str.toLowerCase();
				//System.out.println(str);
				if(stopword && stopwords.contains(str)) continue;
				if(normalization){
					str = str.replaceAll("[\\-\\.]", "");
				}
				if(stemming){
					stemmer.setCurrent(str);
					if(stemmer.stem()){
						str = stemmer.getCurrent();
						//System.out.println(str);
					}
				}
				if(dict.containsKey(str)){
					HashSet<Integer> index = dict.get(str);
					index.add(i);
					dict.replace(str, index);
					
				}
				else{
					HashSet<Integer> index = new HashSet<Integer>();
					index.add(i);
					dict.put(str, index);
					dictString = dictString+" " +str;
					
				}
				//System.out.println(str);
			}
	
		}
		
		for (int i = 0; i < docs.size(); i++) {
			Document doc = docs.get(i);
			String[] wordlist = doc.title.split("[ \\/]");
			for (String str : wordlist) {
				str = str.toLowerCase();
				if(stopword && stopwords.contains(str)) continue;
				if(normalization){
					str = str.replaceAll("[\\-\\.]", "");
				}
				if(stemming){
					stemmer.setCurrent(str);
					if(stemmer.stem()){
						str = stemmer.getCurrent();
						//System.out.println(str);
					}
				}
				if(tf.containsKey(str)){
					ArrayList<Integer> tempArr = tf.get(str);
					if(tempArr.size()<i+1)
						for(int j=tempArr.size();j<i+1;j++)
							tempArr.add(j,Integer.valueOf(0));

						tempArr.set(i,tempArr.get(i)+1);

					tf.put(str, tempArr);
				}
				else{				
					ArrayList<Integer> tempArr = new ArrayList<Integer>();
					//System.out.println(docs.size());
					if(tempArr.size()<i+1)
						for(int j=tempArr.size();j<i+1;j++)
							tempArr.add(j,Integer.valueOf(0));
					tempArr.set(i, Integer.valueOf(1));
					//System.out.println(tf.get(str));
					tf.put(str, tempArr);
				}
				//System.out.println(str);
			}
			
			wordlist = doc.description.split("[ \\/]");
			for (String str : wordlist) {
				str = str.toLowerCase();
				//System.out.println(str);
				if(stopword && stopwords.contains(str)) continue;
				if(normalization){
					str = str.replaceAll("[\\-\\.]", "");
				}
				if(stemming){
					stemmer.setCurrent(str);
					if(stemmer.stem()){
						str = stemmer.getCurrent();
						//System.out.println(str);
					}
				}
				if(tf.containsKey(str)){
					ArrayList<Integer> tempArr = tf.get(str);
					if(tempArr.size()<i+1)
						for(int j=tempArr.size();j<i+1;j++)
							tempArr.add(j,Integer.valueOf(0));
					tempArr.set(i, tempArr.get(i)+1);
					tf.put(str, tempArr);
				}
				else{
					ArrayList<Integer> tempArr = new ArrayList<Integer>();
					//System.out.println(docs.size());
					if(tempArr.size()<i+1)
						for(int j=tempArr.size();j<i+1;j++)
							tempArr.add(j,Integer.valueOf(0));
					tempArr.set(i, Integer.valueOf(1));
					//System.out.println(tf.get(str));
					tf.put(str, tempArr);
				}
				//System.out.println(str);
			}
	
		}
		tfidf = new HashMap<String, ArrayList<Double>>();
		String wordlist[] = dictString.split(" ");
		for(String str: wordlist){
			ArrayList<Double> tempArr = new ArrayList<Double>();
			for(int i=0;i<docs.size();i++){
				if(dict.get(str)!=null && dict.get(str).contains(i)){
					try{
						tempArr.add( Math.log( tf.get(str).get(i)+1)*Math.log(docs.size()/dict.get(str).size()));
					}catch (Exception e){//This is for debug use. There will not be Exception normally.
						System.out.println(i);
						System.out.println(tf.get(str));
						dict.get(str);
					}
				}
				else{
					tempArr.add(Double.valueOf(0));
				}
			}
			tfidf.put(str, tempArr);
		}
		
		return;
	}
	
}
