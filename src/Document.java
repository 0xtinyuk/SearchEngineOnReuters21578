import java.sql.ResultSet;

public class Document {
	public int docID;
	public String title;
	public String description;
	public String topic;
	public Double score;
	
	public Document(int docid, String tit, String desc){
		this.docID = docid;
		this.title = tit;
		this.description = desc;
		this.score = Double.valueOf(0);
		this.topic = null;
	}
	
	public Document(int docid, String tit, String desc, String topic){
		this.docID = docid;
		this.title = tit;
		this.description = desc;
		this.score = Double.valueOf(0);
		this.topic = topic;
	}
	
	public void scoreReset(){
		this.score = Double.valueOf(0);
		return;
	}
}
