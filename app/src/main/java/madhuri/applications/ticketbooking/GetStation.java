package madhuri.applications.ticketbooking;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

public interface GetStation {

    @Headers("Accept: application/json")
        @GET("/fetchStations")
        public Call<StationApiResponse> getStation();


}
