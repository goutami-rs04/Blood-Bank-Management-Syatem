package bloodbanksytem;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class AdminPanel extends JFrame {

  JTabbedPane tabs;

  public AdminPanel() {

    setTitle("Admin Panel - Blood Bank");
    setSize(700, 550);
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    setLocationRelativeTo(null);

    JPanel panel = new JPanel();
    panel.setLayout(null);
    panel.setBackground(new Color(255, 245, 245));

    // Title
    JLabel title = new JLabel("Admin Panel");
    title.setFont(new Font("Arial", Font.BOLD, 22));
    title.setForeground(new Color(180, 0, 0));
    title.setBounds(260, 15, 250, 35);
    panel.add(title);

    // Tabbed Pane
    tabs = new JTabbedPane();
    tabs.setFont(new Font("Arial", Font.BOLD, 13));
    tabs.setBounds(20, 60, 650, 430);

    // Add all tabs
    tabs.addTab("All Donors", createDonorsPanel());
    tabs.addTab("Blood Stock", createStockPanel());
    tabs.addTab("All Requests", 
                createRequestsPanel());
    tabs.addTab("All Staff", createStaffPanel());

    panel.add(tabs);
    add(panel);
    setVisible(true);
  }

  // Tab 1 - All Donors
  JPanel createDonorsPanel() {
    JPanel p = new JPanel(null);
    p.setBackground(Color.WHITE);

    String[] cols = {"ID", "Name", 
                     "Blood Group", "Age",
                     "Phone", "Eligible","Last Donation"};
    DefaultTableModel model = 
                      new DefaultTableModel(
                                       cols, 0);
    JTable table = new JTable(model);
    table.setFont(new Font("Arial", 
                           Font.PLAIN, 12));
    table.setRowHeight(25);
    table.getTableHeader().setBackground(
      new Color(180, 0, 0));
    table.getTableHeader().setForeground(
      Color.WHITE);

    JScrollPane scroll = new JScrollPane(table);
    scroll.setBounds(10, 10, 620, 320);
    p.add(scroll);

    JButton loadBtn = new JButton("LOAD DONORS");
    loadBtn.setFont(new Font("Arial", 
                             Font.BOLD, 13));
    loadBtn.setBounds(230, 345, 160, 35);
    loadBtn.setBackground(new Color(180, 0, 0));
    loadBtn.setForeground(Color.WHITE);
    p.add(loadBtn);

    loadBtn.addActionListener(e -> {
      model.setRowCount(0);
      try {
        Connection con = 
                  DBConnecton.getConnection();
        ResultSet rs = con.createStatement()
          .executeQuery("SELECT * FROM Donors");
        while (rs.next()) {

            java.sql.Date lastDate =
                rs.getDate("last_donation_date");

            long days = 0;

            if (lastDate != null) {

                long diff =
                    System.currentTimeMillis()
                    - lastDate.getTime();

                days = diff / (1000 * 60 * 60 * 24);
            }

            int eligibleValue;

            if (days >= 90) {
                eligibleValue = 1;
            } else {
                eligibleValue = 0;
            }

            PreparedStatement pst =
                con.prepareStatement(
                "UPDATE Donors SET is_eligible=? WHERE donor_id=?"
            );

            pst.setInt(1, eligibleValue);
            pst.setInt(2, rs.getInt("donor_id"));

            pst.executeUpdate();

            model.addRow(new Object[]{
                rs.getInt("donor_id"),
                rs.getString("name"),
                rs.getString("blood_group"),
                rs.getInt("age"),
                rs.getString("phone"),
                eligibleValue == 1 ?
                "✅ Yes" : "❌ No",
                rs.getDate("last_donation_date")
            });
        }        con.close();
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(null,
          "Error: " + ex.getMessage());
      }
    });

    return p;
  }

  // Tab 2 - Blood Stock
  JPanel createStockPanel() {
    JPanel p = new JPanel(null);
    p.setBackground(Color.WHITE);

    String[] cols = {"Blood Group", 
                     "Units", "Expiry", 
                     "Status"};
    DefaultTableModel model = 
                      new DefaultTableModel(
                                       cols, 0);
    JTable table = new JTable(model);
    table.setFont(new Font("Arial", 
                           Font.PLAIN, 12));
    table.setRowHeight(25);
    table.getTableHeader().setBackground(
      new Color(180, 0, 0));
    table.getTableHeader().setForeground(
      Color.WHITE);

    JScrollPane scroll = new JScrollPane(table);
    scroll.setBounds(10, 10, 620, 320);
    p.add(scroll);

    JButton loadBtn = new JButton("LOAD STOCK");
    loadBtn.setFont(new Font("Arial", 
                             Font.BOLD, 13));
    loadBtn.setBounds(230, 345, 160, 35);
    loadBtn.setBackground(new Color(180, 0, 0));
    loadBtn.setForeground(Color.WHITE);
    p.add(loadBtn);

    loadBtn.addActionListener(e -> {
      model.setRowCount(0);
      try {
        Connection con = 
                  DBConnecton.getConnection();
        String sql = 
          "SELECT blood_group, " +
          "SUM(units_available) as units, " +
          "MIN(expiry_date) as expiry, " +
          "CASE " +
          "WHEN SUM(units_available)=0 " +
          "THEN 'Out of Stock' " +
          "WHEN SUM(units_available)<=2 " +
          "THEN 'Critical' " +
          "WHEN SUM(units_available)<=5 " +
          "THEN 'Low' " +
          "ELSE 'Good' END as status " +
          "FROM BloodStock " +
          "WHERE status='Available' " +
          "GROUP BY blood_group";
        ResultSet rs = con.createStatement()
                         .executeQuery(sql);
        while (rs.next()) {
          model.addRow(new Object[]{
            rs.getString("blood_group"),
            rs.getInt("units"),
            rs.getString("expiry"),
            rs.getString("status")
          });
        }
        con.close();
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(null,
          "Error: " + ex.getMessage());
      }
    });

    return p;
  }

  // Tab 3 - All Requests
  JPanel createRequestsPanel() {
    JPanel p = new JPanel(null);
    p.setBackground(Color.WHITE);

    String[] cols = {"ID", "Hospital", 
                     "Blood", "Units",
                     "Urgency", "Status"};
    DefaultTableModel model = 
                      new DefaultTableModel(
                                       cols, 0);
    JTable table = new JTable(model);
    table.setFont(new Font("Arial", 
                           Font.PLAIN, 12));
    table.setRowHeight(25);
    table.getTableHeader().setBackground(
      new Color(180, 0, 0));
    table.getTableHeader().setForeground(
      Color.WHITE);

    JScrollPane scroll = new JScrollPane(table);
    scroll.setBounds(10, 10, 620, 320);
    p.add(scroll);

    JButton loadBtn = new JButton(
                      "LOAD REQUESTS");
    loadBtn.setFont(new Font("Arial", 
                             Font.BOLD, 13));
    loadBtn.setBounds(230, 345, 160, 35);
    loadBtn.setBackground(new Color(180, 0, 0));
    loadBtn.setForeground(Color.WHITE);
    p.add(loadBtn);

    loadBtn.addActionListener(e -> {
      model.setRowCount(0);
      try {
        Connection con = 
                  DBConnecton.getConnection();
        String sql = 
          "SELECT r.request_id, h.name, " +
          "r.blood_group, r.units_needed, " +
          "r.urgency, r.status " +
          "FROM BloodRequests r " +
          "JOIN Hospitals h " +
          "ON r.hospital_id=h.hospital_id";
        ResultSet rs = con.createStatement()
                         .executeQuery(sql);
        while (rs.next()) {
          model.addRow(new Object[]{
            rs.getInt("request_id"),
            rs.getString("name"),
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
    });

    return p;
  }

  // Tab 4 - All Staff
  JPanel createStaffPanel() {
    JPanel p = new JPanel(null);
    p.setBackground(Color.WHITE);

    String[] cols = {"ID", "Name", 
                     "Role", "Phone"};
    DefaultTableModel model = 
                      new DefaultTableModel(
                                       cols, 0);
    JTable table = new JTable(model);
    table.setFont(new Font("Arial", 
                           Font.PLAIN, 12));
    table.setRowHeight(25);
    table.getTableHeader().setBackground(
      new Color(180, 0, 0));
    table.getTableHeader().setForeground(
      Color.WHITE);

    JScrollPane scroll = new JScrollPane(table);
    scroll.setBounds(10, 10, 620, 320);
    p.add(scroll);

    JButton loadBtn = new JButton("LOAD STAFF");
    loadBtn.setFont(new Font("Arial", 
                             Font.BOLD, 13));
    loadBtn.setBounds(230, 345, 160, 35);
    loadBtn.setBackground(new Color(180, 0, 0));
    loadBtn.setForeground(Color.WHITE);
    p.add(loadBtn);

    loadBtn.addActionListener(e -> {
      model.setRowCount(0);
      try {
        Connection con = 
                  DBConnecton.getConnection();
        ResultSet rs = con.createStatement()
          .executeQuery("SELECT * FROM Staff");
        while (rs.next()) {
          model.addRow(new Object[]{
            rs.getInt("staff_id"),
            rs.getString("name"),
            rs.getString("role"),
            rs.getString("phone")
          });
        }
        con.close();
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(null,
          "Error: " + ex.getMessage());
      }
    });

    return p;
  }

  public static void main(String[] args) {
    new AdminPanel();
  }
}