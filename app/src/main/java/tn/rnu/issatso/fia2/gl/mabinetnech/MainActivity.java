package tn.rnu.issatso.fia2.gl.mabinetnech;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class MainActivity extends Activity {

    private static String TAG = MainActivity.class.getName();
    final AsyncTask<Void, Void, String> LoadAsyncTask = new LoadCategoriesAsycTask();
    private final ArrayList<JSONObject> categoriesJsonObjectsArrayList = new ArrayList<>();
    ListView categories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get an object reference of the ListView
        categories = (ListView) findViewById(R.id.categories_list_view);

        // Load the data from using AsyncTask (against network )
        LoadAsyncTask.execute();

        // set on item click listener
        categories.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // TODO : Implement the listener

                        try {
                            Intent intent = new Intent(getApplicationContext(), ThreadsActivity.class);
                            // put the id of the category in the intent extras
                            intent.putExtra(
                                    Contract.CATEGORY_ID,
                                    categoriesJsonObjectsArrayList.get(position).getInt("id"));
                            // start ThreadsActivity
                            startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_logout) {
            SharedPreferences prefs = getSharedPreferences(getResources().getString(R.string.app_name), MODE_PRIVATE);
            prefs.edit()
                    .putBoolean(Contract.USER_LOGGED, false)
                    .apply(); // apply changed to the shared preferences
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private class LoadCategoriesAsycTask extends AsyncTask<Void, Void, String> {
        String jsonString = null;

        @Override
        protected String doInBackground(Void... params) {

            SharedPreferences prefs = getSharedPreferences(getResources().getString(R.string.app_name), MODE_PRIVATE);

            String token = prefs.getString(Contract.USER_TOKEN, null);
            try {


                URL url = new URL("http://10.0.2.1/public/mabinetnetch/getallcategories.php?token=" + token);
                Log.v(TAG, url.toString());

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                BufferedReader r = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder total = new StringBuilder();
                String line;
                while ((line = r.readLine()) != null) {
                    total.append(line);
                }

                jsonString = total.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.v(TAG, jsonString);
            return jsonString;
        }

        @Override
        protected void onPostExecute(String jsonString) {
            try {
                //create JsonObject from the JsonString
                JSONObject jsonObject = new JSONObject(jsonString);
                // get the categories array as JsonArray
                JSONArray categoriesJsonArray = jsonObject.getJSONArray("categories");
                // convert jsonArray to ArrayList and save a copy with all the informations
                ArrayList<String> categoriesArrayList = new ArrayList<>();
                if (categoriesJsonArray != null) {
                    for (int i = 0; i < categoriesJsonArray.length(); ++i) {
                        // add to list item number i ;
                        categoriesArrayList.add(categoriesJsonArray.getJSONObject(i).getString("name"));
                        // a copy with the ids
                        categoriesJsonObjectsArrayList.add(categoriesJsonArray.getJSONObject(i));

                    }
                }

                //create ArrayAdapter
                ArrayAdapter<String> categoriesArrayAdapter = new ArrayAdapter<>(
                        getApplicationContext(), // context
                        R.layout.category_item_layout, // layout of the item
                        R.id.textView, // id of the textView
                        categoriesArrayList // elements of ListView
                );

                categories.setAdapter(categoriesArrayAdapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
