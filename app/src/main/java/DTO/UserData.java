package DTO;

public class UserData {
    private String UID;
    private String nickName;
    private String imageURL;

    public UserData() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public UserData(String UID, String nickName, String imageURL) {
        this.UID = UID;
        this.nickName = nickName;
        this.imageURL = imageURL;
    }

    public void setUID(String UID) { this.UID = UID;}
    public void setNickName(String nickName) {this.nickName = nickName;}
    public void setImageURL(String imageURL) {this.imageURL = imageURL;}

    public String getUID() { return UID; }
    public String getNickName() { return nickName; }
    public String getImageURL() { return imageURL; }
}
