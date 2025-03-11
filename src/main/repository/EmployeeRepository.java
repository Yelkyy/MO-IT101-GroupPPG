package repository;

import db.MySQLConnection;
import model.DynamicModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.text.DecimalFormat;

public class EmployeeRepository {

    DecimalFormat df = new DecimalFormat("PHP #,##0.00");

    private MySQLConnection mySQLConnection;

    public EmployeeRepository() {
        mySQLConnection = new MySQLConnection();
    }

    public List<DynamicModel> findEmployeeByIdOrName(String input) {
        List<DynamicModel> employees = new ArrayList<>();
        String query = "SELECT * FROM motorph.employee_details WHERE employee_id = ? OR first_name LIKE ? OR last_name LIKE ?";

        try (Connection conn = mySQLConnection.connect();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, input);
            stmt.setString(2, "%" + input + "%");
            stmt.setString(3, "%" + input + "%");

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                DynamicModel employee = new DynamicModel(rs);
                employees.add(employee);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employees;
    }

    public List<DynamicModel> getAttendanceByMonthYear(int employeeId, String month, String year) {
        List<DynamicModel> attendance = new ArrayList<>();
        String query = "SELECT * FROM motorph.attendance_record WHERE `Employee #` = ? " +
                "AND YEAR(STR_TO_DATE(`Date`, '%m/%d/%Y')) = ? " +
                "AND MONTH(STR_TO_DATE(`Date`, '%m/%d/%Y')) = ?;";

        try (Connection conn = mySQLConnection.connect();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, employeeId);
            stmt.setString(2, year); // Match the year (last part)
            stmt.setString(3, month); // Match the middle part (Month)

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                DynamicModel record = new DynamicModel(rs);
                attendance.add(record);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return attendance;
    }

    public void calculatePayroll(DynamicModel employee, List<DynamicModel> attendance, String month, String year) {
        // Display the payroll summary header with the month and year
        System.out.println("\n=== Payroll Summary for " + employee.get("first_name") + " " + employee.get("last_name") +
                " for the month of " + getMonthName(month) + " " + year + " ===");

        double totalHours = 0;
        double overtimeHours = 0;
        double totalAllowance = parseDouble(employee.get("Phone Allowance")) +
                parseDouble(employee.get("Clothing Allowance")) +
                parseDouble(employee.get("Rice Subsidy"));
        int totalAbsences = 0;

        for (DynamicModel record : attendance) {
            String logIn = record.get("Log In").toString();
            String logOut = record.get("Log Out").toString();

            // Calculate worked hours
            double workedHours = calculateWorkedHours(logIn, logOut);
            totalHours += workedHours;

            // Calculate overtime (if any)
            if (workedHours > 8) {
                overtimeHours += (workedHours - 8);
            }

            // Track number of absences (assuming absence is marked in the "Log In" field as
            // null or similar)
            if (logIn == null || logOut == null) {
                totalAbsences++;
            }
        }

        // Calculate salary
        double hourlyRate = parseDouble(employee.get("Hourly Rate"));
        double regularSalary = totalHours * hourlyRate;
        double overtimePay = overtimeHours * (hourlyRate * 1.25);

        // Deductions
        double basicSalary = parseDouble(employee.get("Basic Salary"));
        double sss = calculateSSS(basicSalary); // SSS contribution based on salary
        double philhealth = calculatePhilHealth(basicSalary); // PhilHealth contribution based on salary
        double pagibig = calculatePagibig(basicSalary); // Pag-ibig contribution
        double withholdingTax = (regularSalary + overtimePay) * 0.12;
        double lateDeduction = totalAbsences * 200.00; // Assuming PHP 200 per absence

        // Calculate net salary
        double grossSalary = regularSalary + overtimePay + totalAllowance;
        double totalDeductions = sss + philhealth + pagibig + withholdingTax + lateDeduction;
        double netSalary = grossSalary - totalDeductions;

        // Display payroll summary details
        System.out.println("Total Hours Worked: " + String.format("%.2f", totalHours));
        System.out.println("Overtime Hours: " + String.format("%.2f", overtimeHours));
        System.out.println("Overtime Pay: " + df.format(overtimePay));
        System.out.println("Total Allowance: " + df.format(totalAllowance));
        System.out.println("Regular Salary: " + df.format(regularSalary));
        System.out.println("Gross Salary: " + df.format(grossSalary));

        System.out.println("\n--- Deductions ---");

        // Display deductions and net salary
        System.out.println("SSS: " + df.format(sss));
        System.out.println("PhilHealth: " + df.format(philhealth));
        System.out.println("Pag-ibig: " + df.format(pagibig));
        System.out.println("Withholding Tax: " + df.format(withholdingTax));
        System.out.println("Absence/Late Deduction: " + df.format(lateDeduction));
        System.out.println("Deductions: " + df.format(totalDeductions));

        System.out.println("\n-----------\n");

        System.out.println("Net Salary: " + df.format(netSalary));
    }

    // ✅ This method automatically removes commas before parsing
    private double parseDouble(Object value) {
        if (value == null)
            return 0;
        String strValue = value.toString().replace(",", ""); // Remove comma
        return Double.parseDouble(strValue);
    }

    private String getMonthName(String month) {
        switch (month) {
            case "01":
                return "January";
            case "02":
                return "February";
            case "03":
                return "March";
            case "04":
                return "April";
            case "05":
                return "May";
            case "06":
                return "June";
            case "07":
                return "July";
            case "08":
                return "August";
            case "09":
                return "September";
            case "10":
                return "October";
            case "11":
                return "November";
            case "12":
                return "December";
            default:
                return "Unknown Month";
        }
    }

    // Method to calculate the SSS contribution (4.5% of basic salary, minimum PHP
    // 3,000 salary)
    private double calculateSSS(double basicSalary) {
        if (basicSalary < 3000) {
            basicSalary = 3000; // Minimum salary for SSS contribution
        } else if (basicSalary > 25000) {
            basicSalary = 25000; // Maximum salary for SSS contribution
        }
        return basicSalary * 0.045; // 4.5% of Basic Salary
    }

    // Method to calculate the PhilHealth contribution (2.25% of basic salary,
    // capped at PHP 80,000 salary)
    private double calculatePhilHealth(double basicSalary) {
        if (basicSalary < 10000) {
            basicSalary = 10000; // Minimum salary for PhilHealth contribution
        } else if (basicSalary > 80000) {
            basicSalary = 80000; // Maximum salary for PhilHealth contribution
        }
        return basicSalary * 0.0225; // 2.25% of Basic Salary
    }

    // Method to calculate the Pag-ibig contribution (2% of basic salary, capped at
    // PHP 5,000)
    private double calculatePagibig(double basicSalary) {
        double pagibig = basicSalary * 0.02; // 2% of Basic Salary
        if (pagibig > 5000) {
            return 5000; // Cap the Pag-ibig contribution at PHP 5,000
        } else if (pagibig < 1000) {
            return 1000; // Minimum Pag-ibig contribution is PHP 1,000
        }
        return pagibig;
    }

    private double calculateWorkedHours(String logIn, String logOut) {
        if (logIn == null || logOut == null) {
            return 0; // If there's no time, consider it as zero hours worked
        }
        String[] in = logIn.split(":");
        String[] out = logOut.split(":");

        int inHour = Integer.parseInt(in[0]);
        int inMinute = Integer.parseInt(in[1]);
        int outHour = Integer.parseInt(out[0]);
        int outMinute = Integer.parseInt(out[1]);

        double workedHours = (outHour + (outMinute / 60.0)) - (inHour + (inMinute / 60.0));
        return workedHours;
    }
}
