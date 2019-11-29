package Login;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
 
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import DataStructure.User;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.awt.event.ActionEvent;
import javax.swing.JPasswordField;

public class LoginView extends JFrame {
	 	public User user ;
	 	public JButton loginBtn;
	 	public JPanel contentPane;
	    public JTextField idField;
	    public JPasswordField pw;
	    private Map<String, String> map;
	    public JButton joinBtn;
	    /**
	     * Launch the application.
	     */
	    
	    /**
	     * Create the frame.
	     * @return 
	     */
	    public LoginView() {
	        map= new HashMap<String,String>();
	        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        setBounds(100, 100, 500, 400);
	        contentPane = new JPanel();
	        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
	        contentPane.setBackground(new Color(242,238,229));
	        setContentPane(contentPane);
	        contentPane.setLayout(null);
	        
	        ImageIcon id = new ImageIcon("C:\\NW_TP\\NetworkTermProject\\id.png");
	        JLabel lblNewLabel = new JLabel(id);
	        lblNewLabel.setBounds(103, 114, 98, 33);
	        contentPane.add(lblNewLabel);
	        
	        idField = new JTextField();
	        idField.setBounds(268, 114, 141, 33);
	        contentPane.add(idField);
	        idField.setColumns(10);
	        
	        ImageIcon passwd = new ImageIcon("C:\\NW_TP\\NetworkTermProject\\passwd.png");
	        JLabel label = new JLabel(passwd);
	        label.setBounds(110, 173, 144, 47);
	        contentPane.add(label);
	        
	        loginBtn = new JButton("로그인");
	       
	        loginBtn.setBounds(96, 275, 135, 46);
	        contentPane.add(loginBtn);
	        
	        pw = new JPasswordField();
	        pw.setBounds(268, 173, 141, 33);
	        contentPane.add(pw);
	        
	        joinBtn = new JButton("회원가입");
	        joinBtn.setBounds(300, 275, 129, 46);
	        contentPane.add(joinBtn);
	        
	        ImageIcon loginTitle = new ImageIcon("C:\\NW_TP\\NetworkTermProject\\LoginTitle.png");
	        JLabel lblNewLabel_1 = new JLabel(loginTitle);
	        lblNewLabel_1.setBounds(149, 27, 193, 59);
	        contentPane.add(lblNewLabel_1);
	        setVisible(true);
			setResizable(true);
	    }
}
