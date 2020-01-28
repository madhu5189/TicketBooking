package madhuri.applications.ticketbooking;

import retrofit2.Call;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface BookingIDPost {

    @Headers({"Accept: application/json",
            "name:abd",
            "source_station:kurla",
            "destination_station:dadar"})
    @POST("/fetchStations")
    public Call<StationApiResponse> getStation();
}
