package com.gcu.gameland;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import DTO.RoomData;
import DTO.UserData;

public class GameRoomActivity extends AppCompatActivity {
    private final String TAG = "GameRoomActivity";
    private final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
    private ListView listView;
    private MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game_room);

        listView = findViewById(R.id.gameRoomListView);
        toolbar = findViewById(R.id.gameRoomTopAppBar);

        GameRoomListAdapter adapter = new GameRoomListAdapter();
        updateRoomList(adapter);
        listView.setAdapter(adapter);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.gameRoomLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RoomData roomData = (RoomData) adapter.getItem(position);
                updateRoomInfo(roomData);
                Intent intent = new Intent(getApplicationContext(), GameLobbyActivity.class);
                Bundle bundle = new Bundle();
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

    private void updateRoomList(GameRoomListAdapter adapter) {
        myRef.child("rooms").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<RoomData> roomDataList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    RoomData roomData = snapshot.getValue(RoomData.class);
                    roomDataList.add(roomData);
                }

                adapter.items.clear();
                adapter.addRoomDataList(roomDataList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });
    }

    private void updateRoomInfo(RoomData roomData) {
        DatabaseReference roomRef = myRef.child("rooms").child(roomData.getRoomID());
        roomRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                RoomData roomInfo = mutableData.getValue(RoomData.class);
                if (roomInfo == null) {
                    return Transaction.success(mutableData);
                }

                String currentUserUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                if (!roomInfo.getUserList().contains(currentUserUID)) {
                    roomInfo.getUserList().add(currentUserUID);
                }

                mutableData.setValue(roomInfo);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean committed, @Nullable DataSnapshot currentData) {
                if (databaseError != null) {
                    Log.e(TAG, "트랜잭션 실패", databaseError.toException());
                } else {
                    if (committed) {
                        Log.d(TAG, "룸 정보 업데이트 성공");
                    } else {
                        Log.d(TAG, "룸 정보 업데이트 실패: 트랜잭션이 중단됨");
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}