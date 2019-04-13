import java.io.*;
import java.util.*;
import java.util.AbstractMap.SimpleEntry;

import org.tartarus.snowball.SnowballStemmer;
import org.tartarus.snowball.ext.*;
import java.math.BigDecimal;

public class Dictionary {
	public HashMap<String,HashSet<Integer>> dict;
	public HashSet<String> stopwords;
	public HashMap<Integer, HashSet<String>> wordset;
	//public ArrayList<Double> weight;
	public String dictString;
	private HashMap<String, ArrayList<Integer>> tf;
	public HashMap<String, ArrayList<Double>> tfidf;
	private HashMap<String, Integer> bigram;
	public HashMap<String,ArrayList<AbstractMap.SimpleEntry<String,Integer>>> completion;
	public HashMap<String,ArrayList<AbstractMap.SimpleEntry<String,Double>>> expension;
	public boolean stemming,stopword,normalization;
	public boolean hasExpension,hasCompletion;
	private int completionThreshold = 1;//The threshold value of query completion -> suggest only when the bi-gram frequency > threshold
	private int completionListLength = 10;//The max amount of options of completion for each word
	private int expensionListLength = 5;//The max amount of options of completion for each word
	private double expensionThreshold = 0.1;
	private int knn = 5;//the k for K-NN
	
	public Dictionary(){
		dict = new HashMap<String,HashSet<Integer>>();
		stopwords = new HashSet<String>();
		try{
			this.inputFromStopwordsFile("stopwords.txt");
		}catch (Exception e) {
			e.printStackTrace();
		}
		dictString = "";
		hasExpension=false;
		hasCompletion=false;
	}
	
	public void inputFromStopwordsFile(String fileName) throws IOException{
		FileInputStream fin  = new FileInputStream(fileName);
		String currentLine;
		BufferedReader reader = new BufferedReader(new InputStreamReader(fin));
        currentLine = reader.readLine(); 
        while(currentLine !=null){
        	stopwords.add(currentLine.replaceAll("\\n", ""));
        	currentLine = reader.readLine();
        }
        reader.close();
        fin.close();
	}
	
	public void build(ArrayList<Document> docs, boolean stopword, boolean stemming, boolean normalization,boolean reuters,boolean needTopicAssign){
		this.stopword = stopword;
		this.stemming = stemming;
		this.normalization = normalization;
		String norRegex = "[\\-\\.,\\(\\)\"\'\\?\\;]";
		String splitRegex = "[ \\/\\n]";
		//String splitRegex = "[\\s\\S]";
		//this.weight = new ArrayList<Double>();
		
		dict = new HashMap<String,HashSet<Integer>>();
		dictString = "";
		
		tf = new HashMap<String, ArrayList<Integer>>();
		bigram = new HashMap<String,Integer>();
		SnowballStemmer stemmer = new englishStemmer();
		for (int i = 0; i < docs.size(); i++) {
			String lastWord = "";//store the last word for bi-gram
			Document doc = docs.get(i);
			String[] wordlist = doc.title.split(splitRegex);
			for (String str : wordlist) {
				str = str.toLowerCase();
				if(normalization){
					str = str.replaceAll(norRegex, "");
					if (str.equals("")) {lastWord="";continue;}
				}
				if(stopword && stopwords.contains(str)) {lastWord="";continue;}
				if(isNumeric(str)){lastWord= "";continue;}
				
				if(stemming){
					stemmer.setCurrent(str);
					if(stemmer.stem()){
						str = stemmer.getCurrent();
						//System.out.println(str);
					}
				}
				
				buildBiGram(lastWord,str);
				lastWord = str;
				
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
			}
			
			lastWord = "";
			wordlist = doc.description.split(splitRegex);
			for (String str : wordlist) {
				str = str.toLowerCase();
				//System.out.println(str);
				if(normalization){
					str = str.replaceAll(norRegex, "");
					if (str.equals("")) {lastWord="";continue;}
				}
				if(stopword && stopwords.contains(str)) {lastWord="";continue;}
				if(isNumeric(str)){lastWord="";continue;}
				
				if(stemming){
					stemmer.setCurrent(str);
					if(stemmer.stem()){
						str = stemmer.getCurrent();
						//System.out.println(str);
					}
				}
				
				buildBiGram(lastWord,str);
				lastWord = str;
				
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
		
		wordset = new HashMap<Integer, HashSet<String>>();
		
		for (int i = 0; i < docs.size(); i++) {
			Document doc = docs.get(i);
			String[] wordlist = doc.title.split(splitRegex);
			HashSet<String> TempSet = new HashSet<String>();
			for (String str : wordlist) {
				str = str.toLowerCase();
				if(normalization){
					str = str.replaceAll(norRegex, "");
					if (str.equals("")) {continue;}
				}
				if(stopword && stopwords.contains(str)) continue;
				if(stemming){
					stemmer.setCurrent(str);
					if(stemmer.stem()){
						str = stemmer.getCurrent();
						//System.out.println(str);
					}
				}
				if(isNumeric(str))continue;
				TempSet.add(str);
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
			
			wordlist = doc.description.split(splitRegex);
			for (String str : wordlist) {
				str = str.toLowerCase();
				//System.out.println(str);
				if(normalization){
					str = str.replaceAll(norRegex, "");
					if (str.equals("")) {continue;}
				}
				if(stopword && stopwords.contains(str)) continue;
				if(stemming){
					stemmer.setCurrent(str);
					if(stemmer.stem()){
						str = stemmer.getCurrent();
						//System.out.println(str);
					}
				}
				if(isNumeric(str))continue;
				TempSet.add(str);
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
			wordset.put(doc.docID, TempSet);//Store all the words the document contains
	
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
		//System.out.println(dict.size());
		
		hasExpension=false;
		hasCompletion=false;
		if(reuters && needTopicAssign) topicsAssign(docs);
		//buildQueryCompletion();//build up query completion
		//buildQueryExpension();

		tf=null;
		return;
	}
	
	Comparator<AbstractMap.SimpleEntry<String,Integer>> wordIntegerComparator = 
			new Comparator<AbstractMap.SimpleEntry<String,Integer>>() {//Comparator for suggestions, from large frequency to small
		@Override
		public int compare(SimpleEntry<String, Integer> o1, SimpleEntry<String, Integer> o2) {
			if(o1.getValue()<o2.getValue()) return 1;
			if(o1.getValue()>o2.getValue()) return -1;
			return 0;
		}
    };
    
    Comparator<AbstractMap.SimpleEntry<String,Double>> wordDoubleComparator = 
			new Comparator<AbstractMap.SimpleEntry<String,Double>>() {//Comparator for suggestions, from large frequency to small
		@Override
		public int compare(SimpleEntry<String, Double> o1, SimpleEntry<String, Double> o2) {
			if(o1.getValue()<o2.getValue()) return 1;
			if(o1.getValue()>o2.getValue()) return -1;
			return 0;
		}
    };
    
    Comparator<AbstractMap.SimpleEntry<HashSet<String>,Double>> wordSetDoubleComparator = 
			new Comparator<AbstractMap.SimpleEntry<HashSet<String>,Double>>() {
		@Override
		public int compare(SimpleEntry<HashSet<String>, Double> o1, SimpleEntry<HashSet<String>, Double> o2) {
			if(o1.getValue()<o2.getValue()) return 1;
			if(o1.getValue()>o2.getValue()) return -1;
			return 0;
		}
    };
    
    private double docDistance(int x,int y){//return the distance of doc x and doc y
    	HashSet<String> Temp = (HashSet<String>)wordset.get(x).clone();
		Temp.retainAll(wordset.get(y));
		int intersection = Temp.size();
		Temp = (HashSet<String>)wordset.get(x).clone();
		Temp.addAll(wordset.get(y));
		int union=Temp.size();
		return ((double)intersection)/((double)union);
    }
    
    public void topicsAssign(ArrayList<Document> docs){
    	for (int i = 0; i < docs.size(); i++) 
    		if(docs.get(i).topics.size()==0){
    			ArrayList<AbstractMap.SimpleEntry<HashSet<String>,Double>> temp = 
    					new ArrayList<AbstractMap.SimpleEntry<HashSet<String>,Double>>();
    			for (int j = 0; j < docs.size(); j++) 
    				if(i!=j && docs.get(j).topics.size()>0){
    					double similarity = docDistance(i, j);
    					temp.add(new AbstractMap.SimpleEntry<HashSet<String>, Double>(docs.get(j).topics, similarity));
    					Collections.sort(temp,wordSetDoubleComparator);
        				if(temp.size()>knn) temp.remove(temp.size()-1);
    				}
    			HashMap<String, Integer> count = new HashMap<String, Integer>();
    			for(int j=0;j<temp.size();j++){
    				for(String str:temp.get(j).getKey()){
    					if(count.containsKey(str)){
    						count.put(str, count.get(str)+1);
    					}
    					else count.put(str, 1);
    				}
    			}
    			int maxAmount = 0;
    			for(HashMap.Entry<String, Integer> entry: count.entrySet()){
    				if(entry.getValue()>maxAmount){
    					maxAmount = entry.getValue();
    				}
    			}
    			for(HashMap.Entry<String, Integer> entry: count.entrySet()){
    				if(entry.getValue()==maxAmount){
    					docs.get(i).topics.add(entry.getKey());
    				}
    			}
    		}
    	wordset = null;
    }
    
    public void buildQueryExpension(){
    	expension = new HashMap<String,ArrayList<AbstractMap.SimpleEntry<String,Double>>>();
    	for(HashMap.Entry<String,HashSet<Integer>> e1 : dict.entrySet()){
    		for(HashMap.Entry<String,HashSet<Integer>> e2 : dict.entrySet()){
        		if(e1.getKey().compareTo(e2.getKey())>=0) continue;//calculate only when str1<str2 to avoid calculate twice for performance
        		HashSet<Integer> Temp = (HashSet<Integer>)e1.getValue().clone();
        		Temp.retainAll(e2.getValue());
        		int intersection = Temp.size();
        		Temp = (HashSet<Integer>)e1.getValue().clone();
        		Temp.addAll(e2.getValue());
        		int union=Temp.size();
        		Double jaccardDistance = ((double)intersection)/((double)union);
        		if(jaccardDistance>expensionThreshold){
        			if(expension.containsKey(e1.getKey())){
        				ArrayList<AbstractMap.SimpleEntry<String,Double>> tempArr = expension.get(e1.getKey());
        				tempArr.add(new AbstractMap.SimpleEntry<String, Double>(e2.getKey(), jaccardDistance));
        				Collections.sort(tempArr,wordDoubleComparator);
        				if(tempArr.size()>expensionListLength) tempArr.remove(tempArr.size()-1);
        				expension.put(e1.getKey(), tempArr);
        			}
        			else{
        				ArrayList<AbstractMap.SimpleEntry<String,Double>> tempArr = new ArrayList<AbstractMap.SimpleEntry<String,Double>>();
        				tempArr.add(new AbstractMap.SimpleEntry<String, Double>(e2.getKey(), jaccardDistance));
        				expension.put(e1.getKey(), tempArr);
        			}
        		
        			if(expension.containsKey(e2.getKey())){
        				ArrayList<AbstractMap.SimpleEntry<String,Double>> tempArr = expension.get(e2.getKey());
        				tempArr.add(new AbstractMap.SimpleEntry<String, Double>(e1.getKey(), jaccardDistance));
        				Collections.sort(tempArr,wordDoubleComparator);
        				if(tempArr.size()>expensionListLength) tempArr.remove(tempArr.size()-1);
        				expension.put(e2.getKey(), tempArr);
        			}
        			else{
        				ArrayList<AbstractMap.SimpleEntry<String,Double>> tempArr = new ArrayList<AbstractMap.SimpleEntry<String,Double>>();
        				tempArr.add(new AbstractMap.SimpleEntry<String, Double>(e1.getKey(), jaccardDistance));
        				expension.put(e2.getKey(), tempArr);
        			}
        		}
        	}
    	}
    	hasExpension = true;
    }
	
	public void buildQueryCompletion(){
		completion = new HashMap<String,ArrayList<AbstractMap.SimpleEntry<String,Integer>>>();
		for(HashMap.Entry<String, Integer> entry : bigram.entrySet()){
			if(entry.getValue()<completionThreshold) continue;
			String[] wl = entry.getKey().split(" ");
			if(wl.length!=2) continue;
			if(completion.containsKey(wl[0])){
				ArrayList<AbstractMap.SimpleEntry<String,Integer>> tempArr = completion.get(wl[0]);
				tempArr.add(new AbstractMap.SimpleEntry<String, Integer>(wl[1], entry.getValue()));
				Collections.sort(tempArr,wordIntegerComparator);
				if(tempArr.size()>completionListLength) tempArr.remove(tempArr.size()-1);
				completion.put(wl[0], tempArr);
			}
			else{
				ArrayList<AbstractMap.SimpleEntry<String,Integer>> tempArr = new ArrayList<AbstractMap.SimpleEntry<String,Integer>>();
				tempArr.add(new AbstractMap.SimpleEntry<String, Integer>(wl[1], entry.getValue()));
				completion.put(wl[0], tempArr);
			}
		}
		bigram=null;
		hasCompletion = true;
	}
	
	public static boolean isNumeric(String str) {//check if the str is a number
        try {
        	String bigStr = new BigDecimal(str).toString();
        } catch (Exception e) {
            return false;
        }
        return true;
    }
	
	private void buildBiGram(String lastWord,String str){
		if(lastWord.equals("")) return;
		String bg = lastWord + " "+str;
		if(bigram.containsKey(bg)){
			bigram.put(bg,bigram.get(bg)+1);
		}
		else{
			bigram.put(bg, 1);
		}
	}
	
}
