package com.gcu.gameland.DTO;

import java.io.Serializable;

public class Score implements Serializable {
    private String name;
    private float score;

    public Score() {

    }

    public Score(String name, float score) {
        this.name = name;
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }
}
