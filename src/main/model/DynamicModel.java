package model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class DynamicModel {

    private Map<String, Object> data;

    public DynamicModel(ResultSet resultSet) {
        data = new HashMap<>();
        try {
            int columnCount = resultSet.getMetaData().getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                String columnName = resultSet.getMetaData().getColumnName(i);
                String cleanColumnName = columnName.replace(" ", "_").toLowerCase(); // Fix spaces or underscores
                data.put(cleanColumnName, resultSet.getObject(i));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Object get(String columnName) {
        String cleanColumnName = columnName.replace(" ", "_").toLowerCase();
        return data.getOrDefault(cleanColumnName, null);
    }

    public String getString(String columnName) {
        String cleanColumnName = columnName.replace(" ", "_").toLowerCase();
        Object value = data.get(cleanColumnName);
        return value != null ? value.toString() : "";
    }

    public int getInt(String columnName) {
        String cleanColumnName = columnName.replace(" ", "_").toLowerCase();
        Object value = data.get(cleanColumnName);
        try {
            return value != null ? Integer.parseInt(value.toString()) : 0;
        } catch (NumberFormatException e) {
            System.err.println("Error parsing int for column: " + columnName + " - Value: " + value);
            return 0;
        }
    }

    public double getDouble(String columnName) {
        String cleanColumnName = columnName.replace(" ", "_").toLowerCase();
        Object value = data.get(cleanColumnName);

        if (value != null) {
            try {
                return Double.parseDouble(value.toString().replace(",", "")); // Remove commas before parsing
            } catch (NumberFormatException e) {
                System.err.println("Error parsing double for column: " + columnName + " - Value: " + value);
            }
        }
        return 0.0;
    }

    public boolean hasData() {
        return !data.isEmpty();
    }

    public int getId() {
        return getInt("employee_id"); // Default ID column
    }

    @Override
    public String toString() {
        return data.toString();
    }
}
