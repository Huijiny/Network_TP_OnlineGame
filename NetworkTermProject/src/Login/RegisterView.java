package Login;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.ObjectOutputStream;

import javax.swing.*;

import DataStructure.User;

import java.awt.*;
public class RegisterView extends JFrame {
	public RegisterView() {
		JPanel p = new JPanel();
		p.setLayout(null);
		Label name = new Label("이름");
		Label id = new Label("아이디");
		Label pw = new Label("패스워드");
		Label ad = new Label("주소");
		Label other = new Label("추가사항");
		p.add(name);
		p.add(id);
		p.add(pw);
		p.add(ad);
		p.add(other);
		TextField t1 = new TextField();
		TextField t2 = new TextField();
		TextField t3 = new TextField();
		TextField t4 = new TextField();
		TextField t5 = new TextField();
		p.add(t1);
		p.add(t2);
		p.add(t3);
		
		p.add(t4);
		p.add(t5);
		t3.setEchoChar('*');
		JButton j1 = new JButton("저장");
		JButton j2 = new JButton("취소");
		p.add(j1);
		p.add(j2);
		name.setBounds(40,10,40,40);
		id.setBounds(40, 50, 40, 40);
		pw.setBounds(40, 90, 60, 40);
		ad.setBounds(40, 130, 40, 40);
		other.setBounds(40, 170, 60, 40);
		t1.setBounds(120,10,200,30);
		t2.setBounds(120,50, 200,30);
		t3.setBounds(120,90,200,30);
		t4.setBounds(120,130,280,30);
		t5.setBounds(120,180,280,120);
		j1.setBounds(125,330,80,30);
		j2.setBounds(240,330,80,30);
		add(p);
		setSize(500	,500);
		setTitle("회원가입");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setVisible(true);
		j1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				try {
					User user = new User(t1.getText(),t2.getText(),t3.getText());
					setUserInfo(user);
					dispose();
				}catch(Exception ex) {
					JOptionPane.showMessageDialog(null,"회원가입에 실패하였습니다.");
				}
			}
			
		});
		j2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				dispose();
			}
			
		});
	}
	void setUserInfo(User user) {
		FileOutputStream fos = null;
		ObjectOutputStream oos =null;
		
		try {
			if(new File("userinfo.txt").exists()) {
				fos = new FileOutputStream("./userinfo.txt",true);
				oos = new  MyObjectOutputStream(fos);
			}else {
				fos = new FileOutputStream("./userinfo.txt");
				oos = new ObjectOutputStream(fos);
			}
			oos.writeObject(user);
			oos.flush();
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}finally {
			try {
				oos.close();
				fos.close();
			}catch(Exception e) {}
		}
		
		
		
	}
}
