package com.gcu.gameland;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gcu.gameland.DTO.GameData;
import com.gcu.gameland.DTO.Score;

import java.util.ArrayList;
import java.util.List;

public class GameResultAdapter extends BaseAdapter {
    List<Score> items = new ArrayList<Score>();
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
        Score score = items.get(position);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_game_result, parent, false);
        }

        TextView nameText = convertView.findViewById(R.id.gameResultNameTextView);
        TextView scoreText = convertView.findViewById(R.id.gameResultScoreTextView);
        TextView rankText = convertView.findViewById(R.id.gameResultRankTextView);

        nameText.setText(score.getName());
        scoreText.setText(Integer.toString(score.getScore()));
        rankText.setText(Integer.toString(position + 1));
        nameText.setFocusable(false);
        scoreText.setFocusable(false);
        rankText.setFocusable(false);

        return convertView;
    }

    public void addScoreData(List<Score> list) {
        clear();
        items.addAll(list);
        notifyDataSetChanged();
    }

    public void clear() {items.clear();}
}
