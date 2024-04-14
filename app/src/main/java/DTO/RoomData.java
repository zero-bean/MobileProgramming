package DTO;

public class RoomData {
    private String roomID;
    private String roomName;

    public RoomData(String roomID, String roomName) {
        this.roomID = roomID;
        this.roomName = roomName;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    String getRoomID() {
        return this.roomID;
    }

    String getRoomName() {
        return this.roomName;
    }
}
