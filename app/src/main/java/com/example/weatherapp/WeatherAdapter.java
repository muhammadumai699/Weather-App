package com.example.weatherapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.ViewHolder> {

    private Context context;
    private ArrayList<weatherModel> weatherModelArrayList;


    public WeatherAdapter(Context context, ArrayList<weatherModel> weatherModelArrayList) {
        this.context = context;
        this.weatherModelArrayList = weatherModelArrayList;
    }

    @NonNull
    @Override
    public WeatherAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.weather_design, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherAdapter.ViewHolder holder, int position) {

        weatherModel model = weatherModelArrayList.get(position);
        Picasso.get().load("https:".concat(model.getIcon())).into(holder.weatherCondition);
        holder.tempTV.setText(model.getTemperature() + "°C");
        holder.windSpeed.setText(model.getWindSpeed() + "Km/hr");
        SimpleDateFormat getFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        SimpleDateFormat Time = new SimpleDateFormat("hh:mm aa");

        try {
            Date t = getFormat.parse(model.getTime());
            holder.timeTV.setText(Time.format(t));

        } catch (ParseException e) {
            e.printStackTrace();

        }
    }

    @Override
    public int getItemCount() {
        return weatherModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView timeTV, tempTV, windSpeed;
        ImageView weatherCondition;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            timeTV = itemView.findViewById(R.id.timeTV);
            tempTV = itemView.findViewById(R.id.tempTV);
            windSpeed = itemView.findViewById(R.id.windSpeed);
            weatherCondition = itemView.findViewById(R.id.weatherCondition);
        }
    }
}
