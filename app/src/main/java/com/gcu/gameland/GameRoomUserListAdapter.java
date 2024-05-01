package com.gcu.gameland;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import DTO.UserData;

public class GameRoomUserListAdapter extends BaseAdapter {
    ArrayList<UserData> items = new ArrayList<UserData>();
    Context context;

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        context = parent.getContext();
        UserData userData = items.get(position);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.game_lobby_user_list_item, parent, false);
        }

        ImageView userImageView = convertView.findViewById(R.id.gameLobbyUserProfileImageView);
        TextView userNameTextView = convertView.findViewById(R.id.gameLobbyUserNameTextView);

        Glide.with(context).load(userData.getImageURL()).into(userImageView);
        userNameTextView.setText(userData.getNickName());

        return convertView;
    }

    public void addUserData(UserData userData) { items.add(userData); }

}
