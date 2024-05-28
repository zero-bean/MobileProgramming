package com.gcu.gameland.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.gcu.gameland.R;

import java.util.Objects;

public class ProfileChangeDialog extends Dialog {
    private Button cancelBtn;
    private Button confirmBtn;
    private Button uploadBtn;
    private EditText editText;
    private ImageView imageView;

    public ProfileChangeDialog(@NonNull Context context) {
        super(context);
        Objects.requireNonNull(getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_profile_change);
        initViews();
    }

    private void initViews() {
        uploadBtn = findViewById(R.id.profileChangeUploadButton);
        cancelBtn = findViewById(R.id.profileChangeCancleButton);
        confirmBtn = findViewById(R.id.profileChangeConfirmButton);
        editText = findViewById(R.id.profileChangeNameEditText);
        imageView = findViewById(R.id.profileChangeImageView);

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

    public void setOnUploadClickListener(View.OnClickListener listener) {
        uploadBtn.setOnClickListener(listener);
    }

    public String getNameText() {
        return editText.getText().toString();
    }
}
