package service;

import model.EmployeeDetails;
import model.EmployeeTimeLogs;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.time.LocalTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.Duration;

public class PayrollService {

    private static final Scanner scanner = new Scanner(System.in);

    // Main method to handle payroll based on user choice
    public static void processPayroll(EmployeeDetails employee, List<EmployeeTimeLogs> logs, String monthYear) {
        System.out.println("Do you want a [1] Single Week or [2] Week Range?");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume the newline

        if (choice == 1) {
            int week = selectWeek(); // Get the week number
            processSingleWeekPayroll(employee, logs, monthYear, week); // Process payroll for one week
        } else if (choice == 2) {
            int startWeek = selectWeek(); // Get the start week
            int endWeek = selectWeekRange(startWeek); // Get the end week
            processWeekRangePayroll(employee, logs, monthYear, startWeek, endWeek); // Process payroll for a range of
                                                                                    // weeks
        } else {
            System.out.println("Invalid choice. Returning to menu.");
        }
    }

    // Get the week number from the user
    private static int selectWeek() {
        int week;
        do {
            System.out.print("Enter Week (1-4): ");
            week = scanner.nextInt();
            scanner.nextLine(); // Consume the newline
            if (week < 1 || week > 4) {
                System.out.println("Invalid week. Please enter a value between 1 and 4.");
            }
        } while (week < 1 || week > 4);
        return week;
    }

    // Get the end week for a range from the user
    private static int selectWeekRange(int startWeek) {
        int endWeek;
        do {
            System.out.print("Enter End Week (must be greater than or equal to " + startWeek + " and max 4): ");
            endWeek = scanner.nextInt();
            scanner.nextLine(); // Consume the newline
            if (endWeek < startWeek || endWeek > 4) {
                System.out.println("Invalid end week. It must be between " + startWeek + " and 4.");
            }
        } while (endWeek < startWeek || endWeek > 4);
        return endWeek;
    }

    // Process payroll for a single week
    private static void processSingleWeekPayroll(EmployeeDetails employee, List<EmployeeTimeLogs> logs,
            String monthYear, int week) {
        System.out.println("\nProcessing Payroll for " + monthYear + " | Week " + week);
        displayPayrollSummary(employee, filterLogs(logs, monthYear, week, week), monthYear, week, week);
    }

    // Process payroll for a range of weeks
    private static void processWeekRangePayroll(EmployeeDetails employee, List<EmployeeTimeLogs> logs,
            String monthYear, int startWeek, int endWeek) {
        System.out.println("\nProcessing Payroll for " + monthYear + " | Week " + startWeek + " to Week " + endWeek);
        displayPayrollSummary(employee, filterLogs(logs, monthYear, startWeek, endWeek), monthYear, startWeek, endWeek);
    }

    // Filter the logs based on the month/year and the week range
    private static List<EmployeeTimeLogs> filterLogs(List<EmployeeTimeLogs> logs, String monthYear, int startWeek,
            int endWeek) {
        List<EmployeeTimeLogs> filteredLogs = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-yyyy");

        for (EmployeeTimeLogs log : logs) {
            try {
                LocalDate logDate = LocalDate.parse(log.getDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                String logMonthYear = logDate.format(formatter);
                int weekOfMonth = (logDate.getDayOfMonth() - 1) / 7 + 1;

                // Add log if it matches the month/year and falls within the selected week range
                if (logMonthYear.equals(monthYear) && weekOfMonth >= startWeek && weekOfMonth <= endWeek) {
                    filteredLogs.add(log);
                }
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format in logs: " + e.getMessage());
            }
        }
        return filteredLogs;
    }

    // Display the payroll summary for the employee
    private static void displayPayrollSummary(EmployeeDetails employee, List<EmployeeTimeLogs> logs,
            String monthYear, int startWeek, int endWeek) {
        boolean hasDeductions = (endWeek == 4); // Deductions only for the 4th week
        int weeksSelected = (endWeek - startWeek) + 1; // Number of weeks selected

        // Calculate basic salary based on selected weeks
        double basicSalary = employee.getBasicSalary() * weeksSelected / 4;
        double totalCompensation = employee.getRiceSubsidy() + employee.getPhoneAllowance()
                + employee.getClothingAllowance();

        List<String[]> lateDeductions = hasDeductions ? calculateLateUndertime(logs) : new ArrayList<>();
        double totalLateUndertimeDeductions = lateDeductions.isEmpty() ? 0.0
                : Double.parseDouble(lateDeductions.get(lateDeductions.size() - 1)[3].replace(",", ""));

        // Calculate mandatory deductions (SSS, PhilHealth, PagIBIG)
        double sss = hasDeductions ? calculateSSS(basicSalary) : 0.0;
        double philhealth = hasDeductions ? calculatePhilHealth(basicSalary) : 0.0;
        double pagibig = hasDeductions ? calculatePagIbig(basicSalary) : 0.0;
        double nonTaxDeductions = totalLateUndertimeDeductions + sss + philhealth + pagibig;

        // Calculate withholding tax if deductions are applied
        double tax = hasDeductions ? calculateTax(basicSalary, nonTaxDeductions) : 0.0;
        double totalDeductions = nonTaxDeductions + tax;

        // Calculate net pay after all deductions
        double netPay = basicSalary + totalCompensation - totalDeductions;

        // Print the payroll summary
        System.out.println("=============================================");
        System.out.println("          PAYROLL SUMMARY          ");
        System.out.println("=============================================");
        System.out.printf("Employee        : %s (%s)%n", employee.getFullName(), employee.getEmployeeNumber());
        System.out.printf("Payroll Period  : %s | Week %d - %d%n", monthYear, startWeek, endWeek);
        System.out.println("-------------------------------------");
        System.out.printf("Basic Salary       : PHP %,10.2f%n", basicSalary);
        System.out.printf("Rice Subsidy       : PHP %,10.2f%n", employee.getRiceSubsidy());
        System.out.printf("Phone Allowance    : PHP %,10.2f%n", employee.getPhoneAllowance());
        System.out.printf("Clothing Allowance : PHP %,10.2f%n", employee.getClothingAllowance());
        System.out.println("-------------------------------------");
        System.out.printf("Total Compensation : PHP %,10.2f%n", totalCompensation);
        System.out.println("-------------------------------------");
        System.out.printf("Late & Undertime   : PHP %,10.2f%n", totalLateUndertimeDeductions);
        System.out.printf("SSS Contribution   : PHP %,10.2f%n", sss);
        System.out.printf("PhilHealth         : PHP %,10.2f%n", philhealth);
        System.out.printf("Pag-IBIG           : PHP %,10.2f%n", pagibig);
        System.out.printf("Withholding Tax    : PHP %,10.2f%n", tax);
        System.out.println("-------------------------------------");
        System.out.printf("Total Deductions   : PHP %,10.2f%n", totalDeductions);
        System.out.println("-------------------------------------");
        System.out.printf("Net Pay            : PHP %,10.2f%n", netPay);
        System.out.println("=============================================");
    }

    // Calculate late and undertime deductions based on time logs
    private static List<String[]> calculateLateUndertime(List<EmployeeTimeLogs> logs) {
        List<String[]> lateDeductions = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm");
        LocalTime scheduledIn = LocalTime.parse("09:00", formatter);
        LocalTime scheduledOut = LocalTime.parse("18:00", formatter);
        double totalDeduction = 0.0;

        for (EmployeeTimeLogs log : logs) {
            try {
                LocalTime actualIn = LocalTime.parse(log.getLogIn().trim(), formatter);
                LocalTime actualOut = LocalTime.parse(log.getLogOut().trim(), formatter);

                long minutesLate = Math.max(0, Duration.between(scheduledIn, actualIn).toMinutes());
                long minutesUndertime = Math.max(0, Duration.between(actualOut, scheduledOut).toMinutes());

                double deduction = ((minutesLate + minutesUndertime) / 60.0) * 100;
                totalDeduction += deduction;

                // Add deduction details for each log
                lateDeductions.add(new String[] {
                        log.getDate(),
                        String.valueOf(minutesLate),
                        String.valueOf(minutesUndertime),
                        String.format("%,.2f", deduction)
                });

            } catch (DateTimeParseException e) {
                System.out.println(
                        "Invalid time format for Employee #" + log.getEmployeeNumber() + ": " + e.getMessage());
            }
        }

        // Add total deduction at the end of the list
        lateDeductions.add(new String[] { "TOTAL", "", "", String.format("%,.2f", totalDeduction) });
        return lateDeductions;
    }

    // Calculate the SSS contribution
    private static double calculateSSS(double basicSalary) {
        if (basicSalary > 24750) {
            return 1125.00; // For salaries above 24,750
        }
        return basicSalary * 0.045; // For salaries below or equal to 24,750
    }

    // Calculate the PhilHealth contribution
    private static double calculatePhilHealth(double basicSalary) {
        double premium;

        if (basicSalary <= 10000) {
            premium = 300; // Fixed premium for salary <= 10,000
        } else if (basicSalary <= 59999.99) {
            premium = Math.min(basicSalary * 0.03, 1800); // 3% of salary or 1,800, whichever is lower
        } else {
            premium = 1800; // Fixed premium for salary >= 60,000
        }

        return premium / 2; // Employee pays 50% of the premium
    }

    // Calculate the Pag-IBIG contribution
    private static double calculatePagIbig(double basicSalary) {
        return Math.min(100, basicSalary * 0.02); // 2% or 100, whichever is lower
    }

    // Calculate the withholding tax
    private static double calculateTax(double basicSalary, double totalDeductions) {
        double taxableIncome = basicSalary;
        double tax = 0.0;

        // Calculate the tax based on income ranges
        if (taxableIncome <= 20832) {
            tax = 0;
        } else if (taxableIncome <= 33333) {
            tax = (taxableIncome - 20833) * 0.20;
        } else if (taxableIncome <= 66667) {
            tax = 2500 + (taxableIncome - 33333) * 0.25;
        } else if (taxableIncome <= 166667) {
            tax = 10833 + (taxableIncome - 66667) * 0.30;
        } else if (taxableIncome <= 666667) {
            tax = 40833.33 + (taxableIncome - 166667) * 0.32;
        } else {
            tax = 200833.33 + (taxableIncome - 666667) * 0.35;
        }

        return Math.round(tax * 100.0) / 100.0;
    }
}
