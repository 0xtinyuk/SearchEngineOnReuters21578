import java.sql.ResultSet;
import java.util.HashSet;

public class Document {
	public int docID;
	public String title;
	public String description;
	//public String topic;
	public HashSet<String> topics;
	public Double score;
	
	public Document(int docid, String tit, String desc){
		this.docID = docid;
		this.title = tit;
		this.description = desc;
		this.score = Double.valueOf(0);
		this.topics = null;
	}
	
	public Document(int docid, String tit, String desc, String[] topic){
		this.docID = docid;
		this.title = tit;
		this.description = desc;
		this.score = Double.valueOf(0);
		this.topics = new HashSet<String>();
		if(topic == null) return;
		for(int i=0;i<topic.length;i++)
			if(!topic[i].equals(""))
				this.topics.add(topic[i]);
	}
	
	public void scoreReset(){
		this.score = Double.valueOf(0);
		return;
	}
}
