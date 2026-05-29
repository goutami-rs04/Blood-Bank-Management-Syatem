package bloodbanksytem;
 
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
 
public class Dashboard extends JFrame {
 
  String userName;
  String userRole;
  JLabel timeLabel, dateLabel;
  JLabel donorCountLabel;
  JLabel bloodUnitsLabel;
  JLabel requestCountLabel;
  JPanel mainPanel;
  JPanel contentPanel;
 
  Color darkBg    = new Color(13, 13, 13);
  Color sidebarBg = new Color(20, 20, 20);
  Color cardBg    = new Color(30, 30, 30);
  Color redColor  = new Color(200, 0, 0);
  Color textWhite = Color.WHITE;
  Color textGray  = new Color(150, 150, 150);
 
  public Dashboard(String name, String role) {
    this.userName = name;
    this.userRole = role;
 
    setTitle("Blood Bank Management System");
    setSize(1200, 750);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setLocationRelativeTo(null);
    setResizable(true);
 
    mainPanel = new JPanel(new BorderLayout());
    mainPanel.setBackground(darkBg);
 
    mainPanel.add(createSidebar(), BorderLayout.WEST);
 
    contentPanel = createContent();
    mainPanel.add(contentPanel, BorderLayout.CENTER);
 
    add(mainPanel);
 
    startClock();
    try {
        Connection con = DBConnecton.getConnection();
        con.createStatement().executeUpdate(
            "UPDATE BloodStock SET status = 'Expired' " +
            "WHERE expiry_date < CURDATE() AND status = 'Available'"
        );
        con.close();
    } catch (Exception ex) {
        ex.printStackTrace();
    }
    loadStats();
    checkExpiryAlert();
 
    setVisible(true);
  }
 
  // ─── LOAD ICON — Works in Eclipse AND JAR! ───
  ImageIcon loadIcon(String name, int width, int height) {
    try {
      // Try loading from JAR/classpath first
      java.io.InputStream stream =
        getClass().getResourceAsStream("/icons/" + name);
 
      if (stream == null) {
        // Fallback for Eclipse run
        stream = getClass().getResourceAsStream("icons/" + name);
      }
 
      if (stream == null) {
        // Last fallback — file path for Eclipse
        java.io.File f = new java.io.File("src/icons/" + name);
        if (f.exists()) {
          stream = new java.io.FileInputStream(f);
        }
      }
 
      if (stream != null) {
        byte[] bytes = stream.readAllBytes();
        stream.close();
        Image img = new ImageIcon(bytes)
          .getImage()
          .getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
      }
 
    } catch (Exception e) {
      // silently ignore — return empty icon
    }
    // Return blank icon if not found
    return new ImageIcon(
      new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB));
  }
 
  // ─── SIDEBAR ─────────────────────────────────
  JPanel createSidebar() {
    JPanel sidebar = new JPanel();
    sidebar.setLayout(null);
    sidebar.setBackground(sidebarBg);
    sidebar.setPreferredSize(new Dimension(220, 750));
 
    // Logo area
    JPanel logoPanel = new JPanel(null);
    logoPanel.setBackground(redColor);
    logoPanel.setBounds(0, 0, 220, 90);
    sidebar.add(logoPanel);
 
    // Try loading logo icon
    ImageIcon logoImg = loadIcon("blood.png", 40, 40);
    JLabel logoIcon;
    if (logoImg.getIconWidth() > 1) {
      logoIcon = new JLabel(logoImg);
    } else {
      logoIcon = new JLabel("🩸");
      logoIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 30));
    }
    logoIcon.setBounds(10, 22, 45, 45);
    logoPanel.add(logoIcon);
 
    JLabel logoTitle = new JLabel("BLOOD BANK");
    logoTitle.setFont(new Font("Arial", Font.BOLD, 16));
    logoTitle.setForeground(Color.WHITE);
    logoTitle.setBounds(60, 20, 150, 25);
    logoPanel.add(logoTitle);
 
    JLabel logoSub = new JLabel("Management System");
    logoSub.setFont(new Font("Arial", Font.PLAIN, 11));
    logoSub.setForeground(new Color(255, 200, 200));
    logoSub.setBounds(60, 45, 150, 20);
    logoPanel.add(logoSub);
 
    // Menu items: {iconFileName, menuName}
    String[][] menus = {
      {"dashboard.png", "Dashboard"},
      {"donor.png",     "Donor Registration"},
      {"search.png",    "Search Donor"},
      {"blood.png",     "Blood Stock"},
      {"hospital.png",  "Hospital Request"},
      {"history.png",   "Donation History"},
      {"dna.png",       "Compatibility"},
      {"admin.png",     "Admin Panel"},
      {"logout.png",    "Logout"}
    };
 
    int yPos = 100;
    for (int i = 0; i < menus.length; i++) {
      final String iconFile = menus[i][0];
      final String menuName = menus[i][1];
      final boolean isLogout = menuName.equals("Logout");
      final int idx = i;
 
      JPanel menuItem = new JPanel(null);
      menuItem.setBounds(0, yPos, 220, 50);
      menuItem.setBackground(i == 0 ? redColor : sidebarBg);
      menuItem.setCursor(new Cursor(Cursor.HAND_CURSOR));
 
      // Load icon
      ImageIcon mIcon = loadIcon(iconFile, 20, 20);
      JLabel menuIcon;
      if (mIcon.getIconWidth() > 1) {
        menuIcon = new JLabel(mIcon);
      } else {
        // Fallback text icon
        String[] fallback = {"H","D","S","B","HR","DH","C","A","X"};
        menuIcon = new JLabel(fallback[i]);
        menuIcon.setForeground(Color.WHITE);
        menuIcon.setFont(new Font("Arial", Font.BOLD, 12));
      }
      menuIcon.setBounds(15, 12, 25, 25);
      menuItem.add(menuIcon);
 
      JLabel menuText = new JLabel(menuName);
      menuText.setFont(new Font("Arial", Font.BOLD, 13));
      menuText.setForeground(isLogout ? redColor : textWhite);
      menuText.setBounds(50, 15, 160, 20);
      menuItem.add(menuText);
 
      menuItem.addMouseListener(new MouseAdapter() {
        public void mouseClicked(MouseEvent e) {
          handleMenu(menuName);
        }
        public void mouseEntered(MouseEvent e) {
          if (idx != 0)
            menuItem.setBackground(new Color(40, 40, 40));
        }
        public void mouseExited(MouseEvent e) {
          if (idx != 0)
            menuItem.setBackground(sidebarBg);
        }
      });
 
      sidebar.add(menuItem);
      yPos += 50;
    }
 
    return sidebar;
  }
 
  // ─── CONTENT AREA ────────────────────────────
  JPanel createContent() {
    JPanel content = new JPanel(null);
    content.setBackground(darkBg);
 
    // Top bar
    JPanel topBar = new JPanel(null);
    topBar.setBackground(darkBg);
    topBar.setBounds(0, 0, 980, 80);
    content.add(topBar);
 
    JLabel welcomeLabel = new JLabel("Welcome Back, ");
    welcomeLabel.setFont(new Font("Arial", Font.BOLD, 26));
    welcomeLabel.setForeground(textWhite);
    welcomeLabel.setBounds(20, 15, 200, 35);
    topBar.add(welcomeLabel);
 
    JLabel nameLabel = new JLabel(userName);
    nameLabel.setFont(new Font("Arial", Font.BOLD, 26));
    nameLabel.setForeground(redColor);
    nameLabel.setBounds(220, 15, 250, 35);
    topBar.add(nameLabel);
 
    JLabel roleLabel = new JLabel("Role : " + userRole);
    roleLabel.setFont(new Font("Arial", Font.PLAIN, 13));
    roleLabel.setForeground(textGray);
    roleLabel.setBounds(20, 50, 200, 20);
    topBar.add(roleLabel);
 
    dateLabel = new JLabel();
    dateLabel.setFont(new Font("Arial", Font.PLAIN, 13));
    dateLabel.setForeground(textWhite);
    dateLabel.setBounds(730, 20, 220, 20);
    topBar.add(dateLabel);
 
    timeLabel = new JLabel();
    timeLabel.setFont(new Font("Arial", Font.PLAIN, 13));
    timeLabel.setForeground(textWhite);
    timeLabel.setBounds(730, 45, 220, 20);
    topBar.add(timeLabel);
 
    // ─── STAT CARD 1 — Total Donors ──────────
    JPanel card1 = createStatCard(
      "donor.png", "Total Donors",
      "+25 this month", new Color(80, 0, 120), 20);
    content.add(card1);
 
    donorCountLabel = new JLabel("0");
    donorCountLabel.setFont(new Font("Arial", Font.BOLD, 32));
    donorCountLabel.setForeground(textWhite);
    donorCountLabel.setBounds(75, 45, 150, 40);
    card1.add(donorCountLabel);
 
    // ─── STAT CARD 2 — Blood Units ───────────
    JPanel card2 = createStatCard(
      "blood.png", "Blood Units",
      "+18 this month", new Color(140, 0, 0), 330);
    content.add(card2);
 
    bloodUnitsLabel = new JLabel("0");
    bloodUnitsLabel.setFont(new Font("Arial", Font.BOLD, 32));
    bloodUnitsLabel.setForeground(textWhite);
    bloodUnitsLabel.setBounds(75, 45, 150, 40);
    card2.add(bloodUnitsLabel);
 
    // ─── STAT CARD 3 — Requests ──────────────
    JPanel card3 = createStatCard(
      "hospital.png", "Requests",
      "Pending", new Color(0, 50, 120), 640);
    content.add(card3);
 
    requestCountLabel = new JLabel("0");
    requestCountLabel.setFont(new Font("Arial", Font.BOLD, 32));
    requestCountLabel.setForeground(textWhite);
    requestCountLabel.setBounds(75, 45, 150, 40);
    card3.add(requestCountLabel);
 
    // ─── BLOOD STOCK BARS ────────────────────
    JPanel stockPanel = new JPanel(null);
    stockPanel.setBackground(cardBg);
    stockPanel.setBounds(20, 280, 580, 420);
    stockPanel.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60)));
    content.add(stockPanel);
 
    JLabel stockTitle = new JLabel("  Blood Stock Overview");
    stockTitle.setFont(new Font("Arial", Font.BOLD, 16));
    stockTitle.setForeground(redColor);
    stockTitle.setBounds(15, 10, 300, 30);
    stockPanel.add(stockTitle);
 
    // Load real stock data from DB
    String[] groups = {
    	    "A-", "AB+", "B-", "B+",
    	    "O+", "AB-", "A+", "O-"
    	};

    	int[] values = new int[groups.length];

    	try {
    	    Connection con = DBConnecton.getConnection();

    	    ResultSet rs = con.createStatement().executeQuery(
    	        "SELECT blood_group, SUM(units_available) AS units " +
    	        "FROM BloodStock " +
    	        "WHERE status='Available' " +
    	        "GROUP BY blood_group"
    	    );

    	    while (rs.next()) {

    	        String bg = rs.getString("blood_group");
    	        int units = rs.getInt("units");

    	        for (int i = 0; i < groups.length; i++) {

    	            if (groups[i].equals(bg)) {
    	                values[i] = units;
    	                break;
    	            }
    	        }
    	    }

    	    con.close();

    	} catch (Exception ex) {
    	    ex.printStackTrace();
    	}
    int barY = 50;
    int maxVal = 100;
    for (int i = 0; i < groups.length; i++) {
      JLabel groupLabel = new JLabel(groups[i]);
      groupLabel.setFont(new Font("Arial", Font.BOLD, 13));
      groupLabel.setForeground(textWhite);
      groupLabel.setBounds(15, barY, 40, 20);
      stockPanel.add(groupLabel);
 
      JProgressBar progressBar = new JProgressBar(0, maxVal);
      progressBar.setValue(Math.min(values[i], maxVal));
      progressBar.setBounds(60, barY, 390, 15);
      progressBar.setBackground(new Color(50, 50, 50));
      progressBar.setForeground(redColor);
      progressBar.setBorderPainted(false);
      progressBar.setStringPainted(false);
      stockPanel.add(progressBar);
 
      JLabel valueLabel = new JLabel(values[i] + " Units");
      valueLabel.setFont(new Font("Arial", Font.PLAIN, 12));
      valueLabel.setForeground(textGray);
      valueLabel.setBounds(460, barY, 100, 20);
      stockPanel.add(valueLabel);
 
      barY += 45;
    }
 
    JButton viewStockBtn = new JButton("View Full Stock");
    viewStockBtn.setFont(new Font("Arial", Font.BOLD, 12));
    viewStockBtn.setBounds(200, 390, 150, 25);
    viewStockBtn.setBackground(darkBg);
    viewStockBtn.setForeground(redColor);
    viewStockBtn.setBorder(BorderFactory.createLineBorder(redColor));
    viewStockBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    stockPanel.add(viewStockBtn);
    viewStockBtn.addActionListener(e -> new BloodStock());
 
    // ─── EXPIRY ALERT PANEL ──────────────────
    JPanel expiryPanel = new JPanel(null);
    expiryPanel.setBackground(cardBg);
    expiryPanel.setBounds(620, 270, 330, 170);
    expiryPanel.setBorder(BorderFactory.createLineBorder(new Color(255, 165, 0)));
    content.add(expiryPanel);
 
    JLabel expiryTitle = new JLabel("  Expiry Alerts");
    expiryTitle.setFont(new Font("Arial", Font.BOLD, 14));
    expiryTitle.setForeground(new Color(255, 165, 0));
    expiryTitle.setBounds(10, 10, 200, 25);
    expiryPanel.add(expiryTitle);
 
    try {
      Connection con = DBConnecton.getConnection();
      String sql =
        "SELECT blood_group, expiry_date, " +
        "DATEDIFF(expiry_date, CURDATE()) AS days_left " +
        "FROM BloodStock " +
        "WHERE status = 'Available' " +
        "AND DATEDIFF(expiry_date, CURDATE()) <= 7 " +
        "AND DATEDIFF(expiry_date, CURDATE()) >= 0 " +
        "ORDER BY days_left ASC LIMIT 4";
 
      ResultSet rs = con.createStatement().executeQuery(sql);
      int exY = 42;
      boolean hasExpiry = false;
 
      while (rs.next()) {
        hasExpiry = true;
        String bg   = rs.getString("blood_group");
        int days    = rs.getInt("days_left");
        Color aColor = days <= 2 ? Color.RED : new Color(255, 165, 0);
 
        JLabel item = new JLabel(
          "  " + bg + " → " +
          (days == 0 ? "Expires TODAY!" : "Expires in " + days + " days"));
        item.setFont(new Font("Arial", Font.BOLD, 12));
        item.setForeground(aColor);
        item.setBounds(5, exY, 315, 20);
        expiryPanel.add(item);
        exY += 28;
      }
 
      if (!hasExpiry) {
        JLabel ok = new JLabel("  No expiring blood stock!");
        ok.setFont(new Font("Arial", Font.PLAIN, 13));
        ok.setForeground(new Color(0, 200, 0));
        ok.setBounds(20, 70, 280, 25);
        expiryPanel.add(ok);
      }
 
      con.close();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
 
    // ─── EMERGENCY ALERT PANEL ───────────────
    JPanel alertPanel = new JPanel(null);
    alertPanel.setBackground(cardBg);
    alertPanel.setBounds(620, 455, 330, 280);
    alertPanel.setBorder(BorderFactory.createLineBorder(redColor));
    content.add(alertPanel);
 
    JLabel alertTitle = new JLabel("  Emergency Alert");
    alertTitle.setFont(new Font("Arial", Font.BOLD, 15));
    alertTitle.setForeground(redColor);
    alertTitle.setBounds(15, 10, 280, 25);
    alertPanel.add(alertTitle);
 
    JPanel alertCard = new JPanel(null);
    alertCard.setBackground(new Color(40, 0, 0));
    alertCard.setBounds(10, 45, 310, 140);
    alertPanel.add(alertCard);
 
 // ─── LOAD EMERGENCY DATA FROM DATABASE ───

    String emergencyBg = "TEST";
    String emergencyHos = "TEST";
    String emergencyLoc = "TEST";
    String emergencyPhone = "9999999999";
    int emergencyUnits = 5;

    try {

        Connection con = DBConnecton.getConnection();

        String sql =
        	    "SELECT r.blood_group, " +
        	    "r.units_needed, " +
        	    "h.name, " +
        	    "h.location, " +
        	    "h.phone " +
        	    "FROM BloodRequests r, Hospitals h " +
        	    "WHERE r.hospital_id = h.hospital_id " +
        	    "AND LOWER(r.status) = 'pending' " +
        	    "AND LOWER(r.urgency) = 'emergency' " +
        	    "LIMIT 1";

        ResultSet rs =
            con.createStatement().executeQuery(sql);

        if(rs.next()) {

            System.out.println("ROW FOUND");

            emergencyBg =
            	    rs.getString("blood_group");

            	emergencyHos =
            	    rs.getString("name");

            	emergencyLoc =
            	    rs.getString("location");

            	emergencyPhone =
            	    rs.getString("phone");

            	emergencyUnits =
            	    rs.getInt("units_needed");

            System.out.println(emergencyBg);
            System.out.println(emergencyUnits);
        }

        con.close();

    } catch(Exception ex) {
        ex.printStackTrace();
    }
 
    JLabel alertBlood = new JLabel("  " + emergencyBg + " Blood Needed!");
    alertBlood.setFont(new Font("Arial", Font.BOLD, 17));
    alertBlood.setForeground(redColor);
    alertBlood.setBounds(5, 10, 290, 30);
    alertCard.add(alertBlood);
 
    JLabel alertHos = new JLabel("  Hospital : " + emergencyHos);
    alertHos.setFont(new Font("Arial", Font.PLAIN, 12));
    alertHos.setForeground(textWhite);
    alertHos.setBounds(5, 50, 290, 20);
    alertCard.add(alertHos);
 
    JLabel alertLoc = new JLabel("  Location : " + emergencyLoc);
    alertLoc.setFont(new Font("Arial", Font.PLAIN, 12));
    alertLoc.setForeground(textWhite);
    alertLoc.setBounds(5, 78, 290, 20);
    alertCard.add(alertLoc);
    JLabel alertUnits = new JLabel(
    	    "  Units Needed : " + emergencyUnits);

    	alertUnits.setFont(new Font("Arial", Font.PLAIN, 12));
    	alertUnits.setForeground(Color.YELLOW);
    	alertUnits.setBounds(5, 100, 290, 20);

    	alertCard.add(alertUnits);
 
    JLabel alertPhone = new JLabel("  Contact  : " + emergencyPhone);
    alertPhone.setFont(new Font("Arial", Font.PLAIN, 12));
    alertPhone.setForeground(textWhite);
    alertPhone.setBounds(5, 125, 290, 20);
    alertCard.add(alertPhone);
 
    JButton respondBtn = new JButton("  Respond Now");
    respondBtn.setFont(new Font("Arial", Font.BOLD, 13));
    respondBtn.setBounds(55, 200, 210, 35);
    respondBtn.setBackground(redColor);
    respondBtn.setForeground(Color.WHITE);
    respondBtn.setBorder(null);
    respondBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    alertPanel.add(respondBtn);
    respondBtn.addActionListener(e -> new HospitalRequest());
 
    // ─── BOTTOM QUICK BUTTONS ────────────────
    
 
    return content;
  }
 
  // ─── STAT CARD HELPER ────────────────────────
  JPanel createStatCard(String iconFile, String title,
                        String sub, Color bgColor, int x) {
    JPanel card = new JPanel(null);
    card.setBackground(cardBg);
    card.setBounds(x, 90, 290, 170);
    card.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60)));
 
    JPanel iconPanel = new JPanel(new GridBagLayout());
    iconPanel.setBackground(bgColor);
    iconPanel.setBounds(15, 15, 50, 50);
    card.add(iconPanel);
 
    ImageIcon sIcon = loadIcon(iconFile, 28, 28);
    JLabel iconLabel;
    if (sIcon.getIconWidth() > 1) {
      iconLabel = new JLabel(sIcon);
    } else {
      iconLabel = new JLabel(title.substring(0, 1));
      iconLabel.setFont(new Font("Arial", Font.BOLD, 18));
      iconLabel.setForeground(Color.WHITE);
    }
    iconPanel.add(iconLabel);
 
    JLabel titleLabel = new JLabel(title);
    titleLabel.setFont(new Font("Arial", Font.PLAIN, 13));
    titleLabel.setForeground(textGray);
    titleLabel.setBounds(75, 15, 200, 20);
    card.add(titleLabel);
 
    JLabel subLabel = new JLabel(sub);
    subLabel.setFont(new Font("Arial", Font.PLAIN, 12));
    subLabel.setForeground(new Color(0, 180, 0));
    subLabel.setBounds(15, 100, 250, 20);
    card.add(subLabel);
 
    return card;
  }
 
  // ─── EXPIRY POPUP ALERT ──────────────────────
  void checkExpiryAlert() {
    try {
      Connection con = DBConnecton.getConnection();
      String sql =
        "SELECT blood_group, expiry_date, units_available, " +
        "DATEDIFF(expiry_date, CURDATE()) AS days_left " +
        "FROM BloodStock " +
        "WHERE status = 'Available' " +
        "AND DATEDIFF(expiry_date, CURDATE()) <= 7 " +
        "AND DATEDIFF(expiry_date, CURDATE()) >= 0 " +
        "ORDER BY days_left ASC";
 
      ResultSet rs = con.createStatement().executeQuery(sql);
      StringBuilder alertMsg = new StringBuilder();
      int count = 0;
 
      while (rs.next()) {
        String bg   = rs.getString("blood_group");
        int days    = rs.getInt("days_left");
        int units   = rs.getInt("units_available");
 
        if (days == 0) {
          alertMsg.append("🔴 ").append(bg)
            .append(" → Expires TODAY! (").append(units).append(" units)\n");
        } else {
          alertMsg.append("⚠️ ").append(bg)
            .append(" → Expires in ").append(days)
            .append(" days (").append(units).append(" units)\n");
        }
        count++;
      }
 
      if (count > 0) {
        JOptionPane.showMessageDialog(null,
          "BLOOD EXPIRY ALERT!\n\n" + alertMsg +
          "\nPlease use or transfer this blood immediately!",
          "⚠️ Expiry Warning!",
          JOptionPane.WARNING_MESSAGE);
      }
 
      con.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
 
  // ─── HANDLE MENU CLICKS ──────────────────────
  void handleMenu(String menu) {
    if (menu.contains("Donor Registration"))
      new DonorRegistration();
    else if (menu.contains("Search Donor"))
      new SearchDonor();
    else if (menu.contains("Blood Stock"))
      new BloodStock();
    else if (menu.contains("Hospital Request") || menu.contains("Hospital"))
      new HospitalRequest();
    else if (menu.contains("Donation History") || menu.contains("Donation"))
      new DonationHistory();
    else if (menu.contains("Compatibility"))
      new BloodCompatibility();
    else if (menu.contains("Admin Panel") || menu.contains("Admin"))
      new AdminPanel();
    else if (menu.contains("Logout")) {
      int confirm = JOptionPane.showConfirmDialog(null,
        "Are you sure you want to logout?",
        "Logout", JOptionPane.YES_NO_OPTION);
      if (confirm == JOptionPane.YES_OPTION) {
        dispose();
        new loginscreen();
      }
    }
  }
 
  // ─── LOAD STATS FROM DATABASE ────────────────
  void loadStats() {
    try {
      Connection con = DBConnecton.getConnection();
 
      ResultSet rs1 = con.createStatement()
        .executeQuery("SELECT COUNT(*) FROM Donors");
      rs1.next();
      donorCountLabel.setText(String.valueOf(rs1.getInt(1)));
 
      ResultSet rs2 = con.createStatement()
        .executeQuery("SELECT SUM(units_available) FROM BloodStock");
      rs2.next();
      bloodUnitsLabel.setText(String.valueOf(rs2.getInt(1)));
 
      ResultSet rs3 = con.createStatement()
        .executeQuery("SELECT COUNT(*) FROM BloodRequests WHERE status='Pending'");
      rs3.next();
      requestCountLabel.setText(String.valueOf(rs3.getInt(1)));
 
      con.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
 
  // ─── LIVE CLOCK ──────────────────────────────
  void startClock() {
    Timer timer = new Timer(1000, e -> {
      SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
      SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss a");
      Date now = new Date();
      dateLabel.setText("  " + dateFormat.format(now));
      timeLabel.setText("  " + timeFormat.format(now));
    });
    timer.start();
  }
 
  public static void main(String[] args) {
    new Dashboard("Admin", "Manager");
  }
}