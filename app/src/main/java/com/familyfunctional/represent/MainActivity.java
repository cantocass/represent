package com.familyfunctional.represent;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit.Call;
import retrofit.Callback;
import retrofit.JacksonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getCanonicalName();
    public static final int REQUEST_LOCATION = 1;
    private static String[] PERMISSIONS_LOCATION = {Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION};

    private Button gpsButton;
    private EditText zipcodeEditText;
    private Button zipButton;
    private TextView orText;
    private WhoIsMyRepresentativeService api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupApi();

        gpsButton = (Button) findViewById(R.id.btn_use_my_location);
        orText = (TextView) findViewById(R.id.or_text);
        zipcodeEditText = (EditText) findViewById(R.id.zip_code);
        zipButton = (Button) findViewById(R.id.btn_use_another_location);

        zipcodeEditText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    zipButton.performClick();
                    return true;
                }
                return false;
            }
        });
    }

    //region Location
    public void onUseTextLocationClick(View view) {
        launchResultsActivity(zipcodeEditText.getText().toString());
    }

    public void onUseGPSLocationClick(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            requestLocationPermission(view);

        } else {
            String zip = getZipFromLocation(view);
            launchResultsActivity(zip);
        }
    }

    private String getZipFromLocation(View view) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addressesList;
        final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;

        try {
            for (String provider : providers) {
                Location lastLocation = locationManager.getLastKnownLocation(provider);
                if (lastLocation == null) {
                    continue;
                }
                if (bestLocation == null || lastLocation.getAccuracy() < bestLocation.getAccuracy()) {
                    bestLocation = lastLocation;
                }
            }
            addressesList = geocoder.getFromLocation(bestLocation.getLatitude(), bestLocation.getLongitude(), 1);
        } catch (IOException | SecurityException | NullPointerException e) {
            Log.e(TAG, e.getMessage());
            Snackbar.make(view, R.string.location_unavailable, Snackbar.LENGTH_LONG)
                    .setAction(R.string.go, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 1);
                        }
                    })
                    .show();
            return getString(R.string.empty_zip);
        }

        return addressesList.get(0).getPostalCode();
    }
    //endregion

    //region Network
    private void setupApi() {
        Retrofit restAdapter = new Retrofit.Builder()
                .baseUrl("http://www.whoismyrepresentative.com")
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

        api = restAdapter.create(WhoIsMyRepresentativeService.class);
    }
    //endregion

    //region Navigation
    private void launchResultsActivity(String zip) {
        Call<Result> call = api.getAllMembersByZip(zip);
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Response<Result> response, Retrofit retrofit) {
                Intent intent = new Intent(MainActivity.this, ResultsActivity.class);
                ArrayList<Member> members = new ArrayList<>(response.body().results);
                intent.putParcelableArrayListExtra(ResultsActivity.KEY_MEMBER_LIST, members);
                startActivity(intent);
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            switch (requestCode) {
                case 1:
                    break;
            }
        }
    }
    //endregion

    //region Runtime Permissions
    private void requestLocationPermission(View view) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION) ||
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)) {

            Snackbar.make(view, R.string.location_permission_required, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    PERMISSIONS_LOCATION, REQUEST_LOCATION);
                        }
                    })
                    .show();

        } else {
            ActivityCompat.requestPermissions(MainActivity.this,
                    PERMISSIONS_LOCATION, REQUEST_LOCATION);
        }
    }

    public static boolean verifyPermissions(int[] grantResults) {
        if(grantResults.length < 1){
            return false;
        }

        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION: {

                if (verifyPermissions(grantResults)) {


                } else {
                    gpsButton.setVisibility(View.GONE);
                    orText.setVisibility(View.GONE);
                }
            }
        }
    }
    //endregion
}
