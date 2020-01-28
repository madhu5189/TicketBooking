package madhuri.applications.ticketbooking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ConfirmTripActivity extends AppCompatActivity {
    private TextView tvName, tvDocID, tvSrc, tvDest, tvPrice;
    private Button btnConfirm;

    UserDatabase userDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_trip);

        tvName = findViewById(R.id.tvName);
        tvDocID = findViewById(R.id.tvDocID);
        tvSrc = findViewById(R.id.tvSource);
        tvDest = findViewById(R.id.tvDestination);
        tvPrice = findViewById(R.id.tvPrice);
        btnConfirm = findViewById(R.id.btnConfirm);

        userDatabase = UserDatabase.getDatabaseInstance(this);
        User user = userDatabase.userDao().getUserByPhoneNo(DetailsActivity.phoneNumber);

        if (!user.equals(null)) {
        tvName.setText(user.getName());
        tvDocID.setText(user.getDocID());
        }
        tvSrc.setText(PlanTripActivity.srcStn);
        tvDest.setText(PlanTripActivity.destStn);
        tvPrice.setText(String.valueOf(Math.abs(PlanTripActivity.destDist - PlanTripActivity.srcDist)));

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent ticketIntent = new Intent(ConfirmTripActivity.this, TicketActivity.class);
                startActivity(ticketIntent);


            }
        });
    }
}
