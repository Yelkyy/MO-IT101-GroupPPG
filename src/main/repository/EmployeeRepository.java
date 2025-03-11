package repository;

import db.MySQLConnection;
import model.DynamicModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.text.DecimalFormat;

public class EmployeeRepository {

    DecimalFormat df = new DecimalFormat("P #,##0.00");

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
        }

        // Calculate salary
        double hourlyRate = parseDouble(employee.get("Hourly Rate"));
        double regularSalary = totalHours * hourlyRate;
        double overtimePay = overtimeHours * (hourlyRate * 1.25);

        // Deductions
        double basicSalary = parseDouble(employee.get("Basic Salary"));
        double sss = basicSalary * 0.045; // 4.5% of Basic Salary
        double philhealth = basicSalary * 0.0275; // 2.75% of Basic Salary
        double pagibig = 100.00; // Fixed Pag-ibig Deduction (100 PHP)
        double withholdingTax = (regularSalary + overtimePay) * 0.12;
        double lateDeduction = 0; // Not calculated yet

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
        System.out.println("Late Deduction: " + df.format(lateDeduction));
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

    private double calculateWorkedHours(String logIn, String logOut) {
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
