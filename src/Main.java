import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.print.attribute.SetOfIntegerSyntax;

public class Main {
	ArrayList<Document> docs;
	Dictionary dictionary;
	ArrayList<Document> rdocs;

	public void preProcessing(){
		CorpusPreprocessing cp = new CorpusPreprocessing();
		docs = cp.work("UofO.html");
		dictionary = new Dictionary();
		rdocs = cp.reuters();
		return;
	}
	
	public String search(String query, boolean stemming, boolean stopword, boolean normalization, boolean booleanModel,boolean reuters){
		if(reuters){
			dictionary.build(rdocs, stopword,stemming, normalization);
		}
		else{
			dictionary.build(docs, stopword,stemming, normalization);
		}
		String result = "";
		BooleanRetrievalModel booleanRetrievalModel = new BooleanRetrievalModel(query);
		VectorSpaceModel vectorSpaceModel = new VectorSpaceModel(query);
		if(booleanModel){
			booleanRetrievalModel.queryProcessing();
			HashSet<Integer> resultIndex = booleanRetrievalModel.search(dictionary);
			for (Integer index : resultIndex) {
				if(reuters){
					System.out.println("docId:"+rdocs.get(index).docID+"\ntitle:"+rdocs.get(index).title+"\ndesc:"+rdocs.get(index).description);
					result = result +"DocId:"+rdocs.get(index).docID+"\nTitle: "+rdocs.get(index).title+/*"\nDesc: "+rdocs.get(index).description+*/"\n\n";
				}
				else{
					//System.out.println("docId:"+docs.get(index).docID+"\ntitle:"+docs.get(index).title+"\ndesc:"+docs.get(index).description);
					result = result +"DocId:"+docs.get(index).docID+"\nTitle: "+docs.get(index).title+/*"\nDesc: "+docs.get(index).description+*/"\n\n";
				}
			}
		}
		else{
			if(reuters){
				HashSet<Integer> resultIndex = vectorSpaceModel.search(dictionary, rdocs);
				/*for (Integer index : resultIndex) {
					System.out.println("docId:"+rdocs.get(index).docID+"\ntitle:"+rdocs.get(index).title+"\ndesc:"+rdocs.get(index).description);
					result = result +"DocId:"+rdocs.get(index).docID+"\nTitle: "+rdocs.get(index).title+"\nDesc: "+rdocs.get(index).description+"\n\n";
				}*/	
				for(Document document : rdocs){
					if(document.score <=0) break;
					System.out.println("docId:"+document.docID+"\ntitle:"+document.title+"\ndesc:"+document.description);
					result = result +"DocId:"+document.docID+"\nTitle: "+document.title+/*"\nDesc: "+document.description+*/"\n\n";
				}
			}
			else{
				HashSet<Integer> resultIndex = vectorSpaceModel.search(dictionary, docs);
				/*for (Integer index : resultIndex) {
					System.out.println("docId:"+docs.get(index).docID+"\ntitle:"+docs.get(index).title+"\ndesc:"+docs.get(index).description);
					result = result +"DocId:"+docs.get(index).docID+"\nTitle: "+docs.get(index).title+"\nDesc: "+docs.get(index).description+"\n\n";
				}*/	
				for(Document document : docs){
					if(document.score <=0) break;
					//System.out.println("docId:"+document.docID+"\ntitle:"+document.title+"\ndesc:"+document.description);
					result = result +"DocId:"+document.docID+"\nTitle: "+document.title+/*"\nDesc: "+document.description+*/"\n\n";
				}
			}
			
		}
		return result;
	}
	

}
