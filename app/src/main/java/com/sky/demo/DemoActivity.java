package com.sky.demo;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.sky.android.Request;

public class DemoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView textView1 = (TextView) findViewById(android.R.id.text1);
        final TextView textView2 = (TextView) findViewById(android.R.id.text2);

        textView1.setText("response string:");

        new AsyncTask<Void, Void, String>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(Void... params) {
                StringRequest request = new StringRequest("https://www.google.com");
                int statusCode = request.getStatus();
                switch (statusCode) {
                    case Request.STATUS_OK: {
                        return request.getResponseResult();
                    }
                    case Request.STATUS_NETWORK_UNAVAILABLE: {
                        return "STATUS_NETWORK_UNAVAILABLE";
                    }
                    case Request.STATUS_NOT_MODIFIED: {
                        return "STATUS_NOT_MODIFIED";
                    }
                    default: {
                        return "STATUS_UNKNOWN";
                    }
                }
            }

            @Override
            protected void onPostExecute(String s) {
                textView2.setText(s);
            }

        }.execute();
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
        }

        return super.onOptionsItemSelected(item);
    }
}
