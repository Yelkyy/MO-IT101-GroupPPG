package MotorPH;

public class EmployeeDetails {
    private static String[][] employees = {
            { "10001", "Manuel III Garcia", "10-11-1983" },
            { "10002", "Anotino Lim", "06-19-1988" },
            { "10003", "Bianca Sofia Lim", "08-04-1989" },
            { "10004", "Isabella Reyes", "06-16-1994" },
            { "10005", "Eduard Hernandez", "09-23-1989" },
            { "10006", "Andrea Mae Villanueva", "02-14-1988" },
            { "10007", "Brad San Jose", "03-15-1996" },
            { "10008", "Alice Romualdez", "05-14-1992" },
            { "10009", "Rosie Atienza", "09-24-1948" },
            { "10010", "Roderick Alvaro", "03-30-1988" },
            { "10011", "Anthony Salcedo", "09-14-1993" },
            { "10012", "Josie Lopez", "01-14-1987" },
            { "10013", "Martha Farala", "01-11-1942" },
            { "10014", "Leila Martinez", "07-11-1970" },
            { "10015", "Fredrick Romualdez", "03-10-1985" },
            { "10016", "Christian Mata", "10-21-1987" },
            { "10017", "Selena De Leon", "02-20-1975" },
            { "10018", "Allison San Jose", "06-24-1986" },
            { "10019", "Cydney Rosario", "010-06-1966" },
            { "10020", "Mark Bautista", "02-12-1991" },
            { "10021", "Darlene Lazaro", "11-25-1985" },
            { "10022", "Kolby Delos Santos", "02-26-1980" },
            { "10023", "Vella Santos", "12-31-1983" },
            { "10024", "Tomas Del Rosario", "12-18-1978" },
            { "10025", "Jacklyn Tolentino", "05-19-1984" },
            { "10026", "Percival Gutierrez", "12-18-1978" },
            { "10027", "Garfield Manalaysay", "08-28-1986" },
            { "10028", "Lizeth Villegas", "12-12-1981" },
            { "10029", "Carol Ramos", "08-20-1978" },
            { "10030", "Emelia Maceda", "04-14-1973" },
            { "10031", "Delia Aguilar", "01-27-1989" },
            { "10032", "John Rafael Castro", "02-09-1992" },
            { "10033", "Carlos Ian Martinez", "11-16-1990" },
            { "10034", "Beatriz Santos", "08-07-1990" },

    };

    public static void main(String[] args) {
        // Entry point of the application
        System.out.println("Welcome to MotorPH System");

        // Display the list of employees
        displayEmployeeList();
    }

    public static void displayEmployeeList() {
        System.out.println("Employee List:");
        System.out.println("ID\tName\t\tBirthday");
        System.out.println("---------------------------------------------");
        for (String[] employee : employees) {
            System.out.println(employee[0] + "\t" + employee[1] + "\t" + employee[2]);
        }
    }

    public static String[] findEmployeeById(String id) {
        for (String[] employee : employees) {
            if (employee[0].equals(id)) {
                return employee;
            }
        }
        return null;
    }
}