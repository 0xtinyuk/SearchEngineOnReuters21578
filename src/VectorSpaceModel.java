import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;

import org.tartarus.snowball.SnowballStemmer;
import org.tartarus.snowball.ext.englishStemmer;

import java.util.Comparator;


public class VectorSpaceModel {
	public String query;
	public HashMap<String, Double> Q;
	public ArrayList<Double> L;
	public VectorSpaceModel(String qu){
		this.query = qu;
	}
	
	public HashSet<Integer> search(Dictionary dictionary,ArrayList<Document> docs){
		SnowballStemmer stemmer = new englishStemmer();
		this.query = this.query.replaceAll(" +", " ").toLowerCase();
		String wordlist[] = this.query.split(" ");
		String newQuery = "";
		Q=new HashMap<String,Double>();
		L=new ArrayList<Double>();
		for (int i=0;i<docs.size();i++) {
			Document document = docs.get(i);
			document.score = Double.valueOf(0);
			docs.set(i, document);
		}
		for (String str : wordlist) {
			if(dictionary.normalization){
				str = str.replaceAll("[\\-\\.]", "");
			}
			if(dictionary.stemming){
				stemmer.setCurrent(str);
				if(stemmer.stem()){
					str = stemmer.getCurrent();
					//System.out.println(str);
				}
			}
			if(Q.get(str)!=null){
				Q.put(str, Q.get(str)+1);
			}
			else{
				Q.put(str, Double.valueOf(1));
				newQuery = newQuery + " "+str;
			}
		}
		Double s = Double.valueOf(0);
		this.query = newQuery.trim();
		wordlist = this.query.split(" ");
		for (String str : wordlist) {
			if(dictionary.normalization){
				str = str.replaceAll("[\\-\\.]", "");
			}
			if(dictionary.stemming){
				stemmer.setCurrent(str);
				if(stemmer.stem()){
					str = stemmer.getCurrent();
					//System.out.println(str);
				}
			}
			if(Q.get(str)!=null)
				s = s + Q.get(str)*Q.get(str);
		}
		for (String str : wordlist) {
			if(dictionary.normalization){
				str = str.replaceAll("[\\-\\.]", "");
			}
			if(dictionary.stemming){
				stemmer.setCurrent(str);
				if(stemmer.stem()){
					str = stemmer.getCurrent();
					//System.out.println(str);
				}
			}
			if(Q.get(str)!=null)
				Q.put(str, Q.get(str)/s);
			else Q.put(str, Double.valueOf(0));
		}
		
		for (String str : wordlist) {
			if(dictionary.normalization){
				str = str.replaceAll("[\\-\\.]", "");
			}
			if(dictionary.stemming){
				stemmer.setCurrent(str);
				if(stemmer.stem()){
					str = stemmer.getCurrent();
					//System.out.println(str);
				}
			}
			ArrayList<Double> tempArr;
			//System.out.println(str);
			if(dictionary.dict.get(str)==null) continue;
			for (Integer index : dictionary.dict.get(str)) {
				Double temp;
				if(L.size()<index+1)
					for(int j=L.size();j<index+1;j++)
						L.add(j,Double.valueOf(0));
				if(L.get(index)!=null){
					tempArr = dictionary.tfidf.get(str);
					temp = tempArr.get(index) * tempArr.get(index);
					L.add(index, L.get(index)+temp);
				}
				else{
					tempArr = dictionary.tfidf.get(str);
					temp = tempArr.get(index) * tempArr.get(index);
					L.add(index, temp);
				}
			}
		}
		
		ArrayList<Document> documents = docs;
		for (String str : wordlist) {
			if(dictionary.normalization){
				str = str.replaceAll("[\\-\\.]", "");
			}
			if(dictionary.stemming){
				stemmer.setCurrent(str);
				if(stemmer.stem()){
					str = stemmer.getCurrent();
					//System.out.println(str);
				}
			}
			ArrayList<Double> tempArr = dictionary.tfidf.get(str);
			if(dictionary.dict.get(str)==null) continue;
			for (Integer index : dictionary.dict.get(str)) {
				docs.get(index).score += Q.get(str)*tempArr.get(index)/Math.sqrt(L.get(index));
			}
		}
		Collections.sort(documents, new Comparator<Document>() {
            public int compare(Document doc1, Document doc2) {
                return new Double(doc2.score).compareTo(new Double(doc1.score));
            }
        });
		
		HashSet<Integer> result = new HashSet<>();
		for(Document document : documents){
			if(document.score <=0) break;
			//System.out.println(document.docID);
			result.add(document.docID);
		}
		return result;
	}
}
