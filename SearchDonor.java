package bloodbanksytem;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class SearchDonor extends JFrame {

    JComboBox<String> bloodGroupBox;
    JTextField locationField;

    JTable table;
    DefaultTableModel model;

    JButton searchButton;
    JButton refreshButton;

    JLabel resultLabel;

    public SearchDonor() {

        setTitle(" Donor Search");
        setSize(750, 550);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(255, 245, 245));

        // ================= TITLE =================

        JLabel title = new JLabel("Blood Donor Search");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(new Color(200, 0, 0));
        title.setBounds(250, 20, 400, 35);
        panel.add(title);

        // ================= BLOOD GROUP =================

        JLabel bgLabel = new JLabel("Blood Group:");
        bgLabel.setFont(new Font("Arial", Font.BOLD, 14));
        bgLabel.setBounds(40, 90, 120, 25);
        panel.add(bgLabel);

        String[] bgs = {
            "All", "A+", "A-", "B+",
            "B-", "O+", "O-",
            "AB+", "AB-"
        };

        bloodGroupBox = new JComboBox<>(bgs);
        bloodGroupBox.setFont(
            new Font("Arial", Font.PLAIN, 14));
        bloodGroupBox.setBounds(150, 90, 180, 30);
        panel.add(bloodGroupBox);

        // ================= LOCATION =================

        JLabel locLabel = new JLabel("Location:");
        locLabel.setFont(new Font("Arial", Font.BOLD, 14));
        locLabel.setBounds(370, 90, 100, 25);
        panel.add(locLabel);

        locationField = new JTextField();
        locationField.setFont(
            new Font("Arial", Font.PLAIN, 14));
        locationField.setBounds(450, 90, 180, 30);
        panel.add(locationField);

        // ================= SEARCH BUTTON =================

        searchButton = new JButton("SEARCH");
        searchButton.setFont(
            new Font("Arial", Font.BOLD, 13));
        searchButton.setBounds(150, 140, 140, 35);
        searchButton.setBackground(
            new Color(180, 0, 0));
        searchButton.setForeground(Color.WHITE);
        panel.add(searchButton);

        // ================= REFRESH BUTTON =================

        refreshButton = new JButton("REFRESH");
        refreshButton.setFont(
            new Font("Arial", Font.BOLD, 13));
        refreshButton.setBounds(330, 140, 140, 35);
        refreshButton.setBackground(
            new Color(0, 120, 215));
        refreshButton.setForeground(Color.WHITE);
        panel.add(refreshButton);

        // ================= RESULT LABEL =================

        resultLabel = new JLabel("");
        resultLabel.setFont(
            new Font("Arial", Font.BOLD, 13));
        resultLabel.setForeground(
            new Color(180, 0, 0));
        resultLabel.setBounds(40, 185, 600, 25);
        panel.add(resultLabel);

        // ================= TABLE =================

        String[] cols = {
            "ID",
            "Name",
            "Blood Group",
            "Age",
            "Phone",
            "Location",
            "Eligible",
            "Total Donations"
        };

        model = new DefaultTableModel(cols, 0);

        table = new JTable(model);

        table.setFont(
            new Font("Arial", Font.PLAIN, 12));

        table.setRowHeight(25);

        table.getTableHeader().setFont(
            new Font("Arial", Font.BOLD, 12));

        table.getTableHeader().setBackground(
            new Color(180, 0, 0));

        table.getTableHeader().setForeground(
            Color.WHITE);

        // SORTING FEATURE
        table.setAutoCreateRowSorter(true);

        // ROW COLORING
        table.setDefaultRenderer(
            Object.class,
            new DefaultTableCellRenderer() {

            public Component getTableCellRendererComponent(
                    JTable table,
                    Object value,
                    boolean isSelected,
                    boolean hasFocus,
                    int row,
                    int column) {

                Component c =
                    super.getTableCellRendererComponent(
                        table,
                        value,
                        isSelected,
                        hasFocus,
                        row,
                        column);

                String eligible =
                    table.getValueAt(row, 6).toString();

                if (eligible.contains("Yes")) {
                    c.setBackground(
                        new Color(220, 255, 220));
                } else {
                    c.setBackground(
                        new Color(255, 220, 220));
                }

                return c;
            }
        });

        JScrollPane scroll =
            new JScrollPane(table);

        scroll.setBounds(20, 220, 690, 250);

        panel.add(scroll);

        add(panel);

        // ================= LOAD ALL DONORS =================

        searchDonors("All", "");

        // ================= SEARCH BUTTON ACTION =================

        searchButton.addActionListener(
            new ActionListener() {

            public void actionPerformed(
                    ActionEvent e) {

                String bg =
                    bloodGroupBox
                    .getSelectedItem()
                    .toString();

                String loc =
                    locationField
                    .getText()
                    .trim();

                searchDonors(bg, loc);
            }
        });

        // ================= REFRESH BUTTON ACTION =================

        refreshButton.addActionListener(
            new ActionListener() {

            public void actionPerformed(
                    ActionEvent e) {

                bloodGroupBox.setSelectedIndex(0);
                locationField.setText("");

                searchDonors("All", "");
            }
        });

        // ================= DOUBLE CLICK EVENT =================

        table.addMouseListener(
            new MouseAdapter() {

            public void mouseClicked(
                    MouseEvent e) {

                if (e.getClickCount() == 2) {

                    int row =
                        table.getSelectedRow();

                    String details =
                        "Donor ID: "
                        + table.getValueAt(row,0)
                        + "\nName: "
                        + table.getValueAt(row,1)
                        + "\nBlood Group: "
                        + table.getValueAt(row,2)
                        + "\nAge: "
                        + table.getValueAt(row,3)
                        + "\nPhone: "
                        + table.getValueAt(row,4)
                        + "\nLocation: "
                        + table.getValueAt(row,5)
                        + "\nEligible: "
                        + table.getValueAt(row,6)
                        + "\nTotal Donations: "
                        + table.getValueAt(row,7);

                    JOptionPane.showMessageDialog(
                        null,
                        details,
                        "Donor Details",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                }
            }
        });

        setVisible(true);
    }

    // ================= SEARCH METHOD =================

    void searchDonors(
            String bloodGroup,
            String location) {

        model.setRowCount(0);

        try {

            Connection con =
                DBConnecton.getConnection();

            String sql;

            if (bloodGroup.equals("All")
                    && location.isEmpty()) {

                sql =
                    "SELECT * FROM Donors " +
                    "ORDER BY blood_group";

            }

            else if (!bloodGroup.equals("All")
                    && location.isEmpty()) {

                sql =
                    "SELECT * FROM Donors " +
                    "WHERE blood_group=? " +
                    "ORDER BY is_eligible DESC";

            }

            else if (bloodGroup.equals("All")
                    && !location.isEmpty()) {

                sql =
                    "SELECT * FROM Donors " +
                    "WHERE location=?";

            }

            else {

                sql =
                    "SELECT * FROM Donors " +
                    "WHERE blood_group=? " +
                    "AND location=? " +
                    "ORDER BY is_eligible DESC";
            }

            PreparedStatement ps =
                con.prepareStatement(sql);

            // PARAMETERS

            if (!bloodGroup.equals("All")
                    && location.isEmpty()) {

                ps.setString(1, bloodGroup);

            }

            else if (bloodGroup.equals("All")
                    && !location.isEmpty()) {

                ps.setString(1, location);

            }

            else if (!bloodGroup.equals("All")
                    && !location.isEmpty()) {

                ps.setString(1, bloodGroup);
                ps.setString(2, location);

            }

            ResultSet rs =
                ps.executeQuery();

            int count = 0;

            while (rs.next()) {

            	Date lastDonation =
            		    rs.getDate("last_donation_date");

            		boolean eligibleStatus = false;

            		if (lastDonation != null) {

            		    long diff =
            		        System.currentTimeMillis()
            		        - lastDonation.getTime();

            		    long days =
            		        diff / (1000 * 60 * 60 * 24);

            		    if (days >= 90) {
            		        eligibleStatus = true;
            		    }
            		}

            		String eligible =
            		    eligibleStatus
            		    ? "✅ Yes"
            		    : "❌ No";

                model.addRow(new Object[] {

                    rs.getInt("donor_id"),

                    rs.getString("name"),

                    rs.getString("blood_group"),

                    rs.getInt("age"),

                    rs.getString("phone"),

                    rs.getString("location"),

                    eligible,

                    rs.getInt("total_donations")
                });

                count++;
            }

            resultLabel.setText(
                "Found "
                + count
                + " donor(s)");

            con.close();

        }

        catch (Exception ex) {

            JOptionPane.showMessageDialog(
                null,
                "Error: " + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // ================= MAIN METHOD =================

    public static void main(String[] args) {

        new SearchDonor();
    }
}