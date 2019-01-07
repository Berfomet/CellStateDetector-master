package com.example.marcoycaza.cell_state_detector.Repository;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.database.Cursor;

import com.example.marcoycaza.cell_state_detector.Entity.Celda;

import java.util.List;

//Interfaz que sirve de abstracción para utilizar los recursos de ROOM para obtener la data de la db

@Dao
public interface CeldaRepository {

    //Inserts
    @Insert
    void insertSingleCell(Celda celda);//Singular
    @Insert
    void insertCellArray(List<Celda> celdas);//Plural

    //Consultas
    @Query("SELECT * FROM Celda WHERE id = :id")
    Celda fetchOneCeldabyId(int id);
    @Query("SELECT * FROM Celda WHERE cell_id = :cellId")
    Celda fetchOneCeldabyCellId(int cellId);
    @Query("SELECT * FROM Celda WHERE enodb_id = :enId")
    Celda fetchOneCeldabyEnodBId(int enId);
    @Query("SELECT * FROM Celda")
    List<Celda> getAllCelda();



    @Update
    void updateCelda(Celda celda);
    @Delete
    void deleteCelda(Celda celda);

}
