package com.learning.rxjava.introtorxtutorials;


import android.util.Log;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableMaybeObserver;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.observers.DisposableSingleObserver;

public class BaseRxObs {

    static final String TAG = "BaseRx";

    final CompositeDisposable disposable = new CompositeDisposable();

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

    DisposableSingleObserver<Boolean> boolDisposableSingleObserver() {
        return new DisposableSingleObserver<Boolean>() {
            @Override
            public void onSuccess(@NonNull Boolean bool) {
                Log.i(TAG, "onSuccess: " + bool);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.i(TAG, "onError: " + e);
            }
        };
    }

    DisposableSingleObserver<Long> longDisposableSingleObserver() {
        return new DisposableSingleObserver<Long>() {
            @Override
            public void onSuccess(Long aLong) {
                Log.i(TAG, "onSuccess: " + aLong);
            }

            @Override
            public void onError(Throwable e) {
                Log.i(TAG, "onError: " + e);
            }
        };
    }

    DisposableMaybeObserver<Long> longDisposableMaybeObserver() {
        return new DisposableMaybeObserver<Long>() {
            @Override
            public void onSuccess(Long aLong) {
                Log.i(TAG, "onSuccess: " + aLong);
            }

            @Override
            public void onError(Throwable e) {
                Log.i(TAG, "onError: " + e);
            }

            @Override
            public void onComplete() {
                Log.i(TAG, "onComplete");
            }
        };
    }

    DisposableMaybeObserver<String> stringDisposableMaybeObserver() {
        return new DisposableMaybeObserver<String>() {
            @Override
            public void onSuccess(String s) {
                Log.i(TAG, "onSuccess: " + s);
            }

            @Override
            public void onError(Throwable e) {
                Log.i(TAG, "onError: " + e);
            }

            @Override
            public void onComplete() {
                Log.i(TAG, "onComplete");
            }
        };
    }

    DisposableSingleObserver<String> stringDisposableSingleObserver() {
        return new DisposableSingleObserver<String>() {
            @Override
            public void onSuccess(String s) {
                Log.i(TAG, "onSuccess: " + s);
            }

            @Override
            public void onError(Throwable e) {
                Log.i(TAG, "onError: " + e);
            }
        };
    }

    public void clear() {
        disposable.clear();
    }
}
