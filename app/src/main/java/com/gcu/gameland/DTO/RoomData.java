package com.gcu.gameland.DTO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RoomData implements Serializable {
    private String roomID;
    private String roomName;
    private String roomAdminID;
    private GameData gameData;

    private List<UserData> userList;

    public RoomData() {
        userList = new ArrayList<UserData>();
    }
    
    public RoomData(String roomID, String roomName, String roomAdminID) {
        this.roomID = roomID;
        this.roomName = roomName;
        this.roomAdminID = roomAdminID;
        userList = new ArrayList<UserData>();
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
    public void setGameData(GameData gameData) { this.gameData = gameData; }
    public void setUserList(List<UserData> userList) { this.userList = userList; }

    public String getRoomID() {
        return this.roomID;
    }
    public String getRoomName() {
        return this.roomName;
    }
    public String getRoomAdminID() { return this.roomAdminID; }
    public List<UserData> getUserList() { return this.userList; }
    public GameData getGameData() { return this.gameData; }

    public void addUser(UserData user) { userList.add(user); }
    public void deleteUser(UserData user) {
        userList.remove(user);
    }
}
