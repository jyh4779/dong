package com.example.mytest;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "TB_DDONG_SCORE")
public class DDongData implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "NAME")
    private String NAME;

    @ColumnInfo(name = "SCORE")
    private int SCORE;

    @ColumnInfo(name = "PLAY_TIME")
    private String PLAY_TIME;

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getNAME()
    {
        return NAME;
    }
    public void setNAME(String NAME){
        this.NAME = NAME;
    }

    public int getSCORE()
    {
        return SCORE;
    }
    public void setSCORE(int SCORE){
        this.SCORE = SCORE;
    }

    public String getPLAY_TIME()
    {
        return PLAY_TIME;
    }
    public void setPLAY_TIME(String PLAY_TIME){
        this.PLAY_TIME = PLAY_TIME;
    }
}
