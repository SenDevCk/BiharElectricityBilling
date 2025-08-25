package com.nic.app.biharelectricitybilling.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.nic.app.biharelectricitybilling.entity.Payment_Data_Details;


import java.util.List;

@Dao
public interface PaymentDao {
    @Query("SELECT * FROM Payment_Data")
    List<Payment_Data_Details> getAll();

    @Query("SELECT * FROM Payment_Data LIMIT :limit OFFSET :offset")
    List<Payment_Data_Details> loadAllDataByPage(int limit,int offset);

    @Query("SELECT * FROM Payment_Data Where ACT_NO= :act_no")
    List<Payment_Data_Details> getByActno(String act_no);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insert(Payment_Data_Details task);

/*    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Survey_Data_Details task);*/

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveAll(List<Payment_Data_Details> items);

    @Delete
    void delete(Payment_Data_Details task);

    @Query("Delete from Payment_Data where ACT_NO= :act_no")
       void deleteByact_no(String act_no);

    @Query("select count(ACT_NO) from Payment_Data")
     int getcount();

    @Update
    void update(Payment_Data_Details task);
}
