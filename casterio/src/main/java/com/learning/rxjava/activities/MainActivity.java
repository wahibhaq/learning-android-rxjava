package com.learning.rxjava.activities;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func0;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private Subscription subscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //subscribing
        subscription = getGistObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Gist>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, e.getMessage(), e);
                    }

                    @Override
                    public void onNext(Gist gist) {
                        if (gist != null) {

                            StringBuilder sb1 = new StringBuilder();
                            for (Map.Entry<String, String> owner : gist.owner.entrySet()) {
                                if(owner.getKey().equals("login")) {
                                    sb1.append(owner.getKey());
                                    sb1.append(" : ");
                                    sb1.append(owner.getValue().toString());
                                }
                                break;
                            }

                            StringBuilder sb = new StringBuilder();
                            //Output
                            for (Map.Entry<String, GistFile> entry : gist.files.entrySet()) {
                                sb.append(entry.getKey());
                                sb.append(" - ");
                                sb.append("Length of file ");
                                sb.append(entry.getValue().size.toString());
                                sb.append("\n");
                            }

                            TextView textView = (TextView) findViewById(R.id.main_message);
                            textView.setText(sb1.toString() + "\n\n" + sb.toString());
                        }
                    }
                });

    }

    @Nullable
    private Gist getGist() throws IOException {
        OkHttpClient client = new OkHttpClient();

        // Go get this Gist: https://gist.github.com/donnfelker/db72a05cc03ef523ee74
        // via the GitHub API
        Request request = new Request.Builder()
                .url("https://api.github.com/gists/db72a05cc03ef523ee74")
                .build();

            Response response = client.newCall(request).execute();

            if(response.isSuccessful()) {
                Gist gist = new Gson().fromJson(response.body().charStream(), Gist.class);
                return gist;
            }

        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe(); //In order to provide memory leak
        }

    }

    public Observable<Gist> getGistObservable() {

        return Observable.defer(new Func0<Observable<Gist>>() {
            @Override
            public Observable<Gist> call() {
                try {
                    return Observable.just(getGist()); //gets called only after subscriber
                } catch (IOException e) {
                    return null;
                }
            }
        });
    }
}
