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
	public User(int no) {
		this.no = no;
	}
	
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


