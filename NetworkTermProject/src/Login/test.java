package Login;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;

import DataStructure.User;

public class test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
			ArrayList<User> users = new ArrayList<User>();
			
			FileInputStream fis = null;
			ObjectInputStream ois = null;
			File f = new File("./userinfo.txt");
			int c;
			try {
				fis = new FileInputStream("./userinfo.txt");
				if(f.isFile()) {
					ois = new ObjectInputStream(fis);
					while((c = ois.read())==-1) {
						users.add((User)ois.readObject()); 
						System.out.println("유저 정보"+(User)ois.readObject());
						
					}
				}
				ois.close();
			}catch(Exception e) {
				System.out.println("ee1");
				//System.out.println(e.getMessage());
			}
			LoginView lv = new LoginView();
			lv.setVisible(true);
	}
	
	

}
