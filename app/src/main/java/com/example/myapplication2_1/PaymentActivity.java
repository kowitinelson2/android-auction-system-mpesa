package com.example.myapplication2_1;

import static com.example.myapplication2_1.Constants.BUSINESS_SHORT_CODE;
import static com.example.myapplication2_1.Constants.CALLBACKURL;
import static com.example.myapplication2_1.Constants.PARTYB;
import static com.example.myapplication2_1.Constants.PASSKEY;
import static com.example.myapplication2_1.Constants.TRANSACTION_TYPE;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication2_1.Services.DarajaApiClient;
import com.example.myapplication2_1.model.AccessToken;
import com.example.myapplication2_1.model.STKPush;

import java.text.NumberFormat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;


public class PaymentActivity extends AppCompatActivity implements View.OnClickListener {

    private DarajaApiClient mApiClient;
    private ProgressDialog mProgressDialog;


    TextView tax,serviceFee,total;
    EditText subTotal;
    EditText etPhone;
    Button pay_btn;
    NumberFormat nm = NumberFormat.getNumberInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        //toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.payment_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Button checkOut = (Button) findViewById(R.id.pay_btn);
        checkOut.setOnClickListener(this);

        mProgressDialog = new ProgressDialog(this);
        mApiClient = new DarajaApiClient();
        mApiClient.setIsDebug(true); //Set True to enable logging, false to disable.

        //pay_btn.setOnClickListener(this);

        getAccessToken();

        etPhone = findViewById(R.id.etPhone);
        subTotal = findViewById(R.id.sub_total);
        tax = findViewById(R.id.textView17);
        tax.setText (nm.format(500));
        serviceFee = findViewById(R.id.textView18);
        serviceFee.setText (nm.format(200));

        total = findViewById(R.id.total_amt);



    }

    public void getAccessToken() {
        mApiClient.setGetAccessToken(true);
        mApiClient.mpesaService().getAccessToken().enqueue(new Callback<AccessToken>() {
            @Override
            public void onResponse(@NonNull Call<AccessToken> call, @NonNull Response<AccessToken> response) {

                if (response.isSuccessful()) {
                    mApiClient.setAuthToken(response.body().accessToken);
                }
            }

            @Override
            public void onFailure(@NonNull Call<AccessToken> call, @NonNull Throwable t) {

            }
        });
    }

        @Override
        public void onClick(View v) {
        buttonClicked();

        }

        private void buttonClicked(){
            int n;

            try {
                n = Integer.parseInt(subTotal.getText().toString());
            }
            catch (NumberFormatException e)
            {
                n = 0;
            }
            int sum = n + 500 + 200;
            total.setText (nm.format(sum));

            String phone_number = etPhone.getText().toString();
            String amount = subTotal.getText().toString();
            performSTKPush(phone_number,amount);
        }

    public void performSTKPush(String phone_number,String amount) {
        mProgressDialog.setMessage("Processing your request");
        mProgressDialog.setTitle("Please Wait...");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.show();
        String timestamp = Utils.getTimestamp();
        STKPush stkPush = new STKPush(
                BUSINESS_SHORT_CODE,
                Utils.getPassword(BUSINESS_SHORT_CODE, PASSKEY, timestamp),
                timestamp,
                TRANSACTION_TYPE,
                String.valueOf(amount),
                Utils.sanitizePhoneNumber(phone_number),
                PARTYB,
                Utils.sanitizePhoneNumber(phone_number),
                CALLBACKURL,
                "SmartLoan Ltd", //Account reference
                "SmartLoan STK PUSH by TDBSoft"  //Transaction description
        );

        mApiClient.setGetAccessToken(false);

        //Sending the data to the Mpesa API, remember to remove the logging when in production.
        mApiClient.mpesaService().sendPush(stkPush).enqueue(new Callback<STKPush>() {
            @Override
            public void onResponse(@NonNull Call<STKPush> call, @NonNull Response<STKPush> response) {
                mProgressDialog.dismiss();
                try {
                    if (response.isSuccessful()) {
                        Timber.d("post submitted to API. %s", response.body());
                    } else {
                        Timber.e("Response %s", response.errorBody().string());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<STKPush> call, @NonNull Throwable t) {
                mProgressDialog.dismiss();
                Timber.e(t);
            }
        });
    }

}

