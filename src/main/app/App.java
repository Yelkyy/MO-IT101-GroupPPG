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
                System.out.print("Enter Month (MM): ");
                String month = scanner.nextLine();
                System.out.print("Enter Year (YYYY): ");
                String year = scanner.nextLine();

                List<DynamicModel> attendance = repository.getAttendanceByMonthYear(employee.getInt("employee_id"),
                        month, year);
                if (attendance.isEmpty()) {
                    System.out.println("No attendance records found for " + month + "/" + year);
                } else {
                    repository.calculatePayroll(employee, attendance, month, year);
                }

                System.out.println("\n[1] Search Another Month/Year");
                System.out.println("[2] Search Another Employee");
                System.out.println("[3] Exit");
                System.out.print("Choose an option:");
                String option = scanner.nextLine();

                if (option.equals("1")) {
                    continue;
                } else if (option.equals("2")) {
                    break; // Go back to employee input
                } else {
                    System.out.println("Exiting... Goodbye!");
                    System.exit(0);
                }
            }
        }
    }
}