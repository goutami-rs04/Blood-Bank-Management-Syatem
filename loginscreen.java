package bloodbanksytem;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class loginscreen extends JFrame {

  JLabel titleLabel, userLabel, passLabel;
  JTextField userField;
  JPasswordField passField;
  JButton loginButton;
  JPanel mainPanel;

  public loginscreen() {
    setTitle("Blood Bank Management System");
    setSize(450, 350);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setLocationRelativeTo(null);
    setResizable(false);

    mainPanel = new JPanel();
    mainPanel.setLayout(null);
    mainPanel.setBackground(new Color(180, 0, 0));

    titleLabel = new JLabel("Blood Bank System");
    titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
    titleLabel.setForeground(Color.WHITE);
    titleLabel.setBounds(90, 30, 300, 40);
    mainPanel.add(titleLabel);

    userLabel = new JLabel("Username:");
    userLabel.setFont(new Font("Arial", Font.BOLD, 14));
    userLabel.setForeground(Color.WHITE);
    userLabel.setBounds(70, 110, 100, 25);
    mainPanel.add(userLabel);

    userField = new JTextField();
    userField.setFont(new Font("Arial", Font.PLAIN, 14));
    userField.setBounds(180, 110, 180, 30);
    mainPanel.add(userField);

    passLabel = new JLabel("Password:");
    passLabel.setFont(new Font("Arial", Font.BOLD, 14));
    passLabel.setForeground(Color.WHITE);
    passLabel.setBounds(70, 160, 100, 25);
    mainPanel.add(passLabel);

    passField = new JPasswordField();
    passField.setFont(new Font("Arial", Font.PLAIN, 14));
    passField.setBounds(180, 160, 180, 30);
    mainPanel.add(passField);

    loginButton = new JButton("LOGIN");
    loginButton.setFont(new Font("Arial", Font.BOLD, 16));
    loginButton.setBounds(150, 220, 140, 40);
    loginButton.setBackground(Color.WHITE);
    loginButton.setForeground(new Color(180, 0, 0));
    mainPanel.add(loginButton);

    add(mainPanel);

    loginButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        checkLogin();
      }
    });

    setVisible(true);
  }

  void checkLogin() {
    String username = userField.getText();
    String password = new String(passField.getPassword());

    if (username.isEmpty() || password.isEmpty()) {
      JOptionPane.showMessageDialog(null,
        "Please enter username and password!",
        "Error", JOptionPane.ERROR_MESSAGE);
      return;
    }

    try {
      Connection con = DBConnecton.getConnection();
      String sql = "SELECT * FROM Staff WHERE " +
                   "username=? AND password=?";
      PreparedStatement ps = con.prepareStatement(sql);
      ps.setString(1, username);
      ps.setString(2, password);
      ResultSet rs = ps.executeQuery();

      if (rs.next()) {
    	  String name = rs.getString("name");
    	  String role = rs.getString("role");
    	  
    	  try {

    		    Dashboard d =
    		        new Dashboard(name, role);

    		    d.setVisible(true);

    		    dispose();

    		} catch(Exception ex) {

    		    ex.printStackTrace();

    		    JOptionPane.showMessageDialog(
    		        null,
    		        ex.getMessage()
    		    );
    		}
    	}
        
        
        else {
        JOptionPane.showMessageDialog(null,
          "Wrong username or password!",
          "Login Failed!",
          JOptionPane.ERROR_MESSAGE);
      }
      con.close();

    } catch (Exception ex) {
      JOptionPane.showMessageDialog(null,
        "Database Error!\n" + ex.getMessage(),
        "Error", JOptionPane.ERROR_MESSAGE);
    }
  }

  public static void main(String[] args) {
    new loginscreen();
  }
}