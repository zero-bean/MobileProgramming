package com.gcu.gameland;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Random;

public class GameBaseballActivity extends AppCompatActivity {
    int[] comNumbers = new int[4];
    int inputTextCount = 0;
    int hitCount = 1;

    TextView[] inputTextView = new TextView[4];
    Button[] numButton = new Button[10];

    ImageButton backSpaceButton;
    ImageButton hitButton;

    TextView resultTextView;
    ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_baseball_game);

        comNumbers = getComNumbers();

        int[] inputTextViewIds = {
                R.id.input_text_view_0,
                R.id.input_text_view_1,
                R.id.input_text_view_2,
                R.id.input_text_view_3
        };

        for (int i = 0; i < inputTextView.length; i++) {
            inputTextView[i] = findViewById(inputTextViewIds[i]);
        }

        int[] numButtonIds = {
                R.id.num_button_0,
                R.id.num_button_1,
                R.id.num_button_2,
                R.id.num_button_3,
                R.id.num_button_4,
                R.id.num_button_5,
                R.id.num_button_6,
                R.id.num_button_7,
                R.id.num_button_8,
                R.id.num_button_9
        };

        for (int i = 0; i < numButton.length; i++) {
            numButton[i] = findViewById(numButtonIds[i]);
        }

        backSpaceButton = findViewById(R.id.back_space_button);
        hitButton = findViewById(R.id.hit_button);
        resultTextView = findViewById(R.id.result_text_view);
        scrollView = findViewById(R.id.scroll_view);

        for (Button getNumButton : numButton) {
            getNumButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (inputTextCount < 4) {
                        Button button = (Button) v;
                        String getButtonNumber = button.getText().toString();
                        inputTextView[inputTextCount].setText(getButtonNumber);
                        button.setEnabled(false);
                        inputTextCount++;
                    } else {
                        Toast.makeText(getApplicationContext(), "Press Hit Button!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        backSpaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(inputTextCount > 0){
                    int buttonEnableCount = Integer.parseInt(inputTextView[inputTextCount-1].getText().toString());
                    numButton[buttonEnableCount].setEnabled(true);
                    inputTextView[inputTextCount-1].setText("");
                    inputTextCount--;
                } else {
                    Toast.makeText(getApplicationContext(), "Please Enter any number", Toast.LENGTH_SHORT).show();
                }
            }
        });

        hitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(inputTextCount < 4){
                    Toast.makeText(getApplicationContext(),"Please Enter any number",Toast.LENGTH_SHORT).show();
                }else{
                    int[] userNumbers = new int[4];
                    for(int i = 0; i < userNumbers.length; i++){
                        userNumbers[i] = Integer.parseInt(inputTextView[i].getText().toString());
                    }

                    int[] countCheck = new int[3];
                    countCheck = getCountcheck(comNumbers,userNumbers);
                    Log.e("hitButton", "countCheck = S : " + countCheck[0] + "  B : " + countCheck[1]);


                    String resultCount;
                    if(countCheck[0] == 4) {//out
                        resultCount = "1 [" + userNumbers[0] + " " + userNumbers[1] + " " + userNumbers[2] + " " + userNumbers[3] + "] Out! Congratulation!";
                        hitCount = 1;
                    }else{
                        resultCount = "1 [" + userNumbers[0] + " " + userNumbers[1] + " " + userNumbers[2] + " " + userNumbers[3] + "] S : "
                                + countCheck[0] + " B : " + countCheck[1];
                    }
                    if(hitCount == 1){
                        resultTextView.setText(resultCount + "\n");
                    }else{
                        resultTextView.append(resultCount + "\n");
                    }

                    if(countCheck[0] == 4){
                        hitCount= 1;
                    }else{
                        hitCount++;
                    }

                    scrollView.fullScroll(View.FOCUS_DOWN);

                    inputTextCount = 0;
                    for(TextView textView : inputTextView){
                        textView.setText("");
                    }

                    for(Button button : numButton) {
                        button.setEnabled(true);
                    }
                }
            }
        });
    }

    private int[] getCountcheck(int[] comNumbers, int[] userNumbers) {
        int[] setCount = new int[2]; // [0] -> 스트라이크, [1] -> 볼
        for(int i = 0; i < comNumbers.length; i++){
            for(int j = 0; j < userNumbers.length; j++){
                if(comNumbers[i] == userNumbers[j]){
                    if(i == j){
                        setCount[0]++; // 스트라이크
                    } else {
                        setCount[1]++; // 볼
                    }
                }
            }
        }
        return setCount;
    }


    public int[] getComNumbers() {
        int[] setComNumbers = new int[4];

        for (int i = 0; i < setComNumbers.length; i++) {
            setComNumbers[i] = new Random().nextInt(10);
            for (int j = 0; j < i; j++) {
                if (setComNumbers[i] == setComNumbers[j]) {
                    i--;
                    break;
                }
            }
        }
        Log.e("setComNumbers", "setComNumbers = " + setComNumbers[0] + ", " + setComNumbers[1] + ", " + setComNumbers[2] + ", " + setComNumbers[3]);
        return setComNumbers;
    }
}