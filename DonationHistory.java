package bloodbanksytem;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class DonationHistory extends JFrame {

  JComboBox<String> donorBox;
  JTextField unitsField, dateField;
  JButton saveButton, viewButton;
  JTable table;
  DefaultTableModel model;

  public DonationHistory() {

    setTitle("Donation History");
    setSize(600, 560);
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    setLocationRelativeTo(null);

    JPanel panel = new JPanel();
    panel.setLayout(null);
    panel.setBackground(new Color(255, 245, 245));

    // Title
    JLabel title = new JLabel("Donation History");
    title.setFont(new Font("Arial", Font.BOLD, 22));
    title.setForeground(new Color(180, 0, 0));
    title.setBounds(170, 15, 280, 35);
    panel.add(title);

    // Donor
    JLabel donorLabel = new JLabel("Select Donor:");
    donorLabel.setFont(new Font("Arial",
                                Font.BOLD, 13));
    donorLabel.setBounds(30, 70, 120, 25);
    panel.add(donorLabel);

    donorBox = new JComboBox<>();
    donorBox.setFont(new Font("Arial",
                              Font.PLAIN, 13));
    donorBox.setBounds(155, 70, 390, 28);
    panel.add(donorBox);
    loadDonors();

    // Donation Date
    JLabel dateLabel = new JLabel("Donation Date:");
    dateLabel.setFont(new Font("Arial",
                               Font.BOLD, 13));
    dateLabel.setBounds(30, 115, 120, 25);
    panel.add(dateLabel);

    dateField = new JTextField();
    dateField.setFont(new Font("Arial",
                               Font.PLAIN, 13));
    dateField.setBounds(155, 115, 200, 28);
    dateField.setText("2026-05-12");
    panel.add(dateField);

    JLabel dateHint = new JLabel(
                      "(Format: YYYY-MM-DD)");
    dateHint.setFont(new Font("Arial",
                              Font.PLAIN, 11));
    dateHint.setForeground(Color.GRAY);
    dateHint.setBounds(365, 115, 180, 25);
    panel.add(dateHint);

    // Units Donated
    JLabel unitsLabel = new JLabel("Units Donated:");
    unitsLabel.setFont(new Font("Arial",
                                Font.BOLD, 13));
    unitsLabel.setBounds(30, 160, 120, 25);
    panel.add(unitsLabel);

    unitsField = new JTextField();
    unitsField.setFont(new Font("Arial",
                                Font.PLAIN, 13));
    unitsField.setBounds(155, 160, 200, 28);
    panel.add(unitsField);

    // Save Button
    saveButton = new JButton("SAVE DONATION");
    saveButton.setFont(new Font("Arial",
                                Font.BOLD, 13));
    saveButton.setBounds(80, 210, 180, 38);
    saveButton.setBackground(new Color(180,0,0));
    saveButton.setForeground(Color.WHITE);
    panel.add(saveButton);

    // View Button
    viewButton = new JButton("VIEW HISTORY");
    viewButton.setFont(new Font("Arial",
                                Font.BOLD, 13));
    viewButton.setBounds(300, 210, 180, 38);
    viewButton.setBackground(Color.DARK_GRAY);
    viewButton.setForeground(Color.WHITE);
    panel.add(viewButton);

    // Table
    String[] cols = {
      "ID", "Donor Name",
      "Blood Group", "Units",
      "Donation Date"
    };
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
    scroll.setBounds(20, 265, 550, 230);
    panel.add(scroll);

    add(panel);

    // Save button action
    saveButton.addActionListener(
      new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        saveDonation();
      }
    });

    // View button action
    viewButton.addActionListener(
      new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        loadHistory();
      }
    });

    setVisible(true);
  }

  void loadDonors() {
    try {
      Connection con = DBConnecton.getConnection();
      ResultSet rs = con.createStatement()
        .executeQuery("SELECT * FROM Donors");
      while (rs.next()) {
        donorBox.addItem(
          rs.getInt("donor_id") +
          " - " + rs.getString("name") +
          " (" + rs.getString("blood_group")
          + ")");
      }
      con.close();
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(null,
        "Error: " + ex.getMessage());
    }
  }

  void saveDonation() {
    String donorItem = donorBox
                      .getSelectedItem()
                      .toString();
    int donorId = Integer.parseInt(
                  donorItem.split(" - ")[0]);
    String date  = dateField.getText();
    String units = unitsField.getText();

    if (date.isEmpty() || units.isEmpty()) {
      JOptionPane.showMessageDialog(null,
        "Please fill all fields!",
        "Error", JOptionPane.ERROR_MESSAGE);
      return;
    }

    try {
      Connection con = DBConnecton.getConnection();

      // Get blood group of donor
      String bgSql = "SELECT blood_group " +
                     "FROM Donors " +
                     "WHERE donor_id = ?";
      PreparedStatement bgPs =
                        con.prepareStatement(bgSql);
      bgPs.setInt(1, donorId);
      ResultSet bgRs = bgPs.executeQuery();
      String bloodGroup = "";
      if (bgRs.next()) {
        bloodGroup = bgRs.getString("blood_group");
      }

      // Save donation history
      String sql = "INSERT INTO DonationHistory " +
                   "(donor_id, donation_date, " +
                   "units_donated, blood_group) " +
                   "VALUES (?, ?, ?, ?)";
      PreparedStatement ps =
                        con.prepareStatement(sql);
      ps.setInt(1, donorId);
      ps.setString(2, date);
      ps.setInt(3, Integer.parseInt(units));
      ps.setString(4, bloodGroup);
      ps.executeUpdate();

      // Update blood stock
      String stockSql = "UPDATE BloodStock " +
                        "SET units_available = " +
                        "units_available + ? " +
                        "WHERE blood_group = ? " +
                        "AND status = 'Available'";
      PreparedStatement stockPs =
                        con.prepareStatement(
                                       stockSql);
      stockPs.setInt(1, Integer.parseInt(units));
      stockPs.setString(2, bloodGroup);
      stockPs.executeUpdate();

      JOptionPane.showMessageDialog(null,
        "Donation Saved Successfully! ✅\n" +
        "Blood Stock Updated! 🩸",
        "Success",
        JOptionPane.INFORMATION_MESSAGE);

      unitsField.setText("");
      loadHistory();
      con.close();

    } catch (Exception ex) {
      JOptionPane.showMessageDialog(null,
        "Error: " + ex.getMessage(),
        "Error", JOptionPane.ERROR_MESSAGE);
    }
  }

  void loadHistory() {
    model.setRowCount(0);
    try {
      Connection con = DBConnecton.getConnection();
      String sql =
        "SELECT dh.donation_id, " +
        "d.name, dh.blood_group, " +
        "dh.units_donated, " +
        "dh.donation_date " +
        "FROM DonationHistory dh " +
        "JOIN Donors d " +
        "ON dh.donor_id = d.donor_id " +
        "ORDER BY dh.donation_date DESC";

      ResultSet rs = con.createStatement()
                       .executeQuery(sql);
      while (rs.next()) {
        model.addRow(new Object[]{
          rs.getInt("donation_id"),
          rs.getString("name"),
          rs.getString("blood_group"),
          rs.getInt("units_donated"),
          rs.getString("donation_date")
        });
      }
      con.close();
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(null,
        "Error: " + ex.getMessage());
    }
  }

  public static void main(String[] args) {
    new DonationHistory();
  }
}