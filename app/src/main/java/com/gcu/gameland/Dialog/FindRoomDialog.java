package com.gcu.gameland.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.gcu.gameland.R;

import java.util.Objects;

public class FindRoomDialog extends Dialog {

    private Button cancelBtn;
    private Button confirmBtn;
    private EditText editText;

    public FindRoomDialog(@NonNull Context context) {
        super(context);
        Objects.requireNonNull(getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
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

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public void setOnConfirmClickListener(View.OnClickListener listener) {
        confirmBtn.setOnClickListener(listener);
    }

    public String getEnteredText() {
        return editText.getText().toString();
    }
}
