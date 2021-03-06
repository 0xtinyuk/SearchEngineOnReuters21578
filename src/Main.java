import java.awt.EventQueue;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.tartarus.snowball.SnowballStemmer;
import org.tartarus.snowball.ext.englishStemmer;

public class Main {
	ArrayList<Document> docs;
	Dictionary dictionary;
	ArrayList<Document> rdocs;
	boolean hasDict=false,hasStemmed,hasStopword,hasNormalized,hasReuters,hasTopicAssign;
	public int reutersFileAmount = 1;

	public void preProcessing(){
		CorpusPreprocessing cp = new CorpusPreprocessing();
		docs = cp.work("UofO.html");
		dictionary = new Dictionary();
		reutersFileAmount = 1;
		rdocs = cp.reuters(1);
		cp = null;
		return;
	}
	
	public void inputReuters(int fileAmount,boolean stemming, boolean stopword, boolean normalization,boolean reuters,boolean topicAssign){
		reutersFileAmount = fileAmount;
		CorpusPreprocessing cp = new CorpusPreprocessing();
		rdocs = cp.reuters(fileAmount);
		dictionary.build(rdocs, stopword,stemming, normalization,reuters,topicAssign);
	}
	
	public String queryExpension(String query,boolean stemming, boolean stopword, boolean normalization,boolean reuters,boolean topicAssign){
		String[] wordlist = query.split(" ");
		SnowballStemmer stemmer = new englishStemmer();
		if(reuters){
			if(hasDict && (hasReuters == reuters) && (hasStemmed==stemming) 
					&& (hasStopword==stopword) && (hasNormalized==normalization) && (hasTopicAssign == topicAssign)){
				//Dict is the same as the last time
			}
			else{
				inputReuters(reutersFileAmount,stemming,stopword, normalization, reuters,topicAssign);
				//dictionary.build(rdocs, stopword,stemming, normalization,reuters,topicAssign);
				hasDict = true;hasReuters = reuters;hasStemmed=stemming;hasStopword=stopword;hasNormalized=normalization;hasTopicAssign=topicAssign;
			}
		}
		else{
			if(hasDict && (hasReuters == reuters) && (hasStemmed==stemming) 
					&& (hasStopword==stopword) && (hasNormalized==normalization) && (hasTopicAssign == topicAssign)){
				//Dict is the same as the last time
			}
			else{
				dictionary.build(docs, stopword,stemming, normalization,reuters,topicAssign);
				hasDict = true;hasReuters = reuters;hasStemmed=stemming;hasStopword=stopword;hasNormalized=normalization;hasTopicAssign=topicAssign;
			}
		}
		if(!dictionary.hasExpension) dictionary.buildQueryExpension();
		String expensionMessage = "";
		for(String word:wordlist){
			String str=word.toLowerCase();
			if(stemming){
				stemmer.setCurrent(str);
				if(stemmer.stem()){
					str = stemmer.getCurrent();
				}
			};
			if(!dictionary.expension.containsKey(str)) {
				 expensionMessage = expensionMessage + "No Expension Suggestion for "+str+"\n";
				 continue;
			}
			ArrayList<AbstractMap.SimpleEntry<String,Double>> expensionList = dictionary.expension.get(str);
			expensionMessage = expensionMessage + "Expension Suggestion for "+str+":\n";
			for(AbstractMap.SimpleEntry<String,Double> entry : expensionList){
				expensionMessage = expensionMessage + entry.getKey()+ " " + entry.getValue() + "\n";
			}
		}
		return expensionMessage;
	}
	
	public String queryCompletion(String query,boolean stemming, boolean stopword, boolean normalization,boolean reuters, boolean topicAssign){
		String[] wordlist = query.split(" ");
		SnowballStemmer stemmer = new englishStemmer();
		String word = wordlist[wordlist.length-1].toLowerCase();
		if(reuters){
			if(hasDict && (hasReuters == reuters) && (hasStemmed==stemming) 
					&& (hasStopword==stopword) && (hasNormalized==normalization) && (hasTopicAssign == topicAssign)){
				//Dict is the same as the last time
			}
			else{
				inputReuters(reutersFileAmount,stemming,stopword, normalization, reuters,topicAssign);
				//dictionary.build(rdocs, stopword,stemming, normalization,reuters,topicAssign);
				hasDict = true;hasReuters = reuters;hasStemmed=stemming;hasStopword=stopword;hasNormalized=normalization;hasTopicAssign=topicAssign;
			}
		}
		else{
			if(hasDict && (hasReuters == reuters) && (hasStemmed==stemming) 
					&& (hasStopword==stopword) && (hasNormalized==normalization) && (hasTopicAssign == topicAssign)){
				//Dict is the same as the last time
			}
			else{
				dictionary.build(docs, stopword,stemming, normalization,reuters,topicAssign);
				hasDict = true;hasReuters = reuters;hasStemmed=stemming;hasStopword=stopword;hasNormalized=normalization;hasTopicAssign=topicAssign;
			}
		}
		if(!dictionary.hasCompletion) dictionary.buildQueryCompletion();
		String str=word;
		if(stemming){
			stemmer.setCurrent(str);
			if(stemmer.stem()){
				str = stemmer.getCurrent();
			}
		};
		if(!dictionary.completion.containsKey(str)) {
			if(stopword) return "No Query Completion Suggestion!\nRemove Stopword option and Try again";
			else return "No Query Completion Suggestion!";
		}
		ArrayList<AbstractMap.SimpleEntry<String,Integer>> completionList = dictionary.completion.get(str);
		String completionMessage = "";
		for(AbstractMap.SimpleEntry<String,Integer> entry : completionList){
			completionMessage = completionMessage + entry.getKey() +" "+entry.getValue() +"\n";
		}
		return completionMessage;
	}
	
	public String search(String query, boolean stemming, boolean stopword, boolean normalization, boolean booleanModel,
			boolean reuters,boolean topicAssign,boolean topicRestrction,String topicsRequired){
		if(reuters){
			if(hasDict && (hasReuters == reuters) && (hasStemmed==stemming) 
					&& (hasStopword==stopword) && (hasNormalized==normalization) && (hasTopicAssign == topicAssign)){
				//Dict is the same as the last time
			}
			else{
				inputReuters(reutersFileAmount,stemming,stopword, normalization, reuters,topicAssign);
				//dictionary.build(rdocs, stopword,stemming, normalization,reuters,topicAssign);
				hasDict = true;hasReuters = reuters;hasStemmed=stemming;hasStopword=stopword;hasNormalized=normalization;hasTopicAssign=topicAssign;
			}
		}
		else{
			if(hasDict && (hasReuters == reuters) && (hasStemmed==stemming) 
					&& (hasStopword==stopword) && (hasNormalized==normalization) && (hasTopicAssign == topicAssign)){
				//Dict is the same as the last time
			}
			else{
				dictionary.build(docs, stopword,stemming, normalization,reuters,topicAssign);
				hasDict = true;hasReuters = reuters;hasStemmed=stemming;hasStopword=stopword;hasNormalized=normalization;hasTopicAssign=topicAssign;
			}
		}
		String result = "";
		BooleanRetrievalModel booleanRetrievalModel = new BooleanRetrievalModel(query);
		VectorSpaceModel vectorSpaceModel = new VectorSpaceModel(query);
		if(booleanModel){
			booleanRetrievalModel.queryProcessing();
			HashSet<Integer> resultIndex = booleanRetrievalModel.search(dictionary);
			for (Integer index : resultIndex) {
				if(reuters){
					//System.out.println("docId:"+rdocs.get(index).docID+"\ntitle:"+rdocs.get(index).title+"\ndesc:"+rdocs.get(index).description);
					//result = result +"DocId:"+rdocs.get(index).docID+"\nTitle: "+rdocs.get(index).title+"\nTopic:"+rdocs.get(index).topic+"\nDesc: "+rdocs.get(index).description+"\n\n";
					result = result +"DocId:"+rdocs.get(index).docID+"\nTitle: "+rdocs.get(index).title+"\nTopics: "+rdocs.get(index).topics.toString()/*+"\nDesc: "+rdocs.get(index).description*/+"\n";
				}
				else{
					//System.out.println("docId:"+docs.get(index).docID+"\ntitle:"+docs.get(index).title+"\ndesc:"+docs.get(index).description);
					result = result +"DocId:"+docs.get(index).docID+"\nTitle: "+docs.get(index).title/*+"\nDesc: "+docs.get(index).description*/+"\n";
				}
			}
		}
		else{
			if(reuters){
				if(topicsRequired.equals("")) topicRestrction = false;//If there is no topic specified, we regard it as no topics filter
				ArrayList<Integer> resultIndex = null;
				if(reuters && topicRestrction){
					resultIndex = vectorSpaceModel.search(dictionary, rdocs,true,topicsRequired);
				}
				else{
					resultIndex = vectorSpaceModel.search(dictionary, rdocs,false,"");
				}
				for (Integer index : resultIndex) {
					//System.out.println("docId:"+rdocs.get(index).docID+"\ntitle:"+rdocs.get(index).title+"\ndesc:"+rdocs.get(index).description);
					result = result +"DocId:"+rdocs.get(index).docID+"\nTitle: "+rdocs.get(index).title+"\nTopics: "+rdocs.get(index).topics.toString()/*+"\nDesc: "+rdocs.get(index).description*/+"\n";
				}
			}
			else{
				ArrayList<Integer> resultIndex = vectorSpaceModel.search(dictionary, docs,false,"");
				for (Integer index : resultIndex) {
					//System.out.println("docId:"+docs.get(index).docID+"\ntitle:"+docs.get(index).title+"\ndesc:"+docs.get(index).description);
					result = result +"DocId:"+docs.get(index).docID+"\nTitle: "+docs.get(index).title/*+"\nDesc: "+docs.get(index).description*/+"\n";
				}
			}
			
		}
		return result;
	}
	

}
