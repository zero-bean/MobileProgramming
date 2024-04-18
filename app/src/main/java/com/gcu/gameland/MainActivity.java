package com.gcu.gameland;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;

import DTO.UserData;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final DatabaseReference myRef = database.getReference();
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private UserData myUserData;

    private Button sendBtn;
    private Button getBtn;
    private Button logoutBtn;
    private EditText testText;
    private TextView resultText;
    private CircularImageView profileImageView;
    private TextView profileNameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        sendBtn = findViewById(R.id.sendTextButton);
        getBtn = findViewById(R.id.getDataButton);
        logoutBtn = findViewById(R.id.logoutButton);
        testText = findViewById(R.id.testText1);
        resultText = findViewById(R.id.testResultText);
        profileImageView = findViewById(R.id.profileCircularImageView);
        profileNameTextView = findViewById(R.id.profileNameTextView);

        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = testText.getText().toString();
                testText.setText("");
                Log.d("testText", "content: " + msg);
                myRef.push().setValue(msg);
            }
        });

        getBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();

                // 로그인 화면으로 전환
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();
        String userUID = currentUser.getUid();
        DatabaseReference userRef = myRef.child("users").child(userUID);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.d(TAG, "FIREBASE 유저 데이터 갱신 시작");
                    UserData userData = dataSnapshot.getValue(UserData.class);
                    updateUserProfile(userData);
                } else {
                    Log.d(TAG, "FIREBASE 유저 데이터 초기화 시작");
                    initializeUserData();
                    updateUserProfile(myUserData);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "FIREBASE 유저 데이터 갱신 실패: ", databaseError.toException());
            }
        });
    }

    // [END on_start_check_user]

    private void initializeUserData() {
        String userUID = currentUser.getUid();
        String displayName = currentUser.getDisplayName();
        String photoUrl = currentUser.getPhotoUrl() != null ? currentUser.getPhotoUrl().toString() : null;
        myUserData = new UserData(userUID, displayName, photoUrl);

        DatabaseReference userRef = myRef.child("users").child(userUID);
        userRef.setValue(myUserData)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "유저 정보 초기화를 성공하였습니다!");
                        } else {
                            Log.w(TAG, "유저 정보 초기화를 실패하였습니다: ", task.getException());
                        }
                    }
                });
    }

    private void updateUserProfile(UserData userData) {
        myUserData = userData;

        Log.d(TAG, "유저 이름 갱신 성공, name:" + myUserData.getNickName());
        profileNameTextView.setText(myUserData.getNickName());

        String photoUrl = myUserData.getImageURL();
        if (photoUrl != null) {
            Log.d(TAG, "유저 프로필 이미지 갱신 성공, 이미지 URL:" + photoUrl);
            Glide.with(this).load(photoUrl).into(profileImageView);
        } else {
            Log.d(TAG, "유저 프로필 이미지 갱신 실패, 이미지 URL: NULL");
        }
    }
}