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
	public JButton j1;
	public JButton j2;
	public TextField t1;
	public TextField t2 ;
	public TextField t3 ;
	public RegisterView() {
		JPanel p = new JPanel();
		p.setLayout(null);
		ImageIcon nameicon = new ImageIcon("C:\\NW_TP\\NetworkTermProject\\nameRegi.png");
		ImageIcon idicon = new ImageIcon("C:\\NW_TP\\NetworkTermProject\\id.png");
		ImageIcon pwicon = new ImageIcon("C:\\NW_TP\\NetworkTermProject\\passwd.png");
		ImageIcon addressicon = new ImageIcon("C:\\NW_TP\\NetworkTermProject\\addRegi.png");
		ImageIcon addicon = new ImageIcon("C:\\NW_TP\\NetworkTermProject\\additionRegi.png");
		JLabel name = new JLabel(nameicon);
		JLabel id = new JLabel(idicon);
		JLabel pw = new JLabel(pwicon);
		JLabel ad = new JLabel(addressicon);
		JLabel other = new JLabel(addicon);
		p.add(name);
		p.add(id);
		p.add(pw);
		p.add(ad);
		p.add(other);
		t1 = new TextField();
		t2 = new TextField();
		t3 = new TextField();
		TextField t4 = new TextField();
		TextField t5 = new TextField();
		p.add(t1);
		p.add(t2);
		p.add(t3);
		
		p.add(t4);
		p.add(t5);
		t3.setEchoChar('*');
		
		j1 = new JButton("저장");
		j2 = new JButton("취소");
		
		p.add(j1);
		p.add(j2);
		name.setBounds(45,114,75,40);
		id.setBounds(45, 166, 87, 40);
		pw.setBounds(40, 212, 156, 49);
		ad.setBounds(40, 273, 87, 40);
		other.setBounds(40, 352, 133, 40);
		t1.setBounds(211,114,200,36);
		t2.setBounds(211,163, 200,43);
		t3.setBounds(211,221,200,40);
		t4.setBounds(211,273,314,61);
		t5.setBounds(211,352,314,102);
		j1.setBounds(68,486,211,55);
		j2.setBounds(314,486,211,55);
		getContentPane().add(p);
		setSize(600	,600);
		setTitle("회원가입");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		p.setBackground(new Color(242,238,229));

		ImageIcon registerTitle = new ImageIcon("C:\\NW_TP\\NetworkTermProject\\registerTitle.png");
		JLabel lblNewLabel = new JLabel(registerTitle);
		lblNewLabel.setBounds(177, 29, 220, 61);
		p.add(lblNewLabel);
		setVisible(true);
		
	}
		
}
