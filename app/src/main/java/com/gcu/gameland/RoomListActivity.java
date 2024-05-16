package com.gcu.gameland;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
    private RoomListAdapter adapter;
    private ListView listView;
    private MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_list);
        initializeWidgets();
        createRoomListListener();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), LobbyActivity.class);
                Bundle bundle = new Bundle();
                RoomData roomData = (RoomData) adapter.getItem(position);
                updateTheRoomInfo(roomData);
                bundle.putInt("roomID", Integer.parseInt(roomData.getRoomID()));
                bundle.putString("roomName", roomData.getRoomName());
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

    private void initializeWidgets() {
        listView = findViewById(R.id.gameRoomListView);
        toolbar = findViewById(R.id.gameRoomTopAppBar);
        adapter = new RoomListAdapter();
        listView.setAdapter(adapter);
    }

    private void createRoomListListener() {
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()) {
                    return;
                }

                ArrayList<RoomData> roomDataList = new ArrayList<>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    RoomData roomData = data.getValue(RoomData.class);
                    roomDataList.add(roomData);
                }

                adapter.items.clear();
                adapter.addRoomDataList(roomDataList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // ...
            }
        };

        roomsRef.addValueEventListener(listener);
    }

    private void updateTheRoomInfo(RoomData roomData) {
        String UID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        roomData.getUserList().add(UID);

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