package com.gcu.gameland;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {
    private TextView signInWithGoogleTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        signInWithGoogleTextView = findViewById(R.id.signInWithGoogle);

        startBlinkAnimation();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        signInWithGoogleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    // "로그인이 필요한 서비스입니다." 문구의 점멸 애니메이션 효과
    public void startBlinkAnimation() {
        Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink_animation);
        TextView needLoginTextView = findViewById(R.id.needLoginText);
        needLoginTextView.startAnimation(anim);
    }
}