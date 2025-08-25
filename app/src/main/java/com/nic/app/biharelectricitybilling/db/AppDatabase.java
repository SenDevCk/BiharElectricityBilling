package com.nic.app.biharelectricitybilling.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.nic.app.biharelectricitybilling.entity.Payment_Data_Details;
import com.nic.app.biharelectricitybilling.entity.Survey_Data;
import com.nic.app.biharelectricitybilling.entity.Survey_Data_Details;

@Database(entities = {Survey_Data.class, Survey_Data_Details.class, Payment_Data_Details.class}, version = 2,exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract SurveyDao SurveyDao();
    public abstract SurveyDataDao SurveyDataDao();
    public abstract PaymentDao PaymentDao();
}
