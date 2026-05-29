package bloodbanksytem;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class HospitalRequest extends JFrame {

  JComboBox<String> hospitalBox, 
                    bloodGroupBox, 
                    urgencyBox;
  JTextField patientField, unitsField;
  JButton submitButton, viewButton,fulfillButton;
  JTable table;
  DefaultTableModel model;

  public HospitalRequest() {

    setTitle("Hospital Blood Request");
    setSize(600, 620);
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    setLocationRelativeTo(null);

    JPanel panel = new JPanel();
    panel.setLayout(null);
    panel.setBackground(new Color(255, 245, 245));

    // Title
    JLabel title = new JLabel(
                   "Hospital Blood Request");
    title.setFont(new Font("Arial", Font.BOLD, 22));
    title.setForeground(new Color(180, 0, 0));
    title.setBounds(140, 15, 350, 35);
    panel.add(title);

    // Hospital
    JLabel hosLabel = new JLabel("Hospital:");
    hosLabel.setFont(new Font("Arial", 
                              Font.BOLD, 13));
    hosLabel.setBounds(30, 70, 120, 25);
    panel.add(hosLabel);

    hospitalBox = new JComboBox<>();
    hospitalBox.setFont(new Font("Arial", 
                                 Font.PLAIN, 13));
    hospitalBox.setBounds(150, 70, 400, 28);
    panel.add(hospitalBox);
    loadHospitals();

    // Patient Name
    JLabel patLabel = new JLabel("Patient:");
    patLabel.setFont(new Font("Arial", 
                              Font.BOLD, 13));
    patLabel.setBounds(30, 115, 120, 25);
    panel.add(patLabel);

    patientField = new JTextField();
    patientField.setFont(new Font("Arial", 
                                  Font.PLAIN, 13));
    patientField.setBounds(150, 115, 400, 28);
    panel.add(patientField);

    // Blood Group
    JLabel bgLabel = new JLabel("Blood Group:");
    bgLabel.setFont(new Font("Arial", 
                             Font.BOLD, 13));
    bgLabel.setBounds(30, 160, 120, 25);
    panel.add(bgLabel);

    String[] bgs = {"A+","A-","B+","B-",
                    "O+","O-","AB+","AB-"};
    bloodGroupBox = new JComboBox<>(bgs);
    bloodGroupBox.setFont(new Font("Arial", 
                                   Font.PLAIN, 13));
    bloodGroupBox.setBounds(150, 160, 400, 28);
    panel.add(bloodGroupBox);

    // Units Needed
    JLabel unitsLabel = new JLabel("Units Needed:");
    unitsLabel.setFont(new Font("Arial", 
                                Font.BOLD, 13));
    unitsLabel.setBounds(30, 205, 120, 25);
    panel.add(unitsLabel);

    unitsField = new JTextField();
    unitsField.setFont(new Font("Arial", 
                                Font.PLAIN, 13));
    unitsField.setBounds(150, 205, 400, 28);
    panel.add(unitsField);

    // Urgency
    JLabel urgLabel = new JLabel("Urgency:");
    urgLabel.setFont(new Font("Arial", 
                              Font.BOLD, 13));
    urgLabel.setBounds(30, 250, 120, 25);
    panel.add(urgLabel);

    String[] urgency = {"Normal", 
                        "Urgent", 
                        "Emergency"};
    urgencyBox = new JComboBox<>(urgency);
    urgencyBox.setFont(new Font("Arial", 
                                Font.PLAIN, 13));
    urgencyBox.setBounds(150, 250, 400, 28);
    panel.add(urgencyBox);

    // Submit Button
    submitButton = new JButton("SUBMIT REQUEST");
    submitButton.setFont(new Font("Arial", 
                                  Font.BOLD, 14));
    submitButton.setBounds(80, 300, 180, 40);
    submitButton.setBackground(new Color(180,0,0));
    submitButton.setForeground(Color.WHITE);
    panel.add(submitButton);

    // View Button
    viewButton = new JButton("VIEW REQUESTS");
    viewButton.setFont(new Font("Arial", 
                                Font.BOLD, 14));
    viewButton.setBounds(300, 300, 180, 40);
    viewButton.setBackground(Color.DARK_GRAY);
    viewButton.setForeground(Color.WHITE);
    panel.add(viewButton);
    fulfillButton = new JButton("FULFILL");
    fulfillButton.setFont(new Font("Arial",
                                   Font.BOLD, 14));
    fulfillButton.setBounds(190, 350, 180, 35);
    fulfillButton.setBackground(
        new Color(0,120,0));
    fulfillButton.setForeground(Color.WHITE);
    panel.add(fulfillButton);

    // Table
    String[] cols = {"Hospital", "Patient",
                     "Blood", "Units", 
                     "Urgency", "Status"};
    model = new DefaultTableModel(cols, 0);
    table = new JTable(model);
    table.setFont(new Font("Arial", 
                           Font.PLAIN, 12));
    table.setRowHeight(25);
    table.getTableHeader().setFont(
      new Font("Arial", Font.BOLD, 12));
    table.getTableHeader().setBackground(
      new Color(180, 0, 0));
    table.getTableHeader().setForeground(
      Color.WHITE);

    JScrollPane scroll = new JScrollPane(table);
    scroll.setBounds(20, 400, 550, 170);
    panel.add(scroll);

    add(panel);

    submitButton.addActionListener(
      new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        submitRequest();
      }
    });

    viewButton.addActionListener(
      new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        loadRequests();
      }
    });
    fulfillButton.addActionListener(
    		  new ActionListener() {
    		  public void actionPerformed(ActionEvent e) {
    		    fulfillRequest();
    		  }
    		});

    setVisible(true);
    checkExpiryAlert();
  }

  void loadHospitals() {
    try {
      Connection con = DBConnecton.getConnection();
      String sql = "SELECT * FROM Hospitals";
      Statement st = con.createStatement();
      ResultSet rs = st.executeQuery(sql);
      while (rs.next()) {
        hospitalBox.addItem(
          rs.getInt("hospital_id") + 
          " - " + rs.getString("name"));
      }
      con.close();
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(null,
        "Error: " + ex.getMessage());
    }
  }

  void submitRequest() {
    String hospitalItem = hospitalBox
                         .getSelectedItem()
                         .toString();
    int hospitalId = Integer.parseInt(
                     hospitalItem.split(" - ")[0]);
    String patient = patientField.getText();
    String bg = bloodGroupBox
                .getSelectedItem().toString();
    String units = unitsField.getText();
    String urgency = urgencyBox
                    .getSelectedItem().toString();

    if (patient.isEmpty() || units.isEmpty()) {
      JOptionPane.showMessageDialog(null,
        "Please fill all fields!",
        "Error", JOptionPane.ERROR_MESSAGE);
      return;
    }

    try {
      Connection con = DBConnecton.getConnection();
      String sql = "INSERT INTO BloodRequests " +
                   "(hospital_id, blood_group, " +
                   "units_needed, urgency, " +
                   "patient_name, request_date) " +
                   "VALUES (?,?,?,?,?,CURDATE())";

      PreparedStatement ps = 
                        con.prepareStatement(sql);
      ps.setInt(1, hospitalId);
      ps.setString(2, bg);
      ps.setInt(3, Integer.parseInt(units));
      ps.setString(4, urgency);
      ps.setString(5, patient);
      ps.executeUpdate();
     
      JOptionPane.showMessageDialog(null,
    	        "Request Submitted Successfully! ✅",
    	        "Success",
    	        JOptionPane.INFORMATION_MESSAGE);
      patientField.setText("");
      unitsField.setText("");
      loadRequests();
      con.close();

    } catch (Exception ex) { 
      JOptionPane.showMessageDialog(null,
        "Error: " + ex.getMessage(),
        "Error", JOptionPane.ERROR_MESSAGE);
    }
  }

  void loadRequests() {
    model.setRowCount(0);
    try {
      Connection con = DBConnecton.getConnection();
      String sql = "SELECT h.name, " +
                   "r.patient_name, " +
                   "r.blood_group, " +
                   "r.units_needed, " +
                   "r.urgency, r.status " +
                   "FROM BloodRequests r " +
                   "JOIN Hospitals h " +
                   "ON r.hospital_id = " +
                   "h.hospital_id " +
                   "ORDER BY r.request_id DESC";

      Statement st = con.createStatement();
      ResultSet rs = st.executeQuery(sql);
      while (rs.next()) {
        model.addRow(new Object[]{
          rs.getString("name"),
          rs.getString("patient_name"),
          rs.getString("blood_group"),
          rs.getInt("units_needed"),
          rs.getString("urgency"),
          rs.getString("status")
        });
      }
      con.close();
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(null,
        "Error: " + ex.getMessage());
    }
  }

  void fulfillRequest() {

	  int selectedRow = table.getSelectedRow();

	  if(selectedRow == -1) {

	    JOptionPane.showMessageDialog(null,
	      "Please select a request!");

	    return;
	  }

	  try {

	    String patient =
	      model.getValueAt(selectedRow, 1)
	           .toString();

	    Connection con =
	      DBConnecton.getConnection();

	    String sql =
	      "UPDATE BloodRequests " +
	      "SET status='Fulfilled' " +
	      "WHERE patient_name=?";

	    PreparedStatement ps =
	      con.prepareStatement(sql);

	    ps.setString(1, patient);

	    ps.executeUpdate();

	    JOptionPane.showMessageDialog(null,
	      "Request Fulfilled Successfully!");

	    loadRequests();

	    con.close();

	  } catch(Exception ex) {

	    JOptionPane.showMessageDialog(null,
	      "Error: " + ex.getMessage());
	  }
	}
  void checkExpiryAlert() {

	    try {

	        Connection con =
	        DBConnecton.getConnection();

	        String sql =
	        "SELECT * FROM bloodstock " +
	        "WHERE expiry_date <= " +
	        "CURDATE() + INTERVAL 3 DAY";

	        Statement st =
	        con.createStatement();

	        ResultSet rs =
	        st.executeQuery(sql);

	        if(rs.next()) {

	            JOptionPane.showMessageDialog(null,
	            "⚠️ Blood packets are nearing expiry!\n" +
	            "Please use older blood stock first.");

	        }

	        con.close();

	    } catch(Exception ex) {

	        JOptionPane.showMessageDialog(null,
	        "Error: " + ex.getMessage());
	    }
	}

  public static void main(String[] args) {
    new HospitalRequest();
  }
}