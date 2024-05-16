package com.gcu.gameland;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class GameFirstActivity extends AppCompatActivity {
    private final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
    private int roomID;
    private String roomName;
    private Button testBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_first);

        // 로비 액티비티로부터 방 정보와 번호를 받습니다. 다시 되돌아가기 위해서 필요한 정보입니다.
        Intent intent = getIntent();
        roomID = intent.getIntExtra("roomID", 0);
        roomName = intent.getStringExtra("roomName");

        testBtn = findViewById(R.id.testButton);
        // 버튼을 누르면 게임 화면을 종료하고 로비 액티비티로 넘어갑니다.
        testBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initializeSelectedGame();
                Intent i = new Intent(getApplicationContext(), LobbyActivity.class);
                // 기존에 있었던 방 이름과 번호를 전달합니다.
                Bundle bundle = new Bundle();
                bundle.putInt("roomID", roomID);
                bundle.putString("roomName", roomName);
                i.putExtras(bundle);
                // 로비 액티비티로 전환 후, 종료
                startActivity(i);
                finish();
            }
        });
    }

    // 파이어베이스에 저장된 게임 종목을 null로 초기화합니다.
    private void initializeSelectedGame() {
        DatabaseReference selectedGameRef = myRef.child("rooms").child(Integer.toString(roomID)).child("selectedGame");
        selectedGameRef.setValue(null);
    }
}