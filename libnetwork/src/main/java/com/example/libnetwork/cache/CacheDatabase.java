package com.example.libnetwork.cache;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

import com.example.libcommon.AppGlobals;

@Database(entities = {Cache.class}, version = 1, exportSchema = true)
public abstract class CacheDatabase extends RoomDatabase {
    private static final CacheDatabase database;

    static{
        database = Room.databaseBuilder(AppGlobals.getsApplication(),CacheDatabase.class,"ppjoke_cache")
                .allowMainThreadQueries()
                .build();
    }
    public static CacheDatabase get(){
        return database;
    }

    public abstract CacheDao getCache();



}
