package madhuri.applications.ticketbooking;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface StationDao {
    @Query("SELECT * FROM station_table")
    List<Station> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertStation(Station mStation);

    @Insert
    void insertAllStation(Station... mStationList);

    @Delete
    void delete(Station mStation);

    @Update
    void updateStation(Station mStation);

    @Query("SELECT * FROM station_table WHERE id = :id")
    User getStationById(int id);
}
