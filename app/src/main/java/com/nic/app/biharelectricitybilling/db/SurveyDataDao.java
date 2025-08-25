package com.nic.app.biharelectricitybilling.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.nic.app.biharelectricitybilling.entity.Survey_Data;
import com.nic.app.biharelectricitybilling.entity.Survey_Data_Details;

import java.util.List;

@Dao
public interface SurveyDataDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insert(Survey_Data_Details task);

    @Query("select count(ACT_NO) from Survey_Data_Details")
    int getcount();


    @Query("SELECT * FROM Survey_Data_Details")
    List<Survey_Data_Details> getAll();

    @Query("Delete from Survey_Data_Details where ACT_NO= :act_no")
    void deletesurveyByact_no(String act_no);
}
