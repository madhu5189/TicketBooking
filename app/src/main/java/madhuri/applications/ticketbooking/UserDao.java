package madhuri.applications.ticketbooking;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface UserDao {

    @Query("SELECT * FROM user_table")
    List<User> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(User mUser);

    @Insert
    void insertAllUser(User... mUsersList);

    @Delete
    void delete(User mUser);

    @Update
    void updateUser(User mUser);

    @Query("SELECT * FROM user_table WHERE phone_no = :phoneNo")
    User getUserByPhoneNo(String phoneNo);
}
