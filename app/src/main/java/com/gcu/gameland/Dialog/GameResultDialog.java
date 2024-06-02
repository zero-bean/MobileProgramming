package com.gcu.gameland.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;

import com.gcu.gameland.DTO.GameData;
import com.gcu.gameland.GameResultAdapter;
import com.gcu.gameland.R;

import java.util.Objects;

public class GameResultDialog extends Dialog {
    private GameResultAdapter adapter;
    private ListView listView;
    private Button confirmBtn;
    private GameData myGameData;

    public GameResultDialog(@NonNull Context context, GameData gameData) {
        super(context);
        Objects.requireNonNull(getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        this.myGameData = gameData;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_game_result);
        initViews();
    }

    private void initViews() {
        confirmBtn = findViewById(R.id.gameResultButton);
        listView = findViewById(R.id.gameResultListView);
        adapter = new GameResultAdapter();
        adapter.addScoreData(myGameData.getScoreList());
        listView.setAdapter(adapter);
    }

    public void setOnConfirmClickListener(View.OnClickListener listener) {
        confirmBtn.setOnClickListener(listener);
    }
}
