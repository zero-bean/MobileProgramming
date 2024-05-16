package com.gcu.gameland.DTO;

import java.io.Serializable;
import java.util.ArrayList;

public class RoomData implements Serializable {
    private String roomID;
    private String roomName;
    private String roomAdminID;
    private String selectedGame;

    private ArrayList<String> userList;

    public RoomData() {
        userList = new ArrayList<>();
        selectedGame = null;
    }
    
    public RoomData(String roomID, String roomName, String roomAdminID) {
        this.roomID = roomID;
        this.roomName = roomName;
        this.roomAdminID = roomAdminID;
        userList = new ArrayList<>();
        selectedGame = null;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }
    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }
    public void setRoomAdminID(String roomAdminID) {
        this.roomAdminID = roomAdminID;
    }
    public void SetSelectedGame(String selectedGame) { this.selectedGame = selectedGame; }

    public String getRoomID() {
        return this.roomID;
    }
    public String getRoomName() {
        return this.roomName;
    }
    public String getRoomAdminID() { return this.roomAdminID; }
    public ArrayList<String> getUserList() { return this.userList; }
    public String getSelectedGame() { return this.selectedGame; }

    public void addUser(String user) { userList.add(user); }
}
