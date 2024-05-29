package com.gcu.gameland;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.gcu.gameland.Dialog.FindRoomDialog;
import com.gcu.gameland.Dialog.ProfileChangeDialog;
import com.gcu.gameland.Dialog.ProgressDialog;
import com.gcu.gameland.Dialog.TitleWriteDialog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.List;
import java.util.Random;

import com.gcu.gameland.DTO.RoomData;
import com.gcu.gameland.DTO.UserData;

public class MainActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private final DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");
    private final DatabaseReference roomsRef = FirebaseDatabase.getInstance().getReference().child("rooms");
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final FirebaseUser currentUser = mAuth.getCurrentUser();
    private ProgressDialog progressDialog;
    private ProfileChangeDialog profileChangeDialog;
    private CircularImageView profileImageView;
    private TextView profileNameTextView;
    private Button createRoomBtn;
    private Button enterRoomBtn;
    private Button findRoomBtn;
    private Button changeProfileBtn;
    private Button logoutBtn;
    private UserData myUserData;
    private Uri imageUri;

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

                dialog.setOnConfirmClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String roomName = dialog.getEnteredText();
                        int roomNumber = generateRoomId();
                        RoomData myRoomData = createLobby(roomName, roomNumber);

                        Intent intent = new Intent(getApplicationContext(), LobbyActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("myRoomData", myRoomData);
                        bundle.putSerializable("myUserData", myUserData);
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
                Bundle bundle = new Bundle();
                bundle.putSerializable("myUserData", myUserData);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        });

        findRoomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FindRoomDialog dialog = new FindRoomDialog(MainActivity.this);
                dialog.show();

                dialog.setOnConfirmClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String roomID = dialog.getEnteredText();
                        enterLobby(dialog, roomID);
                    }
                });
            }
        });

        changeProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileChangeDialog = new ProfileChangeDialog(MainActivity.this);
                profileChangeDialog.show();

                profileChangeDialog.setOnConfirmClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String newName = profileChangeDialog.getNameText();
                        if (imageUri != null) {
                            if (newName != null) {
                                myUserData.setNickName(newName);
                            }
                            uploadImage(imageUri);
                        } else {
                            if (newName != null) {
                                myUserData.setNickName(newName);
                                updateUserData(myUserData);
                                updateProfile(myUserData);
                            }
                        }
                        profileChangeDialog.dismiss();
                    }
                });

                profileChangeDialog.setOnUploadClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openFileChooser();
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
        progressDialog.show();
        initializeProfile();
    }

    private void initializeWidgets() {
        progressDialog = new ProgressDialog(MainActivity.this);
        profileImageView = findViewById(R.id.profileCircularImageView);
        profileNameTextView = findViewById(R.id.profileNameTextView);
        createRoomBtn = findViewById(R.id.createRoomButton);
        enterRoomBtn = findViewById(R.id.enterRoomButton);
        findRoomBtn = findViewById(R.id.findRoomButton);
        changeProfileBtn = findViewById(R.id.changeProfileButton);
        logoutBtn = findViewById(R.id.logoutButton);
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
                    updateUserData(myUserData);
                } else {
                    myUserData = snapshot.getValue(UserData.class);
                }

                updateProfile(myUserData);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.hide();
            }
        });

    }

    private void updateProfile(UserData userData) {
        profileNameTextView.setText(userData.getNickName());
        String photoUrl = userData.getImageURL();
        if (photoUrl != null) {
            Glide.with(this).load(photoUrl).into(profileImageView);
        }

        progressDialog.hide();
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Glide.with(this).load(imageUri).into(profileChangeDialog.getImageView());
        }
    }

    private void uploadImage(Uri uri) {
        final StorageReference fileReference = storage.getReference().child("profiles/" + currentUser.getUid() + ".jpg");
        fileReference.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String imageURL = uri.toString();
                                myUserData.setImageURL(imageURL);
                                updateUserData(myUserData);
                                updateProfile(myUserData);
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // 이미지 업로드 실패
                        Toast.makeText(MainActivity.this, "이미지 업로드 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateUserData(UserData userData) {
        DatabaseReference userRef = usersRef.child(currentUser.getUid());
        userRef.setValue(userData);
    }

    private RoomData createLobby(String roomName, int roomNumber) {
        String roomID = Integer.toString(roomNumber);
        String roomAdminID = currentUser.getUid();
        RoomData roomData = new RoomData(roomID, roomName, roomAdminID);
        roomData.addUser(myUserData);
        roomsRef.child(roomID).setValue(roomData);

        return roomData;
    }

    private void enterLobby(FindRoomDialog dialog, String roomId) {
        roomsRef.child(roomId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()) {
                    Toast.makeText(getApplicationContext(), "해당 방이 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    return;
                }

                RoomData roomData = snapshot.getValue(RoomData.class);
                List<UserData> userList = roomData.getUserList();
                userList.add(myUserData);
                roomsRef.child(roomId).child("userList").setValue(userList);

                Intent intent = new Intent(getApplicationContext(), LobbyActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("myRoomData", roomData);
                bundle.putSerializable("myUserData", myUserData);
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