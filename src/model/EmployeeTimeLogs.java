package model;

import java.time.LocalTime;
import java.time.Duration;
import java.time.format.DateTimeFormatter;

public class EmployeeTimeLogs {
    private String employeeNumber;
    private String lastName;
    private String firstName;
    private String date;
    private String logIn;
    private String logOut;

    public EmployeeTimeLogs(String employeeNumber, String lastName, String firstName, String date, String logIn,
            String logOut) {
        this.employeeNumber = employeeNumber;
        this.lastName = lastName;
        this.firstName = firstName;
        this.date = date;
        this.logIn = logIn;
        this.logOut = logOut;
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
        return date;
    }

    public String getLogIn() {
        return logIn;
    }

    public String getLogOut() {
        return logOut;
    }

    // NEW METHOD: Calculate total hours worked
    public double getHoursWorked() {
        if (logIn == null || logOut == null || logIn.isEmpty() || logOut.isEmpty()) {
            return 0.0; // If no logs, return 0 hours
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm"); // Example: "08:30"
            LocalTime inTime = LocalTime.parse(logIn, formatter);
            LocalTime outTime = LocalTime.parse(logOut, formatter);

            // Calculate duration in minutes
            long minutesWorked = Duration.between(inTime, outTime).toMinutes();

            // Convert minutes to decimal hours
            return minutesWorked / 60.0;
        } catch (Exception e) {
            System.err.println("Error parsing time for Employee #" + employeeNumber);
            return 0.0;
        }
    }

    @Override
    public String toString() {
        return "Employee #" + employeeNumber + " | Name: " + firstName + " " + lastName +
                " | Date: " + date + " | Log In: " + logIn + " | Log Out: " + logOut +
                " | Hours Worked: " + getHoursWorked();
    }
}
