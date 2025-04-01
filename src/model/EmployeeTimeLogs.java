package model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

public class EmployeeTimeLogs {

    // Employee time log details: personal info and time logs
    private String employeeNumber;
    private String lastName;
    private String firstName;
    private LocalDate date; // Changed to LocalDate for better date handling
    private String logIn;
    private String logOut;

    // List of possible date formats to handle different formats
    private static final List<DateTimeFormatter> DATE_FORMATS = Arrays.asList(
            DateTimeFormatter.ofPattern("MM/dd/yyyy"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("dd-MM-yyyy"));

    // Constructor to initialize EmployeeTimeLogs with data
    public EmployeeTimeLogs(String employeeNumber, String lastName, String firstName, String date, String logIn,
            String logOut) {
        this.employeeNumber = employeeNumber;
        this.lastName = lastName;
        this.firstName = firstName;
        this.date = parseDate(date); // Convert the string date to LocalDate
        this.logIn = logIn;
        this.logOut = logOut;
    }

    // Try to parse the date string into a LocalDate, based on different formats
    private LocalDate parseDate(String dateStr) {
        for (DateTimeFormatter formatter : DATE_FORMATS) {
            try {
                return LocalDate.parse(dateStr, formatter); // Parse using current format
            } catch (DateTimeParseException e) {
                // Ignore and try the next format if the current one fails
            }
        }
        System.err.println("Invalid date format: " + dateStr); // Error message if no format matches
        return null; // Return null if date is invalid
    }

    // Getter methods to retrieve employee info and time logs

    public String getEmployeeNumber() {
        return employeeNumber;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    // Returns the date as a formatted string, or "Invalid Date" if the date is null
    public String getDate() {
        return (date != null) ? date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "Invalid Date";
    }

    public String getLogIn() {
        return logIn;
    }

    public String getLogOut() {
        return logOut;
    }

    // Method to calculate the hours worked based on log-in and log-out times
    public double getHoursWorked() {
        // If log-in or log-out is empty, return 0 hours
        if (logIn == null || logOut == null || logIn.isEmpty() || logOut.isEmpty()) {
            return 0.0;
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm"); // Time format pattern
            LocalTime inTime = LocalTime.parse(logIn, formatter); // Parse log-in time
            LocalTime outTime = LocalTime.parse(logOut, formatter); // Parse log-out time

            // Calculate the time worked in minutes and convert to hours
            long minutesWorked = Duration.between(inTime, outTime).toMinutes();
            return minutesWorked / 60.0; // Return hours worked as a double
        } catch (Exception e) {
            System.err.println("Error processing time log for Employee #" + employeeNumber + ": " + e.getMessage());
            return 0.0; // Return 0 if there is any error in parsing or calculation
        }
    }

    // Method to print a readable representation of the employee's time log
    @Override
    public String toString() {
        return "Employee #" + employeeNumber + " | Name: " + firstName + " " + lastName +
                " | Date: " + getDate() + " | Log In: " + logIn + " | Log Out: " + logOut +
                " | Hours Worked: " + getHoursWorked();
    }
}
