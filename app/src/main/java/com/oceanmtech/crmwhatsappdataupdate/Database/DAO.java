package com.oceanmtech.crmwhatsappdataupdate.Database;

import android.database.Cursor;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DAO {

    @Insert
    void insert_data(DataTable data);

    @Query("Select * from DataTable")
    Cursor getCRMModel();

    @Query("Select * from DataTable where mobile = :mobile")
    List<DataTable> checkNumber(String mobile);
}
