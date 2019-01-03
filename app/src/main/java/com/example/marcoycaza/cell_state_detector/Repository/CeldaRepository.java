package com.example.marcoycaza.cell_state_detector.Repository;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.marcoycaza.cell_state_detector.Entity.Celda;

import java.util.List;

@Dao
public interface CeldaRepository {

    @Insert
    void insertSingleCell(Celda celda);
    @Insert
    void insertCellArray(List<Celda> celdas);
    @Query("SELECT * FROM Celda WHERE id = :id")
    Celda fetchOneCeldabyId(int id);
    @Update
    void updateCelda(Celda celda);
    @Delete
    void deleteCelda(Celda celda);

}
