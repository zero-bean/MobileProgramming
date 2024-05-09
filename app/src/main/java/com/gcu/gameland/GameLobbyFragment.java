package com.gcu.gameland;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
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
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private ValueEventListener userListListener;
    private int roomID;
    private String roomName;
    private ListView listView;
    private GameLobbyUserListAdapter adapter;
    private MaterialToolbar toolbar;
    private Button selectGameBtn;
    private Button startGameBtn;

    public GameLobbyFragment() {
        // Required empty public constructor
    }

    public static GameLobbyFragment newInstance(int roomID, String roomName) {
        GameLobbyFragment fragment = new GameLobbyFragment();
        Bundle args = new Bundle();
        args.putInt("roomID", roomID);
        args.putString("roomName", roomName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            roomID = bundle.getInt("roomID");
            roomName = bundle.getString("roomName");
        }

        adapter = new GameLobbyUserListAdapter();

        userListListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                updateAdapter(adapter, roomID);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                    // ....
            }
        };

        DatabaseReference ref = myRef.child("rooms").child(Integer.toString(roomID)).child("userList");
        ref.addValueEventListener(userListListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeUser(roomID);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_game_lobby, container, false);

        listView = (ListView) rootView.findViewById(R.id.userListView);
        selectGameBtn = rootView.findViewById(R.id.selectGameButton);
        startGameBtn = rootView.findViewById(R.id.startGameButton);
        toolbar = rootView.findViewById(R.id.GameLobbyTopAppBar);
        toolbar.setTitle(roomID + " / " + roomName);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireActivity(), MainActivity.class);
                startActivity(intent);
                requireActivity().finish();
            }
        });

        selectGameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectGameDialog dialog = new SelectGameDialog(requireActivity());
                dialog.show();
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                // 선택된 라디오 버튼의 텍스트를 저장할 변수
                final String[] selectedRadioText = {null};

                dialog.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        // 선택된 라디오 버튼의 인덱스 찾기
                        int radioButtonID = group.getCheckedRadioButtonId();
                        View radioButton = group.findViewById(radioButtonID);

                        // 선택된 라디오 버튼의 텍스트 가져오기
                        selectedRadioText[0] = ((RadioButton) radioButton).getText().toString();
                    }
                });

                dialog.setOnConfirmClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (selectedRadioText[0] != null) {
                            Toast.makeText(getContext(), selectedRadioText[0], Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }
                });

                dialog.setOnCancelClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();

                    }
                });

            }
        });

        startGameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAdminAndStartGame();
            }
        });

        updateAdapter(adapter, roomID);

        listView.setAdapter(adapter);

        return rootView;
    }

    private void updateAdapter(GameLobbyUserListAdapter adapter, int roomID) {
        DatabaseReference userListRef = myRef.child("rooms").child(Integer.toString(roomID)).child("userList");
        userListRef.addListenerForSingleValueEvent(new ValueEventListener() {
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
        adapter.clear();
        for (String uid: UIDs) {
            myRef.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    UserData user = snapshot.getValue(UserData.class);
                    if (user != null && !adapter.isUserExists(user)) {
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

    private void removeUser(int roomID) {
        String userUid = currentUser.getUid();
        DatabaseReference userListRef = myRef.child("rooms").child(Integer.toString(roomID)).child("userList");

        userListRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> list = snapshot.getValue(new GenericTypeIndicator<ArrayList<String>>() {});
                if (list == null || list.size() <= 1) {
                    removeRoomData(roomID);
                    return;
                }

                list.remove(userUid);
                userListRef.setValue(list);
                changeRoomAdminID(roomID);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // onCancelled 처리
            }
        });
    }

    private void removeRoomData(int roomID) {
        DatabaseReference roomRef = myRef.child("rooms").child(Integer.toString(roomID));
        roomRef.removeValue();
    }

    private void changeRoomAdminID(int roomID) {
        DatabaseReference roomRef = myRef.child("rooms").child(Integer.toString(roomID));
        roomRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                RoomData roomInfo = snapshot.getValue(RoomData.class);
                if (roomInfo != null && roomInfo.getUserList() != null) {
                    String newRoomAdminID = roomInfo.getUserList().get(0);

                    roomInfo.setRoomAdminID(newRoomAdminID);

                    roomRef.setValue(roomInfo);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // onCancelled 처리
            }
        });
    }

    private void checkAdminAndStartGame() {
        DatabaseReference roomRef = myRef.child("rooms").child(Integer.toString(roomID));
        roomRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                RoomData roomInfo = snapshot.getValue(RoomData.class);
                if (roomInfo != null && roomInfo.getRoomAdminID().equals(currentUser.getUid())) {
                    // 방 관리자이므로 게임을 시작할 수 있음
                    startGame();
                } else {
                    // 방 관리자가 아니므로 Toast를 통해 알림
                    Toast.makeText(getActivity(), "You are not the admin!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // onCancelled 처리
            }
        });
    }

    private void startGame() {
        Toast.makeText(getActivity(), "Game started!", Toast.LENGTH_SHORT).show();
    }

}