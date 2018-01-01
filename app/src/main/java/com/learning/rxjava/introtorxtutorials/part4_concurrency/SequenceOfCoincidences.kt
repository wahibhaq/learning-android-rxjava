package com.learning.rxjava.introtorxtutorials.part4_concurrency

import android.util.Log
import com.learning.rxjava.introtorxtutorials.BaseRxObs
import com.learning.rxjava.introtorxtutorials.DisplayConsumer
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Consumer
import io.reactivex.functions.Function
import java.util.concurrent.TimeUnit

/**
 * Rx tries to avoid state outside of the pipeline. However, some things are inherently stateful.
 * A server can be up or down, a mobile device may have access to wifi, a button is held down.
 * In Rx, we see those as events with a duration and we call them windows.
 */
class SequenceOfCoincidences : BaseRxObs() {

    /**
     * Window is like Buffer but The main difference is that it doesn't return the groups in
     * buffered chunks. Instead, it returns a sequence of sequences, each sequence corresponding
     * to what would have been a buffer.
     */
    fun windowByCount() {
//        disposable.add(
//                Observable.merge(
//                Observable.range(0, 5)
//                        .window(3, 1))
//                        .subscribe(DisplayConsumer("windowByCount"))
//        )

        disposable.add(
                Observable.range(0, 5)
                        .window(3, 1)
                        .flatMap { o -> o.toList().toObservable() }
                        .subscribe(DisplayConsumer("windowByCount"))
        )
    }

    fun windowByTime() {
        disposable.add(
                Observable.interval(100, TimeUnit.MILLISECONDS)
                .take(5)
                .window(250, 100, TimeUnit.MILLISECONDS)
                .flatMap { it -> it.toList().toObservable() }
                .subscribe { DisplayConsumer("windowByTime")})
    }

    /**
     * join allows you to pair together items from two sequences. We've already seen zip, which
     * pairs values based on their index. join allows you to pair values based on durations.
     *
     * In the signature, we can see two methods called leftDurationSelector and rightDurationSelector,
     * which take as an argument an item of the respective sequence. They return an observable that
     * defines a duration (i.e. a window), just like in the last overload of window. These windows
     * are used to select values to be paired together.
     */
    fun usingJoin() {
        val left = Observable.interval(100, TimeUnit.MILLISECONDS)
                .map { i -> "L" + i }
        val right = Observable.interval(200, TimeUnit.MILLISECONDS)
                .map { i -> "R" + i }

        disposable.add(
                left
                        .join(right,
                                Function { _: String -> Observable.never<Any>() },
                                Function { _: String -> Observable.timer(0,
                                        TimeUnit.MILLISECONDS) },
                                BiFunction { l: String, r: String -> l + " - " + r })
                        .take(10)
                        .subscribeWith(stringDisposableObserver())
        )
    }

    /**
     * The signature is the same as join exept for the resultSelector. Now the result selector
     * takes an item from the left sequence and an observable of values from the right sequence.
     * That observable will emit every right value that the left value is paired with.
     */
    fun usingGroupJoin() {
        val left = Observable.interval(100, TimeUnit.MILLISECONDS)
                .map { i -> "L" + i }
                .take(6)
        val right = Observable.interval(200, TimeUnit.MILLISECONDS)
                .map { i -> "R" + i }
                .take(3)

        disposable.add(
        left
                .groupJoin(
                        right,
                        Function { _: String -> Observable.never<Any>() },
                        Function { _: String -> Observable.timer(0,
                                TimeUnit.MILLISECONDS) },
                        BiFunction { l: String, rs: Observable<String> ->
                            rs.toList().subscribe { t1 -> Log.i(TAG, l + " : " +  t1.toString())  }}
                )
                .subscribe())
    }

}