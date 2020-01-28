package madhuri.applications.ticketbooking;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class StationApiResponse {
    @SerializedName("station")
    @Expose
    private List<Station> station = null;

    public List<Station> getStation() {
        return station;
    }

    public void setStation(List<Station> station) {
        this.station = station;
    }
}
