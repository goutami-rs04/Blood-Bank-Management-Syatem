# 🩸 Blood Bank Management System

A desktop application built using **Java Swing** and **MySQL** for managing blood bank operations efficiently.

## 📌 Features
- Secure role-based login system
- Donor registration and management
- Real-time dashboard with statistics
- Blood stock management with auto-expiry detection
- Hospital blood request processing
- Donation history tracking
- Blood compatibility checker
- Emergency alert notifications
- Admin panel for staff management
- MySQL triggers for automated business rules

## 🛠️ Technologies Used
- Java (Swing GUI)
- MySQL 8.0 (DBMS - Backend Database)
- JDBC (Java Database Connectivity)
- Eclipse IDE
- SQL (DDL, DML, DCL commands)
- MySQL Triggers (Automated Business Rules)
- MySQL Views
- MySQL Events (Scheduled Tasks)

## 🗃️ DBMS Concepts Used
- Normalization (3NF)
- Entity Relationship Diagram (ERD)
- Foreign Key Constraints
- Triggers (5 triggers implemented)
- Views
- Aggregate Functions (SUM, COUNT)
- Joins (INNER JOIN, LEFT JOIN)
- Date Functions (DATEDIFF, CURDATE)

## 🗄️ Database Tables
- donors
- bloodstock
- bloodstocksummary
- hospitals
- bloodrequests
- donationhistory
- staff
- blood_compatibility
- compatible_blood_view

## ▶️ How to Run
1. Install Java JDK 11 or higher
2. Install MySQL 8.0
3. Open MySQL and run:
   CREATE DATABASE bloodbanksytem;
4. Import the database:
   mysql -u root -p bloodbanksytem < BloodBank.sql
5. Open project in Eclipse IDE
6. Open DBConnection.java and update:
   - username = "root"
   - password = "A#Hegde123"
7. Run Dashboard.java

## 🗄️ Database Setup
- Database file: **BloodBank.sql** (included in this repository)
- Import it into MySQL before running the application
- Database name: **bloodbanksytem**
- Tables: 9
- Triggers: 5
- Views: 1

## 🔐 Default Login
| Username | Password | Role |
|----------|----------|------|
| admin    | admin123 | Admin|

## 👩‍💻 Author
**goutami-rs04**
