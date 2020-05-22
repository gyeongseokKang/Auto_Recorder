package cafe.adriel.androidaudioconverter.sample;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface ClassDao {
    @Query("SELECT * FROM class")
    List<Class> getAllClasses();

    @Delete
    public void deleteUser(Class classes);

    @Query("DELETE FROM class")
    void deleteAll();




    @Insert
    void insertAll(Class... classes);
}
