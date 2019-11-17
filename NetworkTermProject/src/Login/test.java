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
			
			int c;
			try {
				fis = new FileInputStream("userinfo.txt");
				ois = new ObjectInputStream(fis);
				
				while((c = ois.read())==-1) {
					users.add((User)ois.readObject()); 
					}
				ois.close();
			}catch(Exception e) {
				System.out.println(e.getMessage());
			}
			//유저 정보 받아오는지 확인 코드. 나중에 지우기.
			for(int i=0;i<users.size();i++) {
				System.out.println(users.get(i));
			}
			//로그인 뷰 띄우는 곳. 나중에 상운이 코드랑 합칠거.
			LoginView lv = new LoginView();
			lv.getRegisteredUserInfo(users);
			lv.setVisible(true);
	}
	
	

}
