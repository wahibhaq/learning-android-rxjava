package com.learning.rxjava.introtorxtutorials

import android.util.Log
import io.reactivex.Observable
import io.reactivex.functions.Consumer
import io.reactivex.subjects.ReplaySubject
import io.reactivex.subjects.Subject
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

    /**
     * reduce is like what we call "accumulation" and best utility for sum of all integers in a
     * sequence. It will only return the final accumulated result
     *
     */
    override fun reduce() {
        val values = Observable.just("This", "is", "Me")
        disposable.add(values
                .reduce { t1: String, t2: String -> t1 + "-" + t2 }
                .subscribeWith(stringDisposableMaybeObserver()))
    }

    /**
     * scan is different to reduce in a way that it will also return intermediate results. Also
     * scan emits when the source emits and does not need the source to complete.
     * reduce can be implemented with scan: reduce(acc) = scan(acc).takeLast()
     */
    override fun scan() {
        val values: Subject<Int> = ReplaySubject.create()

        //Finding minimum
        disposable.add(
                values
                .scan { i1, i2 -> if (i1 < i2) i1 else i2 }
                .distinctUntilChanged()
                .subscribeWith(intDisposableObserver()))

        values.onNext(2)
        values.onNext(3)
        values.onNext(1)
        values.onNext(4)
        values.onComplete()

    }

    /**
     * The performance of creating a new collection for every new item is unacceptable.
     * For that reason, Rx offers the collect operator, which does the same thing as reduce,
     * only using a mutable accumulator this time. By using collect you document that you are not
     * following the convention of immutability
     */
    override fun collect() {
        val values = Observable.range(5, 10)
        disposable.add(values
                .collect({ArrayList<Int>()}, { acc, value -> acc.add(value) })
                .subscribe({ v -> Log.i(TAG, v.toString()) }))

    }

    data class Car(val name: String, val productionYear: Int)

    val carObs = Observable.just(
            Car("bmw", 2015),
            Car("ferrari", 2000),
            Car("suzuki", 1960),
            Car("bmw", 2013))

    override fun toMap() {
        disposable.add(
                carObs.toMap(
                        {name -> name.name},
                        {year -> year.productionYear},
                        {LinkedHashMap<String, Int>()})
                .subscribe({v -> Log.i(TAG, v.toString())}))
    }

    /**
     * used to group rows coz there are common keys. Each value is itself a map so like map of maps
     */
    override fun toMultiMap() {
        disposable.add(
                carObs.toMultimap(
                        { name -> name.name },
                        { name -> name.productionYear },
                        { LinkedHashMap<String, ArrayList<String>>() }


                ).subscribe(Consumer { Log.i(TAG, it.toString()) }))
    }
}