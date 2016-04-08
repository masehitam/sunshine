package com.example.android.sunshine.app;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment {

    ArrayAdapter<String> mForecastAdapter;

    public ForecastFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        
        // Create some dummy data for the ListView.  Here's a sample weekly forecast
        // Data dummy yang akan diatambahkan ke dalam list view
        String[] weathers = {
                "Today - Foggy - 88 / 63",
                "Monday - Foggy - 78 / 63",
                "Tuesday - Cloudy - 83 / 63",
                "Wednesday - Cloudy - 84 / 63",
                "Thursday - Rainy - 85 / 63",
                "Friday - Sunny - 86 / 63",
                "Saturday - Foggy - 87 / 63",
                "Sunday - Cloudy - 88 / 63",
                "Monday - Stormy - 78 / 63",
                "Tuesday - Sunny - 83 / 63",
                "Wednesday - Sunny - 84 / 63",
                "Thursday - Rainy - 85 / 63",
                "Friday - Stormy - 86 / 63"
        };


        // Now that we have some dummy forecast data, create an ArrayAdapter.
        // The ArrayAdapter will take data from a source (like our dummy forecast) and
        // use it to populate the ListView it's attached to.

        /*1 elemen view menggunakan 1 adapter*/
        mForecastAdapter = new ArrayAdapter<String>(
                //referensi ke konteks / ke aktiviti yang sedang digunakan
                getActivity(),
                //id file layout yang akan dijadikan list view
                R.layout.list_item_forecast,
                //id view elemen yang akan dijadikan list view
                R.id.list_item_forecast_textview,
                //data string
                weathers);

        //root view untuk menuju fragmen
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        //adapter untuk mengatur tampilan string ke dalam listview
        
        //referensi list view di fragment
        ListView listview = (ListView)rootView.findViewById(R.id.forecast_listview);
        //menerapkan adapter ke dalam listview
        listview.setAdapter(mForecastAdapter);

        return rootView;
    }

    public class FetchWeatherTask extends AsyncTask<Void, Void, Void> {
        
        // mengembalikan nama kelas, jadi ketika kelas di renami otomatis akan terename juga
        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

        @Override
        protected Void doInBackground(Void... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            // HttpURLConnection adalah kelas yang digunakan untuk http request
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                String baseUrl = "http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7";
                // OPEN_WEATHER_MAP_API_KEY dideklarasikan di /~/app/build.gradle
                // sintak greadle belum dimengerti -_-
                String apiKey = "&APPID=" + BuildConfig.OPEN_WEATHER_MAP_API_KEY;
                URL url = new URL(baseUrl.concat(apiKey));

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                forecastJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e("PlaceholderFragment", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }
        return null;
        }
    }
}