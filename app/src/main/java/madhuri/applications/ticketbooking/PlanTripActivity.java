package madhuri.applications.ticketbooking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PlanTripActivity extends AppCompatActivity {

    private Spinner spinnerSrcStn, spinnerDestStn;
    private TextView tvFinalPrice;
    private Button btnProceed;
    List<Station> stations;

    static int srcDist = 0;
    static int destDist = 0;
    static String srcStn;
    static String destStn;

    UserDatabase userDatabase = UserDatabase.getDatabaseInstance(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_trip);

        spinnerSrcStn = findViewById(R.id.spinnerSrcStn);
        spinnerDestStn = findViewById(R.id.spinnerDestStn);
        tvFinalPrice = findViewById(R.id.tvFinalPrice);
        btnProceed = findViewById(R.id.btnProceed);

        //setSpinner();

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://2a46dc28-6b1e-457f-8343-dfd73b1c2483.mock.pstmn.io")
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        GetStation service = retrofit.create(GetStation.class);

        // Calling '/api/users/2'
        Call<StationApiResponse> callSync = service.getStation();
        callSync.enqueue(new Callback<StationApiResponse>() {
            @Override
            public void onResponse(Call<StationApiResponse> call, Response<StationApiResponse> response) {
                StationApiResponse stationApiResponse = response.body();
                stations = stationApiResponse.getStation();
                //Station stationTable = new Station();
                for (Station station : stations){
                    userDatabase.stationDao().insertStation(station);
                }
                System.out.println(response);
                setSpinner(stations);
            }
            @Override
            public void onFailure(Call<StationApiResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });

        

        btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent confirmTripIntent = new Intent(PlanTripActivity.this, ConfirmTripActivity.class);
                startActivity(confirmTripIntent);
            }
        });
    }

    /*private void setSpinner(int spinnerID) {
        switch (spinnerID) {
            case R.id.spinnerSrcStn:
                String[] src = {"Aadhaar", "Voter ID", "PAN", "Driver's License"};
                ArrayAdapter<String> srcAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, src);
                spinnerSrcStn.setAdapter(srcAdapter);
                break;
            case R.id.spinnerDestStn:
                String[] dest = {"Aadhaar", "Voter ID", "PAN", "Driver's License"};
                ArrayAdapter<String> destAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dest);
                spinnerDestStn.setAdapter(destAdapter);
                break;
            default:
                Toast.makeText(this, "Error loading stations", Toast.LENGTH_LONG).show();
        }*/

        private void setSpinner(List<Station> stations) {
            ArrayList<String> src = new ArrayList();
            final ArrayList<Integer> dist = new ArrayList<>();
            for (Station station: stations) {
                src.add(station.getName());
                dist.add(station.getDistance());
            }
            ArrayAdapter<String> srcAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, src);
            spinnerSrcStn.setAdapter(srcAdapter);
            ArrayAdapter<String> destAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, src);
            spinnerDestStn.setAdapter(destAdapter);


            spinnerSrcStn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String text = parent.getItemAtPosition(position).toString();
                    srcStn = text;
                    srcDist = dist.get(position);
                    //Toast.makeText(parent.getContext(), text, Toast.LENGTH_SHORT).show();
                    tvFinalPrice.setText(String.valueOf(Math.abs(destDist - srcDist)));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            spinnerDestStn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String text = parent.getItemAtPosition(position).toString();
                    destStn = text;
                    destDist = dist.get(position);
                    //Toast.makeText(parent.getContext(), text, Toast.LENGTH_SHORT).show();
                    tvFinalPrice.setText(String.valueOf(Math.abs(destDist - srcDist)));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

        }


}
