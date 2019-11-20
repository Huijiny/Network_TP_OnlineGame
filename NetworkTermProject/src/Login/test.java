package Login;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import java.util.HashMap;

import DataStructure.User;

public class test {
	
	static ArrayList<User> users = new ArrayList<User>();//모든 클라이언트들에게 업데이트되어야함.
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
			getUserInfo();
			FileWhatcher fw = new FileWhatcher();
			try {
				fw.init();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//로그인 뷰 띄우는 곳. 나중에 상운이 코드랑 합칠거.
			LoginView lv = new LoginView();
			lv.getRegisteredUserInfo(users);
			lv.setVisible(true);
	}
	
	static void getUserInfo() {
		
		
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
	}
	
	

}
