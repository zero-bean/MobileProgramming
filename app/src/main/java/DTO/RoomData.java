package DTO;

public class RoomData {
    private String roomID;
    private String roomName;
    private String roomAdminID;
    private int headCount;

    public RoomData() {

    }

    public RoomData(String roomID, String roomName, String roomAdminID, int headCount) {
        this.roomID = roomID;
        this.roomName = roomName;
        this.roomAdminID = roomAdminID;
        this.headCount = headCount;
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
    public void setHeadCount(int headCount) {
        this.headCount = headCount;
    }

    public String getRoomID() {
        return this.roomID;
    }
    public String getRoomName() {
        return this.roomName;
    }
    public String getRoomAdminID() { return this.roomAdminID; }
    public int getHeadCount() { return this.headCount;}
}
