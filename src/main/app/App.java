package app;

import model.DynamicModel;
import repository.EmployeeRepository;

import java.util.List;
import java.util.Scanner;

public class App {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        EmployeeRepository repository = new EmployeeRepository();

        while (true) {
            System.out.println("\nWelcome to MotorPH");
            System.out.print("Enter Employee ID or Name to search: ");
            String input = scanner.nextLine();

            List<DynamicModel> employees = repository.findEmployeeByIdOrName(input);
            if (employees.isEmpty()) {
                System.out.println("No employee found with ID or Name: " + input);
                continue;
            }

            DynamicModel employee = employees.get(0);
            System.out.println("Employee Found!\n");
            System.out.println("Full Name: " + employee.get("first_name") + " " + employee.get("last_name"));
            System.out.println("Position: " + employee.get("position"));

            while (true) {
                String month;
                while (true) {
                    System.out.print("Enter Month (MM): ");
                    month = scanner.nextLine();
                    if (month.matches("0[1-9]|1[0-2]")) {
                        break;
                    } else {
                        System.out.println("Invalid month. Please enter a value between 01 and 12.");
                    }
                }

                System.out.print("Enter Year (YYYY): ");
                String year = scanner.nextLine();

                System.out.print("Do you want a [1] Single Week or [2] Week Range? ");
                String choice = scanner.nextLine();

                int startWeek, endWeek;
                if (choice.equals("1")) {
                    System.out.print("Enter Week Number: ");
                    startWeek = Integer.parseInt(scanner.nextLine());
                    endWeek = startWeek;
                } else if (choice.equals("2")) {
                    System.out.print("Enter Start Week Number: ");
                    startWeek = Integer.parseInt(scanner.nextLine());
                    System.out.print("Enter End Week Number: ");
                    endWeek = Integer.parseInt(scanner.nextLine());

                    if (endWeek < startWeek) {
                        System.out.println("Invalid range! End week must be greater than or equal to start week.");
                        continue;
                    }
                } else {
                    System.out.println("Invalid choice. Please try again.");
                    continue;
                }

                List<DynamicModel> attendance = repository.getAttendanceByWeekRange(employee.getInt("employee_id"),
                        month, year, startWeek, endWeek);

                if (attendance.isEmpty()) {
                    System.out.println("No attendance records found for " + month + "/" + year +
                            ", weeks " + startWeek + " to " + endWeek);
                } else {
                    boolean isSingleWeek = startWeek == endWeek; // Check if it's a single week selection
                    repository.calculateWeeklyPayroll(employee, attendance, month, year, startWeek, endWeek,
                            isSingleWeek);

                }

                System.out.println("\n------- MENU -------");
                System.out.println("\n[1] Search Another Month/Year");
                System.out.println("[2] Search Another Employee");
                System.out.println("[3] Exit");
                System.out.print("\nChoose an option: ");
                String option = scanner.nextLine();
                System.out.println("\n--------------");

                if (option.equals("1")) {
                    continue;
                } else if (option.equals("2")) {
                    break; // Go back to employee input
                } else {
                    System.out.println("Goodbye!");
                    System.exit(0);
                }
            }
        }
    }
}
