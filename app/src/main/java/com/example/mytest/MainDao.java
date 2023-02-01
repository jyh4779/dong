package com.example.mytest;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MainDao {
    @Insert(onConflict = REPLACE)
    void insert(DDongData mainData);

    @Delete
    void delete(DDongData mainData);

    @Delete
    void reset(List<DDongData> mainData);

    @Query("UPDATE TB_DDONG_SCORE SET SCORE = :sText WHERE NAME = :sID")
    void update(int sID, String sText);

    @Query("SELECT * FROM TB_DDONG_SCORE")
    List<DDongData> getAll();

    @Query("SELECT * FROM TB_DDONG_SCORE ORDER BY SCORE DESC")
    List<DDongData> getLank();
}
