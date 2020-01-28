package madhuri.applications.ticketbooking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivityGetOTP extends AppCompatActivity {




    private EditText etPhoneNumber;
    private Button btnGetOTP;
    private String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_get_otp);

        etPhoneNumber = findViewById(R.id.etPhoneNo);
        btnGetOTP = findViewById(R.id.btnGetOTP);

        btnGetOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent detailsIntent = new Intent(MainActivityGetOTP.this, DetailsActivity.class);
                Boolean sendOTP = true;
                if (checkPhoneNumber()) {
                    detailsIntent.putExtra("phoneNumber", phoneNumber);
                    detailsIntent.putExtra("sentOTP", sendOTP);

                    startActivity(detailsIntent);
                }
            }
        });
    }

    private boolean checkPhoneNumber() {
        phoneNumber = etPhoneNumber.getText().toString();

        if (phoneNumber.equals("") || phoneNumber.equals(null)|| phoneNumber.length()<10) {
            etPhoneNumber.setError("Invalid phone number.");
            return false;
        }
        phoneNumber = "+91" + phoneNumber;
        return true;
    }

}
