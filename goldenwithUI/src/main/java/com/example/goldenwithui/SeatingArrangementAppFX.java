package com.example.goldenwithui;



import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class SeatingArrangementAppFX extends Application {
    private DatabaseManager dbManager;

    private SeatingArrangementApp seatingArrangementApp;


    public SeatingArrangementAppFX() {
        dbManager = new DatabaseManager();

        seatingArrangementApp = new SeatingArrangementApp();
    }

    @Override
    public void start(Stage primaryStage) {

            primaryStage.setTitle("Seating Arrangement Application");

            VBox vbox = new VBox();
            vbox.setSpacing(10);
            vbox.setPadding(new Insets(10, 10, 10, 10));

            Button createRoomBtn = new Button("Create a Room");
            createRoomBtn.setOnAction(e -> createRoom(primaryStage));

            Button assignStudentBtn = new Button("Assign a Student to a Room");
                assignStudentBtn.setOnAction(e -> AssignRoomToStudnet(primaryStage));
            Button displayRoomDetailsBtn = new Button("Display Room Details");
        displayRoomDetailsBtn.setOnAction(e -> displayRoomDetails1(primaryStage));
            Button displayStudentDetailsBtn = new Button("Display Student Details");
        displayStudentDetailsBtn.setOnAction(e -> displayStudentDetails1(primaryStage));
            Button exitBtn = new Button("Exit");


            //createRoomBtn.setOnAction(e -> seatingArrangementApp.createRoom());
           // assignStudentBtn.setOnAction(e -> seatingArrangementApp.assignStudentToRoom());
          //  displayRoomDetailsBtn.setOnAction(e -> seatingArrangementApp.displayRoomDetails());
           // displayStudentDetailsBtn.setOnAction(e -> seatingArrangementApp.displayStudentDetails());
            exitBtn.setOnAction(e -> {
                System.out.println("Exiting the application.");
                primaryStage.close();
            });

            vbox.getChildren().addAll(createRoomBtn, assignStudentBtn, displayRoomDetailsBtn, displayStudentDetailsBtn, exitBtn);

            Scene scene = new Scene(vbox, 300, 200);
            primaryStage.setScene(scene);
            primaryStage.show();

    }

    private void displayStudentDetails1(Stage primaryStage) {
        List<Student> students = dbManager.getAllStudents();

        TableView<Student> tableView = new TableView<>(FXCollections.observableArrayList(students));
        TableColumn<Student, Long> rollNoColumn = new TableColumn<>("Roll No");
        TableColumn<Student, String> nameColumn = new TableColumn<>("Student Name");
        TableColumn<Student, String> roomColumn = new TableColumn<>("Room");

        rollNoColumn.setCellValueFactory(new PropertyValueFactory<>("rollNo"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        roomColumn.setCellValueFactory(cellData -> {
            Room assignedRoom = cellData.getValue().getAssignedRoom();
            return new SimpleStringProperty(assignedRoom != null ? assignedRoom.getName() : "Not Assigned");

        });

        tableView.getColumns().addAll(rollNoColumn, nameColumn, roomColumn);

        VBox vbox = new VBox(new Label("------ Student Details ------"), tableView);
        Scene scene = new Scene(vbox);

        Stage newStage = new Stage();
        newStage.setScene(scene);
        newStage.setTitle("Student Details");
        newStage.show();
    }


    public void displayRoomDetails1(Stage primaryStage) {
        List<Room> rooms = dbManager.getAllRooms();

        TableView<Room> tableView = new TableView<>(FXCollections.observableArrayList(rooms));
        TableColumn<Room, String> nameColumn = new TableColumn<>("Room Name");
        TableColumn<Room, Integer> capacityColumn = new TableColumn<>("Capacity");
        TableColumn<Room, Integer> leftSpaceColumn = new TableColumn<>("Left Space");

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        capacityColumn.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        leftSpaceColumn.setCellValueFactory(new PropertyValueFactory<>("leftSpace"));
        for (Room room : rooms) {
            int assignedStudents = seatingArrangementApp.getAssignedStudentCount(room.getName());
            int leftSpace = room.getCapacity() - assignedStudents;
            room.setLeftSpace(leftSpace);
        }


        tableView.getColumns().addAll(nameColumn, capacityColumn, leftSpaceColumn);

        VBox vbox = new VBox(new Label("------ Room Details ------"), tableView);
        Scene scene = new Scene(vbox);

        Stage newStage = new Stage();
        newStage.setScene(scene);
        newStage.setTitle("Room Details");
        newStage.show();
    }


    private void AssignRoomToStudnet(Stage primaryStage) {

        TextInputDialog rollNoDialog = new TextInputDialog();
        rollNoDialog.setTitle("Assign Student to Room");
        rollNoDialog.setHeaderText(null);
        rollNoDialog.setContentText("Enter Student Roll No:");
        rollNoDialog.showAndWait().ifPresent(rollNoStr -> {
            try {
                long rollNo = Long.parseLong(rollNoStr);
                if (seatingArrangementApp.isStudentAlreadyAssigned(rollNo)) {
                    System.out.println("A student with Roll No " + rollNo + " is already assigned to a room.");
                    displayAlert(Alert.AlertType.ERROR, "This Roll Number Exist", "please Check your Roll Number");
                    return;
                }

                TextInputDialog nameDialog = new TextInputDialog();
                nameDialog.setTitle("Assign Student to Room");
                nameDialog.setHeaderText(null);
                nameDialog.setContentText("Enter Student Name:");
                nameDialog.showAndWait().ifPresent(studentName -> {
                    TextInputDialog roomDialog = new TextInputDialog();
                    roomDialog.setTitle("Assign Student to Room");
                    roomDialog.setHeaderText(null);
                    roomDialog.setContentText("Enter Room Name:");
                    roomDialog.showAndWait().ifPresent(roomName -> {

                        Room room = dbManager.getRoomByName(roomName);
                        if (room == null) {
                            displayAlert(Alert.AlertType.ERROR, "Room Not Found", "Room does not exist. Please create the room first and enter correct room name.");
                            return;
                        }
                        int assignedStudents = seatingArrangementApp.getAssignedStudentCount(roomName);
                        int leftSpace = room.getCapacity() - assignedStudents;
                         if (leftSpace <= 0) {
                            displayAlert(Alert.AlertType.ERROR, "Room Capacity Full", "Room capacity is full. Please choose another room.");
                             displayRoomDetails1(primaryStage); // Display available rooms to choose from
                        } else {

                            dbManager.insertStudent(rollNo, studentName, roomName);
                            room.setCapacity(room.getCapacity() - 1);
                            displayAlert(Alert.AlertType.INFORMATION, "Success", "Student assigned to the room successfully!");

                            // Open a new page with student details
                            showStudentDetailsPage(rollNo, studentName, roomName);
                        }
                    });
                });
            } catch (NumberFormatException ex) {
                displayAlert(Alert.AlertType.ERROR, "Invalid Roll No", "Please enter a valid Roll No.");
            }
        });

    }
    private void showStudentDetailsPage(long rollNo, String studentName, String roomName) {
        Stage studentDetailsStage = new Stage();
        studentDetailsStage.setTitle("Student Details");

        VBox vbox = new VBox();
        vbox.setSpacing(10);
        vbox.setPadding(new Insets(10, 10, 10, 10));

        Label rollNoLabel = new Label("Roll No: " + rollNo);
        Label nameLabel = new Label("Name: " + studentName);
        Label roomLabel = new Label("Room: " + roomName);

        vbox.getChildren().addAll(rollNoLabel, nameLabel, roomLabel);

        Scene scene = new Scene(vbox, 300, 200);
        studentDetailsStage.setScene(scene);
        studentDetailsStage.show();
    }
    private void displayAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }


    public void createRoom(Stage primaryStage) {
        Stage resultStage = new Stage();
        resultStage.setTitle("Create a Room");

      //  primaryStage.setTitle("Create a Room");

        VBox vbox = new VBox();
        vbox.setSpacing(10);
        vbox.setPadding(new Insets(10, 10, 10, 10));

        Label nameLabel = new Label("Enter Room Name:");
        TextField nameField = new TextField();

        Label capacityLabel = new Label("Enter Capacity:");
        TextField capacityField = new TextField();

        Button createBtn = new Button("Create");
        createBtn.setOnAction(e -> {
            String roomName = nameField.getText().trim();
            if (roomName.isEmpty()) {
                displayAlert(Alert.AlertType.ERROR, "Invalid Input", "Room name can't be empty.");
                return;
            }

            Room existingRoom = dbManager.getRoomByName(roomName);
            if (existingRoom != null) {
                int assignedStudents = seatingArrangementApp.getAssignedStudentCount(roomName);
                int leftSpace = existingRoom.getCapacity() - assignedStudents;
                displayAlert(Alert.AlertType.ERROR, "Room Already Selected",
                        "The room '" + roomName + "' is already selected.\nLeft space in the room: " + leftSpace);
                return;
            }

            String capacityStr = capacityField.getText().trim();
            if (capacityStr.isEmpty()) {
                displayAlert(Alert.AlertType.ERROR, "Invalid Input", "Capacity can't be empty.");
                return;
            }

            try {
                int capacity = Integer.parseInt(capacityStr);
                if (capacity <= 0) {
                    displayAlert(Alert.AlertType.ERROR, "Invalid Input", "Capacity must be greater than 0.");
                    return;
                }

                dbManager.insertRoom(roomName, capacity);
                showResult("Room Created", "Room '" + roomName + "' with capacity " + capacity + " created successfully!");
            } catch (NumberFormatException ex) {
                displayAlert(Alert.AlertType.ERROR, "Invalid Input", "Invalid capacity value. Please enter a valid integer.");
            }
        });

        vbox.getChildren().addAll(nameLabel, nameField, capacityLabel, capacityField, createBtn);

        Scene scene = new Scene(vbox, 300, 200);
       // primaryStage.setScene(scene);
        resultStage.setScene(scene);
     //   primaryStage.show();
        resultStage.show();
    }



    private void showResult(String title, String content) {
        Stage resultStage = new Stage();
        resultStage.setTitle(title);

        VBox vbox = new VBox();
        vbox.setSpacing(10);
        vbox.setPadding(new Insets(10, 10, 10, 10));

        Label resultLabel = new Label(content);
        vbox.getChildren().add(resultLabel);

        Scene scene = new Scene(vbox, 300, 100);
        resultStage.setScene(scene);
        resultStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
