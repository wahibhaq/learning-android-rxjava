package com.learning.rxjava.katas.introduction;

import android.util.Log;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;

/**
 * copied from https://github.com/senacor/rxjava-katas
 */
public class Kata1CreateObservable {

    private final CompositeDisposable disposables = new CompositeDisposable();

    public Kata1CreateObservable() {
    }

    public void createAnObservable() {
        final String planeType = "Boeing 737";

        // 1) create an observable that emits the plane type
        // 2) subscribe to the observable and print the plane type

        disposables.add(Observable.just(planeType)
                .subscribe(s -> Log.i("KATAS", s), throwable -> Log.e("KATAS", throwable.getMessage())));



    }
}
