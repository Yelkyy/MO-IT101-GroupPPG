package service;

import model.EmployeeDetails;
import model.EmployeeTimeLogs;
import repository.DataHandler;
import java.util.List;
import java.util.ArrayList;

public class EmployeeService {

    public static void displayAllEmployees() {
        List<EmployeeDetails> employees = DataHandler.readEmployeeDetails();
        if (employees.isEmpty()) {
            System.out.println("No employee data found or failed to read CSV.");
        } else {
            System.out.println("\n--- Employee List ---");
            System.out.printf("%-15s %-15s %-15s %-20s\n", "Employee ID", "First Name", "Last Name", "Position");
            System.out.println("----------------------------------------------------------------------------");
            for (EmployeeDetails emp : employees) {
                System.out.printf("%-15s %-15s %-15s %-20s\n",
                        emp.getEmployeeNumber(), emp.getFirstName(), emp.getLastName(), emp.getPosition());
            }
        }
    }

    public static EmployeeDetails findEmployeeById(String empId) {
        List<EmployeeDetails> employees = DataHandler.readEmployeeDetails();
        for (EmployeeDetails emp : employees) {
            if (emp.getEmployeeNumber().equals(empId)) {
                return emp;
            }
        }
        return null;
    }

    public static void showFullDetails(EmployeeDetails employee) {
        System.out.println("\n--- Employee Full Details ---");
        System.out.println("Employee ID         : " + employee.getEmployeeNumber());
        System.out.println("Name               : " + employee.getFirstName() + " " + employee.getLastName());
        System.out.println("Birthday           : " + employee.getBirthday());
        System.out.println("Address            : " + employee.getAddress());
        System.out.println("Phone Number       : " + employee.getPhoneNumber());
        System.out.println("SSS Number         : " + employee.getSssNumber());
        System.out.println("PhilHealth Number  : " + employee.getPhilhealthNumber());
        System.out.println("TIN Number         : " + employee.getTinNumber());
        System.out.println("Pag-IBIG Number    : " + employee.getPagIbigNumber());
        System.out.println("Status             : " + employee.getStatus());
        System.out.println("Position           : " + employee.getPosition());
        System.out.println("Immediate Supervisor: " + employee.getImmediateSupervisor());
        System.out.println("Basic Salary       : PHP " + employee.getBasicSalary());
        System.out.println("Rice Subsidy       : PHP " + employee.getRiceSubsidy());
        System.out.println("Phone Allowance    : PHP " + employee.getPhoneAllowance());
        System.out.println("Clothing Allowance : PHP " + employee.getClothingAllowance());
        System.out.println("Gross Semi-monthly Rate: PHP " + employee.getGrossSemiMonthlyRate());
        System.out.println("Hourly Rate        : PHP " + employee.getHourlyRate());
    }

    public static List<EmployeeTimeLogs> getEmployeeTimeLogs(String empId) {
        List<EmployeeTimeLogs> allLogs = DataHandler.readEmployeeTimeLogs(); // Read from CSV
        List<EmployeeTimeLogs> employeeLogs = new ArrayList<>();

        for (EmployeeTimeLogs log : allLogs) {
            if (log.getEmployeeNumber().equals(empId)) {
                employeeLogs.add(log);
            }
        }

        if (employeeLogs.isEmpty()) {
            System.out.println("No time logs found for Employee ID: " + empId);
        }
        return employeeLogs;
    }

}
