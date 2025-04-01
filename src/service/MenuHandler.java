package service;

import model.EmployeeDetails;
import model.EmployeeTimeLogs;
import java.util.List;
import java.util.Scanner;

public class MenuHandler {

    // This is the main menu that runs in a loop allowing the user to choose options
    public static void mainMenu() {
        Scanner scanner = new Scanner(System.in); // Create a scanner object to read user input
        while (true) {
            // Displaying the main menu options to the user
            System.out.println("\nWelcome to MotorPH!");
            System.out.println("--- Menu ---");
            System.out.println("1. Enter Employee ID");
            System.out.println("2. Show All Employees");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt(); // Read the user's choice as an integer
            scanner.nextLine(); // Consume the newline character left over by nextInt()

            // Switch case to handle user choice
            switch (choice) {
                case 1:
                    handleEmployeeSelection(scanner); // Go to the employee selection process
                    break;
                case 2:
                    EmployeeService.displayAllEmployees(); // Display all employees
                    break;
                case 3:
                    System.out.println("Exiting... Goodbye!"); // Exit the program
                    scanner.close(); // Close the scanner
                    return; // Exit the method
                default:
                    // Inform user if they entered an invalid option
                    System.out.println("Invalid choice. Please select again.");
            }
        }
    }

    // This method handles employee selection based on the ID entered by the user
    private static void handleEmployeeSelection(Scanner scanner) {
        while (true) {
            // Prompt the user to enter an Employee ID
            System.out.print("\nEnter Employee ID: ");
            String empId = scanner.nextLine();
            // Find employee details based on the entered ID
            EmployeeDetails employee = EmployeeService.findEmployeeById(empId);

            if (employee != null) { // If the employee is found
                // Display basic employee information
                System.out.println("\nEmployee Found: " + employee.getFirstName() + " " + employee.getLastName() +
                        " | Position: " + employee.getPosition());

                while (true) {
                    // Show the employee menu with more options
                    System.out.println("\n--- Employee Menu ---");
                    System.out.println("1. Show Full Details");
                    System.out.println("2. Payroll");
                    System.out.println("3. Select Employee Again");
                    System.out.println("4. Exit");
                    System.out.print("Choose an option: ");

                    String input = scanner.nextLine(); // Read input as a string
                    if (!input.matches("\\d+")) { // Check if input is numeric
                        System.out.println("Invalid choice. Please enter a number between 1 and 4.");
                        continue; // Ask for input again if it's not a valid number
                    }

                    int subChoice = Integer.parseInt(input); // Parse the input into an integer
                    switch (subChoice) {
                        case 1:
                            // Show the full details of the selected employee
                            EmployeeService.showFullDetails(employee);
                            break;
                        case 2:
                            // Retrieve and show the payroll for the selected employee
                            List<EmployeeTimeLogs> logs = EmployeeService.getEmployeeTimeLogs(empId);
                            String monthYear = getValidMonthYear(scanner); // Get a valid month-year for payroll
                            if (monthYear != null) {
                                // Process the payroll for the selected employee
                                PayrollService.processPayroll(employee, logs, monthYear);
                            }
                            break;
                        case 3:
                            return; // Go back to entering Employee ID again
                        case 4:
                            System.out.println("Exiting... Goodbye!");
                            scanner.close(); // Close the scanner
                            System.exit(0); // Exit the program
                        default:
                            // Handle invalid input for employee menu
                            System.out.println("Invalid choice. Please select again.");
                    }
                }
            } else {
                // If the employee was not found, ask to try again
                System.out.println("Employee not found. Try again.");
            }
        }
    }

    // This method ensures the user inputs a valid month and year in MM-YYYY format
    private static String getValidMonthYear(Scanner scanner) {
        while (true) {
            // Prompt the user to enter a valid month and year
            System.out.print("Enter month and year (MM-YYYY): ");
            String monthYear = scanner.nextLine();
            if (monthYear.matches("^(0[1-9]|1[0-2])-2024$")) { // Validate if the input is for 2024
                return monthYear; // Return the valid month-year
            } else if (monthYear.matches("^(0[1-9]|1[0-2])-\\d{4}$")) { // If it's a valid month but not 2024
                System.out.println("No payroll found for the entered year.");
                return null; // Return null as there’s no payroll data for other years
            } else if (monthYear.matches("^(0[1-9]|1[0-2])$")) { // If the user only entered month (no year)
                System.out.println("Invalid date format. Please enter the month and year in the format MM-YYYY.");
            } else {
                // If the input doesn't match the expected format, notify the user
                System.out.println(
                        "Invalid format. Please enter a valid month (01-12) and the year 2024 (e.g., 01-2024).");
            }
        }
    }
}
