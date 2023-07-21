package com.example.goldenwithui;



import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    // Database connection details
    private static final String DB_URL = "jdbc:mysql://localhost:3306/seating_arrangement";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Mehtab@786";

    // Establishes the database connection
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    // Close the database connection
    public static void closeConnection(Connection conn) {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Insert a new room into the "rooms" table
    public static void insertRoom(String roomName, int capacity) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO rooms (room_name, capacity) VALUES (?, ?)")) {
            stmt.setString(1, roomName);
            stmt.setInt(2, capacity);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // Insert a new student into the "students" table
    public static void insertStudent(long rollNo, String studentName, String roomName) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO students (roll_no, student_name, room_name) VALUES (?, ?, ?)")) {
            stmt.setLong(1, rollNo);
            stmt.setString(2, studentName);
            stmt.setString(3, roomName);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Get room details by name
    public static Room getRoomByName(String roomName) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM rooms WHERE room_name = ?")) {
            stmt.setString(1, roomName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int capacity = rs.getInt("capacity");
                return new Room(roomName, capacity);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Room> getAllRooms() {
        List<Room> rooms = new ArrayList<>();
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT * FROM rooms");
            while (rs.next()) {
                String roomName = rs.getString("room_name");
                int capacity = rs.getInt("capacity");
                Room room = new Room(roomName, capacity);
                rooms.add(room);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rooms;
    }

    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT * FROM students");
            while (rs.next()) {
                long rollNo = rs.getLong("roll_no");
                String studentName = rs.getString("student_name");
                String roomName = rs.getString("room_name");
                Room assignedRoom = getRoomByName(roomName);
                Student student = new Student(rollNo, studentName);
                student.assignRoom(assignedRoom);
                students.add(student);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }

    public boolean isStudentAssignedToRoom(long rollNo) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM students WHERE roll_no = ?")) {
            stmt.setLong(1, rollNo);
            ResultSet rs = stmt.executeQuery();
            return rs.next(); // If the ResultSet has at least one row, it means the student is assigned to a room.
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

  /*  public void deleteRoom(Room room) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM rooms WHERE room_name = ?")) {
            stmt.setString(1, room.getName());
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Room '" + room.getName() + "' has been deleted successfully!");
            } else {
                System.out.println("Error: Room '" + room.getName() + "' not found or already deleted.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }*/

    // Add other methods to perform SQL operations (e.g., select, update, delete) here
}

