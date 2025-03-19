package service;

import model.EmployeeDetails;
import model.EmployeeTimeLogs;
import java.util.List;
import java.util.Scanner;

public class MenuHandler {

    public static void mainMenu() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\nWelcome to MotorPH!");
            System.out.println("--- Menu ---");
            System.out.println("1. Enter Employee ID");
            System.out.println("2. Show All Employees");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    handleEmployeeSelection(scanner);
                    break;
                case 2:
                    EmployeeService.displayAllEmployees();
                    break;
                case 3:
                    System.out.println("Exiting... Goodbye!");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice. Please select again.");
            }
        }
    }

    private static void handleEmployeeSelection(Scanner scanner) {
        while (true) {
            System.out.print("\nEnter Employee ID: ");
            String empId = scanner.nextLine();
            EmployeeDetails employee = EmployeeService.findEmployeeById(empId);

            if (employee != null) {
                System.out.println("\nEmployee Found: " + employee.getFirstName() + " " + employee.getLastName() +
                        " | Position: " + employee.getPosition());

                while (true) {
                    System.out.println("\n--- Employee Menu ---");
                    System.out.println("1. Show Full Details");
                    System.out.println("2. Payroll");
                    System.out.println("3. Select Employee Again");
                    System.out.println("4. Exit");
                    System.out.print("Choose an option: ");
                    int subChoice = scanner.nextInt();
                    scanner.nextLine(); // Consume newline

                    switch (subChoice) {
                        case 1:
                            EmployeeService.showFullDetails(employee);
                            break;
                        case 2:
                            List<EmployeeTimeLogs> logs = EmployeeService.getEmployeeTimeLogs(empId);
                            PayrollService.processPayroll(employee, logs);
                            break;
                        case 3:
                            return; // Go back to entering Employee ID
                        case 4:
                            System.out.println("Exiting... Goodbye!");
                            scanner.close();
                            System.exit(0);
                        default:
                            System.out.println("Invalid choice. Please select again.");
                    }
                }
            } else {
                System.out.println("Employee not found. Try again.");
            }
        }
    }
}
