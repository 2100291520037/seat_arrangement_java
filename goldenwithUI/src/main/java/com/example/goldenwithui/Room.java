package com.example.goldenwithui;





public class Room {
    private String name;
    private int capacity;
    private int leftSpace;

    public Room(String name, int capacity) {
        this.name = name;
        this.capacity = capacity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
    public int getLeftSpace() {
        return leftSpace;
    }

    public void setLeftSpace(int leftSpace) {
        this.leftSpace = leftSpace;
    }
}
