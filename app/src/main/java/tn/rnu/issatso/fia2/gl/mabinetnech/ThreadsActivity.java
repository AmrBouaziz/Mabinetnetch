package tn.rnu.issatso.fia2.gl.mabinetnech;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import tn.rnu.issatso.fia2.gl.mabinetnech.model.Post;


public class ThreadsActivity extends Activity {

    private static String TAG = ThreadsActivity.class.getName();
    ArrayList<Post> postsArrayList = new ArrayList<>();
    int categoryId;
    SharedPreferences prefs;
    ListView posts;


    private LoadPostsAsyncTask loadPostsAsyncTask = new LoadPostsAsyncTask();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_threads);
        categoryId = getIntent().getIntExtra(Contract.CATEGORY_ID, -1);
        prefs = getSharedPreferences(getResources().getString(R.string.app_name), MODE_PRIVATE);
        posts = (ListView) findViewById(R.id.threads_list_view);
        loadPostsAsyncTask.execute();

        posts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), CommentsActivity.class);
                // todo add extras to intent
                // add the post in the intent
                intent.putExtra(Contract.EXTRA_POST_OBJECT,postsArrayList.get(position));
                startActivity(intent);
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //TODO : get back to this ...

        // inflate the menu with the default main menu
        getMenuInflater().inflate(R.menu.menu_main, menu);
        // append to it the threads menu
        getMenuInflater().inflate(R.menu.menu_threads, menu);

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
        }

        return super.onOptionsItemSelected(item);
    }

    private class LoadPostsAsyncTask extends AsyncTask<Void, Void, String> {
        private String jsonString;

        @Override
        protected String doInBackground(Void... params) {

            String token = prefs.getString(Contract.USER_TOKEN, null);
            try {


                URL url = new URL("http://10.0.2.1/public/mabinetnetch/getcategoryposts.php?token=" + token + "&category=" + categoryId);
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
                // get the posts array as JsonArray
                JSONArray postsJsonArray = jsonObject.getJSONArray("posts");
                // convert jsonArray to ArrayList and save a copy with all the informations

                if (postsJsonArray != null) {
                    for (int i = 0; i < postsJsonArray.length(); ++i) {

                        // add to list item number i ;
                        postsArrayList.add(
                                new Post(
                                        postsJsonArray.getJSONObject(i).getInt("id"),
                                        postsJsonArray.getJSONObject(i).getInt("user_id"),
                                        postsJsonArray.getJSONObject(i).getInt("category_id"),
                                        postsJsonArray.getJSONObject(i).getString("content"),
                                        postsJsonArray.getJSONObject(i).getString("p_date"),
                                        postsJsonArray.getJSONObject(i).getString("p_time"),
                                        postsJsonArray.getJSONObject(i).getString("username")
                                )
                        );

                    }
                }

                //create ArrayAdapter
                PostsArrayAdapter postsArrayAdapter = new PostsArrayAdapter(
                        getApplicationContext(), // context
                        R.layout.thread_item_layout, // layout of the item
                        postsArrayList // elements of ListView
                );

                posts.setAdapter(postsArrayAdapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    public class PostsArrayAdapter extends ArrayAdapter<Post> {

        Context context;
        int resource;
        List<Post> posts;

        public PostsArrayAdapter(Context context, int resource, List<Post> posts) {
            super(context, resource, posts);
            this.context = context;
            this.resource = resource;
            this.posts = posts;

        }

        @Override
        public int getCount() {
            return postsArrayList.size();
        }


        @Override
        public int getPosition(Post item) {
            return postsArrayList.indexOf(item);
        }

        @Override
        public Post getItem(int position) {
            return postsArrayList.get(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View row;

            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            row = inflater.inflate(
                    R.layout.thread_item_layout, // layout resource idcontext
                    parent, // parent
                    false // attache to root false
            );


            TextView content = (TextView) row.findViewById(R.id.content);
            TextView username = (TextView) row.findViewById(R.id.username);
            TextView dateAndTime = (TextView) row.findViewById(R.id.date_and_time);


            Post post = posts.get(position);

            content.setText(post.getContent());
            username.setText(post.getUsername());
            dateAndTime.setText(post.getP_date() + " @ " + post.getP_time());
            Log.e("amrou was here", dateAndTime.getText().toString());
            return row;
        }


    }
}
