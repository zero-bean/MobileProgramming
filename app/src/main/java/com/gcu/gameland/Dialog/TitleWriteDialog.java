package com.gcu.gameland.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.gcu.gameland.R;

public class TitleWriteDialog extends Dialog {

    private Button cancelBtn;
    private Button confirmBtn;
    private EditText editText;

    public TitleWriteDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_tilte_write);
        initViews();
    }

    private void initViews() {
        cancelBtn = findViewById(R.id.TWDCancleButton);
        confirmBtn = findViewById(R.id.TWDConfirmButton);
        editText = findViewById(R.id.TWDEditText);

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

