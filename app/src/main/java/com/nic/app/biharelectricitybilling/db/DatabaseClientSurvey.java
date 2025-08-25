package com.nic.app.biharelectricitybilling.db;

import android.content.Context;

import androidx.room.Room;

public class DatabaseClientSurvey {

    private Context mCtx;
    private static DatabaseClientSurvey mInstance;

    //our app database object
    private AppDatabase appDatabase;

    private DatabaseClientSurvey(Context mCtx) {
        this.mCtx = mCtx;

        //creating the app database with Room database builder
        //MyToDos is the name of the database
        appDatabase = Room.databaseBuilder(mCtx, AppDatabase.class, "SurveyData").build();
    }

    public static synchronized DatabaseClientSurvey getInstance(Context mCtx) {
        if (mInstance == null) {
            mInstance = new DatabaseClientSurvey(mCtx);
        }
        return mInstance;
    }

    public AppDatabase getAppDatabase() {
        return appDatabase;
    }
}