package com.example.mycode_lm_ng.repository;

import android.content.Context;
import androidx.room.Room;

public class OrderDBClient {
    private Context mCtx;
    private static OrderDBClient mInstance;

    //our app database object
    private Repository.AppDatabase appDatabase;

    private OrderDBClient(Context mCtx) {
        this.mCtx = mCtx;

        //creating the app database with Room database builder
        //MyToDos is the name of the database
        appDatabase = Room.databaseBuilder(mCtx, Repository.AppDatabase.class, "MyOrders").build();
    }

    public static synchronized OrderDBClient getInstance(Context mCtx) {
        if (mInstance == null) {
            mInstance = new OrderDBClient(mCtx);
        }
        return mInstance;
    }

    public Repository.AppDatabase getAppDatabase() {
        return appDatabase;
    }
}
