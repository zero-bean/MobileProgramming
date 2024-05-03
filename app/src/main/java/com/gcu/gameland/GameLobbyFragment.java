package com.gcu.gameland;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import DTO.RoomData;
import DTO.UserData;

public class GameLobbyFragment extends Fragment {
    private final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
    private int roomID;

    public GameLobbyFragment() {
        // Required empty public constructor
    }

    public static GameLobbyFragment newInstance() {
        GameLobbyFragment fragment = new GameLobbyFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getActivity().getIntent().getExtras();
        if (bundle != null) {
            roomID = bundle.getInt("RoomID");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_game_lobby, container, false);

        ListView listView = (ListView) rootView.findViewById(R.id.userListView);
        GameLobbyUserListAdapter adapter = new GameLobbyUserListAdapter();

        updateAdapter(adapter, roomID);

        listView.setAdapter(adapter);

        return rootView;
    }

    private void updateAdapter(GameLobbyUserListAdapter adapter, int roomID) {
        DatabaseReference userListRef = myRef.child("rooms").child(Integer.toString(roomID)).child("userList");
        userListRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // 일반적인 형식인 ArrayList<String>을 역직렬화하기 위해 GenericTypeIndicator 사용
                GenericTypeIndicator<ArrayList<String>> typeIndicator = new GenericTypeIndicator<ArrayList<String>>() {};
                ArrayList<String> UIDs = snapshot.getValue(typeIndicator);
                if (UIDs != null) {
                    getUserDataList(UIDs, adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // onCancelled 처리
            }
        });
    }

    private void getUserDataList(ArrayList<String> UIDs, GameLobbyUserListAdapter adapter) {
        for (String uid: UIDs) {
            myRef.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    UserData user = snapshot.getValue(UserData.class);
                    if (user != null) {
                        adapter.addUserData(user);
                        adapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d("getUserDataList", "loadPost:onCancelled", error.toException());
                }
            });
        }
    }

}