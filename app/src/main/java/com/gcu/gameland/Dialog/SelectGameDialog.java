package com.gcu.gameland.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.gcu.gameland.R;

import java.util.Objects;

public class SelectGameDialog extends Dialog {
    private RadioGroup radioGroup;
    private RadioButton radioButton[];
    private Button cancelBtn;
    private Button confirmBtn;
    private String selectedRadioButtonText = null;

    public SelectGameDialog(@NonNull Context context) {
        super(context);
        Objects.requireNonNull(getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
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

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton selecteRadioBtn = group.findViewById(checkedId);
                if (selecteRadioBtn != null) {
                    selectedRadioButtonText = selecteRadioBtn.getText().toString();
                }
            }
        });

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

    public String getSelectedRadioButtonText() { return selectedRadioButtonText; }

}
