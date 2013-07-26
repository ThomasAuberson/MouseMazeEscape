
public class Score {

	//FIELDS
	private int score;
	private String name;
	
	public Score(int s, String n){
		score = s;
		name = n;
	}
	
	public int getScore(){
		return score;
	}
	
	public String getName(){
		return name;
	}
	
	public boolean higherThan(Score s){
		if(score>s.getScore()) return true;
		return false;
	}
}
