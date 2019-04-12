import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.tartarus.snowball.SnowballStemmer;
import org.tartarus.snowball.ext.*;

public class BooleanRetrievalModel {
	public String query;
	public BooleanRetrievalModel(String qu){
		this.query = qu;
	}
	public void queryProcessing(){
		
		String wordlist[] = this.query.split(" ");
		String result = "";
		String temp = "";
		Stack<String> stack = new Stack<String>();
		for(int i=0;i<wordlist.length;i++){
			String word = wordlist[i];
			if(word.equals("AND") || word.equals("AND_NOT")){
				while(!stack.isEmpty() && (stack.peek().equals("AND_NOT") || stack.peek().equals("AND")))
					result = result +" "+ stack.pop();
				stack.push(word);
				continue;
			}
			if(word.startsWith("(")){
				stack.push("(");
				word = word.replace("(", "");
			}
			if(word.endsWith(")")){
				result = result + " " + word.replace(")", "");
				temp = stack.pop();
				while(!temp.startsWith("(")){
					result = result + " " + temp;
					temp = stack.pop();
				}
				continue;
			}
			if(word.equals("OR")){
				if(stack.isEmpty() || stack.peek().equals("("))stack.push(word);
				else{
					while((!stack.isEmpty()) &&(stack.peek().equals("AND") || stack.peek().equals("AND_NOT"))) 
						result = result +" "+ stack.pop();
					stack.push(word);
				}
				continue;
			}
			result = result +" "+ word;
		}
		while(!stack.isEmpty()) result = result + " " + stack.pop();
		this.query = result.trim();
		return;
	}
	
	public HashSet<Integer> search(Dictionary dictionary){
		String wordlist[] = query.split(" ");
		SnowballStemmer stemmer = new englishStemmer();
		Stack<HashSet<Integer>> stack = new Stack<HashSet<Integer>>();

		
		for(int i=0;i<wordlist.length;i++){
			String word = wordlist[i];
			if(word.equals("AND") || word.equals("OR") || word.equals("AND_NOT")){
				HashSet<Integer> resultIndexB = stack.pop();
				HashSet<Integer> resultIndexA = stack.pop();
				if(word.equals("AND")){
					//System.out.println(resultIndexA);
					//System.out.println(resultIndexB);
					resultIndexA.retainAll(resultIndexB);
				}
				else if(word.equals("OR")){
					resultIndexA.addAll(resultIndexB);
				}
				else{
					resultIndexA.removeAll(resultIndexB);
				}
				stack.push(resultIndexA);
			}
			else{
				word = word.toLowerCase();
				if(dictionary.normalization){
					word = word.replaceAll("[\\-\\.]", "");
				}
				if(dictionary.stemming){
					stemmer.setCurrent(word);
					if(stemmer.stem()){
						word = stemmer.getCurrent();
						//System.out.println(str);
					}
				}
				
				HashSet<Integer> resultIndexTemp = new HashSet<Integer>();
				//HashSet<Integer> tt;
				word = " "+word.replaceAll("\\?", "[^ ]?").replaceAll("\\*", "[^ ]*?")+" ";
				//System.out.println(word);
				Pattern pattern = Pattern.compile(word);
		        Matcher matcher = pattern.matcher(dictionary.dictString);
		        while (matcher.find()) {
		        	String target = matcher.group().trim();
		        	//System.out.println(target);
		        	//System.out.println(tt);
		            resultIndexTemp.addAll(dictionary.dict.get(target));
		        }
		        stack.push(resultIndexTemp);
			}
		}
		return stack.pop();
	}
}
