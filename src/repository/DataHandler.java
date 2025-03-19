package repository;

import model.EmployeeDetails;
import model.EmployeeTimeLogs;

import com.opencsv.*;
import com.opencsv.exceptions.CsvException;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class DataHandler {
    private static final String CSV_FILE = "resources\\Copy of MotorPH Employee Data.csv";
    private static final String TIME_LOG_CSV = "resources\\Copy of MotorPH Employee Data Time Logs.csv";

    public static List<EmployeeDetails> readEmployeeDetails() {
        List<EmployeeDetails> employees = new ArrayList<>();
        try (Reader reader = Files.newBufferedReader(Paths.get(CSV_FILE));
                CSVReader csvReader = new CSVReader(reader)) {

            List<String[]> records = csvReader.readAll();
            records.remove(0); // Remove header row

            for (String[] record : records) {
                EmployeeDetails employee = new EmployeeDetails(
                        record[0], record[1], record[2], record[3], record[4],
                        record[5], record[6], record[7], record[8], record[9],
                        record[10], record[11], record[12],
                        Double.parseDouble(record[13].replace(",", "")), // Basic Salary
                        Double.parseDouble(record[14].replace(",", "")), // Rice Subsidy
                        Double.parseDouble(record[15].replace(",", "")), // Phone Allowance
                        Double.parseDouble(record[16].replace(",", "")), // Clothing Allowance
                        Double.parseDouble(record[17].replace(",", "")), // Gross Semi-monthly Rate
                        Double.parseDouble(record[18].replace(",", "")) // Hourly Rate
                );
                employees.add(employee);
            }
        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }
        return employees;
    }

    public static List<EmployeeTimeLogs> readEmployeeTimeLogs() {
        List<EmployeeTimeLogs> timeLogs = new ArrayList<>();
        try (Reader reader = Files.newBufferedReader(Paths.get(TIME_LOG_CSV));
                CSVReader csvReader = new CSVReader(reader)) {

            List<String[]> records = csvReader.readAll();

            if (records.isEmpty()) {
                System.out.println("Warning: No records found in Time Logs CSV.");
                return timeLogs;
            }

            records.remove(0); // Remove header row

            for (String[] record : records) {
                if (record.length < 6) { // Check if all columns exist
                    System.out.println("Skipping malformed row: " + Arrays.toString(record));
                    continue;
                }

                EmployeeTimeLogs timeLog = new EmployeeTimeLogs(
                        record[0], record[1], record[2], record[3], record[4], record[5]);
                timeLogs.add(timeLog);
            }
        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }
        return timeLogs;
    }

    // ✅ NEW METHOD: Write Employee Time Log to CSV
    public static void logEmployeeTime(String empId, String lastName, String firstName, String date, String logIn,
            String logOut) {
        File file = new File(TIME_LOG_CSV);
        boolean fileExists = file.exists();

        try (Writer writer = new FileWriter(file, true);
                CSVWriter csvWriter = new CSVWriter(writer,
                        CSVWriter.DEFAULT_SEPARATOR,
                        CSVWriter.NO_QUOTE_CHARACTER,
                        CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                        CSVWriter.RFC4180_LINE_END)) {

            // Add header if file is newly created
            if (!fileExists) {
                csvWriter.writeNext(
                        new String[] { "Employee #", "Last Name", "First Name", "Date", "Log In", "Log Out" });
            }

            // Append new time log entry
            csvWriter.writeNext(new String[] { empId, lastName, firstName, date, logIn, logOut });
            System.out.println("✅ Time log recorded for Employee #" + empId);

        } catch (IOException e) {
            System.out.println("❌ Error writing time log: " + e.getMessage());
        }
    }
}
