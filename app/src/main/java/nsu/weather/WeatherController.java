package nsu.weather;

import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class WeatherController extends AppCompatActivity {
    
    final String WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather";

//    final String APP_ID = "e72ca729af228beabd5d20e3b7749713";

    final long MIN_TIME = 5000;

    final float MIN_DISTANCE = 1000;


    final String LOGCAT_TAG = "Clima";


    final String LOCATION_PROVIDER = LocationManager.GPS_PROVIDER;

    // Member Variables:
    boolean mUseLocation = true;
    TextView mCityLabel;
    ImageView mWeatherImage;
    TextView mTemperatureLabel;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
