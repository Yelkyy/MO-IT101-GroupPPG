package repository;

import db.MySQLConnection;
import model.DynamicModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.Locale;
import java.time.Duration;
import java.time.LocalTime;

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

    public List<DynamicModel> getAttendanceByWeekRange(int employeeId, String month, String year, int startWeek,
            int endWeek) {
        List<DynamicModel> attendance = new ArrayList<>();
        LocalDate startDate = getStartDateFromWeek(year, month, startWeek);
        LocalDate endDate = getEndDateFromWeek(year, month, endWeek);

        DateTimeFormatter dbFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        String formattedStartDate = startDate.format(dbFormat);
        String formattedEndDate = endDate.format(dbFormat);

        String query = "SELECT * FROM motorph.attendance_record WHERE `Employee #` = ? " +
                "AND STR_TO_DATE(`Date`, '%m/%d/%Y') BETWEEN STR_TO_DATE(?, '%m/%d/%Y') AND STR_TO_DATE(?, '%m/%d/%Y')";

        try (Connection conn = mySQLConnection.connect();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, employeeId);
            stmt.setString(2, formattedStartDate);
            stmt.setString(3, formattedEndDate);

            // System.out.println("Executing SQL: " + query);
            // System.out.println("Employee ID: " + employeeId);
            // System.out.println("Start Date: " + formattedStartDate);
            // System.out.println("End Date: " + formattedEndDate);

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

    private LocalDate getStartDateFromWeek(String year, String month, int weekNumber) {
        LocalDate firstDayOfMonth = LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), 1);
        return firstDayOfMonth.plusWeeks(weekNumber - 1);
    }

    private LocalDate getEndDateFromWeek(String year, String month, int weekNumber) {
        return getStartDateFromWeek(year, month, weekNumber).plusDays(6);
    }

    public void calculateWeeklyPayroll(DynamicModel employee, List<DynamicModel> attendance,
            String month, String year, int startWeek, int endWeek, boolean isSingleWeek) {

        double basicSalary = employee.getDouble("basic_salary"); // Fetch the basic salary from the database
        double expectedHoursPerMonth = 160; // Standard full-time hours per month
        double totalHours = 0;
        double totalOvertime = 0;
        double totalLate = 0;
        double hourlyRate = employee.getDouble("hourly_rate");
        double overtimeRate = hourlyRate * 1.5;
        double lateDeductionRate = hourlyRate / 60; // Deduction per late minute
        double riceSubsidy = employee.getDouble("rice_subsidy");
        double phoneAllowance = employee.getDouble("phone_allowance");
        double clothingAllowance = employee.getDouble("clothing_allowance");

        // Adjusting the Non-Taxable Allowance
        double nonTaxable = 0;
        if (startWeek == 1 && endWeek == 4) {
            // Full Month, use the full non-taxable allowance
            nonTaxable = riceSubsidy + phoneAllowance + clothingAllowance;
        } else if (startWeek == 1 && endWeek == 2) {
            // Biweekly, prorate the non-taxable allowance for 2 weeks
            nonTaxable = (riceSubsidy + phoneAllowance + clothingAllowance) / 2;
        } else {
            // Single week, prorate the non-taxable allowance for 1 week
            nonTaxable = (riceSubsidy + phoneAllowance + clothingAllowance) / 4;
        }

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("H:mm");

        for (DynamicModel record : attendance) {
            LocalTime logIn = LocalTime.parse(record.getString("log_in"), timeFormatter);
            LocalTime logOut = LocalTime.parse(record.getString("log_out"), timeFormatter);
            Duration workDuration = Duration.between(logIn, logOut);

            double hoursWorked = workDuration.toMinutes() / 60.0;
            double overtimeHours = Math.max(0, hoursWorked - 8);
            double lateMinutes = Math.max(0, Duration.between(LocalTime.of(8, 0), logIn).toMinutes());

            totalHours += hoursWorked;
            totalOvertime += overtimeHours;
            totalLate += lateMinutes;
        }

        // Calculate the basic pay based on the period (single week or biweekly)
        double basicPay = 0;

        // **Week 1: Prorate based on total hours worked compared to the expected
        // full-time month hours**
        if (startWeek == 1 && endWeek == 1) {
            basicPay = (basicSalary / expectedHoursPerMonth) * totalHours; // Pro-rate for Week 1 based on worked hours
        } else if (startWeek == 1 && endWeek == 2) {
            // Biweekly period (1-2 weeks), so basic pay is half of the monthly salary
            basicPay = basicSalary / 2; // PHP 45,000 for 1-2 weeks
        } else {
            // Single week, prorate based on actual hours worked
            double maxPossiblePay = (basicSalary * (expectedHoursPerMonth));
            basicPay = Math.min(hourlyRate * totalHours, maxPossiblePay);
        }

        // **Fix for Week Range 1-4: Cap the basic pay to PHP 90,000 for the full
        // month**
        if (startWeek == 1 && endWeek == 4) {
            basicPay = Math.min(basicPay, basicSalary); // Ensure that basic pay does not exceed PHP 90,000
        }

        double overtimePay = totalOvertime * hourlyRate;

        // Fix: Adjust late deduction to account for overtime
        double lateDeduction = totalLate * lateDeductionRate; // Deduct late minutes from basic pay first
        double maxLateDeduction = basicPay * 0.5; // Cap at 50% of basic pay
        lateDeduction = Math.min(lateDeduction, maxLateDeduction); // Apply cap

        // **Calculate Overtime Pay Separately**
        double grossPay = basicPay + nonTaxable + overtimePay - lateDeduction;

        // Deduction logic for Week 4 or Full Month
        boolean showDeductions = (startWeek == 4 || (startWeek == 1 && endWeek == 4));

        double sss = 0;
        double philhealth = 0;
        double pagibig = 0;
        double withholdingTax = 0;
        double totalDeductions = 0;
        if (showDeductions) {
            sss = calculateSSS(grossPay);
            philhealth = calculatePhilHealth(grossPay);
            pagibig = calculatePagibig(grossPay);
            withholdingTax = grossPay * 0.12;
            totalDeductions = sss + philhealth + pagibig + withholdingTax;
        }

        double netPay = grossPay - totalDeductions;

        // **Display correct week format**
        String weekDisplay = isSingleWeek ? "Week: " + startWeek + " of " + month + "/" + year
                : "Week: " + startWeek + " to " + endWeek + " of " + month + "/" + year;

        System.out
                .println("\nPayroll Summary for " + employee.get("first_name") + " " + employee.get("last_name") + ":");
        System.out.println(weekDisplay);
        System.out.println("=====================================");
        System.out.println("\nTotal Hours Worked: " + String.format("%.2f", totalHours));
        System.out.println("Total Overtime Hours: " + String.format("%.2f", totalOvertime));
        System.out.println("Total Late Minutes: " + totalLate);

        System.out.println("\nBasic Pay: " + df.format(basicPay));
        System.out.println("Non-Taxable Allowance: " + df.format(nonTaxable));
        System.out.println("Overtime Pay: " + df.format(overtimePay));
        System.out.println("Late Deduction: " + df.format(lateDeduction));
        System.out.println("-------------------------------------");
        System.out.println("Gross Pay: " + df.format(grossPay));
        System.out.println("-------------------------------------");

        // Display deductions only if it's Week 4 or Full Month
        if (showDeductions) {
            System.out.println("Withholding Tax " + df.format(withholdingTax));
            System.out.println("SSS Contribution " + df.format(sss));
            System.out.println("Philhealth Contribution " + df.format(philhealth));
            System.out.println("PAG-IBIG Contribution " + df.format(pagibig));
            System.out.println("-------------------------------------");
            System.out.println("Total Deductions: " + df.format(totalDeductions));
        } else {
            System.out.println("Withholding Tax: 0");
            System.out.println("SSS Contribution: 0");
            System.out.println("Philhealth Contribution: 0");
            System.out.println("PAG-IBIG Contribution: 0");
            System.out.println("Total Deductions: 0");
        }

        System.out.println("-------------------------------------");
        System.out.println("\033[1mNet Pay: " + df.format(netPay) + "\033[0m");
    }

    private double calculateSSS(double basicSalary) {
        if (basicSalary < 3000) {
            basicSalary = 3000; // Minimum salary for SSS contribution
        } else if (basicSalary > 25000) {
            basicSalary = 25000; // Maximum salary for SSS contribution
        }
        return basicSalary * 0.045; // 4.5% of Basic Salary
    }

    private double calculatePhilHealth(double basicSalary) {
        if (basicSalary < 10000) {
            basicSalary = 10000; // Minimum salary for PhilHealth contribution
        } else if (basicSalary > 80000) {
            basicSalary = 80000; // Maximum salary for PhilHealth contribution
        }
        return basicSalary * 0.0225; // 2.25% of Basic Salary
    }

    private double calculatePagibig(double salary) {
        return Math.min(100, salary * 0.02);
    }
}
