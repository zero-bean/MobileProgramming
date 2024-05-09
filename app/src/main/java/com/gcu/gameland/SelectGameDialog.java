package com.gcu.gameland;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;

public class SelectGameDialog extends Dialog {

    private RadioGroup radioGroup;
    private RadioButton radioButton[];
    private Button cancelBtn;
    private Button confirmBtn;

    public SelectGameDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_select_game);
        initViews();
    }

    private void initViews() {
        radioGroup = findViewById(R.id.selectGameRadioGroup);
        cancelBtn = findViewById(R.id.selectGameCancleButton);
        confirmBtn = findViewById(R.id.selectGameConfirmButton);

        radioButton = new RadioButton[3];
        radioButton[0] = findViewById(R.id.selectGameFirstRadioButton);
        radioButton[1] = findViewById(R.id.selectGameSecondRadioButton);
        radioButton[2] = findViewById(R.id.selectGameThirdRadioButton);
    }

    public void setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener listener) {
        radioGroup.setOnCheckedChangeListener(listener);
    }

    public void setOnCancelClickListener(View.OnClickListener listener) {
        cancelBtn.setOnClickListener(listener);
    }

    public void setOnConfirmClickListener(View.OnClickListener listener) {
        confirmBtn.setOnClickListener(listener);
    }

}
