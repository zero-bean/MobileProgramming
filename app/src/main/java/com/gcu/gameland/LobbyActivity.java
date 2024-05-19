package com.gcu.gameland;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.gcu.gameland.Dialog.ProgressDialog;
import com.gcu.gameland.Dialog.SelectGameDialog;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        initializeFirebase();
        initializeWidgets();
        createUserCountListener();
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
                        gameName = dialog.getSelectedRadioButtonText();
                        selectGameBtn.setText(gameName);
                        dialog.dismiss();
                    }
                });

            }
        });

        startGameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gameName != null) {
                    roomRef.child("selectedGame").setValue(gameName);
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

    private void createUserCountListener() {
        progressDialog.show();
        DatabaseReference userCountRef = roomRef.child("userList");
        ValueEventListener userCountListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    return;
                }

                List<UserData> userList = snapshot.getValue(new GenericTypeIndicator<List<UserData>>() {});
                if (userList != null) {
                    adapter.addUserData(userList);
                }
                progressDialog.hide();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.hide();
            }
        };

        userCountRef.addValueEventListener(userCountListener);
    }

    private void createGameChangeListener() {
        progressDialog.show();

        DatabaseReference selectedGameRef = roomRef.child("selectedGame");
        ValueEventListener gameChangeListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    return;
                }

                String selectedGameName = snapshot.getValue(String.class);
                if (selectedGameName != null) {
                    startGame(selectedGameName);
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

        userListRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<UserData> list = snapshot.getValue(new GenericTypeIndicator<List<UserData>>() {});
                if (list == null || list.size() <= 1) {
                    roomRef.removeValue();
                    return;
                }

                list.remove(myUserData);
                userListRef.setValue(list);
                changeRoomAdminID();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // onCancelled 처리
            }
        });
    }

    private void changeRoomAdminID() {
        roomRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                RoomData roomInfo = snapshot.getValue(RoomData.class);
                if (roomInfo != null && roomInfo.getUserList() != null) {
                    String newRoomAdminID = roomInfo.getUserList().get(0).getUID();
                    roomInfo.setRoomAdminID(newRoomAdminID);
                    roomRef.setValue(roomInfo);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // onCancelled 처리
            }
        });
    }

    private void startGame(String str) {
        Intent intent;
        Bundle bundle = new Bundle();
        bundle.putSerializable("myRoomData", myRoomData);
        bundle.putSerializable("myUserData", myUserData);

        if (str.equals("테스트용 1")) {
            intent = new Intent(getApplicationContext(), GameFirstActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        } else if (str.equals("테스트용 2")) {
            Toast.makeText(getApplicationContext(),"22222", Toast.LENGTH_SHORT).show();
        } else if (str.equals("테스트용 3")) {
            Toast.makeText(getApplicationContext(),"33333", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(),"Null", Toast.LENGTH_SHORT).show();
        }

        finish();
    }
}