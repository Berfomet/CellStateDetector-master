package com.example.marcoycaza.cell_state_detector.Entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

//Entidad Celda registrada en la base de datos

@Entity
public class Celda {

    @NonNull
    @PrimaryKey (autoGenerate = true) //llave de la tabla autogenerada
    private int id;

    @ColumnInfo(name = "cell_name")
    private String cellName;
    @ColumnInfo(name="technology")
    private String technology; //GSM, LTE, UMTS
    @ColumnInfo(name="cell_id")
    private Integer cellId;
    @ColumnInfo(name = "enodb_id")
    private Integer enodBId;
    @ColumnInfo(name = "potencia")
    private Integer dbm; //decibeles

    public Celda(){}

    @NonNull
    public int getId() {
        return id;
    }

    public void setId(@NonNull int id) {
        this.id = id;
    }

    public String getCellName() {
        return cellName;
    }

    public void setCellName(String cellName) {
        this.cellName = cellName;
    }

    public String getTechnology() {
        return technology;
    }

    public void setTechnology(String technology) {
        this.technology = technology;
    }

    public Integer getCellId() {
        return cellId;
    }

    public void setCellId(Integer cellId) {
        this.cellId = cellId;
    }

    public Integer getEnodBId() {
        return enodBId;
    }

    public void setEnodBId(Integer enodBId) {
        this.enodBId = enodBId;
    }

    public Integer getDbm() {
        return dbm;
    }

    public void setDbm(Integer dbm) {
        this.dbm = dbm;
    }
}
