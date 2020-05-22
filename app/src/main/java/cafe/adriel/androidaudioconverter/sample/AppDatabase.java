package cafe.adriel.androidaudioconverter.sample;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {Class.class}, version=1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ClassDao classDao();
}
