package com.learning.rxjava.introtorxtutorials;

import android.util.Log;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

/**
https://github.com/Froussios/Intro-To-RxJava/blob/master/Part%202%20-%20Sequence%20Basics/2.%20Reducing%20a%20sequence.md
 */
public class Part2Reducing implements ReducingSeqTutorial {

    private static final String TAG = Part2Reducing.class.getSimpleName();

    private Disposable disposable;

    public Part2Reducing() {
    }

    @android.support.annotation.NonNull
    private DisposableObserver<Integer> disposableObserver() {
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

    @Override
    public void filter() {
        Observable<Integer> observable = Observable.interval(1,100);
        disposable = observable.filter(integer -> integer % 10 == 0)
                .subscribeWith(disposableObserver());
    }

    @Override
    public void clear() {
        if(!disposable.isDisposed()) {
            disposable.dispose();
        }
    }
}
