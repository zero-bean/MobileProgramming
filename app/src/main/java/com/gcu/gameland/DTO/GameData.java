package com.gcu.gameland.DTO;

import java.util.ArrayList;
import java.util.List;

public class GameData {
    private String game;
    private List<Score> scoreList;

    public GameData() {
        scoreList = new ArrayList<Score>();
    }

    public GameData(String game) {
        this.game = game;
        scoreList = new ArrayList<Score>();
    }

    public String getGame() { return this.game; }
    public List<Score> getScoreList() {return  this.scoreList; }

    public void setGame(String game) { this.game = game; }
    public void setScoreList(List<Score> scoreList) { this.scoreList = scoreList; }

}
