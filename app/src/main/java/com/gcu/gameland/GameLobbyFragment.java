package com.gcu.gameland;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import DTO.RoomData;
import DTO.UserData;

public class GameLobbyFragment extends Fragment {
    private RoomData roomInfo;
    List<UserData> userList = null;

    public GameLobbyFragment() {
        // Required empty public constructor
    }

    public static GameLobbyFragment newInstance(String param1, String param2) {
        GameLobbyFragment fragment = new GameLobbyFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_game_lobby, container, false);

        ListView listView = (ListView) rootView.findViewById(R.id.userListView);
        GameRoomUserListAdapter adapter = new GameRoomUserListAdapter();

        adapter.addUserData(new UserData("EljwDalH3hhNOxKjuBf5x3eoqlW2",
                "박영빈",
                "https://lh3.googleusercontent.com/a/ACg8ocKhAPjiCZcHwkXKBGUAebFuXGVcuftMVKoJmrY78ZH-qZsKCnrW=s96-c"));
        adapter.addUserData(new UserData("PhKNh9QG0pdwAsc32fKAiJqFIRW2",
                "박영빈",
                "https://lh3.googleusercontent.com/a/ACg8ocLs36NrRrYnTBrdUJIuP2VVJ7SD51D8NSXoxHRo6E8D-pGAAw=s96-c"));

        listView.setAdapter(adapter);

        return rootView;
    }
}