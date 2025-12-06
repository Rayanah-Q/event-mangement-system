 # 🎫 Event Registration & Ticket Management System  
<p align="center">
  <!-- ⭐ Add your banner image here -->
  <!-- Example: <img src="images/banner.png" width="100%"> -->
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Language-Java-orange?style=flat-square">
  <img src="https://img.shields.io/badge/GUI-Java%20Swing-blue?style=flat-square">
  <img src="https://img.shields.io/badge/Database-MySQL-lightgrey?style=flat-square">
  <img src="https://img.shields.io/badge/Platform-Desktop%20App-purple?style=flat-square">
</p>

---


# 📂 Table of Contents
- [Overview](#-overview)  
- [Application Screens](#-application-screens)  
- [Features](#-features)  
- [GUI Components](#-gui-components)  
- [Database Integration](#-database-integration)
- [Exception Handling](#-exception-handling)  
- [Testing & Validation](#-testing--validation)
- [Team Info](#-course--team-info)
---

## 📘 Overview

The **Event Registration and Ticket Management System** is a Java Swing desktop application with MySQL backend. It includes:

- 🔐 Secure Login & Sign-Up  
- 👥 Role-based dashboard (Admin / Organizer / Attendee)  
- 🎫 Event creation, editing, deletion  
- 🗄 Full JDBC database connectivity  
- ⚠ Robust validation & exception handling  


---

## 🖥 Application Screens


### 🔐 Login Screen  
<img width="280" height="200" alt="image" src="https://github.com/user-attachments/assets/de650ada-eadb-4307-88bb-b8ed50207be2" />


### 📝 Sign-Up Screen  
<img width="300" height="240" alt="image" src="https://github.com/user-attachments/assets/56f09667-acc6-481e-b8b6-7157c799ef61" />


### 🎟 Organizer Event Dashboard  
<img width="585" height="491" alt="image" src="https://github.com/user-attachments/assets/0d6c1577-b5bc-429f-a9f8-6b98be71b0e4" />


---

# 🧩 Features

### 🔐 Login System
- Validates user credentials  
- Prevents unauthorized access  
- Redirects user based on role  

### 📝 Sign-Up System
- Full name, email, password, confirmation  
- Validates email format  
- Ensures password matching  
- Prevents duplicate email registration  
- Adds new user records to database  

### 🎫 Event Management (Organizer)
- Add Event  
- Edit Event  
- Delete Event  
- Refresh Event List  
- Uses `JTable` for viewing events  

Event attributes:
- Event Title  
- Category  
- Location  
- Seat Capacity  
- Event DateTime (`YYYY-MM-DD HH:MM:SS`)  

---

# 🖼 GUI Components

### Login Components
| Component | Purpose |
|----------|---------|
| `JTextField emailField` | Enter email |
| `JPasswordField passwordField` | Enter password |
| `JButton loginButton` | Login attempt |
| `JButton signupButton` | Go to Sign-Up screen |

### Sign-Up Components
| Component | Purpose |
|----------|---------|
| `JTextField fullNameField` | User's full name |
| `JTextField emailField` | User email |
| `JPasswordField passwordField` | New password |
| `JPasswordField confirmPasswordField` | Confirm password |
| `JComboBox roleBox` | Role selection |
| `JButton registerButton` | Register account |
| `JButton backButton` | Return to login |

---

# 🗄 Database Integration
Database connection:

java
```
Connection con = DBConnection.getConnection();
```
---
# ⚠ Exception Handling

| Issue | System Response |
|-------|----------------|
| Empty input fields | Displays **"Please fill all fields!"** |
| Invalid email format | Displays **"Invalid email format!"** |
| Password confirmation mismatch | Displays **"Passwords do not match!"** |
| Email already exists | Displays **"Email already exists!"** |
| Invalid login credentials | Displays **"Invalid email or password!"** |
| Any SQL or runtime error | Shows a generic error message |

The system ensures stability by preventing crashes and handling all common input and database exceptions gracefully.

---

# 🧪 Testing & Validation

## 🔐 Login Testing
| Test Case | Expected Result |
|-----------|----------------|
| Valid email & password | Successful login → user dashboard opens |
| Wrong password | Error message is shown |
| Empty fields | Application asks user to fill required inputs |

---

## 📝 Sign-Up Testing
| Test Case | Expected Result |
|-----------|----------------|
| Correct info & matching passwords | Account created successfully; redirected to Login |
| Invalid email format | Registration blocked with warning |
| Password mismatch | Warning message shown |
| Existing email | Registration prevented; error message displayed |

---

## 🎫 Testing & Validation – Create/Edit Event

| Test Case | Input | Expected Result | Actual Result | Status |
|-----------|--------|----------------|----------------|--------|
| Empty Fields | One or more fields left empty | System shows validation error | Error message displayed | ✅ Passed |
| Invalid Date Format | Example: `25/1/2025` | Warning: Use `YYYY-MM-DD HH:MM:SS` | Warning displayed | ✅ Passed |
| Invalid Capacity | Negative or zero capacity | Reject input & show warning | Warning shown | ✅ Passed |
| Non-Numeric Capacity | Capacity = `"abc"` | Show numeric-only warning | Error displayed | ✅ Passed |
| Successful Event Creation | All valid fields | Event saved & appears in table | Event created | ✅ Passed |
| Successful Event Editing | Valid updated fields | Database updates record | Event updated | ✅ Passed |

---

## 👨‍🏫 Course & Team Info

**Instructor:** Dr. Rafef Al-suhebani  
**Group Numbers:** 5299, 5302  

**Team Members**
- **Hoor Al-Rumaihani** 
- **Elan Al-Rumihi** 
- **Rayanah Alqoblan** 
- **Shoug Altasan** 


