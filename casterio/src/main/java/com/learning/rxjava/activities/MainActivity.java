package com.learning.rxjava.activities;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.gson.Gson;
import com.learning.rxjava.R;
import com.learning.rxjava.models.Gist;
import com.learning.rxjava.models.GistFile;

import java.io.IOException;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new AsyncTask<Void, Void, Gist>() {

            public IOException error;

            @Override
            protected Gist doInBackground(Void... params) {
                OkHttpClient client = new OkHttpClient();

                // Go get this Gist: https://gist.github.com/donnfelker/db72a05cc03ef523ee74
                // via the GitHub API
                Request request = new Request.Builder()
                        .url("https://api.github.com/gists/db72a05cc03ef523ee74")
                        .build();

                try {
                    Response response = client.newCall(request).execute();

                    if(response.isSuccessful()) {
                        Gist gist = new Gson().fromJson(response.body().charStream(), Gist.class);
                        return gist;
                    }

                    return null;

                } catch (IOException e) {
                    this.error = e;
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Gist gist) {
                super.onPostExecute(gist);

                StringBuilder sb = new StringBuilder();
                //Output
                for(Map.Entry<String, GistFile> entry : gist.files.entrySet()) {
                    sb.append(entry.getKey());
                    sb.append(" - ");
                    sb.append("Length of file ");
                    sb.append(entry.getValue().content.length());
                    sb.append("\n");
                }

                TextView textView = (TextView) findViewById(R.id.main_message);
                textView.setText(sb.toString());
            }
        }.execute();
    }
}
