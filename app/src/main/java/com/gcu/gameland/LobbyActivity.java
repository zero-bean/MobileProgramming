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

import DTO.RoomData;
import DTO.UserData;

public class LobbyActivity extends AppCompatActivity {
    private final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private ValueEventListener userListListener;
    private int roomID;
    private String roomName;
    private ListView listView;
    private LobbyUserListAdapter adapter;
    private MaterialToolbar toolbar;
    private Button selectGameBtn;
    private Button startGameBtn;

    private String selectedGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        selectedGame = null;

        Intent intent = getIntent();
        roomID = intent.getIntExtra("roomID", 0);
        roomName = intent.getStringExtra("roomName");

        adapter = new LobbyUserListAdapter();

        userListListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists())
                    return;

                updateAdapter(adapter, roomID);

                RoomData roomInfo = snapshot.getValue(RoomData.class);
                if(roomInfo.getSelectedGame() != null) {
                    startGame(roomInfo.getSelectedGame());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //
            }
        };

        DatabaseReference ref = myRef.child("rooms").child(Integer.toString(roomID));
        ref.addValueEventListener(userListListener);

        listView = findViewById(R.id.userListView);
        selectGameBtn = findViewById(R.id.selectGameButton);
        startGameBtn = findViewById(R.id.startGameButton);
        toolbar = findViewById(R.id.GameLobbyTopAppBar);
        toolbar.setTitle(roomID + " / " + roomName);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LobbyActivity.this, MainActivity.class);
                startActivity(intent);
                removeUser(roomID);
                finish();
            }
        });

        selectGameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectGameDialog dialog = new SelectGameDialog(LobbyActivity.this);
                dialog.show();
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                dialog.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        RadioButton radioButton = dialog.findViewById(checkedId);
                        if (radioButton != null) {
                            selectedGame = radioButton.getText().toString();
                        }
                    }
                });

                dialog.setOnConfirmClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (selectedGame != null) {
                            selectGameBtn.setText(selectedGame);
                        }
                        dialog.dismiss();
                    }
                });

                dialog.setOnCancelClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectedGame = null;
                        dialog.dismiss();
                    }
                });

            }
        });

        startGameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference roomRef = myRef.child("rooms").child(Integer.toString(roomID));
                roomRef.child("selectedGame").setValue(selectedGame);
            }
        });

        updateAdapter(adapter, roomID);

        listView.setAdapter(adapter);
    }

    private void updateAdapter(LobbyUserListAdapter adapter, int roomID) {
        DatabaseReference userListRef = myRef.child("rooms").child(Integer.toString(roomID)).child("userList");
        userListRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // 일반적인 형식인 ArrayList<String>을 역직렬화하기 위해 GenericTypeIndicator 사용
                GenericTypeIndicator<ArrayList<String>> typeIndicator = new GenericTypeIndicator<ArrayList<String>>() {};
                ArrayList<String> UIDs = snapshot.getValue(typeIndicator);
                if (UIDs != null) {
                    getUserDataList(UIDs, adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // onCancelled 처리
            }
        });
    }

    private void getUserDataList(ArrayList<String> UIDs, LobbyUserListAdapter adapter) {
        adapter.clear();
        for (String uid: UIDs) {
            myRef.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    UserData user = snapshot.getValue(UserData.class);
                    if (user != null && !adapter.isUserExists(user)) {
                        adapter.addUserData(user);
                        adapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d("getUserDataList", "loadPost:onCancelled", error.toException());
                }
            });
        }
    }

    private void removeUser(int roomID) {
        String userUid = currentUser.getUid();
        DatabaseReference userListRef = myRef.child("rooms").child(Integer.toString(roomID)).child("userList");

        userListRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> list = snapshot.getValue(new GenericTypeIndicator<ArrayList<String>>() {});
                if (list == null || list.size() <= 1) {
                    removeRoomData(roomID);
                    return;
                }

                list.remove(userUid);
                userListRef.setValue(list);
                changeRoomAdminID(roomID);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // onCancelled 처리
            }
        });
    }

    private void removeRoomData(int roomID) {
        DatabaseReference roomRef = myRef.child("rooms").child(Integer.toString(roomID));
        roomRef.setValue(null);
    }

    private void changeRoomAdminID(int roomID) {
        DatabaseReference roomRef = myRef.child("rooms").child(Integer.toString(roomID));
        roomRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                RoomData roomInfo = snapshot.getValue(RoomData.class);
                if (roomInfo != null && roomInfo.getUserList() != null) {
                    String newRoomAdminID = roomInfo.getUserList().get(0);

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
        bundle.putInt("roomID", roomID);
        bundle.putString("roomName", roomName);

        if (str.equals("테스트용 1")) {
            intent = new Intent(getApplicationContext(), GameFirstActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        } else if (str.equals("테스트용 2")) {
            Toast.makeText(getApplicationContext(),"22222", Toast.LENGTH_SHORT).show();
        } else if (str.equals("테스트용 3")) {
            Toast.makeText(getApplicationContext(),"33333", Toast.LENGTH_SHORT).show();
        }

        finish();
    }
}