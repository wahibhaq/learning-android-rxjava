package com.learning.rxjava.introtorxtutorials.part3

import android.util.Log
import com.learning.rxjava.introtorxtutorials.BaseRxObs
import com.learning.rxjava.introtorxtutorials.DisplayConsumer
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import java.util.concurrent.TimeUnit

class CombiningSequences : BaseRxObs() {

    val fruits = Observable.just("Apple", "Ananas", "Banana", "Carrot",
            "Orange", "Onion", "Mango")

    val vegetables = Observable.just("Carrot", "Tomatoes", "Onions")

    fun groupingViaConcat() {
        disposable.add(Observable.concat(fruits
                .groupBy { it -> it[0]})
                .subscribe(DisplayConsumer("groupBy")))
    }

    fun countingFrequencyGroupBy() {
        disposable.add(fruits
                .groupBy { it -> it[0]}
                .flatMap {
                    gr -> Observable.just(gr.count())
                        .map { count -> Pair(gr.key, count) }
                }
                .subscribe {
                    it.second.subscribe { t1, t2 -> Log.i(TAG, it.first!!.plus(" : " + t1)) }
                })
    }

    fun concatWith() {
        disposable.add(fruits
                .concatWith(vegetables)
                .subscribeWith(stringDisposableObserver()))
    }


    /**
     * If you need more control than repeat gives, you can control
     * when the repetition starts with the repeatWhen operator.
     */
    fun repeatWhen() {
        disposable.add(fruits
                .take(2)
                .repeatWhen { it -> it.take(3)} // observable that signals the end of a repetition
                .subscribeWith(stringDisposableObserver()))

    }

    fun startWith() {
        disposable.add(fruits
                .startWith("...FRUITS...")
                .concatWith(vegetables
                        .startWith("....VEGETABLES..."))
                .subscribeWith(stringDisposableObserver()))
    }

    /**
     * amb takes a number of observables and returns the one that emits a value first.
     * The rest are discarded. amb() will subscribe to all the sources at once.
     *
     * Usecase: Using the Amb operator, you can send the same request out to many servers and
     * consume the result of the first that responds.
     */
    fun amb() {
        disposable.add(Observable.timer(2, TimeUnit.SECONDS).map { it -> "first" }
                .ambWith { Observable.timer(5, TimeUnit.SECONDS).map { it -> "second" } }
                .subscribeWith(stringDisposableObserver()))
    }

    /**
     * merge combines a set of observables into one, kind of mixing so takes whichever emits first.
     *
     * With merge, as soon as any of the source sequences fails, the merged sequence fails as well.
     * An alternative to that behaviour is mergeDelayError, which will postpone the emission of an
     * error and continue to merge values from sequences that haven't failed.
     */
    fun merge() {
        disposable.add(
                fruits
                        .delay(2, TimeUnit.SECONDS)
                        .mergeWith(vegetables)
                        .subscribeWith(stringDisposableObserver())
        )
    }

    /**
     * The switchOnNext operator takes an observable that emits observables.
     * As soon as a new observable comes, the old one is discarded and values from the newer one are emitted.
     */
    fun switchOnNext() {
        disposable.add(
                Observable.switchOnNext(
                        Observable.interval(100, TimeUnit.MILLISECONDS)
                                .map { i ->
                                    Observable.interval(20, TimeUnit.MILLISECONDS)
                                            .map { i2 -> i }
                                }
                )
                        .take(8)
                        .subscribeWith(longDisposableObserver())
        )
    }

    /**
     * It takes two or more sequences and matches their values one-to-one by index.
     * The zipped sequence terminates when any of the source sequences terminates successfully.
     */
    fun zip() {
        disposable.add(Observable.zip(fruits,
                vegetables,
                BiFunction { t1: String, t2: String -> t1 + " : " + t2 })
                .subscribeWith(stringDisposableObserver()))

    }

    /**
     * Like Zip use indices, CombineLatest uses time.
     * combineLatest first waits for every sequence to have a value. After that, every value
     * emitted by either observable results in a combined value being emitted.
     */
    fun combineLatest() {
                Observable.combineLatest(
                        Observable.interval(100, TimeUnit.MILLISECONDS)
                                .doOnNext { it -> DisplayConsumer("left emits") },
                        Observable.interval(150, TimeUnit.MILLISECONDS)
                                .doOnNext { it -> DisplayConsumer("left emits") },
                        BiFunction { t1: Long, t2: Long -> t1.toString() + " : " + t2.toString() })
                        .take(5)
                        .subscribeWith(stringDisposableObserver())




    }


}