package DataStructure;
import java.io.Serializable;

public class User implements Serializable{

	private int no;
	private String name;
	private String id;
	private String passwd;
	private int win;
	private int lose;
	private int draw;
	private String time;
	
	public User(String name, String id, String passwd) {
		this.name = name;
		this.id = id;
		this.passwd = passwd;
	}
	public User() {
		
	}
	public User(int no) {
		this.no = no;
	}
	public User(String id, String passwd) {
		this.id = id;
		this.passwd = passwd;
	}
	public void setName(String name) {this.name = name;	}
	public void setID(String ID) {this.id = ID;}
	public void setPasswd(String pw) {this.passwd = pw;}
	public void setWin(int win) {this.win = win;}
	public void setLose(int lose) {this.lose = lose;}
	public void setDraw(int draw) {this.draw = draw;}
	public void win() {this.win++;}
	public void lose() {this.lose++;}
	public void draw() {this.draw++;}
	public void time(String time) {this.time = time;}
	public String getName() {return name;}
	public String getID() {return id;}
	public String getPasswd() {return passwd;}
	public int getWin() {return win;}
	public int getlose() {return lose;}
	public int getdraw() {return draw;}
	
	@Override
	public String toString() {
		return String.format("User{number='%s', name='%s', id='%s', passwd='%s',"
				+ "win='%s, lose='%s', draw='%s', time='%s'}", no,name,id,passwd,win,lose,draw,time);
		
	}
	
	
	
}


