package bloodbanksytem;

import javax.swing.*;
import java.awt.event.*;
import java.sql.*;

public class BloodCompatibility extends JFrame {

    public BloodCompatibility() {

        String donor = JOptionPane.showInputDialog(
                "Enter Donor Blood Group:");

        String receiver = JOptionPane.showInputDialog(
                "Enter Receiver Blood Group:");

        try {

            Class.forName(
            "com.mysql.cj.jdbc.Driver");

            Connection con = 
                    DBConnecton.getConnection();

            String query =
            "SELECT * FROM blood_compatibility " +
            "WHERE donor_blood=? " +
            "AND receiver_blood=?";

            PreparedStatement pst =
            con.prepareStatement(query);

            pst.setString(1, donor);
            pst.setString(2, receiver);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {

                JOptionPane.showMessageDialog(
                null,
                "Compatible Blood Group ✅");

            } else {

                JOptionPane.showMessageDialog(
                null,
                "Not Compatible ❌");
            }

            con.close();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
            null,
            e.getMessage());
        }
    }
}