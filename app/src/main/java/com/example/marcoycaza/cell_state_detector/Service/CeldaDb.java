package com.example.marcoycaza.cell_state_detector.Service;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.database.Cursor;

import com.example.marcoycaza.cell_state_detector.Entity.Celda;
import com.example.marcoycaza.cell_state_detector.Repository.CeldaRepository;

//Clase que hace referencia a la base de datos de persistencia que esta usando la app

@Database(entities = {Celda.class}, version = 1, exportSchema = false)
public abstract class CeldaDb extends RoomDatabase {

    //Se tiene que crear un repositorio por cada entidad creada en la base de datos y
    //se debe anexar a esta clase para poder manipular dichos repositorios desde las otras
    //clases que manipulen esta base de datos.
    public abstract CeldaRepository celdaRepository();
    private static final Object sLock = new Object();
    private static CeldaDb INSTANCE;

    public static CeldaDb getInstance(final Context context){

        synchronized (sLock){
            if(INSTANCE == null){
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        CeldaDb.class,
                        "packagename")
                        .build();
            }
            return INSTANCE;
        }

    }

    public static void destroyInstance(){
        if(INSTANCE.isOpen()) INSTANCE.close();
        INSTANCE = null;
    }

}
