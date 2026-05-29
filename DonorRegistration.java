package bloodbanksytem;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class DonorRegistration extends JFrame {

  JTextField nameField, ageField, 
             phoneField, locationField;
  JComboBox<String> bloodGroupBox;
  JButton saveButton, clearButton;

  public DonorRegistration() {

	    setTitle("Donor Registration");
	    setSize(500, 450);
	    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	    setLocationRelativeTo(null);
	    setResizable(false);

	    JPanel panel = new JPanel();
	    panel.setLayout(null);
	    panel.setBackground(new Color(255, 245, 245));

	    // Title
	    JLabel title = new JLabel("Donor Registration");
	    title.setFont(new Font("Arial", Font.BOLD, 22));
	    title.setForeground(new Color(180, 0, 0));
	    title.setBounds(120, 20, 280, 35);
	    panel.add(title);

	    // Name
	    JLabel nameLabel = new JLabel("Full Name:");
	    nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
	    nameLabel.setBounds(50, 80, 120, 25);
	    panel.add(nameLabel);

	    nameField = new JTextField();
	    nameField.setFont(new Font("Arial", Font.PLAIN, 14));
	    nameField.setBounds(180, 80, 250, 30);
	    panel.add(nameField);

	    // Blood Group
	    JLabel bgLabel = new JLabel("Blood Group:");
	    bgLabel.setFont(new Font("Arial", Font.BOLD, 14));
	    bgLabel.setBounds(50, 130, 120, 25);
	    panel.add(bgLabel);

	    String[] bloodGroups = {
	      "A+", "A-", "B+", "B-",
	      "O+", "O-", "AB+", "AB-"
	    };
	    bloodGroupBox = new JComboBox<>(bloodGroups);
	    bloodGroupBox.setFont(new Font("Arial", Font.PLAIN, 14));
	    bloodGroupBox.setBounds(180, 130, 250, 30);
	    panel.add(bloodGroupBox);

	    // Age
	    JLabel ageLabel = new JLabel("Age:");
	    ageLabel.setFont(new Font("Arial", Font.BOLD, 14));
	    ageLabel.setBounds(50, 180, 120, 25);
	    panel.add(ageLabel);

	    ageField = new JTextField();
	    ageField.setFont(new Font("Arial", Font.PLAIN, 14));
	    ageField.setBounds(180, 180, 250, 30);
	    panel.add(ageField);

	    // Phone
	    JLabel phoneLabel = new JLabel("Phone:");
	    phoneLabel.setFont(new Font("Arial", Font.BOLD, 14));
	    phoneLabel.setBounds(50, 230, 120, 25);
	    panel.add(phoneLabel);

	    phoneField = new JTextField();
	    phoneField.setFont(new Font("Arial", Font.PLAIN, 14));
	    phoneField.setBounds(180, 230, 250, 30);
	    panel.add(phoneField);

	    // Location
	    JLabel locLabel = new JLabel("Location:");
	    locLabel.setFont(new Font("Arial", Font.BOLD, 14));
	    locLabel.setBounds(50, 280, 120, 25);
	    panel.add(locLabel);

	    locationField = new JTextField();
	    locationField.setFont(new Font("Arial", Font.PLAIN, 14));
	    locationField.setBounds(180, 280, 250, 30);
	    panel.add(locationField);

	    // Save Button
	    saveButton = new JButton("SAVE DONOR");
	    saveButton.setFont(new Font("Arial", Font.BOLD, 14));
	    saveButton.setBounds(80, 350, 150, 40);
	    saveButton.setBackground(new Color(180, 0, 0));
	    saveButton.setForeground(Color.WHITE);
	    panel.add(saveButton);

	    // Clear Button
	    clearButton = new JButton("CLEAR");
	    clearButton.setFont(new Font("Arial", Font.BOLD, 14));
	    clearButton.setBounds(260, 350, 150, 40);
	    clearButton.setBackground(Color.GRAY);
	    clearButton.setForeground(Color.WHITE);
	    panel.add(clearButton);

	    add(panel);

	    // Save button action
	    saveButton.addActionListener(new ActionListener() {
	      public void actionPerformed(ActionEvent e) {
	        saveDonor();
	      }
	    });

	    // Clear button action
	    clearButton.addActionListener(new ActionListener() {
	      public void actionPerformed(ActionEvent e) {
	        clearFields();
	      }
	    });

	    setVisible(true);
	  }
  void saveDonor() {

    String name     = nameField.getText();
    String bg       = bloodGroupBox.getSelectedItem()
                                   .toString();
    String ageText  = ageField.getText();
    String phone    = phoneField.getText();
    String location = locationField.getText();

    // Check empty fields
    if (name.isEmpty() || ageText.isEmpty() ||
        phone.isEmpty() || location.isEmpty()) {
      JOptionPane.showMessageDialog(null,
        "Please fill all fields!",
        "Error", JOptionPane.ERROR_MESSAGE);
      return;
    }

    int age = Integer.parseInt(ageText);

    // Check age eligibility
    if (age < 18 || age > 65) {
      JOptionPane.showMessageDialog(null,
        "Donor age must be between 18 and 65!",
        "Error", JOptionPane.ERROR_MESSAGE);
      return;
    }

    try {
      Connection con = DBConnecton.getConnection();
      String sql = "INSERT INTO Donors " +
                   "(name, blood_group, age, " +
                   "phone, location) " +
                   "VALUES (?, ?, ?, ?, ?)";

      PreparedStatement ps = 
                        con.prepareStatement(sql);
      ps.setString(1, name);
      ps.setString(2, bg);
      ps.setInt(3, age);
      ps.setString(4, phone);
      ps.setString(5, location);
      ps.executeUpdate();
      String updateSql =
    		  "UPDATE bloodstock " +
    		  "SET units_available = units_available , " +
    		  "collected_date = CURDATE(), " +
    		  "expiry_date = DATE_ADD(CURDATE(), INTERVAL 35 DAY) " +
    		  "WHERE blood_group = ?";

    		  PreparedStatement ps2 =
    		          con.prepareStatement(updateSql);

    		  ps2.setString(1, bg);

    		  ps2.executeUpdate();

      JOptionPane.showMessageDialog(null,
        "Donor Registered Successfully! ✅",
        "Success",
        JOptionPane.INFORMATION_MESSAGE);

      clearFields();
      con.close();

    } catch (Exception ex) {
      JOptionPane.showMessageDialog(null,
        "Error: " + ex.getMessage(),
        "Error", JOptionPane.ERROR_MESSAGE);
    }
  }

  void clearFields() {
    nameField.setText("");
    ageField.setText("");
    phoneField.setText("");
    locationField.setText("");
    bloodGroupBox.setSelectedIndex(0);
  }

  public static void main(String[] args) {
    new DonorRegistration();
  }
}