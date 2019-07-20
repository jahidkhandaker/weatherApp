package nsu.weather;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class WeatherController extends AppCompatActivity {

    final int REQUEST_CODE = 123;

    final String WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather";

   // final String APP_ID = "797dda97fa70a63c0dd03b9d11006ea0";
    final String APP_ID = "e72ca729af228beabd5d20e3b7749713";

    final long MIN_TIME = 5000;

    final float MIN_DISTANCE = 1000;


    final String LOGCAT_TAG = "weatherApp";


    final String LOCATION_PROVIDER = LocationManager.GPS_PROVIDER;
    // final String LOCATION_PROVIDER = LocationManager.NETWORK_PROVIDER;

    // Member Variables:
    boolean mUseLocation = true;
    TextView mCityLabel;
    ImageView mWeatherImage;
    TextView mTemperatureLabel;
    ImageButton changeCityButton;

    LocationManager mLocationManager;
    LocationListener mLocationListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCityLabel = findViewById(R.id.locationTv);
        mWeatherImage = findViewById(R.id.ChangeCityLocation);
        mTemperatureLabel = findViewById(R.id.tempTV);
        changeCityButton = findViewById(R.id.ChangeCityButton);

        changeCityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(WeatherController.this, ChangeCityActivity.class );
                startActivity(myIntent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("weatherApp", "onResume() Called");
        Intent myIntent = getIntent();
        String city = myIntent.getStringExtra("City");

        if (city !=null){
            getWeatherForNewCity(city);
        } else {
            Log.d("weatherApp", "for current location");
            getWeatherForCurrentLocation();
        }

       
    }

    private void getWeatherForNewCity(String city) {
       RequestParams params =new RequestParams();
       params.put("q",city);
       params.put("appId",APP_ID);
       letsDoSomeNetworking(params);
    }


    private void getWeatherForCurrentLocation() {
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("weatherApp", "onLocationChange received");

                String longitude = String.valueOf(location.getLongitude());
                String latitude = String.valueOf(location.getLatitude());
                Log.d("weatherApp", "Longitude: "+longitude);
                Log.d("weatherApp", "Longitude: "+latitude);

                RequestParams params =  new RequestParams();
                params.put("lat", latitude);
                params.put("lon", longitude);
                params.put("appid", APP_ID);

                letsDoSomeNetworking(params);

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.d("weatherApp", "onProviderDisable received");
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);

            return;
        }
        mLocationManager.requestLocationUpdates(LOCATION_PROVIDER, MIN_TIME, MIN_DISTANCE, mLocationListener);

    }

    private void letsDoSomeNetworking(RequestParams params) {
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(WEATHER_URL,params,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response){
                Log.d("weatherApp", "success! JSON:  "+ response.toString());

                WeatherDataModel weatherData = WeatherDataModel.fromJson(response);
                updateUI(weatherData);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e ,JSONObject response){
                Log.e("weatherApp", "Fail "+ e.toString());
                Log.d("weatherApp", "status code "+ statusCode);
                Toast.makeText(WeatherController.this,"Request Failed", Toast.LENGTH_SHORT).show();
            }

        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE ){
            if ( grantResults.length > 0 && grantResults[0] ==PackageManager.PERMISSION_GRANTED){
                Log.d("weatherApp", "onRequestPermissionResult():Permission Granted");

            } else {
                Log.d("weatherApp", "Permission Denied");
            }
        }
    }

    private void updateUI(WeatherDataModel weather){
        mTemperatureLabel.setText(weather.getTemperature());
        mCityLabel.setText(weather.getCity());

        int resourceID = getResources().getIdentifier(weather.getIconName(), "drawable", getPackageName());

        mWeatherImage.setImageResource(resourceID);

    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mLocationManager != null) mLocationManager.removeUpdates(mLocationListener);
    }
}
