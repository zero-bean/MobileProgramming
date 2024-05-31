package com.gcu.gameland;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.gcu.gameland.DTO.GameData;
import com.gcu.gameland.Dialog.ProgressDialog;
import com.gcu.gameland.Dialog.SelectGameDialog;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import com.gcu.gameland.DTO.RoomData;
import com.gcu.gameland.DTO.UserData;

public class LobbyActivity extends AppCompatActivity {
    private DatabaseReference roomRef;
    private ListView listView;
    private LobbyUserListAdapter adapter;
    private ProgressDialog progressDialog;
    private MaterialToolbar toolbar;
    private Button selectGameBtn;
    private Button startGameBtn;
    private RoomData myRoomData;
    private UserData myUserData;
    private String gameName;
    private boolean isRightNavigated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        initializeFirebase();
        initializeWidgets();
        createUserListListener();
        createAdminListener();
        createGameChangeListener();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LobbyActivity.this, MainActivity.class);
                removeUser();
                startActivity(intent);
                finish();
            }
        });

        selectGameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectGameDialog dialog = new SelectGameDialog(LobbyActivity.this);
                dialog.show();

                dialog.setOnConfirmClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isAdmin()) {
                            gameName = dialog.getSelectedRadioButtonText();
                            selectGameBtn.setText(gameName);
                        }
                        dialog.dismiss();
                    }
                });

            }
        });

        startGameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAdmin() && gameName != null) {
                    DatabaseReference gameRef = roomRef.child("gameData");
                    gameRef.setValue(new GameData(gameName));
                }
            }
        });

    }

    private void initializeFirebase() {
        Intent intent = getIntent();
        myRoomData = (RoomData) intent.getSerializableExtra("myRoomData");
        myUserData = (UserData) intent.getSerializableExtra("myUserData");

        roomRef = FirebaseDatabase.getInstance().getReference()
                .child("rooms").child(myRoomData.getRoomID());
    }

    private void initializeWidgets() {
        progressDialog = new ProgressDialog(LobbyActivity.this);
        listView = findViewById(R.id.userListView);
        selectGameBtn = findViewById(R.id.selectGameButton);
        startGameBtn = findViewById(R.id.startGameButton);
        toolbar = findViewById(R.id.GameLobbyTopAppBar);
        adapter = new LobbyUserListAdapter();
        listView.setAdapter(adapter);
        toolbar.setTitle(myRoomData.getRoomID() + " / " + myRoomData.getRoomName());
    }

    private void createUserListListener() {
        progressDialog.show();
        DatabaseReference userListRef = roomRef.child("userList");
        ValueEventListener userListListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    progressDialog.hide();
                    return;
                }

                List<UserData> userList = snapshot.getValue(new GenericTypeIndicator<List<UserData>>() {});
                myRoomData.setUserList(userList);
                adapter.addUserData(userList);
                progressDialog.hide();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.hide();
            }
        };

        userListRef.addValueEventListener(userListListener);
    }

    private void createAdminListener() {
        progressDialog.show();
        DatabaseReference adminRef = roomRef.child("roomAdminID");
        ValueEventListener adminListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    progressDialog.hide();
                    return;
                }

                String adminId = snapshot.getValue(String.class);
                myRoomData.setRoomAdminID(adminId);
                progressDialog.hide();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.hide();
            }
        };

        adminRef.addValueEventListener(adminListener);
    }

    private void createGameChangeListener() {
        progressDialog.show();

        DatabaseReference selectedGameRef = roomRef.child("gameData");
        ValueEventListener gameChangeListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    progressDialog.hide();
                    return;
                }

                GameData gameData = snapshot.getValue(GameData.class);
                String gameName = gameData.getGame();
                if (isUserInRoom()) {
                    startGame(gameName);
                }
                progressDialog.hide();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.hide();
            }
        };

        selectedGameRef.addValueEventListener(gameChangeListener);
    }

    private void removeUser() {
        DatabaseReference userListRef = roomRef.child("userList");
        List<UserData> userDataList = myRoomData.getUserList();
        userDataList.remove(myUserData);
        if (userDataList.isEmpty()) {
            roomRef.removeValue();
        } else {
            String newAdminID = userDataList.get(0).getUID();
            changeRoomAdmin(newAdminID);
            userListRef.setValue(userDataList);
        }
    }

    private void changeRoomAdmin(String userUid) {
        DatabaseReference adminRef = roomRef.child("roomAdminID");
        adminRef.setValue(userUid);
    }

    private void startGame(String str) {
        Intent intent;
        Bundle bundle = new Bundle();
        bundle.putSerializable("myRoomData", myRoomData);
        bundle.putSerializable("myUserData", myUserData);
        isRightNavigated = true;

        if (str.equals("테스트용1")) {
            intent = new Intent(getApplicationContext(), GameFirstActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        } else if (str.equals("숫자야구")) {
            intent = new Intent(getApplicationContext(), GameBaseballActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        } else if (str.equals("테스트용3")) {
            Toast.makeText(getApplicationContext(),"33333", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(),"Null", Toast.LENGTH_SHORT).show();
        }

        finish();
    }

    private boolean isAdmin() {
        return myUserData.getUID().equals(myRoomData.getRoomAdminID());
    }

    private boolean isUserInRoom() {
        List<UserData> userList = myRoomData.getUserList();
        for (UserData user : userList) {
            if (user.getUID().equals(myUserData.getUID())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (!isRightNavigated) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            removeUser();
            finish();
        }
    }
}