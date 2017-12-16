package com.learning.rxjava.introtorxtutorials;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;

/**
https://github.com/Froussios/Intro-To-RxJava/blob/master/Part%202%20-%20Sequence%20Basics/2.%20Reducing%20a%20sequence.md
 */
public class Part2Reducing extends BaseRxObs implements ReducingSeqTutorial {

    public Part2Reducing() {
    }

    @Override
    public void filter() {
        Observable<Integer> observable = Observable.range(1,100);
        disposable.add(observable.filter(integer -> integer % 10 == 0)
                .subscribeWith(intDisposableObserver()));
    }

    //excluding repeating entities. Use when searching for unique occurances
    //distinctUntilChanges() only cares for consecutive occurances but its more optimal implementation
    //because distinct() internally keeps a set
    @Override
    public void distinct() {
        Observable<String> observable = Observable.fromArray("#abc1", "#abc1", "ghi4", "ghi4");
        disposable.add(observable
                .distinct(criteria -> criteria.startsWith("#"))
                .subscribeWith(stringDisposableObserver()));
    }

    //similar to String.split() to split a stream based on passed criteria.
    //Its like if you want to only consider 1st part of your stream
    @Override
    public void take() {
        Observable<Integer> observable = Observable.range(0, 10);
        disposable.add(observable
                .take(5)
                .subscribeWith(intDisposableObserver()));
    }

    //Split Criteria can also be even time.
    //Here observable is emitting list item after every 2 seconds but take() only takes till 5 sec
    //so we end up with only 2 elements
    @Override
    public void takePerTime() {
        Observable<Long> observable = Observable.intervalRange(1, 10, 0,
                1, TimeUnit.SECONDS);
        disposable.add(observable.take(5, TimeUnit.SECONDS)
                .subscribeWith(longDisposableObserver()));
    }

    //Opposite of take as it will consider only the 2nd half of the stream
    //IntervalRange() does what interval does but with range: emit each item after certain delay
    @Override
    public void skip() {
        Observable<Long> observable = Observable.intervalRange(1, 10, 0,
                2, TimeUnit.SECONDS);
        disposable.add(observable
                .skip(5)
                .subscribeWith(longDisposableObserver()));
    }

    //take() and skip() takes predefined values but if you want to specify conditions then
    // we have takeWhile(), skipWhile() for that
    // and even takeLast(), skipLast()
    // and also takeUntil(), skipUntil() which takes items while the predicate is false

}
