package com.learning.rxjava.introtorxtutorials;


import android.util.Log;

import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DisposableObserver;

class BaseRxObs {

    public static final String TAG = "BaseRx";

    @android.support.annotation.NonNull
    DisposableObserver<Integer> intDisposableObserver() {
        return new DisposableObserver<Integer>() {
            @Override
            public void onNext(@NonNull Integer s) {
                Log.i(TAG, "onNext: " + s);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.i(TAG, "onError: " + e);
            }

            @Override
            public void onComplete() {
                Log.i(TAG, "onComplete");
            }
        };
    }

    @android.support.annotation.NonNull
    DisposableObserver<Long> longDisposableObserver() {
        return new DisposableObserver<Long>() {
            @Override
            public void onNext(@NonNull Long s) {
                Log.i(TAG, "onNext: " + s);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.i(TAG, "onError: " + e);
            }

            @Override
            public void onComplete() {
                Log.i(TAG, "onComplete");
            }
        };
    }

    @android.support.annotation.NonNull
    DisposableObserver<String> stringDisposableObserver() {
        return new DisposableObserver<String>() {
            @Override
            public void onNext(@NonNull String s) {
                Log.i(TAG, "onNext: " + s);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.i(TAG, "onError: " + e);
            }

            @Override
            public void onComplete() {
                Log.i(TAG, "onComplete");
            }
        };
    }
}
