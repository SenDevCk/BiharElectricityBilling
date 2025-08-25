package com.nic.app.biharelectricitybilling.db;

import androidx.annotation.WorkerThread;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.nic.app.biharelectricitybilling.entity.Survey_Data;
import com.nic.app.biharelectricitybilling.entity.Survey_Data_Details;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface SurveyDao  {
    @Query("SELECT * FROM Survey_Data")
    List<Survey_Data> getAll();

    @Query("SELECT * FROM Survey_Data LIMIT :limit OFFSET :offset")
    List<Survey_Data> loadAllDataByPage(int limit,int offset);

    @Query("SELECT * FROM Survey_Data Where ACT_NO= :act_no")
    List<Survey_Data> getByActno(String act_no);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Survey_Data task);

/*    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Survey_Data_Details task);*/

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveAll(List<Survey_Data> items);

    @Delete
    void delete(Survey_Data task);

    @Query("Delete from Survey_Data where ACT_NO= :act_no")
       void deleteByact_no(String act_no);

    @Query("select count(ACT_NO) from Survey_Data")
     int getcount();

    @Update
    void update(Survey_Data task);
}
