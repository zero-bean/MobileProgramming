package com.gcu.gameland;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class GameLobbyActivity extends AppCompatActivity {
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final DatabaseReference myRef = database.getReference();
    private GameLobbyFragment gameLobbyFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game_lobby);

        Bundle bundle = getIntent().getExtras();
        int roomID = bundle.getInt("roomID");
        String roomName = bundle.getString("roomName");
        gameLobbyFragment = GameLobbyFragment.newInstance(roomID, roomName);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.gameLobbyFrameLayout, gameLobbyFragment)
                .commit();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.gameLobbyFrameLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}