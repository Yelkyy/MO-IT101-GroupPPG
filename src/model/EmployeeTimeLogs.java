package model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

public class EmployeeTimeLogs {
    private String employeeNumber;
    private String lastName;
    private String firstName;
    private LocalDate date; // Changed to LocalDate
    private String logIn;
    private String logOut;

    // List of possible date formats
    private static final List<DateTimeFormatter> DATE_FORMATS = Arrays.asList(
            DateTimeFormatter.ofPattern("MM/dd/yyyy"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("dd-MM-yyyy"));

    public EmployeeTimeLogs(String employeeNumber, String lastName, String firstName, String date, String logIn,
            String logOut) {
        this.employeeNumber = employeeNumber;
        this.lastName = lastName;
        this.firstName = firstName;
        this.date = parseDate(date); // Convert to LocalDate
        this.logIn = logIn;
        this.logOut = logOut;
    }

    private LocalDate parseDate(String dateStr) {
        for (DateTimeFormatter formatter : DATE_FORMATS) {
            try {
                return LocalDate.parse(dateStr, formatter);
            } catch (DateTimeParseException e) {
                // Ignore and try the next format
            }
        }
        System.err.println("Invalid date format: " + dateStr);
        return null; // Handle invalid dates properly
    }

    public String getEmployeeNumber() {
        return employeeNumber;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getDate() {
        return (date != null) ? date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "Invalid Date";
    }

    public String getLogIn() {
        return logIn;
    }

    public String getLogOut() {
        return logOut;
    }

    public double getHoursWorked() {
        if (logIn == null || logOut == null || logIn.isEmpty() || logOut.isEmpty()) {
            return 0.0;
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm");
            LocalTime inTime = LocalTime.parse(logIn, formatter);
            LocalTime outTime = LocalTime.parse(logOut, formatter);

            long minutesWorked = Duration.between(inTime, outTime).toMinutes();
            return minutesWorked / 60.0;
        } catch (Exception e) {
            System.err.println("Error processing time log for Employee #" + employeeNumber + ": " + e.getMessage());
            return 0.0;
        }
    }

    @Override
    public String toString() {
        return "Employee #" + employeeNumber + " | Name: " + firstName + " " + lastName +
                " | Date: " + getDate() + " | Log In: " + logIn + " | Log Out: " + logOut +
                " | Hours Worked: " + getHoursWorked();
    }
}
