package madhuri.applications.ticketbooking;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "user_table"
)
public class User {


    @ColumnInfo(name = "phone_no")
    private String phoneNo;

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "dob")
    private String dob;

    @ColumnInfo(name = "doc_id")
    private String docID;

    public User(String phoneNo, String name, String dob, String docID) {
        this.phoneNo = phoneNo;
        this.name = name;
        this.dob = dob;
        this.docID = docID;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getDocID() {
        return docID;
    }

    public void setDocID(String docID) {
        this.docID = docID;
    }
}
