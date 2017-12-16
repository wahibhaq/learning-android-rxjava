package com.learning.rxjava.introtorxtutorials;


import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.ReplaySubject;
import io.reactivex.subjects.Subject;

/**
 * https://github.com/Froussios/Intro-To-RxJava/tree/master/Part%202%20-%20Sequence%20Basics
 */
public class Part2CreatingSeq extends BaseRxObs {

    public Part2CreatingSeq() {
    }

    /**
     * Only "Completed" is displayed
     */
    public void createEmptyObservable() {
        Observable<String> values = Observable.empty();
        disposable.add(values.subscribeWith(stringDisposableObserver()));
    }

    public void createNeverObservable() {
        Observable<String> values = Observable.never();
        disposable.add(values.subscribeWith(stringDisposableObserver()));
    }

    public void createErrorObservable() {
        Observable<String> values = Observable.error(new Exception("Oops"));
        disposable.add(values.subscribeWith(stringDisposableObserver()));
    }

    public void createDeferObservable() {
        Observable<Long> now = Observable.defer(() ->
                Observable.just(System.currentTimeMillis()));
        now.subscribe(aLong -> Log.i(TAG, "onAccept: " + aLong));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        now.subscribe(aLong -> Log.i(TAG, "onAccept: " + aLong));
    }

    public void createCreateObservable() {
        Observable<String> values = Observable.create(e -> {
            e.onNext("Hello");
            e.onComplete();
        });
        disposable.add(values.subscribeWith(stringDisposableObserver()));
    }

    /**
     * Accept values pushed before or after the subscription
     */
    public void createReplaySubject() {
        Subject<String> subject = ReplaySubject.create();
        subject.onNext("first");
        subject.onNext("second");
        subject.subscribe(s -> Log.i(TAG, "onAccept: " + s));
        subject.onNext("third");
    }

    /**
     * Will only display "second" as it only takes values pushed after subscription
     */
    public void createPublishSubject() {
        Subject<String> subject = PublishSubject.create();
        subject.onNext("first");
        subject.subscribe(s -> Log.i(TAG, "onAccept: " + s));
        subject.onNext("second");
    }

    /**
     * takes starting value and count of values
     **/
    public void createRangeObservable() {
        Observable<Integer> values = Observable.range(10,100);
        values.subscribe(integer -> Log.i(TAG, "Integers Range: " + integer));
    }

    /**
     * Emits values after a certain delay
     */
    public void createTimerObservable() {
        Observable<Long> values = Observable.timer(2, TimeUnit.SECONDS);
        values.subscribe(
                v -> Log.i(TAG, "onReceived: " + v),
                e -> Log.i(TAG, "onError: " + e),
                () -> Log.i(TAG, "onCompleted"));
    }

    /**
     * for array -> fromArray
     * for List -> fromIterable
     * for Collection -> fromCollection
     */
    public void createFromObservable() {
        List<String> blank = new ArrayList<>();
        blank.add("a");
        blank.add("b");
        Observable<String> values = Observable.fromIterable(blank);
        disposable.add(values.subscribeWith(stringDisposableObserver()));
    }

    /**
     * Emits buffered values depending on parameters
     */
    public void createBufferObservable() {
        Observable<List<String>> values = Observable.just("one", "two", "three", "four", "five")
                .buffer(3,1);

        // 3 means,  it takes max of three from its start index and create list
        // 1 means, it jumps one step every time
        // so the it gives the following list
        // 1 - one, two, three
        // 2 - two, three, four
        // 3 - three, four, five
        // 4 - four, five
        // 5 - five

        values.subscribe(new DisposableObserver<List<String>>() {
            @Override
            public void onNext(@NonNull List<String> strings) {
                Log.i(TAG, "onNext: " + strings);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.i(TAG, "onError: " + e);
            }

            @Override
            public void onComplete() {
                Log.i(TAG, "onComplete");
            }
        });
    }

    /**
     * The observable emits the result of the FutureTask when it is available and then terminates.
     */
    public void convertingFutureTaskToObs() {
        FutureTask<String> futureTask = new FutureTask<>(() -> {
            Thread.sleep(3000);
            return "hello";
        });
        new Thread(futureTask).start();

        Observable<String> observable = Observable.fromFuture(futureTask, 5,TimeUnit.SECONDS);
        disposable.add(observable.subscribeWith(stringDisposableObserver()));
    }

    public void createSingleObservable() {
        Single<String> singleObs = Single.create(emitter -> {
                   //List<ToDo> todosFromWeb = // query a webservice
                   List<String> values = new ArrayList(Arrays.asList("a", "b", "c"));
                    Log.i(TAG, "this is only called once");

                    if(values.size() > 0)
                        emitter.onSuccess("everything went fine");
                    else
                        emitter.onSuccess("everything went down");

        });

        Single<String> cachedSingleObs = singleObs.cache();
        cachedSingleObs
                .observeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    Log.i(TAG, "for demo purpose in subscribed: " + s);
                });
    }

}
