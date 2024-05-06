package com.gcu.gameland;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

import DTO.RoomData;

public class GameRoomListAdapter extends BaseAdapter {
    ArrayList<RoomData> items = new ArrayList<RoomData>();
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
        RoomData roomData = items.get(position);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.game_room_list_item, parent, false);
        }

        TextView gameRoomInfo = convertView.findViewById(R.id.roomInfoTextView);
        TextView gameRoomTitle = convertView.findViewById(R.id.roomTitleTextView);

        gameRoomInfo.setText("[방 번호:" + roomData.getRoomID() + "] / [인원:" + roomData.getUserList().size() + "]");
        gameRoomTitle.setText(roomData.getRoomName());

        return convertView;
    }

    public void addRoomData(RoomData roomData) { items.add(roomData); }
    public void addRoomDataList(ArrayList<RoomData> roomDataList) {
        items.addAll(roomDataList);
        notifyDataSetChanged();
    }
}
