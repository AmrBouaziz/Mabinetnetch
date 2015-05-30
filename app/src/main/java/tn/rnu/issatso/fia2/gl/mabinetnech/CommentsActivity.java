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


public class CommentsActivity extends Activity {

    private static String TAG = ThreadsActivity.class.getName();
    final ArrayList<Comment> commentsArrayList = new ArrayList<>();
    SharedPreferences prefs;
    private ListView comments;
    private View thePostView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        comments = (ListView) findViewById(R.id.threads_list_view);
        // get post from intent
        Intent intent = getIntent();
        Post post = (Post) intent.getSerializableExtra(Contract.EXTRA_POST_OBJECT);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_comments, menu);
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


    class LoadCommentsAsyncTask extends AsyncTask<String, Void, String> {

        private String jsonString;

        @Override
        /**
         * @params param[0]  post id
         */
        protected String doInBackground(String... params) {
            String token = prefs.getString(Contract.USER_TOKEN, null);
            try {


                URL url = new URL("http://10.0.2.1/public/mabinetnetch/getcategoryposts.php?token=" + token + "&post=" + params[0]);
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
                JSONArray commentsJsonArray = jsonObject.getJSONArray("posts");
                // convert jsonArray to ArrayList and save a copy with all the informations

                if (commentsJsonArray != null) {
                    for (int i = 0; i < commentsJsonArray.length(); ++i) {

                        // add to list item number i ;
                        commentsArrayList.add(
                                new Comment(
                                        commentsJsonArray.getJSONObject(i).getInt("id"),
                                        commentsJsonArray.getJSONObject(i).getInt("user_id"),
                                        commentsJsonArray.getJSONObject(i).getInt("post_id"),
                                        commentsJsonArray.getJSONObject(i).getString("content"),
                                        commentsJsonArray.getJSONObject(i).getString("p_date"),
                                        commentsJsonArray.getJSONObject(i).getString("p_time")
                                )
                        );

                    }
                }

                //create ArrayAdapter
                CommentsArrayAdapter commentsArrayAdapter = new CommentsArrayAdapter(
                        getApplicationContext(), // context
                        R.layout.thread_item_layout, // layout of the item
                        commentsArrayList // elements of ListView
                );

                comments.setAdapter(commentsArrayAdapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    public class CommentsArrayAdapter extends ArrayAdapter<Comment> {

        Context context;
        int resource;
        List<Post> posts;

        public CommentsArrayAdapter(Context context, int resource, List<Comment> comments) {
            super(context, resource, comments);
            this.context = context;
            this.resource = resource;
            this.posts = posts;

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
            Log.e("asma was here", dateAndTime.getText().toString());
            return row;
        }


    }
}
