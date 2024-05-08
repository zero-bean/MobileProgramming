package com.gcu.gameland;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;

public class FindRoomDialog extends Dialog {

    private Button cancelBtn;
    private Button confirmBtn;
    private EditText editText;

    public FindRoomDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_find_room);
        initViews();
    }

    private void initViews() {
        cancelBtn = findViewById(R.id.findRoomCancleButton);
        confirmBtn = findViewById(R.id.findRoomConfirmButton);
        editText = findViewById(R.id.findRoomEditText);
    }

    public void setOnCancelClickListener(View.OnClickListener listener) {
        cancelBtn.setOnClickListener(listener);
    }

    public void setOnConfirmClickListener(View.OnClickListener listener) {
        confirmBtn.setOnClickListener(listener);
    }

    public String getEnteredText() {
        return editText.getText().toString();
    }
}
