package com.gcu.gameland;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.gcu.gameland.DTO.UserData;
import com.gcu.gameland.Dialog.ProgressDialog;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

import com.gcu.gameland.DTO.RoomData;

public class RoomListActivity extends AppCompatActivity {
    private final DatabaseReference roomsRef = FirebaseDatabase.getInstance().getReference().child("rooms");
    private ProgressDialog progressDialog;
    private RoomListAdapter adapter;
    private ListView listView;
    private MaterialToolbar toolbar;
    private UserData myUserData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_list);
        getBundleData();
        initializeWidgets();
        createRoomListListener();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), LobbyActivity.class);
                Bundle bundle = new Bundle();
                RoomData roomData = (RoomData) adapter.getItem(position);
                updateTheRoomInfo(roomData);
                bundle.putSerializable("myRoomData", roomData);
                bundle.putSerializable("myUserData", myUserData);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void getBundleData() {
        Intent intent = getIntent();
        myUserData = (UserData) intent.getSerializableExtra("myUserData");
    }

    private void initializeWidgets() {
        progressDialog = new ProgressDialog(RoomListActivity.this);
        listView = findViewById(R.id.gameRoomListView);
        toolbar = findViewById(R.id.gameRoomTopAppBar);
        adapter = new RoomListAdapter();
        listView.setAdapter(adapter);
    }

    private void createRoomListListener() {
        progressDialog.show();

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()) {
                    progressDialog.hide();
                    return;
                }

                ArrayList<RoomData> roomDataList = new ArrayList<>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    RoomData roomData = data.getValue(RoomData.class);
                    roomDataList.add(roomData);
                }
                adapter.items.clear();
                adapter.addRoomDataList(roomDataList);
                progressDialog.hide();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.hide();
            }
        };

        roomsRef.addValueEventListener(listener);
    }

    private void updateTheRoomInfo(RoomData roomData) {
        roomData.getUserList().add(myUserData);
        roomsRef.child(roomData.getRoomID()).setValue(roomData);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}