package service;

import model.EmployeeDetails;
import model.EmployeeTimeLogs;
import java.util.List;
import java.util.Scanner;

public class PayrollService {

    private static final Scanner scanner = new Scanner(System.in);

    public static void processPayroll(EmployeeDetails employee, List<EmployeeTimeLogs> logs) {
        System.out.print("Enter Month and Year (MM-YYYY): ");
        String monthYear = scanner.nextLine();

        System.out.println("Do you want a [1] Single Week or [2] Week Range?");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice == 1) {
            int week = selectWeek();
            processSingleWeekPayroll(employee, logs, monthYear, week);
        } else if (choice == 2) {
            int startWeek = selectWeek();
            int endWeek = selectWeekRange(startWeek);
            processWeekRangePayroll(employee, logs, monthYear, startWeek, endWeek);
        } else {
            System.out.println("Invalid choice. Returning to menu.");
        }
    }

    private static int selectWeek() {
        int week;
        do {
            System.out.print("Enter Week (1-4): ");
            week = scanner.nextInt();
            scanner.nextLine();
            if (week < 1 || week > 4) {
                System.out.println("Invalid week. Please enter a value between 1 and 4.");
            }
        } while (week < 1 || week > 4);
        return week;
    }

    private static int selectWeekRange(int startWeek) {
        int endWeek;
        do {
            System.out.print("Enter End Week (must be greater than or equal to " + startWeek + " and max 4): ");
            endWeek = scanner.nextInt();
            scanner.nextLine();
            if (endWeek < startWeek || endWeek > 4) {
                System.out.println("Invalid end week. It must be between " + startWeek + " and 4.");
            }
        } while (endWeek < startWeek || endWeek > 4);
        return endWeek;
    }

    private static void processSingleWeekPayroll(EmployeeDetails employee, List<EmployeeTimeLogs> logs,
            String monthYear, int week) {
        System.out.println("\nProcessing Payroll for " + monthYear + " | Week " + week);
        displayPayrollSummary(employee, logs, monthYear, week, week);
    }

    private static void processWeekRangePayroll(EmployeeDetails employee, List<EmployeeTimeLogs> logs, String monthYear,
            int startWeek, int endWeek) {
        System.out.println("\nProcessing Payroll for " + monthYear + " | Week " + startWeek + " to Week " + endWeek);
        displayPayrollSummary(employee, logs, monthYear, startWeek, endWeek);
    }

    private static void displayPayrollSummary(EmployeeDetails employee, List<EmployeeTimeLogs> logs, String monthYear,
            int startWeek, int endWeek) {
        boolean hasDeductions = (endWeek == 4);
        int weeksSelected = (endWeek - startWeek) + 1;

        double basicSalary = employee.getBasicSalary() * weeksSelected / 4;
        double totalCompensation = basicSalary + employee.getRiceSubsidy() + employee.getPhoneAllowance()
                + employee.getClothingAllowance();

        double lateUndertimeDeductions = hasDeductions ? calculateLateUndertime(logs) : 0.0;
        double sss = hasDeductions ? calculateSSS(basicSalary) : 0.0;
        double philhealth = hasDeductions ? calculatePhilHealth(basicSalary) : 0.0;
        double pagibig = hasDeductions ? calculatePagIbig(basicSalary) : 0.0;
        double tax = hasDeductions ? calculateTax(basicSalary) : 0.0;
        double totalDeductions = lateUndertimeDeductions + sss + philhealth + pagibig + tax;

        double netPay = totalCompensation - totalDeductions;

        System.out.println("===========================================");
        System.out.println("            PAYROLL SUMMARY");
        System.out.println("===========================================");
        System.out.println("Employee ID  : " + employee.getEmployeeNumber());
        System.out.println("Name         : " + employee.getFirstName() + " " + employee.getLastName());
        System.out.println("Position     : " + employee.getPosition());
        System.out.println("Status       : " + employee.getStatus());
        System.out.println("-------------------------------------------");
        System.out.println("Month & Year : " + monthYear);
        System.out.println("Week(s)      : " + startWeek + " - " + endWeek);
        System.out.println("-------------------------------------------");
        System.out.println("        COMPENSATION DETAILS");
        System.out.println("-------------------------------------------");
        System.out.printf("Basic Salary     : %.2f\n", basicSalary);
        System.out.printf("Rice Subsidy     : %.2f\n", employee.getRiceSubsidy());
        System.out.printf("Phone Allowance  : %.2f\n", employee.getPhoneAllowance());
        System.out.printf("Clothing Allow.  : %.2f\n", employee.getClothingAllowance());
        System.out.println("-------------------------------------------");
        System.out.printf("Total Compensation: %.2f\n", totalCompensation);
        System.out.println("-------------------------------------------");
        System.out.println("        DEDUCTIONS");
        System.out.println("-------------------------------------------");
        System.out.printf("Late/Undertime   : %.2f\n", lateUndertimeDeductions);
        System.out.printf("SSS Contribution : %.2f\n", sss);
        System.out.printf("PhilHealth       : %.2f\n", philhealth);
        System.out.printf("Pag-IBIG         : %.2f\n", pagibig);
        System.out.printf("Tax              : %.2f\n", tax);
        System.out.println("-------------------------------------------");
        System.out.printf("Total Deductions : %.2f\n", totalDeductions);
        System.out.println("-------------------------------------------");
        System.out.printf("NET PAY          : %.2f\n", netPay);
        System.out.println("===========================================");
    }

    private static double calculateLateUndertime(List<EmployeeTimeLogs> logs) {
        return 50.0;
    }

    private static double calculateSSS(double basicSalary) {
        return basicSalary * 0.045;
    }

    private static double calculatePhilHealth(double basicSalary) {
        return basicSalary * 0.03 / 2;
    }

    private static double calculatePagIbig(double basicSalary) {
        return Math.min(100, basicSalary * 0.02);
    }

    private static double calculateTax(double basicSalary) {
        return basicSalary > 20833 ? (basicSalary - 20833) * 0.20 : 0;
    }
}
