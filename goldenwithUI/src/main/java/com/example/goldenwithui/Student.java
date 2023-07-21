package com.example.goldenwithui;




public class Student {
    private long rollNo;
    private String name;
    private Room assignedRoom;

    public Student(long rollNo, String name) {
        this.rollNo = rollNo;
        this.name = name;
    }

    public long getRollNo() {
        return rollNo;
    }

    public void setRollNo(long rollNo) {
        this.rollNo = rollNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Room getAssignedRoom() {
        return assignedRoom;
    }

    public void assignRoom(Room room) {
        this.assignedRoom = room;
    }
}
