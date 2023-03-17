package com.example.messengertestlayout.Room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface MyDao {
    @Query("SELECT * FROM messagesItem")
    List<TableMessageItem> getAll();


    @Insert
    void insertAll(TableMessageItem...tableMessageItems);

    @Query("UPDATE messagesItem SET message =:message, isSender =:isSender, timeStamp=:timestamp ")
    void insertNew(String message, Boolean isSender, Long timestamp);


    @Query("DELETE FROM messagesItem")
    void deleteAll();

    @Query("SELECT COALESCE(max(id),0)+1 FROM messagesItem")
    Long nextid();

    @Query("DELETE FROM messagesItem WHERE id = :id")
    void delete(Integer id);
}
