package com.example.goldenwithui;



import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class SeatingArrangementApp {
    private Scanner scanner;
    private DatabaseManager dbManager;

    public SeatingArrangementApp() {
        scanner = new Scanner(System.in);
        dbManager = new DatabaseManager();
    }



    public void start() {
        while (true) {
            displayMainMenu();
            int choice = getChoiceFromUser(0, 4);
            switch (choice) {
                case 1:
                    createRoom();
                    break;
                case 2:
                    assignStudentToRoom();
                    break;
                case 3:
                    displayRoomDetails();
                    break;
                case 4:
                    displayStudentDetails();
                    break;
                case 0:
                    System.out.println("Exiting the application.");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void displayMainMenu() {
        System.out.println("------ Seating Arrangement Application ------");
        System.out.println("1. Create a Room");
        System.out.println("2. Assign a Student to a Room");
        System.out.println("3. Display Room Details");
        System.out.println("4. Display Student Details");
        System.out.println("0. Exit");
        System.out.print("Enter your choice: ");
    }

    private int getChoiceFromUser(int min, int max) {
        int choice;
        while (true) {
            try {
                choice = Integer.parseInt(scanner.nextLine());
                if (choice >= min && choice <= max) {
                    return choice;
                } else {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a valid choice: ");
            }
        }
    }

    public void createRoom() {
        
        System.out.print("Enter Room Name: ");
        String roomName = scanner.nextLine();
        Room existingRoom = dbManager.getRoomByName(roomName);
        if (existingRoom != null) {
            int assignedStudents = getAssignedStudentCount(roomName);
            int leftSpace = existingRoom.getCapacity() - assignedStudents;
            System.out.println("Error: The room '" + roomName + "' is already selected.");
            System.out.println("Left space in the room: " + leftSpace);
            return;
        }

        System.out.print("Enter Capacity: ");
        int capacity = scanner.nextInt();
        if(capacity==0)
        {
            System.out.println(" capacity can't be empty ");
            return;
        }

        else {
            dbManager.insertRoom(roomName, capacity);
            System.out.println("Room created successfully!");
        }
    }

    


    public void assignStudentToRoom() {
        System.out.print("Enter Student Roll No: ");
        long rollNo = Long.parseLong(scanner.nextLine());
        if (isStudentAlreadyAssigned(rollNo)) {
            System.out.println("A student with Roll No " + rollNo + " is already assigned to a room.");
            return;
        }

        System.out.print("Enter Student Name: ");
        String studentName = scanner.nextLine();

        System.out.print("Enter Room Name: ");
        String roomName = scanner.nextLine();

        Room room = dbManager.getRoomByName(roomName);
        if (room == null) {
            System.out.println("Room does not exist. Please create the room first.");
            return;
        }
        int assignedStudents = getAssignedStudentCount(room.getName());
        int leftSpace = room.getCapacity() - assignedStudents;
        /*if (room == null) {
            System.out.println("Room does not exist. Please create the room first.");
        */ if (leftSpace <= 0) {
            System.out.println("Room capacity is full. Please choose another room.");
            displayRoomDetails(); // Display available rooms to choose from
        } else {
            dbManager.insertStudent(rollNo, studentName, roomName);
            room.setCapacity(room.getCapacity() - 1);
            System.out.println("Student assigned to the room successfully!");
        }
    }

    public boolean isStudentAlreadyAssigned(long rollNo) {
        return dbManager.isStudentAssignedToRoom(rollNo);
    }

    public  void displayRoomDetails() {
        System.out.println("------ Room Details ------");
        List<Room> rooms = dbManager.getAllRooms();
        if (rooms.isEmpty()) {
            System.out.println("No rooms found.");
        } else {

            for (Room room : rooms) {
               int assignedStudents = getAssignedStudentCount(room.getName());
                int leftSpace = room.getCapacity() - assignedStudents;
               // room.setLeftSpace(leftSpace);

                System.out.println("Room Name: " + room.getName() + ", Capacity: " + room.getCapacity() + ", Left Space: " + leftSpace);
            }
        }
    }



    public int getAssignedStudentCount(String roomName) {
        int count = 0;
        List<Student> students = dbManager.getAllStudents();
        for (Student student : students) {
            if (student.getAssignedRoom() != null && student.getAssignedRoom().getName().equals(roomName)) {
                count++;
            }
        }
        return count;
    }


    public void displayStudentDetails() {
        System.out.println("------ Student Details ------");
        List<Student> students = dbManager.getAllStudents();
        if (students.isEmpty()) {
            System.out.println("No students found.");
        } else {
            for (Student student : students) {
                String roomName = (student.getAssignedRoom() != null) ? student.getAssignedRoom().getName() : "Not Assigned";
                System.out.println("Roll No: " + student.getRollNo() + ", Student Name: " + student.getName() + ", Room: " + roomName);
            }
        }
    }


    public DatabaseManager getDatabaseManager() {
        DatabaseManager databaseManager = new DatabaseManager();
        // Perform any initialization or configuration if needed

        return databaseManager;
    }
}
