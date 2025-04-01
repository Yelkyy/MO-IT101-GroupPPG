package service;

import model.EmployeeDetails;
import model.EmployeeTimeLogs;
import repository.DataHandler;
import java.util.List;
import java.util.ArrayList;

public class EmployeeService {

    // This method displays a list of all employees.
    public static void displayAllEmployees() {
        // Retrieve employee details from the data handler (e.g., from CSV or database)
        List<EmployeeDetails> employees = DataHandler.readEmployeeDetails();

        // Check if the employee list is empty, which could mean no data or an error in
        // reading the file
        if (employees.isEmpty()) {
            System.out.println("No employee data found or failed to read CSV.");
        } else {
            // Display a table-like structure for employee data
            System.out.println("\n--- Employee List ---");
            // Print the header for the employee list
            System.out.printf("%-15s %-15s %-15s %-20s\n", "Employee ID", "First Name", "Last Name", "Position");
            System.out.println("----------------------------------------------------------------------------");

            // Loop through each employee and print their details
            for (EmployeeDetails emp : employees) {
                System.out.printf("%-15s %-15s %-15s %-20s\n",
                        emp.getEmployeeNumber(), emp.getFirstName(), emp.getLastName(), emp.getPosition());
            }
        }
    }

    // This method finds an employee by their ID
    public static EmployeeDetails findEmployeeById(String empId) {
        // Retrieve employee details from the data handler
        List<EmployeeDetails> employees = DataHandler.readEmployeeDetails();

        // Loop through each employee to find a match by employee number
        for (EmployeeDetails emp : employees) {
            if (emp.getEmployeeNumber().equals(empId)) {
                return emp; // Return the employee object if found
            }
        }

        // Return null if no employee with the given ID is found
        return null;
    }

    // This method shows the full details of an employee
    public static void showFullDetails(EmployeeDetails employee) {
        // Display employee's detailed information
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

    // This method retrieves time logs for a specific employee based on their ID
    public static List<EmployeeTimeLogs> getEmployeeTimeLogs(String empId) {
        // Retrieve all time logs from the data handler
        List<EmployeeTimeLogs> allLogs = DataHandler.readEmployeeTimeLogs(); // Read from CSV
        List<EmployeeTimeLogs> employeeLogs = new ArrayList<>(); // Initialize an empty list for the employee's logs

        // Loop through all time logs and select logs for the given employee ID
        for (EmployeeTimeLogs log : allLogs) {
            if (log.getEmployeeNumber().equals(empId)) {
                employeeLogs.add(log); // Add matching logs to the employeeLogs list
            }
        }

        // If no time logs are found for the given employee ID, display a message
        if (employeeLogs.isEmpty()) {
            System.out.println("No time logs found for Employee ID: " + empId);
        }

        // Return the list of time logs for the employee
        return employeeLogs;
    }

}
