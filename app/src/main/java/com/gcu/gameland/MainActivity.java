package com.gcu.gameland;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.gcu.gameland.Dialog.FindRoomDialog;
import com.gcu.gameland.Dialog.TitleWriteDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;
import java.util.Random;

import com.gcu.gameland.DTO.RoomData;
import com.gcu.gameland.DTO.UserData;

public class MainActivity extends AppCompatActivity {
    private final DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");
    private final DatabaseReference roomsRef = FirebaseDatabase.getInstance().getReference().child("rooms");
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final FirebaseUser currentUser = mAuth.getCurrentUser();
    private UserData myUserData;
    private CircularImageView profileImageView;
    private TextView profileNameTextView;
    private Button createRoomBtn;
    private Button enterRoomBtn;
    private Button findRoomBtn;
    private Button changeProfileBtn;
    private Button logoutBtn;
    private Button enterStoreBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeWidgets();

        createRoomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TitleWriteDialog dialog = new TitleWriteDialog(MainActivity.this);
                dialog.show();
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                dialog.setOnConfirmClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String roomName = dialog.getEnteredText();
                        int roomNumber = generateRoomId();
                        createLobby(roomName, roomNumber);

                        Intent intent = new Intent(getApplicationContext(), LobbyActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putInt("roomID", roomNumber);
                        bundle.putString("roomName", roomName);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        });

        enterRoomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RoomListActivity.class);
                startActivity(intent);
                finish();
            }
        });

        findRoomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FindRoomDialog dialog = new FindRoomDialog(MainActivity.this);
                dialog.show();
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                dialog.setOnConfirmClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String roomID = dialog.getEnteredText();
                        enterLobby(dialog, roomID);
                    }
                });
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                mAuth.signOut();
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        initializeProfile();
    }

    private void initializeWidgets() {
        profileImageView = findViewById(R.id.profileCircularImageView);
        profileNameTextView = findViewById(R.id.profileNameTextView);
        createRoomBtn = findViewById(R.id.createRoomButton);
        enterRoomBtn = findViewById(R.id.enterRoomButton);
        findRoomBtn = findViewById(R.id.findRoomButton);
        changeProfileBtn = findViewById(R.id.changeProfileButton);
        logoutBtn = findViewById(R.id.logoutButton);
        enterStoreBtn = findViewById(R.id.enterStoreButton);
    }

    private void initializeProfile() {
        DatabaseReference userRef = usersRef.child(currentUser.getUid());
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    String UID = currentUser.getUid();
                    String name = currentUser.getDisplayName();
                    String image = currentUser.getPhotoUrl() != null ? currentUser.getPhotoUrl().toString() : null;
                    myUserData = new UserData(UID, name, image);

                    userRef.setValue(myUserData);
                } else {
                    myUserData = snapshot.getValue(UserData.class);
                }

                updateProfile(myUserData);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void updateProfile(UserData userData) {
        profileNameTextView.setText(userData.getNickName());

        String photoUrl = userData.getImageURL();
        if (photoUrl != null) {
            Glide.with(this).load(photoUrl).into(profileImageView);
        }
    }

    private void createLobby(String roomName, int roomNumber) {
        String roomID = Integer.toString(roomNumber);
        String roomAdminID = currentUser.getUid();
        RoomData roomData = new RoomData(roomID, roomName, roomAdminID);
        roomData.addUser(roomAdminID);
        roomsRef.child(roomID).setValue(roomData);
    }

    private void enterLobby(FindRoomDialog dialog, String roomId) {
        roomsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()) {
                    Toast.makeText(getApplicationContext(), "해당 방이 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    return;
                }

                RoomData roomInfo = snapshot.getValue(RoomData.class);
                ArrayList<String> userList = roomInfo.getUserList();
                userList.add(currentUser.getUid());
                roomsRef.child("userList").setValue(userList);

                Intent intent = new Intent(getApplicationContext(), LobbyActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("roomID", Integer.parseInt(roomId));
                bundle.putString("roomName", roomInfo.getRoomName());
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // ...
            }
        });
    }

    private boolean roomExists(int roomNumber) {
        final boolean[] exists = {false};
        roomsRef.child(Integer.toString(roomNumber)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                exists[0] = dataSnapshot.exists();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // ...
            }
        });
        return exists[0];
    }

    private int generateRoomId() {
        Random random = new Random();
        int roomNumber;

        do {
            roomNumber = random.nextInt(10000);
        } while (roomExists(roomNumber));

        return roomNumber;
    }
}