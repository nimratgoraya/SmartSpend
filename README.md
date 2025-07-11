# 💸 SmartSpend – Smart Spending Habit Analyzer  (Track Smart, Spend Smart)
> A JavaFX-based personal finance tracker that helps you understand and improve your spending habits.

---

## 📌 Key Features

- **Add, view, and manage expenses** with category, date, and amount filters.
- **Spending trend detection** – e.g., “You spent 86% more on Shopping than last month.”
- **Smart Saving Tips** tailored to your non-essential spending patterns.
- **Monthly Budget Tracker** with visual alerts when you're over budget.
- **Summaries & Charts** – Pie charts and bar graphs for instant insights.
- **Advanced filters** – Search by category/date, export to CSV, and print tables.
- **Settings Panel** – Update profile, change password, manage notifications, delete account.
- **Modern JavaFX UI** – Custom visuals, top-aligned GIFs, and responsive layout.

---

## 🛠️ Tech Stack

| Layer         | Tools Used                             |
|---------------|-----------------------------------------|
| Language      | Java (JDK 17+)                          |
| UI            | JavaFX + Scene Builder + Figma          |
| Database      | MySQL + JDBC                            |
| Dev Tools     | VS Code (PowerShell), Batch Scripts     |

---

## 🧪 How to Run the Project

### ✅ Requirements

- Java JDK 17 or above  
- JavaFX SDK (`javafx-sdk-24.0.1`)  
- MySQL Server installed and running  
- MySQL JDBC Connector (`mysql-connector-j-9.3.0.jar`) placed in `/lib`  

### ▶️ Manual (Command-Line)

```bash
cd src
javac --module-path "path\to\javafx-sdk\lib" --add-modules javafx.controls,javafx.fxml -cp ".;../lib/mysql-connector-j-9.3.0.jar;.." *.java
java --module-path "path\to\javafx-sdk\lib" --add-modules javafx.controls,javafx.fxml -cp ".;../lib/mysql-connector-j-9.3.0.jar;.." LoginPage


🖱️ One-Click (Windows)
Just double-click the SmartSpend.bat file to compile and run the app automatically.
No terminal needed!

🧠 Smart Tips Example
💡 “You spent 86% more on Shopping than last month.”
💡 “Try setting a budget goal for Entertainment this month.”

🗄️ Database Schema Overview
Table	Purpose
users	Stores login credentials and user profile
expenses	Each row is an expense entry with category
categories	Predefined types like Rent, Food, Shopping
budgets	Monthly budget per user
savings_goals	Tracks personal savings goals

🧑‍💻 About the Developer
Hi! I'm a B.Tech CSE student with a passion for clean UI, insightful data analytics, and real-world tools.
This project reflects my growing interest in building products that blend Java, SQL, and UX Design.