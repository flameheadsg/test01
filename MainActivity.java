package com.example.walmartcodingassessment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ArrayList<CountryItem> countryItemList;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // configure RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        new NetworkCallerThread().start();
    }

    class NetworkCallerThread extends Thread {
        String classTag = NetworkCallerThread.class.getSimpleName();
        String countriesData = "";

        @Override
        public void run() {
            // fetch JSON data from public URL
            try {
                URL url = new URL("https://gist.githubusercontent.com/peymano-wmt/32dcb892b06648910ddd40406e37fdab/raw/db25946fd77c5873b0303b858e861ce724e0dcd0/countries.json");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = connection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    countriesData = countriesData + line;
                }

                // parse countries JSON data for presentation in RecyclerView
                JSONArray countriesJSONArray = new JSONArray(countriesData);
                for (int i = 0; i < countriesJSONArray.length(); i++) {
                    JSONObject countryJSON = countriesJSONArray.getJSONObject(i);
                    String name = countryJSON.getString("name");
                    String code = countryJSON.getString("code");
                    String capital = countryJSON.getString("capital");
                    String region = countryJSON.getString("region");

                    CountryItem country = new CountryItem(
                        name,
                        code,
                        capital,
                        region
                    );
                    countryItemList.add(country);
                }

                recyclerView.setAdapter(new CountryAdapter(countryItemList));
            // exception-handling during IO operations and JSON parsing
            } catch (IOException e) {
                Log.e(classTag, "caught IOException");
                e.printStackTrace();
            } catch (JSONException e) {
                Log.e(classTag, "caught JSONException");
                throw new RuntimeException(e);
            }
        }
    }

    // data class for Country JSON items containing its fields
    public class CountryItem {
        private String name;
        private String code;
        private String capital;
        private String region;

        CountryItem(
            String _name,
            String _code,
            String _capital,
            String _region
        ) {
            name = _name;
            code = _code;
            capital = _capital;
            region = _region;
        }

        public String getName() {
            return name;
        }

        public String getCode() {
            return code;
        }

        public String getCapital() {
            return capital;
        }

        public String getRegion() {
            return region;
        }
    }

    // country data adapter class for RecyclerView
    public class CountryAdapter extends RecyclerView.Adapter<CountryAdapter.ViewHolder> {
        private List<CountryItem> countriesList;

        public CountryAdapter(List<CountryItem> countriesList) {
            this.countriesList = countriesList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
            return new ViewHolder(view);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            CountryItem item = countriesList.get(position);
            holder.nameAndRegionTextView.setText(item.getName() + ", " + item.getRegion());
            holder.codeTextView.setText(item.getCode());
            holder.capitalTextView.setText(item.getCapital());
        }

        @Override
        public int getItemCount() {
            return countriesList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView nameAndRegionTextView;
            public TextView codeTextView;
            public TextView capitalTextView;

            public ViewHolder(View view) {
                super(view);
                nameAndRegionTextView = view.findViewById(R.id.name_and_region_text_view);
                codeTextView = view.findViewById(R.id.code_text_view);
                capitalTextView = view.findViewById(R.id.capital_text_view);
            }
        }
    }
}