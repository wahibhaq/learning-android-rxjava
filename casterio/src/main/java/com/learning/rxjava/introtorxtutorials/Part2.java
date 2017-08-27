package com.learning.rxjava.introtorxtutorials;


import android.util.Log;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.ReplaySubject;
import io.reactivex.subjects.Subject;

public class Part2 {

    private static final String TAG = Part2.class.getSimpleName();

    private Disposable disposable;

    public Part2() {
    }

    /**
     * Only "Completed" is displayed
     */
    public void createEmptyObservable() {
        Observable<String> values = Observable.empty();
        disposable = values.subscribeWith(disposableObserver());
    }

    @android.support.annotation.NonNull
    private DisposableObserver<String> disposableObserver() {
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

    public void createNeverObservable() {
        Observable<String> values = Observable.never();
        disposable = values.subscribeWith(disposableObserver());
    }

    public void createErrorObservable() {
        Observable<String> values = Observable.error(new Exception("Oops"));
        disposable = values.subscribeWith(disposableObserver());
    }

    public void createDeferObservable() {
        Observable<Long> now = Observable.defer(() ->
                Observable.just(System.currentTimeMillis()));
        disposable = now.subscribe(aLong -> Log.i(TAG, "onAccept: " + aLong));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        disposable = now.subscribe(aLong -> Log.i(TAG, "onAccept: " + aLong));
    }

    public void createCreateObservable() {
        Observable<String> values = Observable.create(e -> {
            e.onNext("Hello");
            e.onComplete();
        });
        disposable = values.subscribeWith(disposableObserver());
    }

    /**
     * Accept values pushed before or after the subscription
     */
    public  void createReplaySubject() {
        Subject<String> subject = ReplaySubject.create();
        subject.onNext("first");
        subject.onNext("second");
        disposable = subject.subscribe(s -> Log.i(TAG, "onAccept: " + s));
        subject.onNext("third");
    }

    /**
     * Will only display "second" as it only takes values pushed after subscription
     */
    public  void createPublishSubject() {
        Subject<String> subject = PublishSubject.create();
        subject.onNext("first");
        disposable = subject.subscribe(s -> Log.i(TAG, "onAccept: " + s));
        subject.onNext("second");
    }

    public void clear() {
        if(!disposable.isDisposed()) {
            disposable.dispose();
        }
    }
}
