package madhuri.applications.ticketbooking;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class DetailsActivity extends AppCompatActivity {

    private EditText etCustomerName, etDOB, etDocumentID, etEnterOTP;
    private Spinner spinnerDocumentType;
    private Button btnVerifyOTP;


    private static final String TAG = "PhoneAuth";
    private FirebaseAuth mAuth;
    private boolean mVerificationInProgress = false;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    public static String phoneNumber;
    private Boolean sendOTP;
    private String selectedDocumentType;

    UserDatabase userDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        phoneNumber = this.getIntent().getStringExtra("phoneNumber");
        sendOTP = this.getIntent().getBooleanExtra("sendOTP", true);

        etCustomerName = findViewById(R.id.etCustomerName);
        etDOB = findViewById(R.id.etDOB);
        etDocumentID = findViewById(R.id.etDocumentID);
        etEnterOTP = findViewById(R.id.etEnterOTP);
        spinnerDocumentType = findViewById(R.id.spinnerDocumentType);
        btnVerifyOTP = findViewById(R.id.btnVerifyOTP);
        setSpinner();

        userDatabase = UserDatabase.getDatabaseInstance(this);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:" + credential);
                // [START_EXCLUDE silent]
                mVerificationInProgress = false;
                // [END_EXCLUDE]

                // [START_EXCLUDE silent]
                // Update the UI and attempt sign in with the phone credential
                // updateUI(STATE_VERIFY_SUCCESS, credential);
                // [END_EXCLUDE]
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);
                // [START_EXCLUDE silent]
                mVerificationInProgress = false;
                // [END_EXCLUDE]

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // [START_EXCLUDE]
                    etEnterOTP.setError("Invalid OTP.");
                    // [END_EXCLUDE]
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // [START_EXCLUDE]
                    Snackbar.make(findViewById(android.R.id.content), "Quota exceeded.",
                            Snackbar.LENGTH_SHORT).show();
                    // [END_EXCLUDE]
                }

                // Show a message and update the UI
                // [START_EXCLUDE]
                //updateUI(STATE_VERIFY_FAILED);
                // [END_EXCLUDE]
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                // [START_EXCLUDE]
                // Update UI
                //updateUI(STATE_CODE_SENT);
                // [END_EXCLUDE]
            }
        };

        if (sendOTP) {
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    phoneNumber,        // Phone number to verify
                    60,                 // Timeout duration
                    TimeUnit.SECONDS,   // Unit of timeout
                    DetailsActivity.this,               // Activity (for callback binding)
                    mCallbacks);
            sendOTP = false;
        }

        btnVerifyOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String customerName = etCustomerName.getText().toString();
                String dob = etDOB.getText().toString();
                String documentID = etDocumentID.getText().toString();

                String code = etEnterOTP.getText().toString();


                checkDataEntered();
                if (TextUtils.isEmpty(code)) {
                    etEnterOTP.setError("Cannot be empty.");
                    return;
                }

                User user = new User(phoneNumber, customerName, dob, documentID);
                userDatabase.userDao().insertUser(user);

                verifyPhoneNumberWithCode(mVerificationId, code);
            }
        });

        spinnerDocumentType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDocumentType = parent.getItemAtPosition(position).toString();
                switch (selectedDocumentType){
                    case "Aadhaar":
                        etDocumentID.setInputType(InputType.TYPE_CLASS_NUMBER);
                        etDocumentID.setFilters(new InputFilter[] {new InputFilter.LengthFilter(12)});
                        break;
                    default:
                        etDocumentID.setFilters(new InputFilter[] {new InputFilter.LengthFilter(10)});
                        break;
                }


                //Toast.makeText(parent.getContext(), selectedDocumentType, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        // [START verify_with_code]
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        // [END verify_with_code]
        signInWithPhoneAuthCredential(credential);
    }

    // [START sign_in_with_phone]
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            Toast.makeText(DetailsActivity.this,"OTP success", Toast.LENGTH_LONG);
                            etCustomerName.setEnabled(false);
                            etDOB.setEnabled(false);
                            etDocumentID.setEnabled(false);
                            etEnterOTP.setEnabled(false);
                            spinnerDocumentType.setEnabled(false);
                            Intent planTripIntent = new Intent(DetailsActivity.this, PlanTripActivity.class);
                            startActivity(planTripIntent);

                            FirebaseUser user = task.getResult().getUser();
                            // [START_EXCLUDE]
                            //updateUI(STATE_SIGNIN_SUCCESS, user);
                            // [END_EXCLUDE]
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                // [START_EXCLUDE silent]
                                etEnterOTP.setError("Invalid code.");
                                //btnVerifyOTP.setText(R.string.resend_otp);
                                // [END_EXCLUDE]
                            }
                            // [START_EXCLUDE silent]
                            // Update UI
                            //updateUI(STATE_SIGNIN_FAILED);
                            // [END_EXCLUDE]
                        }
                    }
                });
    }
    // [END sign_in_with_phone]

    private void setSpinner(){
        String[] items = { "Aadhaar", "Voter ID", "PAN", "Driver's License"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        spinnerDocumentType.setAdapter(adapter);


    }

    private void checkDataEntered(){
        if (TextUtils.isEmpty(etCustomerName.getText().toString())) {
            etCustomerName.setError("Name is required!");
        }
        if (TextUtils.isEmpty(etDOB.getText().toString())) {
            etDOB.setError("Date of Birth is required!");
        }
        if (TextUtils.isEmpty(etDocumentID.getText().toString())) {
            etDocumentID.setError("Document ID is required!");
        }
        if (TextUtils.isEmpty(etEnterOTP.getText().toString())) {
            etEnterOTP.setError("OTP is required!");
        }

        DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        // Input to be parsed should strictly follow the defined date format
        // above.
        format.setLenient(false);
        String date = etDOB.getText().toString();
        try {
            format.parse(date);
        } catch (ParseException e) {
            System.out.println("Date " + date + " is not valid according to " +
                    ((SimpleDateFormat) format).toPattern() + " pattern.");
        }
        String docIdPattern = "[a-zA-Z0-9]";

        switch (selectedDocumentType){
            case "Aadhaar":
                if (!(etDocumentID.getText().toString().trim().matches("[0-9]"))){
                    etDocumentID.setError("Invalid Document ID!");
                }
                break;
            default:
                if (!(etDocumentID.getText().toString().trim().matches(docIdPattern))){
                    etDocumentID.setError("Invalid Document ID!");
                }
                break;
        }



    }
}
