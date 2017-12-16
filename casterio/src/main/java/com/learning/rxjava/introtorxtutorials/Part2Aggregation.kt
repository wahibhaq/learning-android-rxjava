package com.learning.rxjava.introtorxtutorials

import io.reactivex.Observable
import java.util.concurrent.TimeUnit

class Part2Aggregation() : BaseRxObs(), AggregationTutorial {

    override fun count() {
        val values = Observable.range(0, 10)

        disposable.add(values
                .filter({ it -> it % 2 == 0 })
                .count()
                .subscribeWith(longDisposableSingleObserver()))
    }

    /**
     * Instead of getting a java.util.NoSuchElementException,
     * you can use firstOrDefault to get a default value when the sequence is empty.
     * last and lastOrDefault work in the same way as first
     */
    override fun first() {
        val values = Observable.interval(1, TimeUnit.SECONDS)
        disposable.add(values
                .filter({ it -> it > 10 && it.toInt() % 2 == 0 })
                .firstElement()
                .subscribeWith(longDisposableMaybeObserver()))
    }

    /**
     * You can use singleOrError, single(default)
     * But I was gettng exception if stream was returning more than one value
     */
    override fun single() {
        val values = Observable.just("cat", "dog", "mouse", "duck")
        disposable.add(values
                .filter { it -> it.contains("ou") }
                .singleElement()
                .subscribeWith(stringDisposableMaybeObserver()))
    }
}