package bloodbanksytem;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class BloodStock extends JFrame {

  JTable table;
  DefaultTableModel model;
  JButton refreshButton;

  public BloodStock() {

    setTitle("Blood Stock Dashboard");
    setSize(600, 450);
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    setLocationRelativeTo(null);

    JPanel panel = new JPanel();
    panel.setLayout(null);
    panel.setBackground(new Color(255, 245, 245));

    // Title
    JLabel title = new JLabel(
                   "Blood Stock Dashboard");
    title.setFont(new Font("Arial", Font.BOLD, 22));
    title.setForeground(new Color(180, 0, 0));
    title.setBounds(140, 20, 350, 35);
    panel.add(title);

    // Table columns
    String[] columns = {
      "Blood Group",
      "Units Available",
      "Expiry Date",
      "Status"
    };

    model = new DefaultTableModel(columns, 0);
    table = new JTable(model) {
      public Component prepareRenderer(
        TableCellRenderer r, int row, int col) {
        Component c = super.prepareRenderer(
                                     r, row, col);
        String status = (String) getValueAt(
                                      row, 3);
        if (status != null) {
          if (status.equals("Expired"))
        	  c.setBackground(Color.pink);
          else if (status.equals("Out of Stock"))
            c.setBackground(Color.RED);
          else if (status.equals("Critical"))
            c.setBackground(Color.ORANGE);
          else if (status.equals("Low"))
            c.setBackground(Color.YELLOW);
          else
            c.setBackground(Color.GREEN);
        }
        return c;
      }
    };

    table.setFont(new Font("Arial", 
                           Font.PLAIN, 13));
    table.setRowHeight(30);
    table.getTableHeader().setFont(
      new Font("Arial", Font.BOLD, 13));
    table.getTableHeader().setBackground(
      new Color(180, 0, 0));
    table.getTableHeader().setForeground(
      Color.WHITE);

    JScrollPane scroll = new JScrollPane(table);
    scroll.setBounds(30, 70, 530, 280);
    panel.add(scroll);

    // Refresh Button
    refreshButton = new JButton("REFRESH");
    refreshButton.setFont(
      new Font("Arial", Font.BOLD, 14));
    refreshButton.setBounds(220, 370, 150, 40);
    refreshButton.setBackground(
      new Color(180, 0, 0));
    refreshButton.setForeground(Color.WHITE);
    panel.add(refreshButton);

    add(panel);

    // Load data on open
    loadBloodStock();
 // Auto refresh every 5 seconds
    Timer timer = new Timer(5000, new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            loadBloodStock();
        }
    });
    timer.start();

    // Refresh button action
    refreshButton.addActionListener(
      new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        loadBloodStock();
        
      }
    });

    setVisible(true);
  }

  void loadBloodStock() {
    model.setRowCount(0);

    try {
      Connection con = DBConnecton.getConnection();
      String sql =

    		  "SELECT blood_group, " +

    		  "SUM(CASE " +
    		  "WHEN expiry_date >= CURDATE() " +
    		  "THEN units_available " +
    		  "ELSE 0 END) as units, " +

    		  "MAX(CASE " +
    		  "WHEN expiry_date >= CURDATE() " +
    		  "THEN expiry_date " +
    		  "END) as expiry, " +

    		  "CASE " +

    		  "WHEN SUM(CASE " +
    		  "WHEN expiry_date >= CURDATE() " +
    		  "THEN units_available " +
    		  "ELSE 0 END)=0 " +
    		  "THEN 'Out of Stock' " +

    		  "WHEN SUM(CASE " +
    		  "WHEN expiry_date >= CURDATE() " +
    		  "THEN units_available " +
    		  "ELSE 0 END)<=2 " +
    		  "THEN 'Critical' " +

    		  "WHEN SUM(CASE " +
    		  "WHEN expiry_date >= CURDATE() " +
    		  "THEN units_available " +
    		  "ELSE 0 END)<=5 " +
    		  "THEN 'Low' " +

    		  "ELSE 'Available' END as status " +

    		  "FROM BloodStock " +

    		  "GROUP BY blood_group " +

    		  "ORDER BY MAX(collected_date) DESC";
      Statement st = con.createStatement();
      ResultSet rs = st.executeQuery(sql);

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
        "Error: " + ex.getMessage(),
        "Error", JOptionPane.ERROR_MESSAGE);
    }
  }

  public static void main(String[] args) {
    new BloodStock();
  }
}