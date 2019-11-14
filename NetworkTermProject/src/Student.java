
public class Student {

	String name;
	String id;
	String passwd;
	int win;
	int lose;
	int draw;
	String time;
	
	public Student(String name, String id, String passwd) {
		this.name = name;
		this.id = id;
		this.passwd = passwd;
	}
	
	public void win() {this.win++;}
	public void lose() {this.lose++;}
	public void draw() {this.draw++;}
	public String getName() {return name;}
	public String getID() {return id;}
	public String getPasswd() {return passwd;}
	
	
}


