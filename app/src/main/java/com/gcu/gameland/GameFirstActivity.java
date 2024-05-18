package com.gcu.gameland;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.gcu.gameland.DTO.RoomData;
import com.gcu.gameland.DTO.UserData;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class GameFirstActivity extends AppCompatActivity {
    private Button testBtn;
    private RoomData myRoomData;
    private UserData myUserData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_first);
        getBundle();
        testBtn = findViewById(R.id.testButton);

        // 버튼을 누르면 게임 화면을 종료하고 로비 액티비티로 넘어갑니다.
        testBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initializeSelectedGame();
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

    // 로비 액티비티로부터 방 정보와 자신의 정보를 받습니다.
    private void getBundle() {
        Intent intent = getIntent();
        myRoomData = (RoomData) intent.getSerializableExtra("myRoomData");
        myUserData = (UserData) intent.getSerializableExtra("myUserData");
    }

    // 파이어베이스에 저장된 게임 종목을 null로 초기화합니다.
    private void initializeSelectedGame() {
        DatabaseReference selectedGameRef = FirebaseDatabase.getInstance().getReference()
                .child("rooms").child(myRoomData.getRoomID()).child("selectedGame");
        selectedGameRef.setValue(null);
    }
}