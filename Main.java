package MotorPH;

import java.util.Scanner;

public class Main {
    private static Scanner inputVal = new Scanner(System.in);
    private static boolean exitprogram = false;

    public static void main(String[] args) {
        // Displaying the welcome message or the entry point of the application
        System.out.println("Welcome to MotorPH System");

        while (!exitprogram) {
            // Display the menu options or the dashboard
            System.out.println("\nMenu:");
            System.out.println("1. Search Employee Details");
            System.out.println("2. Exit");
            System.out.print("Please select an option: ");

            // Read the user's input
            int choice = inputVal.nextInt();
            inputVal.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    // Search Employee Details
                    searchEmployeeDetails();
                    break;
                case 2:
                    // Exit the program
                    exitprogram = true;
                    System.out.println("Exiting the program. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }

        inputVal.close();
    }

    private static void searchEmployeeDetails() {
        boolean searchAgain = true;

        while (searchAgain) {
            System.out.print("Enter employee ID to search: ");
            String id = inputVal.nextLine();

            String[] employee = EmployeeDetails.findEmployeeById(id);
            if (employee != null) {
                System.out.println("\nEmployee Details:");
                System.out.println("Employee ID: " + employee[0]);
                System.out.println("Employee Name: " + employee[1]);
                System.out.println("Employee Birthday: " + employee[2]);
            } else {
                System.out.println("Employee not found.");
            }

            // Provide options to search again or go back to the main menu
            System.out.println("\nOptions:");
            System.out.println("1. Search Again");
            System.out.println("2. Back to Main Menu");
            System.out.println("3. Exit Program");
            System.out.print("Please select an option: ");

            int choice = inputVal.nextInt();
            inputVal.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    // Search again
                    searchAgain = true;
                    break;
                case 2:
                    // Back to main menu
                    searchAgain = false;
                    break;
                case 3:
                    // Exit the program
                    searchAgain = false;
                    exitprogram = true;
                    System.out.println("Exiting the program. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid option. Returning to main menu.");
                    searchAgain = false;
                    break;
            }
        }
    }
}