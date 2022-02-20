package com.oceanmtech.crmwhatsappdataupdate.Database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {DataTable.class}, version = 1, exportSchema = false)
public abstract class MyDatabase extends RoomDatabase {

    public abstract DAO dao();
}
