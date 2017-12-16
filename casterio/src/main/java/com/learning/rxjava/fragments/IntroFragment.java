package com.learning.rxjava.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.learning.rxjava.R;
import com.learning.rxjava.introtorxtutorials.Part2Aggregation;
import com.learning.rxjava.introtorxtutorials.Part2CreatingSeq;
import com.learning.rxjava.models.Gist;
import com.learning.rxjava.models.GistFile;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by wahibulhaq on 17/04/16.
 */
public class IntroFragment extends Fragment {

    private static final String TAG = IntroFragment.class.getSimpleName();

    private Disposable subscription;

    Part2Aggregation introtorx;

    public static IntroFragment newInstance() {
        return new IntroFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Function<Gist, StringBuilder> outputToDisplay = new Function<Gist, StringBuilder>() {
            @Override
            public StringBuilder apply(@NonNull Gist gist) throws Exception {
                StringBuilder sb1 = new StringBuilder();
                for (Map.Entry<String, String> owner : gist.owner.entrySet()) {
                    if(owner.getKey().equals("login")) {
                        sb1.append(owner.getKey());
                        sb1.append(" : ");
                        sb1.append(owner.getValue());
                    }
                }

                sb1.append("\n\n\n");

                for (Map.Entry<String, GistFile> entry : gist.files.entrySet()) {
                    sb1.append(entry.getKey());
                    sb1.append(" - ");
                    sb1.append("Length of file ");
                    sb1.append(entry.getValue().size);
                    sb1.append("\n");
                }
                return sb1;
            }
        };

        //subscribing
        subscription = getGistObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(outputToDisplay)
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        Log.e(TAG, throwable.getCause().getMessage());
                    }
                })
                .subscribe(new Consumer<StringBuilder>() {
                    @Override
                    public void accept(@NonNull StringBuilder stringBuilder) throws Exception {
                        if(getView().findViewById(R.id.main_message) != null) {
                            TextView textView = (TextView) getView()
                                    .findViewById(R.id.main_message);
                            textView.setText(stringBuilder.toString());
                        }
                    }
                });

        //testing IntroToRx Tutorial exercises
//        Part2CreatingSeq introtorx = new Part2CreatingSeq();
//        introtorx.convertingFutureTaskToObs();

        //Testing Filter Reduce exercises
//        Part2Reducing introtorx = new Part2Reducing();
//        introtorx.skip();

        //Testing Inspection techniques
//        introtorx = new Part2Inspection();
//        introtorx.equals();

        //Testing Aggregation techniques
        introtorx = new Part2Aggregation();
        introtorx.single();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Just some view
        return inflater.inflate(R.layout.fragment_intro, container, false);
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
            return new Gson().fromJson(response.body().charStream(), Gist.class);
        } else {
            return null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(subscription != null && !subscription.isDisposed()) {
            subscription.dispose(); //In order to avoid memory leak
        }

        introtorx.clear();
    }



    public Observable<Gist> getGistObservable() {
        return Observable.defer(new Callable<ObservableSource<? extends Gist>>() {
            @Override
            public ObservableSource<? extends Gist> call() throws Exception {
                try {
                    return Observable.just(getGist()); //gets called only after subscriber
                } catch (IOException e) {
                    return Observable.error(e); //for better error handling in onError()
                }
            }
        });
    }

}
