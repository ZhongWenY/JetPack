package com.example.libnetwork.cache;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "cache")
//@Entity(tableName = "cache",foreignKeys = {@ForeignKey(entity = User.class,parentColumns = "id"
//        ,childColumns = "key",onDelete =  ForeignKey.RESTRICT, onUpdate = ForeignKey.SET_DEFAULT)},indices = {@Index(value ={"key","id"})})
public class Cache implements Serializable {
    @NonNull
    @PrimaryKey(autoGenerate = false)
    public String key;

    public byte[] data;

//    @Embedded
//    public  User user;
}
