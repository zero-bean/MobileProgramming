package com.gcu.gameland.DTO;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.gcu.gameland.R;

public class MainActivity extends AppCompatActivity {

    private EditText editTextTime;
    private Button startButton;
    private Button stopButton;
    private TextView timerTextView;
    private ImageView gifImageView;
    private Handler handler;
    private Runnable runnable;
    private long startTime = 0L;
    private long timeInMilliseconds = 0L;
    private long updatedTime = 0L;
    private int seconds = 0, minutes = 0, milliseconds = 0;
    private boolean isTimeVisible = true;
    private long setTimeInMillis = 0L;
    private Handler visibilityHandler;
    private Runnable hideTimeRunnable;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextTime = findViewById(R.id.editTextTime);
        startButton = findViewById(R.id.startButton);
        stopButton = findViewById(R.id.stopButton);
        timerTextView = findViewById(R.id.timerTextView);
        gifImageView = findViewById(R.id.gifImageView);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputTime = editTextTime.getText().toString();
                if (!inputTime.isEmpty()) {
                    setTimeInMillis = Long.parseLong(inputTime) * 1000; // 입력 시간을 밀리초로 변환
                    startTime = SystemClock.uptimeMillis();
                    handler.postDelayed(runnable, 0);
                    isTimeVisible = true;
                    timerTextView.setVisibility(View.VISIBLE);
                    visibilityHandler.postDelayed(hideTimeRunnable, 2000); // 2초 후에 시간 숨기기
                    // GIF 표시
                    gifImageView.setVisibility(View.VISIBLE);
                    Glide.with(MainActivity.this)
                            .asGif()
                            //.load(R.raw.sandclock)
                            .into(gifImageView);
                } else {
                    Toast.makeText(MainActivity.this, "설정 시간을 입력하세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopTimer();
                Toast.makeText(MainActivity.this, "타이머가 정지되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        handler = new Handler();
        visibilityHandler = new Handler();
        runnable = new Runnable() {
            public void run() {
                timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
                updatedTime = timeInMilliseconds;
                seconds = (int) (updatedTime / 1000);
                minutes = seconds / 60;
                seconds = seconds % 60;
                milliseconds = (int) (updatedTime % 1000) / 10;

                if (isTimeVisible) {
                    timerTextView.setText("" + minutes + ":"
                            + String.format("%02d", seconds) + ":"
                            + String.format("%02d", milliseconds));
                } else {
                    timerTextView.setText("");
                }

                // 오차 시간이 +4초를 넘으면 타이머를 중지
                if (updatedTime - setTimeInMillis > 4000) {
                    stopTimer();
                    Toast.makeText(MainActivity.this, "오차 시간이 4초를 초과하여 타이머가 정지되었습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    handler.postDelayed(this, 0);
                }
            }
        };

        hideTimeRunnable = new Runnable() {
            @Override
            public void run() {
                isTimeVisible = false; // 시간 숨기기
                timerTextView.setText(""); // 타이머 텍스트뷰의 내용을 지워서 시간을 숨김
            }
        };

        // Edge-to-Edge 디자인을 적용
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void stopTimer() {
        handler.removeCallbacks(runnable);
        visibilityHandler.removeCallbacks(hideTimeRunnable); // 타이머 정지 시 시간 숨기기 작업 취소
        timerTextView.setVisibility(View.VISIBLE);
        updateTimerText();
        // GIF 숨기기
        gifImageView.setVisibility(View.GONE);
    }

    private void updateTimerText() {
        long currentTimeMillis = SystemClock.uptimeMillis();
        long elapsedMillis = currentTimeMillis - startTime;
        int elapsedSeconds = (int) (elapsedMillis / 1000);
        int elapsedMinutes = elapsedSeconds / 60;
        elapsedSeconds %= 60;
        int elapsedMilliseconds = (int) ((elapsedMillis % 1000) / 10);

        String elapsedTime = String.format("%02d:%02d:%02d", elapsedMinutes, elapsedSeconds, elapsedMilliseconds);
        timerTextView.setText("경과 시간: " + elapsedTime);

        // 설정 시간과의 오차 계산
        long difference = elapsedMillis - setTimeInMillis;
        int diffSeconds = (int) (difference / 1000);
        int diffMinutes = diffSeconds / 60;
        diffSeconds %= 60;
        int diffMilliseconds = (int) ((difference % 1000) / 10);

        String differenceTime = String.format("%02d:%02d:%02d", diffMinutes, diffSeconds, diffMilliseconds);
        timerTextView.append("\n오차 시간: " + differenceTime);
    }
}
