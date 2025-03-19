# Project Documentation

## Project Overview
This project is an **Automated Payroll System** designed for MotorPH to streamline payroll computation and improve efficiency by replacing manual processes.

## Features
- Employee details and time logs management
- Payroll computation with week selection (single week or week range)
- Automatic deduction calculations based on selected weeks
- Compensation and deductions breakdown
- CSV-based data handling
- Command-line interface for user interaction

## Folder Structure
```
/project-root
│── src/
│   ├── model/
│   │   ├── EmployeeDetails.java
│   │   ├── EmployeeTimeLogs.java
│   ├── repository/
│   │   ├── DataHandler.java
│   ├── service/
│   │   ├── EmployeeService.java
│   │   ├── MenuHandler.java
│   │   ├── PayrollService.java
│   ├── App.java
│── lib/
│   ├── opencsv.jar
│   ├── commons-lang3-3.17.0.jar
│── data/
│   ├── employees.csv
│   ├── time_logs.csv
│── run.bat
```

## Key Components
### **1. Service Layer**
#### `EmployeeService.java`
- Handles employee-related functionalities such as fetching and updating employee details.

#### `MenuHandler.java`
- Manages the user menu interface for navigation and input handling.

#### `PayrollService.java`
- Processes payroll based on selected **Month & Year**.
- Allows selection of **Single Week** (1-4) or **Week Range**.
- Ensures **deductions are only applied for Week 4**.
- Computes compensation, deductions, and net pay.

### **2. Repository Layer**
#### `DataHandler.java`
- Reads and writes data from/to CSV files.
- Uses **OpenCSV** for handling CSV data.

### **3. Entry Point**
#### `App.java`
- Main application runner that initializes and launches the system.

## Recent Fixes & Enhancements
✅ Implemented **dynamic deductions** (only apply if Week 4 is selected).
✅ Ensured **basic salary is divided per selected week**.
✅ Improved **payroll summary readability**.
✅ Created **run.bat** to execute the program without opening Visual Studio Code.
✅ Fixed **OpenCSV import issues** by ensuring JARs are in the `lib/` folder.
✅ Prevented **auto-closing of the command prompt** when running `run.bat`.

## How to Run the Project
1. Ensure Java is installed (`java -version` to check).
2. Place **opencsv.jar** and **commons-lang3-3.17.0.jar** in the `lib/` folder.
3. Open a terminal in the project root directory.
4. Run `run.bat` to compile and execute the project.

---
🚀 **Project is now functional and ready for further improvements!**

