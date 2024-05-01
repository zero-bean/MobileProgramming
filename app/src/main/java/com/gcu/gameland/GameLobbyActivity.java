package com.gcu.gameland;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class GameLobbyActivity extends AppCompatActivity {

    private GameLobbyFragment gameLobbyFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game_lobby);

        gameLobbyFragment = new GameLobbyFragment();

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