package madhuri.applications.ticketbooking;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {User.class, Station.class}, version = 1)
public abstract class UserDatabase extends RoomDatabase {
    private static UserDatabase userDatabase;
    private static final String DATABASE_NAME = "user-database";

    public abstract UserDao userDao();
    public abstract StationDao stationDao();

    public synchronized static UserDatabase getDatabaseInstance(Context context) {
        if (userDatabase == null) {
            userDatabase = Room.databaseBuilder(context.getApplicationContext(), UserDatabase.class, DATABASE_NAME)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return userDatabase;
    }
}
