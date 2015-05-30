package tn.rnu.issatso.fia2.gl.mabinetnech;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class LoginActivity extends Activity {

    EditText email;
    EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences prefs = getSharedPreferences(getResources().getString(R.string.app_name), MODE_PRIVATE);
        Log.i(LoginActivity.class.getName(), "user logged in :" + prefs.getBoolean(Contract.USER_LOGGED, false));
        if (prefs.getBoolean(Contract.USER_LOGGED, false)) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);

        Button login = (Button) findViewById(R.id.button_login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginAsyncTask loginAsyncTask = new LoginAsyncTask
                        (email.getText().toString(), password.getText().toString(), getApplicationContext());

                loginAsyncTask.execute();

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.menu_login, menu);
        return false;
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

    class LoginAsyncTask extends AsyncTask<Void, Void, String> {

        private final Context context;
        private String jsonString = null;
        private String password;
        private String email;

        LoginAsyncTask(String email, String password, Context context) {
            this.context = context;
            this.email = email;
            this.password = password;
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL("http://10.0.2.1/public/mabinetnetch/signin.php?name=" + email + "&password=" + password);
                Log.v(LoginAsyncTask.class.getName(), url.toString());

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
            return jsonString;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // show progress bar and hide login form
            findViewById(R.id.login_form).setVisibility(View.GONE);
            findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String aString) {
            super.onPostExecute(aString);

            boolean loggedIn = false;
            boolean jsonOk = true;
            try {
                JSONObject jsonObject = new JSONObject(jsonString);

                Log.i("getInt(\"status\")", jsonObject.getInt("status") + "");
                if (jsonObject.getInt("status") == 0) {
                    jsonOk = false;
                }
                SharedPreferences prefs = getSharedPreferences(getResources().getString(R.string.app_name), MODE_PRIVATE);

                boolean user_logged = prefs.getBoolean(Contract.USER_LOGGED, false);
                if (!user_logged && jsonOk) {
                    prefs.edit()
                            .putInt(
                                    Contract.USER_ID,
                                    jsonObject.getJSONObject("user").getInt("id"))
                            .putString(
                                    Contract.USER_TOKEN,
                                    jsonObject.getJSONObject("user").getString("password"))
                            .putBoolean(
                                    Contract.USER_LOGGED,
                                    true
                            )
                            .apply();
                }

                loggedIn = jsonObject.getInt("status") == 1;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (loggedIn) {


                //start Main Activity
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                // if there's data do be send put it in the intent

                startActivity(intent);

            }
            // show back the form
            findViewById(R.id.login_form).setVisibility(View.VISIBLE);
            findViewById(R.id.progressBar).setVisibility(View.GONE);
        }
    }
}
