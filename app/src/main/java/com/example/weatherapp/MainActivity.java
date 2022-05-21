package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;


import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout homeRl;
    private ProgressBar loadingPb;
    private TextView cityNameTV, tempratureTV, conditionTv;
    private TextInputEditText etCityName;
    private RecyclerView weatherRv;
    private ImageView backIv, iconIV, searchIcon;
    private ArrayList<weatherModel> weatherModelArrayList;
    private WeatherAdapter weatherAdapter;
    String FinalcityName;
    private GpsTracker gpsTracker;

    final int REQUEST_CODE = 101;
    LocationManager locationManager;
    LocationListener locationListener;


//    private final LocationListener mLocationListener = new LocationListener() {
//
//        @Override
//        public void onLocationChanged(final Location location) {
//
//            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                return;
//            }
//            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME,
//                    LOCATION_REFRESH_DISTANCE, mLocationListener);
//
//            double latitude = location.getLatitude();
//            double longitude = location.getLongitude();
//
//
//            String Value = getCityName(latitude, longitude);
//
//            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//
//
//        }
//    };

    //Permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public String getCityName(Double latitude, Double longitude) {

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        FinalcityName = addresses.get(0).getLocality();

        return FinalcityName;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        initViews();

        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        getLocation();
    }

    public void getLocation() {
        gpsTracker = new GpsTracker(MainActivity.this);
        if (gpsTracker.canGetLocation()) {
            double latitude = gpsTracker.getLatitude();
            double longitude = gpsTracker.getLongitude();

            getCityName(latitude, longitude);

        } else {
            gpsTracker.showSettingsAlert();
        }


        getInfo(FinalcityName);

        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String city = etCityName.getText().toString().toUpperCase(Locale.ROOT);
                if (city.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please Enter the City Name",
                            Toast.LENGTH_SHORT).show();
                } else {
                    etCityName.setText(city);

                    getInfo(city);
                    cityNameTV.setText(city);
                }
            }
        });
    }


    private void initViews() {
        homeRl = findViewById(R.id.home);
        loadingPb = findViewById(R.id.loadingPb);
        cityNameTV = findViewById(R.id.headingCityName);
        tempratureTV = findViewById(R.id.temprature);
        conditionTv = findViewById(R.id.TvTempCondition);
        weatherRv = findViewById(R.id.RvWeather);
        etCityName = findViewById(R.id.EtCityName);
        backIv = findViewById(R.id.backgroundImage);
        iconIV = findViewById(R.id.IvTempCondition);
        searchIcon = findViewById(R.id.searchIV);
        weatherModelArrayList = new ArrayList<>();
        weatherAdapter = new WeatherAdapter(this, weatherModelArrayList);
        weatherRv.setAdapter(weatherAdapter);
    }


    private void getInfo(String cityName) {
        String url = "http://api.weatherapi.com/v1/forecast.json?key=176ae561d2394951ab4110659221905&q=" + cityName + "&days=1&aqi=yes&alerts=yes";
        Log.d("urldenug", url);
        etCityName.setText(cityName);
        cityNameTV.setText(cityName);


        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loadingPb.setVisibility(View.GONE);
                        homeRl.setVisibility(View.VISIBLE);

                        weatherModelArrayList.clear();
                        try {

                            Log.d("ncbafcjab", String.valueOf(response));
                            String temperature = response.getJSONObject("current").getString("temp_c");
                            tempratureTV.setText(temperature + "°C");

                            int isday = response.getJSONObject("current").getInt("is_day");

                            String condition = response.getJSONObject("current").getJSONObject("condition")
                                    .getString("text");
                            String conditionIcon = response.getJSONObject("current").getJSONObject("condition")
                                    .getString("icon");
                            Picasso.get().load("https:".concat(conditionIcon)).into(iconIV);
                            conditionTv.setText(condition);
                            if (isday == 1) {
                                Picasso.get().load("https://images.unsplash.com/photo-1542349314-587b18ea1c2a?crop=entropy&cs=tinysrgb&fm=jpg&ixlib=rb-1.2.1&q=80&raw_url=true&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1169").into(backIv);
                            } else {
                                int color = Integer.parseInt("bdbdbd", 16) + 0xFF000000;
                                tempratureTV.setTextColor(color);
                                etCityName.setTextColor(color);
                                searchIcon.setColorFilter(color);
                                conditionTv.setTextColor(color);
                                Picasso.get().load("https://images.unsplash.com/photo-1590272456521-1bbe160a18ce?crop=entropy&cs=tinysrgb&fm=jpg&ixlib=rb-1.2.1&q=80&raw_url=true&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=627").into(backIv);
                            }
                            JSONObject forcastObject = response.getJSONObject("forecast");
                            JSONObject forcast0 = forcastObject.getJSONArray("forecastday").optJSONObject(0);
                            JSONArray hourArray = forcast0.getJSONArray("hour");

                            for (int i = 0; i < hourArray.length(); i++) {
                                JSONObject hourObj = hourArray.getJSONObject(i);
                                String time = hourObj.getString("time");
                                String temper = hourObj.getString("temp_c");
                                String img = hourObj.getJSONObject("condition").getString("icon");
                                String wind = hourObj.getString("wind_kph");

                                weatherModelArrayList.add(new weatherModel(time, temper, img, wind));
                            }
                            weatherAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Please Enter the Valid City Name...",
                        Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

}