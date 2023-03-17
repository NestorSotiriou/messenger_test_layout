package com.example.messengertestlayout.Room;


import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {TableMessageItem.class}, version = 1,exportSchema = false)
public abstract class RoomDB extends RoomDatabase {
    public abstract MyDao myDao();
}
