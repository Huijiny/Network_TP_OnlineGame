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
			//���� ���� �޾ƿ����� Ȯ�� �ڵ�. ���߿� �����.
			for(int i=0;i<users.size();i++) {
				System.out.println(users.get(i));
			}
			//�α��� �� ���� ��. ���߿� ����� �ڵ�� ��ĥ��.
			LoginView lv = new LoginView();
			lv.getRegisteredUserInfo(users);
			lv.setVisible(true);
	}
	
	

}
