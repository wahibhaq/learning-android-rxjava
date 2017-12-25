package com.learning.rxjava.katas.introduction;

import android.util.Log;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

public class Kata2FetchArticleObservable {

    private Disposable disposable;

    public void createAnObservable() {
        String[] planeTypes = {"Boeing 777", "Boeing 747", "Boeing 737", "Airbus 330", "Airbus 320"};

        // 1) create an observable that emits the plane type
        // 2) transform the planetype to plane number as an integer
        // 3) subscribe to the observable and print the content

        disposable = Observable.fromArray(planeTypes)
                .map(s -> Integer.valueOf(s.split(" ")[1]))
                .subscribeWith(new DisposableObserver<Integer>() {
                    @Override
                    public void onNext(@NonNull Integer integer) {
                        Log.i("KATAS", String.valueOf(integer));
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e("KATAS", e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }
}
